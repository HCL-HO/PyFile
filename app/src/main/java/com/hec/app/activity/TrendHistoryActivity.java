package com.hec.app.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.JsonParseException;
import com.hec.app.R;
import com.hec.app.activity.base.BaseActivityWithMenu;
import com.hec.app.activity.base.BaseApp;
import com.hec.app.config.CommonConfig;
import com.hec.app.entity.BizException;
import com.hec.app.entity.TrendHistoryInfo;
import com.hec.app.framework.widget.ResideMenu;
import com.hec.app.framework.widget.MyToast;
import com.hec.app.lottery.LotteryConfig;
import com.hec.app.util.DialogUtil;
import com.hec.app.util.DisplayUtil;
import com.hec.app.util.IntentUtil;
import com.hec.app.util.LotteryUtil;
import com.hec.app.util.MyAsyncTask;
import com.hec.app.webservice.LotteryService;
import com.hec.app.webservice.ServiceException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class TrendHistoryActivity extends BaseActivityWithMenu implements View.OnClickListener{
    private ImageView imgPerson;
    private ImageView imgBack;
    boolean mIsError = true;
    LinearLayout tabLayout;
    ListView lv;
    List<View> tabs;

    LotteryService lotteryService;
    List<List<TrendHistoryInfo>> list;
    List<List<TrendHistoryInfo>> tempList;
    DListAdapter adapter;
    private ProgressDialog mProgressDialog;

    float mDensity;
    boolean hasNew[] = {false, true, true, true};
    int currentPage[] = {0, 0, 0, 0};
    int currentTab = 0;
    boolean error = false;


    int lotteryId;
    String lotteryType;

    ArrayList<Integer> idList;
    ArrayList<String> nameList;

    long newestIssue = -1;
    private ResideMenu resideMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trend_history);
        lotteryId = getIntent().getIntExtra(CommonConfig.INTENT_TREND_HISTORY_ID, 0);
        lotteryType = getIntent().getStringExtra(CommonConfig.INTENT_TREND_HISTORY_TYPE);

        idList = getIntent().getIntegerArrayListExtra(CommonConfig.INTENT_TREND_HISTORY_IDLIST);
        nameList = getIntent().getStringArrayListExtra(CommonConfig.INTENT_TREND_HISTORY_NAMELIST);

        list = new ArrayList<>();
        for (int i = 0; i <= 3; i++)
            list.add(new ArrayList<TrendHistoryInfo>());

        adapter = new DListAdapter(this);

        lv = (ListView) findViewById(R.id.lottery_history_lv);
        lv.setAdapter(adapter);
        lv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (!hasNew[currentTab] && !error)
                    return;
                if (view.getChildCount() == 0)
                    return;
                if (lv.getLastVisiblePosition() == lv.getAdapter().getCount() - 1
                        && lv.getChildAt(lv.getChildCount() - 1).getBottom() <= lv.getHeight()) {
                    if (error)
                        getResultData(currentTab, currentPage[currentTab]);
                    else
                        getResultData(currentTab, ++currentPage[currentTab]);
                    hasNew[currentTab] = false;
                }
            }
        });

        mDensity = getResources().getDisplayMetrics().density;

        resideMenu = super.getResidingMenu();
        imgPerson = (ImageView) findViewById(R.id.imgPlayTypeDes);
        imgPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resideMenu.openMenu(ResideMenu.DIRECTION_RIGHT);
            }
        });

        imgBack = (ImageView) findViewById(R.id.imgBack);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        tabs = new ArrayList<>();
        tabLayout = (LinearLayout) findViewById(R.id.trend_history_tab);
        tabs.add(addTab("今日"));
        tabs.add(addTab("近50期"));
        tabs.add(addTab("近100期"));
        //tabs.add(addTab("近2天"));
        ((ImageView) tabs.get(0).findViewById(R.id.icon)).setImageResource(R.mipmap.tab_yellow_bar);
        for (int i = 0; i < tabs.size(); i++) {
            final int j = i;
            tabs.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((ImageView) tabs.get(currentTab).findViewById(R.id.icon)).setImageDrawable(null);
                    ((ImageView) tabs.get(j).findViewById(R.id.icon)).setImageResource(R.mipmap.tab_yellow_bar);
                    currentTab = j;
                    adapter.setData(j);
                    adapter.notifyDataSetChanged();
                    if (list.get(currentTab).isEmpty()) {
                        getResultData(currentTab, 0);
                    }
                    if (lv != null)
                        lv.setSelectionAfterHeaderView();
                }
            });
        }
        Spinner spinner = (Spinner) findViewById(R.id.trend_history_spinner);
        ArrayAdapter<String> nameAdapter = new ArrayAdapter<String>(this, R.layout.trend_history_spinner_layout, nameList);

        spinner.setAdapter(nameAdapter);

        for (int i = 0; i < nameList.size(); i++) {
            String s = nameList.get(i);
            if (s.equals(lotteryType)) {
                spinner.setSelection(i);
                break;
            }
        }

        spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView adapterView, View view, int position, long id) {
                if (adapter != null)
                    adapter.clearData();
                adapter.notifyDataSetChanged();
                lotteryType = nameList.get(position);
                lotteryId = idList.get(position);
                if(adapterView.getChildAt(0)!=null) {
                    ((TextView) adapterView.getChildAt(0)).setTextColor(Color.WHITE);
                }
                initializeData();
            }

            public void onNothingSelected(AdapterView arg0) {

            }
        });

        Drawable spinnerDrawable = spinner.getBackground().getConstantState().newDrawable();

        spinnerDrawable.setColorFilter(Color.parseColor("#F1D02B"), PorterDuff.Mode.SRC_ATOP);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            spinner.setBackground(spinnerDrawable);
        }else{
            spinner.setBackgroundDrawable(spinnerDrawable);
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
    }

    private void initializeData() {
        lotteryService = new LotteryService();
        list = new ArrayList<>();
        tempList = new ArrayList<>();
        newestIssue = -1;
        for (int i = 0; i <= 3; i++) {
            list.add(new ArrayList<TrendHistoryInfo>());
            tempList.add(new ArrayList<TrendHistoryInfo>());
            hasNew[i] = false;
            currentPage[i] = 0;
        }
        try {
            getResultData(currentTab, 0);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (lv != null)
            lv.setSelectionAfterHeaderView();
    }

    private void getResultData(final int searchType, final int page) {
        showProgressDialog();
        mIsError = false;
        MyAsyncTask<List<TrendHistoryInfo>> task = new MyAsyncTask<List<TrendHistoryInfo>>(this) {
            @Override
            public List<TrendHistoryInfo> callService() throws IOException,
                    JsonParseException, BizException, ServiceException {
                return lotteryService.GetTrendHistory(lotteryId, searchType, page, LotteryConfig.PLAY_MODE.CLASSIC);
            }
            @Override
            public void onLoaded(List<TrendHistoryInfo> result) throws Exception {
                if(TrendHistoryActivity.this == null || TrendHistoryActivity.this.isFinishing())
                    return;
                closeProgressDialog();
                tempList.set(searchType, new ArrayList<TrendHistoryInfo>());
                if (mIsError) {
                    BaseApp.changeUrl(TrendHistoryActivity.this, new BaseApp.OnChangeUrlListener() {
                        @Override
                        public void changeSuccess() {
                            getResultData(searchType, page);
                        }

                        @Override
                        public void changeFail() {}
                    });
                }
                else {
                    if (result.isEmpty()) {
                        hasNew[searchType] = false;
                        Message msg = Message.obtain();
                        Bundle b = new Bundle();
                        b.putInt("type", searchType);
                        b.putInt("page", page);
                        msg.setData(b);
                        mHandler.sendMessage(msg);
                        return;
                    }
                    if (newestIssue == -1) {
                        newestIssue = Long.parseLong(result.get(0).getIssueNo()) + 1;

                    }
                    TrendHistoryInfo info = new TrendHistoryInfo();
                    info.setDummy(true);
                    if (page == 0) {
                        info.setIssueNo(Long.toString(newestIssue));
                        result.add(0, info);
                        info.setIssueNo("-1");
                    }
                    result.add(info);
                    tempList.set(searchType, result);
                    Message msg = Message.obtain();
                    Bundle b = new Bundle();
                    b.putInt("type", searchType);
                    b.putInt("page", page);
                    msg.setData(b);
                    mHandler.sendMessage(msg);
                    hasNew[searchType] = true;
                }
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

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage (Message msg) {
            super.handleMessage(msg);
            closeProgressDialog();
            //Update UI
            int type = msg.getData().getInt("type", -1);
            int page = msg.getData().getInt("page", -1);
            error = msg.getData().getBoolean("error", false);
            if (type == -1)
                return;
            if (!error) {
                if (list.get(type).size() > 1) {
                    if (list.get(type).get(list.get(type).size() - 1).isDummy())
                        list.get(type).remove(list.get(type).size() - 1);
                }

                list.get(type).addAll(tempList.get(type));
                adapter.setData(type);
                adapter.notifyDataSetChanged();
            }
            closeProgressDialog();
        }
    };

    private void showProgressDialog() {
        try {
            mProgressDialog = DialogUtil.getProgressDialog(this, getResources().getString(R.string.loading_list));
            mProgressDialog.show();
        } catch (Exception e) {

        }
    }

    private void closeProgressDialog() {
        try {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        } catch (Exception e) {

        }
    }
    public class DListAdapter extends BaseAdapter{
        private Activity activity;
        private LayoutInflater inflater = null;
        private List<TrendHistoryInfo> data;
        private int type = 0;

        public DListAdapter(Activity a){
            activity = a;
            if (!setData(0))
                MyToast.show(getBaseContext(), "Failed to obtain list");
            inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public boolean setData(int type) {
            if (list.get(type) != null) {
                data = list.get(type);
                this.type = type;
                return true;
            } else {
                data = new ArrayList<>();
            }
            return false;
        }

        public void clearData(){
            if (data != null)
                data.clear();
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub

            convertView = inflater.inflate(R.layout.list_item_trend_history, null);
            if (position == 0) {
                ((TextView) convertView.findViewById(R.id.lottery_history_issue)).setText(Long.toString(newestIssue));
                convertView.findViewById(R.id.pending_ll).setVisibility(View.VISIBLE);
                return convertView;
            } else if (position == data.size() - 1 && data.get(position).isDummy()) {
                convertView.findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
                return convertView;
            }

            ((TextView) convertView.findViewById(R.id.lottery_history_issue)).setText(data.get(position).getIssueNo());
            TextView profitTv = (TextView) convertView.findViewById(R.id.lottery_history_profit);
         //   if (data.get(position).getProfit() != null)
            if(data.get(position).getProfit() == null){
                profitTv.setText("未投注");
            }else{
                profitTv.setText(data.get(position).getProfit());
            }
         //   else {
           //     profitTv.setText("未投注");
            //    profitTv.setTextColor(Color.parseColor("#486786"));
         //   }

            ViewGroup ll = (ViewGroup) convertView.findViewById(R.id.lottery_history_result_ll);
            if (data.get(position).getCurrentLotteryNum() == null) {
                Log.e("Trend History", "NULL lottery num");
                return convertView;
            }
            String str[] = data.get(position).getCurrentLotteryNum().split(",");

            String yellow = "";
            if (!lotteryType.contains("PK")) {
                int[] counts = new int[str.length];
                for (int i = 0; i < str.length; i++) {
                    for (int j = 0; j < str.length; j++)
                        if (str[i].equals(str[j]))
                            counts[i]++;
                    if (counts[i] >= 3)
                        yellow = str[i];
                }
            }

            View number;

            for (int i = 0; i < str.length; i++) {
                number = inflater.inflate(R.layout.result_solid_circle, ll, false);
                //    number.setId()
                if (!lotteryType.contains("PK")) {
                    ((TextView) number.findViewById(R.id.result_number)).setText(Integer.toString(Integer.parseInt(str[i])));

                    TextView resultNum = (TextView) number.findViewById(R.id.result_number);
                    ImageView img = (ImageView) number.findViewById(R.id.result_num_bg);

                    resultNum.setTypeface(null, Typeface.NORMAL);
                    resultNum.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    if (str[i].equals(yellow)) {
                        ((TextView) number.findViewById(R.id.result_number)).setTextColor(Color.parseColor("#F2D12A"));
                        img.setImageResource(R.mipmap.icon_yellow_result);
                    } else {
                        ((TextView) number.findViewById(R.id.result_number)).setTextColor(Color.parseColor("#079E99"));
                        img.setImageResource(R.mipmap.icon_blue_result);
                    }
                    img.getLayoutParams().width = DisplayUtil.getPxByDp(getBaseContext(), 22);
                    img.getLayoutParams().height = DisplayUtil.getPxByDp(getBaseContext(), 22);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams((int)(22 * mDensity), (int)(22*mDensity));
                //    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                    lp.setMargins(0, 0, (int) (6 * mDensity), 0);

                    number.setLayoutParams(lp);
                }
                else {
                    ImageView img = (ImageView) number.findViewById(R.id.result_num_bg);
                    try {
                        img.setImageResource(LotteryUtil.getPK10NumberImage(Integer.parseInt(str[i])));
                    } catch (Exception e) {
                        Log.i("Beijing error", data.get(position).getCurrentLotteryNum());
                        return convertView;
                    }
                    img.getLayoutParams().width = (int) (16 * mDensity);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams((int)(16 * mDensity), (int)(16*mDensity));
                    lp.setMargins(0, 30, 0, 0);
                    number.setLayoutParams(lp);
                    ((TextView) number.findViewById(R.id.result_number)).setText("");
                }
                ll.addView(number);
            }
            return convertView;
        }


    }

    View addTab(String text) {
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View tab = inflater.inflate(R.layout.custom_tab_view, tabLayout, false);
        TextView text1 = (TextView)tab.findViewById(R.id.text1);
        text1.setText(text);
        tabLayout.addView(tab);
        return tab;
    }
}
