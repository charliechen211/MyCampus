package com.stpi.campus.entity.partTimeJob;

/**
 * Created by Administrator on 2014/8/11.
 */
public class PartTimeJobInfo {
    private String jobId;
    private String title;
    private String date;
    private Integer commentNum;

    public PartTimeJobInfo(String jobId, String title, String date) {
        this.jobId = jobId;
        this.title = title;
        this.date = date;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getCommentNum() {
        return commentNum;
    }

    public void setCommentNum(Integer commentNum) {
        this.commentNum = commentNum;
    }
}
