package com.stpi.campus.entity.item;

/**
 * Created by cyc on 13-12-13.
 */
public class DetailItemInfoHelper {

    private PreviewItemInfo result;

    private String state;

    public DetailItemInfoHelper() {
        this.result = new PreviewItemInfo();
        this.state = "fail";
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public PreviewItemInfo getResult() {
        return result;
    }

    public void setResult(PreviewItemInfo result) {
        this.result = result;
    }
}
