package com.example.navermapapp;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Restaurant implements Parcelable {
    private String address;
    private String name ;
    private String call ;

    private String gubun ;
    private String gubunDetail ;

    public Restaurant(Parcel in){
        String[] data = new String[5];

        in.readStringArray(data);
        // the order needs to be the same as in writeToParcel() method
        this.address = data[0];
        this.name = data[1];
        this.call = data[2];
        this.gubun = data[3];
        this.gubunDetail = data[4];
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {this.address,
                this.name,
                this.call,
        this.gubun,this.gubunDetail});
    }
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Restaurant createFromParcel(Parcel in) {
            return new Restaurant(in);
        }

        public Restaurant[] newArray(int size) {
            return new Restaurant[size];
        }
    };
}
