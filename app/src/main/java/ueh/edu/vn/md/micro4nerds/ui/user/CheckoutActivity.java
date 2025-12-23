package ueh.edu.vn.md.micro4nerds.ui.user;

import android.content.Intent;
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
import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import ueh.edu.vn.md.micro4nerds.R;
import ueh.edu.vn.md.micro4nerds.data.local.SharedPrefManager;
import ueh.edu.vn.md.micro4nerds.data.model.CartItem;
import ueh.edu.vn.md.micro4nerds.data.model.Product;
import ueh.edu.vn.md.micro4nerds.data.model.User;
import ueh.edu.vn.md.micro4nerds.ui.adapter.CheckoutAdapter;
import ueh.edu.vn.md.micro4nerds.ui.viewmodel.CartViewModel;
import ueh.edu.vn.md.micro4nerds.ui.viewmodel.OrderViewModel;
import ueh.edu.vn.md.micro4nerds.utils.NetworkUtils;

public class CheckoutActivity extends AppCompatActivity {

    private Button btnSubmitOrder;
    private TextView tvOrderAmount, tvDeliveryFee, tvTotalAmount, tvAutofill;
    private EditText etFullName, etAddress, etPhoneNumber;
    private RadioGroup rgPaymentMethod, rgDeliveryMethod;
    private ProgressBar progressBar;
    private RecyclerView rvCheckoutItems;
    private CheckoutAdapter checkoutAdapter;

    private OrderViewModel orderViewModel;
    private SharedPrefManager sharedPrefManager;

    private double deliveryFee = 25000;
    private List<CartItem> itemsToCheckout;
    private double orderAmountToCheckout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        orderViewModel = new ViewModelProvider(this).get(OrderViewModel.class);
        sharedPrefManager = new SharedPrefManager(this);

        initViews();
        checkIntent();
        setupListeners();
        observeViewModel();

        // TỰ ĐỘNG ĐIỀN THÔNG TIN KHI VỪA MỞ MÀN HÌNH
        autofillUserInfo(false);
    }

    private void checkIntent() {
        boolean isBuyNow = getIntent().getBooleanExtra("is_buy_now", false);
        if (isBuyNow) {
            Product product = (Product) getIntent().getSerializableExtra("product_item");
            if (product != null) {
                itemsToCheckout = Collections.singletonList(new CartItem(product.getId(), product.getName(), product.getPrice(), product.getImageUrl(), 1));
                orderAmountToCheckout = product.getPrice();
                setupRecyclerView();
                displaySummary(orderAmountToCheckout);
            } else {
                handleCheckoutError("Không tìm thấy thông tin sản phẩm.");
            }
        } else {
            CartViewModel cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);
            cartViewModel.getCartItems().observe(this, cartItems -> {
                itemsToCheckout = cartItems;
                setupRecyclerView();
            });
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
        tvAutofill = findViewById(R.id.tvAutofill);
        etFullName = findViewById(R.id.etFullName);
        etAddress = findViewById(R.id.etAddress);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        rgPaymentMethod = findViewById(R.id.rgPaymentMethod);
        rgDeliveryMethod = findViewById(R.id.rgDeliveryMethod);
        progressBar = findViewById(R.id.progressBarCheckout);
        rvCheckoutItems = findViewById(R.id.rvCheckoutItems);
        findViewById(R.id.btnBack).setOnClickListener(v -> onBackPressed());
    }

    private void setupRecyclerView() {
        if (itemsToCheckout != null) {
            checkoutAdapter = new CheckoutAdapter(this, itemsToCheckout);
            rvCheckoutItems.setAdapter(checkoutAdapter);
        }
    }

    private void setupListeners() {
        if (tvAutofill != null) {
            tvAutofill.setOnClickListener(v -> autofillUserInfo(true));
        }

        rgDeliveryMethod.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbExpress) deliveryFee = 100000;
            else if (checkedId == R.id.rbFast) deliveryFee = 50000;
            else deliveryFee = 25000;
            displaySummary(orderAmountToCheckout);
        });

        btnSubmitOrder.setOnClickListener(v -> {
            if (!NetworkUtils.isNetworkAvailable(this)) {
                Toast.makeText(this, "Không có kết nối Internet. Vui lòng kiểm tra lại mạng!", Toast.LENGTH_LONG).show();
                return;
            }

            if (itemsToCheckout == null || itemsToCheckout.isEmpty()) {
                Toast.makeText(this, "Không có sản phẩm để thanh toán!", Toast.LENGTH_SHORT).show();
                return;
            }

            String fullName = etFullName.getText().toString().trim();
            String address = etAddress.getText().toString().trim();
            String phoneNumber = etPhoneNumber.getText().toString().trim(); 

            if (fullName.isEmpty() || address.isEmpty() || phoneNumber.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ họ tên, địa chỉ và số điện thoại", Toast.LENGTH_SHORT).show();
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

            orderViewModel.checkout(itemsToCheckout, orderAmountToCheckout, fullName, phoneNumber, address, shippingMethod);
        });
    }

    private void autofillUserInfo(boolean showToast) {
        User user = sharedPrefManager.getUser();
        if (user != null) {
            boolean hasData = false;
            
            if (user.getName() != null && !user.getName().isEmpty()) {
                etFullName.setText(user.getName());
                hasData = true;
            }
            if (user.getPhone() != null && !user.getPhone().isEmpty()) {
                etPhoneNumber.setText(user.getPhone());
                hasData = true;
            }
            if (user.getAddress() != null && !user.getAddress().isEmpty()) {
                etAddress.setText(user.getAddress());
                hasData = true;
            }

            if (showToast) {
                if (hasData) {
                    Toast.makeText(this, "Đã điền thông tin từ hồ sơ", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Hồ sơ chưa có thông tin đầy đủ. Vui lòng cập nhật trong Profile.", Toast.LENGTH_LONG).show();
                }
            }
        } else if (showToast) {
            Toast.makeText(this, "Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
        }
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
                    new ViewModelProvider(this).get(CartViewModel.class).clearCart();
                    Intent intent = new Intent(this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
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
