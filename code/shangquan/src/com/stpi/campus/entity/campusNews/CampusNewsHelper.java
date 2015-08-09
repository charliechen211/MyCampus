package com.stpi.campus.entity.campusNews;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2014/7/29.
 */
public class CampusNewsHelper {
    private List<CampusNewsInfo> result;
    private String state;

    public CampusNewsHelper() {
        result = new ArrayList<CampusNewsInfo>();
        state = "fail";
    }

    public List<CampusNewsInfo> getResult() {
        return result;
    }

    public void setResult(List<CampusNewsInfo> result) {
        this.result = result;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
