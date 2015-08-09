package com.tang.guangshangquan.items.user;

/**
 * Created by Administrator on 13-11-28.
 */
public class LoginResult {

    private String state = null;
    private int userId = 0;

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
