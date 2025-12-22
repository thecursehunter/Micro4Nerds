package ueh.edu.vn.md.micro4nerds.ui.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ueh.edu.vn.md.micro4nerds.R;
import ueh.edu.vn.md.micro4nerds.data.model.CartItem;
import ueh.edu.vn.md.micro4nerds.data.model.Order;
import ueh.edu.vn.md.micro4nerds.utils.FormatUtils;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private List<Order> orderList;

    public OrderAdapter() {
        this.orderList = new ArrayList<>();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setOrderList(List<Order> orderList) {
        this.orderList = orderList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.bind(order);
    }

    @Override
    public int getItemCount() {
        return orderList != null ? orderList.size() : 0;
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvOrderDate;
        private final TextView tvOrderStatus;
        private final ImageView imgProductPreview;
        private final TextView tvProductName;
        private final TextView tvItemCount;
        private final TextView tvOrderTotal;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderDate = itemView.findViewById(R.id.tvOrderDate);
            tvOrderStatus = itemView.findViewById(R.id.tvOrderStatus);
            imgProductPreview = itemView.findViewById(R.id.imgProductPreview);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvItemCount = itemView.findViewById(R.id.tvItemCount);
            tvOrderTotal = itemView.findViewById(R.id.tvOrderTotal);
        }

        @SuppressLint("SetTextI18n")
        public void bind(Order order) {
            // 1. Ngày đặt
            if (order.getTimestamp() != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                tvOrderDate.setText(sdf.format(order.getTimestamp()));
            } else {
                tvOrderDate.setText("N/A");
            }

            // 2. Trạng thái (Tạm thời fix cứng)
            tvOrderStatus.setText("Completed");

            // 3. Thông tin sản phẩm (Lấy sản phẩm đầu tiên làm đại diện)
            if (order.getInfo() != null && !order.getInfo().isEmpty()) {
                CartItem firstItem = order.getInfo().get(0);
                
                // Tên sản phẩm
                tvProductName.setText(firstItem.getProductName());

                // Ảnh sản phẩm
                if (firstItem.getImageUrl() != null && !firstItem.getImageUrl().isEmpty()) {
                    Glide.with(itemView.getContext())
                            .load(firstItem.getImageUrl())
                            .placeholder(R.drawable.ic_cart)
                            .error(R.drawable.ic_cart)
                            .into(imgProductPreview);
                } else {
                    imgProductPreview.setImageResource(R.drawable.ic_cart);
                }

                // Số lượng các món còn lại
                int otherItemsCount = order.getInfo().size() - 1;
                if (otherItemsCount > 0) {
                    tvItemCount.setText("and " + otherItemsCount + " other items");
                    tvItemCount.setVisibility(View.VISIBLE);
                } else {
                    tvItemCount.setVisibility(View.GONE);
                }
            } else {
                tvProductName.setText("Unknown Product");
                imgProductPreview.setImageResource(R.drawable.ic_cart);
                tvItemCount.setVisibility(View.GONE);
            }

            // 4. Tổng tiền - Sử dụng FormatUtils
            tvOrderTotal.setText(FormatUtils.formatCurrency(order.getTotalPrice()));
        }
    }
}
