package com.example.smart_control.model;

import android.os.Parcel;
import android.os.Parcelable;

public class AlarmModel implements Parcelable {
    int id;
    String name, tall, time, count, old_time, day, status;

    public AlarmModel(int id, String name, String tall, String time, String count, String old_time, String day, String status) {
        this.id = id;
        this.name = name;
        this.tall = tall;
        this.time = time;
        this.count = count;
        this.old_time = old_time;
        this.day = day;
        this.status = status;
    }

    // contrustor(empty)
    public AlarmModel() {
    }

    protected AlarmModel(Parcel in) {
        id = in.readInt();
        name = in.readString();
        tall = in.readString();
        time = in.readString();
        count = in.readString();
        old_time = in.readString();
        day = in.readString();
        status = in.readString();
    }

    public static final Creator<AlarmModel> CREATOR = new Creator<AlarmModel>() {
        @Override
        public AlarmModel createFromParcel(Parcel in) {
            return new AlarmModel(in);
        }

        @Override
        public AlarmModel[] newArray(int size) {
            return new AlarmModel[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTall() {
        return tall;
    }

    public void setTall(String tall) {
        this.tall = tall;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getOld_time() {
        return old_time;
    }

    public void setOld_time(String old_time) {
        this.old_time = old_time;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(name);
        parcel.writeString(tall);
        parcel.writeString(time);
        parcel.writeString(count);
        parcel.writeString(old_time);
        parcel.writeString(day);
        parcel.writeString(status);
    }
}
