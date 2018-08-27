package com.hec.app.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by chejenhu on 12/7/17.
 */
public class MessagePushTXContentInfo {

    @SerializedName("Summary")
    private String mSummary;
    @SerializedName("UserId")
    private String mUserId;
    @SerializedName("Amount")
    private String mAmount;

    public String getSummary() {
        return mSummary;
    }

    public void setSummary(String summary) {
        mSummary = summary;
    }

    public String getUserId() {
        return mUserId;
    }

    public void setUserId(String userId) {
        mUserId = userId;
    }

    public String getAmount() {
        return mAmount;
    }

    public void setAmount(String amount) {
        mAmount = amount;
    }
}
