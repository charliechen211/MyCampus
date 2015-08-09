package com.stpi.campus.entity.user;

/**
 * Created by cyc on 13-12-19.
 */
public class DetailUserInfoHelper {

    private DetailUserInfo result;

    private String state;

    public DetailUserInfoHelper() {
        this.result = new DetailUserInfo();
        this.state = "fail";
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public DetailUserInfo getResult() {
        return result;
    }

    public void setResult(DetailUserInfo result) {
        this.result = result;
    }

}
