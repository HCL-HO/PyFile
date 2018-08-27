package com.hec.app.webservice;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.hec.app.BuildConfig;
import com.hec.app.config.UrlConfig;
import com.hec.app.entity.AppBean;
import com.hec.app.entity.BestPrizeInfo;
import com.hec.app.entity.BizException;
import com.hec.app.entity.BulletinInfo;
import com.hec.app.entity.EggInfo;
import com.hec.app.entity.GameListInfo;
import com.hec.app.entity.LatestWinningListInfo;
import com.hec.app.entity.Response;
import com.hec.app.entity.ServerTimeResponseinfo;
import com.hec.app.entity.ServiceRequestResult;
import com.hec.app.entity.UserInfo;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by hec on 2015/10/27.
 */
public class HomeService extends BaseService {
    private static String ENTRANCE_INFO = "entrance_info";

    public String getHomeImages() throws IOException, JsonParseException, ServiceException, BizException {
        String html = read("http://210.200.219.176:8073/Test/GetUser");
        Gson g = new Gson();
        UserInfo u = g.fromJson(html, UserInfo.class);
        Log.i("HomeService", u.getUserName());
        Log.i("HomeService", "请求完成");
        Log.i("HomeService", html);
        return html;
    }

    public List<BulletinInfo> getLatestBulletins() throws IOException, JsonParseException, ServiceException, BizException {
        Gson g = new Gson();
        Uri.Builder b = Uri.parse(getRestfulServiceHost()).buildUpon();
        b.path("/Home/LatestBulletins");
        //b.path("/home/bulletins");
        String url = b.build().toString();
        String html = read(url);

        Type messageType = new TypeToken<Response<List<BulletinInfo>>>() {
        }.getType();
        return getResult(html, messageType);
    }

    public List<BulletinInfo> getBulletins(int pageindex, int pagesize)
            throws IOException, JsonParseException, ServiceException, BizException {
        Gson g = new Gson();
        Uri.Builder b = Uri.parse(getRestfulServiceHost()).buildUpon();
        String bulletpath = "/home/bulletinscache";
        b.path(bulletpath);
        b.appendQueryParameter("offset", pageindex + "");
        b.appendQueryParameter("limit", pagesize + "");
        String url = b.build().toString();
        String html = read(url);
        Type messageType = new TypeToken<Response<List<BulletinInfo>>>() {
        }.getType();
        return getResult(html, messageType);

    }

    public BestPrizeInfo getTodayBestPrize() throws IOException, JsonParseException, ServiceException, BizException {
        Gson g = new Gson();
        Uri.Builder b = Uri.parse(getRestfulServiceHost()).buildUpon();
        String bulletpath = "/home/todaybestprizecache";
        b.path(bulletpath);
        String url = b.build().toString();
        String html = read(url);
//        TestUtil.print("getTodayBestPrize " + html);
        Type messageType = new TypeToken<Response<BestPrizeInfo>>() {
        }.getType();
        return getResult(html, messageType);
    }

    public BestPrizeInfo getUserBestPrize() throws IOException, JsonParseException, ServiceException, BizException {
        Gson g = new Gson();
        Uri.Builder b = Uri.parse(getRestfulServiceHost()).buildUpon();
        String bulletpath = "/home/userbestprize";
        b.path(bulletpath);
        String url = b.build().toString();
        String html = read(url);
        Type messageType = new TypeToken<Response<BestPrizeInfo>>() {
        }.getType();
        return getResult(html, messageType);
    }

    private String getEntrance() {
        if (BuildConfig.SIT) {
            return APP_INFO_HOST = UrlConfig.APP_INFO_HOST_LIST.get(3);
        } else if (BuildConfig.UAT) {
            return APP_INFO_HOST = UrlConfig.APP_INFO_HOST_LIST.get(4);
        } else if (BuildConfig.DEBUG) {
            return APP_INFO_HOST = UrlConfig.APP_INFO_HOST_LIST.get(2);
        } else {
            return APP_INFO_HOST;
        }
    }

    public AppBean getAppInfo() throws IOException, JsonParseException, ServiceException, BizException {
        Gson g = new Gson();
        Uri.Builder b = Uri.parse(getEntrance()).buildUpon();   // http://10.99.1.142:7878
        String bulletpath = "/entrance/info";
        b.path(bulletpath);
        String url = b.build().toString();
        String html = read(url);
        Log.i("speed", "entrance " + html);
        Type messageType = new TypeToken<Response<AppBean>>() {
        }.getType();
        Response<AppBean> entranceResponse = g.fromJson(html, messageType);
        return entranceResponse.getData();
    }

    public List<EggInfo> getEasterEgg() throws IOException, JsonParseException, ServiceException, BizException {
        Gson g = new Gson();
        Uri.Builder b = Uri.parse(getRestfulServiceHost()).buildUpon();
        String bulletpath = "/home/colouregg";
        b.path(bulletpath);
        String url = b.build().toString();
        String html = read(url);
        Type messageType = new TypeToken<Response<List<EggInfo>>>() {
        }.getType();
        //Log.i("wxj", html);
        return getResult(html, messageType);
    }

