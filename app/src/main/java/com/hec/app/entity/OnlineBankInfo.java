package com.hec.app.entity;

import java.io.Serializable;

/**
 * Created by wangxingjian on 2017/3/30.
 */

public class OnlineBankInfo implements Serializable{
    private float amount;
    private int paytype;
    private int MoneyInType;
    private String BankTypeName;
    private String AdminBankBankUser;
    private String AdminBankBankCard;
    private String AttachWord;

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public int getPaytype() {
        return paytype;
    }

    public void setPaytype(int paytype) {
        this.paytype = paytype;
    }

    public int getMoneyInType() {
        return MoneyInType;
    }

    public void setMoneyInType(int moneyInType) {
        MoneyInType = moneyInType;
    }

    public String getBankTypeName() {
        return BankTypeName;
    }

    public void setBankTypeName(String bankTypeName) {
        BankTypeName = bankTypeName;
    }

    public String getAdminBankBankUser() {
        return AdminBankBankUser;
    }

    public void setAdminBankBankUser(String adminBankBankUser) {
        AdminBankBankUser = adminBankBankUser;
    }

    public String getAdminBankBankCard() {
        return AdminBankBankCard;
    }

    public void setAdminBankBankCard(String adminBankBankCard) {
        AdminBankBankCard = adminBankBankCard;
    }

    public String getAttachWord() {
        return AttachWord;
    }

    public void setAttachWord(String attachWord) {
        AttachWord = attachWord;
    }
}
