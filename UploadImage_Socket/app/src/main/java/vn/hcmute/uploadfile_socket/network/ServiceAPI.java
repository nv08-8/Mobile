package vn.hcmute.uploadfile_socket.network;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.GET;
import retrofit2.http.Query;
import vn.hcmute.uploadfile_socket.cons.Const;
import vn.hcmute.uploadfile_socket.model.ImageUpload;

public interface ServiceAPI {
    @Multipart
    // Upload to local Node server (new friendly endpoint)
    @POST("api/upload")
    Call<List<ImageUpload>> upload(@Part(Const.MY_USERNAME) RequestBody username,
                                   @Part MultipartBody.Part avatar);

    @Multipart
    @POST("api/upload")
    Call<ResponseBody> upload1(@Part(Const.MY_USERNAME) RequestBody username,
                                @Part MultipartBody.Part avatar);

    // Fetch profile info by id using multipart/form-data (matches Postman form-data)
    // The server provides a GET /fetch route which returns profile information by id
    @GET("fetch")
    Call<ResponseBody> getProfile(@Query("id") String id);

    // Upload directly to remote PHP endpoint (absolute URL overrides base URL)
    // Keep remote PHP endpoint for reference (deprecated). Use uploadRemote only if needed.
    @Multipart
    @POST("http://app.iotstar.vn:8081/appfoods/updateimages.php")
    Call<ResponseBody> uploadRemote(@Part("id") RequestBody id,
                                    @Part MultipartBody.Part avatar);

    // Upload to local Node server which will upsert to MongoDB
    @Multipart
    @POST("api/upload")
    Call<ResponseBody> uploadLocal(@Part("id") RequestBody id,
                                   @Part MultipartBody.Part avatar);

}
