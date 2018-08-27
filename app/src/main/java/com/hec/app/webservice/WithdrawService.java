package com.hec.app.webservice;

import android.net.Uri;
import android.net.Uri.Builder;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.hec.app.entity.BankInfo;
import com.hec.app.entity.BankInfo.BankList;
import com.hec.app.entity.BizException;
import com.hec.app.entity.CityInfo;
import com.hec.app.entity.NewBankInfo;
import com.hec.app.entity.ProvinceInfo;
import com.hec.app.entity.Response;
import com.hec.app.entity.UpdBankInfo;
import com.hec.app.util.StringUtil;

import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class WithdrawService extends BaseService {

    public List<BankList> getBankList() throws IOException, JsonParseException, ServiceException, BizException {
        Type messageType = new TypeToken<Response<List<BankList>>>(){}.getType();
        Builder b = Uri.parse(getRestfulServiceHost()).buildUpon();
        b.path("/withdraw/banks");
        String html = read(b.build().toString());
        Log.i("wxj","banklist "+html);
        return (List) getResult(html, messageType);
    }

    public List<BankInfo> getBankCard() throws IOException, JsonParseException, ServiceException, BizException {
        Type messageType = new TypeToken<Response<List<BankInfo>>>(){}.getType();
        Builder b = Uri.parse(getRestfulServiceHost()).buildUpon();
        b.path("/withdraw/cards");
        String html = read(b.build().toString());
        return (List) getResult(html, messageType);
    }

    public Response addBankCard(int BankTypeId, String BankName, String BankCard, String CardUser) throws IOException, JsonParseException, ServiceException, BizException {
        Type messageType = new TypeToken<Response>(){}.getType();
        Builder b = Uri.parse(getRestfulServiceHost()).buildUpon();
        b.path("/withdraw/addcard");
        String url = b.build().toString();
        Gson g = new Gson();
        BankInfo info = new BankInfo();
        info.setBankTypeId(BankTypeId);
        info.setBankName(BankName);
        info.setBankCard(BankCard);
        info.setCardUser(CardUser);
        String html = BaseService.create(url, g.toJson(info));
        return (new Gson()).fromJson(html, messageType);
    }

    public Response<BankInfo> withdraw(int BankId, String BankName, Double Amount, String MoneyPassword) throws IOException, JsonParseException, ServiceException, BizException {
        Type messageType = new TypeToken<Response<BankInfo>>(){}.getType();
        Builder b = Uri.parse(getRestfulServiceHost()).buildUpon();
        b.path("/withdraw/submitrequest");
        String url = b.build().toString();
        Gson g = new Gson();
        BankInfo info = new BankInfo();
        info.setBankId(BankId);
        info.setBankName(BankName);
        info.setAmount(Amount);
        info.setMoneyPassword(StringUtil.encrypt(MoneyPassword));
        String html = BaseService.create(url, g.toJson(info));
        return (new Gson()).fromJson(html, messageType);
    }

    public List<ProvinceInfo> getProvince() throws IOException, JsonParseException, ServiceException, BizException {
        Type messageType = new TypeToken<Response<List<ProvinceInfo>>>(){}.getType();
        Builder b = Uri.parse(getRestfulServiceHost()).buildUpon();
        b.path("/withdraw/province");
        String url = b.build().toString();
        Gson g = new Gson();
        String html = read(url);
        Log.i("wxj","bank " + html);
        return getResult(html,messageType);
    }

    public List<CityInfo> getCity(int provinceid) throws IOException, JsonParseException, ServiceException, BizException {
        Type messageType = new TypeToken<Response<List<CityInfo>>>(){}.getType();
        Builder b = Uri.parse(getRestfulServiceHost()).buildUpon();
        b.path("/withdraw/city");
        b.appendQueryParameter("provincesid",String.valueOf(provinceid));
        String url = b.build().toString();
        Gson g = new Gson();
        String html = read(url);
        Log.i("wxj","bank city" + html);
        return getResult(html,messageType);
    }

    public Response addCardNew
            (int BankTypeId, String BankName, String BankCard, String CardUser
            ,String BankBranch, String BankCity, String Phone, String BankProvince
            )
            throws IOException, JsonParseException, ServiceException, BizException {
        Type messageType = new TypeToken<Response>(){}.getType();
        Builder b = Uri.parse(getRestfulServiceHost()).buildUpon();
        b.path("/withdraw/addcardnew");
        String url = b.build().toString();
        Gson g = new Gson();
        BankInfo info = new BankInfo();
        info.setBankTypeId(BankTypeId);
        info.setBankName(BankName);
        info.setBankCard(BankCard);
        info.setCardUser(CardUser);
        info.setBankBranch(BankBranch);
        info.setBankCity(BankCity);
        info.setBankProvince(BankProvince);
        info.setPhone(Phone);
        String html = BaseService.create(url, g.toJson(info));
        return (new Gson()).fromJson(html, messageType);
    }

    public Response<BankInfo> withdrawQuick(int BankId, String BankName, Double Amount, String MoneyPassword) throws IOException, JsonParseException, ServiceException, BizException {
        Type messageType = new TypeToken<Response<BankInfo>>(){}.getType();
        Builder b = Uri.parse(getRestfulServiceHost()).buildUpon();
        b.path("/withdraw/submitquickrequest");
        String url = b.build().toString();
        Gson g = new Gson();
        BankInfo info = new BankInfo();
        info.setBankId(BankId);
        info.setBankName(BankName);
        info.setAmount(Amount);
        info.setMoneyPassword(StringUtil.encrypt(MoneyPassword));
        String html = BaseService.create(url, g.toJson(info));
        return (new Gson()).fromJson(html, messageType);
    }

    public Response quickUpdate
            (int BankTypeId, String BankName, String BankCard, String CardUser
                    ,String BankBranch, String BankCity, String Phone, String BankProvince
            )
            throws IOException, JsonParseException, ServiceException, BizException {
        Type messageType = new TypeToken<Response>(){}.getType();
        Builder b = Uri.parse(getRestfulServiceHost()).buildUpon();
        b.path("/withdraw/updatecardnew");
        String url = b.build().toString();
        Gson g = new Gson();
        BankInfo info = new BankInfo();
        info.setBankTypeId(BankTypeId);
        info.setBankName(BankName);
        info.setBankCard(BankCard);
        info.setCardUser(CardUser);
        info.setBankBranch(BankBranch);
        info.setBankCity(BankCity);
        info.setBankProvince(BankProvince);
        info.setPhone(Phone);
        String html = BaseService.create(url, g.toJson(info));
        return (new Gson()).fromJson(html, messageType);
    }

    public Response<NewBankInfo> getBankInfo(int bankId, String bankName) throws IOException, JsonParseException, ServiceException, BizException {
        Uri.Builder b = Uri.parse(getRestfulServiceHost()).buildUpon();
        b.path("/withdraw/GetBankInfo");
        String url = b.build().toString();

        JSONObject json = new JSONObject();
        try {
            json.put("bankId", bankId);
            json.put("bankName", bankName);
        } catch (Exception e) {

        }

        String html = create(url, json.toString());
        Type messageType = new TypeToken<Response<NewBankInfo>>() {}.getType();
        return (new Gson()).fromJson(html, messageType);
    }

    public Response updBankInfo(String moneyPwd, NewBankInfo newBankInfo) throws IOException, JsonParseException, ServiceException, BizException {
        Uri.Builder b = Uri.parse(getRestfulServiceHost()).buildUpon();
        b.path("/withdraw/CheckMoneyPwdAndUpdBankInfo");
        String url = b.build().toString();

        UpdBankInfo updBankInfo = new UpdBankInfo();
        updBankInfo.setMoneyPwd(moneyPwd);
        updBankInfo.setModel(newBankInfo);

        String html = BaseService.create(url, new Gson().toJson(updBankInfo));
        Type messageType = new TypeToken<Response>(){}.getType();
        return (new Gson()).fromJson(html, messageType);
    }

    public Response delBankInfo(String moneyPwd, int bankId, String bankName) throws IOException, JsonParseException, ServiceException, BizException {
        Uri.Builder b = Uri.parse(getRestfulServiceHost()).buildUpon();
        b.path("/withdraw/CheckMoneyPwdAndDelBankInfo");
        String url = b.build().toString();

        JSONObject json = new JSONObject();
        try {
            json.put("moneyPwd", moneyPwd);
            json.put("bankId", bankId);
            json.put("bankName", bankName);
        } catch (Exception e) {

        }

        String html = create(url, json.toString());
        Type messageType = new TypeToken<Response>() {}.getType();
        return (new Gson()).fromJson(html, messageType);
    }
}
