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
        // --- Code Firebase gốc ---
        FirebaseUser currentUser = auth.getCurrentUser();
        
        // Vì bạn chưa có hệ thống đăng nhập, currentUser sẽ là null
        // và sẽ luôn đi vào nhánh onFailure, gây ra lỗi "Đặt hàng thất bại".
        // Đây là hành vi đúng theo thiết kế bảo mật.
        if (currentUser == null) {
            callback.onFailure(new Exception("User not logged in."));
            return;
        }

        order.setUserId(currentUser.getUid());

        // Thêm đơn hàng vào collection "orders" trong Firestore
        db.collection("orders")
                .add(order)
                .addOnSuccessListener(documentReference -> callback.onSuccess())
                .addOnFailureListener(callback::onFailure);
    }
}
