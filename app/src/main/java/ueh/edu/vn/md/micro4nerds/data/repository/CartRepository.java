package ueh.edu.vn.md.micro4nerds.data.repository;

import android.content.Context;

import java.util.List;

import ueh.edu.vn.md.micro4nerds.data.local.dao.CartDao;
import ueh.edu.vn.md.micro4nerds.data.model.CartItem;
import ueh.edu.vn.md.micro4nerds.data.model.Product;

public class CartRepository {

    private final CartDao cartDao;

    public CartRepository(Context context) {
        this.cartDao = new CartDao(context);
    }

    public List<CartItem> getCartItems() {
        return cartDao.getCartItems();
    }

    public void addToCart(Product product) {
        CartItem item = new CartItem(product.getId(), product.getName(), product.getPrice(), product.getImageUrl(), 1);
        cartDao.addToCart(item);
    }
}
