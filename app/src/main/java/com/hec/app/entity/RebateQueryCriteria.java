package com.hec.app.entity;

/**
 * Created by hec on 2015/12/8.
 */
public class RebateQueryCriteria {
    private int playTypeID;
    private String playTypeName;
    private String playTypeRadioName;

    public RebateQueryCriteria(int playTypeID, String playTypeName, String playTypeRadioName) {
        this.playTypeID = playTypeID;
        this.playTypeName = playTypeName;
        this.playTypeRadioName = playTypeRadioName;
    }

    public int getPlayTypeID() {
        return playTypeID;
    }

    public void setPlayTypeID(int playTypeID) {
        this.playTypeID = playTypeID;
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
