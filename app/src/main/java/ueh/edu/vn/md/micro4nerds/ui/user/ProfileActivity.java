package ueh.edu.vn.md.micro4nerds.ui.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseUser;

import ueh.edu.vn.md.micro4nerds.R;
import ueh.edu.vn.md.micro4nerds.data.repository.AuthRepository;
import ueh.edu.vn.md.micro4nerds.databinding.ActivityProfileBinding;
import ueh.edu.vn.md.micro4nerds.ui.auth.LoginActivity;
import ueh.edu.vn.md.micro4nerds.ui.base.BaseActivity;

public class ProfileActivity extends BaseActivity {

    private ActivityProfileBinding binding;
    private AuthRepository authRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        authRepository = new AuthRepository();
        loadUserProfile();
        setupMenuListeners();

        binding.btnLogout.setOnClickListener(v -> {
            authRepository.logout();
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    private void loadUserProfile() {
        FirebaseUser user = authRepository.getCurrentUser();
        if (user != null) {
            binding.tvEmail.setText(user.getEmail());
            binding.tvName.setText(user.getDisplayName() != null ? user.getDisplayName() : "User");

            if (user.getPhotoUrl() != null) {
                Glide.with(this)
                        .load(user.getPhotoUrl())
                        .placeholder(R.drawable.ic_launcher_background) // ảnh mặc định
                        .into(binding.imgProfile);
            }
        }
    }

    private void setupMenuListeners() {
        binding.tvEditProfile.setOnClickListener(v -> {
            // TODO: Implement edit profile functionality
            Toast.makeText(this, "Edit Profile clicked", Toast.LENGTH_SHORT).show();
        });

        binding.tvMyOrders.setOnClickListener(v -> {
            // TODO: Implement my orders functionality
            Toast.makeText(this, "My Orders clicked", Toast.LENGTH_SHORT).show();
        });

        binding.tvSettings.setOnClickListener(v -> {
            // TODO: Implement settings functionality
            Toast.makeText(this, "Settings clicked", Toast.LENGTH_SHORT).show();
        });
    }
}
