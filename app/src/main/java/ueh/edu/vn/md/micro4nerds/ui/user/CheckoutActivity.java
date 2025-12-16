package ueh.edu.vn.md.micro4nerds.ui.user;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import java.text.NumberFormat;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import ueh.edu.vn.md.micro4nerds.R;
import ueh.edu.vn.md.micro4nerds.data.model.CartItem;
import ueh.edu.vn.md.micro4nerds.data.model.Product;
import ueh.edu.vn.md.micro4nerds.ui.cart.CartViewModel;
import ueh.edu.vn.md.micro4nerds.ui.order.OrderViewModel;

public class CheckoutActivity extends AppCompatActivity {

    private Button btnSubmitOrder;
    private TextView tvOrderAmount, tvDeliveryFee, tvTotalAmount;
    private EditText etFullName, etAddress;
    private RadioGroup rgPaymentMethod, rgDeliveryMethod;
    private ProgressBar progressBar;

    private OrderViewModel orderViewModel;

    private double deliveryFee = 25000;
    private List<CartItem> itemsToCheckout;
    private double orderAmountToCheckout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        orderViewModel = new ViewModelProvider(this).get(OrderViewModel.class);

        initViews();
        checkIntent();
        setupListeners();
        observeViewModel();
    }

    private void checkIntent() {
        boolean isBuyNow = getIntent().getBooleanExtra("is_buy_now", false);
        if (isBuyNow) {
            Product product = (Product) getIntent().getSerializableExtra("product_item");
            if (product != null) {
                itemsToCheckout = Collections.singletonList(new CartItem(product.getId(), product.getName(), product.getPrice(), product.getImageUrl(), 1));
                orderAmountToCheckout = product.getPrice();
                displaySummary(orderAmountToCheckout);
            } else {
                handleCheckoutError("Không tìm thấy thông tin sản phẩm.");
            }
        } else {
            CartViewModel cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);
            cartViewModel.getCartItems().observe(this, cartItems -> itemsToCheckout = cartItems);
            cartViewModel.getTotalPrice().observe(this, totalPrice -> {
                orderAmountToCheckout = totalPrice != null ? totalPrice : 0.0;
                displaySummary(orderAmountToCheckout);
            });
        }
    }

    private void initViews() {
        btnSubmitOrder = findViewById(R.id.btnSubmitOrder);
        tvOrderAmount = findViewById(R.id.tvOrderAmount);
        tvDeliveryFee = findViewById(R.id.tvDeliveryFee);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        etFullName = findViewById(R.id.etFullName);
        etAddress = findViewById(R.id.etAddress);
        rgPaymentMethod = findViewById(R.id.rgPaymentMethod);
        rgDeliveryMethod = findViewById(R.id.rgDeliveryMethod);
        progressBar = findViewById(R.id.progressBarCheckout);
        findViewById(R.id.btnBack).setOnClickListener(v -> onBackPressed());
    }

    private void setupListeners() {
        rgDeliveryMethod.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbExpress) deliveryFee = 100000;
            else if (checkedId == R.id.rbFast) deliveryFee = 50000;
            else deliveryFee = 25000;
            displaySummary(orderAmountToCheckout);
        });

        btnSubmitOrder.setOnClickListener(v -> {
            if (itemsToCheckout == null || itemsToCheckout.isEmpty()) {
                Toast.makeText(this, "Không có sản phẩm để thanh toán!", Toast.LENGTH_SHORT).show();
                return;
            }

            String fullName = etFullName.getText().toString().trim();
            String address = etAddress.getText().toString().trim();

            if (fullName.isEmpty() || address.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ họ tên và địa chỉ", Toast.LENGTH_SHORT).show();
                return;
            }

            int selectedShippingId = rgDeliveryMethod.getCheckedRadioButtonId();
            String shippingMethod;
            if (selectedShippingId != -1) {
                RadioButton selectedRadioButton = findViewById(selectedShippingId);
                shippingMethod = selectedRadioButton.getText().toString();
            } else {
                Toast.makeText(this, "Vui lòng chọn phương thức vận chuyển", Toast.LENGTH_SHORT).show();
                return;
            }

            // Gọi phương thức checkout đã được cập nhật với đầy đủ thông tin
            orderViewModel.checkout(itemsToCheckout, orderAmountToCheckout, fullName, address, shippingMethod);
        });
    }

    private void observeViewModel() {
        orderViewModel.getCheckoutState().observe(this, state -> {
            if (state == null) return;
            switch (state) {
                case LOADING:
                    progressBar.setVisibility(View.VISIBLE);
                    btnSubmitOrder.setEnabled(false);
                    break;
                case SUCCESS:
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Đặt hàng thành công!", Toast.LENGTH_LONG).show();
                    new ViewModelProvider(this).get(CartViewModel.class).loadCartItems();
                    finish();
                    break;
                case ERROR:
                    String errorMessage = orderViewModel.getLastError(); 
                    handleCheckoutError(errorMessage);
                    break;
                case IDLE:
                    btnSubmitOrder.setEnabled(true);
                    progressBar.setVisibility(View.GONE);
                    break;
            }
        });
    }

    private void displaySummary(Double orderAmount) {
        if (orderAmount == null) return;
        double totalAmount = orderAmount + deliveryFee;
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        tvOrderAmount.setText(currencyFormatter.format(orderAmount));
        tvDeliveryFee.setText(currencyFormatter.format(deliveryFee));
        tvTotalAmount.setText(currencyFormatter.format(totalAmount));
    }

    private void handleCheckoutError(String message) {
        progressBar.setVisibility(View.GONE);
        btnSubmitOrder.setEnabled(true);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        orderViewModel.resetCheckoutState();
    }
}
