package ueh.edu.vn.md.micro4nerds.ui.viewmodel;

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
    private String lastError = null;

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

    public String getLastError() {
        return lastError;
    }

    public void checkout(List<CartItem> items, double totalPrice, String customerName, String phoneNumber, String address, String shippingMethod) {
        String userId = FirebaseAuth.getInstance().getUid();
        
        checkoutState.setValue(CheckoutState.LOADING);

        Order order = new Order(userId, customerName, phoneNumber, items, totalPrice, address, shippingMethod);

        orderRepository.createOrder(order, new OrderRemoteDataSource.OrderCallback() {
            @Override
            public void onSuccess() {
                lastError = null;
                checkoutState.postValue(CheckoutState.SUCCESS);
            }

            @Override
            public void onFailure(Exception e) {
                lastError = e.getMessage();
                checkoutState.postValue(CheckoutState.ERROR);
            }
        });
    }

    public void resetCheckoutState() {
        checkoutState.setValue(CheckoutState.IDLE);
    }
}
