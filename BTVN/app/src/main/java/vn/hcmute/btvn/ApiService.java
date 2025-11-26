package vn.hcmute.btvn;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import vn.hcmute.btvn.model.Category;
import vn.hcmute.btvn.model.Product;

public interface ApiService {

    @GET("/api/categories")
    Call<List<Category>> getCategories();

    @GET("/api/products")
    Call<List<Product>> getAllProducts();

    @GET("/api/categories/{categoryId}/products")
    Call<List<Product>> getProductsByCategory(@Path("categoryId") int categoryId);

    // Thêm @Query để có thể gửi categoryId tùy chọn
    @GET("/api/products/top-sellers")
    Call<List<Product>> getTopSellingProducts(@Query("categoryId") Integer categoryId);

    // Thêm @Query để có thể gửi categoryId tùy chọn
    @GET("/api/products/newest")
    Call<List<Product>> getNewestProducts(@Query("categoryId") Integer categoryId);
}
