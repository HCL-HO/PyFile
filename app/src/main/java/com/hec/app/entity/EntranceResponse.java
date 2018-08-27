package com.hec.app.entity;

/**
 * Created by wangxingjian on 2016/10/18.
 */

public class EntranceResponse {
    private boolean success;
    private AppBean data;

    public boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public AppBean getData() {
        return data;
    }

    public void setData(AppBean data) {
        this.data = data;
    }
}