    public ServiceRequestResult redeemEasterEgg(int eggType) throws IOException, JsonParseException, ServiceException, BizException {
        Gson g = new Gson();
        Uri.Builder b = Uri.parse(getRestfulServiceHost()).buildUpon();
        String bulletpath = "/home/opencolouregg";
        b.path(bulletpath);
        String url = b.build().toString();
        JSONObject json = new JSONObject();
        try {
            json.put("eggType", eggType);
        } catch (Exception e) {

        }
        String html = create(url, json.toString());
        //Log.i("hec", html);
        Type messageType = new TypeToken<ServiceRequestResult>() {
        }.getType();
        return g.fromJson(html, messageType);
    }

    public Response testURLSpeed(String backupurl) throws IOException, JsonParseException, ServiceException, BizException {
        Gson g = new Gson();
        Uri.Builder b = Uri.parse(backupurl).buildUpon();
        String bulletpath = "/account/testconnection/";///account/testconnection/
        b.path(bulletpath);
        String url = b.build().toString();
        String html = read(url);
        Type messageType = new TypeToken<Response>() {
        }.getType();
        return g.fromJson(html, messageType);
    }

    public ServerTimeResponseinfo getServerTime() throws IOException, JsonParseException, ServiceException, BizException {
        Gson g = new Gson();
        Uri.Builder b = Uri.parse(getRestfulServiceHost()).buildUpon();
        b.path("/home/servertime/");
        String url = b.build().toString();
        String html = read(url);
        Log.i("wxj", "servertime: " + html);
        Type messageType =
                new TypeToken<ServerTimeResponseinfo>() {
                }.getType();
        return g.fromJson(html, messageType);
    }

    public Bitmap getBannerImage(String imageurl) throws IOException, JsonParseException, ServiceException, BizException {
        Gson g = new Gson();
        Uri.Builder b = Uri.parse(imageurl).buildUpon();
        String url = b.build().toString();
        com.squareup.okhttp.Response html = getPic(url);
        InputStream is = html.body().byteStream();
        Bitmap bm = BitmapFactory.decodeStream(is);
        return bm;
    }

    public String getUrls() throws IOException, JsonParseException, ServiceException, BizException {
        String urlStr = BaseService.BRAND_URL;
        if (urlStr.isEmpty()) {
            urlStr = UrlConfig.BRAND_URL;
        }

        Uri.Builder builder = Uri.parse(urlStr).buildUpon();
        String url = builder.build().toString();
        String html = read(url);
        return html;
    }

    public Response testBrandSpeed(String backupurl) throws IOException, JsonParseException, ServiceException, BizException {
        Gson g = new Gson();
        Uri.Builder builder = Uri.parse(backupurl).buildUpon();
        String url = builder.build().toString();
        String html = read(url);
        Type messageType = new TypeToken<Response>() {
        }.getType();
        return g.fromJson(html, messageType);
    }

    public List<GameListInfo> getGameList() throws IOException, JsonParseException, ServiceException, BizException {
        Gson g = new Gson();
        Uri.Builder b = Uri.parse(BaseService.SLOT_URL).buildUpon();
        b.path("/game/getgames/");
        //b.appendQueryParameter("dev","");
        String url = b.build().toString();
        String html = read(url);
        Type messageType =
                new TypeToken<List<GameListInfo>>() {
                }.getType();
        return g.fromJson(html, messageType);
    }

    public List<LatestWinningListInfo> getLatestWinningList(String period) throws IOException, JsonParseException, ServiceException, BizException {
        Gson g = new Gson();
        Uri.Builder b = Uri.parse(getRestfulServiceHost()).buildUpon();
        b.path("/home/GetLatestWinningList");
        b.appendQueryParameter("period", period);
        String url = b.build().toString();
        String html = read(url);
        Type messageType = new TypeToken<Response<List<LatestWinningListInfo>>>() {
        }.getType();
        return getResult(html, messageType);
    }

    public Response vipServerLogin() throws IOException, JsonParseException, ServiceException, BizException {
        Type messageType = new TypeToken<Response>() {
        }.getType();
        Uri.Builder b = Uri.parse(getRestfulVIPServiceHost()).buildUpon();
        b.path("/home/LogOnVIPValidation");
        String url = b.build().toString();
        String html = create(url, "");
        return getAllResult(html, messageType);
    }

    public Response getVipMqUrl() throws IOException, JsonParseException, ServiceException, BizException {
        Type messageType = new TypeToken<Response>() {
        }.getType();
        Uri.Builder b = Uri.parse(getRestfulVIPServiceHost()).buildUpon();
        b.path("/account/GetMSMQAddr");
        String url = b.build().toString();
        String html = create(url, "");
        return getAllResult(html, messageType);
    }
}
