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

        // 1. Kiểm tra mạng
        if (SystemUtils.isNetworkAvailable(context)) {
            // --- CÓ MẠNG: LẤY TỪ FIREBASE ---
            remoteDataSource.fetchProducts(new ProductRemoteDataSource.ProductCallback() {
                @Override
                public void onDataLoaded(List<Product> products) {
                    // Trả dữ liệu về cho ViewModel/UI ngay (UI vẫn hiển thị dữ liệu mới)
                    callback.onDataLoaded(products);

                    // --- Sync Logic (part 1): Lưu vào SQLite ngầm bằng Executor ---
                    if (productDao != null && products != null) {
                        Log.d(TAG, "Submitting background task to persist " + products.size() + " products");
                        executor.submit(() -> {
                            try {
                                Log.d(TAG, "Background insert started");
                                productDao.insertListProducts(products);
                                Log.d(TAG, "Background insert finished successfully");
                            } catch (Exception ex) {
                                Log.e(TAG, "Error inserting products into local DB", ex);
                            }
                            // TODO: Optional: update last-sync timestamp here
                        });
                    } else {
                        Log.d(TAG, "ProductDao is null or products is null — skipping background persist");
                    }
                }

                @Override
                public void onError(Exception e) {
                    // Nếu fetch từ remote lỗi -> thử lấy dữ liệu cục bộ
                    try {
                        if (productDao != null) {
                            List<Product> localList = productDao.getAllProducts();
                            if (localList != null && !localList.isEmpty()) {
                                Log.d(TAG, "Remote fetch failed — returning local cache of " + localList.size() + " products");
                                callback.onDataLoaded(localList);
                                return;
                            }
                        }
                    } catch (Exception ex) {
                        Log.e(TAG, "Error reading local products during fallback", ex);
                    }
                    // Nếu không có dữ liệu local thì trả lỗi về UI
                    callback.onError(e);
                }
            });

        } else {
            // --- MẤT MẠNG: LẤY TỪ SQLITE ---
            try {
                if (productDao != null) {
                    List<Product> localList = productDao.getAllProducts();
                    if (localList != null && !localList.isEmpty()) {
                        Log.d(TAG, "No network — returning local cache of " + localList.size() + " products");
                        callback.onDataLoaded(localList);
                        return;
                    }
                }
            } catch (Exception ex) {
                Log.e(TAG, "Error reading local products when offline", ex);
            }

            // Không có mạng và không có dữ liệu offline
            callback.onError(new Exception("Không có kết nối mạng và chưa có dữ liệu Offline!"));
        }
    }
}
