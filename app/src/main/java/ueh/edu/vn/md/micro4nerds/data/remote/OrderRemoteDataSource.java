package ueh.edu.vn.md.micro4nerds.data.remote;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.List;

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

    // Callback mới để trả về danh sách đơn hàng
    public interface GetOrdersCallback {
        void onOrdersLoaded(List<Order> orders);
        void onError(Exception e);
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

    // Hàm lấy lịch sử đơn hàng
    public void fetchOrdersFromFirestore(String userId, GetOrdersCallback callback) {
        db.collection("orders")
                .whereEqualTo("userId", userId)
                // .orderBy("timestamp", Query.Direction.DESCENDING) // TẠM THỜI TẮT DÒNG NÀY ĐỂ FIX LỖI INDEX
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // Chuyển đổi kết quả thành List<Order>
                    List<Order> orders = queryDocumentSnapshots.toObjects(Order.class);
                    callback.onOrdersLoaded(orders);
                })
                .addOnFailureListener(callback::onError);
    }
}
