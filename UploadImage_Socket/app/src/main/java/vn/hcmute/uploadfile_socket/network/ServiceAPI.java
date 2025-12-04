package vn.hcmute.uploadfile_socket.network;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import vn.hcmute.uploadfile_socket.cons.Const;
import vn.hcmute.uploadfile_socket.model.ImageUpload;

public interface ServiceAPI {
    @Multipart
    @POST("updateimages.php")
    Call<List<ImageUpload>> upload(@Part(Const.MY_USERNAME) RequestBody username,
                                   @Part MultipartBody.Part avatar);

    @Multipart
    @POST("updateimages.php")
    Call<ResponseBody> upload1(@Part(Const.MY_USERNAME) RequestBody username,
                                @Part MultipartBody.Part avatar);
}
