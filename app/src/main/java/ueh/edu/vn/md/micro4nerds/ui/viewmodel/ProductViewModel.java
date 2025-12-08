package ueh.edu.vn.md.micro4nerds.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import ueh.edu.vn.md.micro4nerds.data.model.Product;
import ueh.edu.vn.md.micro4nerds.data.remote.ProductRemoteDataSource; // Import file của A
import ueh.edu.vn.md.micro4nerds.data.repository.ProductRepository;

import java.util.List;
public class ProductViewModel extends AndroidViewModel {

    private ProductRepository repository;

    private MutableLiveData<List<Product>> productList = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public ProductViewModel(@NonNull Application application) {
        super(application);
        repository = new ProductRepository(application);
        loadProducts();
    }

    public void loadProducts() {
        isLoading.setValue(true);

        // Gọi Repository với Interface của A
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
}
