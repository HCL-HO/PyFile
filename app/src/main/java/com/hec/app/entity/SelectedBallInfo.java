package com.hec.app.entity;

import java.util.List;

/**
 * Created by jhezenhu on 2017/5/3.
 */

public class SelectedBallInfo {
    private List<String> mSelectedBall;
    private double mBet;

    public List<String> getSelectedBall() {
        return mSelectedBall;
    }

    public void setSelectedBall(List<String> selectedBall) {
        this.mSelectedBall = selectedBall;
    }

    public double getBet() {
        return mBet;
    }

    public void setBet(double bet) {
        this.mBet = bet;
    }
}
