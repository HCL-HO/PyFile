package com.hec.app.entity;

/**
 * Created by hk13h on 5/2/2016.
 */
public class ChangePwInfo {
    private String oldpassword;
    private String newpassword;

    public String getOldPw() {
        return oldpassword;
    }

    public void setOldPw(String oldPw) {
        this.oldpassword = oldPw;
    }

    public String getNewPw() {
        return newpassword;
    }

    public void setNewPw(String newPw) {
        this.newpassword = newPw;
    }
}
