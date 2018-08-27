package com.hec.app.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Isaac on 13/4/2016.
 */
public class EggInfo {
    @SerializedName("EggMoney")
    float EggMoney;
    @SerializedName("EggName")
    String EggName;
    @SerializedName("EggType")
    int EggType;

    public String getEggMoney() {
        return String.format("%.2f", EggMoney);
    }

    public void setEggMoney (float val) {
        EggMoney = val;
    }

    public String getEggName() {
        return EggName;
    }

    public void setEggName (String val) {
        EggName = val;
    }

    public int getEggType() {
        return EggType;
    }

    public void setEggType (int val) {
        EggType = val;
    }
}
