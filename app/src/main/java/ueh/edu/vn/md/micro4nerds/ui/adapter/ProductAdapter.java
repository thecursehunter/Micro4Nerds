package ueh.edu.vn.md.micro4nerds.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import ueh.edu.vn.md.micro4nerds.R;
import ueh.edu.vn.md.micro4nerds.data.local.SharedPrefManager;
import ueh.edu.vn.md.micro4nerds.data.model.Product;
import ueh.edu.vn.md.micro4nerds.ui.auth.LoginActivity;

import java.util.ArrayList;
import java.util.List;
public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private Context context;
    private List<Product> productList;
    private ProductClickListener listener;
    private SharedPrefManager sharedPrefManager;

    // Interface để gửi tín hiệu Click về cho HomeActivity xử lý
    // Chúng ta tách rõ 2 hành động khác nhau
    public interface ProductClickListener {
        void onProductClick(Product product); // Click vào ảnh/khung -> Xem Detail
        void onBuyNowClick(Product product);  // Click chữ "Mua Ngay" -> Đi Checkout
    }

    // Constructor
    public ProductAdapter(Context context, ProductClickListener listener) {
        this.context = context;
        this.listener = listener;
        this.productList = new ArrayList<>();
        this.sharedPrefManager = new SharedPrefManager(context);
    }

    // Hàm cập nhật dữ liệu mới (gọi từ HomeActivity)
    public void setProductList(List<Product> products) {
        this.productList = products;
        notifyDataSetChanged(); // Báo cho Adapter biết dữ liệu đã đổi để vẽ lại
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Gọi file layout item_product.xml cậu vừa tạo
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        if (product == null) return;

        // 1. Gán tên sản phẩm
        holder.tvName.setText(product.getName());

        // 2. Load ảnh bằng Glide (Thư viện chuẩn của Google khuyên dùng)
        // Nếu link ảnh lỗi hoặc chưa load xong -> hiện ảnh placeholder màu xám
        Glide.with(context)
                .load(product.getImageUrl())
                .placeholder(android.R.color.darker_gray)
                .error(android.R.color.holo_red_light)
                .centerCrop()
                .into(holder.imgProduct);

        // 3. Xử lý sự kiện CLICK (check stock))

        // A. Click vào chữ "MUA NGAY"
        if (product.getStock() <= 0) {
            // --- TRƯỜNG HỢP HẾT HÀNG ---
            holder.tvBuyNow.setText("HẾT HÀNG");
            holder.tvBuyNow.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
            holder.tvBuyNow.setOnClickListener(null);
            // làm mờ
            holder.tvBuyNow.setAlpha(0.5f);
        }
        else {
            // --- TRƯỜNG HỢP CÒN HÀNG ---
            holder.tvBuyNow.setText("MUA NGAY");

            // Trả về màu đen mặc định
            holder.tvBuyNow.setTextColor(context.getResources().getColor(android.R.color.black));
            holder.tvBuyNow.setAlpha(1.0f);

            // check đăng nhập
            holder.tvBuyNow.setOnClickListener(v -> {
                if (!sharedPrefManager.isLoggedIn()) {
                    Toast.makeText(context, "Vui lòng đăng nhập để mua hàng", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                    return;
                }

                if (listener != null) {
                    listener.onBuyNowClick(product);
                }
            });
        }

        // B. Click vào toàn bộ ô sản phẩm (trừ nút Mua Ngay ra)
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onProductClick(product);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (productList != null) {
            return productList.size();
        }
        return 0;
    }

    // Class nắm giữ các View trong layout
    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView tvName, tvBuyNow;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            // Ánh xạ đúng ID trong file item_product.xml
            imgProduct = itemView.findViewById(R.id.imgProduct);
            tvName = itemView.findViewById(R.id.tvProductName);
            tvBuyNow = itemView.findViewById(R.id.tvBuyNow);
            // imgHeart mình không ánh xạ vì cậu bảo chỉ để trang trí, chưa cần click
        }
    }
}
