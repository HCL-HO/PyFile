package com.hec.app.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by chejenhu on 12/7/17.
 */
public class MessagePushTransferNoticeContentInfo {

    @SerializedName("Summary")
    private String mSummary;
    @SerializedName("UserId")
    private String mUserId;

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
}
