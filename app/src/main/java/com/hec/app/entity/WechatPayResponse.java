package com.hec.app.entity;

public class WechatPayResponse {

    private String success;
    private String message;
    private WechatPayInfo result;

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public WechatPayInfo getResult() {
        return result;
    }

    public void setResult(WechatPayInfo result) {
        this.result = result;
    }
}
