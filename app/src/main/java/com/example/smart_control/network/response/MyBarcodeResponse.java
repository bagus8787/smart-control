package com.example.smart_control.network.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MyBarcodeResponse {
    @Expose
    @SerializedName("data") String data;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
