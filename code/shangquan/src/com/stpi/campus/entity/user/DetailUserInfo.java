package com.stpi.campus.entity.user;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cyc on 13-12-17.
 */
public class DetailUserInfo {

    private String userName = "";
    private String picture = "";
    private int age;
    private int job;
    private int home;
    private List<String> tags = null;
    private int followNum = 0;
    private int fanNum = 0;
    private float point = 0;
    private int userId = 0;
    private String schoolName;
    private String regionName;
    private List<String> usertags = null;


    public DetailUserInfo() {
        this.userName = "";
        this.picture = "";
        this.age = 0;
        this.job = 0;
        this.home = 0;
        this.usertags = new ArrayList<String>();
        this.followNum = 0;
        this.fanNum = 0;
        this.point = 0;
        this.userId = 0;
        this.schoolName = "上海交通大学";
        this.regionName = "闵行校区";
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getJob() {
        return job;
    }

    public void setJob(int job) {
        this.job = job;
    }

    public int getHome() {
        return home;
    }

    public void setHome(int home) {
        this.home = home;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public List<String> getTags() {
        return usertags;
    }

    public void setTags(List<String> tags) {
        this.usertags = tags;
    }

    public int getFollowNum() {
        return followNum;
    }

    public void setFollowNum(int followNum) {
        this.followNum = followNum;
    }

    public int getFanNum() {
        return fanNum;
    }

    public void setFanNum(int fanNum) {
        this.fanNum = fanNum;
    }

    public float getPoint() {
        return point;
    }

    public void setPoint(float point) {
        this.point = point;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }
}
