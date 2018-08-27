package com.hec.app.entity;

public class BankInfo {
    private Double Amount;
    private String BankCard;
    private int BankId;
    private String BankName;
    private int BankTypeId;
    private String CardUser;
    private int ColorIndex;
    private String MoneyPassword;
    private String BankBranch;
    private String BankCity;
    private String Phone;
    private String BankProvince;

    public String getBankBranch() {
        return BankBranch;
    }

    public void setBankBranch(String bankBranch) {
        BankBranch = bankBranch;
    }

    public String getBankCity() {
        return BankCity;
    }

    public void setBankCity(String bankCity) {
        BankCity = bankCity;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getBankProvince() {
        return BankProvince;
    }

    public void setBankProvince(String bankProvince) {
        BankProvince = bankProvince;
    }

    public class BankList {
        private int BankTypeID;
        private String BankTypeName;

        public int getBankTypeID() {
            return this.BankTypeID;
        }

        public void setBankTypeID(int bankTypeID) {
            this.BankTypeID = bankTypeID;
        }

        public String getBankTypeName() {
            return this.BankTypeName;
        }

        public void setBankTypeName(String bankTypeName) {
            this.BankTypeName = bankTypeName;
        }
    }

    public Double getAmount() {
        return this.Amount;
    }

    public void setAmount(Double amount) {
        this.Amount = amount;
    }

    public String getMoneyPassword() {
        return this.MoneyPassword;
    }

    public void setMoneyPassword(String moneyPassword) {
        this.MoneyPassword = moneyPassword;
    }

    public int getColorIndex() {
        return this.ColorIndex;
    }

    public void setColorIndex(int colorIndex) {
        this.ColorIndex = colorIndex;
    }

    public int getBankTypeId() {
        return this.BankTypeId;
    }

    public void setBankTypeId(int bankTypeId) {
        this.BankTypeId = bankTypeId;
    }

    public int getBankId() {
        return this.BankId;
    }

    public void setBankId(int bankId) {
        this.BankId = bankId;
    }

    public String getBankName() {
        return this.BankName;
    }

    public void setBankName(String bankName) {
        this.BankName = bankName;
    }

    public String getCardUser() {
        return this.CardUser;
    }

    public void setCardUser(String cardUser) {
        this.CardUser = cardUser;
    }

    public String getBankCard() {
        return this.BankCard;
    }

    public void setBankCard(String bankCard) {
        this.BankCard = bankCard;
    }
}
