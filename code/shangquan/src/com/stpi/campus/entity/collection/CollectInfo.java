package com.stpi.campus.entity.collection;

/**
 * Created by cyc on 13-12-13.
 */
public class CollectInfo {
    private Integer moduleId;

    private String moduleName;

    private Integer itemId;

    private String title;

    private boolean adverFlag;

    private Integer adverId;

    private String content;

    private String picture;

    private String date;

    private  String itemLocation;

    private Double longitude;

    private Double latitude;

    private String itemTel;

    public Integer getModuleId() {
        return moduleId;
    }

    public void setModuleId(Integer moduleId) {
        this.moduleId = moduleId;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isAdverFlag() {
        return adverFlag;
    }

    public void setAdverFlag(boolean adverFlag) {
        this.adverFlag = adverFlag;
    }

    public Integer getAdverId() {
        return adverId;
    }

    public void setAdverId(Integer adverId) {
        this.adverId = adverId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setItemLocation(String itemLocation) {
        this.itemLocation = itemLocation;
    }


    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public String getItemLocation() {
        return itemLocation;
    }


    public Double getLatitude() {
        return latitude;
    }

    public String getItemTel() {
        return itemTel;
    }

    public void setItemTel(String itemTel) {
        this.itemTel = itemTel;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
