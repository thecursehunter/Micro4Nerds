package ueh.edu.vn.md.micro4nerds.data.repository;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthRepository {
    private FirebaseAuth firebaseAuth;

    public AuthRepository() {
        this.firebaseAuth = FirebaseAuth.getInstance();
    }

    // Callback interface để trả kết quả về cho Activity/ViewModel
    public interface AuthCallback {
        void onSuccess(FirebaseUser user);
        void onFailure(String message);
    }

    // Hàm Đăng nhập
    public void login(String email, String password, AuthCallback callback) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess(firebaseAuth.getCurrentUser());
                    } else {
                        callback.onFailure(task.getException().getMessage());
                    }
                });
    }

    // Hàm Đăng ký
    public void register(String email, String password, AuthCallback callback) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess(firebaseAuth.getCurrentUser());
                    } else {
                        callback.onFailure(task.getException().getMessage());
                    }
                });
    }

    // Hàm lấy user hiện tại
    public FirebaseUser getCurrentUser() {
        return firebaseAuth.getCurrentUser();
    }

    // Hàm Đăng xuất
    public void logout() {
        firebaseAuth.signOut();
    }
}