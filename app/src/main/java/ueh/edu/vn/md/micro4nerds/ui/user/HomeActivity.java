package ueh.edu.vn.md.micro4nerds.ui.user;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import ueh.edu.vn.md.micro4nerds.data.model.Product;
import ueh.edu.vn.md.micro4nerds.ui.adapter.BannerAdapter;
import ueh.edu.vn.md.micro4nerds.ui.adapter.ProductAdapter;
import ueh.edu.vn.md.micro4nerds.ui.base.BaseActivity;
import ueh.edu.vn.md.micro4nerds.ui.viewmodel.CartViewModel;
import ueh.edu.vn.md.micro4nerds.ui.viewmodel.ProductViewModel;
import ueh.edu.vn.md.micro4nerds.utils.ViewUtils;


public class HomeActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {
    // Khai báo biến
    private ViewPager2 viewPagerBanner;
    private RecyclerView rvProducts;
    private CardView cvBadge;
    private TextView tvCartCount;

    // Drawer & Navigation
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageView btnMenu;

    private BannerAdapter bannerAdapter;
    private ProductAdapter productAdapter;
    private ProductViewModel productViewModel;
    private CartViewModel cartViewModel;

    // Handler để chạy auto slide cho banner
    private Handler sliderHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Ánh xạ View
        initViews();

        // Cấu hình Banner (Slide ảnh)
        setupBanner();

        // Cấu hình Danh sách sản phẩm (Lưới 4x4)
        setupProductList();

        // --- LOGIC MỚI CHO MENU ---
        setupDrawer();      // Cấu hình đóng mở Drawer
        setupDrawerHeader();// Hiển thị Avatar/Tên lên Header
        setupBottomNavBehavior();

