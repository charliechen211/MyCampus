package com.stpi.campus.entity.collection;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cyc on 13-12-13.
 */
public class CollectInfoHelper {

    private List<CollectInfo> results;

    private String state;

    public CollectInfoHelper() {
        state = "fail";
        results = new ArrayList<CollectInfo>();
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public List<CollectInfo> getResults() {
        return results;
    }

    public void setResults(List<CollectInfo> results) {
        this.results = results;
    }
}
