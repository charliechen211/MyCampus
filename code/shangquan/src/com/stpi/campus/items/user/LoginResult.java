package com.stpi.campus.items.user;


import com.stpi.campus.entity.user.DetailUserInfo;

/**
 * Created by Administrator on 13-11-28.
 */
public class LoginResult {

    private String state = null;
    private DetailUserInfo result;

    public LoginResult() {
        this.result = new DetailUserInfo();
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
