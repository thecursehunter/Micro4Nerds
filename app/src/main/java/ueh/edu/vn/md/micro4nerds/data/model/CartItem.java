package ueh.edu.vn.md.micro4nerds.data.model;

import com.google.firebase.firestore.Exclude;

public class CartItem {
    private String productId;
    private String productName;
    private double productPrice;
    private String productUserId; // Link đến User nào
    private String imageUrl;
    private int quantity;

    public CartItem() { }

    public CartItem(String productId, String productName, double productPrice, String imageUrl, int quantity) {
        this.productId = productId;
        this.productName = productName;
        this.productPrice = productPrice;
        this.imageUrl = imageUrl;
        this.quantity = quantity;
    }

    // Getter & Setter...
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public double getProductPrice() { return productPrice; }
    public void setProductPrice(double productPrice) { this.productPrice = productPrice; }
    
    @Exclude // Báo cho Firestore bỏ qua trường này khi lưu
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
