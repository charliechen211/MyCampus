package com.stpi.campus.entity.sh_lv;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cyc on 14-7-27.
 */
public class ShCommentInfoHelper {

    private List<ShCommentInfo> results = null;
    private String state = null;

    public ShCommentInfoHelper() {
        this.results = new ArrayList<ShCommentInfo>();
        this.state = "fail";
    }

    public List<ShCommentInfo> getResults() {
        return results;
    }

    public void setResults(List<ShCommentInfo> results) {
        this.results = results;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

}
