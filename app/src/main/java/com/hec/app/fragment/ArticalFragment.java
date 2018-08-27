package com.hec.app.fragment;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextPaint;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.JsonParseException;
import com.hec.app.R;
import com.hec.app.activity.ArticalActivity;
import com.hec.app.activity.base.BaseApp;
import com.hec.app.entity.ArticleListInfo;
import com.hec.app.entity.BizException;
import com.hec.app.entity.CollectOrDeleteArticleInfo;
import com.hec.app.entity.EvaluateArtilceInfo;
import com.hec.app.entity.Response;
import com.hec.app.entity.ShareInfo;
import com.hec.app.framework.widget.LoadingLayout;
import com.hec.app.framework.widget.MyToast;
import com.hec.app.util.DateTransmit;
import com.hec.app.util.DialogUtil;
import com.hec.app.util.MyAsyncTask;
import com.hec.app.webservice.ArticleService;
import com.hec.app.webservice.HomeService;
import com.hec.app.webservice.ServiceException;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import cn.sharesdk.onekeyshare.OnekeyShare;


/**
 * A simple {@link Fragment} subclass.
 */
public class ArticalFragment extends ListFragment implements ArticalActivity.TitleItemSelectedListener{

    public static final String ARGUMENT = "argument";
    private TabLayout tabLayout;
    private boolean mIsError;
    private View footerView;
    private List<Map<String,String>> records;
    private ArticalFragment.SimpleRecordListAdapter mRecordAdapter;
    private List<ArticleListInfo> articleList = new ArrayList<>();
    private TextView loading;
    private ProgressBar progressBar;
    private int TITLE_CATE;
    private boolean isEssence = false;
    private boolean isOriginal = false;
    private int pages = 1;
    private int lastItem;
    private boolean hasMore = true;
    private SwipeRefreshLayout swipeRefreshLayout;
    private View v;
    private ProgressDialog progressDialog;
    private Map<Integer,Boolean> isReadMap = new HashMap<>();

