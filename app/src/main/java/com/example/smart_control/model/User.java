package com.example.smart_control.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User implements Parcelable {
    @Expose
    @SerializedName("id") int id;
    @Expose
    @SerializedName("nik") String nik;
    @Expose
    @SerializedName("fullname") String fullname;
    @Expose
    @SerializedName("email") String email;
    @Expose
    @SerializedName("foto") String foto;
    @Expose
    @SerializedName("token") String token;
    @Expose
    @SerializedName("fcm_key") String fcm_key;
    @Expose
    @SerializedName("role_id") int role_id;
    @Expose
    @SerializedName("role_name") String role_name;

    @Expose
    @SerializedName("asal_provinsi_id") int asal_provinsi_id;
    @Expose
    @SerializedName("asal_kota_id") int asal_kota_id;
    @Expose
    @SerializedName("asal_kecamatan_id") int asal_kecamatan_id;
    @Expose
    @SerializedName("asal_kelurahan_id") long asal_kelurahan_id;

    @Expose
    @SerializedName("login_url_weh") String login_url_web;

//    @Expose
//    @SerializedName("get_db_list")
//    Db_list db_list;

//    @Expose
//    @SerializedName("get_role")
//    Role role;

//    public int getRole_id() {
//        return role_id;
//    }
//
//    public void setRole_id(int role_id) {
//        this.role_id = role_id;
//    }
//
//    public String getRole_name() {
//        return role_name;
//    }
//
//    public void setRole_name(String role_name) {
//        this.role_name = role_name;
//    }
//
//    public Db_list getDb_list() {
//        return db_list;
//    }
//
//    public void setDb_list(Db_list db_list) {
//        this.db_list = db_list;
//    }
//
//    public Role getRole() {
//        return role;
//    }
//
//    public void setRole(Role role) {
//        this.role = role;
//    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNik() {
        return nik;
    }

    public void setNik(String nik) {
        this.nik = nik;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getFcm_key() {
        return fcm_key;
    }

    public void setFcm_key(String fcm_key) {
        this.fcm_key = fcm_key;
    }

    public int getAsal_provinsi_id() {
        return asal_provinsi_id;
    }

    public void setAsal_provinsi_id(int asal_provinsi_id) {
        this.asal_provinsi_id = asal_provinsi_id;
    }

    public int getAsal_kota_id() {
        return asal_kota_id;
    }

    public void setAsal_kota_id(int asal_kota_id) {
        this.asal_kota_id = asal_kota_id;
    }

    public int getAsal_kecamatan_id() {
        return asal_kecamatan_id;
    }

    public void setAsal_kecamatan_id(int asal_kecamatan_id) {
        this.asal_kecamatan_id = asal_kecamatan_id;
    }

    public long getAsal_kelurahan_id() {
        return asal_kelurahan_id;
    }

    public void setAsal_kelurahan_id(long asal_kelurahan_id) {
        this.asal_kelurahan_id = asal_kelurahan_id;
    }

    public String getLogin_url_web() {
        return login_url_web;
    }

    public void setLogin_url_web(String login_url_web) {
        this.login_url_web = login_url_web;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.nik);
        dest.writeString(this.fullname);
        dest.writeString(this.email);
        dest.writeString(this.token);
        dest.writeString(this.fcm_key);
        dest.writeString(this.foto);

        dest.writeInt(this.asal_provinsi_id);
        dest.writeInt(this.asal_kota_id);
        dest.writeInt(this.asal_kecamatan_id);
        dest.writeLong(this.asal_kelurahan_id);
        dest.writeString(this.login_url_web);
    }

    public User(int id, String nik, String fullname, String email, String foto,String token,String fcm_key,int asal_provinsi_id,int asal_kota_id,int asal_kecamatan_id,long asal_kelurahan_id,String login_url_web) {
        this.id = id;
        this.nik = nik;
        this.fullname = fullname;
        this.email = email;
        this.foto = foto;
        this.token = token;
        this.fcm_key = fcm_key;

        this.asal_provinsi_id = asal_provinsi_id;
        this.asal_kota_id = asal_kota_id;
        this.asal_kecamatan_id = asal_kecamatan_id;
        this.asal_kelurahan_id = asal_kelurahan_id;

        this.login_url_web = login_url_web;

    }

    public User(Parcel in) {
        this.id = in.readInt();
        this.nik = in.readString();
        this.fullname = in.readString();
        this.email = in.readString();
        this.token = in.readString();
        this.fcm_key = in.readString();
        this.foto = in.readString();
        this.asal_provinsi_id = in.readInt();
        this.asal_kota_id = in.readInt();
        this.asal_kecamatan_id = in.readInt();
        this.asal_kelurahan_id = in.readInt();
        this.login_url_web = in.readString();
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
