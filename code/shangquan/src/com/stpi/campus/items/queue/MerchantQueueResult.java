package com.stpi.campus.items.queue;

import java.util.List;

/**
 * Created by Administrator on 13-12-13.
 */
public class MerchantQueueResult {

    private List<TableOrderInfo> currentOrder;

    private List<UserInQueueInfo> result;

    public List<TableOrderInfo> getCurrentOrder() {
        return currentOrder;
    }

    public void setCurrentOrder(List<TableOrderInfo> currentOrder) {
        this.currentOrder = currentOrder;
    }

    public List<UserInQueueInfo> getResult() {
        return result;
    }

    public void setResult(List<UserInQueueInfo> result) {
        this.result = result;
    }
}
