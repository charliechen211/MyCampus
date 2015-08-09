package com.stpi.campus.entity.interactionWall;

/**
 * Created by Administrator on 2014/7/30.
 */
public class TopicCommentInfo {
    private String date;
    private String content;
    private String id;
    private String topicId;

    public TopicCommentInfo() {
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }
}
