package ueh.edu.vn.md.micro4nerds.ui.user;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.widget.TextView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

import ueh.edu.vn.md.micro4nerds.R;
import ueh.edu.vn.md.micro4nerds.data.local.SharedPrefManager;
import ueh.edu.vn.md.micro4nerds.data.model.CartItem;
import ueh.edu.vn.md.micro4nerds.data.model.Product;
import ueh.edu.vn.md.micro4nerds.ui.adapter.BannerAdapter;
import ueh.edu.vn.md.micro4nerds.ui.adapter.ProductAdapter;
import ueh.edu.vn.md.micro4nerds.ui.base.BaseActivity;
import ueh.edu.vn.md.micro4nerds.ui.viewmodel.CartViewModel;
import ueh.edu.vn.md.micro4nerds.ui.viewmodel.ProductViewModel;


public class HomeActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {
    private ViewPager2 viewPagerBanner;
    private RecyclerView rvProducts;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageView btnMenu;

    private BannerAdapter bannerAdapter;
    private ProductAdapter productAdapter;
    private ProductViewModel productViewModel;
    private CartViewModel cartViewModel;

    private Handler sliderHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initViews();
        setupBanner();
        setupProductList();
        setupDrawer();
        setupDrawerHeader();
        setupBottomNavBehavior();
        observeData();
    }

    private void initViews() {
        viewPagerBanner = findViewById(R.id.viewPagerBanner);
        rvProducts = findViewById(R.id.rvProducts);
        // Ánh xạ Drawer
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        btnMenu = findViewById(R.id.btnMenu);
    }

    private void setupBanner() {
        List<Integer> listBanners = new ArrayList<>();
        listBanners.add(R.drawable.banner_1);
        listBanners.add(R.drawable.banner_2);
        listBanners.add(R.drawable.banner_3);

        bannerAdapter = new BannerAdapter(listBanners);
        viewPagerBanner.setAdapter(bannerAdapter);

        viewPagerBanner.setClipToPadding(false);
        viewPagerBanner.setClipChildren(false);
        viewPagerBanner.setOffscreenPageLimit(3);
        viewPagerBanner.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        viewPagerBanner.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                sliderHandler.removeCallbacks(sliderRunnable);
                sliderHandler.postDelayed(sliderRunnable, 3000);
            }
        });
    }

    private Runnable sliderRunnable = new Runnable() {
        @Override
        public void run() {
            if (viewPagerBanner.getAdapter() != null) {
                int currentItem = viewPagerBanner.getCurrentItem();
                int totalItem = viewPagerBanner.getAdapter().getItemCount();
                viewPagerBanner.setCurrentItem(currentItem < totalItem - 1 ? currentItem + 1 : 0);
            }
        }
    };

    private void setupProductList() {
        int spanCount = getResources().getConfiguration().orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE ? 3 : 2;
        rvProducts.setLayoutManager(new GridLayoutManager(this, spanCount));

        productAdapter = new ProductAdapter(this, new ProductAdapter.ProductClickListener() {
            @Override
            public void onProductClick(Product product) {
                Intent intent = new Intent(HomeActivity.this, ProductDetailActivity.class);
                intent.putExtra("product_item", product);
                startActivity(intent);
            }

            @Override
            public void onBuyNowClick(Product product) {
                Intent intent = new Intent(HomeActivity.this, CheckoutActivity.class);
                intent.putExtra("product_item", product);
                intent.putExtra("is_buy_now", true);
                startActivity(intent);
            }
        });
        rvProducts.setAdapter(productAdapter);
    }

    private void observeData() {
        productViewModel = new ViewModelProvider(this).get(ProductViewModel.class);
        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);

        productViewModel.getProductList().observe(this, products -> {
            if (products != null && !products.isEmpty()) {
                productAdapter.setProductList(products);
            }
        });

        cartViewModel.getCartItems().observe(this, cartItems -> {
            updateCartCount(calculateTotalItems(cartItems));
        });
    }

    private int calculateTotalItems(List<CartItem> cartItems) {
        int total = 0;
        if (cartItems != null) {
            for (CartItem item : cartItems) {
                total += item.getQuantity();
            }
        }
        return total;
    }

    @Override
    protected void onPause() {
        super.onPause();
        sliderHandler.removeCallbacks(sliderRunnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sliderHandler.postDelayed(sliderRunnable, 3000);
        if (cartViewModel != null && cartViewModel.getCartItems().getValue() != null) {
            updateCartCount(calculateTotalItems(cartViewModel.getCartItems().getValue()));
        }
        setupDrawerHeader(); // Cập nhật lại header mỗi khi quay lại trang chủ
    }

    private void setupDrawer() {
        if (btnMenu != null) {
            btnMenu.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
        }
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void setupDrawerHeader() {
        View headerView = navigationView.getHeaderView(0);
        TextView tvName = headerView.findViewById(R.id.tvNavName);
        TextView tvEmail = headerView.findViewById(R.id.tvNavEmail);
        ImageView imgAvatar = headerView.findViewById(R.id.imgNavAvatar);

        SharedPrefManager pref = new SharedPrefManager(this);

        if (pref.isLoggedIn()) {
            ueh.edu.vn.md.micro4nerds.data.model.User currentUser = pref.getUser();
            if (currentUser != null) {
                tvName.setText(currentUser.getName());
                tvEmail.setText(currentUser.getEmail());

                // LOGIC GIẢI MÃ AVATAR (BASE64 HOẶC URL)
                String avatarData = currentUser.getAvatar();
                if (avatarData != null && !avatarData.isEmpty()) {
                    if (avatarData.startsWith("http")) {
                        Glide.with(this).load(avatarData).circleCrop().into(imgAvatar);
                    } else {
                        try {
                            byte[] decodedString = Base64.decode(avatarData, Base64.DEFAULT);
                            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            Glide.with(this).load(decodedByte).circleCrop().into(imgAvatar);
                        } catch (Exception e) {
                            imgAvatar.setImageResource(R.drawable.ic_default_avatar);
                        }
                    }
                } else {
                    imgAvatar.setImageResource(R.drawable.ic_default_avatar);
                }
            }
        } else {
            tvName.setText("Khách");
            tvEmail.setText("Vui lòng đăng nhập");
            imgAvatar.setImageResource(R.drawable.ic_default_avatar);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        String filterKeyword = "";

        if (id == R.id.nav_home) {
            filterKeyword = "";
        }
        else if (id == R.id.nav_camera_panasonic) {
            filterKeyword = "Panasonic";
        }
        else if (id == R.id.nav_camera_olympus) {
            filterKeyword = "Olympus";
        }
        else if (id == R.id.nav_camera_om) {
            filterKeyword = "OM";
        }
        else if (id == R.id.nav_camera_blackmagic) {
            filterKeyword = "Blackmagic";
        }
        else if (id == R.id.nav_acc_lens) {
            filterKeyword = "Lens";
        }
        else if (id == R.id.nav_acc_bag) {
            filterKeyword = "Túi";
        }
        else if (id == R.id.nav_profile) {
            if (new SharedPrefManager(this).isLoggedIn()) {
                startActivity(new Intent(this, ueh.edu.vn.md.micro4nerds.ui.user.ProfileActivity.class));
            } else {
                Toast.makeText(this, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, ueh.edu.vn.md.micro4nerds.ui.auth.LoginActivity.class));
            }
        }
        else if (id == R.id.nav_orders) {
            if (new SharedPrefManager(this).isLoggedIn()) startActivity(new Intent(this, OrderHistoryActivity.class));
            else startActivity(new Intent(this, ueh.edu.vn.md.micro4nerds.ui.auth.LoginActivity.class));
        }
        else if (id == R.id.nav_logout) {
            new SharedPrefManager(this).logout();
            finish();
            startActivity(getIntent());
        }

        if (!filterKeyword.isEmpty()) {
            productAdapter.setProductList(productViewModel.filterProducts(filterKeyword));
        } else {
            productViewModel.loadProducts();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) drawerLayout.closeDrawer(GravityCompat.START);
        else super.onBackPressed();
    }

    private void setupBottomNavBehavior() {
        ImageView btnHome = findViewById(R.id.btnHome);
        if (btnHome != null) {
            btnHome.setOnClickListener(v -> {
                productViewModel.loadProducts();
                androidx.core.widget.NestedScrollView scrollView = findViewById(R.id.nestedScrollView);
                if (scrollView != null) {
                    scrollView.smoothScrollTo(0, 0);
                }
                Toast.makeText(this, "Tất cả sản phẩm", Toast.LENGTH_SHORT).show();
            });
        }
    }
}
