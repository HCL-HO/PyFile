package com.hec.app.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by wangxingjian on 2016/12/1.
 */

public class NextIssueInfoNew {
    @SerializedName("CurrentIssueNo")
    private String CurrentIssueNo;
    @SerializedName("CurrentLotteryTime")
    private String CurrentLotteryTime;
    @SerializedName("LatesttIssueNo")
    private String LatesttIssueNo;
    @SerializedName("LatesttLotteryNum")
    private String LatesttLotteryNum;
    @SerializedName("LotteryID")
    private int LotteryID;
    @SerializedName("LotteryType")
    private String LotteryType;
    @SerializedName("NextIssueNo")
    private String NextIssueNo;
    @SerializedName("NextLotteryTime")
    private String NextLotteryTime;

    public String getNextIssueNo() {
        return NextIssueNo;
    }

    public void setNextIssueNo(String nextIssueNo) {
        NextIssueNo = nextIssueNo;
    }

    public String getNextLotteryTime() {
        return NextLotteryTime;
    }

    public void setNextLotteryTime(String nextLotteryTime) {
        NextLotteryTime = nextLotteryTime;
    }

    public String getCurrentIssueNo() {
        return CurrentIssueNo;
    }

    public void setCurrentIssueNo(String currentIssueNo) {
        CurrentIssueNo = currentIssueNo;
    }

    public String getCurrentLotteryTime() {
        return CurrentLotteryTime;
    }

    public void setCurrentLotteryTime(String currentLotteryTime) {
        CurrentLotteryTime = currentLotteryTime;
    }

    public String getLatesttIssueNo() {
        return LatesttIssueNo;
    }

    public void setLatesttIssueNo(String latesttIssueNo) {
        LatesttIssueNo = latesttIssueNo;
    }

    public String getLatesttLotteryNum() {
        return LatesttLotteryNum;
    }

    public void setLatesttLotteryNum(String latesttLotteryNum) {
        LatesttLotteryNum = latesttLotteryNum;
    }

    public int getLotteryID() {
        return LotteryID;
    }

    public void setLotteryID(int lotteryID) {
        LotteryID = lotteryID;
    }

    public String getLotteryType() {
        return LotteryType;
    }

    public void setLotteryType(String lotteryType) {
        LotteryType = lotteryType;
    }
}
