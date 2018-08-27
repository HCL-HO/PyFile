package com.hec.app.webservice;

import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.hec.app.entity.AliPayNewInfo;
import com.hec.app.entity.AlipayInfo;
import com.hec.app.entity.BankTypeInfo;
import com.hec.app.entity.BizException;
import com.hec.app.entity.DinpayInfo;
import com.hec.app.entity.OnlineBankInfo;
import com.hec.app.entity.OnlineInfo;
import com.hec.app.entity.Response;
import com.hec.app.entity.ServiceRequestResult;
import com.hec.app.entity.WechatPayResponse;

import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by Isaac on 26/2/2016.
 */
public class RechargeService extends BaseService{
    final String RECHARGE = "recharge";

    public RechargeService() {}

    public List<BankTypeInfo> getOnlineRechargeList (String amount) throws IOException, JsonParseException, ServiceException, BizException {
        Gson g = new Gson();
        List<BankTypeInfo> list;
        Uri.Builder b = Uri.parse(getRestfulServiceHost()).buildUpon();
        b.path("/" + RECHARGE + "/online");
        b.appendQueryParameter("amount", amount);
        String url = b.build().toString();
        String html = read(url);
        Type messageType = new TypeToken<Response<List<BankTypeInfo>>>() {
        }.getType();
        list = getResult(html, messageType);
        return list;
    }

    public OnlineInfo submitOnline(String BankTypeID, String amount) throws IOException, JsonParseException, ServiceException, BizException {
        Gson g = new Gson();
        String jsonString;
        Uri.Builder b = Uri.parse(getRestfulServiceHost()).buildUpon();
        b.path("/" + RECHARGE + "/submitonline");
        String url = b.build().toString();
        JSONObject json = new JSONObject();
        try {
            json.put("BankTypeID", BankTypeID);
            json.put("Amount", amount);
        } catch (Exception e) {

        }
        String html = create(url, json.toString());
        Type messageType = new TypeToken<Response<OnlineInfo>>() {
        }.getType();
        return getResult(html,messageType);
    }

    public List<BankTypeInfo> getWechatRechargeList (String amount, String isappinstalled) throws IOException, JsonParseException, ServiceException, BizException {
        Gson g = new Gson();
        List<BankTypeInfo> list;
        Uri.Builder b = Uri.parse(getRestfulServiceHost()).buildUpon();
        b.path("/" + RECHARGE + "/wechat");
        b.appendQueryParameter("amount", amount);
        b.appendQueryParameter("isappinstalled", isappinstalled);
        String url = b.build().toString();
        String html = read(url);
        Type messageType = new TypeToken<Response<List<BankTypeInfo>>>() {
        }.getType();
        list = getResult(html, messageType);
        return list;
    }

    public List<BankTypeInfo> getQQRechargeList (String amount) throws IOException, JsonParseException, ServiceException, BizException {
        Uri.Builder builder = Uri.parse(getRestfulServiceHost()).buildUpon();
        builder.path("/" + RECHARGE + "/tencent");
        builder.appendQueryParameter("amount", amount);

        String url = builder.build().toString();
        String html = read(url);
        Type messageType = new TypeToken<Response<List<BankTypeInfo>>>() {
        }.getType();

        List<BankTypeInfo> list;
        list = getResult(html, messageType);
        return list;
    }

    public List<BankTypeInfo> getJdPayRechargeList (String amount) throws IOException, JsonParseException, ServiceException, BizException {
        Uri.Builder builder = Uri.parse(getRestfulServiceHost()).buildUpon();
        builder.path("/" + RECHARGE + "/JDPay");
        builder.appendQueryParameter("amount", amount);

        String url = builder.build().toString();
        String html = read(url);
        Type messageType = new TypeToken<Response<List<BankTypeInfo>>>() {
        }.getType();

        List<BankTypeInfo> list;
        list = getResult(html, messageType);
        return list;
    }

    public List<BankTypeInfo> getOneTouchRechargeList (String amount) throws IOException, JsonParseException, ServiceException, BizException {
        Uri.Builder builder = Uri.parse(getRestfulServiceHost()).buildUpon();
        builder.path("/" + RECHARGE + "/OneTouch");
        builder.appendQueryParameter("amount", amount);

        String url = builder.build().toString();
        String html = read(url);
        Type messageType = new TypeToken<Response<List<BankTypeInfo>>>() {
        }.getType();

        List<BankTypeInfo> list;
        list = getResult(html, messageType);
        return list;
    }