    public ArticalFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_artical, container, false);
        footerView = inflater.inflate(R.layout.footer_view_item, null);
        loading = (TextView) footerView.findViewById(R.id.footer_item);
        progressBar = (ProgressBar) footerView.findViewById(R.id.progressBar_loading);
        loading.setText("上拉加载!");
        footerView.setVisibility(View.INVISIBLE);
        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.article_swipe);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                articleList.clear();
                switch (TITLE_CATE){
                    case 0:
                        Log.i("art","first swipe");
                        getArticleList(isEssence,isOriginal,1,1,1);
                        break;
                    case 1:
                        getArticleList(isEssence,isOriginal,1,2,1);
                        break;
                    case 2:
                        getMyArticleList(isEssence,isOriginal,1,1,1);
                        break;
                    case 3:
                        getCollectedArticleList(isEssence,isOriginal,1,1,1);
                        break;
                    case 4:
                        getPopularArticleList(isEssence,isOriginal,1,1,1);
                        break;
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((ArticalActivity)getContext()).setOnTitleItemSelectedListener(this);
        footerView.setVisibility(View.VISIBLE);
        setTablayout();
        setonTabClickListener();
        records = new ArrayList<Map<String, String>>();
        mRecordAdapter = new ArticalFragment.SimpleRecordListAdapter(getActivity(), records);
        getListView().setDivider(new ColorDrawable(getResources().getColor(R.color.colorPrimaryDark)));
        getListView().setDividerHeight(5);
        getListView().addFooterView(footerView);
        getListView().setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if(scrollState == SCROLL_STATE_IDLE && lastItem == mRecordAdapter.getCount() && hasMore){
                pages++;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                            switch (TITLE_CATE){
                                case 0:
                                    Log.i("art","first onscroll");
                                    getArticleList(isEssence,isOriginal,1,1,pages);
                                    break;
                                case 1:
                                    getArticleList(isEssence,isOriginal,1,2,pages);
                                    break;
                                case 2:
                                    getMyArticleList(isEssence,isOriginal,1,1,pages);
                                    break;
                                case 3:
                                    getCollectedArticleList(isEssence,isOriginal,1,1,pages);
                                    break;
                                case 4:
                                    getPopularArticleList(isEssence,isOriginal,1,1,pages);
                                    break;
                            }
                        mRecordAdapter.notifyDataSetChanged();
                    }
                }, 1000);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                lastItem = firstVisibleItem + visibleItemCount - 1;
            }
        });
        setListAdapter(mRecordAdapter);
        //getArticleList(false,false,1,1,1);
    }

    private void setTablayout(){
        tabLayout = (TabLayout) getView().findViewById(R.id.record_list_tabs);
            tabLayout.addTab(tabLayout.newTab().setText("全部"));
            tabLayout.addTab(tabLayout.newTab().setText("精华"));
            tabLayout.addTab(tabLayout.newTab().setText("原创"));
    }

    private void setonTabClickListener(){
        if(tabLayout != null){
            tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    if(tab.getText().equals("全部")){
                        isEssence = false;
                        isOriginal = false;
                        hasMore = true;
                        switch (TITLE_CATE){
                            case 0:
                                Log.i("art","first tab");
                                articleList.clear();
                                getArticleList(isEssence,isOriginal,1,1,1);
                                break;
                            case 1:
                                articleList.clear();
                                getArticleList(isEssence,isOriginal,1,2,1);
                                break;
                            case 2:
                                articleList.clear();
                                getMyArticleList(isEssence,isOriginal,1,1,1);
                                break;
                            case 3:
                                articleList.clear();
                                getCollectedArticleList(isEssence,isOriginal,1,1,1);
                                break;
                            case 4:
                                articleList.clear();
                                getPopularArticleList(isEssence,isOriginal,1,1,1);
                                break;
                        }
                    }else if(tab.getText().equals("精华")){
                        isEssence = true;
                        isOriginal = false;
                        hasMore = true;
                        switch (TITLE_CATE){
                            case 0:
                                articleList.clear();
                                getArticleList(isEssence,isOriginal,1,1,1);
                                break;
                            case 1:
                                articleList.clear();
                                getArticleList(isEssence,isOriginal,1,2,1);
                                break;
                            case 2:
                                articleList.clear();
                                getMyArticleList(isEssence,isOriginal,1,1,1);
                                break;
                            case 3:
                                articleList.clear();
                                getCollectedArticleList(isEssence,isOriginal,1,1,1);
                                break;
                            case 4:
                                articleList.clear();
                                getPopularArticleList(isEssence,isOriginal,1,1,1);
                                break;
                        }
                    }else if(tab.getText().equals("原创")){
                        isEssence = false;
                        isOriginal = true;
                        hasMore = true;
                        switch (TITLE_CATE){
                            case 0:
                                articleList.clear();
                                getArticleList(isEssence,isOriginal,1,1,1);
                                break;
                            case 1:
                                articleList.clear();
                                getArticleList(isEssence,isOriginal,1,2,1);
                                break;
                            case 2:
                                articleList.clear();
                                getMyArticleList(isEssence,isOriginal,1,1,1);
                                break;
                            case 3:
                                articleList.clear();
                                getCollectedArticleList(isEssence,isOriginal,1,1,1);
                                break;
                            case 4:
                                articleList.clear();
                                getPopularArticleList(isEssence,isOriginal,1,1,1);
                                break;
                        }
                    }
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });
        }
    }

    /**
     * For spinner items!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
     * @param position
     */
    @Override
    public void click(int position) {
        switch (position){
            case 0:
                Log.i("art","first spinner");
                articleList.clear();
                TITLE_CATE = 0;
                hasMore = true;
                getArticleList(isEssence,isOriginal,1,1,1);
                break;
            case 1:
                articleList.clear();
                TITLE_CATE = 1;
                hasMore = true;
                getArticleList(isEssence,isOriginal,1,2,1);
                break;
            case 2:
                articleList.clear();
                TITLE_CATE = 2;
                hasMore = true;
                getMyArticleList(isEssence,isOriginal,1,1,1);
                break;
            case 3:
                articleList.clear();
                TITLE_CATE = 3;
                hasMore = true;
                getCollectedArticleList(isEssence,isOriginal,1,1,1);
                break;
            case 4:
                articleList.clear();
                TITLE_CATE = 4;
                hasMore = true;
                getPopularArticleList(isEssence,isOriginal,1,1,1);
                break;
        }
    }

    private void getArticleList(final boolean isEssence, final boolean isOriginal, final int sortBy, final int categtoyid, final int pageNumber){
        mIsError = false;
        showProgressDialog("正在加载!");
        Log.i("art","first get article");
        MyAsyncTask<List<ArticleListInfo>> task = new MyAsyncTask<List<ArticleListInfo>>(getContext()) {
            @Override
            public List<ArticleListInfo> callService() throws IOException, JsonParseException, BizException, ServiceException {
                return new ArticleService().getArticleList(isEssence,isOriginal,sortBy,categtoyid,pageNumber);
            }

            @Override
            public void onLoaded(List<ArticleListInfo> results) throws Exception {
                closeProgressDialog();
                if(!mIsError){
                    Log.i("art","first in load");
                    if(results != null){
                        if(results.size()<20){
                            hasMore = false;
                            progressBar.setVisibility(View.GONE);
                            loading.setText("没有更多信息");
                        }else{
                            progressBar.setVisibility(View.VISIBLE);
                            loading.setText("上拉加载!");
                        }
                        articleList.addAll(results);
                    }else{
                        hasMore = false;
                        progressBar.setVisibility(View.GONE);
                        loading.setText("没有更多信息");
                    }
                    getData(articleList);
                }
                else {
                    BaseApp.changeUrl(getActivity(), new BaseApp.OnChangeUrlListener() {
                        @Override
                        public void changeSuccess() {
                            getArticleList(isEssence, isOriginal, sortBy, categtoyid, pageNumber);
                        }

                        @Override
                        public void changeFail() {}
                    });
                }
            }
        };
        task.executeTask();
        task.setOnError(new MyAsyncTask.OnError() {
            @Override
            public void handleError(Exception paramException) {
                mIsError = true;
            }
        });
    }

    private void getPopularArticleList(final boolean isEssence, final boolean isOriginal, final int sortBy, final int categtoyid, final int pageNumber){
        mIsError = false;
        showProgressDialog("正在加载!");
        MyAsyncTask<List<ArticleListInfo>> task = new MyAsyncTask<List<ArticleListInfo>>(getContext()) {
            @Override
            public List<ArticleListInfo> callService() throws IOException, JsonParseException, BizException, ServiceException {
                return new ArticleService().getPopularArticleList(isEssence,isOriginal,sortBy,categtoyid,pageNumber);
            }

            @Override
            public void onLoaded(List<ArticleListInfo> results) throws Exception {
                closeProgressDialog();
                if(!mIsError){
                    if(results != null){
                        if(results.size()<20){
                            hasMore = false;
                            progressBar.setVisibility(View.GONE);
                            loading.setText("没有更多信息");
                        }else{
                            progressBar.setVisibility(View.VISIBLE);
                            loading.setText("上拉加载!");
                        }
                        articleList.addAll(results);
                    }else{
                        hasMore = false;
                        progressBar.setVisibility(View.GONE);
                        loading.setText("没有更多信息");
                    }
                    getData(articleList);
                }
                else {
                    BaseApp.changeUrl(getActivity(), new BaseApp.OnChangeUrlListener() {
                        @Override
                        public void changeSuccess() {
                            getPopularArticleList(isEssence, isOriginal, sortBy, categtoyid, pageNumber);
                        }

                        @Override
                        public void changeFail() {}
                    });
                }
            }
        };
        task.executeTask();
        task.setOnError(new MyAsyncTask.OnError() {
            @Override
            public void handleError(Exception paramException) {
                mIsError = true;
            }
        });
    }

    private void getMyArticleList(final boolean isEssence, final boolean isOriginal, final int sortBy, final int categtoyid, final int pageNumber){
        mIsError = false;
        showProgressDialog("正在加载!");
        MyAsyncTask<List<ArticleListInfo>> task = new MyAsyncTask<List<ArticleListInfo>>(getContext()) {
            @Override
            public List<ArticleListInfo> callService() throws IOException, JsonParseException, BizException, ServiceException {
                return new ArticleService().getMyArticleList(isEssence,isOriginal,sortBy,categtoyid,pageNumber);
            }

            @Override
            public void onLoaded(List<ArticleListInfo> results) throws Exception {
                closeProgressDialog();
                if(!mIsError){
                    if(results != null){
                        if(results.size()<20){
                            hasMore = false;
                            progressBar.setVisibility(View.GONE);
                            loading.setText("没有更多信息");
                        }else{
                            progressBar.setVisibility(View.VISIBLE);
                            loading.setText("上拉加载!");
                        }
                        articleList.addAll(results);
                    }else{
                        hasMore = false;
                        progressBar.setVisibility(View.GONE);
                        loading.setText("没有更多信息");
                    }
                    getData(articleList);
                }
                else {
                    BaseApp.changeUrl(getActivity(), new BaseApp.OnChangeUrlListener() {
                        @Override
                        public void changeSuccess() {
                            getMyArticleList(isEssence, isOriginal, sortBy, categtoyid, pageNumber);
                        }

                        @Override
                        public void changeFail() {}
                    });
                }
            }
        };
        task.executeTask();
        task.setOnError(new MyAsyncTask.OnError() {
            @Override
            public void handleError(Exception paramException) {
                mIsError = true;
            }
        });
    }

    private void getCollectedArticleList(final boolean isEssence, final boolean isOriginal, final int sortBy, final int categtoyid, final int pageNumber){
        mIsError = false;
        showProgressDialog("正在加载!");
        MyAsyncTask<List<ArticleListInfo>> task = new MyAsyncTask<List<ArticleListInfo>>(getContext()) {
            @Override
            public List<ArticleListInfo> callService() throws IOException, JsonParseException, BizException, ServiceException {
                return new ArticleService().getCollectedArticleList(isEssence,isOriginal,sortBy,categtoyid,pageNumber);
            }

            @Override
            public void onLoaded(List<ArticleListInfo> results) throws Exception {
                closeProgressDialog();
                if(!mIsError){
                    if(results != null){
                        if(results.size()<20){
                            hasMore = false;
                            progressBar.setVisibility(View.GONE);
                            loading.setText("没有更多信息");
                        }else{
                            progressBar.setVisibility(View.VISIBLE);
                            loading.setText("上拉加载!");
                        }
                        articleList.addAll(results);
                    }else{
                        hasMore = false;
                        progressBar.setVisibility(View.GONE);
                        loading.setText("没有更多信息");
                    }
                    getData(articleList);
                }
                else {
                    BaseApp.changeUrl(getActivity(), new BaseApp.OnChangeUrlListener() {
                        @Override
                        public void changeSuccess() {
                            getCollectedArticleList(isEssence, isOriginal, sortBy, categtoyid, pageNumber);
                        }

                        @Override
                        public void changeFail() {}
                    });
                }
            }
        };
        task.executeTask();
        task.setOnError(new MyAsyncTask.OnError() {
            @Override
            public void handleError(Exception paramException) {
                mIsError = true;
            }
        });
    }

    private void getData(List<ArticleListInfo> list){
        records.clear();
        if(list != null){
            for(ArticleListInfo info : list){
                String modifiedtime = DateTransmit.dateTransmits(info.getModifiedTime());
                String publishtime = DateTransmit.dateTransmits(info.getPublishedTime());
                Map<String, String> mMap = new HashMap();
                mMap.put("title",info.getTitle());
                mMap.put("author",info.getUserName());
                mMap.put("publishtime",publishtime);
                mMap.put("commentno",String.valueOf(info.getThumbsDown()+info.getThumbsUp()));
                mMap.put("zan",String.valueOf(info.getThumbsUp()));
                mMap.put("cai",String.valueOf(info.getThumbsDown()));
                mMap.put("essence",String.valueOf(info.isEssence()));
                mMap.put("original",String.valueOf(info.isOriginal()));
                mMap.put("id",String.valueOf(info.getId()));
                mMap.put("body",info.getBody());
                mMap.put("mythumbstype",String.valueOf(info.getMyThumbsType()));
                mMap.put("collect",String.valueOf(info.isMyCollectedType()));
                mMap.put("view",String.valueOf(info.getViews()));
                mMap.put("status",String.valueOf(info.getStatus()));
                mMap.put("isread",String.valueOf(info.isRead()));
                Log.i("art",info.getMyThumbsType()+" th");
                records.add(mMap);
                Log.i("art","records " + records.size());
            }
        }
        mRecordAdapter.notifyDataSetChanged();
    }

    private class SimpleRecordListAdapter extends ArrayAdapter{

        List<Boolean> isClick = new ArrayList<>();
        private final Context context;
        List<Map<String, String>> mlist;

        public SimpleRecordListAdapter(Context context, List<Map<String, String>> mlist) {
            super(context, R.layout.list_item_article);
            this.context = context;
            this.mlist = mlist;
        }

        @Override
        public int getCount() {
            return mlist != null ? mlist.size() : 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            isClick.add(false);
            View rowView = inflater.inflate(R.layout.list_item_article,parent,false);
            LinearLayout ll_article_title = (LinearLayout) rowView.findViewById(R.id.ll_article_title);
            TextView tv_title = (TextView) rowView.findViewById(R.id.tv_article_title);
            ImageView img_flag = (ImageView) rowView.findViewById(R.id.img_article_title_flag);
            TextView tv_author = (TextView) rowView.findViewById(R.id.tv_author);
            TextView tv_publishtime = (TextView) rowView.findViewById(R.id.tv_post_time);
            TextView tv_comment_people = (TextView) rowView.findViewById(R.id.tv_comment_people);
            final LinearLayout ll_article_content = (LinearLayout) rowView.findViewById(R.id.ll_article_content);
            TextView tv_article_content = (TextView) rowView.findViewById(R.id.tv_article_content);
            final LinearLayout ll_comment_total = (LinearLayout) rowView.findViewById(R.id.ll_commet_total_layout);
            final TextView tv_zan = (TextView) rowView.findViewById(R.id.tv_zan);
            final TextView tv_cai = (TextView) rowView.findViewById(R.id.tv_cai);
            final LinearLayout ll_dianzan = (LinearLayout) rowView.findViewById(R.id.ll_dianzan);
            final LinearLayout ll_diancai = (LinearLayout) rowView.findViewById(R.id.ll_diancai);
            final TextView tv_dianzan = (TextView) rowView.findViewById(R.id.tv_dianzan);
            final TextView tv_diancai = (TextView) rowView.findViewById(R.id.tv_diancai);
            final ImageView img_dianzan = (ImageView) rowView.findViewById(R.id.img_dianzan);
            final ImageView img_diancai = (ImageView) rowView.findViewById(R.id.img_diancai);
            final TextView tv_collect = (TextView) rowView.findViewById(R.id.tv_collect);
            final TextView tv_share = (TextView) rowView.findViewById(R.id.tv_share);
            TextView tv_delete = (TextView) rowView.findViewById(R.id.tv_delete);
            TextView tv_status = (TextView) rowView.findViewById(R.id.tv_status);

            if(TITLE_CATE == 2){
                tv_delete.setVisibility(View.VISIBLE);
            }
            if(TITLE_CATE == 3){
                tv_collect.setVisibility(View.GONE);
            }

            final Map<String, String> map = mlist.get(position);
            if("1".equals(map.get("status"))){

            }else if("0".equals(map.get("status"))){
                tv_status.setVisibility(View.VISIBLE);
                tv_status.setText("审核中");
            }else if("2".equals(map.get("status"))){
                tv_status.setVisibility(View.VISIBLE);
                tv_status.setText("审核未通过");
            }else if("3".equals(map.get("status"))){
                tv_status.setVisibility(View.VISIBLE);
                tv_status.setText("搁置");
            }
            if(Boolean.parseBoolean(map.get("collect"))){
                tv_collect.setText("已收藏");
                tv_collect.setTextColor(getResources().getColor(R.color.article_green));
            }
            if(!Boolean.parseBoolean(map.get("essence"))
                    && !Boolean.parseBoolean(map.get("original"))){
                img_flag.setVisibility(View.INVISIBLE);
            }else if (Boolean.parseBoolean(map.get("essence"))){
                img_flag.setImageResource(R.mipmap.orange_icon);
            }else if(Boolean.parseBoolean(map.get("original"))){
                img_flag.setImageResource(R.mipmap.yellow_icon);
            }
            tv_collect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tv_collect.setText("已收藏");
                    tv_collect.setTextColor(getResources().getColor(R.color.article_green));
                    collectOrDelete(1,Integer.parseInt(map.get("id")));
                }
            });

            tv_share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //MyToast.show(getActivity(),"即将上线！");
                    shareArticle(Integer.parseInt(map.get("id")));
                }
            });

            tv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    collectOrDelete(2,Integer.parseInt(map.get("id")));
                }
            });
            if("0".equals(map.get("mythumbstype"))){

            }else if("1".equals(map.get("mythumbstype"))){
                tv_dianzan.setTextColor(getResources().getColor(R.color.article_green));
                img_dianzan.setImageResource(R.mipmap.like_icon_pressed);
            }else if("2".equals(map.get("mythumbstype"))){
                tv_diancai.setTextColor(getResources().getColor(R.color.article_green));
                img_diancai.setImageResource(R.mipmap.dislike_icon_pressed);
            }
            TextPaint tp = tv_title.getPaint();
            tp.setFakeBoldText(true);
            tv_title.setText(map.get("title"));
            tv_author.setText(map.get("author"));
            tv_publishtime.setText(map.get("publishtime"));
            tv_comment_people.setText("[已有 " + map.get("view") + " 人阅读]");
            if(!isClick.get(position)){
                ll_article_content.setVisibility(View.GONE);
                ll_comment_total.setVisibility(View.GONE);
            }else{
                ll_article_content.setVisibility(View.VISIBLE);
                ll_comment_total.setVisibility(View.VISIBLE);
            }
            tv_zan.setText("点赞 " + map.get("zan"));
            tv_cai.setText("点踩 " + map.get("cai"));
            tv_article_content.setText(map.get("body"));

            ll_article_title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!isClick.get(position)){
                        ll_article_content.setVisibility(View.VISIBLE);
                        ll_comment_total.setVisibility(View.VISIBLE);
                        isClick.set(position,true);
                    }else{
                        ll_article_content.setVisibility(View.GONE);
                        ll_comment_total.setVisibility(View.GONE);
                        isClick.set(position,false);
                    }
                    if(Boolean.parseBoolean(map.get("isread"))
                            ||isReadMap.containsKey(Integer.parseInt(map.get("id")))){

                    }else{
                        articleList.get(position).setRead(true);
                        articleList.get(position).setViews(articleList.get(position).getViews()+1);
                        getData(articleList);
                        isReadMap.put(Integer.parseInt(map.get("id")),true);
                        readArticle(Integer.parseInt(map.get("id")));
                    }
                }
            });
            ll_dianzan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("art","dianzan");
                    tv_dianzan.setTextColor(getResources().getColor(R.color.article_green));
                    img_dianzan.setImageResource(R.mipmap.like_icon_pressed);
                    tv_diancai.setTextColor(getResources().getColor(R.color.article_grew));
                    img_diancai.setImageResource(R.mipmap.dislike_icon_idle);
                    evaluateArticle(Integer.parseInt(map.get("id")),1);
                    if("0".equals(map.get("mythumbstype"))){
                        articleList.get(position).setThumbsUp(articleList.get(position).getThumbsUp()+1);
                        getData(articleList);
                        //records.get(position).put("zan", String.valueOf(Integer.parseInt(map.get("zan"))+1));
                        tv_zan.setText("点赞 "+String.valueOf(Integer.parseInt(map.get("zan"))));
                    }else if("1".equals(map.get("mythumbstype"))){

                    }else if("2".equals(map.get("mythumbstype"))){
                        articleList.get(position).setThumbsUp(articleList.get(position).getThumbsUp()+1);
                        //records.get(position).put("zan", String.valueOf(Integer.parseInt(map.get("zan"))+1));
                        articleList.get(position).setThumbsDown(articleList.get(position).getThumbsDown()-1);
                        //records.get(position).put("cai", String.valueOf(Integer.parseInt(map.get("cai"))-1));
                        getData(articleList);
                        tv_zan.setText("点赞 "+String.valueOf(Integer.parseInt(map.get("zan"))));
                        tv_cai.setText("点踩 "+String.valueOf(Integer.parseInt(map.get("cai"))));
                    }
                    articleList.get(position).setMyThumbsType(1);
                    getData(articleList);
                    //records.get(position).put("mythumbstype","1");
                }
            });
            ll_diancai.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tv_dianzan.setTextColor(getResources().getColor(R.color.article_grew));
                    img_dianzan.setImageResource(R.mipmap.like_icon_idle);
                    tv_diancai.setTextColor(getResources().getColor(R.color.article_green));
                    img_diancai.setImageResource(R.mipmap.dislike_icon_pressed);
                    evaluateArticle(Integer.parseInt(map.get("id")),2);
                    if("0".equals(map.get("mythumbstype"))){
                        articleList.get(position).setThumbsDown(articleList.get(position).getThumbsDown()+1);
                        getData(articleList);
                        //records.get(position).put("cai", String.valueOf(Integer.parseInt(map.get("cai"))+1));
                        tv_cai.setText("点踩 "+String.valueOf(Integer.parseInt(map.get("cai"))));
                    }else if("1".equals(map.get("mythumbstype"))){
                        //records.get(position).put("cai", String.valueOf(Integer.parseInt(map.get("cai"))+1));
                        //records.get(position).put("zan", String.valueOf(Integer.parseInt(map.get("zan"))-1));
                        articleList.get(position).setThumbsUp(articleList.get(position).getThumbsUp()-1);
                        articleList.get(position).setThumbsDown(articleList.get(position).getThumbsDown()+1);
                        getData(articleList);
                        tv_cai.setText("点踩 "+String.valueOf(Integer.parseInt(map.get("cai"))));
                        tv_zan.setText("点赞 "+String.valueOf(Integer.parseInt(map.get("zan"))));
                    }else if("2".equals(map.get("mythumbstype"))){

                    }
                    articleList.get(position).setMyThumbsType(2);
                    getData(articleList);
                    //records.get(position).put("mythumbstype","2");
                }
            });
            return rowView;
        }
    }

    private void evaluateArticle(final int articleId, final int thtype){
        mIsError = false;
        MyAsyncTask<EvaluateArtilceInfo> task = new MyAsyncTask<EvaluateArtilceInfo>(getContext()) {
            @Override
            public EvaluateArtilceInfo callService() throws IOException, JsonParseException, BizException, ServiceException {
                return new ArticleService().evaluateArticle(articleId,thtype);
            }

            @Override
            public void onLoaded(EvaluateArtilceInfo result) throws Exception {
                if(!mIsError){
                    if(result.isSuccess()){
                        MyToast.show(getContext(),"评论成功");
                    }else{
                        MyToast.show(getContext(),result.getMessage());
                    }
                }else{
                    MyToast.show(getContext(),getErrorMessage());
                }
            }
        };
        task.executeTask();
        task.setOnError(new MyAsyncTask.OnError() {
            @Override
            public void handleError(Exception paramException) {
                mIsError = true;
            }
        });
    }

    private void collectOrDelete(final int method, final int id){
        mIsError = false;
        MyAsyncTask<CollectOrDeleteArticleInfo> task = new MyAsyncTask<CollectOrDeleteArticleInfo>(getContext()) {
            @Override
            public CollectOrDeleteArticleInfo callService() throws IOException, JsonParseException, BizException, ServiceException {
                if(method == 1){
                    return new ArticleService().collectArticle(id);
                }else if(method == 2){
                    return new ArticleService().deleteArticle(id);
                }
                return null;
            }

            @Override
            public void onLoaded(CollectOrDeleteArticleInfo paramT) throws Exception {
                if(!mIsError){
                    if(paramT.isSuccess()){
                        MyToast.show(getContext(),"操作成功");
                        if(method == 2){
                            int deleteposition = 0;
                            for(int i=0;i<articleList.size();i++){
                                if(articleList.get(i).getId() == id){
                                    deleteposition = i;
                                    break;
                                }
                            }
                            articleList.remove(deleteposition);
                            mRecordAdapter.notifyDataSetChanged();
                        }
                    }else{
                        MyToast.show(getContext(),paramT.getMessage());
                    }
                }else{
                    MyToast.show(getContext(),getErrorMessage());
                }
            }
        };
        task.executeTask();
        task.setOnError(new MyAsyncTask.OnError() {
            @Override
            public void handleError(Exception paramException) {
                mIsError = true;
            }
        });
    }

    private void readArticle(final int id){
        MyAsyncTask<Response> task = new MyAsyncTask<Response>(getContext()) {
            @Override
            public Response callService() throws IOException, JsonParseException, BizException, ServiceException {
                return new ArticleService().readArticle(id);
            }

            @Override
            public void onLoaded(Response paramT) throws Exception {

            }
        };
        task.executeTask();
    }

    private void shareArticle(final int id){
        MyAsyncTask<ShareInfo> task = new MyAsyncTask<ShareInfo>(getActivity()) {
            @Override
            public ShareInfo callService() throws IOException, JsonParseException, BizException, ServiceException {
                return new ArticleService().shareArticle(id);
            }

            @Override
            public void onLoaded(ShareInfo paramT) throws Exception {
                buildImage(paramT.getUrl(),paramT.getTitle());
            }
        };
        task.executeTask();
    }

    private Bitmap b1;
    private Bitmap b2;

    private void buildImage(String url,String title){
        b2 = BitmapFactory.decodeResource(getResources(),R.mipmap.logofor256);
        b1 = wordtoimage(url,title,b2.getWidth());
        Bitmap b3 = newBitmap(b1,b2);
        OnekeyShare oks = new OnekeyShare();
        oks.setText(url);
        savebitmap("awx",b3);
        oks.setImagePath(Environment.getExternalStorageDirectory().getAbsolutePath() +"/awx.png");
        oks.show(getActivity());
        MyToast.show(getActivity(),"目前仅支持微信及朋友圈分享！");
    }

    private File savebitmap(String filename, Bitmap bitmap) {
        String extStorageDirectory = Environment.getExternalStorageDirectory().getAbsolutePath();
        OutputStream outStream = null;
        File file = new File(extStorageDirectory, filename + ".png");
        try {
            outStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.flush();
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    private Bitmap newBitmap(Bitmap bit1,Bitmap bit2){
        int width = bit2.getWidth();
        int height = bit1.getHeight() + bit2.getHeight();
        //创建一个空的Bitmap(内存区域),宽度等于第一张图片的宽度，高度等于两张图片高度总和
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        //将bitmap放置到绘制区域,并将要拼接的图片绘制到指定内存区域
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(bit1, 0, 0, null);
        canvas.drawBitmap(bit2, 0, bit1.getHeight(), null);
        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();
        return bitmap;
    }

    private Bitmap wordtoimage(String url,String word,int w){
        int x=0,y=30;
        Bitmap bitmap;
        try {
            TextProperty tp = new TextProperty("《" + word+"》 " + "来自 "+url);
            //bitmap = Bitmap.createBitmap(20*tp.getWidth(), 20*tp.getHeight(), Bitmap.Config.ARGB_8888);
            bitmap = Bitmap.createBitmap(w, 40 * tp.getHeigt(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            canvas.drawColor(getResources().getColor(R.color.colorPrimary));
            Paint paint = new Paint();
            paint.setColor(getResources().getColor(R.color.white));
            paint.setTextSize(16);
            paint.setAntiAlias(true);
            paint.setSubpixelText(true);
            paint.setFlags(Paint.ANTI_ALIAS_FLAG);
            paint.setFlags(Paint.DITHER_FLAG);
            paint.setHinting(Paint.HINTING_ON);
            String [] ss = tp.getContext();
            //canvas.drawText(tp.getString(),x,y,paint);
            int k = 0;
            for(int i=0;i < tp.getHeigt();i++){
                if(k+tp.getWidth() < tp.getString().length()){
                    canvas.drawText(tp.getString().substring(k,k+tp.getWidth()),x,y,paint);
                }else{
                    canvas.drawText(tp.getString().substring(k,tp.getString().length()),x,y,paint);
                }
                k = k+tp.getWidth();
                y = y+20;
            }
            canvas.save(Canvas.ALL_SAVE_FLAG);
            canvas.restore();
            return bitmap;
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    private void showProgressDialog(String loadingMessage){
        try {
            progressDialog = DialogUtil.getProgressDialog(getContext(),loadingMessage);
            progressDialog.show();
        } catch (Exception e) {

        }
    }

    private void closeProgressDialog() {
        try {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        } catch (Exception e) {

        }
    }

    public class TextProperty {
        private int heigt;      //读入文本的行数
        private int width = 25;      //一行文字数
        private String[] context = new String[2048];
        private String s = "";//存储读入的文本

        /*
         *@parameter wordNum  设置每行显示的字数
         * 构造函数将文本读入，将每行字符串切割成小于等于35个字符的字符串  存入字符数组
         *
         */
        public TextProperty(String s){
            this.s = s;
            for(int i=0;i<s.length();i++){
                context[i] = s.substring(i,i+1);
            }
            heigt = (s.length()/width)+1;
            //heigt = s.length();
            //width = s.length();
        }


        public int getHeigt() {
            return heigt;
        }

        public int getWidth(){
            return width;
        }

        public String[] getContext() {
            return context;
        }

        public String getString(){
            return s;
        }
    }
}
