package com.hec.app.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Isaac on 26/2/2016.
 */
public class BankTypeInfo {
    @SerializedName("BankTypeID")
    String BankTypeID;
    @SerializedName("BankTypeName")
    String BankTypeName;
    @SerializedName("MoneyInType")
    int MoneyInType;


    public void setBankTypeID (String val) {
        BankTypeID = val;
    }

    public String getBankTypeID () {
        return BankTypeID;
    }

    public void setBankTypeName (String val) {
        BankTypeName = val;
    }

    public String getBankTypeName () {
        return BankTypeName;
    }

    public void setMoneyInType (int moneyInType) {
        MoneyInType = moneyInType;
    }

    public int getMoneyInType () {
        return MoneyInType;
    }
}
