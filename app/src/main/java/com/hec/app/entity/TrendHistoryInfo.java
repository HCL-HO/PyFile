package com.hec.app.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Isaac on 29/1/2016.
 */
public class TrendHistoryInfo {
    @SerializedName("CurrentLotteryNum")
    private String currentLotteryNum;
    @SerializedName("IssueNo")
    private String issueNo;
    @SerializedName("WinMoney")
    private String winMoney;

    private boolean dummy = false;

    public String getCurrentLotteryNum() {
        return currentLotteryNum;
    }

    public void setCurrentLotteryNum(String currentLotteryNum) {
        this.currentLotteryNum = currentLotteryNum;
    }

    public String getIssueNo() {
        return issueNo;
    }

    public void setIssueNo(String issueNo) {
        this.issueNo = issueNo;
    }

    public String getProfit() {
        if (winMoney == null)
            return "未投注";
        return winMoney;
    }

    public boolean isDummy() {
        return dummy;
    }

    public void setDummy(boolean val) {
        dummy = val;
    }

    public void setWinMoney(String winMoney) {
        this.winMoney = winMoney;
    }
}
