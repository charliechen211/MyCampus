package com.stpi.campus.entity.sh_lv;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cyc on 14-7-27.
 */
public class ShInfoHelper {

    private List<ShInfo> results = null;
    private String state = null;

    public ShInfoHelper() {
        this.results = new ArrayList<ShInfo>();
        this.state = "fail";
    }

    public List<ShInfo> getResults() {
        return results;
    }

    public void setResults(List<ShInfo> results) {
        this.results = results;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

}
