package com.hec.app.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.JsonParseException;
import com.hec.app.R;
import com.hec.app.activity.LoginActivity;
import com.hec.app.activity.MoneyActivity;
import com.hec.app.activity.RecordContentActivity;
import com.hec.app.activity.StartActivity;
import com.hec.app.activity.base.BaseApp;
import com.hec.app.entity.AfterLotteryInfo;
import com.hec.app.entity.BizException;
import com.hec.app.entity.BrowseResultInfo;
import com.hec.app.entity.BulletinInfo;
import com.hec.app.entity.DetailLotteryInfo;
import com.hec.app.entity.HasCollection;
import com.hec.app.entity.MoneyDetailInfo;
import com.hec.app.entity.NewsInfo;
import com.hec.app.entity.PartlyLotteryInfo;
import com.hec.app.framework.adapter.MyDecoratedAdapter;
import com.hec.app.framework.content.CBCollectionResolver;
import com.hec.app.framework.content.CollectionStateObserver;
import com.hec.app.framework.widget.MyToast;
import com.hec.app.util.DateTransmit;
import com.hec.app.util.DialogUtil;
import com.hec.app.util.IntentUtil;
import com.hec.app.util.MyAsyncTask;
import com.hec.app.util.TestUtil;
import com.hec.app.webservice.DetailLotteryService;
import com.hec.app.webservice.HomeService;
import com.hec.app.webservice.MoneyService;
import com.hec.app.webservice.NewsService;
import com.hec.app.webservice.RequestAnno;
import com.hec.app.webservice.ServiceException;

import java.io.IOException;
import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Callable;

/**
 * Created by Joshua on 2016/1/5.
 */
public class RecordListFragment extends ListFragment{

    public static final int REQUEST_DETAIL = 0x110;
    private List<String> mTitles = Arrays.asList("Hello", "World", "Android");
    private int mCurrentPos ;
    private ArrayAdapter<String> mAdapter ;
    private simpleRecordListAdapter mRecordAdapter;
    private List<Map<String, String>> records;
    private List<PartlyLotteryInfo> partlyLotteryInfoList = new ArrayList<>();
    private List<AfterLotteryInfo> afterLotteryInfo = new ArrayList<>();
    private List<MoneyDetailInfo> moneyDetailInfo = new ArrayList<>();
    public static final String ARGUMENT = "argument";
    private final String PLAY_ID = "PlayID";
    private String type;
    private boolean mIsError;
    private TabLayout tabLayout;
    private View footerView;
    private LayoutInflater inflater;
    private final static String TYPE_TODAY = "1";
    private final static String TYPE_THREEDAYS = "2";
    private final static String TYPE_WEEK = "3";
    private final static String TYPE_MONTH = "4";
    private int[] colors= {R.color.green, R.color.red, R.color.gray};
    private int lastItem;
    private int OFFSET=0;
    private final int LIMIT = 15;
    private String STATE = null;
    TextView loading;ProgressBar progressBar;
    private ProgressDialog progressDialog;
    private View view;

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        type = getActivity().getIntent().getStringExtra(RecordListFragment.ARGUMENT);
        if(type.compareTo("投注记录") == 0){
            showProgressDialog("正在加载投注纪录");
        }else if(type.compareTo("追号记录") == 0) {
            showProgressDialog("正在加载追号纪录");
        }else if(type.compareTo("投注支出") == 0 || type.compareTo("奖金派送") == 0
                || type.compareTo("充值&转入") == 0 || type.compareTo("提现&转出") == 0){
            showProgressDialog("正在加载今天资金明细");
        }
        footerView.setVisibility(View.VISIBLE);
        setTablayout();
        setonTabClickListener();
        records = new ArrayList<Map<String, String>>();
        mRecordAdapter = new simpleRecordListAdapter(getActivity(), records);
        getListView().setDivider(null);
        getListView().setDividerHeight(0);
        getListView().addFooterView(footerView);
        if(type.compareTo("投注支出") == 0 || type.compareTo("奖金派送") == 0
                || type.compareTo("充值&转入") == 0 || type.compareTo("提现&转出") == 0){
            getListView().setSelector(getResources().getDrawable(R.color.white));
        }

