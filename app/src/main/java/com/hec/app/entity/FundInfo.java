package com.hec.app.entity;

public class FundInfo {
    private Long Balance;
    private Long Bet;
    private Long Moneyin;
    private Long Moneyout;
    private Long Prize;
    private Long WinLoss;

    public Long getBalance() {
        return this.Balance;
    }

    public void setBalance(Long balance) {
        this.Balance = balance;
    }

    public Long getWinLoss() {
        return this.WinLoss;
    }

    public void setWinLoss(Long winLoss) {
        this.WinLoss = winLoss;
    }

    public Long getPrize() {
        return this.Prize;
    }

    public void setPrize(Long prize) {
        this.Prize = prize;
    }

    public Long getBet() {
        return this.Bet;
    }

    public void setBet(Long bet) {
        this.Bet = bet;
    }

    public Long getMoneyin() {
        return this.Moneyin;
    }

    public void setMoneyin(Long moneyin) {
        this.Moneyin = moneyin;
    }

    public Long getMoneyout() {
        return this.Moneyout;
    }

    public void setMoneyout(Long moneyout) {
        this.Moneyout = moneyout;
    }
}
