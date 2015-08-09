package com.stpi.campus.entity.order;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cyc on 14-1-1.
 */
public class OrderInfoHelper {

    private String state;
    private List<OrderInfo> results;

    public OrderInfoHelper() {
        this.state = "fail";
        this.results = new ArrayList<OrderInfo>();
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public List<OrderInfo> getResults() {
        return results;
    }

    public void setResults(List<OrderInfo> results) {
        this.results = results;
    }
}
