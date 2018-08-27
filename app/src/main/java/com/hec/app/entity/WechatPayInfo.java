package com.hec.app.entity;

public class WechatPayInfo {

    private Object ExtensionData;
    private String AdminBankBankCard;
    private String AdminBankBankUser;
    private int BankId;
    private String BankTypeName;
    private int MoneyInType;

    public Object getExtensionData() {
        return ExtensionData;
    }

    public void setExtensionData(Object extensionData) {
        ExtensionData = extensionData;
    }

    public String getAdminBankBankCard() {
        return AdminBankBankCard;
    }

    public void setAdminBankBankCard(String adminBankBankCard) {
        AdminBankBankCard = adminBankBankCard;
    }

    public String getAdminBankBankUser() {
        return AdminBankBankUser;
    }

    public void setAdminBankBankUser(String adminBankBankUser) {
        AdminBankBankUser = adminBankBankUser;
    }

    public int getBankId() {
        return BankId;
    }

    public void setBankId(int bankId) {
        BankId = bankId;
    }

    public String getBankTypeName() {
        return BankTypeName;
    }

    public void setBankTypeName(String bankTypeName) {
        BankTypeName = bankTypeName;
    }

    public int getMoneyInType() {
        return MoneyInType;
    }

    public void setMoneyInType(int moneyInType) {
        MoneyInType = moneyInType;
    }
}
