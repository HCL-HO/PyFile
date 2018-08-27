package com.hec.app.webservice;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.hec.app.activity.LoginActivity;
import com.hec.app.entity.AfterDetailLotteryInfo;
import com.hec.app.entity.AfterLotteryInfo;
import com.hec.app.entity.BizException;
import com.hec.app.entity.DetailLotteryInfo;
import com.hec.app.entity.PartlyLotteryInfo;
import com.hec.app.entity.Response;
import com.hec.app.entity.SlotDataInfo;
import com.hec.app.entity.SlotInfo;
import com.hec.app.framework.widget.MyToast;
import com.hec.app.util.TestUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class DetailLotteryService extends BaseService {
    private static String msg;
    public DetailLotteryService() {

    }

    public List<PartlyLotteryInfo> getDetailLotteryInfo(String state,int offset,int limit) throws IOException, JsonParseException, ServiceException, BizException {
        Uri.Builder b = Uri.parse(getRestfulServiceHost()).buildUpon();
        b.path("/history/orders");
        b.appendQueryParameter("state", state);
        b.appendQueryParameter("offset", offset + "");
        b.appendQueryParameter("limit", limit + "");
        String url = b.build().toString();
        String html = read(url);
        //TestUtil.print(url);
        //TestUtil.print(html);
        Type messageType = new TypeToken<Response<List<PartlyLotteryInfo>>>(){}.getType();
        return getResult(html,messageType);
    }

    public DetailLotteryInfo getDetailLotteryInfoByOrder(int PlayID) throws IOException, JsonParseException, ServiceException, BizException {
        Uri.Builder b = Uri.parse(getRestfulServiceHost()).buildUpon();
        String s="/history/orders/"+PlayID+"";
        b.path(s);
        String url = b.build().toString();
        String html = read(url);
        Log.i("lala",url);
        Log.i("lala","getDetailLotteryInfoByOrder:   "+html);
        //TestUtil.print(url);
        //TestUtil.print(html);
        Type messageType = new TypeToken<Response<DetailLotteryInfo>>(){}.getType();
        return getResult(html,messageType);
    }

    public List<AfterLotteryInfo> getAfterLotteryInfo(String state,int offset,int limit) throws IOException, JsonParseException, ServiceException, BizException{
        Uri.Builder b = Uri.parse(getRestfulServiceHost()).buildUpon();
        b.path("/history/after");
        b.appendQueryParameter("state",state);
        b.appendQueryParameter("offset", offset + "");
        b.appendQueryParameter("limit", limit + "");
        String url = b.build().toString();
        String html = read(url);

        Type messageType = new TypeToken<Response<List<AfterLotteryInfo>>>(){}.getType();
        return getResult(html,messageType);
    }

    public AfterDetailLotteryInfo getAfterLotteryInfoByOrder(int AfterNoID) throws IOException, JsonParseException, ServiceException, BizException{
        Uri.Builder b = Uri.parse(getRestfulServiceHost()).buildUpon();
        String s = "/history/after/"+AfterNoID+"";
        b.path(s);
        String url = b.build().toString();
        String html = read(url);
        Type messageType = new TypeToken<Response<AfterDetailLotteryInfo>>(){}.getType();
        return getResult(html,messageType);
    }

    public SlotInfo getSlotDetailInfo(long slotID) throws IOException, JsonParseException, ServiceException, BizException{
        Uri.Builder b = Uri.parse(getRestfulServiceHost()).buildUpon();
        String s = "/history/slots/"+slotID;
        b.path(s);
        String url = b.build().toString();
        String html = read(url);
        Type messageType = new TypeToken<Response<SlotInfo>>(){}.getType();
        return getResult(html,messageType);
    }

    public com.hec.app.entity.Response<DetailLotteryInfo> withdrawLottery(int playid)throws IOException,
            JsonParseException, ServiceException, BizException{
        Uri.Builder b = Uri.parse(getRestfulServiceHost()).buildUpon();
        b.path("/history/cancelorder");
        String url = b.build().toString();
        Gson g = new Gson();
        DetailLotteryInfo info = new DetailLotteryInfo();
        info.setOrderID(playid);
        String html = create(url, g.toJson(info));
        //Log.i("wxj","withdrawLottery"+html);
        Type messageType = new TypeToken<Response<DetailLotteryInfo>>(){}.getType();
        com.hec.app.entity.Response<DetailLotteryInfo> r = g.fromJson(html, messageType);
        return r;
    }

    public com.hec.app.entity.Response<AfterDetailLotteryInfo> stopLottery(int afternoid)throws IOException,
            JsonParseException, ServiceException, BizException{
        Uri.Builder b = Uri.parse(getRestfulServiceHost()).buildUpon();
        b.path("/history/cancelafter");
        String url = b.build().toString();
        Gson g = new Gson();
        AfterDetailLotteryInfo info = new AfterDetailLotteryInfo();
        info.setAfterNoID(afternoid);
        String html = create(url,g.toJson(info));
        //Log.i("wxj",html);
        Type messageType = new TypeToken<Response<AfterDetailLotteryInfo>>(){}.getType();
        com.hec.app.entity.Response<AfterDetailLotteryInfo> r = g.fromJson(html, messageType);
        return r;
    }

    public List<SlotInfo> getSlotInfo(String state,int offset,int limit)  throws IOException,
            JsonParseException, ServiceException, BizException{
        Uri.Builder b = Uri.parse(getRestfulServiceHost()).buildUpon();
        b.path("/history/slots/");
        b.appendQueryParameter("state", state);
        b.appendQueryParameter("offset", offset + "");
        b.appendQueryParameter("limit", limit + "");
        String url = b.build().toString();
        String html = read(url);
        Log.i("laohuji","laohuji "+html);
        Type messageType = new TypeToken<Response<List<SlotInfo>>>(){}.getType();
        Log.i("laohuji","after type");
        return getResult(html,messageType);
    }
}