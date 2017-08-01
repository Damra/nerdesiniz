package com.bilgetech.nerdesiniz;



import com.google.gson.annotations.SerializedName;

/**
 * TODO: implement
 */
public class Person {

    @SerializedName("deviceId")
    private String deviceId;

    @SerializedName("color")
    private String color;

    @SerializedName("name")
    private String name;

    @SerializedName("location")
    private Location location;

    @SerializedName("completed")
    private boolean completed;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

}
