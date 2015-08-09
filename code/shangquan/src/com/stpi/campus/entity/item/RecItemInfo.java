package com.stpi.campus.entity.item;

/**
 * Created by cyc on 14-1-2.
 */
public class RecItemInfo {

    private float price;
    private float averageRating;
    private float distance;
    private int eType;
    private int eid;
    private float latitude;
    private float longitude;
    private int iType;
    private String merchantName;
    private String name;
    private String reason;
    private int recType;
    private float weight;
    private String picture;

    public RecItemInfo() {
        this.price = 0;
        this.averageRating = 0;
        this.distance = 0;
        this.eType = 0;
        this.eid = 0;
        this.latitude = 0;
        this.longitude = 0;
        this.iType = 0;
        this.name = "";
        this.reason = "";
        this.recType = 0;
        this.weight = 0;
        this.picture = "";
        merchantName = "";
    }

    public int getiType() {
        return iType;
    }

    public void setiType(int iType) {
        this.iType = iType;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
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
