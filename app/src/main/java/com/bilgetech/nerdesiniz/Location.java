package com.bilgetech.nerdesiniz;

import com.google.gson.annotations.SerializedName;

/**
 * Created by DamraKOC on 1.8.2017.
 */
public class Location {

    @SerializedName("lat")
    private double lat = 0;

    @SerializedName("lng")
    private double lng = 0;

    public Location() {
    }

    public double getLatitude() {
        return lat;
    }

    public void setLatitude(double latitude) {
        this.lat = latitude;
    }

    public double getLongitude() {
        return lng;
    }

    public void setLongitude(double longitude) {
        this.lng = longitude;
    }
}