    public Response<AlipayInfo> submitWechat(String BankTypeID, String amount) throws IOException, JsonParseException, ServiceException, BizException {
        Gson g = new Gson();
        AlipayInfo result;
        Uri.Builder b = Uri.parse(getRestfulServiceHost()).buildUpon();
        b.path("/" + RECHARGE + "/submitwechat");
        String url = b.build().toString();
        JSONObject json = new JSONObject();
        try {
            json.put("BankTypeID", BankTypeID);
            json.put("Amount", amount);
        } catch (Exception e) {
        }
        String html = create(url, json.toString());
        Log.i("wxj", html);
        Type messageType = new TypeToken<Response<AlipayInfo>>() {
        }.getType();
        return (new Gson()).fromJson(html, messageType);
    }

    public WechatPayResponse submitWechatTransfer(String userName, String amount) throws IOException, JsonParseException, ServiceException, BizException {
        Gson g = new Gson();
        AlipayInfo result;
        Uri.Builder b = Uri.parse(getRestfulServiceHost()).buildUpon();
        b.path("/" + RECHARGE + "/wechat2");
        String url = b.build().toString();
        JSONObject json = new JSONObject();
        try {
            json.put("userName", userName);
            json.put("amount", amount);
        } catch (Exception e) {
        }
        String html = create(url, json.toString());
        Log.i("wxj", html);
        Type messageType = new TypeToken<WechatPayResponse>() {}.getType();
        return (new Gson()).fromJson(html, messageType);
    }

    public Response<AlipayInfo> submitTencent(String BankTypeID, String amount) throws IOException, JsonParseException, ServiceException, BizException {
        Uri.Builder builder = Uri.parse(getRestfulServiceHost()).buildUpon();
        builder.path("/" + RECHARGE + "/submittencent");
        String url = builder.build().toString();
        JSONObject json = new JSONObject();
        try {
            json.put("BankTypeID", BankTypeID);
            json.put("Amount", amount);
        } catch (Exception e) {
        }
        String html = create(url, json.toString());
        Type messageType = new TypeToken<Response<AlipayInfo>>() {
        }.getType();
        return (new Gson()).fromJson(html, messageType);
    }

    public Response<AlipayInfo> submitJDPay(String BankTypeID, String amount) throws IOException, JsonParseException, ServiceException, BizException {
        Uri.Builder builder = Uri.parse(getRestfulServiceHost()).buildUpon();
        builder.path("/" + RECHARGE + "/SubmitJDPay");
        String url = builder.build().toString();
        JSONObject json = new JSONObject();
        try {
            json.put("BankTypeID", BankTypeID);
            json.put("Amount", amount);
        } catch (Exception e) {
        }
        String html = create(url, json.toString());
        Type messageType = new TypeToken<Response<AlipayInfo>>() {
        }.getType();
        return (new Gson()).fromJson(html, messageType);
    }

    public Response<AlipayInfo> submitOneTouch(String BankTypeID, String amount) throws IOException, JsonParseException, ServiceException, BizException {
        Uri.Builder builder = Uri.parse(getRestfulServiceHost()).buildUpon();
        builder.path("/" + RECHARGE + "/submitonetouch");
        String url = builder.build().toString();
        JSONObject json = new JSONObject();
        try {
            json.put("BankTypeID", BankTypeID);
            json.put("Amount", amount);
        } catch (Exception e) {
        }
        String html = create(url, json.toString());
        Type messageType = new TypeToken<Response<AlipayInfo>>() {
        }.getType();
        return (new Gson()).fromJson(html, messageType);
    }

    public Response<AlipayInfo> submitAlipay(String BankTypeID, String amount) throws IOException, JsonParseException, ServiceException, BizException {
        Gson g = new Gson();
        AlipayInfo result;
        Uri.Builder b = Uri.parse(getRestfulServiceHost()).buildUpon();
        b.path("/" + RECHARGE + "/submitalipaydirect");
        String url = b.build().toString();
        JSONObject json = new JSONObject();
        try {
            json.put("BankTypeID", BankTypeID);
            json.put("Amount", amount);
        } catch (Exception e) {

        }
        String html = create(url, json.toString());
        Log.i("Alipay", html);
        Type messageType = new TypeToken<Response<AlipayInfo>>() {
        }.getType();
        return (new Gson()).fromJson(html, messageType);
    }



