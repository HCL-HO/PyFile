package com.hec.app.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hec on 2015/11/11.
 */
public class PlayTypeInfo {
    @SerializedName("PlayTypeID")
    private int playTypeID;
    @SerializedName("LotteryID")
    private int lotteryID;
    @SerializedName("PlayTypeName")
    private String playTypeName;

    public PlayTypeInfo(){

    }

    public PlayTypeInfo(int playTypeID, int lotteryID, String playTypeName) {
        this.playTypeID = playTypeID;
        this.lotteryID = lotteryID;
        this.playTypeName = playTypeName;
    }

    public int getPlayTypeID() {
        return playTypeID;
    }

    public void setPlayTypeID(int playTypeID) {
        this.playTypeID = playTypeID;
    }

    public int getLotteryID() {
        return lotteryID;
    }

    public void setLotteryID(int lotteryID) {
        this.lotteryID = lotteryID;
    }

    public String getPlayTypeName() {
        return playTypeName;
    }

    public void setPlayTypeName(String playTypeName) {
        this.playTypeName = playTypeName;
    }
}
