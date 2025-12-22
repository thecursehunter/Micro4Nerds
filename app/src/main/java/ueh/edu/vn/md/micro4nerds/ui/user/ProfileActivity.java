package ueh.edu.vn.md.micro4nerds.ui.user;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;

import ueh.edu.vn.md.micro4nerds.R;
import ueh.edu.vn.md.micro4nerds.data.local.SharedPrefManager;
import ueh.edu.vn.md.micro4nerds.data.model.User;
import ueh.edu.vn.md.micro4nerds.databinding.ActivityProfileBinding;
import ueh.edu.vn.md.micro4nerds.ui.auth.LoginActivity;
import ueh.edu.vn.md.micro4nerds.ui.base.BaseActivity;
import ueh.edu.vn.md.micro4nerds.ui.viewmodel.CartViewModel;
import ueh.edu.vn.md.micro4nerds.utils.ViewUtils;

public class ProfileActivity extends BaseActivity {

    private ActivityProfileBinding binding;
    private CartViewModel cartViewModel;
    private SharedPrefManager sharedPrefManager;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Khởi tạo các lớp quản lý
        sharedPrefManager = new SharedPrefManager(this);
        firebaseAuth = FirebaseAuth.getInstance();

        // Nếu chưa đăng nhập, chuyển về trang Login
        if (!sharedPrefManager.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        loadUserProfile();
        setupMenuListeners();
        observeViewModel();

        binding.btnLogout.setOnClickListener(v -> {
            // Đăng xuất khỏi Firebase và xóa session local
            firebaseAuth.signOut();
            sharedPrefManager.logout();

            // Chuyển về trang Login
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void loadUserProfile() {
        // Lấy thông tin người dùng từ SharedPreferences
        User user = sharedPrefManager.getUser();
        if (user != null) {
            binding.tvEmail.setText(user.getEmail());
            binding.tvName.setText(user.getName() != null ? user.getName() : "User");

            // Tải avatar bằng Glide (đã có URL chất lượng cao từ lúc đăng nhập)
            if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
                Glide.with(this)
                        .load(user.getAvatar())
                        .placeholder(R.mipmap.ic_launcher) // Ảnh chờ (dùng logo thay vì ic_cart)
                        .error(R.mipmap.ic_launcher)       // Ảnh lỗi
                        .into(binding.imgProfile);
            } else {
                binding.imgProfile.setImageResource(R.mipmap.ic_launcher);
            }
        }
    }

    private void setupMenuListeners() {
        binding.tvEditProfile.setOnClickListener(v -> {
            Toast.makeText(this, "Edit Profile clicked", Toast.LENGTH_SHORT).show();
        });

        binding.tvMyOrders.setOnClickListener(v -> {
            // Chuyển sang màn hình Lịch sử đơn hàng
            startActivity(new Intent(this, OrderHistoryActivity.class));
        });

        binding.tvSettings.setOnClickListener(v -> {
            Toast.makeText(this, "Settings clicked", Toast.LENGTH_SHORT).show();
        });
    }

    private void observeViewModel() {
        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);
        cartViewModel.getCartItems().observe(this, cartItems -> {
            ViewUtils.updateCartBadge(binding.bottomNav.cvBadge, binding.bottomNav.tvCartCount, cartItems);
        });
    }
}
