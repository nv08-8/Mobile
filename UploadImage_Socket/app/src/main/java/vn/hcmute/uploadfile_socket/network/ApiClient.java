package vn.hcmute.uploadfile_socket.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    // Base URL for the user's API
    public static final String BASE_URL = "http://app.iotstar.vn:8081/appfoods/";
    public static ServiceAPI serviceapi;

    static {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        serviceapi = retrofit.create(ServiceAPI.class);
    }
}
