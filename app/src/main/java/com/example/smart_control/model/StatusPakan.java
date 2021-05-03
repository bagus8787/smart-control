package com.example.smart_control.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StatusPakan {
    @Expose
    @SerializedName("status") String status;
    @Expose
    @SerializedName("level") String level;
    @Expose
    @SerializedName("persen") Integer persen;

    public StatusPakan(String status, String level, Integer persen) {
        this.status = status;
        this.level = level;
        this.persen = persen;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public Integer getPersen() {
        return persen;
    }

    public void setPersen(Integer persen) {
        this.persen = persen;
    }
}
