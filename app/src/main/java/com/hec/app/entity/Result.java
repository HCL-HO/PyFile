package com.hec.app.entity;


public class Result<T> {
    private String ApiCode;
    private String Data;
    private String Message;
    private boolean Success;
    private T DataObj;

    public String getApiCode() {
        return ApiCode;
    }

    public void setApiCode(String apiCode) {
        ApiCode = apiCode;
    }

    public String getData() {
        return Data;
    }


    public void setData(String data) {
        Data = data;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public boolean isSuccess() {
        return Success;
    }

    public void setSuccess(boolean success) {
        Success = success;
    }

    public T getDataObj() {
        return DataObj;
    }

    public void setDataObj(T dataObj) {
        DataObj = dataObj;
    }
}
