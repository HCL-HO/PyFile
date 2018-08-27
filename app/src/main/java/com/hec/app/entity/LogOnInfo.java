package com.hec.app.entity;

/**
 * Created by hec on 2015/11/16.
 */
public class LogOnInfo {
    private int code;
    private int userID;
    private String userName;
    private String password;
    private String verifyCode;
    private String key;
    private String msg;
    private boolean isInfoComplete;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getVerifyCode() {
        return verifyCode;
    }

    public void setVerifyCode(String verifyCode) {
        this.verifyCode = verifyCode;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public boolean getIsInfoComplete() {
        return isInfoComplete;
    }

    public void setInfoComplete(boolean val) {
        isInfoComplete = val;
    }
}
