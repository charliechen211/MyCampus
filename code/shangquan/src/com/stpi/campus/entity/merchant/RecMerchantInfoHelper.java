package com.stpi.campus.entity.merchant;

import java.util.ArrayList;
import java.util.List;

public class RecMerchantInfoHelper {

    private List<RecMerchantInfo> recResult;

    private String state;

    public RecMerchantInfoHelper() {
        this.recResult = new ArrayList<RecMerchantInfo>();
        this.state = "fail";
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public List<RecMerchantInfo> getRecResult() {
        return recResult;
    }

    public void setRecResult(List<RecMerchantInfo> recResult) {
        this.recResult = recResult;
    }

}
