package com.stpi.campus.entity.item;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cyc on 14-1-2.
 */
public class ItemInfoListHelper {

    private List<ItemInfo> results;

    private String state;

    public ItemInfoListHelper() {
        this.results = new ArrayList<ItemInfo>();
        this.state = "fail";
    }

    public List<ItemInfo> getResults() {
        return results;
    }

    public void setResults(List<ItemInfo> results) {
        this.results = results;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
