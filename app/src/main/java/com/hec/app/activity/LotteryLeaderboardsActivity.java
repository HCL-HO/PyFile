package com.hec.app.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonParseException;
import com.hec.app.R;
import com.hec.app.activity.base.BaseActivity;
import com.hec.app.activity.base.BaseApp;
import com.hec.app.entity.BizException;
import com.hec.app.entity.LatestWinningListInfo;
import com.hec.app.util.DialogUtil;
import com.hec.app.util.MyAsyncTask;
import com.hec.app.webservice.HomeService;
import com.hec.app.webservice.ServiceException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LotteryLeaderboardsActivity extends BaseActivity {
    final String TYPE_DAY = "day";
    final String TYPE_WEEK = "week";
    final String TYPE_MONTH = "month";

    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;
    private LinearLayout tabBarLayout;
    private List<View> tabList;
    private int currentTabIndex;
    private boolean isError;
    private List<LatestWinningListInfo> dayList;
    private List<LatestWinningListInfo> weekList;
    private List<LatestWinningListInfo> monthList;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lottery_leaderboards);
        initData();
        initView();
    }

    private void initData() {
        tabList = new ArrayList<>();
        dayList = new ArrayList<>();
        weekList = new ArrayList<>();
        monthList = new ArrayList<>();
        currentTabIndex = 0;
    }

    private void initView() {
        ImageView imgBack = (ImageView) findViewById(R.id.imgBack);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        tabBarLayout = (LinearLayout) findViewById(R.id.tab_bar_ll);
        tabList.add(addTab("日排行"));
        tabList.add(addTab("周排行"));
//        tabList.add(addTab("月排行"));

        for (int i = 0; i < tabList.size(); ++i) {
            final int j = i;
            tabList.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            v.setEnabled(true);
                        }
                    }, 500);

                    (tabList.get(currentTabIndex).findViewById(R.id.icon)).setBackground(null);
                    (tabList.get(currentTabIndex).findViewById(R.id.tab_rl)).setBackground(null);
                    ((TextView) tabList.get(currentTabIndex).findViewById(R.id.text1)).setTextColor(Color.parseColor("#9ddcdc"));
                    (tabList.get(j).findViewById(R.id.icon)).setBackgroundColor(Color.parseColor("#f7e06a"));
                    (tabList.get(j).findViewById(R.id.tab_rl)).setBackgroundResource(R.color.tab_bar_background);
                    ((TextView) tabList.get(j).findViewById(R.id.text1)).setTextColor(Color.WHITE);
                    currentTabIndex = j;

                    switch (currentTabIndex) {
                        case 0:
                            adapter.setData(dayList);
                            if (dayList.size() == 0) {
                                getLatestWinningList(TYPE_DAY);
                            }
                            break;
                        case 1:
                            adapter.setData(weekList);
                            if (weekList.size() == 0) {
                                getLatestWinningList(TYPE_WEEK);
                            }
                            break;
                        case 2:
                            adapter.setData(monthList);
                            if (monthList.size() == 0) {
                                getLatestWinningList(TYPE_MONTH);
                            }
                            break;
                    }
                    adapter.notifyDataSetChanged();
                }
            });
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        adapter = new RecyclerViewAdapter(this);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(false);
                switch (currentTabIndex) {
                    case 0:
                        getLatestWinningList(TYPE_DAY);
                        break;
                    case 1:
                        getLatestWinningList(TYPE_WEEK);
                        break;
                    case 2:
                        getLatestWinningList(TYPE_MONTH);
                        break;
                }
            }
        });

        tabList.get(0).callOnClick();
    }

    View addTab(String tabTitle) {
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View tab = inflater.inflate(R.layout.custom_leaderboards_tab_view, tabBarLayout, false);
        TextView text = (TextView)tab.findViewById(R.id.text1);
        text.setText(tabTitle);
        tabBarLayout.addView(tab);
        return tab;
    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
        private List<LatestWinningListInfo> data;
        private Context context;

        public class ViewHolder extends RecyclerView.ViewHolder {
            public ImageView noImage;
            public TextView noText;
            public TextView nameText;
            public TextView lotteryText;
            public TextView amountText;
            public ViewHolder(View view) {
                super(view);
                noImage = (ImageView)view.findViewById(R.id.no_image);
                noText = (TextView)view.findViewById(R.id.no_text);
                nameText = (TextView)view.findViewById(R.id.name_text);
                lotteryText = (TextView)view.findViewById(R.id.lottery_text);
                amountText = (TextView)view.findViewById(R.id.amount_text);
            }
        }

        public RecyclerViewAdapter(Context context) {
            this.data = new ArrayList<>();
            this.context = context;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_lottery_leaderboard, null);
            ViewHolder holder = new ViewHolder(view);
            return holder;
        }

        public void setData(List<LatestWinningListInfo> list){
            data.clear();
            data.addAll(list);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            LatestWinningListInfo info = data.get(position);

            switch (position) {
                case 0:
                    holder.noImage.setImageResource(R.mipmap.leaderboards_first);
                    holder.noText.setText("");
                    break;
                case 1:
                    holder.noImage.setImageResource(R.mipmap.leaderboards_second);
                    holder.noText.setText("");
                    break;
                case 2:
                    holder.noImage.setImageResource(R.mipmap.leaderboards_third);
                    holder.noText.setText("");
                    break;
                default:
                    holder.noImage.setImageResource(R.mipmap.leaderboards_other);
                    holder.noText.setText(String.valueOf(position+1));
                    break;
            }

            if (!TextUtils.isEmpty(info.getLotteryType())) {
                holder.lotteryText.setText(info.getLotteryType());
            }

            holder.amountText.setText(String.valueOf(info.getWinMoney()));

            String userName = info.getUserName();
            if(!TextUtils.isEmpty(userName) && userName.length() > 3) {
                userName = userName.substring(0, userName.length() - 3) + "***";
            } else if(!TextUtils.isEmpty(userName) && userName.length() > 2) {
                userName = userName.substring(0, userName.length() - 2) + "***";
            } else if(!TextUtils.isEmpty(userName) && userName.length() > 1) {
                userName = userName.substring(0, userName.length() - 1) + "***";
            }
            if (!TextUtils.isEmpty(userName)) {
                holder.nameText.setText(userName);
            }
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }

    private void getLatestWinningList(final String period){
        isError = false;
        showProgressDialog();
        MyAsyncTask<List<LatestWinningListInfo>> task = new MyAsyncTask<List<LatestWinningListInfo>>(LotteryLeaderboardsActivity.this) {
            @Override
            public List<LatestWinningListInfo> callService() throws IOException, JsonParseException, BizException, ServiceException {
                return new HomeService().getLatestWinningList(period);
            }

            @Override
            public void onLoaded(List<LatestWinningListInfo> list) throws Exception {
                closeProgressDialog();
                if (!isError && list != null) {
                    if (period.equals(TYPE_DAY)) {
                        dayList.clear();
                        dayList = list;
                        adapter.setData(dayList);
                    } else if (period.equals(TYPE_WEEK)) {
                        weekList.clear();
                        weekList = list;
                        adapter.setData(weekList);
                    } else if (period.equals(TYPE_MONTH)) {
                        monthList.clear();
                        monthList = list;
                        adapter.setData(monthList);
                    }

                    adapter.notifyDataSetChanged();
                } else {
                    BaseApp.changeUrl(LotteryLeaderboardsActivity.this, new BaseApp.OnChangeUrlListener() {
                        @Override
                        public void changeSuccess() {
                            getLatestWinningList(period);
                        }

                        @Override
                        public void changeFail() {}
                    });
                }
            }
        };
        task.setOnError(new MyAsyncTask.OnError() {
            @Override
            public void handleError(Exception paramException) {
                isError = true;
            }
        });
        task.executeTask();
    }

    private void showProgressDialog() {
        try {
            progressDialog = DialogUtil.getProgressDialog(this, getResources().getString(R.string.loading_list));
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
