package com.hec.app.entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by Isaac on 26/2/2016.
 */
public class BestPrizeInfo implements Serializable{
    @SerializedName("LotteryTime")
    String LotteryTime;
    @SerializedName("LotteryType")
    String LotteryType;
    @SerializedName("UserName")
    String UserName;
    @SerializedName("WinMoney")
    String WinMoney;

    public void setLotteryTime (String val) {
        LotteryTime = val;
    }

    public String getLotteryTime() {
        return LotteryTime;
    }

    public void setLotteryType (String val) {
        LotteryType = val;
    }

    public String getLotteryType () {
        return LotteryType;
    }

    public void setUserName (String val) {
        UserName = val;
    }

    public String getUserName() {
        return UserName;
    }

    public void setWinMoney(String val) {
        WinMoney = val;
    }

    public String getWinMoney () {
        return WinMoney;
    }

    public HashMap<String, String> toHashMap() {
        HashMap<String, String> hm = new HashMap<>();
        if (LotteryTime != null)
            hm.put("LotteryTime", LotteryTime);
        if (LotteryType != null)
            hm.put("LotteryType", LotteryType);
        if (UserName != null)
            hm.put("UserName", UserName);
        if (WinMoney != null)
            hm.put("WinMoney", WinMoney);
        return hm;
    }
}
