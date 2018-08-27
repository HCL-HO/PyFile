package com.hec.app.webservice;

import android.net.Uri;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.hec.app.entity.BizException;
import com.hec.app.entity.CustomServiceChatInfo;
import com.hec.app.entity.CustomerServiceImageInfo;
import com.hec.app.entity.CustomerServiceInfo;
import com.hec.app.entity.CustomerServiceSendInfo;
import com.hec.app.entity.Response;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class CustomerService extends BaseService {

    public List<CustomServiceChatInfo> getChatInfo(String date,  boolean isFirstTimeOpen, int currentMessageID) throws IOException, JsonParseException, ServiceException, BizException {
        Gson g = new Gson();
        Uri.Builder b = Uri.parse(getRestfulVIPServiceHost()).buildUpon();
        b.path("/" + "Home" + "/GetVIPChat");
        CustomerServiceInfo info = new CustomerServiceInfo();
        info.setDate(date);
        info.setCurrentMessageID(currentMessageID);
        info.setFirstTimeOpen(isFirstTimeOpen);
        String url = b.build().toString();
        String html = create(url, g.toJson(info));
        return getResult(html, new TypeToken<Response<List<CustomServiceChatInfo>>>() {}.getType());
    }

    public Response sendChatMessage(String text)  throws IOException, JsonParseException, ServiceException, BizException {
        Uri.Builder b = Uri.parse(getRestfulVIPServiceHost()).buildUpon();
        Gson g = new Gson();
        b.path("/" + "Home" + "/SendVIPMessage");
        CustomerServiceSendInfo info = new CustomerServiceSendInfo();
        info.setMessage(text);
        String url = b.build().toString();
        String html = create(url, g.toJson(info));
        Type messageType = new TypeToken<Response>() {}.getType();
        return g.fromJson(html, messageType);
    }

    public Response sendChatPicture(String fileName, String imgString)  throws IOException, JsonParseException, ServiceException, BizException {
        Uri.Builder b = Uri.parse(getRestfulVIPServiceHost()).buildUpon();
        Gson g = new Gson();
        b.path("/" + "Home" + "/SendVIPPicture");
        CustomerServiceImageInfo info = new CustomerServiceImageInfo();
        info.setFileName(fileName);
        info.setFileStr(imgString);
        String url = b.build().toString();
        String html = create(url, g.toJson(info));
        Type messageType = new TypeToken<Response>() {}.getType();
        return g.fromJson(html, messageType);
    }

}
