package com.stpi.campus.entity.merchant;

/**
 * Created by cyc on 13-12-27.
 */
public class SubscribeMerchantInfo {

    private Integer merchantId;
    private String content;
    private String fromdate;
    private String todate;
    private String picture;
    private String merchantName;

    public SubscribeMerchantInfo() {
        this.merchantId = 0;
        this.content = "";
        this.fromdate = "";
        this.todate = "";
        this.picture = "";
        this.merchantName = "";
    }

    public Integer getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Integer merchantId) {
        this.merchantId = merchantId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFromdate() {
        return fromdate;
    }

    public void setFromdate(String fromdate) {
        this.fromdate = fromdate;
    }

    public String getTodate() {
        return todate;
    }

    public void setTodate(String todate) {
        this.todate = todate;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

}