        getListView().setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if(scrollState == SCROLL_STATE_IDLE && lastItem == mRecordAdapter.getCount()){
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            OFFSET = OFFSET + LIMIT;
                            if(tabLayout.getSelectedTabPosition()==0){
                                if(type.compareTo("投注记录") == 0){
                                    getDetailLotteryInfo(STATE,OFFSET,LIMIT);
                                }else if(type.compareTo("追号记录") == 0){
                                    getAfterLotteryInfo(STATE,OFFSET,LIMIT);
                                }else if (type.compareTo("投注支出") == 0 || type.compareTo("奖金派送") == 0
                                        || type.compareTo("充值&转入") == 0 || type.compareTo("提现&转出") == 0){
                                    getMoneyDetailInfo(TYPE_TODAY,type,OFFSET,LIMIT);
                                }
                            }else if(tabLayout.getSelectedTabPosition()==1){
                                if(type.compareTo("投注记录") == 0){
                                    getDetailLotteryInfo(STATE,OFFSET,LIMIT);
                                }else if(type.compareTo("追号记录") == 0){
                                    getAfterLotteryInfo(STATE,OFFSET,LIMIT);
                                }else if (type.compareTo("投注支出") == 0 || type.compareTo("奖金派送") == 0
                                        || type.compareTo("充值&转入") == 0 || type.compareTo("提现&转出") == 0){
                                    getMoneyDetailInfo(TYPE_THREEDAYS,type,OFFSET,LIMIT);
                                }
                            }else if(tabLayout.getSelectedTabPosition()==2){
                                if(type.compareTo("投注记录") == 0){
                                    getDetailLotteryInfo(STATE,OFFSET,LIMIT);
                                }else if(type.compareTo("追号记录") == 0){
                                    getAfterLotteryInfo(STATE,OFFSET,LIMIT);
                                }else if (type.compareTo("投注支出") == 0 || type.compareTo("奖金派送") == 0
                                        || type.compareTo("充值&转入") == 0 || type.compareTo("提现&转出") == 0){
                                    getMoneyDetailInfo(TYPE_WEEK,type,OFFSET,LIMIT);
                                }
                            }else if(tabLayout.getSelectedTabPosition()==3){
                                if(type.compareTo("投注记录") == 0){
                                    getDetailLotteryInfo(STATE,OFFSET,LIMIT);
                                }else if(type.compareTo("追号记录") == 0){
                                    getAfterLotteryInfo(STATE,OFFSET,LIMIT);
                                }else if (type.compareTo("投注支出") == 0 || type.compareTo("奖金派送") == 0
                                        || type.compareTo("充值&转入") == 0 || type.compareTo("提现&转出") == 0){
                                    getMoneyDetailInfo(TYPE_MONTH,type,OFFSET,LIMIT);
                                }
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
        if(type.compareTo("投注记录") == 0){
            getDetailLotteryInfo(STATE,OFFSET,LIMIT);
        }else if(type.compareTo("追号记录") == 0) {
            getAfterLotteryInfo(STATE,OFFSET,LIMIT);
        }else if(type.compareTo("投注支出") == 0 || type.compareTo("奖金派送") == 0
                || type.compareTo("充值&转入") == 0 || type.compareTo("提现&转出") == 0){
            getMoneyDetailInfo(TYPE_TODAY,type,OFFSET,LIMIT);
        }
    }

    private void setonTabClickListener() {

        if(tabLayout!=null){
            tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    if (type.compareTo("投注记录") == 0) {
                        if (tab.getText().equals("全部")) {
                            partlyLotteryInfoList.clear();
                            STATE = "";
                            OFFSET = 0;
                            showProgressDialog("正在加载投注纪录");
                            getDetailLotteryInfo(STATE, OFFSET, LIMIT);
                        } else if (tab.getText().equals("未开奖")) {
                            partlyLotteryInfoList.clear();
                            STATE = "0";
                            OFFSET = 0;
                            showProgressDialog("正在加载未开奖纪录");
                            getDetailLotteryInfo(STATE,OFFSET,LIMIT);
                        } else if (tab.getText().equals("已中奖")) {
                            partlyLotteryInfoList.clear();
                            STATE = "1";
                            OFFSET = 0;
                            showProgressDialog("正在加载已中奖纪录");
                            getDetailLotteryInfo(STATE,OFFSET,LIMIT);
                        } else if (tab.getText().equals("未中奖")) {
                            partlyLotteryInfoList.clear();
                            STATE = "2";
                            OFFSET = 0;
                            showProgressDialog("正在加载未中奖纪录");
                            getDetailLotteryInfo(STATE,OFFSET,LIMIT);
                        }
                    } else if (type.compareTo("追号记录") == 0) {
                        if (tab.getText().equals("全部")) {
                            afterLotteryInfo.clear();
                            STATE = "";
                            OFFSET = 0;
                            showProgressDialog("正在加载追号纪录");
                            getAfterLotteryInfo(STATE,OFFSET,LIMIT);
                        } else if (tab.getText().equals("进行中")) {
                            afterLotteryInfo.clear();
                            STATE = "0";
                            OFFSET = 0;
                            showProgressDialog("正在加载进行中纪录");
                            getAfterLotteryInfo(STATE, OFFSET, LIMIT);
                        } else if (tab.getText().equals("已结束")) {
                            afterLotteryInfo.clear();
                            STATE = "1";
                            OFFSET = 0;
                            showProgressDialog("正在加载已结束纪录");
                            getAfterLotteryInfo(STATE, OFFSET, LIMIT);
                        } else if (tab.getText().equals("已中止")) {
                            afterLotteryInfo.clear();
                            STATE = "2";
                            OFFSET = 0;
                            showProgressDialog("正在加载已中止纪录");
                            getAfterLotteryInfo(STATE,OFFSET,LIMIT);
                        }
                    }else if(type.compareTo("投注支出") == 0 || type.compareTo("奖金派送") == 0
                            || type.compareTo("充值&转入") == 0 || type.compareTo("提现&转出") == 0){
                        if(tab.getText().equals("今天")){
                            OFFSET = 0;
                            moneyDetailInfo.clear();
                            showProgressDialog("正在加载今天资金明细");
                            getMoneyDetailInfo(TYPE_TODAY, type,OFFSET,LIMIT);
                        }else if(tab.getText().equals("3天内")){
                            OFFSET=0;
                            moneyDetailInfo.clear();
                            showProgressDialog("正在加载3天内资金明细");
                            getMoneyDetailInfo(TYPE_THREEDAYS,type,OFFSET,LIMIT);
                        }else if(tab.getText().equals("本周")){
                            OFFSET = 0;
                            moneyDetailInfo.clear();
                            showProgressDialog("正在加载本周资金明细");
                            getMoneyDetailInfo(TYPE_WEEK,type,OFFSET,LIMIT);
                        }else if(tab.getText().equals("本月")){
                            OFFSET = 0;
                            moneyDetailInfo.clear();
                            showProgressDialog("正在加载本月资金明细");
                            getMoneyDetailInfo(TYPE_MONTH,type,OFFSET,LIMIT);
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_record_list, container, false);
        view.setVisibility(View.INVISIBLE);
        footerView = inflater.inflate(R.layout.footer_view_item, null);
        loading = (TextView) footerView.findViewById(R.id.footer_item);
        progressBar = (ProgressBar) footerView.findViewById(R.id.progressBar_loading);
        loading.setText("上拉加载!");
        footerView.setVisibility(View.INVISIBLE);
        return view;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id)
    {
        mCurrentPos = position ;
        Intent intent = new Intent(getActivity(), RecordContentActivity.class);
        if(type.compareTo("投注记录") == 0){
            if(position<=partlyLotteryInfoList.size()-1) {
                intent.putExtra(RecordContentFragment.ARGUMENT, "投注详情");
                intent.putExtra(RecordContentFragment.PLAYID, partlyLotteryInfoList.get(position).getOrderID());
                startActivityForResult(intent, REQUEST_DETAIL);
            }
        }else if(type.compareTo("追号记录") == 0){
            if(position<=afterLotteryInfo.size()-1) {
                intent.putExtra(RecordContentFragment.ARGUMENT, "追号详情");
                intent.putExtra(RecordContentFragment.AFTERNOID, afterLotteryInfo.get(position).getAfterNoID());
                startActivityForResult(intent, REQUEST_DETAIL);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Log.e("TAG", "onActivityResult");
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_DETAIL)
        {
//            mTitles.set(mCurrentPos, mTitles.get(mCurrentPos)+" -- "+ data.getStringExtra(RecordContentFragment.RESPONSE));
//            mAdapter.notifyDataSetChanged();
        }
    }

    private void setTablayout(){
        tabLayout = (TabLayout) getView().findViewById(R.id.record_list_tabs);
        if(type.compareTo("投注记录") == 0){
            tabLayout.addTab(tabLayout.newTab().setText("全部"));
            tabLayout.addTab(tabLayout.newTab().setText("未开奖"));
            tabLayout.addTab(tabLayout.newTab().setText("已中奖"));
            tabLayout.addTab(tabLayout.newTab().setText("未中奖"));
        }else if(type.compareTo("追号记录") == 0){
            tabLayout.addTab(tabLayout.newTab().setText("全部"));
            tabLayout.addTab(tabLayout.newTab().setText("进行中"));
            tabLayout.addTab(tabLayout.newTab().setText("已结束"));
            tabLayout.addTab(tabLayout.newTab().setText("已中止"));
        }else if(type.compareTo("投注支出") == 0 || type.compareTo("奖金派送") == 0
                || type.compareTo("充值&转入") == 0 || type.compareTo("提现&转出") == 0){
            tabLayout.addTab(tabLayout.newTab().setText("今天"));
            tabLayout.addTab(tabLayout.newTab().setText("3天内"));
            tabLayout.addTab(tabLayout.newTab().setText("本周"));
            //tabLayout.addTab(tabLayout.newTab().setText("本月"));
        }
    }

    private void getData(List<PartlyLotteryInfo> mPartlyLotteryInfoList,
                         List<AfterLotteryInfo> mAfterLotteryInfo) {
        records.clear();
        if(type.compareTo("投注记录")==0){
            if(mPartlyLotteryInfoList!=null){
                for(PartlyLotteryInfo p : mPartlyLotteryInfoList){
                    String dateStr = DateTransmit.dateTransmits(p.getNoteTime());
                    Map<String, String> mMap = new HashMap();
                    mMap.put("day", dateStr.substring(8,10)+"日");
                    mMap.put("month", dateStr.substring(5,7)+"月");
                    mMap.put("title",p.getLotteryType().replace("和盛","聚星"));
                    if(p.getOrderState()==0){
                        mMap.put("status","未开奖");
                    }else if(p.getOrderState()==1){
                        mMap.put("status","已中奖");
                    }else if(p.getOrderState()==2){
                        mMap.put("status","未中奖");
                    }else if(p.getOrderState()==3){
                        mMap.put("status","已撤单");
                    }
                    mMap.put("price"," "+String.valueOf(p.getNoteMoney()));
                    mMap.put("lotterytype", p.getPlayTypeName());
                    mMap.put("amount", " "+ String.valueOf(p.getWinMoney()));
                    records.add(mMap);
                }
            }
        }else if(type.compareTo("追号记录") == 0){
            if(mAfterLotteryInfo!=null){
                for(AfterLotteryInfo a : mAfterLotteryInfo){
                    String dateStr = DateTransmit.dateTransmits(a.getOrderTime());
                    Map<String, String> mMap = new HashMap();
                    mMap.put("day", dateStr.substring(8,10)+"日");
                    mMap.put("month", dateStr.substring(5, 7) + "月");
                    mMap.put("title",a.getLotteryType().replace("和盛","聚星"));
                    mMap.put("status", String.valueOf(a.getAfterState()));
                    mMap.put("ordermoney",String.valueOf(a.getOrderMoney()));
                    mMap.put("restperiods",String.valueOf(a.getRestPeriods()));
                    mMap.put("totalperiods",String.valueOf(a.getTotalPeriods()));
                    records.add(mMap);
                }
            }
        }

        mRecordAdapter.notifyDataSetChanged();
    }

    private void getMoenyData(List<MoneyDetailInfo> mMoneyDetailInfo){
        records.clear();
            if(mMoneyDetailInfo!=null){
                for(MoneyDetailInfo m : mMoneyDetailInfo){
                    String dateStr = DateTransmit.dateTransmits(m.getDate());
                    Map<String, String> mMap = new HashMap();
                    mMap.put("day", dateStr.substring(8,10)+"日");
                    mMap.put("month", dateStr.substring(5, 7) + "月");
                    mMap.put("itemname",m.getItemName());
                    mMap.put("itemdetails",m.getItemDetails());
                    mMap.put("amount",String.valueOf(m.getAmount()));
                    records.add(mMap);
                }
                Log.i("wxj","get money null");
            }
        Log.i("wxj","get money here");
        mRecordAdapter.notifyDataSetChanged();
    }

    public class simpleRecordListAdapter extends ArrayAdapter{

        private final Context context;
        List<Map<String, String>> mlist;
        String mtype;

        public simpleRecordListAdapter(Context context, List<Map<String, String>> mlist) {
            super(context, R.layout.list_item_record);
            this.context = context;
            this.mlist = mlist;
        }

        @Override
        public int getCount() {
            return mlist.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.list_item_record, parent, false);
            TextView day = (TextView) rowView.findViewById(R.id.recordDay);
            TextView month = (TextView) rowView.findViewById(R.id.recordMonth);
            TextView price = (TextView) rowView.findViewById(R.id.price);
            TextView title = (TextView) rowView.findViewById(R.id.recordTitle);
            TextView status = (TextView) rowView.findViewById(R.id.recordStatus);
            TextView amount = (TextView) rowView.findViewById(R.id.winAmount);
            TextView lotterytype = (TextView) rowView.findViewById(R.id.lotterytype);
            View divider = rowView.findViewById(R.id.record_divider);
            View divider2 = rowView.findViewById(R.id.record_divider2);
            View divider3 = rowView.findViewById(R.id.record_divider3);

                Map<String, String> map = mlist.get(position);
                String s = "";
                if(type.compareTo("投注记录") == 0) {
                    day.setText(map.get("day"));
                    month.setText(map.get("month"));
                    title.setText(map.get("title"));
                    price.setText("¥ "+map.get("price"));
                    status.setText(map.get("status"));
                    amount.setText("¥ "+map.get("amount"));
                    lotterytype.setText("["+map.get("lotterytype")+"]");
                    s = map.get("status");
                }else if (type.compareTo("追号记录") == 0){
                    day.setText(map.get("day"));
                    month.setText(map.get("month"));
                    title.setText(map.get("title"));
                    price.setText("¥"+map.get("ordermoney"));
                    status.setText(map.get("status"));
                    lotterytype.setVisibility(View.GONE);
                    s = map.get("status");
                    int i = Integer.parseInt(map.get("totalperiods"))-Integer.parseInt(map.get("restperiods"));
                    amount.setText(i+""+"/"+map.get("totalperiods"));
                }else if(type.compareTo("投注支出") == 0 || type.compareTo("奖金派送") == 0
                        || type.compareTo("充值&转入") == 0 || type.compareTo("提现&转出") == 0){
                    day.setText(map.get("day"));
                    month.setText(map.get("month"));
                    title.setText(map.get("itemname"));
                    price.setText(map.get("itemdetails"));
                    lotterytype.setVisibility(View.GONE);
                    amount.setVisibility(View.INVISIBLE);
                    status.setTextSize(16);
                    if(!map.get("amount").substring(0, 1).equals("-")){
                        status.setTextColor(getResources().getColor(R.color.green));
                        status.setText("+" + map.get("amount"));
                    }else if(map.get("amount").substring(0,1).equals("-")){
                        status.setTextColor(getResources().getColor(R.color.gray));
                        status.setText(map.get("amount"));
                    }
                    status.setBackgroundColor(getResources().getColor(R.color.white));
                }
                if(type.compareTo("投注记录") == 0){
                    if (s.startsWith("已中奖"))
                        status.setBackgroundResource(R.mipmap.icon_drawn);
                    else if(s.startsWith("未中奖"))
                        status.setBackgroundResource(R.mipmap.icon_undrawn);
                    else if(s.startsWith("未开奖"))
                        status.setBackgroundResource(R.mipmap.resize_padding);

                } else if(type.compareTo("追号记录") == 0) {
                    if (s.equals("0")) {
                        status.setText("进行中");
                        status.setBackgroundResource(R.mipmap.icon_afterchasing);
                    }else if (s.equals("2")) {
                        status.setText("已中止");
                        status.setBackgroundResource(R.mipmap.icon_drawn);
                    }else if (s.equals("1")){
                        status.setText("已结束");
                        status.setBackgroundResource(R.mipmap.icon_afterfinish);
                    }
                }
                if(position > 0 &&
                        ( mlist.get(position - 1).get("day").compareTo(map.get("day")) == 0 &&
                                mlist.get(position - 1).get("month").compareTo(map.get("month")) == 0)) {
                    divider.setVisibility(View.GONE);
                    day.setVisibility(View.GONE);
                    month.setVisibility(View.GONE);
                    divider3.setVisibility(View.VISIBLE);
                }else{
                    divider3.setVisibility(View.GONE);
                }

                if(position == mlist.size() - 1){
                    divider2.setVisibility(View.VISIBLE);
                }

                return rowView;
        }
    }

    public class RecordListAdapter extends
            MyDecoratedAdapter<NewsInfo> {
        int resource;

        public RecordListAdapter(Context context, int _resource) {
            super(context);
            this.mContext = context;
            this.inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.resource=_resource;
        }

        private LayoutInflater inflater;
        private Context mContext;

        @Override
        protected View newErrorView(Context context, ViewGroup parent) {
            View view = inflater.inflate(R.layout.list_item_error, parent,
                    false);
            view.findViewById(R.id.retry).setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            retry();
                        }
                    });

            return view;
        }

        @Override
        protected View newLoadingView(Context context, ViewGroup parent) {
            return inflater.inflate(R.layout.list_item_loading, parent, false);
        }

        @Override
        protected View newNormalView(int position, View convertView,
                                     ViewGroup parent) {
            LinearLayout newView;
            NewsInfo news = getItem(position);
            String title = news.getTitle();
            String summary = news.getSummary();
            if(convertView == null){
                newView = new LinearLayout(getContext());
                String inflater = Context.LAYOUT_INFLATER_SERVICE;
                LayoutInflater li;
                li = (LayoutInflater)getContext().getSystemService(inflater);
                li.inflate(resource, newView, true);
            }
            else{
                newView = (LinearLayout)convertView;
            }

            TextView titleView = (TextView)newView.findViewById(R.id.titleTextView);
            TextView contentView = (TextView)newView.findViewById(R.id.contentTextView);

            titleView.setText(title);
            contentView.setText(summary);

            return newView;
        }


    }

