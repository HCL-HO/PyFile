package com.hec.app.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by hec on 2015/11/17.
 */
public class BasicDataInfo {
    @SerializedName("PlayMode")
    private int playMode;
    @SerializedName("LotteryInfos")
    private List<LotteryInfo> lotteryInfos;
    @SerializedName("PlayTypes")
    private List<PlayTypeInfo> playTypes;
    @SerializedName("PlayTypeRadios")
    private List<PlayTypeRadioInfo> playTypeRadios;
    @SerializedName("PlayTypeNums")
    private List<PlayTypeNumInfo> playTypeNums;
    @SerializedName("SysSettings")
    private SysSettings sysSettings;
    @SerializedName("HashCode")
    private String hashCode;

    public int getPlayMode() {
        return playMode;
    }

    public void setPlayMode(int playMode) {
        this.playMode = playMode;
    }

    public List<LotteryInfo> getLotteryInfos() {
        return lotteryInfos;
    }

    public void setLotteryInfos(List<LotteryInfo> lotteryInfos) {
        this.lotteryInfos = lotteryInfos;
    }

    public List<PlayTypeInfo> getPlayTypes() {
        return playTypes;
    }

    public void setPlayTypes(List<PlayTypeInfo> playTypes) {
        this.playTypes = playTypes;
    }

    public List<PlayTypeRadioInfo> getPlayTypeRadios() {
        return playTypeRadios;
    }

    public void setPlayTypeRadios(List<PlayTypeRadioInfo> playTypeRadios) {
        this.playTypeRadios = playTypeRadios;
    }

    public List<PlayTypeNumInfo> getPlayTypeNums() {
        return playTypeNums;
    }

    public void setPlayTypeNums(List<PlayTypeNumInfo> playTypeNums) {
        this.playTypeNums = playTypeNums;
    }

    public SysSettings getSysSettings() {
        return sysSettings;
    }

    public void setSysSettings(SysSettings sysSettings) {
        this.sysSettings = sysSettings;
    }

    public String getHashCode() {
        return hashCode;
    }

    public void setHashCode(String hashCode) {
        this.hashCode = hashCode;
    }
}
