package com.hec.app.webservice;

import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.hec.app.entity.ArticleCategoryInfo;
import com.hec.app.entity.ArticleCountInfo;
import com.hec.app.entity.ArticleListInfo;
import com.hec.app.entity.BizException;
import com.hec.app.entity.CollectOrDeleteArticleInfo;
import com.hec.app.entity.EvaluateArtilceInfo;
import com.hec.app.entity.PartlyLotteryInfo;
import com.hec.app.entity.PosrArticleInfo;
import com.hec.app.entity.Response;
import com.hec.app.entity.ShareInfo;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by wangxingjian on 2017/2/14.
 */

public class ArticleService extends BaseService {

    public List<ArticleCategoryInfo> getArticleCategory() throws IOException, JsonParseException, ServiceException, BizException {
        Uri.Builder b = Uri.parse(getRestfulServiceHost()).buildUpon();
        b.path("/Article/CategoryList/");
        String url = b.build().toString();
        String html = read(url);
        Type messageType = new TypeToken<Response<List<ArticleCategoryInfo>>>(){}.getType();
        return getResult(html,messageType);
    }

    public List<ArticleListInfo> getArticleList(boolean isEssence, boolean isOriginal, int sortBy, int categtoyid, int pageNumber)
            throws IOException, JsonParseException, ServiceException, BizException {
        Uri.Builder b = Uri.parse(getRestfulServiceHost()).buildUpon();
        b.path("/Article/List/");
        if(!isEssence && !isOriginal){
            b.appendQueryParameter("isEssence",null);
            b.appendQueryParameter("isOriginal",null);
        }else{
            b.appendQueryParameter("isEssence",String.valueOf(isEssence));
            b.appendQueryParameter("isOriginal",String.valueOf(isOriginal));
        }
        b.appendQueryParameter("isEssence",String.valueOf(isEssence));
        b.appendQueryParameter("isOriginal",String.valueOf(isOriginal));
        b.appendQueryParameter("sortBy",String.valueOf(sortBy));
        b.appendQueryParameter("categoryId",String.valueOf(categtoyid));
        b.appendQueryParameter("pageNumber",String.valueOf(pageNumber));
        String url = b.build().toString();
        String html = read(url);
        Log.i("art",html);
        Type messageType = new TypeToken<Response<List<ArticleListInfo>>>(){}.getType();
        return getResult(html,messageType);
    }

    public List<ArticleListInfo> getPopularArticleList(boolean isEssence, boolean isOriginal, int sortBy, int categtoyid, int pageNumber)
            throws IOException, JsonParseException, ServiceException, BizException {
        Uri.Builder b = Uri.parse(getRestfulServiceHost()).buildUpon();
        b.path("/Article/PopularList/");
        if(!isEssence && !isOriginal){
            b.appendQueryParameter("isEssence",null);
            b.appendQueryParameter("isOriginal",null);
        }else{
            b.appendQueryParameter("isEssence",String.valueOf(isEssence));
            b.appendQueryParameter("isOriginal",String.valueOf(isOriginal));
        }
        b.appendQueryParameter("sortBy",String.valueOf(sortBy));
        b.appendQueryParameter("categoryId",String.valueOf(categtoyid));
        b.appendQueryParameter("pageNumber",String.valueOf(pageNumber));
        String url = b.build().toString();
        String html = read(url);
        Log.i("art",html);
        Type messageType = new TypeToken<Response<List<ArticleListInfo>>>(){}.getType();
        return getResult(html,messageType);
    }

    public List<ArticleListInfo> getMyArticleList(boolean isEssence, boolean isOriginal, int sortBy, int categtoyid, int pageNumber)
            throws IOException, JsonParseException, ServiceException, BizException {
        Uri.Builder b = Uri.parse(getRestfulServiceHost()).buildUpon();
        b.path("/Article/ListByUserOwned/");
        if(!isEssence && !isOriginal){
            b.appendQueryParameter("isEssence",null);
            b.appendQueryParameter("isOriginal",null);
        }else{
            b.appendQueryParameter("isEssence",String.valueOf(isEssence));
            b.appendQueryParameter("isOriginal",String.valueOf(isOriginal));
        }
        b.appendQueryParameter("sortBy",String.valueOf(sortBy));
        b.appendQueryParameter("categoryId",String.valueOf(categtoyid));
        b.appendQueryParameter("pageNumber",String.valueOf(pageNumber));
        String url = b.build().toString();
        String html = read(url);
        Log.i("art",html);
        Type messageType = new TypeToken<Response<List<ArticleListInfo>>>(){}.getType();
        return getResult(html,messageType);
    }

