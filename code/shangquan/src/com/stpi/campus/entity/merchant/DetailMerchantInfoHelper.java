package com.stpi.campus.entity.merchant;

/**
 * Created by Administrator on 13-12-5.
 */
public class DetailMerchantInfoHelper {

    private DetailMerchantInfo result;

    private String state;

    public DetailMerchantInfoHelper() {
        state = "fail";
        result = new DetailMerchantInfo();
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public DetailMerchantInfo getResult() {
        return result;
    }

    public void setResult(DetailMerchantInfo result) {
        this.result = result;
    }
}