    @RequestAnno
    private void getDetailLotteryInfo(final String state,final int offset, final int limit){
        mIsError = false;
        MyAsyncTask<List<PartlyLotteryInfo>> task = new MyAsyncTask<List<PartlyLotteryInfo>>(getActivity()) {

            @Override
            public List<PartlyLotteryInfo> callService() throws IOException,
                    JsonParseException, BizException, ServiceException {
                return new DetailLotteryService().getDetailLotteryInfo(state, offset, limit);
            }

            @Override
            public void onLoaded(List<PartlyLotteryInfo> result) throws Exception {
                if(getActivity() == null || getActivity().isFinishing())
                    return;
                closeProgressDialog();
                if (!mIsError) {

                    if(result != null){
                        if(result.size()<LIMIT){
                            progressBar.setVisibility(View.GONE);
                            loading.setText("没有更多信息");
                        }else if(result.size()==LIMIT) {
                            progressBar.setVisibility(View.VISIBLE);
                            loading.setText("上拉加载!");
                        }
                        partlyLotteryInfoList.addAll(result);
                    }else{
                        progressBar.setVisibility(View.GONE);
                        loading.setText("没有更多信息");
                    }
                    getData(partlyLotteryInfoList,null);
                }
                else {
                    BaseApp.changeUrl(getActivity(), new BaseApp.OnChangeUrlListener() {
                        @Override
                        public void changeSuccess() {
                            getDetailLotteryInfo(state, offset, limit);
                        }

                        @Override
                        public void changeFail() {
                            footerView.setVisibility(View.INVISIBLE);
                            closeProgressDialog();
                        }
                    });
                }
                view.setVisibility(View.VISIBLE);
            }
        };
        task.setOnError(new MyAsyncTask.OnError() {

            @Override
            public void handleError(Exception e) {
                mIsError = true;
            }
        });
        task.executeTask();
    }

