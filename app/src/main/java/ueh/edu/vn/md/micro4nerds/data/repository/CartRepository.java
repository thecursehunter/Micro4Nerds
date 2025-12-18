package ueh.edu.vn.md.micro4nerds.data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import ueh.edu.vn.md.micro4nerds.data.local.dao.CartDao;
import ueh.edu.vn.md.micro4nerds.data.model.CartItem;
import ueh.edu.vn.md.micro4nerds.data.model.Product;

public class CartRepository {

    private static CartRepository instance;
    private final CartDao cartDao;
    private final MutableLiveData<List<CartItem>> cartItemsLiveData = new MutableLiveData<>();

    private CartRepository(Context context) {
        this.cartDao = new CartDao(context);
        loadCartItems();
    }

    public static synchronized CartRepository getInstance(Context context) {
        if (instance == null) {
            instance = new CartRepository(context.getApplicationContext());
        }
        return instance;
    }

    public LiveData<List<CartItem>> getCartItems() {
        return cartItemsLiveData;
    }

    public void loadCartItems() {
        cartItemsLiveData.postValue(cartDao.getCartItems());
    }

    public void addToCart(Product product) {
        CartItem item = new CartItem(product.getId(), product.getName(), product.getPrice(), product.getImageUrl(), 1);
        cartDao.addToCart(item);
        loadCartItems(); // Reload and notify observers
    }

    public void removeFromCart(String productId) {
        cartDao.removeFromCart(productId);
        loadCartItems(); // Reload and notify observers
    }

    public void updateQuantity(String productId, int newQuantity) {
        cartDao.updateQuantity(productId, newQuantity);
        loadCartItems(); // Reload and notify observers
    }

    public void clearCart() {
        cartDao.clearCart();
        loadCartItems(); // Reload and notify observers
    }
}
