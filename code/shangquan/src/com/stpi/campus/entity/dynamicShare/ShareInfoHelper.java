package com.stpi.campus.entity.dynamicShare;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cyc on 13-12-4.
 */
public class ShareInfoHelper {

    private List<AroundShareInfo> results;

    private String state;

    public ShareInfoHelper() {
        state = "fail";
        results = new ArrayList<AroundShareInfo>();
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public List<AroundShareInfo> getResults() {
        return results;
    }

    public void setResults(List<AroundShareInfo> results) {
        this.results = results;
    }
}
