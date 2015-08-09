package com.stpi.campus.entity.item;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cyc on 14-1-2.
 */
public class RecItemInfoHelper {

    private List<RecItemInfo> recResult;

    private String state;

    public RecItemInfoHelper() {
        this.recResult = new ArrayList<RecItemInfo>();
        this.state = "fail";
    }

    public List<RecItemInfo> getRecResult() {
        return recResult;
    }

    public void setRecResult(List<RecItemInfo> recResult) {
        this.recResult = recResult;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
