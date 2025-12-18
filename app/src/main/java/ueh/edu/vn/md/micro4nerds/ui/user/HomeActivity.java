package ueh.edu.vn.md.micro4nerds.ui.user;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.List;

import ueh.edu.vn.md.micro4nerds.R;
import ueh.edu.vn.md.micro4nerds.data.model.Product;
import ueh.edu.vn.md.micro4nerds.ui.adapter.BannerAdapter;
import ueh.edu.vn.md.micro4nerds.ui.adapter.ProductAdapter;
import ueh.edu.vn.md.micro4nerds.ui.base.BaseActivity;
import ueh.edu.vn.md.micro4nerds.ui.viewmodel.CartViewModel;
import ueh.edu.vn.md.micro4nerds.ui.viewmodel.ProductViewModel;
import ueh.edu.vn.md.micro4nerds.utils.ViewUtils;

public class HomeActivity extends BaseActivity {
    // Khai báo biến
    private ViewPager2 viewPagerBanner;
    private RecyclerView rvProducts;
    private CardView cvBadge;
    private TextView tvCartCount;

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

        // Quan sát dữ liệu từ ViewModel (Để lấy list từ Firebase)
        observeData();
    }

    private void initViews() {
        viewPagerBanner = findViewById(R.id.viewPagerBanner);
        rvProducts = findViewById(R.id.rvProducts);
        cvBadge = findViewById(R.id.cvBadge);
        tvCartCount = findViewById(R.id.tvCartCount);
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
    }
}
