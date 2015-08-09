package com.stpi.campus.entity.item;

import java.util.ArrayList;
import java.util.List;

public class PreviewItemInfo {

    private String itemName;
    private float price;
    private float rate;
    private int itemId;
    private String picture;
    private List<String> tags;

    public PreviewItemInfo() {
        this.itemName = "";
        this.price = 0;
        this.rate = 0;
        this.itemId = 0;
        this.picture = "";
        this.tags = new ArrayList<String>();
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }
}
