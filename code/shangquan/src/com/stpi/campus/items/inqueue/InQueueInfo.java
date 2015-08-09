package com.stpi.campus.items.inqueue;

import com.stpi.campus.items.queue.TableOrderInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 14-1-4.
 */
public class InQueueInfo {
    private int merchantId;
    private String merchantName;
    private String pic;
    private List<InQueueOrder> queueList;
    private List<TableOrderInfo> tableOrderList;

    public InQueueInfo() {
        this.merchantId = 0;
        this.tableOrderList = new ArrayList<TableOrderInfo>();
        this.queueList = new ArrayList<InQueueOrder>();
        this.pic = "";
        this.merchantName = "";
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public int getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(int merchantId) {
        this.merchantId = merchantId;
    }

    public List<InQueueOrder> getQueueList() {
        return queueList;
    }

    public void setQueueList(List<InQueueOrder> queueList) {
        this.queueList = queueList;
    }

    public List<TableOrderInfo> getTableOrderList() {
        return tableOrderList;
    }

    public void setTableOrderList(List<TableOrderInfo> tableOrderList) {
        this.tableOrderList = tableOrderList;
    }
}
