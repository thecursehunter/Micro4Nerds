package ueh.edu.vn.md.micro4nerds.ui.order;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import ueh.edu.vn.md.micro4nerds.data.model.CartItem;
import ueh.edu.vn.md.micro4nerds.data.model.Order;
import ueh.edu.vn.md.micro4nerds.data.remote.OrderRemoteDataSource;
import ueh.edu.vn.md.micro4nerds.data.repository.OrderRepository;

public class OrderViewModel extends AndroidViewModel {

    private final OrderRepository orderRepository;
    private final MutableLiveData<CheckoutState> checkoutState = new MutableLiveData<>();
    private String lastError = null; // Biến lưu lỗi cuối cùng

    public enum CheckoutState {
        IDLE,
        LOADING,
        SUCCESS,
        ERROR
    }

    public OrderViewModel(@NonNull Application application) {
        super(application);
        orderRepository = new OrderRepository(application);
        checkoutState.setValue(CheckoutState.IDLE);
    }

    public LiveData<CheckoutState> getCheckoutState() {
        return checkoutState;
    }

    // --- HÀM MỚI ĐỂ LẤY LỖI ---
    public String getLastError() {
        return lastError;
    }

    public void checkout(List<CartItem> items, double totalPrice) {
        String userId = FirebaseAuth.getInstance().getUid();
        // Vì đang giả lập, chúng ta có thể bỏ qua userId
        // if (userId == null) { ... }

        checkoutState.setValue(CheckoutState.LOADING);

        Order order = new Order(userId, items, totalPrice);

        orderRepository.createOrder(order, new OrderRemoteDataSource.OrderCallback() {
            @Override
            public void onSuccess() {
                lastError = null; // Xóa lỗi cũ
                checkoutState.postValue(CheckoutState.SUCCESS);
            }

            @Override
            public void onFailure(Exception e) {
                lastError = e.getMessage(); // Lưu lại tin nhắn lỗi
                checkoutState.postValue(CheckoutState.ERROR);
            }
        });
    }

    public void resetCheckoutState() {
        checkoutState.setValue(CheckoutState.IDLE);
    }
}
