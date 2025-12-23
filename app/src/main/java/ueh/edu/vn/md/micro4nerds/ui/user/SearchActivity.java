package ueh.edu.vn.md.micro4nerds.ui.user;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ueh.edu.vn.md.micro4nerds.R;
import ueh.edu.vn.md.micro4nerds.data.model.Product;
import ueh.edu.vn.md.micro4nerds.ui.adapter.ProductAdapter;
import ueh.edu.vn.md.micro4nerds.ui.base.BaseActivity;
import ueh.edu.vn.md.micro4nerds.ui.viewmodel.ProductViewModel;

public class SearchActivity extends BaseActivity {

    private EditText edtSearch;
    private ImageView btnClose, btnClear;
    private RecyclerView rvSearchResults;

    private ProductAdapter productAdapter;
    private ProductViewModel productViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        initViews();
        setupRecyclerView();
        setupViewModel();
        setupListeners();

        // Mở bàn phím ngay khi vào trang (Tùy chọn)
        edtSearch.requestFocus();
    }

    private void initViews() {
        edtSearch = findViewById(R.id.edtSearch);
        btnClose = findViewById(R.id.btnClose);
        btnClear = findViewById(R.id.btnClear);
        rvSearchResults = findViewById(R.id.rvSearchResults);
    }

    private void setupRecyclerView() {
        // 1. Kiểm tra chiều xoay màn hình
        int spanCount = 2; // Đt dọc là 2 cột

        int orientation = getResources().getConfiguration().orientation;
        if (orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE) {
            spanCount = 3; // Nếu Đt ngang thì thành 3 cột
        }
        // 2. Thiết lập Layout Manager với số cột động
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, spanCount);
        rvSearchResults.setLayoutManager(gridLayoutManager);

        // Tái sử dụng ProductAdapter của trang Home
        productAdapter = new ProductAdapter(this, new ProductAdapter.ProductClickListener() {
            @Override
            public void onProductClick(Product product) {
                // Click vào ảnh -> Xem chi tiết
                Intent intent = new Intent(SearchActivity.this, ProductDetailActivity.class);
                intent.putExtra("product_item", product);
                startActivity(intent);
            }

            @Override
            public void onBuyNowClick(Product product) {
                // Click mua ngay -> Checkout
                Intent intent = new Intent(SearchActivity.this, CheckoutActivity.class);
                intent.putExtra("product_item", product);
                intent.putExtra("is_buy_now", true);
                startActivity(intent);
            }
        });

        // Vuốt danh sách -> Ẩn bàn phím
        rvSearchResults.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                // Khi người dùng bắt đầu kéo (DRAGGING) thì ẩn bàn phím
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    android.view.inputmethod.InputMethodManager imm =
                            (android.view.inputmethod.InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

                    if (getCurrentFocus() != null) {
                        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    }
                    // Mất focus khỏi ô nhập liệu để con trỏ không nhấp nháy nữa
                    edtSearch.clearFocus();
                }
            }
        });

        rvSearchResults.setAdapter(productAdapter);
    }

    private void setupViewModel() {
        productViewModel = new ViewModelProvider(this).get(ProductViewModel.class);

        // Quan trọng: Chúng ta cần đảm bảo ViewModel đã tải danh sách sản phẩm về rồi
        // Nếu ở Home đã tải, thì ở đây nó sẽ dùng chung cache (nếu dùng ViewModel Share)
        // Nhưng an toàn nhất là gọi load lại (nó sẽ check cache Repository)
        productViewModel.getProductList().observe(this, products -> {
            // Khi mới vào, chưa search gì -> Để list rỗng
        });
    }

    private void setupListeners() {
        // 1. Nút Đóng (X to) -> Thoát trang Search
        btnClose.setOnClickListener(v -> finish());

        // 2. Nút Xóa (x nhỏ) -> Xóa trắng ô nhập
        btnClear.setOnClickListener(v -> edtSearch.setText(""));

        // 3. Sự kiện gõ phím (Real-time Search)
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String keyword = s.toString();

                // Ẩn hiện nút Clear
                if (keyword.isEmpty()) {
                    btnClear.setVisibility(View.GONE);
                } else {
                    btnClear.setVisibility(View.VISIBLE);
                }

                // GỌI HÀM LỌC TỪ VIEWMODEL
                if (productViewModel != null) {
                    List<Product> filteredList = productViewModel.filterProducts(keyword);
                    productAdapter.setProductList(filteredList);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
}
