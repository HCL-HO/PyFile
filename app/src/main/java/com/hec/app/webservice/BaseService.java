package com.hec.app.webservice;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.hec.app.BuildConfig;
import com.hec.app.activity.base.BaseApp;
import com.hec.app.config.UrlConfig;
import com.hec.app.entity.BizException;
import com.hec.app.entity.CustomerInfo;
import com.hec.app.framework.http.OkHttpClientManager;
import com.hec.app.util.CustomerAccountManager;
import com.hec.app.util.StringUtil;
import com.hec.app.util.TestUtil;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.lang.reflect.Type;


public class BaseService {
    public static String RESTFUL_SERVICE_HOST = UrlConfig.RESTFUL_SERVICE_HOST;
    public static String BACKUP_URL = UrlConfig.BACKUP_URL;
    public static final String SALT = "asianark";
    public static String APP_INFO_HOST = UrlConfig.APP_INFO_HOST_LIST.get(0);
    public static String SLOT_URL = UrlConfig.SLOT_URL.get(0);
    public static String SLOT_WS_URL = UrlConfig.SLOT_WS_URL.get(0);
    public static String SLOT_CDN_URL = UrlConfig.SLOT_CDN_URL.get(0);
    public static String SLOT_FISHING_URL = UrlConfig.FISHING_URL.get(0);
    public static String SLOT_FISHING_WS = UrlConfig.FISHING_WS.get(0);
    public static String CHAT_URL = UrlConfig.CHAT_URL.get(0);
    public static String BRAND_URL = UrlConfig.BRAND_URL;
    public static String MQ_URL = UrlConfig.MQ_URL;
    public static float BASE_BALANCE = 0;
    //public static final String APP_INFO_HOST = "http://192.168.0.201:8005/";

    public static final String getRestfulServiceHost() {
        if (BuildConfig.SIT) {
            return UrlConfig.TEST_RESTFUL_SERVICE_HOST;
        } else if (BuildConfig.UAT) {
            return UrlConfig.UAT_RESTFUL_SERVICE_HOST;
        } else if (BuildConfig.DEBUG) {
            return UrlConfig.URL_HK_TEST;
        } else {
            return RESTFUL_SERVICE_HOST;
        }
    }

    public static final String getRestfulVIPServiceHost() {
        if (BaseApp.getAppBean() != null && BaseApp.getAppBean().getVipApiUrl() != null && !BaseApp.getAppBean().getVipApiUrl().equals("")) {
            return BaseApp.getAppBean().getVipApiUrl();
        }

        if (BuildConfig.SIT || BuildConfig.UAT) {
            return UrlConfig.TEST_RESTFUL_VIP_SERVICE_HOST;
        }
        return UrlConfig.RESTFUL_VIP_SERVICE_HOST;
    }

    private static void setHeaders() {
        CustomerInfo customer = CustomerAccountManager.getInstance().getCustomer();
        if (customer != null) {
            OkHttpClientManager.setHeaders(customer.getAuthenticationKey(), customer.getUserID(), customer.getUserName());
            Log.i("wxj", "heart hehe " + customer.getAuthenticationKey());
        } else {
            Log.i("wxj", "heart is null");
        }
    }

    protected static void setSpecificHeaders(String connection) {
        OkHttpClientManager.setHeaders(connection);
    }

    protected static String read(String urlString) throws IOException, ServiceException, BizException, IllegalArgumentException {
        if (urlString.indexOf("?") > 0) {
            urlString += "&v=" + String.valueOf(System.currentTimeMillis());
        } else {
            urlString += "?v=" + String.valueOf(System.currentTimeMillis());
        }

        TestUtil.print("url: " + StringUtil.decodeURL(urlString));
        setHeaders();
        if (urlString.contains("testconnection")) {
            setSpecificHeaders("Close");
            //setSpecificHeaders("Keep-Alive");
        } else {
            setSpecificHeaders("Keep-Alive");
        }
        String verifyKey = "";
        CustomerInfo customer = CustomerAccountManager.getInstance().getCustomer();
        if (customer != null) {
            Log.i("wxj", "verify " + urlString);
            verifyKey = StringUtil.getMd5Hash(customer.getUserName() + "+" + BaseService.SALT + "+" + StringUtil.decodeURL(cutUrl(urlString)));
            Log.i("speed", "verfiykey " + urlString + " " + verifyKey);
        }
        TestUtil.print(urlString + " " + OkHttpClientManager.getInstance().getAuthKey() + " " + OkHttpClientManager.getInstance().getUserID() + " " + OkHttpClientManager.getInstance().getUserName() + " " + verifyKey);
        Log.i("speed", "read get " + Thread.currentThread().toString());
        Response response = OkHttpClientManager.get(urlString, verifyKey);
        TestUtil.print("response: " + response.toString());
        String body = response.body().string();
        TestUtil.print("response bodywxj: " + urlString + "body " + body);
        Log.i("speed", urlString + " " + response.code());
        if (response.code() != 200) {
            Log.i("speed", "no200: " + urlString + "  " + response.code());
            //throw new IOException();
        }
        return body;
    }

