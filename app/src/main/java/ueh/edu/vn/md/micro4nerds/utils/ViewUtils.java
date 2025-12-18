
package ueh.edu.vn.md.micro4nerds.utils;

import android.view.View;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import java.util.List;

import ueh.edu.vn.md.micro4nerds.data.model.CartItem;

public class ViewUtils {
    public static void updateCartBadge(CardView cvBadge, TextView tvCartCount, List<CartItem> cartItems) {
        if (cvBadge == null || tvCartCount == null) {
            return;
        }
        
        int totalItems = 0;
        if (cartItems != null && !cartItems.isEmpty()) {
            for (CartItem item : cartItems) {
                if (item != null && item.getQuantity() > 0) {
                    totalItems += item.getQuantity();
                }
            }
        }

        if (totalItems > 0) {
            cvBadge.setVisibility(View.VISIBLE);
            tvCartCount.setText(String.valueOf(totalItems));
        } else {
            cvBadge.setVisibility(View.GONE);
            tvCartCount.setText("0");
        }
    }
}
