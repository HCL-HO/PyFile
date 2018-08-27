package com.hec.app.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by wangxingjian on 16/2/25.
 */
public class HomeBalanceInfo {
    @SerializedName("AvailableScores")
    private String AvailableScores;
    @SerializedName("FreezeScores")
    private String FreezeScores;
    @SerializedName("AllGain")
    private String AllGain;
    @SerializedName("AgAvaliableScores")
    private String AgAvaliableScores;
    @SerializedName("SportAvaliableScores")
    private String SportAvaliableScores;
    @SerializedName("PTAvaliableScores")
    private String PTAvaliableScores;
    @SerializedName("AgFreezeScores")
    private String AgFreezeScores;
    @SerializedName("SportFreezeScores")
    private String SportFreezeScores;

    public String getPTFreezeScores() {
        return PTFreezeScores;
    }

    public void setPTFreezeScores(String PTFreezeScores) {
        this.PTFreezeScores = PTFreezeScores;
    }

    public String getAgAvaliableScores() {
        return AgAvaliableScores;
    }

    public void setAgAvaliableScores(String agAvaliableScores) {
        AgAvaliableScores = agAvaliableScores;
    }

    public String getSportAvaliableScores() {
        return SportAvaliableScores;
    }

    public void setSportAvaliableScores(String sportAvaliableScores) {
        SportAvaliableScores = sportAvaliableScores;
    }

    public String getPTAvaliableScores() {
        return PTAvaliableScores;
    }

    public void setPTAvaliableScores(String PTAvaliableScores) {
        this.PTAvaliableScores = PTAvaliableScores;
    }

    public String getAgFreezeScores() {
        return AgFreezeScores;
    }

    public void setAgFreezeScores(String agFreezeScores) {
        AgFreezeScores = agFreezeScores;
    }

    public String getSportFreezeScores() {
        return SportFreezeScores;
    }

    public void setSportFreezeScores(String sportFreezeScores) {
        SportFreezeScores = sportFreezeScores;
    }

    @SerializedName("PTFreezeScores")
    private String PTFreezeScores;

    public HomeBalanceInfo(String availableScores, String freezeScores, String allGain) {
        AvailableScores = availableScores;
        FreezeScores = freezeScores;
        AllGain = allGain;
    }

    public String getAvailableScores() {
        return AvailableScores;
    }

    public void setAvailableScores(String availableScores) {
        AvailableScores = availableScores;
    }

    public String getFreezeScores() {
        return FreezeScores;
    }

    public void setFreezeScores(String freezeScores) {
        FreezeScores = freezeScores;
    }

    public String getAllGain() {
        return AllGain;
    }

    public void setAllGain(String allGain) {
        AllGain = allGain;
    }
}
