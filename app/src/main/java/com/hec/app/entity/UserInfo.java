package com.hec.app.entity;

/**
 * Created by hec on 2015/10/27.
 */
public class UserInfo {
    private String userName;
    private Double rebatePro;

    public UserInfo(String userName, Double rebatePro) {
        this.userName = userName;
        this.rebatePro = rebatePro;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Double getRebatePro() {
        return rebatePro;
    }

    public void setRebatePro(Double rebatePro) {
        this.rebatePro = rebatePro;
    }
}
