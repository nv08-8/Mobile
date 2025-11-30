package vn.hcmute.viewflipper_cricleindicator.remote;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import vn.hcmute.viewflipper_cricleindicator.models.MessageModel;

public interface ApiService {
    @FormUrlEncoded
    @POST("newimagesmanager.php")
    Call<MessageModel> loadImageSlider(@Field("position") int position);
}
