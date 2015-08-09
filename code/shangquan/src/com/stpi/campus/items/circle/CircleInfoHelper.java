package com.stpi.campus.items.circle;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cyc on 14-8-31.
 */
public class CircleInfoHelper {

    private List<CircleInfo> results = null;
    private String state = "";

    public CircleInfoHelper() {
        state = "success";
        results = new ArrayList<CircleInfo>();
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public List<CircleInfo> getResults() {
        return results;
    }

    public void setResults(List<CircleInfo> results) {
        this.results = results;
    }

}