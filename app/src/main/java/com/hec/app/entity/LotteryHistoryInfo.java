package com.hec.app.entity;

/**
 * Created by hec on 2015/11/13.
 */
public class LotteryHistoryInfo {
    private String issueNo;
    private String LotteryNumbers;
    private String beforeThree;
    private String afterThree;

    public LotteryHistoryInfo(String issueNo, String lotteryNumbers, String beforeThree, String afterThree) {
        this.issueNo = issueNo;
        LotteryNumbers = lotteryNumbers;
        this.beforeThree = beforeThree;
        this.afterThree = afterThree;
    }

    public String getIssueNo() {
        return issueNo;
    }

    public void setIssueNo(String issueNo) {
        this.issueNo = issueNo;
    }

    public String getLotteryNumbers() {
        return LotteryNumbers;
    }

    public void setLotteryNumbers(String lotteryNumbers) {
        LotteryNumbers = lotteryNumbers;
    }

    public String getBeforeThree() {
        return beforeThree;
    }

    public void setBeforeThree(String beforeThree) {
        this.beforeThree = beforeThree;
    }

    public String getAfterThree() {
        return afterThree;
    }

    public void setAfterThree(String afterThree) {
        this.afterThree = afterThree;
    }
}
