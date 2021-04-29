package com.example.smart_control.network.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResponseLogin extends BaseResponse{
    @Expose
    @SerializedName("data")
    ResponseDataUser responseData;

    public ResponseDataUser getResponseData() {
        return responseData;
    }

    public void setResponseData(ResponseDataUser responseData) {
        this.responseData = responseData;
    }
}
