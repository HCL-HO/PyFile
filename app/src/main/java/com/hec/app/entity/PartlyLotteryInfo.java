package com.hec.app.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by wangxingjian on 16/2/4.
 */
public class PartlyLotteryInfo {
    @SerializedName("LotteryType")
    private String LotteryType;
    @SerializedName("NoteMoney")
    private float NoteMoney;
    @SerializedName("NoteTime")
    private String NoteTime;
    @SerializedName("PalyCurrentNum")
    private String PalyCurrentNum;
    @SerializedName("OrderID")
    private int OrderID;
    @SerializedName("OrderState")
    private int OrderState;
    @SerializedName("WinMoney")
    private float WinMoney;
    @SerializedName("PlayTypeName")
    private String PlayTypeName;

    public PartlyLotteryInfo(String lotteryType, float noteMoney,
                             String noteTime, String palyCurrentNum,
                             int orderID, int orderState, float winMoney, String playTypeName) {
        LotteryType = lotteryType;
        NoteMoney = noteMoney;
        NoteTime = noteTime;
        PalyCurrentNum = palyCurrentNum;
        OrderID = orderID;
        OrderState = orderState;
        WinMoney = winMoney;
        PlayTypeName = playTypeName;
    }

    public String getLotteryType() {
        return LotteryType;
    }

    public void setLotteryType(String lotteryType) {
        LotteryType = lotteryType;
    }

    public float getNoteMoney() {
        return NoteMoney;
    }

    public void setNoteMoney(float noteMoney) {
        NoteMoney = noteMoney;
    }

    public String getNoteTime() {
        return NoteTime;
    }

    public void setNoteTime(String noteTime) {
        NoteTime = noteTime;
    }

    public String getPalyCurrentNum() {
        return PalyCurrentNum;
    }

    public void setPalyCurrentNum(String palyCurrentNum) {
        PalyCurrentNum = palyCurrentNum;
    }

    public int getOrderID() {
        return OrderID;
    }

    public void setOrderID(int orderID) {
        OrderID = orderID;
    }

    public int getOrderState() {
        return OrderState;
    }

    public void setOrderState(int orderState) {
        OrderState = orderState;
    }

    public float getWinMoney() {
        return WinMoney;
    }

    public void setWinMoney(float winMoney) {
        WinMoney = winMoney;
    }

    public String getPlayTypeName() {
        return PlayTypeName;
    }

    public void setPlayTypeName(String playTypeName) {
        PlayTypeName = playTypeName;
    }
}
