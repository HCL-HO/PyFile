package com.hec.app.config;

/**
 * Created by jhezenhu on 2017/7/3.
 */

public final class CommonConfig {
    // Package Name
    public static final String PACKAGE_NAME_ALIPAY = "com.eg.android.AlipayGphone";
    public static final String PACKAGE_NAME_WECHAT = "com.tencent.mm";

    // Recharge
    public static final int RECHARGE_QUICK          = 0;
    public static final int RECHARGE_JD_PAY         = 1;
    public static final int RECHARGE_ALIPAY         = 2;
    public static final int RECHARGE_WECHAT         = 3;
    public static final int RECHARGE_ONE_TOUCH      = 4;
    public static final int RECHARGE_QQ             = 5;
    public static final int RECHARGE_ONLINE_BANK    = 6;

    // Key
    public static final String KEY_BASICDATA_EXPERT     = "HEC_BASICDATA_EXPERT";
    public static final String KEY_ALLPLAYCONFIG        = "KEY_ALLPLAYCONFIG";
    public static final String KEY_HOME_FIRST_FILE      = "HOME_FIRST_FILE";
    public static final String KEY_HOME_FIRST_DATA      = "HOME_FIRST_DATA";
    public static final String KEY_ENTRANCE             = "entrance";
    public static final String KEY_FIRST                = "first";
    public static final String KEY_SECOND               = "second";
    public static final String KEY_SLOT                 = "slot";
    public static final String KEY_SETTING_PREFERENCE   = "setting_preference";
    public static final String KEY_CONFIRM_WHEN_EXIT    = "confirm_when_exit";
    public static final String KEY_TOKEN                = "token";
    public static final String KEY_TOKEN_TOKENS         = "tokens";
    public static final String KEY_TOKEN_USER_ID        = "userid";
    public static final String KEY_TOKEN_USER_NAME      = "username";
    public static final String KEY_TOKEN_INFOCOMPLETE   = "infocomplete";
    public static final String KEY_TOKEN_BANK_SHOW      = "bankShow";
    public static final String KEY_USERBESTWIN              = "userbestwin";
    public static final String KEY_USERBESTWIN_LOTTERY_TYPE = "lotterytype";
    public static final String KEY_USERBESTWIN_AMOUNT       = "amount";
    public static final String KEY_SERVERTIME               = "servertime";
    public static final String KEY_SERVERTIME_SERVER_TIMES  = "servertimes";
    public static final String KEY_RETRY        = "retry";
    public static final String KEY_RETRY_COUNT  = "count";
    public static final String KEY_DATA             = "data";
    public static final String KEY_DATA_RMB         = "RMB";
    public static final String KEY_DATA_STUPID_LOGIN_PASSWORD_FLAG    = "KEY_DATA_STUPID_LOGIN_PASSWORD_FLAG";
    public static final String KEY_DATA_STUPID_MONEY_PASSWORD_FLAG    = "KEY_DATA_STUPID_MONEY_PASSWORD_FLAG";
    public static final String KEY_DATA_STUPID_PASSWORD_SHOW_DIALOG   = "KEY_DATA_STUPID_PASSWORD_SHOW_DIALOG";
    public static final String KEY_DATA_USER_NAMES  = "usernames";
    public static final String KEY_HASHCODE                     = "KEY_HASHCODE";
    public static final String KEY_HASHCODE_BASICDATA_CACHE     = "KEY_HASHCODE_BASICDATA_CACHE";
    public static final String KEY_HASHCODE_ALLPLAYCONFIG_CACHE = "KEY_HASHCODE_ALLPLAYCONFIG_CACHE";
    public static final String KEY_INFO_COMPLETE_FRAFMENT   = "InfoCompleteFragment";
    public static final String KEY_CATCH_SLOT_BJL           = "CATCH_SLOT_BJL";
    public static final String KEY_IS_CATCH_ADD_SLOT_BJL    = "IS_CATCH_ADD_SLOT_BJL";
    public static final String KEY_IS_LOGIN   = "IS_LOGIN";
    public static final String KEY_CONFIDENTIALITY_DIALOG_CHECKBOX   = "KEY_CONFIDENTIALITY_DIALOG_CHECKBOX";
    public static final String KEY_SYSTEM_NOTIFICATION   = "KEY_SYSTEM_NOTIFICATION";


