package ueh.edu.vn.md.micro4nerds.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.Objects;

import ueh.edu.vn.md.micro4nerds.data.local.SharedPrefManager;
import ueh.edu.vn.md.micro4nerds.data.model.User;
import ueh.edu.vn.md.micro4nerds.data.remote.FirebaseAuthDataSource;
import ueh.edu.vn.md.micro4nerds.databinding.ActivityRegisterBinding;
import ueh.edu.vn.md.micro4nerds.ui.base.BaseActivity;
import ueh.edu.vn.md.micro4nerds.ui.user.HomeActivity;

public class RegisterActivity extends BaseActivity {

    private ActivityRegisterBinding binding;
    private FirebaseAuthDataSource authDataSource;
    private SharedPrefManager sharedPrefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        authDataSource = new FirebaseAuthDataSource();
        sharedPrefManager = new SharedPrefManager(this);

        setClickListeners();
    }

    private void setClickListeners() {
        binding.btnRegister.setOnClickListener(v -> registerUser());
        binding.tvBackToLogin.setOnClickListener(v -> finish());
    }

    private void registerUser() {
        String name = Objects.requireNonNull(binding.edtNameReg.getText()).toString().trim();
        String email = Objects.requireNonNull(binding.edtEmailReg.getText()).toString().trim();
        String password = Objects.requireNonNull(binding.edtPasswordReg.getText()).toString().trim();
        String confirmPassword = Objects.requireNonNull(binding.edtConfirmPasswordReg.getText()).toString().trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Mật khẩu không khớp!", Toast.LENGTH_SHORT).show();
            return;
        }

        showLoading(true);

        authDataSource.register(email, password, name, new FirebaseAuthDataSource.AuthCallback() {
            @Override
            public void onSuccess(User user) {
                sharedPrefManager.saveUser(user);
                showLoading(false);
                Toast.makeText(RegisterActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                navigateToHome();
            }

            @Override
            public void onError(String message) {
                showLoading(false);
                Toast.makeText(RegisterActivity.this, "Lỗi đăng ký: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLoading(boolean isLoading) {
        if (isLoading) {
            binding.btnRegister.setVisibility(View.INVISIBLE);
        } else {
            binding.btnRegister.setVisibility(View.VISIBLE);
        }
    }

    private void navigateToHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
