package com.stpi.campus.entity.comment;

/**
 * Created by cyc on 13-12-13.
 */
public class CommentInfo {

    private String commentId;
    private String content;
    private int entityId;
    private int entityType;
    private float rate1;
    private float rate2;
    private float rate3;
    private String timestamp;
    private int userId;
    private String userName;
    private int consume;

    public CommentInfo() {
        this.commentId = "";
        this.content = "";
        this.entityId = 0;
        this.entityType = 0;
        this.rate1 = 0;
        this.rate2 = 0;
        this.rate3 = 0;
        this.timestamp = "";
        this.userId = 0;
        this.userName = "";
        this.consume = 0;
    }

    public int getConsume() {
        return consume;
    }

    public void setConsume(int consume) {
        this.consume = consume;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getEntityId() {
        return entityId;
    }

    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    public int getEntityType() {
        return entityType;
    }

    public void setEntityType(int entityType) {
        this.entityType = entityType;
    }

    public float getRate1() {
        return rate1;
    }

    public void setRate1(float rate1) {
        this.rate1 = rate1;
    }

    public float getRate2() {
        return rate2;
    }

    public void setRate2(float rate2) {
        this.rate2 = rate2;
    }

    public float getRate3() {
        return rate3;
    }

    public void setRate3(float rate3) {
        this.rate3 = rate3;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
