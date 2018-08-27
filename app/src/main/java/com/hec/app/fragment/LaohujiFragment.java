package com.hec.app.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.JsonParseException;
import com.hec.app.R;
import com.hec.app.activity.RecordContentActivity;
import com.hec.app.activity.SlotRecordActivity;
import com.hec.app.activity.base.BaseApp;
import com.hec.app.entity.AfterLotteryInfo;
import com.hec.app.entity.BizException;
import com.hec.app.entity.MoneyDetailInfo;
import com.hec.app.entity.PartlyLotteryInfo;
import com.hec.app.entity.SlotInfo;
import com.hec.app.framework.widget.MyToast;
import com.hec.app.util.DateTransmit;
import com.hec.app.util.DialogUtil;
import com.hec.app.util.IntentUtil;
import com.hec.app.util.MyAsyncTask;
import com.hec.app.webservice.DetailLotteryService;
import com.hec.app.webservice.ServiceException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;


public class LaohujiFragment extends ListFragment {

    private ArrayAdapter<String> mAdapter ;
    private LaohujiFragment.simpleRecordListAdapter mRecordAdapter;
    private List<Map<String, String>> records;
    public static final String ARGUMENT = "argument";
    private List<SlotInfo> SlotInfoList = new ArrayList<>();
    private final String PLAY_ID = "PlayID";
    private String type;
    private boolean mIsError;
    private TabLayout tabLayout;
    private View footerView;
    private LayoutInflater inflater;
    private int lastItem;
    private int OFFSET=0;
    private final int LIMIT = 15;
    private String STATE = null;
    TextView loading;
    ProgressBar progressBar;
    private ProgressDialog progressDialog;
    private View view;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setTablayout();
        showProgressDialog("正在加载老虎机纪录");
        footerView.setVisibility(View.VISIBLE);
        setonTabClickListener();
        records = new ArrayList<Map<String, String>>();
        mRecordAdapter = new simpleRecordListAdapter(getActivity(), records);
        getListView().setDivider(null);
        getListView().setDividerHeight(0);
        getListView().addFooterView(footerView);

        getListView().setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if(scrollState == SCROLL_STATE_IDLE && lastItem == mRecordAdapter.getCount()){
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            OFFSET = OFFSET + LIMIT;
                            if(tabLayout.getSelectedTabPosition()==0){
                                getSlotInfo(STATE,OFFSET,LIMIT);
                            }else if(tabLayout.getSelectedTabPosition()==1){
                                getSlotInfo(STATE,OFFSET,LIMIT);
                            }else if(tabLayout.getSelectedTabPosition()==2){
                                getSlotInfo(STATE,OFFSET,LIMIT);
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
        getSlotInfo(STATE,OFFSET,LIMIT);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_record_list, container, false);
        view.setVisibility(View.INVISIBLE);
        footerView = inflater.inflate(R.layout.footer_view_item, null);
        loading = (TextView) footerView.findViewById(R.id.footer_item);
        progressBar = (ProgressBar) footerView.findViewById(R.id.progressBar_loading);
        loading.setText("上拉加载!");
        footerView.setVisibility(View.INVISIBLE);
        return view;
    }

    private void setTablayout() {
        tabLayout = (TabLayout) getView().findViewById(R.id.record_list_tabs);
        tabLayout.addTab(tabLayout.newTab().setText("全部"));
        tabLayout.addTab(tabLayout.newTab().setText("已中奖"));
        tabLayout.addTab(tabLayout.newTab().setText("未中奖"));
        tabLayout.addTab(tabLayout.newTab().setText("已取消"));
    }

