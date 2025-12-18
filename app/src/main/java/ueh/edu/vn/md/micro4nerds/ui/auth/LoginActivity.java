package ueh.edu.vn.md.micro4nerds.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import java.util.Objects;

import ueh.edu.vn.md.micro4nerds.R;
import ueh.edu.vn.md.micro4nerds.data.local.SharedPrefManager;
import ueh.edu.vn.md.micro4nerds.data.model.User;
import ueh.edu.vn.md.micro4nerds.data.remote.FirebaseAuthDataSource;
import ueh.edu.vn.md.micro4nerds.databinding.ActivityLoginBinding;
import ueh.edu.vn.md.micro4nerds.ui.base.BaseActivity;
import ueh.edu.vn.md.micro4nerds.ui.user.HomeActivity;

public class LoginActivity extends BaseActivity {

    private static final String TAG = "LoginActivity";
    private ActivityLoginBinding binding;
    private FirebaseAuthDataSource authDataSource;
    private SharedPrefManager sharedPrefManager;
    private GoogleSignInClient mGoogleSignInClient;
    private ActivityResultLauncher<Intent> googleSignInLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        authDataSource = new FirebaseAuthDataSource();
        sharedPrefManager = new SharedPrefManager(this);

        if (sharedPrefManager.isLoggedIn()) {
            navigateToHome();
            return;
        }

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        configureGoogleSignIn();
        initGoogleSignInLauncher();
        setClickListeners();
    }

    private void configureGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void initGoogleSignInLauncher() {
        googleSignInLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                        try {
                            GoogleSignInAccount account = task.getResult(ApiException.class);
                            firebaseAuthWithGoogle(account);
                        } catch (ApiException e) {
                            Log.w(TAG, "Google sign in failed", e);
                            showLoading(false);
                            Toast.makeText(this, "Google Sign-In failed: " + e.getStatusCode(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        showLoading(false);
                    }
                });
    }

    private void setClickListeners() {
        binding.btnLogin.setOnClickListener(v -> loginUser());
        binding.btnGoogleSignIn.setOnClickListener(v -> signInWithGoogle());
        binding.tvGoToRegister.setOnClickListener(v -> startActivity(new Intent(this, RegisterActivity.class)));
        binding.tvForgotPassword.setOnClickListener(v -> Toast.makeText(this, "Forgot Password clicked", Toast.LENGTH_SHORT).show());
    }

    private void loginUser() {
        String email = Objects.requireNonNull(binding.edtEmail.getText()).toString().trim();
        String password = Objects.requireNonNull(binding.edtPassword.getText()).toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        showLoading(true);

        authDataSource.login(email, password, new FirebaseAuthDataSource.AuthCallback() {
            @Override
            public void onSuccess(User user) {
                sharedPrefManager.saveUser(user);
                showLoading(false);
                Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                navigateToHome();
            }

            @Override
            public void onError(String message) {
                showLoading(false);
                Toast.makeText(LoginActivity.this, "Login Failed: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void signInWithGoogle() {
        showLoading(true);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        googleSignInLauncher.launch(signInIntent);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        authDataSource.loginWithGoogle(account, new FirebaseAuthDataSource.AuthCallback() {
            @Override
            public void onSuccess(User user) {
                sharedPrefManager.saveUser(user);
                showLoading(false);
                Toast.makeText(LoginActivity.this, "Google Sign-In Successful", Toast.LENGTH_SHORT).show();
                navigateToHome();
            }

            @Override
            public void onError(String message) {
                showLoading(false);
                Toast.makeText(LoginActivity.this, "Google Sign-In Failed: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    
    private void showLoading(boolean isLoading) {
        if (isLoading) {
            binding.btnLogin.setVisibility(View.INVISIBLE);
            binding.btnGoogleSignIn.setEnabled(false);
        } else {
            binding.btnLogin.setVisibility(View.VISIBLE);
            binding.btnGoogleSignIn.setEnabled(true);
        }
    }
}
