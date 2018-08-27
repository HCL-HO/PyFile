package com.hec.app.webservice;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.net.Uri.Builder;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.hec.app.entity.AmountInfo;
import com.hec.app.entity.BalanceInfo;
import com.hec.app.entity.BizException;
import com.hec.app.entity.ChangePwInfo;
import com.hec.app.entity.HomeBalanceInfo;
import com.hec.app.entity.LogOnInfo;
import com.hec.app.entity.LogonInfoNew;
import com.hec.app.entity.LogoncaptchaInfo;
import com.hec.app.entity.NicknameInfo;
import com.hec.app.entity.RecoverPWInfo;
import com.hec.app.entity.RecoverPwBySmsInfo;
import com.hec.app.entity.Response;
import com.hec.app.entity.Result;
import com.hec.app.entity.SecurityInfoFinishInfo;
import com.hec.app.entity.VipInfo;
import com.hec.app.util.StringUtil;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.List;

import org.apmem.tools.layouts.BuildConfig;
import org.json.JSONObject;

public class AccountService extends BaseService {

    public Response<LogOnInfo> logOn(String userName, String password) throws IOException, JsonParseException, ServiceException, BizException {
        Uri.Builder b = Uri.parse(getRestfulServiceHost()).buildUpon();
        b.path("/Account/LogOn");
        String url = b.build().toString();
        Gson g = new Gson();
        LogOnInfo info = new LogOnInfo();
        info.setUserName(userName);
        String pw = StringUtil.encrypt(password);
        Log.i("wxj","pwd encrypt  "+pw);
        info.setPassword(pw);
        Log.i("speed","before "+getRestfulServiceHost());
        String html = create(url, g.toJson(info));
        Type messageType = new TypeToken<Response<LogOnInfo>>() {}.getType();
        Log.i("speed","after net");
        return (new Gson()).fromJson(html, messageType);
    }

    public Response<LogonInfoNew> logOnWithCaptcha(String userName, String password, String verifyID, String captcha) throws IOException, JsonParseException, ServiceException, BizException {
        Uri.Builder b = Uri.parse(getRestfulServiceHost()).buildUpon();
        b.path("/Account/LogOn");
        String url = b.build().toString();
        Log.i("speed","logon "+url);
        Gson g = new Gson();
        LogoncaptchaInfo info = new LogoncaptchaInfo();
        info.setUserName(userName);
        String pw = StringUtil.encrypt(password);
        Log.i("wxj","pwd encrypt  "+pw);
        info.setPassword(pw);
        info.setCaptcha_key(verifyID);
        info.setCaptcha_value(captcha);
        String html = create(url, g.toJson(info));
        Log.i("login","captcha login: "+g.toJson(info));
        Type messageType = new TypeToken<Response<LogonInfoNew>>() {}.getType();
        return (new Gson()).fromJson(html, messageType);
    }

    public Result<Object> checkSMSSwitch() throws IOException, JsonParseException, ServiceException, BizException {
        Gson g = new Gson();
        Builder b = Uri.parse(getRestfulServiceHost()).buildUpon();
        b.path("/Account/CanParentToSonSMS");
        String url = b.build().toString();
        String html = create(url, "");
        Type messageType = new TypeToken<Result<Object>>() {
        }.getType();
        return g.fromJson(html, messageType);
    }

    public Result<Object> checkUserMonIn() throws IOException, JsonParseException, ServiceException, BizException {
        Gson g = new Gson();
        Builder b = Uri.parse(getRestfulServiceHost()).buildUpon();
        b.path("/account/CheckUserMonIn");
        String url = b.build().toString();
        String html = create(url, "");
        Type messageType = new TypeToken<Result<Object>>() {
        }.getType();
        return g.fromJson(html, messageType);
    }

    public SecurityInfoFinishInfo securityInfoFinish() throws IOException, JsonParseException, ServiceException, BizException {
        Type messageType = new TypeToken<Response<SecurityInfoFinishInfo>>(){}.getType();
        Builder b = Uri.parse(getRestfulServiceHost()).buildUpon();
        b.path("/Account/SecurityInfoFinish");
        String url = b.build().toString();
        String html = read(url);
        return getResult(html, messageType);
    }

