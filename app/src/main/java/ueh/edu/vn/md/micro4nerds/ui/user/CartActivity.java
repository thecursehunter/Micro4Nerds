package ueh.edu.vn.md.micro4nerds.ui.user;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ueh.edu.vn.md.micro4nerds.R;
import ueh.edu.vn.md.micro4nerds.data.local.dao.CartDao;
import ueh.edu.vn.md.micro4nerds.data.model.CartItem;
import ueh.edu.vn.md.micro4nerds.ui.adapter.CartAdapter;
import ueh.edu.vn.md.micro4nerds.ui.base.BaseActivity;

public class CartActivity extends BaseActivity implements CartAdapter.CartItemListener {

    private RecyclerView rvCartItems;
    private TextView tvTotalPrice;
    private Button btnCheckout;
    private ImageView btnBack;
    private CartAdapter cartAdapter;
    private List<CartItem> cartItems;
    private CartDao cartDao;
    private double currentTotalPrice = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        cartDao = new CartDao(this);

        rvCartItems = findViewById(R.id.rvCartItems);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        btnCheckout = findViewById(R.id.btnCheckout);
        btnBack = findViewById(R.id.btnBack);

        setupRecyclerView();
        setupClickListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCartItems();
    }

    private void setupRecyclerView() {
        rvCartItems.setLayoutManager(new LinearLayoutManager(this));
        cartItems = new ArrayList<>();
        cartAdapter = new CartAdapter(this, cartItems, this);
        rvCartItems.setAdapter(cartAdapter);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> {
            startActivity(new Intent(CartActivity.this, HomeActivity.class));
            finish();
        });

        btnCheckout.setOnClickListener(v -> {
            if (cartItems.isEmpty()) {
                Toast.makeText(this, "Giỏ hàng của bạn đang trống", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(CartActivity.this, CheckoutActivity.class);
            // Gửi tổng tiền sang CheckoutActivity
            intent.putExtra(CheckoutActivity.EXTRA_ORDER_AMOUNT, currentTotalPrice);
            startActivity(intent);
        });
    }

    private void loadCartItems() {
        cartItems.clear();
        cartItems.addAll(cartDao.getCartItems());
        cartAdapter.notifyDataSetChanged();
        updateTotalPrice();
    }

    private void updateTotalPrice() {
        currentTotalPrice = 0;
        for (CartItem item : cartItems) {
            currentTotalPrice += item.getProductPrice() * item.getQuantity();
        }
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        tvTotalPrice.setText(currencyFormatter.format(currentTotalPrice));
    }

    @Override
    public void onQuantityChange(int position, int newQuantity) {
        if (newQuantity > 0) {
            CartItem item = cartItems.get(position);
            item.setQuantity(newQuantity);
            cartDao.updateQuantity(item.getProductId(), newQuantity);
            cartAdapter.notifyItemChanged(position);
            updateTotalPrice();
        }
    }

    @Override
    public void onItemRemove(int position) {
        CartItem item = cartItems.get(position);
        cartDao.removeFromCart(item.getProductId());
        cartItems.remove(position);
        cartAdapter.notifyItemRemoved(position);
        cartAdapter.notifyItemRangeChanged(position, cartItems.size());
        updateTotalPrice();
        Toast.makeText(this, "Đã xóa sản phẩm", Toast.LENGTH_SHORT).show();
    }
}
