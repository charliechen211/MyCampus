package com.stpi.campus.entity.merchant;

import java.util.ArrayList;
import java.util.List;

public class DetailMerchantInfo {

    private int averageConsume = 0;
    private float averageValue = 0;
    private int merchantId = 0;
    private String merchantName = "";
    private String telNumber = "";
    private String address = "";
    private List<String> tagName = null;
    private String picture = "";
    private double distance = 0;
    private int type = 0;
    private int typeId = 0;

    public DetailMerchantInfo() {
        this.averageConsume = 0;
        this.averageValue = 0;
        this.merchantId = 0;
        this.merchantName = "";
        this.telNumber = "";
        this.address = "";
        this.tagName = new ArrayList<String>();
        this.picture = "";
        this.distance = 0;
        this.type = 0;
        this.typeId = 0;
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

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<String> getTagName() {
        return tagName;
    }

    public void setTagName(List<String> tagName) {
        this.tagName = tagName;
    }

}
