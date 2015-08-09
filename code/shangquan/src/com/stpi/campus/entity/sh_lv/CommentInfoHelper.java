package com.stpi.campus.entity.sh_lv;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cyc on 14-7-27.
 */
public class CommentInfoHelper {

    private List<CommentInfo> results = null;
    private String state = null;

    public CommentInfoHelper() {
        this.results = new ArrayList<CommentInfo>();
        this.state = "fail";
    }

    public List<CommentInfo> getResults() {
        return results;
    }

    public void setResults(List<CommentInfo> results) {
        this.results = results;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

}
