package com.stpi.campus.entity.campusNews;

/**
 * Created by Administrator on 2014/8/5.
 */
public class CampusNewsWebContentHelper {
    String result;
    String state;

    public CampusNewsWebContentHelper(String result, String state) {
        this.result = result;
        this.state = state;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
