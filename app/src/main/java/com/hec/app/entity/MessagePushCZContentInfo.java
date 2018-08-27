package com.hec.app.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by chejenhu on 12/7/17.
 */
public class MessagePushCZContentInfo {

    @SerializedName("UserId")
    private String mUserId;
    @SerializedName("AvailableScore")
    private String mAvailableScore;

    public String getUserId() {
        return mUserId;
    }

    public void setUserId(String userId) {
        mUserId = userId;
    }

    public String getAvailableScore() {
        return mAvailableScore;
    }

    public void setAvailableScore(String availableScore) {
        mAvailableScore = availableScore;
    }
}
