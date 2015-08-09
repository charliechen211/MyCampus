package com.stpi.campus.entity.schoolRegion;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cyc on 2014/11/22.
 */
public class SchoolRegionHelper {

    private List<SchoolInfo> results;
    private String state;

    public SchoolRegionHelper() {
        results = new ArrayList<SchoolInfo>();
    }

    public SchoolRegionHelper(List<SchoolInfo> results, String state) {
        this.results = results;
        this.state = state;
    }

    public List<SchoolInfo> getResults() {
        return results;
    }

    public void setResults(List<SchoolInfo> results) {
        this.results = results;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
