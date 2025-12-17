package ueh.edu.vn.md.micro4nerds.ui.user;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider; // Thêm import
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import ueh.edu.vn.md.micro4nerds.R;
import ueh.edu.vn.md.micro4nerds.data.model.CartItem;
import ueh.edu.vn.md.micro4nerds.ui.adapter.CartAdapter;
import ueh.edu.vn.md.micro4nerds.ui.base.BaseActivity;
import ueh.edu.vn.md.micro4nerds.ui.viewmodel.CartViewModel; // Thêm import

public class CartActivity extends BaseActivity implements CartAdapter.CartItemListener {

    private RecyclerView rvCartItems;
    private TextView tvTotalPrice;
    private Button btnCheckout;
    private ImageView btnBack;
    private CartAdapter cartAdapter;

    // --- Sử dụng ViewModel ---
    private CartViewModel cartViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // --- Khởi tạo ViewModel ---
        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);

        initViews();
        setupRecyclerView();
        setupClickListeners();
        observeViewModel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // ViewModel sẽ tự động quản lý việc tải lại dữ liệu khi cần
        cartViewModel.loadCartItems();
    }

    private void initViews() {
        rvCartItems = findViewById(R.id.rvCartItems);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        btnCheckout = findViewById(R.id.btnCheckout);
        btnBack = findViewById(R.id.btnBack);
    }

    private void setupRecyclerView() {
        rvCartItems.setLayoutManager(new LinearLayoutManager(this));
        // Adapter sẽ được khởi tạo với danh sách rỗng, ViewModel sẽ cập nhật sau
        cartAdapter = new CartAdapter(this, new ArrayList<>(), this);
        rvCartItems.setAdapter(cartAdapter);
    }

    private void observeViewModel() {
        // Lắng nghe thay đổi danh sách giỏ hàng
        cartViewModel.getCartItems().observe(this, cartItems -> {
            if (cartItems != null) {
                cartAdapter.updateItems(cartItems);
            }
        });

        // Lắng nghe thay đổi tổng tiền
        cartViewModel.getTotalPrice().observe(this, totalPrice -> {
            if (totalPrice != null) {
                NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
                tvTotalPrice.setText(currencyFormatter.format(totalPrice));
            }
        });
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> {
            // Cân nhắc sử dụng onBackPressed() thay vì tạo Intent mới
            onBackPressed();
        });

        btnCheckout.setOnClickListener(v -> {
            if (cartAdapter.getItemCount() == 0) {
                Toast.makeText(this, "Giỏ hàng của bạn đang trống", Toast.LENGTH_SHORT).show();
                return;
            }
            // ĐÃ SỬA: Không cần gửi tổng tiền nữa
            // CheckoutActivity sẽ tự lấy dữ liệu từ ViewModel
            Intent intent = new Intent(CartActivity.this, CheckoutActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onQuantityChange(int position, int newQuantity) {
        CartItem item = cartAdapter.getItem(position);
        if (item != null) {
            cartViewModel.updateQuantity(item.getProductId(), newQuantity);
        }
    }

    @Override
    public void onItemRemove(int position) {
        CartItem item = cartAdapter.getItem(position);
        if (item != null) {
            cartViewModel.removeFromCart(item.getProductId());
            Toast.makeText(this, "Đã xóa sản phẩm", Toast.LENGTH_SHORT).show();
        }
    }
}
