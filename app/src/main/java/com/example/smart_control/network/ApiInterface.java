package com.example.smart_control.network;

import com.example.smart_control.model.StatusPakan;
import com.example.smart_control.network.response.ArtikelResponse;
import com.example.smart_control.network.response.BaseResponse;
import com.example.smart_control.network.response.LogOutResponse;
import com.example.smart_control.network.response.RegisterResponse;
import com.example.smart_control.network.response.ResponseLogin;
import com.example.smart_control.network.response.UserResponse;

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

public interface ApiInterface {
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
    @FormUrlEncoded
    @POST("timer/add")
    Call<String> addAlarm(
            @Field("time") String time,
            @Field("count") Integer count);

    @GET("beri_pakan")
    Call<String> beriPakan();

    @GET("status_pakan")
    Call<StatusPakan> statusPakan(
            @Field("status") String status,
            @Field("jarak") Integer jarak);
}