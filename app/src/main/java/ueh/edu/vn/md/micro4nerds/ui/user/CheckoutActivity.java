package ueh.edu.vn.md.micro4nerds.ui.user;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.NumberFormat;
import java.util.Locale;

import ueh.edu.vn.md.micro4nerds.R;

public class CheckoutActivity extends AppCompatActivity {

    public static final String EXTRA_ORDER_AMOUNT = "EXTRA_ORDER_AMOUNT";

    private ImageView btnBack;
    private Button btnSubmitOrder;
    private TextView tvOrderAmount, tvDeliveryFee, tvTotalAmount;
    private EditText etFullName, etAddress;
    private RadioGroup rgPaymentMethod, rgDeliveryMethod;

    private double orderAmount = 0;
    private double deliveryFee = 25000; // Phí giao hàng mặc định

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        orderAmount = getIntent().getDoubleExtra(EXTRA_ORDER_AMOUNT, 0.0);

        btnBack = findViewById(R.id.btnBack);
        btnSubmitOrder = findViewById(R.id.btnSubmitOrder);
        tvOrderAmount = findViewById(R.id.tvOrderAmount);
        tvDeliveryFee = findViewById(R.id.tvDeliveryFee);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        etFullName = findViewById(R.id.etFullName);
        etAddress = findViewById(R.id.etAddress);
        rgPaymentMethod = findViewById(R.id.rgPaymentMethod);
        rgDeliveryMethod = findViewById(R.id.rgDeliveryMethod);

        setupClickListeners();
        displaySummary();
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> onBackPressed());

        rgDeliveryMethod.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbExpress) {
                deliveryFee = 100000;
            } else if (checkedId == R.id.rbFast) {
                deliveryFee = 50000;
            } else {
                deliveryFee = 25000;
            }
            displaySummary();
        });

        btnSubmitOrder.setOnClickListener(v -> {
            String fullName = etFullName.getText().toString().trim();
            String address = etAddress.getText().toString().trim();

            if (fullName.isEmpty() || address.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ họ tên và địa chỉ", Toast.LENGTH_SHORT).show();
                return;
            }

            int selectedPaymentId = rgPaymentMethod.getCheckedRadioButtonId();
            RadioButton selectedPaymentRadioButton = findViewById(selectedPaymentId);
            String paymentMethod = selectedPaymentRadioButton.getText().toString();

            int selectedDeliveryId = rgDeliveryMethod.getCheckedRadioButtonId();
            RadioButton selectedDeliveryRadioButton = findViewById(selectedDeliveryId);
            String deliveryMethod = selectedDeliveryRadioButton.getText().toString();

            String message = "Đã đặt hàng cho " + fullName + " tại " + address + "\nThanh toán: " + paymentMethod + "\nGiao hàng: " + deliveryMethod;
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();

            finish();
        });
    }

    private void displaySummary() {
        double totalAmount = orderAmount + deliveryFee;
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

        tvOrderAmount.setText(currencyFormatter.format(orderAmount));
        tvDeliveryFee.setText(currencyFormatter.format(deliveryFee));
        tvTotalAmount.setText(currencyFormatter.format(totalAmount));
    }
}
