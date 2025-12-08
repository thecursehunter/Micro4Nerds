package ueh.edu.vn.md.micro4nerds.ui.user;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import ueh.edu.vn.md.micro4nerds.databinding.ActivityProfileBinding;
import ueh.edu.vn.md.micro4nerds.data.repository.AuthRepository;
import com.google.firebase.auth.FirebaseUser;
import ueh.edu.vn.md.micro4nerds.ui.auth.LoginActivity;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;
    private AuthRepository authRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        authRepository = new AuthRepository();
        loadUserProfile();

        // Nút Đăng xuất
        binding.btnLogout.setOnClickListener(v -> {
            authRepository.logout();
            // Quay về màn hình Login và xóa lịch sử activity
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    private void loadUserProfile() {
        FirebaseUser user = authRepository.getCurrentUser();
        if (user != null) {
            binding.tvEmail.setText(user.getEmail());
            // Nếu có tên hiển thị (cần update profile firebase trước đó)
            binding.tvName.setText(user.getDisplayName() != null ? user.getDisplayName() : "User");
        }
    }
}