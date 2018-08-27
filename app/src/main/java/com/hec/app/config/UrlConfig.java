package com.hec.app.config;

import com.hec.app.BuildConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jhezenhu on 2017/4/11.
 */

public final class UrlConfig {
    public static final String RESTFUL_SERVICE_HOST = "http://m.ahdzf.com/";
    public static final String TEST_RESTFUL_SERVICE_HOST = "http://192.168.3.16:7788/";
    public static final String TEST_RESTFUL_VIP_SERVICE_HOST = "http://192.168.3.14:7788/";
    public static final String RESTFUL_VIP_SERVICE_HOST = "http://vipm.szhfdmt888.com/";
    public static final String STAGE_RESTFUL_SERVICE_HOST = "http://m.staging.jx.ark88.local/";
    public static final String UAT_RESTFUL_SERVICE_HOST = "http://m.6j71.com/";
    public static final String BACKUP_URL = "http://103.240.143.88:28083/";
    public static final String CDN_URL = "http://home.bigbrothers.info:19088/";
    public static final String WEBCHAT_URL = "http://chat.advc9.com/webchat/chat.aspx?siteid=666666&did=1";
    public static final String WEBCHAT_VIP_URL = "http://chat.advc9.com/webchat/chat.aspx?siteid=666666&did=3";
    public static final String AG_APK_DOWNLOAD_PATH = "http://agmbet.com/universal/AG_setup.apk";
    public static final String URL_HK_TEST = "http://bigbrothers.info:19088";

    // Brand Url
    public static final String BRAND_TEST_DOMAIN = "http://192.168.3.13:8088/";
    public static final String BRAND_PATH = "BrandMobile/Goddess";
    public static final String BRAND_URL = "http://web.wqq3.com/";

    public static final String TEST_CUSTOMER_SERVICE_IMAGE_URL_PREFIX = TEST_RESTFUL_VIP_SERVICE_HOST + "upload/";
    public static final String CUSTOMER_SERVICE_IMAGE_URL_PREFIX = RESTFUL_VIP_SERVICE_HOST + "upload/";


    // Rabbit Message push Uri
    public static final String MQ_URL = "amqp://hjmqu1:qwertyuiop@mq.luck111.com:5672";
    public static final String TEST_MQ_URL = "amqp://hjmqu1:qwertyuiop@sit-n51plus-sv.ark88.local:5672";

    public static final String MQ_VIP_URL = "amqp://hjmqu1:qwertyuiop@vipkf.b66r.com:15674";
    public static final String TEST_MQ_VIP_URL = "amqp://hjmqu1:qwertyuiop@192.168.3.14:5672";

    public static String[] apiList;

    static {
        if (BuildConfig.DEBUG) {
            apiList = new String[]{};
        } else {
            apiList = new String[]{
                    "http://m.jLyedu.com/",
                    "http://m.kdb885.com/",
                    "http://m.tr0577.com/",
                    "http://1.32.216.108:28083/",
                    "http://128.1.37.142:28083"
            };
        }
    }

    // Tiger Slot Url
    public static final List<String> SLOT_URL = new ArrayList<String>(){{
        add("slot.fwjtqd.com");
        add("http://bj.291cc.com");
    }};

    public static final List<String> SLOT_WS_URL = new ArrayList<String>(){{
        add("ws://ws.xin101.com");
    }};

    public static final List<String> SLOT_CDN_URL = new ArrayList<String>(){{
        add("http://cdn2.ycycar.com/");
    }};

    public static final List<String> CHAT_URL = new ArrayList<String>(){{
        add("http://chat.i18888.com/login/");
    }};

    public static final List<String> FISHING_URL = new ArrayList<String>(){{
        add("");
    }};

    public static final List<String> FISHING_WS = new ArrayList<String>(){{
        add("");
    }};

    // Debug Choose Service URL List
    public static final List<String> URL_HOST_LIST = new ArrayList<String>(){{
        add(TEST_RESTFUL_SERVICE_HOST);
        add("http://bigbrothers.info:19088/");
        add(CDN_URL);
        add(RESTFUL_SERVICE_HOST);
        add("http://192.168.0.210:8088");
        add("http://m.1111.com/");
    }};

    // Entrance Info URL
    public static final List<String> APP_INFO_HOST_LIST = new ArrayList<String>(){{
        add("http://52.192.23.131:38080/");
        add("http://103.240.143.90:38080/");
        add("http://192.168.0.210:19089/");
        add("http://192.168.3.16:7878/"); //sit
        add("http://192.168.3.74:38080/"); //uat
    }};

    // Local Service URL List
    public static final List<String> LOCAL_API_URL_LIST = new ArrayList<String>(){{
        add("http://183.60.107.28:28083");
        add("http://183.60.107.7:28083");
        add("http://220.169.242.164:28083");
        add("http://220.169.242.105:28083");
    }};

}
