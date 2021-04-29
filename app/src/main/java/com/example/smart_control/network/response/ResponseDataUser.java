package com.example.smart_control.network.response;

import com.example.smart_control.model.AppData;
import com.example.smart_control.model.User;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResponseDataUser extends ResponseLogin {
    @Expose
    @SerializedName("user")
    User userData;

    @Expose
    @SerializedName("appData")
    AppData appData;

    public User getUserData() {
        return userData;
    }

    public void setUserData(User userData) {
        this.userData = userData;
    }

    public AppData getAppData() {
        return appData;
    }

    public void setAppData(AppData appData) {
        this.appData = appData;
    }
}
