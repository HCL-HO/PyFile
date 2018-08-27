package com.hec.app.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hec on 2015/11/12.
 */
public class PlayTypeNumInfo {
    @SerializedName("PlayTypeNumName")
    private String playTypeNumName;
    @SerializedName("PlayTypeId")
    private int playTypeId;
    @SerializedName("PlayTypeRadioId")
    private int playTypeRadioId;

    public PlayTypeNumInfo(String playTypeNumName, int playTypeId, int playTypeRadioId) {
        this.playTypeNumName = playTypeNumName;
        this.playTypeId = playTypeId;
        this.playTypeRadioId = playTypeRadioId;
    }

    public String getPlayTypeNumName() {
        return playTypeNumName;
    }

    public void setPlayTypeNumName(String playTypeNumName) {
        this.playTypeNumName = playTypeNumName;
    }

    public int getPlayTypeId() {
        return playTypeId;
    }

    public void setPlayTypeId(int playTypeId) {
        this.playTypeId = playTypeId;
    }

    public int getPlayTypeRadioId() {
        return playTypeRadioId;
    }

    public void setPlayTypeRadioId(int playTypeRadioId) {
        this.playTypeRadioId = playTypeRadioId;
    }
}
