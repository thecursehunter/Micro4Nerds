package ueh.edu.vn.md.micro4nerds.data.repository;

import android.content.Context;
import android.util.Log;

import ueh.edu.vn.md.micro4nerds.data.local.SharedPrefManager;
import ueh.edu.vn.md.micro4nerds.data.local.dao.CartDao;
import ueh.edu.vn.md.micro4nerds.data.model.Order;
import ueh.edu.vn.md.micro4nerds.data.model.User;
import ueh.edu.vn.md.micro4nerds.data.remote.OrderRemoteDataSource;

public class OrderRepository {
    private static final String TAG = "CHECKOUT_DEBUG";
    private final OrderRemoteDataSource remoteDataSource;
    private final CartDao cartDao;
    private final SharedPrefManager sharedPrefManager;

    public OrderRepository(Context context) {
        this.remoteDataSource = new OrderRemoteDataSource();
        this.cartDao = new CartDao(context.getApplicationContext());
        this.sharedPrefManager = new SharedPrefManager(context.getApplicationContext());
    }

    public void createOrder(Order order, OrderRemoteDataSource.OrderCallback callback) {
        Log.d(TAG, "OrderRepository: Hàm createOrder() được gọi.");
        remoteDataSource.createOrder(order, new OrderRemoteDataSource.OrderCallback() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "OrderRepository: Firebase báo đặt hàng THÀNH CÔNG. Bắt đầu xóa giỏ hàng local.");
                try {
                    cartDao.clearCart();
                    Log.d(TAG, "OrderRepository: Gọi cartDao.clearCart() thành công. Báo success về cho ViewModel.");
                    callback.onSuccess();
                } catch (Exception e) {
                    Log.e(TAG, "OrderRepository: Lỗi khi gọi cartDao.clearCart()", e);
                    callback.onFailure(e);
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "OrderRepository: Firebase báo đặt hàng THẤT BẠI.", e);
                callback.onFailure(e);
            }
        });
    }

    // Hàm lấy lịch sử đơn hàng
    public void fetchOrderHistory(OrderRemoteDataSource.GetOrdersCallback callback) {
        // Lấy userId từ SharedPrefManager theo yêu cầu
        if (sharedPrefManager.isLoggedIn()) {
            User user = sharedPrefManager.getUser();
            // Đã sửa lỗi: dùng user.getUid() thay vì user.getId()
            if (user != null && user.getUid() != null) {
                remoteDataSource.fetchOrdersFromFirestore(user.getUid(), callback);
            } else {
                callback.onError(new Exception("User ID not found in local storage"));
            }
        } else {
            callback.onError(new Exception("User not logged in"));
        }
    }
}
