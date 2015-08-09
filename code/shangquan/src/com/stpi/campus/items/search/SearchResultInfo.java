package com.stpi.campus.items.search;

/**
 * Created by Administrator on 13-12-5.
 */
public class SearchResultInfo {
    private String address = null;
    private int averageConsume = -1;
    private float averageValue = -1;
    private int merchantId = -1;
    private String merchantName = null;
    private String telNumber = null;
    private String picture = null;
    private double distance = 0;
    private int type = 0;
    private int typeId = 0;
    private String[] tagName = null;

    public String[] getTagName() {
        return tagName;
    }

    public void setTagName(String[] tagName) {
        this.tagName = tagName;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getAverageConsume() {
        return averageConsume;
    }

    public void setAverageConsume(int averageConsume) {
        this.averageConsume = averageConsume;
    }

    public float getAverageValue() {
        return averageValue;
    }

    public void setAverageValue(float averageValue) {
        this.averageValue = averageValue;
    }

    public int getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(int merchantId) {
        this.merchantId = merchantId;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getTelNumber() {
        return telNumber;
    }

    public void setTelNumber(String telNumber) {
        this.telNumber = telNumber;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }
}
