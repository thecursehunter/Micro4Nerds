package ueh.edu.vn.md.micro4nerds.ui.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import ueh.edu.vn.md.micro4nerds.R;
import ueh.edu.vn.md.micro4nerds.data.model.Product;
import ueh.edu.vn.md.micro4nerds.ui.base.BaseActivity;
import ueh.edu.vn.md.micro4nerds.utils.FormatUtils;

public class ProductDetailActivity extends BaseActivity {
    // Khai báo View
    private ImageView imgProduct;
    private TextView tvName, tvStock, tvPrice, tvDesc;
    private Button btnBuyNow, btnAddToCart;
    private ImageView btnHeart;
    private LinearLayout layoutActions; // Cái hộp chứa 3 nút
    private TextView tvOutOfStockMsg;   // Dòng chữ báo hết hàng

    private Product product; // Biến lưu sản phẩm hiện tại
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        initViews();
        getIntentData();
        setupListeners();
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

        // Ánh xạ khu vực ẩn hiện
        layoutActions = findViewById(R.id.layoutActions);
        tvOutOfStockMsg = findViewById(R.id.tvOutOfStockMsg);
    }

    private void getIntentData() {
        // Lấy object Product được gửi từ HomeActivity
        if (getIntent().hasExtra("product_item")) {
            product = (Product) getIntent().getSerializableExtra("product_item");

            if (product != null) {
                displayProductData();
            }
        }
    }

    private void displayProductData() {
        // 1. Tên
        tvName.setText(product.getName());

        // 2. Giá (FormatUtils của Minh)
        tvPrice.setText(FormatUtils.formatCurrency(product.getPrice()));

        // 3. Stock (XỬ LÝ LOGIC STOCK)
        if (product.getStock() <= 0) {
            // Trường hợp HẾT HÀNG
            tvStock.setText("Tạm hết hàng");
            tvStock.setTextColor(getResources().getColor(android.R.color.holo_red_dark)); // Đổi chữ stock thành màu đỏ

            layoutActions.setVisibility(View.GONE);      // Ẩn toàn bộ nút bấm
            tvOutOfStockMsg.setVisibility(View.VISIBLE); // Hiện thông báo to đùng
        } else {
            // Trường hợp CÒN HÀNG
            tvStock.setText("Còn " + product.getStock() + " sản phẩm trong cửa hàng");
            tvStock.setTextColor(getResources().getColor(android.R.color.darker_gray)); // Màu xám mặc định

            layoutActions.setVisibility(View.VISIBLE);   // Hiện nút bấm
            tvOutOfStockMsg.setVisibility(View.GONE);    // Ẩn thông báo
        }

        // 4. Mô tả
        // Kiểm tra nếu null thì hiện thông báo
        if (product.getDescription() != null && !product.getDescription().isEmpty()) {
            tvDesc.setText(product.getDescription());
        } else {
            tvDesc.setText("Đang cập nhật mô tả...");
        }

        // 5. Ảnh (Dùng Glide)
        Glide.with(this)
                .load(product.getImageUrl())
                .placeholder(android.R.color.darker_gray)
                .error(android.R.color.holo_red_light) // Nếu link lỗi hiện màu đỏ
                .centerCrop()
                .into(imgProduct);
    }

    private void setupListeners() {
        // Nút MUA NGAY -> Sang Checkout
        btnBuyNow.setOnClickListener(v -> {
            if (product != null) {
                Intent intent = new Intent(ProductDetailActivity.this, CheckoutActivity.class);
                intent.putExtra("product_item", product);
                intent.putExtra("is_buy_now", true); // Cờ báo hiệu mua ngay
                startActivity(intent);
            }
        });

        // Nút THÊM GIỎ -> Hiện Toast
        btnAddToCart.setOnClickListener(v -> {
            // Giai đoạn 1: Chỉ thông báo
            // Giai đoạn 2: Gọi CartViewModel để lưu vào SQLite
            Toast.makeText(this, "Đã thêm '" + product.getName() + "' vào giỏ hàng!", Toast.LENGTH_SHORT).show();

            // Cập nhật số badge trên BottomNav (Demo chơi cho vui nếu cậu muốn)
            // updateCartCount(1); // Hàm có sẵn trong BaseActivity
        });

        // Nút TIM -> Hiện Toast
        btnHeart.setOnClickListener(v -> {
            Toast.makeText(this, "Đã thêm vào yêu thích", Toast.LENGTH_SHORT).show();
        });
    }
}