package ueh.edu.vn.md.micro4nerds.data.repository;

import android.content.Context;
import android.util.Log; // Thêm import Log

import ueh.edu.vn.md.micro4nerds.data.local.dao.CartDao;
import ueh.edu.vn.md.micro4nerds.data.model.Order;
import ueh.edu.vn.md.micro4nerds.data.remote.OrderRemoteDataSource;

public class OrderRepository {
    private static final String TAG = "CHECKOUT_DEBUG"; // Tag để lọc log
    private final OrderRemoteDataSource remoteDataSource;
    private final CartDao cartDao;

    public OrderRepository(Context context) {
        this.remoteDataSource = new OrderRemoteDataSource();
        this.cartDao = new CartDao(context.getApplicationContext());
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
}
