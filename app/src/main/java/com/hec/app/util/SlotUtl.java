package com.hec.app.util;

import com.google.gson.Gson;
import com.hec.app.activity.base.BaseApp;
import com.hec.app.config.CommonConfig;
import com.hec.app.entity.SlotDataInfo;
import com.hec.app.webservice.BaseService;

/**
 * Created by wangxingjian on 2017/10/12.
 */

public class SlotUtl {

    /**
     * Build the json object by parameters.
     * @param scene telling Unity if we should go directly to one game.
     * @param username
     * @param slotUrl
     * @param balance Money the user has.
     * @return
     */
    public static final String buildDataAccordingToScene(String scene, String username, String slotUrl, float balance){
        return new Gson().toJson(new SlotDataInfo(
                username
                , "11"
                , balance
                , BaseApp.getAppBean().getIosAppUrl().replace("ios", "android")
                , slotUrl
                , BaseService.SLOT_WS_URL
                , CustomerAccountManager.getInstance().getCustomer().getAuthenticationKey()//This is the token.
                , BaseService.SLOT_CDN_URL
                , false
                , scene
                , BaseService.SLOT_FISHING_URL
                , BaseService.SLOT_FISHING_WS
        ));
    }

    public static void switchUrlForTest(){

        BaseService.SLOT_URL = "http://192.168.0.101/";//http://192.168.0.101/
        BaseService.SLOT_WS_URL = "ws://192.168.0.101/";
        BaseService.SLOT_CDN_URL = "http://192.168.0.221:2762/";//http://hec-dev-slot-cdn.bigbrothers.info/
        BaseService.CHAT_URL = "https://chat.bigbrothers.info/";

        BaseService.SLOT_FISHING_URL = "http://192.168.0.108/";//"http://hec-dev-fishing.bigbrothers.info/"
        BaseService.SLOT_FISHING_WS = "ws://192.168.0.108/";//"ws://hec-dev-fishing.bigbrothers.info/"

        //http://home.bigbrothers.info:6621/admin/;//chenhao server for outside
        //AASlotUrl = "http://192.168.0.120:8000/";//fish's server
        //AASlotUrl = "http://192.168.0.101/";//test server
        //AASlotUrl = "http://192.168.0.180:6621/";//ch'server
        //AASlotUrl = "http://hec-dev-slotback.bigbrothers.info/";//test
        //AASlotUrl = "http://bj.xs3022.com/";
        //,"ws://hec-dev-slotback.bigbrothers.info/"
        //,"ws://192.168.0.101/"
        //,CommonConfig.SLOT_CDN
        //,"http://hec-dev-slot-cdn.bigbrothers.info/"
        //,"http://192.168.0.221:2762/"
    }
}
