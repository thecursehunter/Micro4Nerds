package ueh.edu.vn.md.micro4nerds.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import ueh.edu.vn.md.micro4nerds.data.model.Product;
import ueh.edu.vn.md.micro4nerds.data.remote.ProductRemoteDataSource; // Import file của A
import ueh.edu.vn.md.micro4nerds.data.repository.ProductRepository;

import java.util.ArrayList;
import java.util.List;
public class ProductViewModel extends AndroidViewModel {

    private ProductRepository repository;

    private MutableLiveData<List<Product>> productList = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();

    // --- CHỨC NĂNG LẤY DỮ LIỆU ---
    public ProductViewModel(@NonNull Application application) {
        super(application);
        repository = new ProductRepository(application);
        loadProducts();
    }

    public void loadProducts() {
        isLoading.setValue(true);

        // Gọi Repository với Interface của Minh
        repository.getProducts(new ProductRemoteDataSource.ProductCallback() {
            @Override
            public void onDataLoaded(List<Product> products) {
                isLoading.setValue(false);
                productList.setValue(products);
            }

            @Override
            public void onError(Exception e) {
                isLoading.setValue(false);
                // Lấy message từ Exception để hiện thông báo
                errorMessage.setValue(e.getMessage());
            }
        });
    }

    public LiveData<List<Product>> getProductList() { return productList; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getErrorMessage() { return errorMessage; }

    // --- CHỨC NĂNG TÌM KIẾM ---

    // Hàm nhận vào từ khóa, trả về danh sách đã lọc
    public List<Product> filterProducts(String keyword) {
        // Lấy danh sách gốc hiện có
        List<Product> originalList = productList.getValue();
        List<Product> filteredList = new ArrayList<>();

        if (originalList == null || originalList.isEmpty()) {
            return filteredList; // Trả về rỗng nếu chưa có dữ liệu gốc
        }

        // Nếu từ khóa rỗng -> Trả về rỗng (để màn hình search sạch sẽ)
        if (keyword == null || keyword.trim().isEmpty()) {
            return filteredList;
        }

        // Chuẩn hóa từ khóa về chữ thường để so sánh
        String lowerCaseKeyword = keyword.toLowerCase().trim();

        for (Product product : originalList) {
            if (product.getName() != null) {
                // Kiểm tra tên sản phẩm có chứa từ khóa không
                if (product.getName().toLowerCase().contains(lowerCaseKeyword)) {
                    filteredList.add(product);
                }
            }
        }

        return filteredList;
    }
}
