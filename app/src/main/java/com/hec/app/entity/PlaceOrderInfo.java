package com.hec.app.entity;

import com.google.gson.annotations.SerializedName;
import com.hec.app.util.LotteryUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hec on 2015/12/14.
 */
public class PlaceOrderInfo implements Serializable {
    private static final long serialVersionUID = -7060210544600464481L;

    @SerializedName("LotteryID")
    private int lotteryID;
    @SerializedName("LotteryName")
    private String lotteryName;
    @SerializedName("PlayTypeID")
    private int playTypeID;
    @SerializedName("PlayTypeName")
    private String playTypeName;
    @SerializedName("PlayTypeRadioID")
    private int playTypeRadioID;
    @SerializedName("PlayTypeRadioName")
    private String playTypeRadioName;
    @SerializedName("Price")
    private double price;
    @SerializedName("Qty")
    private int qty;
    @SerializedName("CurrentIssueNo")
    private String currentIssueNo;
    @SerializedName("SelectedNums")
    private String selectedNums;
    @SerializedName("RebatePro")
    private double rebatePro;
    @SerializedName("HabitRebatePro")
    private double habitRebatePro;
    @SerializedName("CustomerBonusPct")
    private double customerBonusPct;
    @SerializedName("AllNumbers")
    private ArrayList<List<String>> allNumbers;
    @SerializedName("Multiple")
    private int multiple;
    @SerializedName("Periods")
    private int periods;
    @SerializedName("IsAfter")
    private boolean isAfter;
    @SerializedName("IsWinStop")
    private boolean isWinStop;
    @SerializedName("RebateProMoney")
    private double rebateProMoney;
    @SerializedName("PlayMode")
    private int playMode;
    @SerializedName("ManualBet")
    private double manualBet;

    public PlaceOrderInfo() {
    }

    public int getLotteryID() {
        return lotteryID;
    }

    public void setLotteryID(int lotteryID) {
        this.lotteryID = lotteryID;
    }

    public String getLotteryName() {
        return lotteryName;
    }

    public void setLotteryName(String lotteryName) {
        this.lotteryName = lotteryName;
    }

    public int getPlayTypeID() {
        return playTypeID;
    }

    public void setPlayTypeID(int playTypeID) {
        this.playTypeID = playTypeID;
    }

    public String getPlayTypeName() {
        return playTypeName;
    }

    public void setPlayTypeName(String playTypeName) {
        this.playTypeName = playTypeName;
    }

    public int getPlayTypeRadioID() {
        return playTypeRadioID;
    }

    public void setPlayTypeRadioID(int playTypeRadioID) {
        this.playTypeRadioID = playTypeRadioID;
    }

    public String getPlayTypeRadioName() {
        return playTypeRadioName;
    }

    public void setPlayTypeRadioName(String playTypeRadioName) {
        this.playTypeRadioName = playTypeRadioName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public double getTotalAmount() {
        double totalamount, temp;
        totalamount = temp = LotteryUtil.getTotalAmount(qty, price);

        for (int i = 1; i < periods; ++i) {
            temp = temp * multiple;
            totalamount = totalamount + temp;
        }

        return totalamount;
    }

    public String getCurrentIssueNo() {
        return currentIssueNo;
    }

    public void setCurrentIssueNo(String currentIssueNo) {
        this.currentIssueNo = currentIssueNo;
    }

    public String getSelectedNums() {
        return selectedNums;
    }

    public void setSelectedNums(String selectedNums) {
        this.selectedNums = selectedNums;
    }

    public double getRebatePro() {
        return rebatePro;
    }

    public void setRebatePro(double rebatePro) {
        this.rebatePro = rebatePro;
    }

    public double getHabitRebatePro() {
        return habitRebatePro;
    }

    public void setHabitRebatePro(double habitRebatePro) {
        this.habitRebatePro = habitRebatePro;
    }

    public double getCustomerBonusPct() {
        return customerBonusPct;
    }

    public void setCustomerBonusPct(double customerBonusPct) {
        this.customerBonusPct = customerBonusPct;
    }

    public ArrayList<List<String>> getAllNumbers() {
        return allNumbers;
    }

    public void setAllNumbers(ArrayList<List<String>> allNumbers) {
        this.allNumbers = allNumbers;
    }

    public int getMultiple() {
        return multiple;
    }

    public void setMultiple(int multiple) {
        this.multiple = multiple;
    }

    public int getPeriods() {
        return periods;
    }

    public void setPeriods(int periods) {
        this.periods = periods;
    }

    public boolean isAfter() {
        return isAfter;
    }

    public void setIsAfter(boolean isAfter) {
        this.isAfter = isAfter;
    }

    public boolean isWinStop() {
        return isWinStop;
    }

    public void setIsWinStop(boolean isWinStop) {
        this.isWinStop = isWinStop;
    }

    public double getRebateProMoney() {
        return rebateProMoney;
    }

    public void setRebateProMoney(double rebateProMoney) {
        this.rebateProMoney = rebateProMoney;
    }

    public int getPlayMode() {
        return playMode;
    }

    public void setPlayMode(int playMode) {
        this.playMode = playMode;
    }

    public double getManualBet() {
        return manualBet;
    }

    public void setManualBet(double manualBet) {
        this.manualBet = manualBet;
    }
}
