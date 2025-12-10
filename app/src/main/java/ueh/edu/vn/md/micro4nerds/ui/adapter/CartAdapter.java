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

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import ueh.edu.vn.md.micro4nerds.R;
import ueh.edu.vn.md.micro4nerds.data.model.CartItem;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private final Context context;
    private final List<CartItem> cartItems;
    private final CartItemListener listener;

    public interface CartItemListener {
        void onQuantityChange(int position, int newQuantity);
        void onItemRemove(int position);
    }

    public CartAdapter(Context context, List<CartItem> cartItems, CartItemListener listener) {
        this.context = context;
        this.cartItems = cartItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem cartItem = cartItems.get(position);

        holder.tvProductName.setText(cartItem.getProductName());

        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        holder.tvProductPrice.setText(currencyFormatter.format(cartItem.getProductPrice()));
        holder.tvQuantity.setText(String.format(Locale.US, "%02d", cartItem.getQuantity()));

        Glide.with(context)
                .load(cartItem.getImageUrl())
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.imgProduct);

        if (cartItem.getQuantity() == 1) {
            holder.btnDecrease.setAlpha(0.5f);
            holder.btnDecrease.setEnabled(false);
        } else {
            holder.btnDecrease.setAlpha(1.0f);
            holder.btnDecrease.setEnabled(true);
        }

        holder.btnIncrease.setOnClickListener(v -> {
            listener.onQuantityChange(holder.getAdapterPosition(), cartItem.getQuantity() + 1);
        });

        holder.btnDecrease.setOnClickListener(v -> {
            listener.onQuantityChange(holder.getAdapterPosition(), cartItem.getQuantity() - 1);
        });

        holder.btnDelete.setOnClickListener(v -> {
            listener.onItemRemove(holder.getAdapterPosition());
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct, btnDelete, btnIncrease, btnDecrease;
        TextView tvProductName, tvProductPrice, tvQuantity;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            btnDecrease = itemView.findViewById(R.id.btnDecrease);
            btnIncrease = itemView.findViewById(R.id.btnIncrease);
        }
    }
}
