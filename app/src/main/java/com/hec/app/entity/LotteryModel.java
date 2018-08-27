package com.hec.app.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hec on 2015/12/8.
 */
public class LotteryModel {
    @SerializedName("IssueNo")
    private String issueNo;
    @SerializedName("CurrentLotteryNum")
    private String currentLotteryNum;
    @SerializedName("CurrentLotteryTimeStr")
    private String currentLotteryTime ;
    @SerializedName("IsLottery")
    private boolean isLottery;
    @SerializedName("CurrentTimeStr")
    private String currentTime;

    public String getIssueNo() {
        return issueNo;
    }

    public void setIssueNo(String issueNo) {
        this.issueNo = issueNo;
    }

    public String getCurrentLotteryNum() {
        return currentLotteryNum;
    }

    public void setCurrentLotteryNum(String currentLotteryNum) {
        this.currentLotteryNum = currentLotteryNum;
    }

    public String getCurrentLotteryTime() {
        return currentLotteryTime;
    }

    public void setCurrentLotteryTime(String currentLotteryTime) {
        this.currentLotteryTime = currentLotteryTime;
    }

    public boolean getIsLottery() {
        return isLottery;
    }

    public void setIsLottery(boolean isLottery) {
        this.isLottery = isLottery;
    }

    public String getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }
}