    public List<ArticleListInfo> getCollectedArticleList(boolean isEssence, boolean isOriginal, int sortBy, int categtoyid, int pageNumber)
            throws IOException, JsonParseException, ServiceException, BizException {
        Uri.Builder b = Uri.parse(getRestfulServiceHost()).buildUpon();
        b.path("/Article/ListByUserCollected/");
        if(!isEssence && !isOriginal){
            b.appendQueryParameter("isEssence",null);
            b.appendQueryParameter("isOriginal",null);
        }else{
            b.appendQueryParameter("isEssence",String.valueOf(isEssence));
            b.appendQueryParameter("isOriginal",String.valueOf(isOriginal));
        }
        b.appendQueryParameter("sortBy",String.valueOf(sortBy));
        b.appendQueryParameter("categoryId",String.valueOf(categtoyid));
        b.appendQueryParameter("pageNumber",String.valueOf(pageNumber));
        String url = b.build().toString();
        String html = read(url);
        Log.i("art",html);
        Type messageType = new TypeToken<Response<List<ArticleListInfo>>>(){}.getType();
        return getResult(html,messageType);
    }

    public EvaluateArtilceInfo evaluateArticle(int articleId,int type) throws IOException, JsonParseException, ServiceException, BizException {
        Uri.Builder b = Uri.parse(getRestfulServiceHost()).buildUpon();
        b.path("/Article/Evaluate/");
        Gson gson = new Gson();
        String url = b.build().toString();
        EvaluateArtilceInfo e = new EvaluateArtilceInfo();
        e.setArticleId(articleId);
        e.setType(type);
        String html = create(url,gson.toJson(e));
        Log.i("art","eva "+html);
        Type messageType = new TypeToken<EvaluateArtilceInfo>(){}.getType();
        return gson.fromJson(html,messageType);
    }

    public PosrArticleInfo postArticle(String title,String content) throws IOException, JsonParseException, ServiceException, BizException {
        Uri.Builder b = Uri.parse(getRestfulServiceHost()).buildUpon();
        b.path("/Article/Create/");
        Gson gson = new Gson();
        PosrArticleInfo p = new PosrArticleInfo();
        p.setTitle(title);
        p.setBody(content);
        String url = b.build().toString();
        String html = create(url,gson.toJson(p));
        Log.i("art","eva "+html);
        Type messageType = new TypeToken<PosrArticleInfo>(){}.getType();
        return gson.fromJson(html,messageType);
    }

    public CollectOrDeleteArticleInfo collectArticle(int id) throws IOException, JsonParseException, ServiceException, BizException {
        Uri.Builder b = Uri.parse(getRestfulServiceHost()).buildUpon();
        b.path("/Article/Collect/");
        Gson gson = new Gson();
        String url = b.build().toString();
        CollectOrDeleteArticleInfo c = new CollectOrDeleteArticleInfo();
        c.setId(id);
        String html = create(url,gson.toJson(c));
        Log.i("art","eva "+html);
        Type messageType = new TypeToken<CollectOrDeleteArticleInfo>(){}.getType();
        return gson.fromJson(html,messageType);
    }

    public CollectOrDeleteArticleInfo deleteArticle(int id) throws IOException, JsonParseException, ServiceException, BizException {
        Uri.Builder b = Uri.parse(getRestfulServiceHost()).buildUpon();
        b.path("/Article/Delete/");
        Gson gson = new Gson();
        String url = b.build().toString();
        CollectOrDeleteArticleInfo c = new CollectOrDeleteArticleInfo();
        c.setId(id);
        String html = create(url,gson.toJson(c));
        Log.i("art","eva "+html);
        Type messageType = new TypeToken<CollectOrDeleteArticleInfo>(){}.getType();
        return gson.fromJson(html,messageType);
    }

    public Response readArticle(int id)  throws IOException, JsonParseException, ServiceException, BizException {
        Uri.Builder b = Uri.parse(getRestfulServiceHost()).buildUpon();
        b.path("/Article/ReadArticle/");
        Gson gson = new Gson();
        String url = b.build().toString();
        CollectOrDeleteArticleInfo c = new CollectOrDeleteArticleInfo();
        c.setId(id);
        String html = create(url,gson.toJson(c));
        Log.i("art","read "+html);
        Type messageType = new TypeToken<Response>(){}.getType();
        return gson.fromJson(html,messageType);
    }
    public ArticleCountInfo getArticleCount() throws IOException, JsonParseException, ServiceException, BizException {
        Uri.Builder b = Uri.parse(getRestfulServiceHost()).buildUpon();
        b.path("/Article/TodayCount/");
        Gson gson = new Gson();
        String url = b.build().toString();
        String html = read(url);
        Log.i("wxj","article " + html);
        Type messageType = new TypeToken<Response<ArticleCountInfo>>(){}.getType();
        Log.i("wxj","article lal");
        return getResult(html,messageType);
    }

    public ShareInfo shareArticle(int id) throws IOException, JsonParseException, ServiceException, BizException {
        Uri.Builder b = Uri.parse(RESTFUL_SERVICE_HOST).buildUpon();
        b.path("/article/sharing/"+String.valueOf(id)+"/");
        Gson gson = new Gson();
        String url = b.build().toString();
        String html = read(url);
        Log.i("art","read "+html);
        Type messageType = new TypeToken<Response<ShareInfo>>(){}.getType();
        return getResult(html,messageType);
    }
}
