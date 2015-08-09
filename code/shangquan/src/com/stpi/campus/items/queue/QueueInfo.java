package com.stpi.campus.items.queue;

/**
 * Created by Administrator on 13-12-12.
 */
public class QueueInfo {

    private String description;
    private String endTime;
    private String startTime;
    private String tableType;
    private int merchantId;
    private int mtiId;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getTableType() {
        return tableType;
    }

    public void setTableType(String tableType) {
        this.tableType = tableType;
    }

    public int getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(int merchantId) {
        this.merchantId = merchantId;
    }

    public int getMtiId() {
        return mtiId;
    }

    public void setMtiId(int mtiId) {
        this.mtiId = mtiId;
    }
}
