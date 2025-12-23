package ueh.edu.vn.md.micro4nerds.data.model;

public class User {
    private String uid;
    private String email;
    private String name;
    private String role; // "admin" hoặc "user"
    private String avatar; // URL ảnh đại diện
    private String phone;   // Số điện thoại
    private String address; // Địa chỉ

    // Constructor rỗng bắt buộc cho Firebase
    public User() { }

    public User(String uid, String email, String name, String role) {
        this.uid = uid;
        this.email = email;
        this.name = name;
        this.role = role;
        this.avatar = null;
        this.phone = "";
        this.address = "";
    }

    // --- Getter & Setter ---
    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
}
