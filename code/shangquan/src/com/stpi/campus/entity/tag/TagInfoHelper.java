package com.stpi.campus.entity.tag;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cyc on 14-1-5.
 */
public class TagInfoHelper {

    private String state;
    private List<String> recResult;

    public TagInfoHelper() {
        this.state = "fail";
        this.recResult = new ArrayList<String>();
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public List<String> getRecResult() {
        return recResult;
    }

    public void setRecResult(List<String> recResult) {
        this.recResult = recResult;
    }
}
