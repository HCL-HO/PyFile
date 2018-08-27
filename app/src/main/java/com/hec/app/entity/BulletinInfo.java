package com.hec.app.entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by hec on 2015/12/8.
 */
public class BulletinInfo implements Serializable{
    @SerializedName("BulletinID")
    private int BulletinID;
    @SerializedName("BulletinTitle")
    private String bulletinTitle;
    @SerializedName("BulletinTime")
    private String bulletinTime;
    @SerializedName("BulletinText")
    private String bulletinText;

    public int getBulletinID() {
        return BulletinID;
    }

    public void setBulletinID(int bulletinID) {
        this.BulletinID = bulletinID;
    }

    public String getBulletinTitle() {
        return bulletinTitle;
    }

    public void setBulletinTitle(String bulletinTitle) {
        this.bulletinTitle = bulletinTitle;
    }

    public String getBulletinTime() {
        return bulletinTime;
    }

    public void setBulletinTime(String bulletinTime) {
        this.bulletinTime = bulletinTime;
    }

    public String getBulletinText() {
        return bulletinText;
    }

    public void setBulletinText(String bulletinText) {
        this.bulletinText = bulletinText;
    }
}