    private void setonTabClickListener(){
        if(tabLayout!=null){
            tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                        if (tab.getText().equals("全部")) {
                            SlotInfoList.clear();
                            STATE = "";
                            OFFSET = 0;
                            showProgressDialog("正在加载老虎机纪录");
                            getSlotInfo(STATE, OFFSET, LIMIT);
                        } else if (tab.getText().equals("未中奖")) {
                            SlotInfoList.clear();
                            STATE = "2";
                            OFFSET = 0;
                            showProgressDialog("正在加载未中奖纪录");
                            getSlotInfo(STATE,OFFSET,LIMIT);
                        } else if (tab.getText().equals("已中奖")) {
                            SlotInfoList.clear();
                            STATE = "1";
                            OFFSET = 0;
                            showProgressDialog("正在加载已中奖纪录");
                            getSlotInfo(STATE,OFFSET,LIMIT);
                        } else if (tab.getText().equals("已取消")){
                            SlotInfoList.clear();
                            STATE = "4";
                            OFFSET = 0;
                            showProgressDialog("正在加载已取消纪录");
                            getSlotInfo(STATE,OFFSET,LIMIT);
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

    private void getSlotInfo(final String state,final int offset, final int limit){
            mIsError = false;
            MyAsyncTask<List<SlotInfo>> task = new MyAsyncTask<List<SlotInfo>>(getActivity()) {

                @Override
                public List<SlotInfo> callService() throws IOException,
                        JsonParseException, BizException, ServiceException {
                    return new DetailLotteryService().getSlotInfo(state, offset, limit);
                }

                @Override
                public void onLoaded(List<SlotInfo> result) throws Exception {
                    if(getActivity() == null || getActivity().isFinishing())
                        return;
                    if (!mIsError) {
                        Log.i("laohuji","222");
                        closeProgressDialog();
                        if(result.size()<LIMIT){
                            progressBar.setVisibility(View.GONE);
                            loading.setText("没有更多信息");
                        }else if(result.size()==LIMIT) {
                            progressBar.setVisibility(View.VISIBLE);
                            loading.setText("上拉加载!");
                        }
                        SlotInfoList.addAll(result);
                        getData(SlotInfoList);
                    }
                    else {
                        BaseApp.changeUrl(getActivity(), new BaseApp.OnChangeUrlListener() {
                            @Override
                            public void changeSuccess() {
                                getSlotInfo(state, offset, limit);
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

    private void getData(List<SlotInfo> SlotInfoLists) {
        records.clear();
            if(SlotInfoLists!=null){
                for(SlotInfo p : SlotInfoLists){
                    String dateStr = DateTransmit.dateTransmits(p.getNoteTime());
                    Map<String, String> mMap = new HashMap();
                    mMap.put("day", dateStr.substring(8,10)+"日");
                    mMap.put("month", dateStr.substring(5,7)+"月");
                    mMap.put("title",p.getPlayTypeName().replace("和盛","聚星"));
                    if(p.getOrderState()==0){
                        mMap.put("status","未开奖");
                    }else if(p.getOrderState()==4){
                        mMap.put("status","已取消");
                    }else if(p.getOrderState()==2){
                        mMap.put("status","未中奖");
                    }else if(p.getOrderState()==1){
                        mMap.put("status","已中奖");
                    }else {
                        mMap.put("status","已撤单");
                    }
                    mMap.put("price",String.valueOf(p.getNoteMoney()));
                    mMap.put("lotterytype", getMemo(p.getMemo(),p.getOrderState()));
                    Log.i("wxj", "getPlayTypeName" + getMemo(p.getMemo(),p.getOrderState()));
                    mMap.put("amount", String.valueOf(p.getWinMoney()));
                    records.add(mMap);
                }
            }
        Log.i("laohuji","getdata");
        mRecordAdapter.notifyDataSetChanged();
    }

    private String getMemo(String input,int state){
        Log.i("wxj","state "+state);
        if (state == 3){
            String[] array = input.split(" ");
            String output = "";
            if(array.length > 2){
                for(int i=2;i<array.length;i++){
                    output = output + array[i] + " ";
                }
            }
            if(output.length() > 25){
                return output.substring(0,output.length()-24);
            }
        }
        String[] array = input.split(" ");
        String output = "";
        if(array.length > 2){
            for(int i=2;i<array.length;i++){
                output = output + array[i] + " ";
            }
        }
        return output;
    }

    public class simpleRecordListAdapter extends ArrayAdapter{
        private final Context context;
        List<Map<String, String>> mlist;
        String mtype;

        public simpleRecordListAdapter(Context context, List<Map<String, String>> mlist) {
            super(context, R.layout.item_slot);
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
            View rowView = inflater.inflate(R.layout.item_slot, parent, false);
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
            Log.i("laohuji","in adaptor");
            Map<String, String> map = mlist.get(position);
            String s = "";
                day.setText(map.get("day"));
                month.setText(map.get("month"));
                title.setText(map.get("title"));
                price.setText("¥"+map.get("price"));
                status.setText(map.get("status"));
                amount.setText("¥"+map.get("amount"));
                String memo = map.get("lotterytype");
                if(memo.length() > 14){
                    lotterytype.setText("  "+map.get("lotterytype").substring(0,11)+"...");
                }else {
                    lotterytype.setText("  "+map.get("lotterytype"));
                }
                s = map.get("status");
                if (s.startsWith("已中奖"))
                    status.setBackgroundResource(R.mipmap.icon_drawn);
                else if(s.startsWith("未中奖"))
                    status.setBackgroundResource(R.mipmap.icon_undrawn);
                else if(s.startsWith("未开奖"))
                    status.setBackgroundResource(R.mipmap.resize_padding);
                else if(s.startsWith("已取消"))
                    status.setBackgroundResource(R.mipmap.resize_padding);
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

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Intent intent = new Intent(getActivity(), RecordContentActivity.class);
        intent.putExtra("slot", "slot");
        intent.putExtra("id", SlotInfoList.get(position).getID());
        startActivity(intent);
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
//                .setTitle("详细投注信息")
//                .setMessage(getMemo(SlotInfoList.get(position).getMemo(),SlotInfoList.get(position).getOrderState()))
//                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                });
//        builder.show();
    }
}
