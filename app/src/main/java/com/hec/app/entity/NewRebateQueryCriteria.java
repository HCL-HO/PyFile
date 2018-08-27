package com.hec.app.entity;

/**
 * Created by hec on 2015/12/8.
 */
public class NewRebateQueryCriteria {
    private int playTypeID;
    private int playTypeRadioID;
    private String playTypeName;
    private String playTypeRadioName;

    public NewRebateQueryCriteria(int playTypeID, int playTypeRadioID, String playTypeName, String playTypeRadioName) {
        this.playTypeID = playTypeID;
        this.playTypeRadioID = playTypeRadioID;
        this.playTypeName = playTypeName;
        this.playTypeRadioName = playTypeRadioName;
    }

    public int getPlayTypeID() {
        return playTypeID;
    }

    public void setPlayTypeID(int playTypeID) {
        this.playTypeID = playTypeID;
    }

    public int getPlayTypeRadioID() {
        return playTypeRadioID;
    }

    public void setPlayTypeRadioID(int playTypeID) {
        this.playTypeRadioID = playTypeRadioID;
    }

    public String getPlayTypeName() {
        return playTypeName;
    }

    public void setPlayTypeName(String playTypeName) {
        this.playTypeName = playTypeName;
    }

    public String getPlayTypeRadioName() {
        return playTypeRadioName;
    }

    public void setPlayTypeRadioName(String playTypeRadioName) {
        this.playTypeRadioName = playTypeRadioName;
    }
}
