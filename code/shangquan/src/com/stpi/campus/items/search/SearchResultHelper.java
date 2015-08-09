package com.stpi.campus.items.search;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 13-12-5.
 */
public class SearchResultHelper {

    private List<SearchResultInfo> results = null;
    private String state = "";

    public SearchResultHelper() {
        state = "success";
        results = new ArrayList<SearchResultInfo>();
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public List<SearchResultInfo> getResults() {
        return results;
    }

    public void setResults(List<SearchResultInfo> results) {
        this.results = results;
    }
}
