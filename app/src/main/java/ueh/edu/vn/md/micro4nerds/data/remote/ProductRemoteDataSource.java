package ueh.edu.vn.md.micro4nerds.data.remote;

import ueh.edu.vn.md.micro4nerds.data.model.Product;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class ProductRemoteDataSource {
    private final FirebaseFirestore db;

    public ProductRemoteDataSource() {
        db = FirebaseFirestore.getInstance();
    }

    // Interface callback để trả dữ liệu về Repository (Vì Firebase chạy bất đồng bộ)
    public interface ProductCallback {
        void onDataLoaded(List<Product> products);
        void onError(Exception e);
    }

    public void fetchProducts(ProductCallback callback) {
        db.collection("products")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Product> list = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        // Firebase tự map JSON sang Object Product
                        Product p = doc.toObject(Product.class);
                        // Đảm bảo ID được gán đúng (đề phòng quên nhập id trong field)
                        p.setId(doc.getId());
                        list.add(p);
                    }
                    callback.onDataLoaded(list);
                })
                .addOnFailureListener(e -> {
                    callback.onError(e);
                });
    }
}
