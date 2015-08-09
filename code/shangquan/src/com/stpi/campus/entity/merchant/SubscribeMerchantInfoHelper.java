package com.stpi.campus.entity.merchant;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cyc on 13-12-27.
 */
public class SubscribeMerchantInfoHelper {

    private List<SubscribeMerchantInfo> results = null;
    private String state = null;

    public SubscribeMerchantInfoHelper() {
        this.results = new ArrayList<SubscribeMerchantInfo>();
        this.state = "fail";
    }

    public List<SubscribeMerchantInfo> getResults() {
        return results;
    }

    public void setResults(List<SubscribeMerchantInfo> results) {
        this.results = results;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

}
