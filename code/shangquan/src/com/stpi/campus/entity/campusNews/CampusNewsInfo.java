package com.stpi.campus.entity.campusNews;

/**
 * Created by Administrator on 2014/7/29.
 */
public class CampusNewsInfo {
    private String topicId;
    private String timestamp;
    private String title;

    public CampusNewsInfo() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
