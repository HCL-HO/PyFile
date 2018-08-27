package com.hec.app.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Isaac on 25/5/2016.
 */
public class MMCInfo {
    @SerializedName("issueno")
    String issueNo;
    @SerializedName("itype")
    int iType;
    @SerializedName("multiple")
    int multiple;
    @SerializedName("amount")
    Double amount;
    @SerializedName("count")
    int count;
    @SerializedName("state")
    int state;

    public MMCInfo() {

    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public MMCInfo(String issueNo, int iType, int multiple, Double amount, int count, int state) {
        this.issueNo = issueNo;
        this.iType = iType;
        this.multiple = multiple;
        this.amount = amount;
        this.count = count;
        this.state = state;
    }

    public int getCount() {
        return count;

    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public MMCInfo (String issueNo, int iType, int multiple) {

        this.issueNo = issueNo;
        this.iType = iType;
        this.multiple = multiple;
    }

    public void setIssueNo (String val) {
        issueNo = val;
    }

    public String getIssueNo() {
        return issueNo;
    }

    public void setiType (int val) {
        iType = val;
    }

    public int getiType() {
        return iType;
    }

    public void setMultiple (int val) {
        multiple = val;
    }

    public int getMultiple() {
        return multiple;
    }
}
