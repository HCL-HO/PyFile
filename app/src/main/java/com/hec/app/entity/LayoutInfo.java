package com.hec.app.entity;

import java.util.List;

/**
 * Created by hec on 2015/11/12.
 */
public class LayoutInfo {
    private int minNumber;
    private int maxNumber;
    private List<PlayTypeNumInfo> playTypeNums;
    private LotteryInfo lotteryInfo;
    private PlayTypeInfo playTypeInfo;
    private PlayTypeRadioInfo playTypeRadioInfo;
    public String a;

    public LayoutInfo(int minNumber, int maxNumber, List<PlayTypeNumInfo> playTypeNums, LotteryInfo lotteryInfo, PlayTypeInfo playTypeInfo, PlayTypeRadioInfo playTypeRadioInfo) {
        this.minNumber = minNumber;
        this.maxNumber = maxNumber;
        this.playTypeNums = playTypeNums;
        this.lotteryInfo = lotteryInfo;
        this.playTypeInfo = playTypeInfo;
        this.playTypeRadioInfo = playTypeRadioInfo;
    }

    public int getMinNumber() {
        return minNumber;
    }

    public void setMinNumber(int minNumber) {
        this.minNumber = minNumber;
    }

    public int getMaxNumber() {
        return maxNumber;
    }

    public void setMaxNumber(int maxNumber) {
        this.maxNumber = maxNumber;
    }

    public List<PlayTypeNumInfo> getPlayTypeNums() {
        return playTypeNums;
    }

    public void setPlayTypeNums(List<PlayTypeNumInfo> playTypeNums) {
        this.playTypeNums = playTypeNums;
    }

    public LotteryInfo getLotteryInfo() {
        return lotteryInfo;
    }

    public void setLotteryInfo(LotteryInfo lotteryInfo) {
        this.lotteryInfo = lotteryInfo;
    }

    public PlayTypeInfo getPlayTypeInfo() {
        return playTypeInfo;
    }

    public void setPlayTypeInfo(PlayTypeInfo playTypeInfo) {
        this.playTypeInfo = playTypeInfo;
    }

    public PlayTypeRadioInfo getPlayTypeRadioInfo() {
        return playTypeRadioInfo;
    }

    public void setPlayTypeRadioInfo(PlayTypeRadioInfo playTypeRadioInfo) {
        this.playTypeRadioInfo = playTypeRadioInfo;
    }
}
