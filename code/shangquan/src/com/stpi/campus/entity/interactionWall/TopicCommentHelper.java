package com.stpi.campus.entity.interactionWall;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2014/7/30.
 */
public class TopicCommentHelper {
    private List<TopicCommentInfo> result;
    private String state;

    public TopicCommentHelper() {
        result = new ArrayList<TopicCommentInfo>();
        state = "fail";
    }

    public List<TopicCommentInfo> getResult() {
        return result;
    }

    public void setResult(List<TopicCommentInfo> result) {
        this.result = result;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