    public Response<ChangePwInfo> changePw(int tag, String oldPw, String newPw) throws IOException, JsonParseException, ServiceException, BizException {
        String html = BuildConfig.FLAVOR;
        Type messageType = new TypeToken<Response<ChangePwInfo>>(){}.getType();
        Builder b;
        String url;
        Gson g;
        ChangePwInfo info;
        if (tag == 0) {
            b = Uri.parse(getRestfulServiceHost()).buildUpon();
            b.path("/Account/changeloginpassword");
            url = b.build().toString();
            g = new Gson();
            info = new ChangePwInfo();
            info.setOldPw(StringUtil.encrypt(oldPw));
            info.setNewPw(StringUtil.encrypt(newPw));
            html = create(url, g.toJson(info));
        } else if (tag == 1) {
            b = Uri.parse(getRestfulServiceHost()).buildUpon();
            b.path("/Account/changemoneypassword");
            url = b.build().toString();
            g = new Gson();
            info = new ChangePwInfo();
            info.setOldPw(StringUtil.encrypt(oldPw));
            info.setNewPw(StringUtil.encrypt(newPw));
            html = create(url, g.toJson(info));
        }
        return  (new Gson()).fromJson(html, messageType);
    }

    public Response changePwWithQuestion(String userName, String pw, int Question1ID,  int Question2ID, String Answer1, String Answer2) throws IOException, JsonParseException, ServiceException, BizException {
        Uri.Builder b = Uri.parse(getRestfulServiceHost()).buildUpon();
        Type messageType = new TypeToken<Response>(){}.getType();
        b.path("/Account/changepwdbyquestions");
        String url = b.build().toString();
        Gson g = new Gson();
        RecoverPWInfo info = new RecoverPWInfo();
        info.setUserName(userName);
        info.setNewPassword(StringUtil.encrypt(pw));
        info.setQuestion1ID(Question1ID);
        info.setQuestion2ID(Question2ID);
        info.setAnswer1(Answer1);
        info.setAnswer2(Answer2);
        String html = create(url, g.toJson(info));
        return  (new Gson()).fromJson(html, messageType);
    }

    public Response changePwWithFundPw(String userName, String pw) throws IOException, JsonParseException, ServiceException, BizException {
        Uri.Builder b = Uri.parse(getRestfulServiceHost()).buildUpon();
        Type messageType = new TypeToken<Response>(){}.getType();
        b.path("/Account/resetpwdbymoneypwd");
        String url = b.build().toString();
        Gson g = new Gson();
        RecoverPWInfo info = new RecoverPWInfo();
        info.setUserName(userName);
        info.setMoneyPwd(StringUtil.encrypt(pw));
        String html = create(url, g.toJson(info));
        return  (new Gson()).fromJson(html, messageType);
    }

    public Response changePwWithEmail(String userName, String email) throws IOException, JsonParseException, ServiceException, BizException {
        Uri.Builder b = Uri.parse(getRestfulServiceHost()).buildUpon();
        Type messageType = new TypeToken<Response>(){}.getType();
        b.path("/Account/resetpwdbyemail");
        String url = b.build().toString();
        Gson g = new Gson();
        RecoverPWInfo info = new RecoverPWInfo();
        info.setUserName(userName);
        info.setEmail(email);
        String html = create(url, g.toJson(info));
        return  (new Gson()).fromJson(html, messageType);
    }

    public Response findLoginPwdBySMS(String userName, String phone, String type) throws IOException, JsonParseException, ServiceException, BizException {
        Uri.Builder b = Uri.parse(getRestfulServiceHost()).buildUpon();
        Type messageType = new TypeToken<Response>(){}.getType();
        b.path("/Account/FindLoginPwdBySMS");
        String url = b.build().toString();
        Gson g = new Gson();
        RecoverPwBySmsInfo info = new RecoverPwBySmsInfo();
        info.setUserName(userName);
        info.setPhoneNumber(phone);
        info.setType(type);
        String html = create(url, g.toJson(info));
        return  (new Gson()).fromJson(html, messageType);
    }

