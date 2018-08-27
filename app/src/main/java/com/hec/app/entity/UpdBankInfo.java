package com.hec.app.entity;

import com.networkbench.com.google.gson.annotations.SerializedName;

public class UpdBankInfo {
    @SerializedName("moneyPwd")
    private String moneyPwd;
    @SerializedName("model")
    private NewBankInfo model;

    public String getMoneyPwd() {
        return moneyPwd;
    }

    public void setMoneyPwd(String moneyPwd) {
        this.moneyPwd = moneyPwd;
    }

    public NewBankInfo getModel() {
        return model;
    }

    public void setModel(NewBankInfo model) {
        this.model = model;
    }
}
