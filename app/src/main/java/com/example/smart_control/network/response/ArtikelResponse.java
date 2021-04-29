package com.example.smart_control.network.response;

import com.example.smart_control.model.ArtikelList;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ArtikelResponse extends BaseResponse{
    @Expose
    @SerializedName("data")
    ArtikelList artikelList;

    public ArtikelList getArtikelList() {
        return artikelList;
    }

    public void setArtikelList(ArtikelList artikelList) {
        this.artikelList = artikelList;
    }
}

