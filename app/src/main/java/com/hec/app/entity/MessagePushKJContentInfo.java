package com.hec.app.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by chejenhu on 12/7/17.
 */
public class MessagePushKJContentInfo {

    @SerializedName("AvailableScore")
    private float mAvailableScore;
    @SerializedName("FrozenScore")
    private float mFrozenScore;
    @SerializedName("ProfitAndLossScore")
    private float mProfitAndLossScore;
    @SerializedName("Summary")
    private String mSummary;
    @SerializedName("UserId")
    private String mUserId;
    @SerializedName("FromClient")
    private String mFromClient;

    public float getAvailableScore() {
        return mAvailableScore;
    }

    public void setAvailableScore(float availableScore) {
        mAvailableScore = availableScore;
    }

    public float getFrozenScore() {
        return mFrozenScore;
    }

    public void setFrozenScore(float frozenScore) {
        mFrozenScore = frozenScore;
    }

    public float getProfitAndLossScore() {
        return mProfitAndLossScore;
    }

    public void setProfitAndLossScore(float profitAndLossScore) {
        mProfitAndLossScore = profitAndLossScore;
    }

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

    public String getFromClient() {
        return mFromClient;
    }

    public void setFromClient(String fromClient) {
        mFromClient = fromClient;
    }
}
