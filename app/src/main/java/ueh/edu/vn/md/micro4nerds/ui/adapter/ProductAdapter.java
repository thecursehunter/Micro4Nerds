package ueh.edu.vn.md.micro4nerds.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import ueh.edu.vn.md.micro4nerds.R;
import ueh.edu.vn.md.micro4nerds.data.model.Product;

import java.util.ArrayList;
import java.util.List;
public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private Context context;
    private List<Product> productList;
    private ProductClickListener listener;

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

        // 3. Xử lý sự kiện CLICK (Logic tách biệt theo yêu cầu của cậu)

        // A. Click vào chữ "MUA NGAY"
        holder.tvBuyNow.setOnClickListener(v -> {
            if (listener != null) {
                listener.onBuyNowClick(product);
            }
        });

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