    private void getAfterLotteryInfo(final String state,final int offset, final int limit){
        mIsError = false;
        MyAsyncTask<List<AfterLotteryInfo>> task = new MyAsyncTask<List<AfterLotteryInfo>>(getActivity()) {

            @Override
            public List<AfterLotteryInfo> callService() throws IOException,
                    JsonParseException, BizException, ServiceException {
                return new DetailLotteryService().getAfterLotteryInfo(state, offset, limit);
            }

            @Override
            public void onLoaded(List<AfterLotteryInfo> result) throws Exception {
                if(getActivity() == null || getActivity().isFinishing())
                    return;
                closeProgressDialog();
                if (!mIsError) {
                    if(result != null){
                        if(result.size()<LIMIT){
                            progressBar.setVisibility(View.GONE);
                            loading.setText("没有更多信息");
                        }else if(result.size()==LIMIT) {
                            progressBar.setVisibility(View.VISIBLE);
                            loading.setText("上拉加载!");
                        }
                        afterLotteryInfo.addAll(result);
                    }else{
                        progressBar.setVisibility(View.GONE);
                        loading.setText("没有更多信息");
                    }
                    getData(null, afterLotteryInfo);
                }
                else {
                    BaseApp.changeUrl(getActivity(), new BaseApp.OnChangeUrlListener() {
                        @Override
                        public void changeSuccess() {
                            getAfterLotteryInfo(state, offset, limit);
                        }

                        @Override
                        public void changeFail() {
                            footerView.setVisibility(View.INVISIBLE);
                            closeProgressDialog();
                        }
                    });
                }
                view.setVisibility(View.VISIBLE);
            }
        };
        task.setOnError(new MyAsyncTask.OnError() {

            @Override
            public void handleError(Exception e) {
                mIsError = true;
            }
        });
        task.executeTask();
    }

