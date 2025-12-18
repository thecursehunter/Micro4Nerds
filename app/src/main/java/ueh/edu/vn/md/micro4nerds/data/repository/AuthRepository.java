package ueh.edu.vn.md.micro4nerds.data.repository;

import android.content.Context;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import ueh.edu.vn.md.micro4nerds.utils.SharedPrefManager;

public class AuthRepository {
    private final FirebaseAuth firebaseAuth;
    private final SharedPrefManager sharedPrefManager;

    public AuthRepository(Context context) {
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.sharedPrefManager = SharedPrefManager.getInstance(context.getApplicationContext());
    }

    public interface AuthCallback {
        void onSuccess(FirebaseUser user);
        void onFailure(String message);
    }

    public void login(String email, String password, AuthCallback callback) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        sharedPrefManager.userLogin(user);
                        callback.onSuccess(user);
                    } else {
                        callback.onFailure(task.getException().getMessage());
                    }
                });
    }
    
    public void loginWithCredential(AuthCredential credential, AuthCallback callback) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        sharedPrefManager.userLogin(user);
                        callback.onSuccess(user);
                    } else {
                        callback.onFailure(task.getException().getMessage());
                    }
                });
    }

    public void register(String email, String password, AuthCallback callback) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        sharedPrefManager.userLogin(user);
                        callback.onSuccess(user);
                    } else {
                        callback.onFailure(task.getException().getMessage());
                    }
                });
    }

    public FirebaseUser getCurrentUser() {
        return firebaseAuth.getCurrentUser();
    }

    public void logout() {
        firebaseAuth.signOut();
        sharedPrefManager.logout();
    }

    public boolean isLoggedIn() {
        return sharedPrefManager.isLoggedIn();
    }
}