    // Bundle
    public static final String BUNDLE_GOTIGER_USERNAME  = "UserName";
    public static final String BUNDLE_GOTIGER_BALANCE   = "Balance";
    public static final String BUNDLE_GOTIGER_AASLOTURL = "AASlotUrl";
    public static final String BUNDLE_GOTIGER_SCENE = "scene";
    public static final String BUNDLE_RECHARGE_DATA     = "data";
    public static final String BUNDLE_FIND_PASSWORD_POSITION    = "position";
    public static final String BUNDLE_FIND_PASSWORD_USERNAME    = "userName";

    // Intent
    public static final String INTENT_NICKNAME_TAG  = "tag";
    public static final String INTENT_FORM_TAG      = "tag";
    public static final String INTENT_SUCCESS_VALID_EMAIL   = "validEmail";
    public static final String INTENT_SUCCESS_TAG           = "tag";
    public static final String INTENT_TRANSFER_PLAYTYPE		= "playtype";
    public static final String INTENT_TRANSFER_AVALIBALE	= "Avalibale";
    public static final String INTENT_TRANSFER_FREEZE		= "Freeze";
    public static final String INTENT_TREND_HISTORY_ID		    = "ID";
    public static final String INTENT_TREND_HISTORY_TYPE        = "Type";
    public static final String INTENT_TREND_HISTORY_IDLIST      = "IDList";
    public static final String INTENT_TREND_HISTORY_NAMELIST    = "NameList";

    // Map
    public static final String MAP_FIND_PASSWORD_IMAGE  = "image";
    public static final String MAP_FIND_PASSWORD_TITLE  = "title";
    public static final String MAP_FIND_PASSWORD_INFO   = "info";

    // Handler Key
    public static final int HANDLER_RECHARGE_RESULT         = 0;
    public static final int HANDLER_RECHARGE_ADD_BANK_TYPE  = 1;

    // Recharge Money In Type
    public static final int MONEY_IN_TYPE_WECHAT    = 13;
    public static final int MONEY_IN_TYPE_ALIPAT    = 15;
    public static final int MONEY_IN_TYPE_QQ        = 17;
    public static final int MONEY_IN_TYPE_JD_PAY    = 18;
    public static final int MONEY_IN_TYPE_WL_PAY    = 19;

    // Nickname
    public static final String NICKNAME_KEY_ADD = "1";
    public static final String NICKNAME_KEY_FIX = "2";

    // Find Password Position
    public static final int FIND_PASSWORD_EMAIL     = 0;
    public static final int FIND_PASSWORD_ISSUE     = 1;
    public static final int FIND_PASSWORD_MONEY_PWD = 2;
    public static final int FIND_PASSWORD_SMS       = 3;

    // Transfer PlayType
    public static final String TRANSFER_PLAYTYPE_REALMAN	= "realman";
    public static final String TRANSFER_PLAYTYPE_PT			= "pt";
    public static final String TRANSFER_PLAYTYPE_SPORTS		= "sports";
    public static final String TRANSFER_IN                  = "in";
    public static final String TRANSFER_OUT                 = "out";

    // Slot
    public static final String THREE_D_BACARRAT     = "baccarat_online";
    public static final String SLOT_CDN             = "http://cdn.ycycar.com/";
    public static final String DEFAULT_TAG          = "";
    public static final String FISHING              = "fishing";

    //Webchat
    public static final String WEBCHAT_TYPE     = "WEBCHAT_TYPE";
    public static final int WEBCHAT_TYPE_NORMAL = 0;
    public static final int WEBCHAT_TYPE_VIP    = 1;

    public static final byte[] KEY_BYTE = {67,50,67,54,66,65,65,66,68,66,53,65,53,49,66,50,52,48,49,55,65,67,55,54,53,48,67,54,52,49,50,57};
    public static final byte[] IV_BYTE = {57,57,69,50,48,57,51,68,67,65,57,50,57,49,69,51};
}
