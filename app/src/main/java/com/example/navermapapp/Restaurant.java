package com.example.navermapapp;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 1) 직접 코딩
@Data
// 1) 직접 코딩
@NoArgsConstructor
// 1) 직접 코딩
@AllArgsConstructor
// 1) 직접 코딩
public class Restaurant implements Parcelable {
    // 1) 직접 코딩
    private String address;
    private String name ;
    private String call ;
    private String gubun ;
    private String gubunDetail ;

    // 2) 출처 : https://stackoverflow.com/questions/7181526/how-can-i-make-my-custom-objects-parcelable
    public Restaurant(Parcel in){
        String[] data = new String[5];

        in.readStringArray(data);
        // 1) 직접 코딩 - 출처 응용
        this.address = data[0];
        this.name = data[1];
        this.call = data[2];
        this.gubun = data[3];
        this.gubunDetail = data[4];
    }

    // 2) 출처 : https://stackoverflow.com/questions/7181526/how-can-i-make-my-custom-objects-parcelable
    @Override
    public int describeContents() {
        return 0;
    }

    // 2) 출처 : https://stackoverflow.com/questions/7181526/how-can-i-make-my-custom-objects-parcelable
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {
                // 1) 직접코딩 - 출처 응용
                this.address,
                this.name,
                this.call,
                this.gubun,
                this.gubunDetail
        });
    }

    // 2) 출처 : https://stackoverflow.com/questions/7181526/how-can-i-make-my-custom-objects-parcelable
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Restaurant createFromParcel(Parcel in) {
            return new Restaurant(in);
        }
        public Restaurant[] newArray(int size) {
            return new Restaurant[size];
        }
    };
}
