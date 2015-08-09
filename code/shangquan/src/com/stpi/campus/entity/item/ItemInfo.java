package com.stpi.campus.entity.item;

/**
 * Created by cyc on 14-1-2.
 */
public class ItemInfo {

    private String itemDescription = "";
    private Integer itemId = 0;
    private String itemLocation = "";
    private String itemName = "";
    private String itemPic = null;
    private String itemPlus = "";
    private String itemTel = "";
    private Double latitude =null;
    private Double longitude=null;
    private Integer objectId = 0;
    private Integer typeId = 0;

    public ItemInfo() {
        ;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public String getItemLocation() {
        return itemLocation;
    }

    public void setItemLocation(String itemLocation) {
        this.itemLocation = itemLocation;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemPic() {
        return itemPic;
    }

    public void setItemPic(String itemPic) {
        this.itemPic = itemPic;
    }

    public String getItemPlus() {
        return itemPlus;
    }

    public void setItemPlus(String itemPlus) {
        this.itemPlus = itemPlus;
    }

    public String getItemTel() {
        return itemTel;
    }

    public void setItemTel(String itemTel) {
        this.itemTel = itemTel;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Integer getObjectId() {
        return objectId;
    }

    public void setObjectId(Integer objectId) {
        this.objectId = objectId;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }
}
