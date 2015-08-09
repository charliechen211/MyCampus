package com.stpi.campus.items.inqueue;

/**
 * Created by Administrator on 14-1-4.
 */
public class InQueueOrder {

    private String endTime;
    private String startTime;
    private int merchantId;
    private int orderNum;
    private int queueId;
    private String status;
    private String tableType;
    private String tableTypeString;
    private int userId;

    public InQueueOrder() {
        this.endTime = "";
        this.startTime = "";
        this.merchantId = 0;
        this.orderNum = 0;
        this.queueId = 0;
        this.status = "";
        this.tableType = "";
        this.tableTypeString = "";
        this.userId = 0;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public int getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(int merchantId) {
        this.merchantId = merchantId;
    }

    public int getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(int orderNum) {
        this.orderNum = orderNum;
    }

    public int getQueueId() {
        return queueId;
    }

    public void setQueueId(int queueId) {
        this.queueId = queueId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTableType() {
        return tableType;
    }

    public void setTableType(String tableType) {
        this.tableType = tableType;
    }

    public String getTableTypeString() {
        return tableTypeString;
    }

    public void setTableTypeString(String tableTypeString) {
        this.tableTypeString = tableTypeString;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
