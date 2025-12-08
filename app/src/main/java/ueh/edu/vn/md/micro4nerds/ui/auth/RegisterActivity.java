package ueh.edu.vn.md.micro4nerds.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import ueh.edu.vn.md.micro4nerds.databinding.ActivityRegisterBinding;
import ueh.edu.vn.md.micro4nerds.data.repository.AuthRepository;
import com.google.firebase.auth.FirebaseUser;
import ueh.edu.vn.md.micro4nerds.ui.user.HomeActivity;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private AuthRepository authRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        authRepository = new AuthRepository();

        binding.btnRegister.setOnClickListener(v -> {
            String email = binding.edtEmailReg.getText().toString().trim();
            String pass = binding.edtPasswordReg.getText().toString().trim();
            String confirmPass = binding.edtConfirmPasswordReg.getText().toString().trim();

            if (email.isEmpty() || pass.isEmpty() || confirmPass.isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!pass.equals(confirmPass)) {
                Toast.makeText(RegisterActivity.this, "Mật khẩu không khớp!", Toast.LENGTH_SHORT).show();
                return;
            }

            authRepository.register(email, pass, new AuthRepository.AuthCallback() {
                @Override
                public void onSuccess(FirebaseUser user) {
                    Toast.makeText(RegisterActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                    // Đăng ký xong vào thẳng Home
                    startActivity(new Intent(RegisterActivity.this, HomeActivity.class));
                    finishAffinity(); // Xóa stack để không back về login
                }

                @Override
                public void onFailure(String message) {
                    Toast.makeText(RegisterActivity.this, "Lỗi: " + message, Toast.LENGTH_SHORT).show();
                }
            });
        });

        // Quay lại Login nếu đã có tài khoản
        binding.tvBackToLogin.setOnClickListener(v -> finish());
    }
}