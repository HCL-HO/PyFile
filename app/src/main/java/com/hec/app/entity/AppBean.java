package com.hec.app.entity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;
import com.hec.app.BuildConfig;
import com.hec.app.activity.base.BaseApp;
import com.hec.app.framework.widget.MyToast;
import com.hec.app.util.MyAsyncTask;
import com.hec.app.webservice.BaseService;
import com.hec.app.webservice.HomeService;
import com.hec.app.webservice.ServiceException;
//import com.umeng.analytics.MobclickAgent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;

/**
 * Created by asianark on 21/3/16.
 */
public class AppBean {
    @SerializedName("apiUrl")
    String apiUrl;
    @SerializedName("appUrl")
    String appUrl;
    @SerializedName("version")
    String version;
    @SerializedName("apiUrls")
    String apiUrls;
    @SerializedName("AASlotUrl")
    String AASlotUrl;
    @SerializedName("AASlotUrlNew")
    String AASlotUrlNew;
    @SerializedName("iosAppUrl")
    String iosAppUrl;
    @SerializedName("iosVersion")
    String iosVersion;
    @SerializedName("iosForceVersion")
    String iosForceVersion;
    @SerializedName("ForceVersion")
    String ForceVersion;
    @SerializedName("webNavigationUrl")
    String webNavigationUrl;
    @SerializedName("MqUrl")
    String MqUrl;
    @SerializedName("AASlotWSUrl")
    String AASlotWSUrl;
    @SerializedName("AASlotCDNUrl")
    String AASlotCDNUrl;
    @SerializedName("ChatUrl")
    String ChatUrl;
    @SerializedName("AAFishingHTTPUrl")
    String AAFishingHTTPUrl;
    @SerializedName("AAFishingWSUrl")
    String AAFishingWSUrl;
    @SerializedName("aaMinAvailableVersion")
    String aaMinAvailableVersion;
    @SerializedName("ADUrl")
    String ADUrl;
    @SerializedName("VIPcontactUrl")
    String vipContactUrl;
    @SerializedName("VipApiUrl")
    String VipApiUrl;

    List<RestfulApiUrlEntity> availableUrlList;

    public String getAaMinAvailableVersion() {
        return aaMinAvailableVersion;
    }

    public void setAaMinAvailableVersion(String aaMinAvailableVersion) {
        this.aaMinAvailableVersion = aaMinAvailableVersion;
    }

    public String getAAFishingWSUrl() {
        return AAFishingWSUrl;
    }

    public void setAAFishingWSUrl(String AAFishingWSUrl) {
        this.AAFishingWSUrl = AAFishingWSUrl;
    }

    public String getAAFishingHTTPUrl() {
        return AAFishingHTTPUrl;
    }

    public void setAAFishingHTTPUrl(String AAFishingHTTPUrl) {
        this.AAFishingHTTPUrl = AAFishingHTTPUrl;
    }

    public String getChatUrl() {
        return ChatUrl;
    }

    public void setChatUrl(String chatUrl) {
        ChatUrl = chatUrl;
    }

    public String getHomeBannersUrl() {
        return HomeBannersUrl;
    }

    public void setHomeBannersUrl(String homeBannersUrl) {
        HomeBannersUrl = homeBannersUrl;
    }

    @SerializedName("HomeBannersUrl")

    String HomeBannersUrl;

    public String getIosAppUrl() {
        return iosAppUrl;
    }

    public void setIosAppUrl(String iosAppUrl) {
        this.iosAppUrl = iosAppUrl;
    }

    public String getBasicDataLastUpdateTime() {
        return BasicDataLastUpdateTime;
    }

    public void setBasicDataLastUpdateTime(String basicDataLastUpdateTime) {
        BasicDataLastUpdateTime = basicDataLastUpdateTime;
    }

    @SerializedName("BasicDataLastUpdateTime")
    String BasicDataLastUpdateTime;

    public String getContactUrl() {
        return contactUrl;
    }

    public void setContactUrl(String contactUrl) {
        this.contactUrl = contactUrl;
    }

    @SerializedName("contactUrl")
    String contactUrl;

    private SharedPreferences token;
    int retrycount = 0;
    private boolean isDialogshowing = false;

    public String getAASlotUrlNew() {
        return AASlotUrlNew;
    }

    public void setAASlotUrlNew(String AASlotUrlNew) {
        this.AASlotUrlNew = AASlotUrlNew;
    }

    public String getAASlotUrl() {
        return AASlotUrl;
    }

    public String getAASlotWSUrl() {
        return AASlotWSUrl;
    }

    public void setAASlotWSUrl(String AASlotWSUrl) {
        this.AASlotWSUrl = AASlotWSUrl;
    }

    public void setAASlotUrl(String AASlotUrl) {
        this.AASlotUrl = AASlotUrl;
    }

    public String getAASlotCDNUrl() {
        return AASlotCDNUrl;
    }

    public void setAASlotCDNUrl(String AASlotCDNUrl) {
        this.AASlotCDNUrl = AASlotCDNUrl;
    }

    public String getApiUrls() {
        return apiUrls;
    }

    public void setApiUrls(String apiUrls) {
        this.apiUrls = apiUrls;
    }

