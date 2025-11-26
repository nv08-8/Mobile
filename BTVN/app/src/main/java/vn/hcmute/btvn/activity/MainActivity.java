package vn.hcmute.btvn.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.hcmute.btvn.ApiService;
import vn.hcmute.btvn.model.Category;
import vn.hcmute.btvn.R;
import vn.hcmute.btvn.RetrofitClient;
import vn.hcmute.btvn.adapter.ProductAdapter;
import vn.hcmute.btvn.model.Product;

public class MainActivity extends AppCompatActivity {

    private Spinner spinnerCategories, spinnerFilter;
    private RecyclerView recyclerViewProducts;
    private TextView tvProductListTitle;

    private ApiService apiService;
    private ProductAdapter productAdapter;
    private List<Category> categoryList = new ArrayList<>();

    // ID đặc biệt cho mục "Tất cả"
    private static final int ALL_CATEGORIES_ID = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        apiService = RetrofitClient.getApiService();
        
        spinnerCategories = findViewById(R.id.spinner_categories);
        spinnerFilter = findViewById(R.id.spinner_filter);
        recyclerViewProducts = findViewById(R.id.recycler_view_products);
        tvProductListTitle = findViewById(R.id.tv_product_list_title);

        setupRecyclerView();
        fetchCategories();
        setupFilterSpinner();
    }

    private void setupRecyclerView() {
        recyclerViewProducts.setLayoutManager(new LinearLayoutManager(this));
        productAdapter = new ProductAdapter(this, new ArrayList<>());
        recyclerViewProducts.setAdapter(productAdapter);
    }

    private void fetchCategories() {
        apiService.getCategories().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categoryList = response.body();
                    
                    // Tạo danh sách tên để hiển thị, thêm "Tất cả" vào đầu
                    List<String> categoryDisplayNames = new ArrayList<>();
                    categoryDisplayNames.add("Tất cả");
                    categoryDisplayNames.addAll(categoryList.stream().map(Category::getName).collect(Collectors.toList()));

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, categoryDisplayNames);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerCategories.setAdapter(adapter);

                    // Cài đặt listener sau khi đã có dữ liệu
                    setupSpinnersListener();
                } else {
                    showToast("Không thể tải danh mục");
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                showToast("Lỗi mạng: " + t.getMessage());
            }
        });
    }

    private void setupFilterSpinner() {
        List<String> filterOptions = Arrays.asList("Sản phẩm mặc định", "Top 10 sản phẩm có số lượng bán nhiều nhất", "Top 10 mới nhất (được tạo <=7 ngày)");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, filterOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilter.setAdapter(adapter);
    }

    private void setupSpinnersListener() {
        AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                applyCurrentFilter();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        };
        spinnerCategories.setOnItemSelectedListener(listener);
        spinnerFilter.setOnItemSelectedListener(listener);
    }

    private void applyCurrentFilter() {
        int categoryPosition = spinnerCategories.getSelectedItemPosition();
        int filterPosition = spinnerFilter.getSelectedItemPosition();

        // Xác định categoryId để gửi đi. Null nếu là "Tất cả".
        Integer categoryId = null;
        String categoryName = "Tất cả sản phẩm";
        if (categoryPosition > 0) { // > 0 vì vị trí 0 là "Tất cả"
            Category selectedCategory = categoryList.get(categoryPosition - 1);
            categoryId = selectedCategory.getId();
            categoryName = selectedCategory.getName();
        }

        switch (filterPosition) {
            case 0: // Sản phẩm mặc định
                if (categoryId == null) {
                    fetchAllProducts();
                } else {
                    fetchProductsByCategory(categoryId, categoryName);
                }
                break;
            case 1: // Top 10 bán chạy
                fetchTopSellingProducts(categoryId, categoryName);
                break;
            case 2: // Top 10 mới nhất
                fetchNewestProducts(categoryId, categoryName);
                break;
        }
    }
    
    private void fetchAllProducts() {
        tvProductListTitle.setText("Tất cả sản phẩm");
        apiService.getAllProducts().enqueue(createProductCallback());
    }

    private void fetchProductsByCategory(int categoryId, String categoryName) {
        tvProductListTitle.setText("Sản phẩm trong: " + categoryName);
        apiService.getProductsByCategory(categoryId).enqueue(createProductCallback());
    }

    private void fetchTopSellingProducts(Integer categoryId, String categoryName) {
        tvProductListTitle.setText("Top bán chạy: " + categoryName);
        apiService.getTopSellingProducts(categoryId).enqueue(createProductCallback());
    }

    private void fetchNewestProducts(Integer categoryId, String categoryName) {
        tvProductListTitle.setText("Mới nhất: " + categoryName);
        apiService.getNewestProducts(categoryId).enqueue(createProductCallback());
    }

    private Callback<List<Product>> createProductCallback() {
        return new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    productAdapter.updateProducts(response.body());
                } else {
                    productAdapter.updateProducts(new ArrayList<>());
                    showToast("Không có sản phẩm nào");
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                showToast("Lỗi mạng: " + t.getMessage());
            }
        };
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
