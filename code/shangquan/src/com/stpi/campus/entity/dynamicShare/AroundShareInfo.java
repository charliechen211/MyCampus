package com.stpi.campus.entity.dynamicShare;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cyc on 13-12-4.
 */
public class AroundShareInfo {

    private String picture;
    private String userName;
    private String content;
    private String timestamp;
    private String merchantName;
    private float rate;
    private int entityId;
    private String itemName;
    private int entityType;
    private List<String> tags;
    private float consume;

    public AroundShareInfo() {
        this.picture = "";
        this.userName = "";
        this.content = "";
        this.timestamp = "";
        this.merchantName = "";
        this.rate = 0;
        this.entityId = 0;
        this.itemName = "";
        this.entityType = 0;
        this.tags = new ArrayList<String>();
        this.consume = 0;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public float getConsume() {
        return consume;
    }

    public void setConsume(float consume) {
        this.consume = consume;
    }

    public int getEntityId() {
        return entityId;
    }

    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    public int getEntityType() {
        return entityType;
    }

    public void setEntityType(int entityType) {
        this.entityType = entityType;
    }
}
