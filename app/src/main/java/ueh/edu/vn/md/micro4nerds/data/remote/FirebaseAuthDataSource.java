package ueh.edu.vn.md.micro4nerds.data.remote;

import android.net.Uri;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import ueh.edu.vn.md.micro4nerds.data.model.User;

public class FirebaseAuthDataSource {
    private final FirebaseAuth auth;
    private final FirebaseFirestore db;

    public FirebaseAuthDataSource() {
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    public interface AuthCallback {
        void onSuccess(User user);
        void onError(String message);
    }

    public void register(String email, String password, String name, AuthCallback callback) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser firebaseUser = authResult.getUser();
                    if (firebaseUser != null) {
                        User newUser = new User(firebaseUser.getUid(), email, name, "user");
                        db.collection("users").document(newUser.getUid())
                                .set(newUser)
                                .addOnSuccessListener(aVoid -> callback.onSuccess(newUser))
                                .addOnFailureListener(e -> callback.onError("Lỗi lưu DB: " + e.getMessage()));
                    }
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void login(String email, String password, AuthCallback callback) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser firebaseUser = auth.getCurrentUser();
                    if (firebaseUser != null) {
                        db.collection("users").document(firebaseUser.getUid())
                                .get()
                                .addOnSuccessListener(documentSnapshot -> {
                                    if (documentSnapshot.exists()) {
                                        User user = documentSnapshot.toObject(User.class);
                                        callback.onSuccess(user);
                                    } else {
                                        callback.onError("Không tìm thấy thông tin user.");
                                    }
                                })
                                .addOnFailureListener(e -> callback.onError("Lỗi lấy data từ DB: " + e.getMessage()));
                    } else {
                        callback.onError("Lỗi không xác định, user null.");
                    }
                })
                .addOnFailureListener(e -> callback.onError("Sai email hoặc mật khẩu."));
    }

    // --- 3. ĐĂNG NHẬP BẰNG GOOGLE ---
    public void loginWithGoogle(GoogleSignInAccount account, AuthCallback callback) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser firebaseUser = authResult.getUser();
                    if (firebaseUser != null) {
                        // Lấy thông tin chi tiết từ FirebaseUser
                        String uid = firebaseUser.getUid();
                        String name = firebaseUser.getDisplayName();
                        String email = firebaseUser.getEmail();
                        Uri photoUrl = firebaseUser.getPhotoUrl();

                        // **Fix Avatar URL để lấy ảnh nét hơn**
                        final String highResPhotoUrl = (photoUrl != null) 
                            ? photoUrl.toString().replace("s96-c", "s400-c") 
                            : null;

                        // Kiểm tra xem user đã tồn tại trong Firestore chưa
                        db.collection("users").document(uid).get()
                           .addOnSuccessListener(documentSnapshot -> {
                               User userToSave;
                               if (documentSnapshot.exists()) {
                                   // User đã tồn tại, cập nhật thông tin (nếu cần)
                                   userToSave = documentSnapshot.toObject(User.class);
                                   // Cập nhật lại avatar và tên nếu có thay đổi
                                   userToSave.setName(name);
                                   userToSave.setAvatar(highResPhotoUrl);
                               } else {
                                   // User mới, tạo object mới
                                   userToSave = new User(uid, email, name, "user");
                                   userToSave.setAvatar(highResPhotoUrl);
                               }

                               // Lưu hoặc cập nhật user vào Firestore
                               db.collection("users").document(uid).set(userToSave)
                                 .addOnSuccessListener(aVoid -> callback.onSuccess(userToSave))
                                 .addOnFailureListener(e -> callback.onError("Lỗi lưu thông tin Google User: " + e.getMessage()));
                           })
                           .addOnFailureListener(e -> callback.onError("Lỗi kiểm tra user: " + e.getMessage()));
                    }
                })
                .addOnFailureListener(e -> callback.onError("Đăng nhập Google thất bại: " + e.getMessage()));
    }
}
