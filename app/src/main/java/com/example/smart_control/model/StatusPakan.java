package com.example.smart_control.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StatusPakan {
    @Expose
    @SerializedName("status") String status;
    @Expose
    @SerializedName("jarak") int jarak;

    public StatusPakan(String status, int jarak) {
        this.status = status;
        this.jarak = jarak;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getJarak() {
        return jarak;
    }

    public void setJarak(int jarak) {
        this.jarak = jarak;
    }
}
