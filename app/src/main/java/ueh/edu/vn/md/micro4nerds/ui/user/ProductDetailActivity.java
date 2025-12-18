package ueh.edu.vn.md.micro4nerds.ui.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;

import ueh.edu.vn.md.micro4nerds.R;
import ueh.edu.vn.md.micro4nerds.data.model.Product;
import ueh.edu.vn.md.micro4nerds.data.repository.CartRepository; // Thêm import
import ueh.edu.vn.md.micro4nerds.ui.base.BaseActivity;
import ueh.edu.vn.md.micro4nerds.ui.viewmodel.CartViewModel;
import ueh.edu.vn.md.micro4nerds.utils.FormatUtils;
import ueh.edu.vn.md.micro4nerds.utils.ViewUtils;

public class ProductDetailActivity extends BaseActivity {
    // Khai báo View
    private ImageView imgProduct;
    private TextView tvName, tvStock, tvPrice, tvDesc;
    private Button btnBuyNow, btnAddToCart;
    private ImageView btnHeart;
    private LinearLayout layoutActions;
    private TextView tvOutOfStockMsg;
    private CardView cvBadge;
    private TextView tvCartCount;

    private Product product;
    private CartRepository cartRepository; // Thêm CartRepository
    private CartViewModel cartViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        // Khởi tạo repository
        cartRepository = new CartRepository(this);

        initViews();
        getIntentData();
        setupListeners();
        observeViewModel();
    }

    private void initViews() {
        imgProduct = findViewById(R.id.imgProductDetail);
        tvName = findViewById(R.id.tvDetailName);
        tvStock = findViewById(R.id.tvDetailStock);
        tvPrice = findViewById(R.id.tvDetailPrice);
        tvDesc = findViewById(R.id.tvDetailDesc);

        btnBuyNow = findViewById(R.id.btnBuyNow);
        btnAddToCart = findViewById(R.id.btnAddToCart);
        btnHeart = findViewById(R.id.btnHeart);

        layoutActions = findViewById(R.id.layoutActions);
        tvOutOfStockMsg = findViewById(R.id.tvOutOfStockMsg);
        cvBadge = findViewById(R.id.cvBadge);
        tvCartCount = findViewById(R.id.tvCartCount);
    }

    private void getIntentData() {
        if (getIntent().hasExtra("product_item")) {
            product = (Product) getIntent().getSerializableExtra("product_item");
            if (product != null) {
                displayProductData();
            }
        }
    }

    private void displayProductData() {
        tvName.setText(product.getName());
        tvPrice.setText(FormatUtils.formatCurrency(product.getPrice()));

        if (product.getStock() <= 0) {
            tvStock.setText("Tạm hết hàng");
            tvStock.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            layoutActions.setVisibility(View.GONE);
            tvOutOfStockMsg.setVisibility(View.VISIBLE);
        } else {
            tvStock.setText("Còn " + product.getStock() + " sản phẩm trong cửa hàng");
            tvStock.setTextColor(getResources().getColor(android.R.color.darker_gray));
            layoutActions.setVisibility(View.VISIBLE);
            tvOutOfStockMsg.setVisibility(View.GONE);
        }

        if (product.getDescription() != null && !product.getDescription().isEmpty()) {
            tvDesc.setText(product.getDescription());
        } else {
            tvDesc.setText("Đang cập nhật mô tả...");
        }

        Glide.with(this)
                .load(product.getImageUrl())
                .placeholder(android.R.color.darker_gray)
                .error(android.R.color.holo_red_light)
                .centerCrop()
                .into(imgProduct);
    }

    private void setupListeners() {
        btnBuyNow.setOnClickListener(v -> {
            if (product != null) {
                Intent intent = new Intent(ProductDetailActivity.this, CheckoutActivity.class);
                intent.putExtra("product_item", product);
                intent.putExtra("is_buy_now", true);
                startActivity(intent);
            }
        });

        // Nút THÊM GIỎ HÀNG -> Lưu vào SQLite
        btnAddToCart.setOnClickListener(v -> {
            if (product != null) {
                // Gọi repository để thêm sản phẩm vào giỏ hàng
                cartRepository.addToCart(product);

                // Hiển thị thông báo cho người dùng
                Toast.makeText(this, "Đã thêm '" + product.getName() + "' vào giỏ hàng!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Không thể thêm sản phẩm này vào giỏ hàng.", Toast.LENGTH_SHORT).show();
            }
        });

        btnHeart.setOnClickListener(v -> {
            Toast.makeText(this, "Đã thêm vào yêu thích", Toast.LENGTH_SHORT).show();
        });
    }

    private void observeViewModel() {
        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);
        cartViewModel.getCartItems().observe(this, cartItems -> {
            ViewUtils.updateCartBadge(cvBadge, tvCartCount, cartItems);
        });
    }
}
