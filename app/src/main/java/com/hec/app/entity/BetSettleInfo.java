package com.hec.app.entity;

/**
 * Created by hec on 2015/12/16.
 */
public class BetSettleInfo {
    private int lotteryID;
    private String lotteryName;
    private int playTypeID;
    private String playTypeName;
    private int playTypeRadioID;
    private String playTypeRadioName;
    private double price;
    private int amount;
    private String selectedNums;
    private double manualBet;
    private String unit;


    public BetSettleInfo() {
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

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getSelectedNums() {
        return selectedNums;
    }

    public void setSelectedNums(String selectedNums) {
        this.selectedNums = selectedNums;
    }

    public double getManualBet() {
        return manualBet;
    }

    public void setManualBet(double manualBet) {
        this.manualBet = manualBet;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
