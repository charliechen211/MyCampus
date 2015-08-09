package com.stpi.campus.entity.partTimeJob;

import java.sql.Timestamp;

/**
 * Created by Administrator on 2014/8/11.
 */
public class PartTimeJobContentInfo {
    private Integer jobId;
    private int userId;
    private String title;
    private String content;
    private int jobtype;
    private String jobname;
    private String place;
    private String pay;
    private String requirement;
    private String linkway;
    private int commentNum;
    private Timestamp date;

    public PartTimeJobContentInfo() {}

    public Integer getJobId() {
        return jobId;
    }

    public int getUserId() {
        return userId;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public int getJobtype() {
        return jobtype;
    }

    public String getJobname() {
        return jobname;
    }

    public String getPlace() {
        return place;
    }

    public String getPay() {
        return pay;
    }

    public String getRequirement() {
        return requirement;
    }

    public String getLinkway() {
        return linkway;
    }

    public int getCommentNum() {
        return commentNum;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setJobId(Integer jobId) {
        this.jobId = jobId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setJobtype(int jobtype) {
        this.jobtype = jobtype;
    }

    public void setJobname(String jobname) {
        this.jobname = jobname;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public void setPay(String pay) {
        this.pay = pay;
    }

    public void setRequirement(String requirement) {
        this.requirement = requirement;
    }

    public void setLinkway(String linkway) {
        this.linkway = linkway;
    }

    public void setCommentNum(int commentNum) {
        this.commentNum = commentNum;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }
}
