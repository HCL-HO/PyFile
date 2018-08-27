package com.hec.app.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hec on 2015/11/13.
 */
public class CurrentLotteryInfo {
    @SerializedName("CurrentLotteryNum")
    private String currentLotteryNum;
    @SerializedName("IsLottery")
    private boolean isLottery;
    @SerializedName("CurrentLotteryTimeStr")
    private String currentLotteryTimeStr;
    @SerializedName("IssueNo")
    private String issueNo;

    public String getCurrentLotteryNum() {
        return currentLotteryNum;
    }

    public void setCurrentLotteryNum(String currentLotteryNum) {
        this.currentLotteryNum = currentLotteryNum;
    }

    public boolean isLottery() {
        return isLottery;
    }

    public void setIsLottery(boolean isLottery) {
        this.isLottery = isLottery;
    }

    public String getCurrentLotteryTimeStr() {
        return currentLotteryTimeStr;
    }

    public void setCurrentLotteryTimeStr(String currentLotteryTime) {
        this.currentLotteryTimeStr = currentLotteryTime;
    }

    public String getIssueNo() {
        return issueNo;
    }

    public void setIssueNo(String issueNo) {
        this.issueNo = issueNo;
    }
}