    public AliPayNewInfo submitAlipayNew(String name, String amount) throws IOException, JsonParseException, ServiceException, BizException{
        Uri.Builder b = Uri.parse(getRestfulServiceHost()).buildUpon();
        b.path("/" + RECHARGE + "/submitalipay");
        String url = b.build().toString();
        JSONObject json = new JSONObject();
        try {
            json.put("ReferUserName", name);
            json.put("Amount", amount);
        } catch (Exception e) {

        }
        String html = create(url, json.toString());
        Log.i("wxj", "Alipay "+html);
        Type messageType = new TypeToken<Response<AliPayNewInfo>>() {
        }.getType();
        return getResult(html,messageType);
    }

    public List<BankTypeInfo> getAlipayRechargeList (String amount, String isappinstalled) throws IOException, JsonParseException, ServiceException, BizException {
        Gson g = new Gson();
        List<BankTypeInfo> list;
        Uri.Builder b = Uri.parse(getRestfulServiceHost()).buildUpon();
        b.path("/" + RECHARGE + "/alipaydirect");
        b.appendQueryParameter("amount", amount);
        b.appendQueryParameter("isappinstalled", isappinstalled);
        String url = b.build().toString();
        String html = read(url);
        Log.i("Alipay", html);
        Log.i("AlipayUrl", url);
        Type messageType = new TypeToken<Response<List<BankTypeInfo>>>() {
        }.getType();
        list = getResult(html, messageType);
        return list;
    }

    public ServiceRequestResult cancelAlipay() throws IOException, JsonParseException, ServiceException, BizException {
        Gson g = new Gson();
        Uri.Builder b = Uri.parse(getRestfulServiceHost()).buildUpon();
        b.path("/" + RECHARGE + "/cancelalipay");
        String url = b.build().toString();
        JSONObject json = new JSONObject();
        String html = create(url, "");
        Log.i("Cancel", html);
        Type messageType = new TypeToken<ServiceRequestResult>() {
        }.getType();
        return g.fromJson(html, messageType);
    }

    public DinpayInfo submitDinpay(String BankTypeID, String amount) throws IOException, JsonParseException, ServiceException, BizException {
        Gson g = new Gson();
        DinpayInfo result;
        Uri.Builder b = Uri.parse(getRestfulServiceHost()).buildUpon();
        b.path("/" + RECHARGE + "/submitdinpay");
        String url = b.build().toString();
        JSONObject json = new JSONObject();
        try {
            json.put("BankTypeID", BankTypeID);
            json.put("Amount", amount);
        } catch (Exception e) {

        }
        String html = create(url, json.toString());
     //   Log.i("Dinpay", html);
        Type messageType = new TypeToken<Response<DinpayInfo>>() {
        }.getType();
        result = getResult(html, messageType);
        return result;
    }

    public List<BankTypeInfo> getDinpayRechargeList (String amount) throws IOException, JsonParseException, ServiceException, BizException {
        Gson g = new Gson();
        List<BankTypeInfo> list;
        Uri.Builder b = Uri.parse(getRestfulServiceHost()).buildUpon();
        b.path("/" + RECHARGE + "/dinpay");
        b.appendQueryParameter("amount", amount);
        String url = b.build().toString();
        String html = read(url);
        Type messageType = new TypeToken<Response<List<BankTypeInfo>>>() {
        }.getType();
        list = getResult(html, messageType);
        return list;
    }

    public OnlineBankInfo submitOnlineBank(int type,float amount)throws IOException, JsonParseException, ServiceException, BizException {
        Gson g = new Gson();
        Uri.Builder b = Uri.parse(getRestfulServiceHost()).buildUpon();
        b.path("/" + RECHARGE + "/submitbank/");
        String url = b.build().toString();
        OnlineBankInfo onlineBankInfo = new OnlineBankInfo();
        onlineBankInfo.setAmount(amount);
        onlineBankInfo.setPaytype(type);
        String html = create(url, g.toJson(onlineBankInfo));
        Log.i("html","online bank " + html);
        Type messageType = new TypeToken<Response<OnlineBankInfo>>() {
        }.getType();
        return getResult(html, messageType);
    }

    public Response cancelAlipayNew(String name) throws IOException, JsonParseException, ServiceException, BizException {
        Uri.Builder b = Uri.parse(getRestfulServiceHost()).buildUpon();
        b.path("/" + RECHARGE + "/cancelalipay");
        String url = b.build().toString();
        JSONObject json = new JSONObject();
        try {
            json.put("ReferUserName", name);
        } catch (Exception e) {

        }
        String html = create(url, json.toString());
        Log.i("wxj", "Alipay "+html);
        Type messageType = new TypeToken<Response>() {
        }.getType();
        return new Gson().fromJson(html,messageType);
    }
}
