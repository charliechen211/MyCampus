package com.stpi.campus.entity.comment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cyc on 13-12-13.
 */
public class CommentInfoHelper {

    private List<CommentInfo> results;

    private String state = null;

    public CommentInfoHelper() {
        state = "fail";
        results = new ArrayList<CommentInfo>();
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public List<CommentInfo> getResults() {
        return results;
    }

    public void setResults(List<CommentInfo> results) {
        this.results = results;
    }
}
