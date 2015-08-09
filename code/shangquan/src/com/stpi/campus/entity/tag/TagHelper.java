package com.stpi.campus.entity.tag;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cyc on 2014/11/22.
 */
public class TagHelper {

    private List<String> results;
    private String state;

    public TagHelper() {
        results = new ArrayList<String>();
        state = "fail";
    }

    public TagHelper(List<String> results, String state) {
        this.results = results;
        this.state = state;
    }

    public List<String> getResults() {
        return results;
    }

    public void setResults(List<String> results) {
        this.results = results;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

}
