package com.hec.app.entity;

/**
 * Created by hec on 2015/11/17.
 */
public class Response<T> {
    private boolean success;
    private String message;
    private T data;

    public boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
