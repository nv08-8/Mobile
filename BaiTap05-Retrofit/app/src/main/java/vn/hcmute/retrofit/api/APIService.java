package vn.hcmute.retrofit.api;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import vn.hcmute.retrofit.model.Category;

public interface APIService {
    @GET("categories.php")
    Call<List<Category>> getCategoryAll();
}