    public BalanceInfo getBalance() throws IOException, JsonParseException, ServiceException, BizException {
        Type messageType = new TypeToken<Response<BalanceInfo>>(){}.getType();
        Builder b = Uri.parse(getRestfulServiceHost()).buildUpon();
        b.path("/account/balance");
        String html = read(b.build().toString());
        return getResult(html, messageType);
    }

    public List<RecoverPWInfo> getQuestion() throws IOException, JsonParseException, ServiceException, BizException {
        Type messageType = new TypeToken<Response<List<RecoverPWInfo>>>(){}.getType();
        Builder b = Uri.parse(getRestfulServiceHost()).buildUpon();
        b.path("/account/questions");
        String html = read(b.build().toString());
        Log.i("wxj","question "+html);
        return getResult(html, messageType);
    }

    public Response checkQuestion(String userName, int Question1ID,  int Question2ID, String Answer1, String Answer2) throws IOException, JsonParseException, ServiceException, BizException {
        Uri.Builder b = Uri.parse(getRestfulServiceHost()).buildUpon();
        b.path("/Account/checkquestions");
        b.appendQueryParameter("userName", userName)
                .appendQueryParameter("Question1ID", String.valueOf(Question1ID))
                .appendQueryParameter("Answer1", Answer1)
                .appendQueryParameter("Question2ID", String.valueOf(Question2ID))
                .appendQueryParameter("Answer2", Answer2);
        String url = b.build().toString();
        String html = read(url);
        Type messageType = new TypeToken<Response>() {
        }.getType();
        return  (new Gson()).fromJson(html, messageType);
    }

    public Response checkUserName(String userName) throws IOException, JsonParseException, ServiceException, BizException {
        Uri.Builder b = Uri.parse(getRestfulServiceHost()).buildUpon();
        b.path("/Account/checkusername");
        b.appendQueryParameter("UserName", userName);
        String url = b.build().toString();
        String html = read(url);
        Type messageType = new TypeToken<Response>() {
        }.getType();
        return (new Gson()).fromJson(html, messageType);
    }

    public HomeBalanceInfo getHomeBalanceInfo() throws IOException, JsonParseException, ServiceException, BizException {
        Uri.Builder b = Uri.parse(getRestfulServiceHost()).buildUpon();
        String bulletpath = "/account/simplebalance";
        b.path(bulletpath);
        String url = b.build().toString();
        String html = read(url);
        Log.i("speed","transfer "+html);
        Type messageType = new TypeToken<Response<HomeBalanceInfo>>() {
        }.getType();
        return getResult(html, messageType);
    }

    public HomeBalanceInfo getTransferBalanceInfo() throws IOException, JsonParseException, ServiceException, BizException {
        Uri.Builder b = Uri.parse(getRestfulServiceHost()).buildUpon();
        String bulletpath = "/account/balance";
        b.path(bulletpath);
        String url = b.build().toString();
        String html = read(url);
        Log.i("speed","transfer "+html);
        Type messageType = new TypeToken<Response<HomeBalanceInfo>>() {
        }.getType();
        return getResult(html, messageType);
    }

    public Response<?> sendHeartBeat() throws IOException, JsonParseException, ServiceException, BizException, IllegalArgumentException{
        Uri.Builder b = Uri.parse(getRestfulServiceHost()).buildUpon();
        b.path("/account/heartbeat");
        String url = b.build().toString();
        String html = create(url, "");
        Gson g = new Gson();
        Log.i("wxj","heartbeat");
        Type messageType = new TypeToken<Response<?>>() {
        }.getType();
        return g.fromJson(html, messageType);
    }

    public Response submitSecurityInfo(String moneypwd, int Question1ID,  int Question2ID, String Answer1, String Answer2, String email, String phone)
            throws IOException, JsonParseException, ServiceException, BizException {
        Uri.Builder b = Uri.parse(getRestfulServiceHost()).buildUpon();
        b.path("/Account/securityinfo");
        String url = b.build().toString();
        moneypwd = StringUtil.encrypt(moneypwd);
        JSONObject json = new JSONObject();
        try {
            json.put("moneypwd", moneypwd);
            json.put("question1id", Question1ID);
            json.put("answer1", Answer1);
            json.put("question2id", Question2ID);
            json.put("answer2", Answer2);
            json.put("email", email);
            json.put("PhoneNumber", phone);
        } catch (Exception e) {

        }
        String html = create(url, json.toString());
        Log.i("USERINFO", html);
        Type messageType = new TypeToken<Response>() {
        }.getType();
        return (new Gson()).fromJson(html, messageType);
    }

