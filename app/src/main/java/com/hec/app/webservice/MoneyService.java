package com.hec.app.webservice;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.hec.app.entity.BizException;

import com.hec.app.entity.MoneyDetailInfo;
import com.hec.app.entity.MoneyFundInfo;
import com.hec.app.entity.Response;
import com.hec.app.framework.widget.MyToast;
import com.hec.app.util.TestUtil;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by wangxingjian on 16/2/18.
 */
public class MoneyService extends BaseService {
    private Context context;
    public MoneyService(Context context) {
        this.context = context;
    }

    public MoneyFundInfo getMoneyFundInfo(String type)throws IOException, JsonParseException, ServiceException, BizException {
        Uri.Builder b = Uri.parse(getRestfulServiceHost()).buildUpon();
        String s  = "/fund/info";
        b.path(s);
        b.appendQueryParameter("type",type);
        String url = b.build().toString();
        String html = read(url);
        Type messageType = new TypeToken<Response<MoneyFundInfo>>(){}.getType();
        return getResult(html,messageType);
    }

    public List<MoneyDetailInfo> getMoneyDetailInfo(String type,String kindOfMoney,int offset,int limit) throws IOException, JsonParseException, ServiceException, BizException {
        Uri.Builder b = Uri.parse(getRestfulServiceHost()).buildUpon();
        String s  = "";
        if(kindOfMoney.equals("奖金派送")){
            s="/fund/prize";
        }else if(kindOfMoney.equals("充值&转入")){
            s="/fund/moneyin";
        }else if(kindOfMoney.equals("投注支出")){
            s="/fund/bet";
        }else if(kindOfMoney.equals("提现&转出")){
            s="/fund/moneyout";
        }
        b.path(s);
        b.appendQueryParameter("type",type);
        b.appendQueryParameter("offset",offset+"");
        b.appendQueryParameter("limit",limit+"");
        String url = b.build().toString();
        String html = read(url);
        TestUtil.print(url);
//        TestUtil.print(html);
        Type messageType = new TypeToken<Response<List<MoneyDetailInfo>>>(){}.getType();
        return getResult(html,messageType);
    }
}


