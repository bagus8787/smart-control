package com.example.smart_control.network;

import com.example.smart_control.model.StatusPakan;
import com.example.smart_control.network.response.ArtikelResponse;
import com.example.smart_control.network.response.BaseResponse;
import com.example.smart_control.network.response.LogOutResponse;
import com.example.smart_control.network.response.RegisterResponse;
import com.example.smart_control.network.response.ResponseLogin;
import com.example.smart_control.network.response.UserResponse;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {

    String BASE_URL = "http://192.168.4.1";

    @Headers({"Accept: application/json"})

//    BASERESPONSE
    @GET("login")
    Call<BaseResponse> baseResponse();

    //Login
    @FormUrlEncoded
    @POST("login")
    Call<ResponseLogin> postLogin(@Field("email") String email,
                                  @Field("password") String password);

    //Register
    @FormUrlEncoded
    @POST("register")
    Call<RegisterResponse> postRegister(
            @Field("nik") String nik,
            @Field("fullname") String fullname,
            @Field("email") String email,
            @Field("password") String password,
            @Field("provinsi_id") Integer provinsi_id,
            @Field("kota_id") Integer kota_id,
            @Field("kec_id") Integer kec_id,
            @Field("desa_id") long desa_id,
            @Field("asal_provinsi_id") Integer asal_provinsi_id,
            @Field("asal_kota_id") Integer asal_kota_id,
            @Field("asal_kec_id") Integer asal_kec_id,
            @Field("asal_desa_id") long asal_desa_id
    );

    @Multipart
    @POST("account/update-foto")
    Call<BaseResponse> updateProfil(@Part MultipartBody.Part file);

    //user
    @GET("account/profile/{appDesaCode}/{nik}")
    Call<UserResponse> getUser(@Header("App-Token") String app_token,
                               @Header("ProDesa-Token") String prodesa_token,
                               @Path("appDesaCode") String appdesa_code,
                               @Path("nik") String nik
    );

    //    Log Out
    @GET("logout/{id}")
    Call<LogOutResponse> log_out(@Path("id") int id);

    //    Get Artikel
    @GET("{artikel}/{appDesaCode}/all")
    Call<ArtikelResponse> getArtikel(@Header("app-token") String app_token,
                                     @Header("prodesa-token") String prodesa_token,
                                     @Path("artikel") String artikel,
                                     @Path("appDesaCode") String appdesa_code
    );




//    Local
    //Register
    @FormUrlEncoded
    @POST("register")
    Call<String> register(
            @Field("device_id") String device_id,
            @Field("secret_key") String secret_key);

    //Device Config
    @FormUrlEncoded
    @POST("config")
    Call<String> config(
            @Field("ssid") String ssid,
            @Field("pass") String pass,
            @Field("ap_pass") String ap_pass,
            @Field("secret_key") String secret_key);

    //Timer add
    @FormUrlEncoded
    @POST("timer/add")
    Call<String> addAlarm(
            @Field("time") String time,
            @Field("count") Integer count,
            @Field("secret_key") String secret_key);

    //Timer Edit
    @FormUrlEncoded
    @POST("timer/edit")
    Call<String> editAlarm(
            @Field("time") String time,
            @Field("count") Integer count,
            @Field("old_time") String old_time,
            @Field("secret_key") String secret_key);

    //Timer Delete
    @FormUrlEncoded
    @POST("timer/edit")
    Call<String> deleteAlarm(
            @Field("time") String time,
            @Field("secret_key") String secret_key);

    @GET("beri_pakan")
    Call<String> beriPakan(
            @Query("count") String count
    );

    @GET("status_pakan")
    Call<StatusPakan> statusPakan();


}
