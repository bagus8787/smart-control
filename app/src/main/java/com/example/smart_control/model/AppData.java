package com.example.smart_control.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AppData {
    @Expose
    @SerializedName("appKey") String app_key;
    @Expose
    @SerializedName("appDesaCode") String app_desa_code;

    public String getApp_key() {
        return app_key;
    }

    public void setApp_key(String app_key) {
        this.app_key = app_key;
    }

    public String getApp_desa_code() {
        return app_desa_code;
    }

    public void setApp_desa_code(String app_desa_code) {
        this.app_desa_code = app_desa_code;
    }
}
