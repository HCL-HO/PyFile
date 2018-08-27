package com.hec.app.webservice;

import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.hec.app.entity.AgentListInfo;
import com.hec.app.entity.BizException;
import com.hec.app.entity.MemberInfo;
import com.hec.app.entity.OfflineTransferInfo;
import com.hec.app.entity.RebateUtil;
import com.hec.app.entity.Response;
import com.hec.app.entity.Result;
import com.hec.app.entity.ServiceRequestResult;
import com.hec.app.util.StringUtil;

import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by Isaac on 5/2/2016.
 */
public class AgentService extends BaseService {
    final String AGENT = "agent";
    int defaultSize = 20;

    public AgentService() {
    }

    public List<AgentListInfo> getAgentList() throws IOException, JsonParseException, ServiceException, BizException {
        Gson g = new Gson();
        List<AgentListInfo> list;
        Uri.Builder b = Uri.parse(getRestfulServiceHost()).buildUpon();
        b.path("/" + AGENT + "/members");
        //       b.appendQueryParameter("offset", Integer.toString(getOffset(pageNum, defaultSize)));
        //       b.appendQueryParameter("limit", Integer.toString(defaultSize));
        Log.i("AGENT", "HERE");
        String url = b.build().toString();
        String html = read(url);
        Type messageType = new TypeToken<Response<List<AgentListInfo>>>() {
        }.getType();
        list = getResult(html, messageType);
        return list;
    }

    public RebateUtil getRebateQuotas() throws IOException, JsonParseException, ServiceException, BizException {
        Gson g = new Gson();
        String jsonString;
        RebateUtil r = new RebateUtil();
        Uri.Builder b = Uri.parse(getRestfulServiceHost()).buildUpon();
        b.path("/" + AGENT + "/quotas");
        String url = b.build().toString();
        String html = read(url);

        try {
            JSONObject json = new JSONObject(html);
            if (!json.getBoolean("success")) {
                r.setSuccess(false);
                throw new BizException("600", "获取返点配额时发生错误, 请稍後再试");
            } else {
                html = json.get("data").toString();
                r.setQuotas(json.getJSONObject("data").getJSONArray("quotas"));
            }
        } catch (Exception e) {
            r = new RebateUtil();
            r.setSuccess(false);
            e.printStackTrace();
        }

        return r;
    }

    public MemberInfo getMemberInfo(String ID) throws IOException, JsonParseException, ServiceException, BizException {
        Gson g = new Gson();
        MemberInfo memberInfo = new MemberInfo();
        Uri.Builder b = Uri.parse(getRestfulServiceHost()).buildUpon();
        b.path("/" + AGENT + "/members/" + ID);
        String url = b.build().toString();
        String html = read(url);

        Type messageType = new TypeToken<Response<MemberInfo>>() {
        }.getType();

        memberInfo = getResult(html, messageType);
        Log.i("AGENT", html);
        return memberInfo;
    }

    public String getAvailableRebateList() throws IOException, JsonParseException, ServiceException, BizException {
        Gson g = new Gson();
        String jsonString;
        Uri.Builder b = Uri.parse(getRestfulServiceHost()).buildUpon();
        b.path("/" + AGENT + "/rebates");
        String url = b.build().toString();
        String html = read(url);
        jsonString = html;
        Log.i("AGENT", html);
        try {
            jsonString = (new JSONObject(html)).getJSONObject("data").get("rebates").toString();
        } catch (Exception e) {
            e.printStackTrace();
            jsonString = "";
        }
        return jsonString;
    }

    public ServiceRequestResult addMember(String memberName, String pw, String rebate) throws IOException, JsonParseException, ServiceException, BizException {
        Gson g = new Gson();
        Uri.Builder b = Uri.parse(getRestfulServiceHost()).buildUpon();
        b.path("/" + AGENT + "/addmember");
        String url = b.build().toString();
        JSONObject json = new JSONObject();
        try {
            json.put("MemberName", memberName);
            json.put("MemberPwd", StringUtil.encrypt(pw));
            json.put("rebate", rebate);
        } catch (Exception e) {

        }
        String html = create(url, json.toString());
        Type messageType = new TypeToken<ServiceRequestResult>() {
        }.getType();
        return g.fromJson(html, messageType);
    }

