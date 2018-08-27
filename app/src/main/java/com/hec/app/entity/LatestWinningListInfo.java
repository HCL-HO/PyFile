package com.hec.app.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LatestWinningListInfo {
//    @SerializedName("ExtensionData")
//    List<String> extensionData;
    @SerializedName("LotteryTime")
    String lotteryTime;
    @SerializedName("LotteryType")
    String lotteryType;
    @SerializedName("UserName")
    String userName;
    @SerializedName("WinMoney")
    double winMoney;

//    public List<String> getExtensionData() {
//        return extensionData;
//    }
//
//    public void setExtensionData(List<String> extensionData) {
//        this.extensionData = extensionData;
//    }

    public String getLotteryTime() {
        return lotteryTime;
    }

    public void setLotteryTime(String lotteryTime) {
        this.lotteryTime = lotteryTime;
    }

    public String getLotteryType() {
        return lotteryType;
    }

    public void setLotteryType(String lotteryType) {
        this.lotteryType = lotteryType;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public double getWinMoney() {
        return winMoney;
    }

    public void setWinMoney(double winMoney) {
        this.winMoney = winMoney;
    }
}
