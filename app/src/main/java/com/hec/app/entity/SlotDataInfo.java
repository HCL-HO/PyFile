package com.hec.app.entity;

/**
 * Created by wangxingjian on 2017/10/12.
 */

public class SlotDataInfo {
    public String userName;
    public String version;
    public float balance;
    public String updateUrl;
    public String slotUrl;
    public String AASlotWSUrl;
    public String token;
    public String cdnUrl;
    public boolean development;
    public String scene;
    public String AAFishingHTTPUrl;
    public String AAFishingWSUrl;

    public SlotDataInfo(String userName
            , String version
            , float balance
            , String updateUrl
            , String slotUrl
            , String AASlotWSUrl
            , String token
            , String cdnUrl
            , boolean development
            , String scene
            , String AAFishingHTTPUrl
            , String AAFishingWSUrl) {
        this.userName = userName;
        this.version = version;
        this.balance = balance;
        this.updateUrl = updateUrl;
        this.slotUrl = slotUrl;
        this.AASlotWSUrl = AASlotWSUrl;
        this.token = token;
        this.cdnUrl = cdnUrl;
        this.development = development;
        this.scene = scene;
        this.AAFishingHTTPUrl = AAFishingHTTPUrl;
        this.AAFishingWSUrl = AAFishingWSUrl;
    }
}
