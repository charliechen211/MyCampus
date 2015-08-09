package com.stpi.campus.entity.homepage;

/**
 * Created by lyl on 2014/11/14.
 */
public class Subscription {

    private Integer itemId;  //具体商家或者实体的Id
    private int typeId; //校园周边的 二级分类，比如住宿 餐饮之类
    private int objectId;  //校园周边的  三级分类  比如地铁，公交等级别的分类

    private String itemName;
    private String itemDescription;
    private String itemPic;
    private String itemLocation;
    private String itemTel;
    private String itemPlus;  //备注

    //经纬度
    private Double longitude;
    private Double latitude;

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public int getObjectId() {
        return objectId;
    }

    public void setObjectId(int objectId) {
        this.objectId = objectId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public String getItemPic() {
        return itemPic;
    }

    public void setItemPic(String itemPic) {
        this.itemPic = itemPic;
    }

    public String getItemLocation() {
        return itemLocation;
    }

    public void setItemLocation(String itemLocation) {
        this.itemLocation = itemLocation;
    }

    public String getItemTel() {
        return itemTel;
    }

    public void setItemTel(String itemTel) {
        this.itemTel = itemTel;
    }

    public String getItemPlus() {
        return itemPlus;
    }

    public void setItemPlus(String itemPlus) {
        this.itemPlus = itemPlus;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }
}
