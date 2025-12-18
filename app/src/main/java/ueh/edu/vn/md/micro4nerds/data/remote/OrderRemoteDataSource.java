package ueh.edu.vn.md.micro4nerds.data.remote;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import ueh.edu.vn.md.micro4nerds.data.model.Order;

public class OrderRemoteDataSource {

    private final FirebaseFirestore db;
    private final FirebaseAuth auth;

    public OrderRemoteDataSource() {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    public interface OrderCallback {
        void onSuccess();
        void onFailure(Exception e);
    }

    public void createOrder(Order order, OrderCallback callback) {
        // --- BẬT LẠI TÍNH NĂNG KIỂM TRA ĐĂNG NHẬP ---
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            callback.onFailure(new Exception("User not logged in."));
            return;
        }

        // Sử dụng ID của người dùng thực tế
        order.setUserId(currentUser.getUid());

        // Thêm đơn hàng vào collection "orders" trong Firestore
        db.collection("orders")
                .add(order)
                .addOnSuccessListener(documentReference -> callback.onSuccess())
                .addOnFailureListener(callback::onFailure);
    }
}