    protected static Response getPic(String urlString) throws IOException, ServiceException, BizException, IllegalArgumentException {
        setHeaders();
        Log.i("speed", "read get " + Thread.currentThread().toString());
        Response response = OkHttpClientManager.get(urlString, "");
        BaseApp.CAPTCHA_KEY = response.header("captcha_key");
        return response;
    }

    protected static String create(String urlString, String bodyString) throws
            IOException, ServiceException, BizException, IllegalArgumentException {
        Log.i("wxj", "chat body " + bodyString);
        Log.i("transfer", "url: " + StringUtil.decodeURL(urlString) + "," + StringUtil.decodeURL(bodyString));
//        if (urlString.indexOf("?") > 0) {
//            urlString += "&v=" + String.valueOf(System.currentTimeMillis());
//        } else {
//            urlString += "?v=" + String.valueOf(System.currentTimeMillis());
//        }
        setHeaders();
        String verifyKey = "";
        CustomerInfo customer = CustomerAccountManager.getInstance().getCustomer();
        String plus = "+";
        if (customer != null) {
            if (bodyString != null && bodyString.isEmpty())
                plus = "";
            String s = customer.getUserName() + "+" + BaseService.SALT + "+" + StringUtil.decodeURL(cutUrl(urlString)) + plus + StringUtil.decodeURL(bodyString);
            verifyKey = StringUtil.getMd5Hash(s);
        }
        Response response = OkHttpClientManager.post(urlString, bodyString, verifyKey);
//
//        Log.i("wxj","chat cookie "+ CookieManager.getInstance().getCookie(urlString) + " "+ CookieManager.getInstance().acceptCookie() +"" +urlString);
        TestUtil.print("response: " + response.toString());
        String body = response.body().string();
        TestUtil.print("response bodywxj: " + urlString + "body " + body);
        if (response.code() != 200) {
            throw new IOException();
        }
        return body;
    }

    protected static String[] chat(String urlString, String bodyString) throws
            IOException, ServiceException, BizException, IllegalArgumentException {
        setHeaders();
        String[] ss = new String[2];
        Response response = OkHttpClientManager.post(urlString, bodyString, "");
        String body = response.body().string();
        String cookie = response.header("Set-Cookie");
        ss[0] = body;
        ss[1] = cookie;
        TestUtil.print("response body: " + body);
        if (response.code() != 200) {
            throw new IOException();
        }
        return ss;
    }

    protected static String update(String urlString, String bodyString, String API) throws IOException,
            ServiceException, BizException {
        return create(urlString, bodyString);
    }

    protected static String delete(String urlString) throws IOException, ServiceException, BizException {
        setHeaders();
        String verifyKey = "";
        CustomerInfo customer = CustomerAccountManager.getInstance().getCustomer();
        String plus = "+";
        if (customer != null) {
            verifyKey = StringUtil.getMd5Hash(customer.getUserName() + "+" + BaseService.SALT + "+" + StringUtil.decodeURL(urlString));
        }
        Response response = OkHttpClientManager.post(urlString, "", verifyKey);

        return response.body().string();
    }

    private static <T> void checkResponseResult(com.hec.app.entity.Response<T> response) throws BizException {
        if (!response.getSuccess()) {
            TestUtil.print("result fail");
            throw new BizException("600", response.getMessage());
        }
    }

    protected <T> T getResult(String html, Type messageType) throws BizException, JsonParseException {
        Gson g = new Gson();
        com.hec.app.entity.Response<T> r = g.fromJson(html, messageType);
        if (r != null) {
            checkResponseResult(r);
        } else {
            throw new JsonParseException("数据包解析错误");
        }
        return r.getData();
    }

    protected com.hec.app.entity.Response getAllResult(String html, Type messageType) throws BizException, JsonParseException {
        Gson g = new Gson();
        com.hec.app.entity.Response r = g.fromJson(html, messageType);
        if (r != null) {
            checkResponseResult(r);
        } else {
            throw new JsonParseException("数据包解析错误");
        }
        return r;
    }

    private static String cutUrl(String url) {
        String output = "";
        if (url != null) {
            for (int i = 3; i < url.split("/").length; i++) {
                output = output + url.split("/")[i] + "/";
            }
//            Log.i("wxj","verify " + output.substring(0,output.length()-1));
            if (output.length() != 0) {
                return output.substring(0, output.length() - 1);
            } else {
                return "";
            }

        } else {
            return output;
        }
    }
}