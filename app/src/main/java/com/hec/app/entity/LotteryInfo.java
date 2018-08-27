package com.hec.app.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hec on 2015/11/12.
 */
public class LotteryInfo {
    @SerializedName("LotteryID")
    private int lotteryID;
    @SerializedName("LotteryType")
    private String lotteryType;
    @SerializedName("TypeUrl")
    private String typeUrl;

    public LotteryInfo(int lotteryID, String lotteryType,String typeUrl) {
        this.lotteryID = lotteryID;
        this.lotteryType = lotteryType;
        this.typeUrl=typeUrl;
    }

    public int getLotteryID() {
        return lotteryID;
    }

    public void setLotteryID(int lotteryID) {
        this.lotteryID = lotteryID;
    }

    public String getLotteryType() {
        return lotteryType;
    }

    public void setLotteryType(String lotteryType) {
        this.lotteryType = lotteryType;
    }

    public String getTypeUrl() {
        return typeUrl;
    }

    public void setTypeUrl(String typeUrl) {
        this.typeUrl = typeUrl;
    }
}
