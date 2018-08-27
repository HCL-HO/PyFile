package com.hec.app.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hec on 2015/12/8.
 */
public class LotteryDrawResultInfo {
    @SerializedName("LotteryTypeName")
    private String lotteryTypeName ;
    @SerializedName("LotteryNumbers")
    private int lotteryNumbers ;
    @SerializedName("LatestTime")
    private LotteryModel latestTime ;
    @SerializedName("CurrentTime")
    private LotteryModel currentTime ;

    public String getLotteryTypeName() {
        return lotteryTypeName;
    }

    public void setLotteryTypeName(String lotteryTypeName) {
        this.lotteryTypeName = lotteryTypeName;
    }

    public int getLotteryNumbers() {
        return lotteryNumbers;
    }

    public void setLotteryNumbers(int lotteryNumbers) {
        this.lotteryNumbers = lotteryNumbers;
    }

    public LotteryModel getLatestTime() {
        return latestTime;
    }

    public void setLatestTime(LotteryModel latestTime) {
        this.latestTime = latestTime;
    }

    public LotteryModel getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(LotteryModel currentTime) {
        this.currentTime = currentTime;
    }
}
