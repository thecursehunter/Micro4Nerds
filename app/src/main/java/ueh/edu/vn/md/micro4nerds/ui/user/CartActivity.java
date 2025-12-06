package ueh.edu.vn.md.micro4nerds.ui.user;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Locale;

import ueh.edu.vn.md.micro4nerds.R;
import ueh.edu.vn.md.micro4nerds.data.model.CartItem;
import ueh.edu.vn.md.micro4nerds.ui.adapter.CartAdapter;

public class CartActivity extends AppCompatActivity implements CartAdapter.CartItemListener {

    private RecyclerView rvCartItems;
    private TextView tvTotalPrice;
    private Button btnCheckout;
    private ImageView btnBack;
    private CartAdapter cartAdapter;
    private ArrayList<CartItem> cartItems;
    private double currentTotalPrice = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        rvCartItems = findViewById(R.id.rvCartItems);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        btnCheckout = findViewById(R.id.btnCheckout);
        btnBack = findViewById(R.id.btnBack);

        setupRecyclerView();
        setupClickListeners();
        loadInitialData(); 
    }

    private void setupRecyclerView() {
        rvCartItems.setLayoutManager(new LinearLayoutManager(this));
        cartItems = new ArrayList<>();
        cartAdapter = new CartAdapter(this, cartItems, this);
        rvCartItems.setAdapter(cartAdapter);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> onBackPressed());

        btnCheckout.setOnClickListener(v -> {
            Intent intent = new Intent(CartActivity.this, CheckoutActivity.class);
            // Gửi tổng tiền sang CheckoutActivity
            intent.putExtra(CheckoutActivity.EXTRA_ORDER_AMOUNT, currentTotalPrice);
            startActivity(intent);
        });
    }

    private void loadInitialData() {
        cartItems.add(new CartItem("1", "Minimal Stand", 25.00, "", 1));
        cartItems.add(new CartItem("2", "Coffee Table", 20.00, "", 2));
        cartItems.add(new CartItem("3", "Minimal Desk", 50.00, "", 1));
        cartAdapter.notifyDataSetChanged();
        updateTotalPrice();
    }

    private void updateTotalPrice() {
        currentTotalPrice = 0;
        for (CartItem item : cartItems) {
            currentTotalPrice += item.getProductPrice() * item.getQuantity();
        }
        tvTotalPrice.setText(String.format(Locale.US, "$ %.2f", currentTotalPrice));
    }

    @Override
    public void onQuantityChange(int position, int newQuantity) {
        if (newQuantity > 0) {
            cartItems.get(position).setQuantity(newQuantity);
            cartAdapter.notifyItemChanged(position);
            updateTotalPrice();
        }
    }

    @Override
    public void onItemRemove(int position) {
        cartItems.remove(position);
        cartAdapter.notifyItemRemoved(position);
        cartAdapter.notifyItemRangeChanged(position, cartItems.size()); 
        updateTotalPrice();
        Toast.makeText(this, "Item removed", Toast.LENGTH_SHORT).show();
    }
}