    public Response doTransfer(String tag, String playtype, double amount) throws IOException, JsonParseException, ServiceException, BizException {
        Uri.Builder b = Uri.parse(getRestfulServiceHost()).buildUpon();
        if(tag.equals("in")&&playtype.equals("realman")){
            b.path("/account/agtransferin/");
        } else if(tag.equals("in")&&playtype.equals("pt")){
            b.path("/account/pttransferin/");
        } else if(tag.equals("in")&&playtype.equals("sports")){
            b.path("/account/sporttransferin/");
        } else if(tag.equals("out")&&playtype.equals("realman")){
            b.path("/account/agtransferout/");
        } else if(tag.equals("out")&&playtype.equals("pt")){
            b.path("/account/pttransferout/");
        } else if(tag.equals("out")&&playtype.equals("sports")){
            b.path("/account/sporttransferout/");
        }
        Gson g = new Gson();
        AmountInfo a = new AmountInfo();
        a.setAmount(amount);
        String json = g.toJson(a);
        String url = b.build().toString();
        String html = create(url, json);

        Log.i("transfer","transfer "+html);
        Type messageType = new TypeToken<Response>() {
        }.getType();
        return g.fromJson(html, messageType);
    }

    public Bitmap getCaptcha() throws IOException, JsonParseException, ServiceException, BizException {
        Uri.Builder b = Uri.parse(getRestfulServiceHost()).buildUpon();
        b.path("/account/GetCaptchaCode");
        String url = b.build().toString();
        com.squareup.okhttp.Response html = getPic(url);
        InputStream is = html.body().byteStream();
        Bitmap bm = BitmapFactory.decodeStream(is);
        return bm;
    }

    public NicknameInfo getNicknameInfo() throws IOException, JsonParseException, ServiceException, BizException {
        Uri.Builder b = Uri.parse(getRestfulServiceHost()).buildUpon();
        String bulletpath = "/account/GetUserNickname";
        b.path(bulletpath);
        String url = b.build().toString();
        String html = read(url);
        Type messageType = new TypeToken<Response<NicknameInfo>>() {
        }.getType();
        return getResult(html, messageType);
    }

    public Response updateNicknameInfo(String nickname) throws IOException, JsonParseException, ServiceException, BizException {
        Uri.Builder b = Uri.parse(getRestfulServiceHost()).buildUpon();
        String bulletpath = "/account/UpdateUserNickname";
        b.path(bulletpath);
        String url = b.build().toString();
        NicknameInfo nicknameInfo = new NicknameInfo();
        nicknameInfo.setNickname(nickname);
        String html = create(url,new Gson().toJson(nicknameInfo));
        Type messageType = new TypeToken<Response>() {
        }.getType();
        return new Gson().fromJson(html,messageType);
    }

    public Response sendNumberSMS(String phoneNumber) throws IOException, JsonParseException, ServiceException, BizException {
        Uri.Builder b = Uri.parse(getRestfulServiceHost()).buildUpon();
        b.path("/Account/SendNumberSMS");
        String url = b.build().toString();

        JSONObject json = new JSONObject();
        try {
            json.put("PhoneNumber", phoneNumber);
        } catch (Exception e) {

        }

        String html = create(url, json.toString());
        Type messageType = new TypeToken<Response>() {}.getType();
        return (new Gson()).fromJson(html, messageType);
    }

    public Response updateIsPhoneRotection(String verificationCode) throws IOException, JsonParseException, ServiceException, BizException {
        Uri.Builder b = Uri.parse(getRestfulServiceHost()).buildUpon();
        b.path("/Account/UpdateIsPhoneRotection");
        String url = b.build().toString();

        JSONObject json = new JSONObject();
        try {
            json.put("VerificationCode", verificationCode);
        } catch (Exception e) {

        }

        String html = create(url, json.toString());
        Type messageType = new TypeToken<Response>() {}.getType();
        return (new Gson()).fromJson(html, messageType);
    }

