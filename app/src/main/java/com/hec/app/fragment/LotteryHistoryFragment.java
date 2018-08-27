package com.hec.app.fragment;


import android.app.ListFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonParseException;
import com.hec.app.R;
import com.hec.app.activity.LoginActivity;
import com.hec.app.activity.base.BaseApp;
import com.hec.app.entity.BizException;
import com.hec.app.entity.CurrentLotteryInfo;
import com.hec.app.entity.LotteryDrawResultInfo;
import com.hec.app.entity.LotteryInfo;
import com.hec.app.entity.TrendHistoryInfo;
import com.hec.app.framework.adapter.CommonAdapter;
import com.hec.app.framework.adapter.ViewHolder;
import com.hec.app.framework.widget.MyToast;
import com.hec.app.lottery.LotteryConfig;
import com.hec.app.util.DisplayUtil;
import com.hec.app.util.IntentUtil;
import com.hec.app.util.LotteryUtil;
import com.hec.app.util.MyAsyncTask;
import com.hec.app.util.TestUtil;
import com.hec.app.webservice.LotteryService;
import com.hec.app.webservice.ServiceException;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.zip.Inflater;

public class LotteryHistoryFragment extends ListFragment {
    private static final String ARG_LOTTERY_ID = "lotteryID";

    private int lotteryID;
    private LotteryInfo lotteryInfo;
    private Boolean mIsError;
    private LocalBroadcastManager broadcastManager;
    private List<TrendHistoryInfo> results;
    private String preIssueNo;
    private float mDensity;
    //private SharedPreferences tenIssue;