        // Quan sát dữ liệu từ ViewModel (Để lấy list từ Firebase)
        observeData();
    }

    private void initViews() {
        viewPagerBanner = findViewById(R.id.viewPagerBanner);
        rvProducts = findViewById(R.id.rvProducts);
        cvBadge = findViewById(R.id.cvBadge);
        tvCartCount = findViewById(R.id.tvCartCount);
        // Ánh xạ Drawer
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        // Ánh xạ nút Menu (Nó nằm trong include toolbar nên findViewById được luôn)
        btnMenu = findViewById(R.id.btnMenu);
    }

    // --- PHẦN 1: LOGIC BANNER ---
    private void setupBanner() {
        // 1. Tạo list ảnh tĩnh (từ Drawable)
        List<Integer> listBanners = new ArrayList<>();
        listBanners.add(R.drawable.banner_1);
        listBanners.add(R.drawable.banner_2);
        listBanners.add(R.drawable.banner_3);

        // 2. Gán Adapter
        bannerAdapter = new BannerAdapter(listBanners);
        viewPagerBanner.setAdapter(bannerAdapter);

        // 3. (Optional) Hiệu ứng chuyển cảnh đẹp mắt
        viewPagerBanner.setClipToPadding(false);
        viewPagerBanner.setClipChildren(false);
        viewPagerBanner.setOffscreenPageLimit(3);
        viewPagerBanner.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        // 4. Đăng ký sự kiện thay đổi trang để chạy Auto Slide
        viewPagerBanner.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                // Hủy lệnh cũ, đặt lệnh mới sau 3 giây
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

                if (currentItem < totalItem - 1) {
                    viewPagerBanner.setCurrentItem(currentItem + 1);
                } else {
                    viewPagerBanner.setCurrentItem(0);
                }
            }
        }
    };

    // --- PHẦN 2: LOGIC SẢN PHẨM ---
    private void setupProductList() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        rvProducts.setLayoutManager(gridLayoutManager);

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

        // Lắng nghe dữ liệu
        // Giả sử hàm trong ViewModel tên là getProducts() hoặc getProductList()
        // Cậu check file ProductViewModel của bạn A để gọi cho đúng tên hàm nhé
        productViewModel.getProductList().observe(this, products -> {
            if (products != null && !products.isEmpty()) {
                productAdapter.setProductList(products);
            } else {
                // Handle empty list
            }
        });

        cartViewModel.getCartItems().observe(this, cartItems -> {
            ViewUtils.updateCartBadge(cvBadge, tvCartCount, cartItems);
        });

        // Gọi hàm load dữ liệu (nếu ViewModel không tự load trong constructor)
        // productViewModel.fetchProducts();
    }

    // --- PHẦN 3: LIFECYCLE (Quản lý pin) ---
    @Override
    protected void onPause() {
        super.onPause();
        sliderHandler.removeCallbacks(sliderRunnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sliderHandler.postDelayed(sliderRunnable, 3000);
        // Refresh cart counter when returning to HomeActivity
        // The observer is already set up in observeData(), but we can force a refresh
        if (cartViewModel != null && cartViewModel.getCartItems().getValue() != null) {
            ViewUtils.updateCartBadge(cvBadge, tvCartCount, cartViewModel.getCartItems().getValue());
        }
    }

    // --- PHẦN 4 (MỚI): CẤU HÌNH MENU TRƯỢT ---
    private void setupDrawer() {
        // 1. Xử lý nút Menu trên Toolbar
        // Ghi đè sự kiện của BaseActivity: Ở Home, bấm nút này là mở Drawer
        if (btnMenu != null) {
            btnMenu.setOnClickListener(v -> {
                drawerLayout.openDrawer(GravityCompat.START);
            });
        }

        // 2. Lắng nghe sự kiện click vào item trong Menu
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void setupDrawerHeader() {
        View headerView = navigationView.getHeaderView(0);
        TextView tvName = headerView.findViewById(R.id.tvNavName);
        TextView tvEmail = headerView.findViewById(R.id.tvNavEmail);
        ImageView imgAvatar = headerView.findViewById(R.id.imgNavAvatar);

        SharedPrefManager pref = new SharedPrefManager(this);

        if (pref.isLoggedIn()) {
            // 2. Lấy nguyên cục User ra (theo code bạn B)
            ueh.edu.vn.md.micro4nerds.data.model.User currentUser = pref.getUser();

            if (currentUser != null) {
                tvName.setText(currentUser.getName());
                tvEmail.setText(currentUser.getEmail());

                Glide.with(this)
                 .load(currentUser.getAvatar())
                 .placeholder(R.drawable.ic_menu)
                 .circleCrop()
                 .into(imgAvatar);
            }
        } else {
            tvName.setText("Khách");
            tvEmail.setText("Vui lòng đăng nhập");
        }
    }

    // --- XỬ LÝ CLICK ITEM TRONG MENU (QUAN TRỌNG) ---
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        String filterKeyword = "";

        int id = item.getItemId();

        // Dùng if-else thay vì switch-case (do bản Android Gradle mới yêu cầu)
        if (id == R.id.nav_home) {
            filterKeyword = ""; // Rỗng = Hiện tất cả
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

        // kiểm tra sự kiện click vào profile trong menu
        else if (id == R.id.nav_profile) {
            // Kiểm tra đăng nhập
            if (new SharedPrefManager(this).isLoggedIn()) {
                startActivity(new Intent(this, ueh.edu.vn.md.micro4nerds.ui.user.ProfileActivity.class));
            } else {
                Toast.makeText(this, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, ueh.edu.vn.md.micro4nerds.ui.auth.LoginActivity.class));
            }
        }
        // kiểm tra sự kiện click vào Order History trong menu
        else if (id == R.id.nav_orders) {
            if (new SharedPrefManager(this).isLoggedIn()) {
                startActivity(new Intent(this, ueh.edu.vn.md.micro4nerds.ui.user.OrderHistoryActivity.class));
            } else {
                Toast.makeText(this, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, ueh.edu.vn.md.micro4nerds.ui.auth.LoginActivity.class));
            }
        }
        else if (id == R.id.nav_logout) {
            new SharedPrefManager(this).logout();
            Toast.makeText(this, "Đã đăng xuất!", Toast.LENGTH_SHORT).show();
            finish();
            startActivity(getIntent());
        }

        // GỌI VIEWMODEL ĐỂ LỌC (Tái sử dụng hàm filter ở trang Search)
        if (!filterKeyword.isEmpty()) {
            List<Product> filtered = productViewModel.filterProducts(filterKeyword);
            productAdapter.setProductList(filtered);
            Toast.makeText(this, "Đang lọc: " + filterKeyword, Toast.LENGTH_SHORT).show();
        } else {
            // Nếu keyword rỗng -> Load lại toàn bộ (Gọi lại hàm gốc)
            productViewModel.loadProducts();
        }

        // Đóng Drawer sau khi chọn
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    // Xử lý nút Back của điện thoại: Nếu Drawer đang mở thì đóng lại chứ không thoát app
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // --- HÀM: XỬ LÝ NÚT HOME KO BỊ GHI ĐÈ TRONG BOTTOM NAV ---
    private void setupBottomNavBehavior() {
        ImageView btnHome = findViewById(R.id.btnHome);
        if (btnHome != null) {
            btnHome.setOnClickListener(v -> {
                // Logic: Reset lại danh sách về ban đầu
                productViewModel.loadProducts(); // Gọi hàm load gốc

                // Cuộn lên đầu trang cho mượt
                androidx.core.widget.NestedScrollView scrollView = findViewById(R.id.nestedScrollView);
                if (scrollView != null) {
                    scrollView.smoothScrollTo(0, 0);
                }
                Toast.makeText(this, "Tất cả sản phẩm", Toast.LENGTH_SHORT).show();
            });
        }
    }
}