    public Result<String> sendCaptcha() throws IOException, JsonParseException, ServiceException, BizException {
        Gson g = new Gson();
        Uri.Builder b = Uri.parse(getRestfulServiceHost()).buildUpon();
        b.path("/account/SendSMSCodeNoLimit");
        String url = b.build().toString();
        String html = create(url, "");
        Type messageType = new TypeToken<Result<String>>() {
        }.getType();
        return g.fromJson(html, messageType);
    }

    public ServiceRequestResult transferMoney(String memberName, String pw, String amount) throws IOException, JsonParseException, ServiceException, BizException {
        Gson g = new Gson();
        Uri.Builder b = Uri.parse(getRestfulServiceHost()).buildUpon();
        b.path("/" + AGENT + "/transfer");
        String url = b.build().toString();
        JSONObject json = new JSONObject();
        try {
            json.put("LowUserName", memberName);
            json.put("MoneyPwd", StringUtil.encrypt(pw));
            json.put("Money", amount);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String html = create(url, json.toString());
        Type messageType = new TypeToken<ServiceRequestResult>() {
        }.getType();
        return g.fromJson(html, messageType);
    }

    public Result<OfflineTransferInfo> transferMoneyWithCaptcha(String memberName, String pw, String amount, boolean hasSMS, String captcha, boolean check30Min) throws IOException, JsonParseException, ServiceException, BizException {
        Gson g = new Gson();
        Uri.Builder b = Uri.parse(getRestfulServiceHost()).buildUpon();
        b.path("/account/TransferOfflineVerification");
        String url = b.build().toString();
        JSONObject json = new JSONObject();
        try {
            json.put("userName", memberName);
            json.put("money", amount);
            json.put("moneypwd", StringUtil.encrypt(pw));
            json.put("hasSMS", hasSMS);
            json.put("verificationCode", captcha);
            json.put("checkThirdMinCache", check30Min);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String html = create(url, json.toString());
        Type messageType = new TypeToken<Result<OfflineTransferInfo>>() {
        }.getType();
        return g.fromJson(html, messageType);
    }

    public String getReferralLink(String rebate) throws IOException, JsonParseException, ServiceException, BizException {
        Gson g = new Gson();
        String jsonString;
        Uri.Builder b = Uri.parse(getRestfulServiceHost()).buildUpon();
        b.path("/" + AGENT + "/referral");
        b.appendQueryParameter("rebate", rebate);
        String url = b.build().toString();
        String html = read(url);

        jsonString = html;
        try {
            jsonString = (new JSONObject(html)).getJSONObject("data").get("url").toString();
        } catch (Exception e) {
            e.printStackTrace();
            jsonString = "";
        }
    /*    Type messageType = new TypeToken<Response<String>>() {
        }.getType();
        return getResult(html, messageType);*/
        return jsonString;
    }

    public ServiceRequestResult toggleTransfer(String ID, boolean enable) throws IOException, JsonParseException, ServiceException, BizException {
        Gson g = new Gson();
        String jsonString;
        Uri.Builder b = Uri.parse(getRestfulServiceHost()).buildUpon();
        b.path("/" + AGENT + "/enabletransfer");
        String url = b.build().toString();
        JSONObject json = new JSONObject();
        try {
            json.put("UserID", ID);
            json.put("IsEnable", enable);
        } catch (Exception e) {

        }
        String html = create(url, json.toString());
        Log.i("AGENT", html);
        Type messageType = new TypeToken<ServiceRequestResult>() {
        }.getType();
        return g.fromJson(html, messageType);
    }

    public ServiceRequestResult updateRebate(String ID, String rebate) throws IOException, JsonParseException, ServiceException, BizException {
        Gson g = new Gson();
        String jsonString;
        Uri.Builder b = Uri.parse(getRestfulServiceHost()).buildUpon();
        b.path("/" + AGENT + "/updaterebate");
        String url = b.build().toString();
        JSONObject json = new JSONObject();
        try {
            json.put("UserID", ID);
            json.put("rebate", rebate);
        } catch (Exception e) {

        }
        String html = create(url, json.toString());
        Log.i("AGENT", html);
        Type messageType = new TypeToken<ServiceRequestResult>() {
        }.getType();
        return g.fromJson(html, messageType);
    }

    public int getOffset(int pageNum, int size) {
        if (pageNum == 0)
            return 0;
        return pageNum * size + 1;
    }

    public int getDefaultSize() {
        return defaultSize;
    }

    public void setDefaultSize(int val) {
        defaultSize = val;
    }
}
