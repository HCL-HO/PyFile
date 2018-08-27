package com.hec.app.entity;

/**
 * Created by asianark on 22/3/16.
 */
public class Trace {
    private int lotteryId;
    private int playTypeId;
    private int playTypeRadioId;
    private int playMode;

    public Trace(int lotteryId, int playTypeId, int playTypeRadioId, int playMode) {
        this.lotteryId = lotteryId;
        this.playTypeId = playTypeId;
        this.playTypeRadioId = playTypeRadioId;
        this.playMode = playMode;
    }

    public int getPlayTypeRadioId() {

        return playTypeRadioId;
    }

    public void setPlayTypeRadioId(int playTypeRadioId) {
        this.playTypeRadioId = playTypeRadioId;
    }

    public int getPlayTypeId() {
        return playTypeId;
    }

    public void setPlayTypeId(int playTypeId) {
        this.playTypeId = playTypeId;
    }

    public int getLotteryId() {
        return lotteryId;
    }

    public void setLotteryId(int lotteryId) {
        this.lotteryId = lotteryId;
    }

    public int getPlayMode() {
        return playMode;
    }

    public void setPlayMode(int playMode) {
        this.playMode = playMode;
    }
}