    public List<RestfulApiUrlEntity> getAvailableUrlList() {
        if (availableUrlList == null) {
            availableUrlList = new ArrayList<>();
        }
        return availableUrlList;
    }

    public void setAvailableUrlList(List<RestfulApiUrlEntity> list) {
        availableUrlList = list;
    }

    boolean mIsError = false;
    int retryCount = 0;
    int apiIndex = 0;

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

    private Map<String, Boolean> availableURLs = new HashMap<>();

    public Map<String, Boolean> getAvailableURLs() {
        Log.i("speed", "in get");
        return availableURLs;
    }

    public String getApiUrl() {
        return apiUrl;//real environment
    }

    public boolean resetApiUrl(Activity act) {
        if (BaseService.BACKUP_URL != null) {
            BaseService.RESTFUL_SERVICE_HOST = BaseService.BACKUP_URL;
        }
        return true;
    }

    public boolean resetApiUrl(final Context act, final Callable<Integer> func) {
        retrycount++;
        String url = BaseService.RESTFUL_SERVICE_HOST;
        Log.i("speed", "resetApiUrl" + url);
        if (availableURLs.containsKey(url)) {
            availableURLs.put(url, false);
        }
        Log.i("speed", "after put ");
        Map<String, String> map = new HashMap<String, String>();
        map.put("badurl", url + " no network at all");
        if (!BaseApp.haveNetwork) {
//            MobclickAgent.onEventValue(act, "badurls", map, 100);
        }
        Log.i("speed", "we will use backup " + BaseService.BACKUP_URL);
        if (BaseService.BACKUP_URL != null) {
            BaseService.RESTFUL_SERVICE_HOST = BaseService.BACKUP_URL;
        }
        int total = 0;
        List<String> list = new ArrayList<>();
        for (String s : availableURLs.keySet()) {
            if (availableURLs.get(s)) {
                total++;
                list.add(s);
            }
        }
        if (total == 0) {
            int k = new Random().nextInt(4);
            BaseService.BACKUP_URL = apiList[k];
        } else {
            int k = new Random().nextInt(total);
            BaseService.BACKUP_URL = list.get(k);
        }
        //BaseService.RESTFUL_SERVICE_HOST = "http://main.ceshi188.com:8100/";
        if (retrycount > 9) {
            BaseService.RESTFUL_SERVICE_HOST = "http://m.ahdzf.com/";
            if (!isDialogshowing) {
                Log.i("receiverok", "in retrycount");
                android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(act)
                        .setTitle("网络出现问题！")
                        .setMessage("请您选择重试！")
                        .setPositiveButton("重试", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                retrycount = 0;
                                for (String url : availableURLs.keySet()) {
                                    availableURLs.put(url, true);
                                }
                                MyToast.count = 0;
                                isDialogshowing = false;
                            }
                        })
                        .setCancelable(false);
                dialog.show();
                isDialogshowing = true;
            }
            return false;
        }
        return true;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public String getAppUrl() {
        return appUrl;
    }

    public void setAppUrl(String appUrl) {
        this.appUrl = appUrl;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    public void getAppInfo(final Activity act, final Callable<Integer> func) {
        mIsError = false;
        MyAsyncTask<AppBean> task = new MyAsyncTask<AppBean>(act) {
            @Override
            public AppBean callService() throws IOException,
                    JsonParseException, BizException, ServiceException {
                return new HomeService().getAppInfo();
            }

            @Override
            public void onLoaded(AppBean result) throws Exception {
                if (!mIsError) {
                    Log.i("net", "got new ip from entrance" + result.getApiUrl());
                    BaseService.RESTFUL_SERVICE_HOST = result.getApiUrl();
                    if (func != null)
                        func.call();
                } else {
                    Log.i("net", "cant got new ip from entrance" + retryCount);
                }
            }
        };
        task.setOnError(new MyAsyncTask.OnError() {
            @Override
            public void handleError(Exception e) {
                mIsError = true;
            }
        });
        task.executeTask();
    }

    public String getIosForceVersion() {
        return iosForceVersion;
    }

    public void setIosForceVersion(String iosForceVersion) {
        this.iosForceVersion = iosForceVersion;
    }

    public String getForceVersion() {
        return ForceVersion;
    }

    public void setForceVersion(String fVersion) {
        ForceVersion = fVersion;
    }

    public String getWebNavigationUrl() {
        return this.webNavigationUrl;
    }

    public void setWebNavigationUrl(String webNavigationUrl) {
        this.webNavigationUrl = webNavigationUrl;
    }

    public String getMqUrl() {
        return this.MqUrl;
    }

    public void setMqUrl(String mqUrl) {
        this.MqUrl = mqUrl;
    }

    public String getADUrl() {
        return ADUrl;
    }

    public void setADUrl(String ADUrl) {
        this.ADUrl = ADUrl;
    }

    public String getVIPContactUrl() {
        return this.vipContactUrl;
    }

    public void setVIPContactUrl(String vipContactUrl) {
        this.vipContactUrl = vipContactUrl;
    }

    public String getVipApiUrl() {
        return VipApiUrl;
    }

    public void setVipApiUrl(String vipApiUrl) {
        VipApiUrl = vipApiUrl;
    }


}
