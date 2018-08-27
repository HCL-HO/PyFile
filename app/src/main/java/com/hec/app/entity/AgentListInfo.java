package com.hec.app.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Isaac on 5/2/2016.
 */
public class AgentListInfo {
    @SerializedName("AvailableScores")
    String AvailableScores;
    @SerializedName("Turnover")
    String Turnover;
    @SerializedName("UserID")
    String UserID;
    @SerializedName("UserName")
    String UserName;
    @SerializedName("WinLoss")
    String WinLoss;

    public void setAvailableScores (String val){
        AvailableScores = val;
    }

    public double getAvailableScores () {
        if (AvailableScores != null)
            return Double.parseDouble(AvailableScores);
        else
            return 0;
    }

    public void setTurnover (Double val) {
        Turnover = Double.toString(val);
    }

    public double getTurnover () {
        return Double.parseDouble(Turnover);
    }

    public void setUserID (String val) {
        UserID = val;
    }

    public String getUserID () {
        return UserID;
    }

    public void setUserName (String val) {
        UserName = val;
    }

    public String getUserName () {
        return UserName;
    }

    public void setWinLoss (Double val) {
        WinLoss = Double.toString(val);
    }

    public Double getWinLoss () {
        return Double.parseDouble(WinLoss);
    }
}
