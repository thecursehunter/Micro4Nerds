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
import ueh.edu.vn.md.micro4nerds.ui.user.SearchActivity;

public class BaseActivity extends AppCompatActivity {
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    private void setupCommonUI() {
        View toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            ImageView btnMenu = toolbar.findViewById(R.id.btnMenu);
            CardView cvAvatar = toolbar.findViewById(R.id.cvAvatar);

            if (btnMenu != null) {
                btnMenu.setOnClickListener(v -> {
                    Toast.makeText(this, "Menu Clicked", Toast.LENGTH_SHORT).show();
                });
            }

            if (cvAvatar != null) {
                cvAvatar.setOnClickListener(v -> {
                    if (!ProfileActivity.class.isInstance(this)) {
                        startActivity(new Intent(this, ProfileActivity.class));
                    }
                });
            }
        }

        View bottomNav = findViewById(R.id.bottomNav);
        if (bottomNav != null) {
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
                btnSearch.setOnClickListener(v -> {
                    startActivity(new Intent(this, SearchActivity.class));
                });
            }
            if (btnHeart != null) {
                btnHeart.setOnClickListener(v -> Toast.makeText(this, "Wishlist Clicked", Toast.LENGTH_SHORT).show());
            }
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
