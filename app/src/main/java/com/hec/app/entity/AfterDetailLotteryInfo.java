package com.hec.app.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by wangxingjian on 16/2/16.
 */
public class AfterDetailLotteryInfo {

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
    @SerializedName("PalyNum")
    private String PalyNum;
    @SerializedName("PalyCurrentNum")
    private String PalyCurrentNum;
    @SerializedName("PlayTypeName")
    private String PlayTypeName;
    @SerializedName("RestPeriods")
    private int RestPeriods;
    @SerializedName("StopCondition")
    private String StopCondition;
    @SerializedName("TotalPeriods")
    private int TotalPeriods;
    @SerializedName("TotalWin")
    private float TotalWin;
    @SerializedName("WinLoss")
    private float WinLoss;

    public AfterDetailLotteryInfo() {
    }

    public AfterDetailLotteryInfo(int afterNoID, int afterState, String lotteryType,
                                  float orderMoney, String orderTime, String palyNum,
                                  String palyCurrentNum, String playTypeName, int restPeriods,
                                  String stopCondition, int totalPeriods, float totalWin, float winLoss) {
        AfterNoID = afterNoID;
        AfterState = afterState;
        LotteryType = lotteryType;
        OrderMoney = orderMoney;
        OrderTime = orderTime;
        PalyNum = palyNum;
        PalyCurrentNum = palyCurrentNum;
        PlayTypeName = playTypeName;
        RestPeriods = restPeriods;
        StopCondition = stopCondition;
        TotalPeriods = totalPeriods;
        TotalWin = totalWin;
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

    public String getPalyNum() {
        return PalyNum;
    }

    public void setPalyNum(String palyNum) {
        PalyNum = palyNum;
    }

    public String getPalyCurrentNum() {
        return PalyCurrentNum;
    }

    public void setPalyCurrentNum(String palyCurrentNum) {
        PalyCurrentNum = palyCurrentNum;
    }

    public String getPlayTypeName() {
        return PlayTypeName;
    }

    public void setPlayTypeName(String playTypeName) {
        PlayTypeName = playTypeName;
    }

    public int getRestPeriods() {
        return RestPeriods;
    }

    public void setRestPeriods(int restPeriods) {
        RestPeriods = restPeriods;
    }

    public String getStopCondition() {
        return StopCondition;
    }

    public void setStopCondition(String stopCondition) {
        StopCondition = stopCondition;
    }

    public int getTotalPeriods() {
        return TotalPeriods;
    }

    public void setTotalPeriods(int totalPeriods) {
        TotalPeriods = totalPeriods;
    }

    public float getTotalWin() {
        return TotalWin;
    }

    public void setTotalWin(float totalWin) {
        TotalWin = totalWin;
    }

    public float getWinLoss() {
        return WinLoss;
    }

    public void setWinLoss(float winLoss) {
        WinLoss = winLoss;
    }
}
