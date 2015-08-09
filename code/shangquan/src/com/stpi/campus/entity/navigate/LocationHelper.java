package com.stpi.campus.entity.navigate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lyl on 2014/8/31.
 */
public class LocationHelper {
    private List<Location> results;

    private String state;

    public LocationHelper() {
        results = new ArrayList<Location>();
    }

    public String getState() {
        return state;
    }


    public void setState(String state) {
        state = state;
    }

    public List<Location> getResults() {
        return results;
    }

    public void setResults(List<Location> results) {
        this.results = results;
    }
}
