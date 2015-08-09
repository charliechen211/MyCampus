package com.stpi.campus.entity.cart;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cyc on 14-1-1.
 */
public class CartInfoHelper {

    private String state;
    private List<CartInfo> results;

    public CartInfoHelper() {
        this.state = "fail";
        this.results = new ArrayList<CartInfo>();
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public List<CartInfo> getResults() {
        return results;
    }

    public void setResults(List<CartInfo> results) {
        this.results = results;
    }
}
