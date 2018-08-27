package com.hec.app.entity;

/**
 * Created by hec on 2015/11/16.
 */
public class SecurityInfoFinishInfo {
    private String email;
    private String phoneNumber;
    private boolean isPhoneRotection;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean getIsPhoneRotection() {
        return isPhoneRotection;
    }

    public void setIsPhoneRotection(boolean isPhoneRotection) {
        this.isPhoneRotection = isPhoneRotection;
    }
}
