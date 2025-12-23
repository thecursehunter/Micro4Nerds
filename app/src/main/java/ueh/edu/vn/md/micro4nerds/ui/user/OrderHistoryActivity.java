package ueh.edu.vn.md.micro4nerds.ui.user;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;

import ueh.edu.vn.md.micro4nerds.data.model.Order;
import ueh.edu.vn.md.micro4nerds.databinding.ActivityOrderHistoryBinding;
import ueh.edu.vn.md.micro4nerds.ui.adapter.OrderAdapter;
import ueh.edu.vn.md.micro4nerds.ui.base.BaseActivity;
import ueh.edu.vn.md.micro4nerds.ui.viewmodel.OrderViewModel;

public class OrderHistoryActivity extends BaseActivity {

    private ActivityOrderHistoryBinding binding;
    private OrderViewModel orderViewModel;
    private OrderAdapter orderAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Khởi tạo ViewModel
        orderViewModel = new ViewModelProvider(this).get(OrderViewModel.class);
        
        // Xử lý nút Back
        binding.btnBack.setOnClickListener(v -> finish());

        // Thiết lập RecyclerView
        setupRecyclerView();

        // Lắng nghe dữ liệu từ ViewModel
        observeViewModel();

        // Tải lịch sử đơn hàng
        loadData();
    }

    private void setupRecyclerView() {
        orderAdapter = new OrderAdapter();
        binding.rvOrderHistory.setLayoutManager(new LinearLayoutManager(this));
        binding.rvOrderHistory.setAdapter(orderAdapter);
    }

    private void observeViewModel() {
        orderViewModel.getOrderList().observe(this, orders -> {
            // Ẩn ProgressBar khi có dữ liệu trả về
            binding.progressBar.setVisibility(View.GONE);

            if (orders == null || orders.isEmpty()) {
                // Hiển thị thông báo nếu không có đơn hàng nào
                binding.tvEmptyHistory.setVisibility(View.VISIBLE);
                binding.rvOrderHistory.setVisibility(View.GONE);
                orderAdapter.setOrderList(new ArrayList<>());
            } else {
                // Hiển thị danh sách đơn hàng
                binding.tvEmptyHistory.setVisibility(View.GONE);
                binding.rvOrderHistory.setVisibility(View.VISIBLE);
                orderAdapter.setOrderList(orders);
            }
        });
    }

    private void loadData() {
        // Hiển thị ProgressBar trước khi tải
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.tvEmptyHistory.setVisibility(View.GONE);
        
        // Gọi hàm tải dữ liệu từ ViewModel
        orderViewModel.loadOrderHistory();
    }
}
