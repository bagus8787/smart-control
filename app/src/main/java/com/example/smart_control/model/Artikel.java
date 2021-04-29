package com.example.smart_control.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Artikel implements Parcelable {
    @Expose
    @SerializedName("id") int id;
    @Expose
    @SerializedName("gambar") String gambar;
    @Expose
    @SerializedName("isi") String isi;
    @Expose
    @SerializedName("tgl_upload") String tgl_upload;
    @Expose
    @SerializedName("id_kategori") int id_kategori;
    @Expose
    @SerializedName("id_user") int id_user;
    @Expose
    @SerializedName("judul") String judul;
    @Expose
    @SerializedName("gambar1") String gambar1;
    @Expose
    @SerializedName("slug") String slug;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGambar() {
        return gambar;
    }

    public void setGambar(String gambar) {
        this.gambar = gambar;
    }

    public String getIsi() {
        return isi;
    }

    public void setIsi(String isi) {
        this.isi = isi;
    }

    public String getTgl_upload() {
        return tgl_upload;
    }

    public void setTgl_upload(String tgl_upload) {
        this.tgl_upload = tgl_upload;
    }

    public int getId_kategori() {
        return id_kategori;
    }

    public void setId_kategori(int id_kategori) {
        this.id_kategori = id_kategori;
    }

    public int getId_user() {
        return id_user;
    }

    public void setId_user(int id_user) {
        this.id_user = id_user;
    }

    public String getJudul() {
        return judul;
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }

    public String getGambar1() {
        return gambar1;
    }

    public void setGambar1(String gambar1) {
        this.gambar1 = gambar1;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.gambar);
        dest.writeString(this.isi);
        dest.writeString(this.tgl_upload);
        dest.writeInt(this.id_kategori);
        dest.writeInt(this.id_user);
        dest.writeString(this.judul);
        dest.writeString(this.gambar1);
        dest.writeString(this.slug);
    }

    public Artikel() {
    }

    protected Artikel(Parcel in) {
        this.id = in.readInt();
        this.gambar = in.readString();
        this.isi = in.readString();
        this.tgl_upload = in.readString();
        this.id_kategori = in.readInt();
        this.id_user = in.readInt();
        this.judul = in.readString();
        this.gambar1 = in.readString();
        this.slug = in.readString();
    }

    public static final Parcelable.Creator<Artikel> CREATOR = new Parcelable.Creator<Artikel>() {
        @Override
        public Artikel createFromParcel(Parcel source) {
            return new Artikel(source);
        }

        @Override
        public Artikel[] newArray(int size) {
            return new Artikel[size];
        }
    };

    @Override
    public String toString() {
        return judul;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Artikel){
            Artikel c = (Artikel ) obj;
            if(c.getJudul().equals(judul) && c.getId()==id ) return true;
        }

        return false;
    }
}
