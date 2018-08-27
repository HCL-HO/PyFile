package com.hec.app.entity;

/**
 * Created by wangxingjian on 2016/12/2.
 */

public class LogonInfoNew {
    private String userName;
    private int userID;
    private String key;
    private Boolean isInfoComplete;
    private String currentTime;
    private BalanceInfo balanceInfo;
    private BestPrizeInfo userBestPrize;
    private Boolean isVIP;
    private String bankShow;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Boolean getInfoComplete() {
        return isInfoComplete;
    }

    public void setInfoComplete(Boolean infoComplete) {
        isInfoComplete = infoComplete;
    }

    public String getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }

    public BalanceInfo getBalanceInfo() {
        return balanceInfo;
    }

    public void setBalanceInfo(BalanceInfo balanceInfo) {
        this.balanceInfo = balanceInfo;
    }

    public BestPrizeInfo getUserBestPrize() {
        return userBestPrize;
    }

    public void setUserBestPrize(BestPrizeInfo userBestPrize) {
        this.userBestPrize = userBestPrize;
    }

    public Boolean getVIP() {
        return isVIP;
    }

    public void setVIP(Boolean VIP) {
        isVIP = VIP;
    }

    public String getBankShow() {
        return bankShow;
    }

    public void setBankShow(String bankShow) {
        this.bankShow = bankShow;
    }
}
