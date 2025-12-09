package ueh.edu.vn.md.micro4nerds.ui.base;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import ueh.edu.vn.md.micro4nerds.R;
import ueh.edu.vn.md.micro4nerds.ui.user.CartActivity;
import ueh.edu.vn.md.micro4nerds.ui.user.HomeActivity;
import ueh.edu.vn.md.micro4nerds.ui.user.ProfileActivity;
// Import thêm các Activity khác nếu có (Search, Wishlist...)

public class BaseActivity extends AppCompatActivity {
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Lưu ý: Không gọi setContentView ở đây, để các con tự gọi
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        // Ngay sau khi giao diện được nạp, ta sẽ tìm và gắn sự kiện cho các nút luôn
        setupCommonUI();
    }

    private void setupCommonUI() {
        // --- 1. XỬ LÝ TOOLBAR (TOP) ---
        View toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            ImageView btnMenu = toolbar.findViewById(R.id.btnMenu);
            CardView cvAvatar = toolbar.findViewById(R.id.cvAvatar);

            // Click Menu -> Mở ngăn kéo hoặc hiện thông báo
            if (btnMenu != null) {
                btnMenu.setOnClickListener(v -> {
                    Toast.makeText(this, "Menu Clicked", Toast.LENGTH_SHORT).show();
                    // TODO: Mở Navigation Drawer nếu có
                });
            }

            // Click Avatar -> Sang trang cá nhân (Profile)
            if (cvAvatar != null) {
                cvAvatar.setOnClickListener(v -> {
                    if (!ProfileActivity.class.isInstance(this)) { // Nếu đang ở Profile rồi thì thôi
                        startActivity(new Intent(this, ProfileActivity.class));
                    }
                });
            }
        }

        // --- 2. XỬ LÝ BOTTOM NAVIGATION (BOTTOM) ---
        View bottomNav = findViewById(R.id.bottomNav);
        if (bottomNav != null) {
            ImageView btnHome = bottomNav.findViewById(R.id.btnHome);
            ImageView btnSearch = bottomNav.findViewById(R.id.btnSearch);
            ImageView btnHeart = bottomNav.findViewById(R.id.btnHeart);
            View btnCartContainer = bottomNav.findViewById(R.id.btnCart); // Layout chứa icon Cart + Badge

            // Home
            if (btnHome != null) {
                btnHome.setOnClickListener(v -> {
                    if (!(this instanceof HomeActivity)) {
                        startActivity(new Intent(this, HomeActivity.class));
                        overridePendingTransition(0, 0); // Tắt hiệu ứng chuyển cảnh cho mượt
                        finish(); // Đóng trang cũ để không bị chồng chất Activity
                    }
                });
            }

            // Cart
            if (btnCartContainer != null) {
                btnCartContainer.setOnClickListener(v -> {
                    if (!(this instanceof CartActivity)) {
                        startActivity(new Intent(this, CartActivity.class));
                        overridePendingTransition(0, 0);
                    }
                });
            }

            // Search & Heart (Tương tự)
            if (btnSearch != null) {
                btnSearch.setOnClickListener(v -> Toast.makeText(this, "Search Clicked", Toast.LENGTH_SHORT).show());
            }
            if (btnHeart != null) {
                btnHeart.setOnClickListener(v -> Toast.makeText(this, "Wishlist Clicked", Toast.LENGTH_SHORT).show());
            }
        }
    }

    // Hàm tiện ích: Cập nhật số lượng trên giỏ hàng (Gọi từ bất cứ đâu)
    public void updateCartCount(int count) {
        TextView tvCount = findViewById(R.id.tvCartCount);
        CardView cvBadge = findViewById(R.id.cvBadge);

        if (tvCount != null && cvBadge != null) {
            if (count > 0) {
                cvBadge.setVisibility(View.VISIBLE);
                tvCount.setText(String.valueOf(count));
            } else {
                cvBadge.setVisibility(View.GONE);
            }
        }
    }
}
