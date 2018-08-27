package com.hec.app.entity;

/**
 * Created by wangxingjian on 2017/1/11.
 */

public class ServerTimeResponseinfo {
    private boolean success;
    private String message;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
