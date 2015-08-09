package com.stpi.campus.entity.sh_lv;

/**
 * Created by cyc on 14-7-27.
 */
public class CommentInfo {

    private Integer id;
    private Integer userId;
    private Integer objectId;
    private String comment;
    private String timestamp;
    private Integer type;
    private String userName;
    private String picture;

    public CommentInfo() {
        this.id = 0;
        this.userId = 0;
        this.objectId = 0;
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

    public Integer getObjectId() {
        return objectId;
    }

    public void setObjectId(Integer objectId) {
        this.objectId = objectId;
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
