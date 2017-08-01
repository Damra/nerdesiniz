package com.bilgetech.nerdesiniz;

import com.google.gson.annotations.SerializedName;

/**
 * Created by DamraKOC on 1.8.2017.
 */
public class Location {

    @SerializedName("lat")
    double latitude = 0;

    @SerializedName("lng")
    double longitude = 0;

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
