package com.stpi.campus.entity.merchant;

public class RecMerchantInfo {

    private float averageConsume;
    private float averageRating;
    private float distance;
    private int eType;
    private int eid;
    private float latitude;
    private float longitude;
    private int mType;
    private String name;
    private String reason;
    private int recType;
    private float weight;
    private String picture;

    public RecMerchantInfo() {
        this.averageConsume = 0;
        this.averageRating = 0;
        this.distance = 0;
        this.eType = 0;
        this.eid = 0;
        this.latitude = 0;
        this.longitude = 0;
        this.mType = 0;
        this.name = "";
        this.reason = "";
        this.recType = 0;
        this.weight = 0;
        this.picture = "";
    }

    public float getAverageConsume() {
        return averageConsume;
    }

    public void setAverageConsume(float averageConsume) {
        this.averageConsume = averageConsume;
    }

    public float getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(float averageRating) {
        this.averageRating = averageRating;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public int geteType() {
        return eType;
    }

    public void seteType(int eType) {
        this.eType = eType;
    }

    public int getEid() {
        return eid;
    }

    public void setEid(int eid) {
        this.eid = eid;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public int getmType() {
        return mType;
    }

    public void setmType(int mType) {
        this.mType = mType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public int getRecType() {
        return recType;
    }

    public void setRecType(int recType) {
        this.recType = recType;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }
}
