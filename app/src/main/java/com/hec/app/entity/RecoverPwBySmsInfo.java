package com.hec.app.entity;

/**
 * Created by hec on 2015/11/16.
 */
public class RecoverPwBySmsInfo {
    private String UserName;
    private String PhoneNumber;
    private String Type;

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        this.Type = type;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String phone) {
        this.PhoneNumber = phone;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }
}
