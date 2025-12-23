package ueh.edu.vn.md.micro4nerds.ui.base;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;

import ueh.edu.vn.md.micro4nerds.R;
import ueh.edu.vn.md.micro4nerds.data.local.SharedPrefManager;
import ueh.edu.vn.md.micro4nerds.data.model.User;
import ueh.edu.vn.md.micro4nerds.ui.auth.LoginActivity;
import ueh.edu.vn.md.micro4nerds.ui.user.ProfileActivity;
import ueh.edu.vn.md.micro4nerds.ui.user.CartActivity;
import ueh.edu.vn.md.micro4nerds.ui.user.HomeActivity;
import ueh.edu.vn.md.micro4nerds.ui.user.SearchActivity;

public class BaseActivity extends AppCompatActivity {

    private SharedPrefManager sharedPrefManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPrefManager = new SharedPrefManager(this);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        setupCommonUI();
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        setupCommonUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateToolbarAvatar();
    }

    private void setupCommonUI() {
        View toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setupToolbar(toolbar);
        }

        View bottomNav = findViewById(R.id.bottomNav);
        if (bottomNav != null) {
            setupBottomNavigation(bottomNav);
        }
    }

    private void setupToolbar(View toolbar) {
        ImageView btnMenu = toolbar.findViewById(R.id.btnMenu);
        CardView cvAvatar = toolbar.findViewById(R.id.cvAvatar);

        if (btnMenu != null) {
            if (this instanceof HomeActivity) {
                btnMenu.setImageResource(R.drawable.ic_menu);
            }
            else {
                btnMenu.setImageResource(R.drawable.ic_back);
                btnMenu.setOnClickListener(v -> finish());
            }
        }

        if (cvAvatar != null) {
            cvAvatar.setOnClickListener(v -> {
                if (sharedPrefManager.isLoggedIn()) {
                    if (!(this instanceof ProfileActivity)) {
                        startActivity(new Intent(this, ProfileActivity.class));
                    }
                } else {
                    if (!(this instanceof LoginActivity)) {
                        startActivity(new Intent(this, LoginActivity.class));
                    }
                }
            });
        }
    }

    private void setupBottomNavigation(View bottomNav) {
        ImageView btnHome = bottomNav.findViewById(R.id.btnHome);
        ImageView btnSearch = bottomNav.findViewById(R.id.btnSearch);
        ImageView btnHeart = bottomNav.findViewById(R.id.btnHeart);
        View btnCartContainer = bottomNav.findViewById(R.id.btnCart);

        if (btnHome != null) {
            btnHome.setOnClickListener(v -> {
                if (!(this instanceof HomeActivity)) {
                    Intent intent = new Intent(this, HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                }
            });
        }

        if (btnCartContainer != null) {
            btnCartContainer.setOnClickListener(v -> {
                if (!(this instanceof CartActivity)) {
                    startActivity(new Intent(this, CartActivity.class));
                    overridePendingTransition(0, 0);
                }
            });
        }

        if (btnSearch != null) {
            btnSearch.setOnClickListener(v -> startActivity(new Intent(this, SearchActivity.class)));
        }

        if (btnHeart != null) {
            btnHeart.setOnClickListener(v -> Toast.makeText(this, "Wishlist Clicked", Toast.LENGTH_SHORT).show());
        }
    }

    private void updateToolbarAvatar() {
        ImageView imgAvatar = findViewById(R.id.imgAvatar);
        if (imgAvatar == null) return;

        if (sharedPrefManager.isLoggedIn()) {
            User user = sharedPrefManager.getUser();
            if (user != null) {
                String avatarData = user.getAvatar();
                if (avatarData != null && !avatarData.isEmpty()) {
                    if (avatarData.startsWith("http")) {
                        // Trường hợp là link URL
                        Glide.with(this)
                                .load(avatarData)
                                .placeholder(R.drawable.ic_default_avatar)
                                .error(R.drawable.ic_default_avatar)
                                .into(imgAvatar);
                    } else {
                        // Trường hợp là chuỗi Base64 (Mã hóa)
                        try {
                            byte[] decodedString = Base64.decode(avatarData, Base64.DEFAULT);
                            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            imgAvatar.setImageBitmap(decodedByte);
                        } catch (Exception e) {
                            imgAvatar.setImageResource(R.drawable.ic_default_avatar);
                        }
                    }
                } else {
                    imgAvatar.setImageResource(R.drawable.ic_default_avatar);
                }
            }
        } else {
            imgAvatar.setImageResource(R.drawable.ic_default_avatar);
        }
    }

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
