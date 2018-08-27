package com.hec.app.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hec on 2015/10/27.
 */
public class SysSettings {
    @SerializedName("MaxMoneyOut")
    private double maxMoneyOut;
    @SerializedName("MaxMoneyOutCount")
    private double maxMoneyOutCount;
    @SerializedName("MinMoneyOut")
    private double minMoneyOut;
    @SerializedName("MaxBetCount")
    private double maxBetCount;
    @SerializedName("MaxBonusMoney")
    private double maxBonusMoney;
    @SerializedName("MaxOneBetMoney")
    private double maxOneBetMoney;
    @SerializedName("MinOneBetMoney")
    private double minOneBetMoney;
    @SerializedName("MinMoneyIn")
    private double minMoneyIn;

    public double getMaxMoneyOut() {
        return maxMoneyOut;
    }

    public void setMaxMoneyOut(double maxMoneyOut) {
        this.maxMoneyOut = maxMoneyOut;
    }

    public double getMaxMoneyOutCount() {
        return maxMoneyOutCount;
    }

    public void setMaxMoneyOutCount(double maxMoneyOutCount) {
        this.maxMoneyOutCount = maxMoneyOutCount;
    }

    public double getMinMoneyOut() {
        return minMoneyOut;
    }

    public void setMinMoneyOut(double minMoneyOut) {
        this.minMoneyOut = minMoneyOut;
    }

    public double getMaxBetCount() {
        return maxBetCount;
    }

    public void setMaxBetCount(double maxBetCount) {
        this.maxBetCount = maxBetCount;
    }

    public double getMaxBonusMoney() {
        return maxBonusMoney;
    }

    public void setMaxBonusMoney(double maxBonusMoney) {
        this.maxBonusMoney = maxBonusMoney;
    }

    public double getMaxOneBetMoney() {
        return maxOneBetMoney;
    }

    public void setMaxOneBetMoney(double maxOneBetMoney) {
        this.maxOneBetMoney = maxOneBetMoney;
    }

    public double getMinOneBetMoney() {
        return minOneBetMoney;
    }

    public void setMinOneBetMoney(double minOneBetMoney) {
        this.minOneBetMoney = minOneBetMoney;
    }

    public double getMinMoneyIn() {
        return minMoneyIn;
    }

    public void setMinMoneyIn(double minMoneyIn) {
        this.minMoneyIn = minMoneyIn;
    }
}
