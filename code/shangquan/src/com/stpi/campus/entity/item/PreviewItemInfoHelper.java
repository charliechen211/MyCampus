package com.stpi.campus.entity.item;

import java.util.ArrayList;
import java.util.List;

public class PreviewItemInfoHelper {

    private List<PreviewItemInfo> results;

    private String state;

    public PreviewItemInfoHelper() {
        this.results = new ArrayList<PreviewItemInfo>();
        this.state = "fail";
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public List<PreviewItemInfo> getResults() {
        return results;
    }

    public void setResults(List<PreviewItemInfo> results) {
        this.results = results;
    }

}
