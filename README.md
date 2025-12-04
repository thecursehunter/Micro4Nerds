# Micro4Nerds
An android app dedicated to selling Micro Four Thirds Cameras, Lenses &amp; Accesories

# Folder Structure
```text
com.ueh.edu.vn.md.micro4nerds
└── data
    ├── local                     # Xử lý dữ liệu Offline (SQLite)
    │   ├── SQLiteHelper.java      # CRUD Cache & Cart
    │   ├── SharedPrefManager.java # Lưu session, token
    │   └── dao/                   # (Tùy chọn) DAO nếu dùng Room
    │
    ├── remote                    # Firebase / API
    │   ├── FirebaseAuthDataSource.java
    │   ├── ProductRemoteDataSource.java
    │   └── OrderRemoteDataSource.java
    │
    ├── repository                # Điều phối Local & Remote
    │   ├── AuthRepository.java
    │   ├── ProductRepository.java
    │   ├── CartRepository.java
    │   └── OrderRepository.java
    │
    └── model                     # POJO Models
        ├── User.java
        ├── Product.java
        ├── CartItem.java
        └── Order.java

└── ui
    ├── viewmodel
    │   ├── AuthViewModel.java
    │   ├── ProductViewModel.java
    │   ├── CartViewModel.java
    │   └── OrderViewModel.java
    │
    ├── adapter
    │   ├── ProductAdapter.java
    │   ├── CartAdapter.java
    │   └── OrderAdapter.java
    │
    ├── auth
    │   ├── LoginActivity.java
    │   └── RegisterActivity.java
    │
    └── user                     # Các màn hình chính
        ├── HomeActivity.java
        ├── ProductDetailActivity.java
        ├── CartActivity.java
        ├── CheckoutActivity.java
        ├── OrderHistoryActivity.java
        └── ProfileActivity.java

└── utils
    ├── Constants.java
    ├── FormatUtils.java
    └── SystemUtils.java

MyApplication.java
```
