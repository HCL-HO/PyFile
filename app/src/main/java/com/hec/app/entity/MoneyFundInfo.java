package com.hec.app.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by wangxingjian on 16/2/18.
 */
public class MoneyFundInfo {
    @SerializedName("Balance")
    private double Balance;
    @SerializedName("WinLoss")
    private double WinLoss;
    @SerializedName("Prize")
    private double Prize;
    @SerializedName("Bet")
    private double Bet;
    @SerializedName("Moneyin")
    private double Moneyin;
    @SerializedName("Moneyout")
    private double Moneyout;

    public MoneyFundInfo(float balance, int winLoss, int prize, int bet, float moneyin, float moneyout) {
        Balance = balance;
        WinLoss = winLoss;
        Prize = prize;
        Bet = bet;
        Moneyin = moneyin;
        Moneyout = moneyout;
    }

    public double getBalance() {
        return Balance;
    }

    public void setBalance(float balance) {
        Balance = balance;
    }

    public double getWinLoss() {
        return WinLoss;
    }

    public void setWinLoss(int winLoss) {
        WinLoss = winLoss;
    }

    public double getPrize() {
        return Prize;
    }

    public void setPrize(int prize) {
        Prize = prize;
    }

    public double getBet() {
        return Bet;
    }

    public void setBet(int bet) {
        Bet = bet;
    }

    public double getMoneyin() {
        return Moneyin;
    }

    public void setMoneyin(float moneyin) {
        Moneyin = moneyin;
    }

    public double getMoneyout() {
        return Moneyout;
    }

    public void setMoneyout(float moneyout) {
        Moneyout = moneyout;
    }
}