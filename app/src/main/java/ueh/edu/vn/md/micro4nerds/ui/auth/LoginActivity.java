package ueh.edu.vn.md.micro4nerds.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import ueh.edu.vn.md.micro4nerds.databinding.ActivityLoginBinding; // Auto-generated
import ueh.edu.vn.md.micro4nerds.data.repository.AuthRepository;
import com.google.firebase.auth.FirebaseUser;
import ueh.edu.vn.md.micro4nerds.ui.user.HomeActivity;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private AuthRepository authRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        authRepository = new AuthRepository();

        // Xử lý sự kiện nút Đăng nhập
        binding.btnLogin.setOnClickListener(v -> {
            String email = binding.edtEmail.getText().toString().trim();
            String pass = binding.edtPassword.getText().toString().trim();

            if (!email.isEmpty() && !pass.isEmpty()) {
                authRepository.login(email, pass, new AuthRepository.AuthCallback() {
                    @Override
                    public void onSuccess(FirebaseUser user) {
                        Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                        // Chuyển hướng sang Home (Task 3)
                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                        finish();
                    }

                    @Override
                    public void onFailure(String message) {
                        Toast.makeText(LoginActivity.this, "Lỗi: " + message, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        // Điều hướng sang màn hình Đăng ký (Task 3)
        binding.tvGoToRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }
}