package com.hec.app.entity;

import android.os.Parcelable;

import java.io.Serializable;

public class BalanceInfo implements Serializable{
    private String AgAvaliableScores;
    private String AgFreezeScores;
    private String AvailableScores;
    private String FreezeScores;
    private String SportAvaliableScores;
    private String SportFreezeScores;

    public String getAgAvaliableScores() {
        return this.AgAvaliableScores;
    }

    public void setAgAvaliableScores(String agAvaliableScores) {
        this.AgAvaliableScores = agAvaliableScores;
    }

    public String getAgFreezeScores() {
        return this.AgFreezeScores;
    }

    public void setAgFreezeScores(String agFreezeScores) {
        this.AgFreezeScores = agFreezeScores;
    }

    public String getAvailableScores() {
        return this.AvailableScores;
    }

    public void setAvailableScores(String availableScores) {
        this.AvailableScores = availableScores;
    }

    public String getFreezeScores() {
        return this.FreezeScores;
    }

    public void setFreezeScores(String freezeScores) {
        this.FreezeScores = freezeScores;
    }

    public String getSportAvaliableScores() {
        return this.SportAvaliableScores;
    }

    public void setSportAvaliableScores(String sportAvaliableScores) {
        this.SportAvaliableScores = sportAvaliableScores;
    }

    public String getSportFreezeScores() {
        return this.SportFreezeScores;
    }

    public void setSportFreezeScores(String sportFreezeScores) {
        this.SportFreezeScores = sportFreezeScores;
    }
}
