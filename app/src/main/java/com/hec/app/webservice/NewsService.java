package com.hec.app.webservice;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.hec.app.entity.BizException;
import com.hec.app.entity.NewsInfo;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hec on 2015/10/28.
 */
public class NewsService extends BaseService {
    private String imageUrl1 = "http://img3.redocn.com/20111021/Redocn_2011101710453765.jpg";
    private String imageUrl2 = "http://image.hiapk.com/pic/2012/09/21/and_pic_jcbz_02_20120921_001.jpg";
    private String imageUrl3 = "http://img3.redocn.com/20111112/Redocn_2011110509285082.jpg";
    private String imageUrl4 = "http://b.hiphotos.baidu.com/image/pic/item/10dfa9ec8a1363277b98e73d938fa0ec08fac742.jpg";
    private String imageUrl5 = "http://t1.mmonly.cc/uploads/allimg/20150416/32014-0fTvGJ.jpg";
    private String imageUrl6 = "http://a.hiphotos.baidu.com/image/pic/item/21a4462309f7905288ab5a1909f3d7ca7bcbd564.jpg";

    public List<NewsInfo> getLatestNews() throws IOException, JsonParseException, ServiceException, BizException {
        String html = read("http://210.200.219.176:8073/Test/GetLatestNews");
        Gson g = new Gson();
        Type messageType = new TypeToken<List<NewsInfo>>() {
        }.getType();
        List<NewsInfo> news = g.fromJson(html, messageType);

        return news;
    }

    public List<NewsInfo> getMoreNews() throws IOException, JsonParseException, ServiceException, BizException {
        String html = read("http://210.200.219.176:8073/Test/GetMoreNews");
        Gson g = new Gson();
        Type messageType = new TypeToken<List<NewsInfo>>() {
        }.getType();
        List<NewsInfo> news = g.fromJson(html, messageType);

        return news;
    }

    public NewsInfo getNewsDetail(int id) throws IOException, JsonParseException, ServiceException, BizException {
        String html = read("http://210.200.219.176:8073/Test/GetNewsDetail/" + String.valueOf(id));
        Gson g = new Gson();
        return g.fromJson(html, NewsInfo.class);
    }

    public List<String> getBanners() {
        List<String> list = new ArrayList<>();
        list.add(imageUrl1);
        list.add(imageUrl2);
        list.add(imageUrl3);
        list.add(imageUrl4);
        list.add(imageUrl5);
        list.add(imageUrl6);
        return list;
    }
}
