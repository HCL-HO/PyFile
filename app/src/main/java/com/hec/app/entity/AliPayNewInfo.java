package com.hec.app.entity;

import java.io.Serializable;

/**
 * Created by wangxingjian on 2017/4/3.
 */

public class AliPayNewInfo implements Serializable{
    private String AttachWord;
    private String BankName;
    private String BankUser;
    private String BankCard;
    private double Amount;

    public String getAttachWord() {
        return AttachWord;
    }

    public void setAttachWord(String attachWord) {
        AttachWord = attachWord;
    }

    public String getBankName() {
        return BankName;
    }

    public void setBankName(String bankName) {
        BankName = bankName;
    }

    public String getBankUser() {
        return BankUser;
    }

    public void setBankUser(String bankUser) {
        BankUser = bankUser;
    }

    public String getBankCard() {
        return BankCard;
    }

    public void setBankCard(String bankCard) {
        BankCard = bankCard;
    }

    public double getAmount() {
        return Amount;
    }

    public void setAmount(double amount) {
        Amount = amount;
    }
}
