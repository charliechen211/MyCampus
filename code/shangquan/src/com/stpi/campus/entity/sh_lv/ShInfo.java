package com.stpi.campus.entity.sh_lv;

/**
 * Created by cyc on 14-7-27.
 */
public class ShInfo {

    private ShInfoDetail shlvinfo = new ShInfoDetail();
    private String timestamp = "";
    private String userName = "";
    private String userPic = "";

    public ShInfo() {
    }

    public ShInfo(ShInfoDetail shlvinfo, String timestamp, String userName, String userPic) {
        this.shlvinfo = shlvinfo;
        this.timestamp = timestamp;
        this.userName = userName;
        this.userPic = userPic;
    }

    public ShInfoDetail getShlvinfo() {
        return shlvinfo;
    }

    public void setShlvinfo(ShInfoDetail shlvinfo) {
        this.shlvinfo = shlvinfo;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPic() {
        return userPic;
    }

    public void setUserPic(String userPic) {
        this.userPic = userPic;
    }
}
