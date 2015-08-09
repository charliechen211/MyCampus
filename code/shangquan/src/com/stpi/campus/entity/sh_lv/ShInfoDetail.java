package com.stpi.campus.entity.sh_lv;

/**
 * Created by cyc on 2014/11/15.
 */
public class ShInfoDetail {

    private Integer circleId;
    private Integer commentNum;
    private Integer delFlg;
    private String description = "";
    private String linkWay = "";
    private String picture = "";
    private Integer shId;
    private String timpstamp = "";
    private String title = "";
    private Integer type;
    private Integer userId;

    public ShInfoDetail() {
    }

    public ShInfoDetail(Integer circleId, Integer commentNum, Integer delFlg, String description, String linkWay, String picture, Integer shId, String timpstamp, String title, Integer type, Integer userId) {
        this.circleId = circleId;
        this.commentNum = commentNum;
        this.delFlg = delFlg;
        this.description = description;
        this.linkWay = linkWay;
        this.picture = picture;
        this.shId = shId;
        this.timpstamp = timpstamp;
        this.title = title;
        this.type = type;
        this.userId = userId;
    }

    public Integer getCircleId() {
        return circleId;
    }

    public void setCircleId(Integer circleId) {
        this.circleId = circleId;
    }

    public Integer getCommentNum() {
        return commentNum;
    }

    public void setCommentNum(Integer commentNum) {
        this.commentNum = commentNum;
    }

    public Integer getDelFlg() {
        return delFlg;
    }

    public void setDelFlg(Integer delFlg) {
        this.delFlg = delFlg;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLinkWay() {
        return linkWay;
    }

    public void setLinkWay(String linkWay) {
        this.linkWay = linkWay;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public Integer getShId() {
        return shId;
    }

    public void setShId(Integer shId) {
        this.shId = shId;
    }

    public String getTimpstamp() {
        return timpstamp;
    }

    public void setTimpstamp(String timpstamp) {
        this.timpstamp = timpstamp;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

}
