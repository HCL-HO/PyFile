package com.hec.app.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hec on 2015/11/11.
 */
public class PlayTypeRadioInfo {
    @SerializedName("PlayTypeRadioID")
    private int playTypeRadioID;
    @SerializedName("PlayTypeRadioName")
    private String playTypeRadioName;
    @SerializedName("PlayTypeID")
    private int playTypeID;
    @SerializedName("PlayDescription")
    private String playDescription;
    @SerializedName("WinExample")
    private String winExample;
    @SerializedName("PlayTypeModel")
    private String playTypeModel;

    public String getWinExample() {
        return winExample;
    }

    public void setWinExample(String winExample) {
        this.winExample = winExample;
    }

    public PlayTypeRadioInfo(int playTypeRadioID, String playTypeRadioName, int playTypeID, String playDescription,String WinExample) {
        this.playTypeRadioID = playTypeRadioID;
        this.playTypeRadioName = playTypeRadioName;
        this.playTypeID = playTypeID;
        this.playDescription = playDescription;
        this.winExample = WinExample;
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

    public int getPlayTypeID() {
        return playTypeID;
    }

    public void setPlayTypeID(int playTypeID) {
        this.playTypeID = playTypeID;
    }

    public String getPlayDescription() {
        return playDescription;
    }

    public void setPlayDescription(String playDescription) {
        this.playDescription = playDescription;
    }

    public String getPlayTypeModel() {
        return playTypeModel;
    }

    public void setPlayTypeModel(String playTypeModel) {
        this.playTypeModel = playTypeModel;
    }
}
