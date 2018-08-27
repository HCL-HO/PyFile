package com.hec.app.entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CustomerServiceInfo implements Serializable {

    @SerializedName("specificDate")
    private String date;
    @SerializedName("isFirstTimeOpen")
    private boolean isFirstTimeOpen;
    @SerializedName("CurrentMessageID")
    private int currentMessageID;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isFirstTimeOpen() {
        return isFirstTimeOpen;
    }

    public void setFirstTimeOpen(boolean firstTimeOpen) {
        isFirstTimeOpen = firstTimeOpen;
    }

    public int getCurrentMessageID() {
        return currentMessageID;
    }

    public void setCurrentMessageID(int currentMessageID) {
        this.currentMessageID = currentMessageID;
    }
}