    public Response isMoneyPwdEasy() throws IOException, JsonParseException, ServiceException, BizException {
        Uri.Builder b = Uri.parse(getRestfulServiceHost()).buildUpon();
        b.path("/Account/IsMoneyPwdEasy");
        String url = b.build().toString();

        String html = create(url, "");
        Type messageType = new TypeToken<Response>() {}.getType();
        return (new Gson()).fromJson(html, messageType);
    }

    public VipInfo isVIP() throws IOException, JsonParseException, ServiceException, BizException {
        Type messageType = new TypeToken<Response<VipInfo>>(){}.getType();
        Builder b = Uri.parse(getRestfulServiceHost()).buildUpon();
        b.path("/Account/isVIP");
        String url = b.build().toString();
        String html = read(url);
        return getResult(html, messageType);
    }

    public Response checkMoneyPwd(String moneyPassword) throws IOException, JsonParseException, ServiceException, BizException {
        Uri.Builder b = Uri.parse(getRestfulServiceHost()).buildUpon();
        b.path("/Account/CheckMoneyPwd");
        String url = b.build().toString();

        JSONObject json = new JSONObject();
        try {
            json.put("MoneyPwd", moneyPassword);
        } catch (Exception e) {

        }

        String html = create(url, json.toString());
        Type messageType = new TypeToken<Response>() {}.getType();
        return (new Gson()).fromJson(html, messageType);
    }

    public Response sendSMSCode() throws IOException, JsonParseException, ServiceException, BizException {
        Uri.Builder b = Uri.parse(getRestfulServiceHost()).buildUpon();
        b.path("/Account/SendSMSCode");
        String url = b.build().toString();

        String html = create(url, "");
        Type messageType = new TypeToken<Response>() {}.getType();
        return (new Gson()).fromJson(html, messageType);
    }

    public Response checkSMSCodeAndUnPhoneRotection(String verificationCode) throws IOException, JsonParseException, ServiceException, BizException {
        Uri.Builder b = Uri.parse(getRestfulServiceHost()).buildUpon();
        b.path("/Account/CheckSMSCodeAndUnPhoneRotection");
        String url = b.build().toString();

        JSONObject json = new JSONObject();
        try {
            json.put("VerificationCode", verificationCode);
        } catch (Exception e) {

        }

        String html = create(url, json.toString());
        Type messageType = new TypeToken<Response>() {}.getType();
        return (new Gson()).fromJson(html, messageType);
    }

    public Response checkSMSCode(String verificationCode) throws IOException, JsonParseException, ServiceException, BizException {
        Uri.Builder b = Uri.parse(getRestfulServiceHost()).buildUpon();
        b.path("/Account/CheckSMSCode");
        String url = b.build().toString();

        JSONObject json = new JSONObject();
        try {
            json.put("VerificationCode", verificationCode);
        } catch (Exception e) {

        }

        String html = create(url, json.toString());
        Type messageType = new TypeToken<Response>() {}.getType();
        return (new Gson()).fromJson(html, messageType);
    }

    public Response checkMoneyPwdAndBankInfo(String moneyPwd, String bankCard, String cardUser) throws IOException, JsonParseException, ServiceException, BizException {
        Uri.Builder b = Uri.parse(getRestfulServiceHost()).buildUpon();
        b.path("/Account/CheckMoneyPwdAndBankInfo");
        String url = b.build().toString();

        JSONObject json = new JSONObject();
        try {
            json.put("moneyPwd", moneyPwd);
            json.put("bankCard", bankCard);
            json.put("cardUser", cardUser);
        } catch (Exception e) {

        }

        String html = create(url, json.toString());
        Type messageType = new TypeToken<Response>() {}.getType();
        return (new Gson()).fromJson(html, messageType);
    }

    public Result<String> getBankShow() throws IOException, JsonParseException, ServiceException, BizException {
        Uri.Builder b = Uri.parse(getRestfulServiceHost()).buildUpon();
        b.path("/Account/GetBankShow");
        String url = b.build().toString();

        String html = create(url, "");
        Type messageType = new TypeToken<Result<String>>() {}.getType();
        Result<String> result = (new Gson()).fromJson(html, messageType);
        return (new Gson()).fromJson(html, messageType);
    }
}
