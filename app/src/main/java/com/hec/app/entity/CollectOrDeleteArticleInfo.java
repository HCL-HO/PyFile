package com.hec.app.entity;

/**
 * Created by wangxingjian on 2017/2/16.
 */

public class CollectOrDeleteArticleInfo {
    private int id;
    private boolean success;
    private String message;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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
