package com.hec.app.entity;

import com.google.gson.annotations.SerializedName;
import com.hec.app.util.TestUtil;

import java.util.Date;

/**
 * Created by wangxingjian on 16/2/4.
 */
public class DetailLotteryInfo {
    @SerializedName("CurrentLotteryNum")
    private String CurrentLotteryNum;
    @SerializedName("LotteryType")
    private String LotteryType;
    @SerializedName("NoteMoney")
    private double NoteMoney;
    @SerializedName("NoteNum")
    private int NoteNum;
    @SerializedName("NoteTime")
    private String NoteTime;
    @SerializedName("PalyCurrentNum")
    private String PalyCurrentNum;
    @SerializedName("PalyNum")
    private String PalyNum;
    @SerializedName("OrderID")
    private int OrderID;
    @SerializedName("PlayTypeName")
    private String PlayTypeName;
    @SerializedName("RebatePro")
    private double RebatePro;
    @SerializedName("RebateProMoney")
    private String RebateProMoney;
    @SerializedName("SingleMoney")
    private float SingleMoney;
    @SerializedName("OrderState")
    private int OrderState;
    @SerializedName("WinMoney")
    private float WinMoney;
    @SerializedName("WinNum")
    private int WinNum;

    public DetailLotteryInfo() {
    }

    public DetailLotteryInfo(String currentLotteryNum, String lotteryType,
                             float noteMoney, int noteNum, String noteTime,
                             String palyCurrentNum, String palyNum, int orderID,
                             String playTypeName, float rebatePro, String rebateProMoney,
                             float singleMoney, int orderState, float winMoney) {
        CurrentLotteryNum = currentLotteryNum;
        LotteryType = lotteryType;
        NoteMoney = noteMoney;
        NoteNum = noteNum;
        NoteTime = noteTime;
        PalyCurrentNum = palyCurrentNum;
        PalyNum = palyNum;
        OrderID = orderID;
        PlayTypeName = playTypeName;
        RebatePro = rebatePro;
        RebateProMoney = rebateProMoney;
        SingleMoney = singleMoney;
        OrderState = orderState;
        WinMoney = winMoney;
    }

    public String getCurrentLotteryNum() {
        return CurrentLotteryNum;
    }

    public void setCurrentLotteryNum(String currentLotteryNum) {
        CurrentLotteryNum = currentLotteryNum;
    }

    public String getLotteryType() {
        return LotteryType;
    }

    public void setLotteryType(String lotteryType) {
        LotteryType = lotteryType;
    }

    public double getNoteMoney() {
        return NoteMoney;
    }

    public void setNoteMoney(float noteMoney) {
        NoteMoney = noteMoney;
    }

    public int getNoteNum() {
        return NoteNum;
    }

    public void setNoteNum(int noteNum) {
        NoteNum = noteNum;
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

    public String getPalyNum() {
        return PalyNum;
    }

    public void setPalyNum(String palyNum) {
        PalyNum = palyNum;
    }

    public int getOrderID() {
        return OrderID;
    }

    public void setOrderID(int orderID) {
        OrderID = orderID;
    }

    public String getPlayTypeName() {
        return PlayTypeName;
    }

    public void setPlayTypeName(String playTypeName) {
        PlayTypeName = playTypeName;
    }

    public double getRebatePro() {
        return RebatePro;
    }

    public void setRebatePro(float rebatePro) {
        RebatePro = rebatePro;
    }

    public String getRebateProMoney() {
        return RebateProMoney;
    }

    public void setRebateProMoney(String rebateProMoney) {
        RebateProMoney = rebateProMoney;
    }

    public float getSingleMoney() {
        return SingleMoney;
    }

    public void setSingleMoney(float singleMoney) {
        SingleMoney = singleMoney;
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

    public int getWinNum() {
        return WinNum;
    }

    public void setWinNum(int winNum) {
        WinNum = winNum;
    }
}
