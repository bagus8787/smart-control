package com.example.smart_control.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Notification {
    @Expose
    @SerializedName("id") Integer id;
    @Expose
    @SerializedName("title") String title;
    @Expose
    @SerializedName("body") String body;

    public Notification(Integer id, String title, String body) {
        this.id = id;
        this.title = title;
        this.body = body;
    }

    public Notification() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
