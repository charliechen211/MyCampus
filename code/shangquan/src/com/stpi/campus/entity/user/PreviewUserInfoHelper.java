package com.stpi.campus.entity.user;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cyc on 13-12-17.
 */
public class PreviewUserInfoHelper {

    private List<DetailUserInfo> results = new ArrayList<DetailUserInfo>();

    private String state = null;

    public PreviewUserInfoHelper() {
        this.results = new ArrayList<DetailUserInfo>();
        this.state = "fail";
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public List<DetailUserInfo> getResults() {
        return results;
    }

    public void setResults(List<DetailUserInfo> results) {
        this.results = results;
    }

}
