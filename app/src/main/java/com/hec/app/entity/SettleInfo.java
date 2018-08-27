package com.hec.app.entity;

import java.util.List;

/**
 * Created by jhezenhu on 2017/5/4.
 */

public class SettleInfo {
    List<String> ballList;
    double manualBet;
    int bets;
    List<String> unitList;

    public List<String> getBallList() {
        return ballList;
    }

    public void setBallList(List<String> ballList) {
        this.ballList = ballList;
    }

    public double getManualBet() {
        return manualBet;
    }

    public void setManualBet(double manualBet) {
        this.manualBet = manualBet;
    }

    public int getBets() {
        return bets;
    }

    public void setBets(int bets) {
        this.bets = bets;
    }

    public List<String> getUnitList() {
        return unitList;
    }

    public void setUnitList(List<String> unitList) {
        this.unitList = unitList;
    }
}
