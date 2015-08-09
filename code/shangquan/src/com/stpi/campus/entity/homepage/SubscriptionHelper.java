package com.stpi.campus.entity.homepage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lyl on 2014/11/14.
 */
public class SubscriptionHelper {

    private String state;

    private List<Subscription> result;

    public SubscriptionHelper() {
        result = new ArrayList<Subscription>();
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public List<Subscription> getResult() {
        return result;
    }

    public void setResult(List<Subscription> result) {
        this.result = result;
    }
}
