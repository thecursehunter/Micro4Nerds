package ueh.edu.vn.md.micro4nerds.data.repository;

import android.content.Context;

// Import Model và Utils
import ueh.edu.vn.md.micro4nerds.data.model.Product;
import ueh.edu.vn.md.micro4nerds.utils.SystemUtils;

// Import nguồn dữ liệu Remote (Của Minh)
import ueh.edu.vn.md.micro4nerds.data.remote.ProductRemoteDataSource;


import ueh.edu.vn.md.micro4nerds.data.local.dao.ProductDao;
import ueh.edu.vn.md.micro4nerds.data.local.SQLiteHelper;

import java.util.List;

public class ProductRepository {

    private Context context;
    private ProductRemoteDataSource remoteDataSource;

    // Biến DAO (Nếu chưa có file ProductDao thì comment dòng này lại)
    // private ProductDao productDao;

    public ProductRepository(Context context) {
        this.context = context;
        this.remoteDataSource = new ProductRemoteDataSource();

        // --- GIAI ĐOẠN 2: KHỞI TẠO SQLITE ---
        // (Khi nào bạn A xong SQLiteHelper thì mở comment đoạn này ra)
        /*
        SQLiteHelper dbHelper = new SQLiteHelper(context);
        this.productDao = new ProductDao(dbHelper);
        */
    }

    // Hàm gọi dữ liệu (ViewModel sẽ gọi hàm này)
    public void getProducts(ProductRemoteDataSource.ProductCallback callback) {

        // 1. Kiểm tra mạng
        if (SystemUtils.isNetworkAvailable(context)) {
            // --- CÓ MẠNG: LẤY TỪ FIREBASE ---
            remoteDataSource.fetchProducts(new ProductRemoteDataSource.ProductCallback() {
                @Override
                public void onDataLoaded(List<Product> products) {
                    // Logic Sync: Có dữ liệu mới -> Lưu vào SQLite để dùng dần
                    // (Mở comment khi có ProductDao)
                    /*
                    if (productDao != null) {
                        productDao.insertList(products);
                    }
                    */

                    // Trả dữ liệu về cho ViewModel hiển thị
                    callback.onDataLoaded(products);
                }

                @Override
                public void onError(Exception e) {
                    callback.onError(e);
                }
            });

        } else {
            // --- MẤT MẠNG: LẤY TỪ SQLITE ---
            /*
            if (productDao != null) {
                List<Product> localList = productDao.getAllProducts();
                if (!localList.isEmpty()) {
                    callback.onDataLoaded(localList);
                    return;
                }
            }
            */

            // Giai đoạn 1: Báo lỗi luôn
            callback.onError(new Exception("Không có kết nối mạng và chưa có dữ liệu Offline!"));
        }
    }
}
