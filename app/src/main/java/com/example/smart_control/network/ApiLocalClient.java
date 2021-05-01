package com.example.smart_control.network;

import com.example.smart_control.network.interceptor.TokenAuthenticator;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiLocalClient {
//    private static final String url = "http://192.168.1.16/api/v1/";

    private static final String url = "http://192.168.4.1/";

    private static ApiInterface REST_CLIENT;

    private static Retrofit retrofit;

    public ApiLocalClient() {

    }

    public static ApiInterface getInstance() {
        if (REST_CLIENT == null) {
            setupApiClient();
        }
        return REST_CLIENT;
    }

    private static void setupApiClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(url)
//                    .client(client)
//                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        REST_CLIENT = retrofit.create(ApiInterface.class);
    }

    public static Retrofit getClient() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .addInterceptor(new TokenAuthenticator())
                .build();

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit;

    }
}