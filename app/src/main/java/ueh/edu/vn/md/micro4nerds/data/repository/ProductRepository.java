package ueh.edu.vn.md.micro4nerds.data.repository;

import android.content.Context;
import android.util.Log;

// Import Model và Utils
import ueh.edu.vn.md.micro4nerds.data.model.Product;
import ueh.edu.vn.md.micro4nerds.utils.SystemUtils;

// Import nguồn dữ liệu Remote (Của Minh)
import ueh.edu.vn.md.micro4nerds.data.remote.ProductRemoteDataSource;


import ueh.edu.vn.md.micro4nerds.data.local.dao.ProductDao;
import ueh.edu.vn.md.micro4nerds.data.local.SQLiteHelper;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProductRepository {

    private Context context;
    private ProductRemoteDataSource remoteDataSource;

    // DAO để truy cập SQLite
    private ProductDao productDao;

    // Executor để chạy lưu ngầm vào SQLite
    private final ExecutorService executor;

    private static final String TAG = "ProductRepoSync";

    public ProductRepository(Context context) {
        this.context = context;
        this.remoteDataSource = new ProductRemoteDataSource();
        // Khởi tạo DAO và executor cho việc sync ngầm
        this.productDao = new ProductDao(context);
        this.executor = Executors.newSingleThreadExecutor();
    }

    // Hàm gọi dữ liệu (ViewModel sẽ gọi hàm này)
    public void getProducts(ProductRemoteDataSource.ProductCallback callback) {
        if (callback == null) {
            Log.e(TAG, "Callback is null, cannot proceed");
            return;
        }

        // 1. Kiểm tra mạng
        boolean isNetworkAvailable = SystemUtils.isNetworkAvailable(context);
        Log.d(TAG, "Network available: " + isNetworkAvailable);

        if (isNetworkAvailable) {
            // --- CÓ MẠNG: LẤY TỪ FIREBASE ---
            Log.d(TAG, "Fetching products from Firebase...");
            remoteDataSource.fetchProducts(new ProductRemoteDataSource.ProductCallback() {
                @Override
                public void onDataLoaded(List<Product> products) {
                    if (products == null || products.isEmpty()) {
                        Log.w(TAG, "Firebase returned empty or null products list, trying local cache");
                        loadFromLocalCache(callback, "Firebase trả về danh sách rỗng");
                        return;
                    }

                    Log.d(TAG, "Successfully fetched " + products.size() + " products from Firebase");
                    // Trả dữ liệu về cho ViewModel/UI ngay (UI vẫn hiển thị dữ liệu mới)
                    callback.onDataLoaded(products);

                    // --- Sync Logic: Lưu vào SQLite ngầm bằng Executor ---
                    saveToLocalCache(products);
                }

                @Override
                public void onError(Exception e) {
                    Log.e(TAG, "Firebase fetch failed: " + e.getMessage(), e);
                    // Nếu fetch từ remote lỗi -> thử lấy dữ liệu cục bộ (fallback)
                    loadFromLocalCache(callback, "Lỗi kết nối Firebase: " + e.getMessage());
                }
            });

        } else {
            // --- MẤT MẠNG: LẤY TỪ SQLITE ---
            Log.d(TAG, "No network connection, loading from local cache...");
            loadFromLocalCache(callback, "Không có kết nối mạng");
        }
    }

    /**
     * Lưu danh sách sản phẩm vào SQLite cache (chạy ngầm)
     */
    private void saveToLocalCache(List<Product> products) {
        if (productDao == null) {
            Log.e(TAG, "ProductDao is null, cannot save to cache");
            return;
        }

        if (products == null || products.isEmpty()) {
            Log.w(TAG, "Products list is null or empty, skipping cache save");
            return;
        }

        Log.d(TAG, "Submitting background task to persist " + products.size() + " products to SQLite");
        executor.submit(() -> {
            try {
                Log.d(TAG, "Background cache save started");
                productDao.insertListProducts(products);
                Log.d(TAG, "Background cache save finished successfully - " + products.size() + " products cached");
            } catch (Exception ex) {
                Log.e(TAG, "Error inserting products into local DB", ex);
            }
        });
    }

    /**
     * Load dữ liệu từ SQLite cache (offline mode)
     * @param callback Callback để trả kết quả
     * @param errorContext Context của lỗi để hiển thị thông báo phù hợp
     */
    private void loadFromLocalCache(ProductRemoteDataSource.ProductCallback callback, String errorContext) {
        try {
            if (productDao == null) {
                Log.e(TAG, "ProductDao is null, cannot load from cache");
                callback.onError(new Exception(errorContext + ". Không thể truy cập cache cục bộ."));
                return;
            }

            List<Product> localList = productDao.getAllProducts();
            if (localList != null && !localList.isEmpty()) {
                Log.d(TAG, "Successfully loaded " + localList.size() + " products from local cache");
                callback.onDataLoaded(localList);
            } else {
                Log.w(TAG, "Local cache is empty or null");
                String errorMessage = errorContext;
                if (SystemUtils.isNetworkAvailable(context)) {
                    errorMessage += ". Chưa có dữ liệu được lưu cục bộ.";
                } else {
                    errorMessage += " và chưa có dữ liệu Offline!";
                }
                callback.onError(new Exception(errorMessage));
            }
        } catch (Exception ex) {
            Log.e(TAG, "Error reading local products from cache", ex);
            callback.onError(new Exception(errorContext + ". Lỗi đọc cache: " + ex.getMessage()));
        }
    }

    /**
     * Kiểm tra xem có dữ liệu trong cache không (dùng để hiển thị UI offline indicator)
     */
    public boolean hasCachedData() {
        try {
            if (productDao != null) {
                List<Product> cached = productDao.getAllProducts();
                return cached != null && !cached.isEmpty();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error checking cache", e);
        }
        return false;
    }

    /**
     * Force reload từ cache (dùng khi muốn refresh mà không cần network)
     */
    public void loadFromCache(ProductRemoteDataSource.ProductCallback callback) {
        loadFromLocalCache(callback, "Đang tải từ cache cục bộ");
    }
}
