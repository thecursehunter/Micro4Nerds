package ueh.edu.vn.md.micro4nerds.ui.viewmodel;

import android.app.Application;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;
import ueh.edu.vn.md.micro4nerds.data.model.CartItem;
import ueh.edu.vn.md.micro4nerds.data.model.Order;
import ueh.edu.vn.md.micro4nerds.data.remote.OrderRemoteDataSource;
import ueh.edu.vn.md.micro4nerds.data.repository.CartRepository;
import ueh.edu.vn.md.micro4nerds.data.repository.OrderRepository;

public class CartViewModel extends AndroidViewModel {
    private static final String TAG = "CHECKOUT_DEBUG";
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final LiveData<List<CartItem>> cartItemsLiveData;
    private final LiveData<Double> totalPriceLiveData;

    private final MutableLiveData<OrderPlacementState> orderPlacementState = new MutableLiveData<>();

    public enum OrderPlacementState {
        IDLE,
        LOADING,
        SUCCESS,
        ERROR
    }

    public CartViewModel(@NonNull Application application) {
        super(application);
        cartRepository = CartRepository.getInstance(application);
        orderRepository = new OrderRepository(application);

        cartItemsLiveData = cartRepository.getCartItems();
        totalPriceLiveData = Transformations.map(cartItemsLiveData, this::calculateTotalPrice);

        orderPlacementState.setValue(OrderPlacementState.IDLE);
    }

    public LiveData<List<CartItem>> getCartItems() {
        return cartItemsLiveData;
    }

    public LiveData<Double> getTotalPrice() {
        return totalPriceLiveData;
    }

    public LiveData<OrderPlacementState> getOrderPlacementState() {
        return orderPlacementState;
    }

    private Double calculateTotalPrice(List<CartItem> cartItems) {
        double totalPrice = 0.0;
        if (cartItems != null) {
            for (CartItem item : cartItems) {
                totalPrice += item.getProductPrice() * item.getQuantity();
            }
        }
        return totalPrice;
    }

    public void removeFromCart(String productId) {
        cartRepository.removeFromCart(productId);
    }

    public void updateQuantity(String productId, int newQuantity) {
        if (newQuantity > 0) {
            cartRepository.updateQuantity(productId, newQuantity);
        } else {
            cartRepository.removeFromCart(productId);
        }
    }

    public void clearCart() {
        cartRepository.clearCart();
    }

    // Thêm phoneNumber vào tham số
    public void placeOrder(String customerName, String phoneNumber, String address, String shippingMethod) {
        Log.d(TAG, "CartViewModel: Hàm placeOrder() được gọi.");
        String userId = FirebaseAuth.getInstance().getUid();
        List<CartItem> currentItems = cartItemsLiveData.getValue();
        Double currentTotal = totalPriceLiveData.getValue();

        if (userId == null || currentItems == null || currentItems.isEmpty() || currentTotal == null) {
            Log.e(TAG, "CartViewModel: Dữ liệu không hợp lệ để đặt hàng.");
            orderPlacementState.setValue(OrderPlacementState.ERROR);
            return;
        }

        orderPlacementState.setValue(OrderPlacementState.LOADING);

        // Truyền đầy đủ 7 tham số cho constructor của Order
        Order order = new Order(userId, customerName, phoneNumber, currentItems, currentTotal, address, shippingMethod);

        orderRepository.createOrder(order, new OrderRemoteDataSource.OrderCallback() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "CartViewModel: Nhận được tín hiệu onSuccess từ Repository.");
                clearCart();
                orderPlacementState.postValue(OrderPlacementState.SUCCESS);
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "CartViewModel: Nhận được tín hiệu onFailure từ Repository.", e);
                orderPlacementState.postValue(OrderPlacementState.ERROR);
            }
        });
    }

    public void resetOrderState() {
        orderPlacementState.setValue(OrderPlacementState.IDLE);
    }
}
