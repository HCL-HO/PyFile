package com.hec.app.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by wangxingjian on 16/2/16.
 */
public class AfterLotteryInfo {
    @SerializedName("AfterNoID")
    private int AfterNoID;
    @SerializedName("AfterState")
    private int AfterState;
    @SerializedName("LotteryType")
    private String LotteryType;
    @SerializedName("OrderMoney")
    private float OrderMoney;
    @SerializedName("OrderTime")
    private String OrderTime;
    @SerializedName("RestPeriods")
    private int RestPeriods;
    @SerializedName("StartCurrentNum")
    private int StartCurrentNum;
    @SerializedName("TotalPeriods")
    private int TotalPeriods;
    @SerializedName("WinLoss")
    private float WinLoss;

    public AfterLotteryInfo(int afterNoID, int afterState, String lotteryType,
                            float orderMoney, String orderTime, int restPeriods,
                            int startCurrentNum, int totalPeriods, float winLoss) {
        AfterNoID = afterNoID;
        AfterState = afterState;
        LotteryType = lotteryType;
        OrderMoney = orderMoney;
        OrderTime = orderTime;
        RestPeriods = restPeriods;
        StartCurrentNum = startCurrentNum;
        TotalPeriods = totalPeriods;
        WinLoss = winLoss;
    }

    public int getAfterNoID() {
        return AfterNoID;
    }

    public void setAfterNoID(int afterNoID) {
        AfterNoID = afterNoID;
    }

    public int getAfterState() {
        return AfterState;
    }

    public void setAfterState(int afterState) {
        AfterState = afterState;
    }

    public String getLotteryType() {
        return LotteryType;
    }

    public void setLotteryType(String lotteryType) {
        LotteryType = lotteryType;
    }

    public float getOrderMoney() {
        return OrderMoney;
    }

    public void setOrderMoney(float orderMoney) {
        OrderMoney = orderMoney;
    }

    public String getOrderTime() {
        return OrderTime;
    }

    public void setOrderTime(String orderTime) {
        OrderTime = orderTime;
    }

    public int getRestPeriods() {
        return RestPeriods;
    }

    public void setRestPeriods(int restPeriods) {
        RestPeriods = restPeriods;
    }

    public int getStartCurrentNum() {
        return StartCurrentNum;
    }

    public void setStartCurrentNum(int startCurrentNum) {
        StartCurrentNum = startCurrentNum;
    }

    public int getTotalPeriods() {
        return TotalPeriods;
    }

    public void setTotalPeriods(int totalPeriods) {
        TotalPeriods = totalPeriods;
    }

    public float getWinLoss() {
        return WinLoss;
    }

    public void setWinLoss(float winLoss) {
        WinLoss = winLoss;
    }
}