    private void getMoneyDetailInfo(final String ordertype, final String kindOfMoney, final int offset, final int limit){
        mIsError = false;
        MyAsyncTask<List<MoneyDetailInfo>> task = new MyAsyncTask<List<MoneyDetailInfo>>(getActivity()) {

            @Override
            public List<MoneyDetailInfo> callService() throws IOException,
                    JsonParseException, BizException, ServiceException {
                return new MoneyService(getContext()).getMoneyDetailInfo(ordertype, kindOfMoney, offset, limit);
            }

            @Override
            public void onLoaded(List<MoneyDetailInfo> result) throws Exception {
                if(getActivity() == null || getActivity().isFinishing())
                    return;
                closeProgressDialog();
                if (!mIsError) {
                    if(result != null){
                        if(result.size()<LIMIT){
                            progressBar.setVisibility(View.GONE);
                            loading.setText("没有更多信息");
                        }else if(result.size()==LIMIT){
                            progressBar.setVisibility(View.VISIBLE);
                            loading.setText("上拉加载");
                        }
                        moneyDetailInfo.addAll(result);
                    }else{
                        progressBar.setVisibility(View.GONE);
                        loading.setText("该信息正在维护中");
                    }
                    getMoenyData(moneyDetailInfo);
                }
                else {
                    BaseApp.changeUrl(getActivity(), new BaseApp.OnChangeUrlListener() {
                        @Override
                        public void changeSuccess() {
                            getMoneyDetailInfo(ordertype, kindOfMoney, offset, limit);
                        }

                        @Override
                        public void changeFail() {
                            footerView.setVisibility(View.INVISIBLE);
                            closeProgressDialog();
                        }
                    });
                }

                view.setVisibility(View.VISIBLE);
            }
        };
        task.setOnError(new MyAsyncTask.OnError() {

            @Override
            public void handleError(Exception e) {
                mIsError = true;
            }
        });
        task.executeTask();
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
}