    public static LotteryHistoryFragment newInstance(int lotteryID) {
        LotteryHistoryFragment fragment = new LotteryHistoryFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_LOTTERY_ID, lotteryID);
        fragment.setArguments(args);
        return fragment;
    }

    public LotteryHistoryFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            lotteryID = getArguments().getInt(ARG_LOTTERY_ID);
            Log.i("wxj","controller " + lotteryID);
            try {
                lotteryInfo = new LotteryService().getLotteryInfo(lotteryID, LotteryConfig.PLAY_MODE.CLASSIC);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(new LotteryService().getTrendFromLocal(lotteryID) != null){
            results = new LotteryService().getTrendFromLocal(lotteryID);
            bindData(results);
        }else{
            getLatestLotteryResult();
        }
        broadcastManager = LocalBroadcastManager.getInstance(getActivity());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BaseApp.COUNTDOWN_ACTION);
        broadcastManager.registerReceiver(countDownReceiver, intentFilter);
        mDensity = getResources().getDisplayMetrics().density;
    }

    BroadcastReceiver countDownReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (results != null && results.size() > 0) {
                TrendHistoryInfo last = new TrendHistoryInfo();
                preIssueNo = intent.getStringExtra("PreIssueNo");
                boolean isLottery = intent.getBooleanExtra("IsLottery", false);
                if (!isLottery) {
                    if(!results.get(0).getIssueNo().equals(preIssueNo)) {
                        last.setCurrentLotteryNum("开奖中...");
                        last.setIssueNo(preIssueNo);
                        int lastIndex = results.size();
                        results.add(0, last);
                        results.remove(lastIndex);
                        bindData(results);
                    }
                } else {
                    getLatestLotteryResult();
                }
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lottery_history, container, false);
        return view;
    }

    public void getLatestLotteryResult() {
        mIsError = false;
        final LotteryService lotteryService = new LotteryService();
        lotteryService.setDefaultSize(10);
        MyAsyncTask<List<TrendHistoryInfo>> task = new MyAsyncTask<List<TrendHistoryInfo>>(getActivity()) {

            @Override
            public List<TrendHistoryInfo> callService() throws IOException,
                    JsonParseException, BizException, ServiceException {
                return lotteryService.GetTrendHistory(lotteryID, 1, 0, LotteryConfig.PLAY_MODE.CLASSIC);
            }

            @Override
            public void onLoaded(List<TrendHistoryInfo> result) throws Exception {
                if(getActivity() == null || getActivity().isFinishing())
                    return;
                if (!mIsError) {
                    Log.e("TrendHistoryInfo", "1:" + result.get(0).getIssueNo() + ",2:" + result.get(1).getIssueNo() + ",3:" + result.get(2).getIssueNo());
                    results = result;
                    bindData(result);
                }else{

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
    private void showLotteryResult(String result, LinearLayout ll_LotteryResult) {
        ll_LotteryResult.removeAllViews();
        if(result==null){
            return;
        }
        Context context = getActivity();
        if (result.equals("开奖中...")) {
            ImageView loading = new ImageView(context);
            loading.setImageResource(R.mipmap.lottery_loading_result);
            TextView t = new TextView(context);
            t.setTextAppearance(context, R.style.Lottery_History_Result_Text_Style);
            t.setText(result);
            int dp10 = DisplayUtil.getPxByDp(context, 10);
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            p.setMargins(dp10, 0, 0, 0);
            ll_LotteryResult.addView(loading);
            ll_LotteryResult.addView(t, p);
        } else {
            List<String> list = Arrays.asList(result.split(","));
            String num = findSanSiyin(list);
            for (String s : list) {
                if (lotteryInfo.getLotteryType().contains("PK")) {
                    ImageView imageView = new ImageView(context);
                    imageView.setImageResource(LotteryUtil.getPK10NumberImage(Integer.parseInt(s)));
                    int dp3 = DisplayUtil.getPxByDp(context, 1);
                    LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    p.setMargins(0, 0, dp3, 0);
                    ll_LotteryResult.addView(imageView, p);
                } else {
                    View number = LayoutInflater.from(context).inflate(R.layout.result_solid_circle, null, false);
                    TextView resultNum = (TextView) number.findViewById(R.id.result_number);
                    ImageView img = (ImageView) number.findViewById(R.id.result_num_bg);
                    resultNum.setText(String.valueOf(Integer.parseInt(s)));
                    resultNum.setTypeface(null, Typeface.NORMAL);
                    resultNum.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    if (num != null && num.equals(s)) {
                        img.setImageResource(R.mipmap.icon_yellow_result);
                        resultNum.setTextColor(Color.parseColor("#F2D12A"));
                    } else {
                        resultNum.setTextColor(Color.parseColor("#079E99"));
                        img.setImageResource(R.mipmap.lottery_result_bg);
                    }
                    img.getLayoutParams().width = DisplayUtil.getPxByDp(context, 22);
                    img.getLayoutParams().height = DisplayUtil.getPxByDp(context, 22);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams((int)(22 * mDensity), (int)(22*mDensity));
                    lp.setMargins(0, 0, (int) (6 * mDensity), 0);
                    number.setLayoutParams(lp);
                    ll_LotteryResult.addView(number);
                }
            }
        }
    }

    private String findSanSiyin(final List<String> list) {
        int count;
        for (int i = 0; i < list.size() - 1; i++) {
            count = 0;
            for (int j = i + 1; j < list.size(); j++) {
                if (list.get(i).equals(list.get(j))) {
                    count++;
                    if (count >= 2) {
                        return list.get(i);
                    }
                }
            }
        }
        return null;
    }

    private void bindData(List<TrendHistoryInfo> list) {
        if(getActivity() == null)
            return;
        /*if (preIssueNo != null) {
            for (TrendHistoryInfo info : list) {
                if (info.getIssueNo().equals(preIssueNo)) {
                    list.remove(info);
                    break;
                }
            }
        }*/
        CommonAdapter<TrendHistoryInfo> adapter = new CommonAdapter<TrendHistoryInfo>(getActivity(), list, R.layout.list_item_lottery_result) {
            @Override
            public void convert(ViewHolder helper, TrendHistoryInfo item, int position) {
                LinearLayout container = helper.getView(R.id.ll_LotteryResult);
                if(item != null){
                    showLotteryResult(item.getCurrentLotteryNum(), container);
                }
                helper.setText(R.id.tvIssueNo, item.getIssueNo());
                if(item.getProfit() == null){
                    helper.setText(R.id.tvprofit, "未投注");
                }else{
                    String s = item.getProfit();
                    if(s.contains(".")&&s.length()>3){
//                        String[] ss = s.split("\\.");
//                        s = ss[0]+"."+ss[1].substring(0,1);
                        s = s.substring(0,s.length()-3);
                    }
                    helper.setText(R.id.tvprofit, s);
                }
            }
        };
        setListAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        super.onPause();
        broadcastManager.unregisterReceiver(countDownReceiver);
    }

    public void getNewHistory(String currentLotteryNo, String latesttIssueNo){
        preIssueNo = latesttIssueNo;
        List<TrendHistoryInfo> list = new LotteryService().getTrendFromLocal(lotteryID);
        if(list!= null && list.size() != 0){
            if(Long.parseLong(currentLotteryNo) - 1
                    > Long.parseLong(new LotteryService().getTrendFromLocal(lotteryID).get(0).getIssueNo())){
                getLatestLotteryResult();
            } else {
                bindData(list);
            }
        }else{
            getLatestLotteryResult();
        }
    }
}
