package ueh.edu.vn.md.micro4nerds.data.model;

import com.google.firebase.firestore.ServerTimestamp;
import java.util.Date;
import java.util.List;

public class Order {
    private String userId;
    private String customerName;
    private String phoneNumber; // Thêm số điện thoại
    private List<CartItem> info;
    private double totalPrice;
    private String address;
    private String shippingMethod;
    private Date timestamp;

    public Order() {}

    public Order(String userId, String customerName, String phoneNumber, List<CartItem> info, double totalPrice, String address, String shippingMethod) {
        this.userId = userId;
        this.customerName = customerName;
        this.phoneNumber = phoneNumber;
        this.info = info;
        this.totalPrice = totalPrice;
        this.address = address;
        this.shippingMethod = shippingMethod;
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getPhoneNumber() { return phoneNumber; } // Getter mới
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; } // Setter mới

    public List<CartItem> getInfo() { return info; }
    public void setInfo(List<CartItem> info) { this.info = info; }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getShippingMethod() { return shippingMethod; }
    public void setShippingMethod(String shippingMethod) { this.shippingMethod = shippingMethod; }

    @ServerTimestamp
    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
}
