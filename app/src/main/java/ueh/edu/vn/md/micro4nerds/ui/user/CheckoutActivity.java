package ueh.edu.vn.md.micro4nerds.ui.user;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

import ueh.edu.vn.md.micro4nerds.R;

public class CheckoutActivity extends AppCompatActivity {

    public static final String EXTRA_ORDER_AMOUNT = "EXTRA_ORDER_AMOUNT";

    private ImageView btnBack;
    private Button btnSubmitOrder;
    private TextView tvOrderAmount, tvDeliveryFee, tvTotalAmount;

    private double orderAmount = 0;
    private double deliveryFee = 5.00; // Phí giao hàng cố định

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        // Lấy tổng tiền từ Intent
        orderAmount = getIntent().getDoubleExtra(EXTRA_ORDER_AMOUNT, 0.0);

        // Ánh xạ các view
        btnBack = findViewById(R.id.btnBack);
        btnSubmitOrder = findViewById(R.id.btnSubmitOrder);
        tvOrderAmount = findViewById(R.id.tvOrderAmount);
        tvDeliveryFee = findViewById(R.id.tvDeliveryFee);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);

        setupClickListeners();
        displaySummary();
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> onBackPressed());

        btnSubmitOrder.setOnClickListener(v -> {
            Toast.makeText(this, "Order submitted!", Toast.LENGTH_SHORT).show();
            // TODO: Xóa giỏ hàng và chuyển về màn hình chính
            finish();
        });
    }

    private void displaySummary() {
        double totalAmount = orderAmount + deliveryFee;

        tvOrderAmount.setText(String.format(Locale.US, "$ %.2f", orderAmount));
        tvDeliveryFee.setText(String.format(Locale.US, "$ %.2f", deliveryFee));
        tvTotalAmount.setText(String.format(Locale.US, "$ %.2f", totalAmount));
    }
}
