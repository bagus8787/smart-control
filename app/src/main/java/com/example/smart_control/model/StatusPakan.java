package com.example.smart_control.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StatusPakan {
    @Expose
    @SerializedName("status") String status;
    @Expose
    @SerializedName("jarak") int jarak;
}
