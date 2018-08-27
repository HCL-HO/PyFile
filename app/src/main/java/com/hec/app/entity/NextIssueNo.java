package com.hec.app.entity;

/**
 * Created by hec on 2015/11/13.
 */
public class NextIssueNo {
    private String issueNo;
    private int totalSeconds;
    private String lotteryResult;


    public NextIssueNo(String issueNo, int totalSeconds, String lotteryResult) {
        this.issueNo = issueNo;
        this.totalSeconds = totalSeconds;
        this.lotteryResult = lotteryResult;
    }

    public String getIssueNo() {
        return issueNo;
    }

    public void setIssueNo(String issueNo) {
        this.issueNo = issueNo;
    }

    public int getTotalSeconds() {
        return totalSeconds;
    }

    public void setTotalSeconds(int totalSeconds) {
        this.totalSeconds = totalSeconds;
    }

    public String getLotteryResult() {
        return lotteryResult;
    }

    public void setLotteryResult(String lotteryResult) {
        this.lotteryResult = lotteryResult;
    }
}
