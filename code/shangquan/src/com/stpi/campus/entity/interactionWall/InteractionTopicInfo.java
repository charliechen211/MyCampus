package com.stpi.campus.entity.interactionWall;


/**
 * Created by Administrator on 2014/7/27.
 */
public class InteractionTopicInfo {
    private String content;
    private String timestamp;
    private Integer commentNum;
    private boolean friend;
    private String topicId;

    public InteractionTopicInfo() {
        content = "";
        timestamp = "";
        commentNum = 0;
        friend = false;
        topicId = "";
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

    public Integer getCommentNum() {
        return commentNum;
    }

    public void setCommentNum(Integer commentNum) {
        this.commentNum = commentNum;
    }

    public boolean isFriend() {
        return friend;
    }

    public void setFriend(boolean isFriend) {
        this.friend = isFriend;
    }

    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }
}
