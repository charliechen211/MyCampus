package com.stpi.campus.entity.sh_lv;

/**
 * Created by cyc on 14-7-27.
 */
public class ShCommentInfo {

    private Integer id;
    private Integer userId;
    private Integer shId;
    private String comment;
    private String timestamp;
    private Integer type;
    private String userName;
    private String picture;

    public ShCommentInfo() {
        this.id = 0;
        this.userId = 0;
        this.shId = 0;
        this.comment = "";
        this.timestamp = "";
        this.type = 0;
        this.userName = "";
        this.picture = "";
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getShId() {
        return shId;
    }

    public void setShId(Integer shId) {
        this.shId = shId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }
}
