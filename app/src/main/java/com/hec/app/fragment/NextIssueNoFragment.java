package com.hec.app.fragment;


import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonParseException;
import com.hec.app.R;
import com.hec.app.activity.base.BaseApp;
import com.hec.app.entity.BizException;
import com.hec.app.entity.LotteryDrawResultInfo;
import com.hec.app.entity.LotteryInfo;
import com.hec.app.entity.NextIssueInfoNew;
import com.hec.app.entity.ServerTimeResponseinfo;
import com.hec.app.framework.widget.MyToast;
import com.hec.app.lottery.LotteryConfig;
import com.hec.app.util.DateUtil;
import com.hec.app.util.DialogUtil;
import com.hec.app.util.DisplayUtil;
import com.hec.app.util.LotteryUtil;
import com.hec.app.util.MyAsyncTask;
import com.hec.app.util.TestUtil;
import com.hec.app.webservice.HomeService;
import com.hec.app.webservice.LotteryService;
import com.hec.app.webservice.ServiceException;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class NextIssueNoFragment extends Fragment {
    private static final String ARG_LOTTERY_ID = "lotteryID";
    private OnCurrentIssueNoCompletedListener listener;
    private MyCount countDown;
    private int lotteryID;
    private LotteryInfo lotteryInfo;
    private Boolean mIsError;
    private String currentIssueNo = "";
    private String preIssueNo = "";
    private Handler mHandler;
    private AlertDialog dialog;
    private TextView tvMinute1;
    private TextView tvMinute2;
    private TextView tvSeconds1;
    private TextView tvSeconds2;
    private LinearLayout ll_LotteryResult;
    private TextView currentIssueNoTv;//, preIssueNoTv;

    private SharedPreferences currentTime;
    private long nextIssueTime = 0;
    private View view;
    private LotteryHistoryFragment lotteryHistoryFragment;
    private long serverTime_Diff = 0;
    private LinearLayout nextIssueLayout;
    private AVLoadingIndicatorView loadingView;
    private boolean isFinish = false;


    //MyAsyncTask<LotteryDrawResultInfo> task;
    MyAsyncTask<NextIssueInfoNew> task;
    private Handler handler2 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            getNextIssueNo(false);
        }
    };

    public static NextIssueNoFragment newInstance(int lotteryID) {
        NextIssueNoFragment fragment = new NextIssueNoFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_LOTTERY_ID, lotteryID);
        fragment.setArguments(args);
        return fragment;
    }

    public void getHistory(LotteryHistoryFragment lotteryHistoryFragment) {
        this.lotteryHistoryFragment = lotteryHistoryFragment;
    }

    public NextIssueNoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            lotteryID = getArguments().getInt(ARG_LOTTERY_ID);
            try {
                lotteryInfo = new LotteryService().getLotteryInfo(lotteryID, LotteryConfig.PLAY_MODE.CLASSIC);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_next_issue_no, container, false);
        currentTime = getActivity().getSharedPreferences(String.valueOf(lotteryID), Context.MODE_PRIVATE);
        nextIssueTime = currentTime.getLong("currentlotterytime", 0);
        LinearLayout nextIssueTime = (LinearLayout) view.findViewById(R.id.nextIssueTime);
        tvMinute1 = (TextView) view.findViewById(R.id.tvMinute1);
        tvMinute2 = (TextView) view.findViewById(R.id.tvMinute2);
        tvSeconds1 = (TextView) view.findViewById(R.id.tvSeconds1);
        tvSeconds2 = (TextView) view.findViewById(R.id.tvSeconds2);
        if (lotteryInfo != null) {
            if (lotteryInfo.getLotteryType().contains("秒秒彩")) {
                nextIssueTime.setVisibility(View.GONE);
            }
        }
        //ll_LotteryResult = (LinearLayout) view.findViewById(R.id.ll_LotteryResult);
        dialog = DialogUtil.getAlertDialog(getActivity(), "温馨提示", "", "确定", null, "", null);

        currentIssueNoTv = (TextView) view.findViewById(R.id.currentIssueNoTv);
//        preIssueNoTv = (TextView) view.findViewById(R.id.preIssueNoTv);

        nextIssueLayout = (LinearLayout) view.findViewById(R.id.nextissue);
        loadingView = (AVLoadingIndicatorView) view.findViewById(R.id.custom_loading);
        nextIssueLayout.setVisibility(View.INVISIBLE);
        loadingView.setVisibility(View.VISIBLE);
        loadingView.show();

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setHandler();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof OnCurrentIssueNoCompletedListener) {
            listener = (OnCurrentIssueNoCompletedListener) activity;
        } else {
            throw new IllegalArgumentException("activity must implements FragmentInteraction");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ll_LotteryResult = (LinearLayout) view.findViewById(R.id.ll_LotteryResult);
        ll_LotteryResult.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        getServerTime();
    }

    @Override
    public void onPause() {
        TestUtil.print("next issue no onPause");
        super.onPause();
        if (countDown != null) {
            countDown.stop();
        }

        if (task != null) {
            task.cancel(true);
            TestUtil.print("task cancel " + task.isCancelled());
            task = null;
        }
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    private void getNextIssueNo(final boolean first) {
        if (task != null)
            return;
        mIsError = false;
        task = new MyAsyncTask<NextIssueInfoNew>(getActivity()) {

            @Override
            public NextIssueInfoNew callService() throws IOException,
                    JsonParseException, BizException, ServiceException {
                return new LotteryService().getSingleCommonNextIssue(lotteryID);
            }

            @Override
            public void onLoaded(NextIssueInfoNew result) throws Exception {
                if (getActivity() == null || getActivity().isFinishing())
                    return;
                task.cancel(false);
                task = null;
                if (!mIsError) {
                    Log.i("start", "result " + result.getLotteryType());

                    if (first && lotteryHistoryFragment != null) {
                        lotteryHistoryFragment.getNewHistory(result.getCurrentIssueNo(), result.getLatesttIssueNo());
                    }
                    if (result.getCurrentLotteryTime() == null || "".equals(result.getCurrentLotteryTime())) {
                        MyToast.show(getActivity(), "开奖程序出现小问题，请返回上一页后重试。");
                        return;
                    }
                    long diff = 0;
                    long currentlotterytime = Long.parseLong(result.getCurrentLotteryTime().substring(6, 19));
                    long nextLotteryTime = 0;
                    if (result.getNextLotteryTime() != null)
                        nextLotteryTime = Long.parseLong(result.getNextLotteryTime().substring(6, 19));
                    if (System.currentTimeMillis() - serverTime_Diff < currentlotterytime) {
                        diff = currentlotterytime - System.currentTimeMillis() + serverTime_Diff;
                        currentIssueNo = result.getCurrentIssueNo();
                        preIssueNo = result.getLatesttIssueNo();
                    } else if (System.currentTimeMillis() - serverTime_Diff >= currentlotterytime
                            && System.currentTimeMillis() - serverTime_Diff < nextLotteryTime) {
                        diff = nextLotteryTime - System.currentTimeMillis() + serverTime_Diff;
                        if (result.getNextIssueNo() != null) {
                            currentIssueNo = result.getNextIssueNo();
                        }
                        if (result.getCurrentIssueNo() != null) {
                            preIssueNo = result.getLatesttIssueNo();
                        }
                    } else if (System.currentTimeMillis() - serverTime_Diff > nextLotteryTime
                            && System.currentTimeMillis() - serverTime_Diff > currentlotterytime) {
                        //MyToast.show(getActivity(),"开奖程序略有延迟,请稍等！");
                        handler2.sendEmptyMessageDelayed(0, 1000);
                        return;
                    }
                    loadingView.hide();
                    loadingView.setVisibility(View.GONE);
                    nextIssueLayout.setVisibility(View.VISIBLE);
                    currentIssueNoTv.setText("第" + currentIssueNo + "期");
                    //preIssueNoTv.setText("第" + preIssueNo + "期");
                    if (countDown != null) {
                        countDown.stop();
                        countDown = null;
                    }
                    countDown = new MyCount(diff, 1000);
                    countDown.start();
                    if (!"".equals(result.getLatesttLotteryNum())
                            && System.currentTimeMillis() - serverTime_Diff < currentlotterytime) {
                        showLotteryResult(result.getLatesttLotteryNum(), result.getLotteryType());
                        SharedPreferences.Editor editor = currentTime.edit();
                        editor.putString("currentissueno", result.getCurrentIssueNo());
                        editor.putString("latestlotterynum", result.getLatesttLotteryNum());
                        editor.putString("latestissueno", result.getLatesttIssueNo());
                        editor.putString("lotterytype", result.getLotteryType());
                        editor.putLong("currentlotterytime", Long.parseLong(result.getCurrentLotteryTime().substring(6, 19)));
                        editor.commit();
                        if (!first) {
                            //广播通知History更新最新开奖结果
                            Intent intent = new Intent(BaseApp.COUNTDOWN_ACTION);
                            intent.putExtra("PreIssueNo", result.getLatesttIssueNo());
                            intent.putExtra("IsLottery", true);
                            LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
                        }
                    } else {
                        ll_LotteryResult.removeAllViews();
                        TextView t = new TextView(getActivity());
                        t.setText("开奖中...");
                        t.setTextAppearance(getActivity(), R.style.Lottery_Head_Result_Loading_Style);
                        ll_LotteryResult.addView(t);
                        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)t.getLayoutParams();
                        lp.leftMargin = 30;
                        t.setLayoutParams(lp);
                        Message message = new Message();
                        message.what = 1;

                        if (lotteryID == LotteryConfig.LOTTERY_ID.GERMANY_PK10 || lotteryID == LotteryConfig.LOTTERY_ID.ITALY_PK10 ||
                                lotteryID == LotteryConfig.LOTTERY_ID.ITALY_REAMTIME || lotteryID == LotteryConfig.LOTTERY_ID.QQ_REAMTIME ||
                                lotteryID == LotteryConfig.LOTTERY_ID.HS_REALTIME || lotteryID == LotteryConfig.LOTTERY_ID.HS_SELECT_FIVE ||
                                lotteryID == LotteryConfig.LOTTERY_ID.HS_SF_REAMTIME || lotteryID == LotteryConfig.LOTTERY_ID.HS_PK10) {
                            mHandler.sendMessageDelayed(message, 5000);
                        } else {
                            mHandler.sendMessageDelayed(message, 10000);
                        }
                    }
                } else {
                    BaseApp.changeUrl(getActivity(), new BaseApp.OnChangeUrlListener() {
                        @Override
                        public void changeSuccess() {
                            getNextIssueNo(first);
                        }

                        @Override
                        public void changeFail() {
                        }
                    });
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

    private void showLotteryResult(String result, String lotterytype) {
        List<String> list = Arrays.asList(result.split(","));
        Context context = getActivity();
        Log.i("wxj", "next in PK out " + ll_LotteryResult.getWidth());
        ll_LotteryResult.removeAllViews();
        for (String s : list) {
            if (lotterytype != null) {
                if (lotterytype.contains("PK")) {
                    Log.i("wxj", "next in PK 10 " + s);
                    ImageView imageView = new ImageView(context);
                    imageView.setImageResource(LotteryUtil.getPK10NumberImage(Integer.parseInt(s)));
                    int dp1 = DisplayUtil.getPxByDp(context, 1);
                    //ll_LotteryResult.getWidth();
                    Log.i("wxj", "next in PK lala " + ll_LotteryResult.getWidth());
                    LinearLayout.LayoutParams p = new LinearLayout.LayoutParams
                            (0, LinearLayout.LayoutParams.WRAP_CONTENT);
                    p.weight = 1;
                    p.setMargins(0, 0, dp1, 0);
                    ll_LotteryResult.addView(imageView, p);
                } else {
                    TextView t = new TextView(context);
                    t.setText(String.valueOf(Integer.parseInt(s)));
                    //t.setTextColor(getResources().getColor(R.color.transparent));
                    int dp2 = DisplayUtil.getPxByDp(context, 2);
                    Log.i("wxj", "next in PK haha " + ll_LotteryResult.getWidth());
                    LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
                    p.weight = 1;
                    p.setMargins(dp2, 0, 0, 0);
                    t.setGravity(Gravity.CENTER);
//                    t.setLayoutParams(p);
                    t.setTextAppearance(context, R.style.Lottery_Head_Result_Style);
                    t.setBackgroundResource(R.mipmap.lottery_result_bg);
                    ll_LotteryResult.addView(t, p);

                }
            }
        }
    }

    private void setHandler() {
        //mHandler = new MyHandler(this);
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg != null) {
                    Log.i("wxj", "latesttime!!!!");
                    getNextIssueNo(false);
                }
            }
        };
    }

    public String getCurrentIssueNo() {
        return currentIssueNo;
    }

    public class MyCount {
        private long millisInFuture;
        private long countDownInterval = 1000;
        private ScheduledExecutorService scheduledThreadPool;

        public MyCount(long millisInFuture, long countDownInterval) {
            this.millisInFuture = millisInFuture;
            this.countDownInterval  =countDownInterval;
        }

        public void stop() {
            if (scheduledThreadPool != null) {
                scheduledThreadPool.shutdownNow();
            }
        }

        public void start() {
            if (scheduledThreadPool != null) {
                scheduledThreadPool.shutdownNow();
                scheduledThreadPool = null;
            }
            scheduledThreadPool = Executors.newSingleThreadScheduledExecutor();
            scheduledThreadPool.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            millisInFuture -= countDownInterval;
                            if (millisInFuture <= 0) {
                                millisInFuture = 0;
                                onFinish();
                                stop();
                            } else {
                                onTick(millisInFuture);
                            }
                        }
                    });

                }
            }, 0, countDownInterval, TimeUnit.MILLISECONDS);
        }

        public void onTick(long millisUntilFinished) {
            String leftTime = DateUtil.formatTime(millisUntilFinished / 1000, true);
            List<String> list = Arrays.asList(leftTime.split(":"));
            List<String> minutes = Arrays.asList(list.get(0).split(""));
            List<String> seconds = Arrays.asList(list.get(1).split(""));
            tvMinute1.setText(minutes.get(1));
            tvMinute2.setText(minutes.get(2));
            tvSeconds1.setText(seconds.get(1));
            tvSeconds2.setText(seconds.get(2));
        }

        public void onFinish() {
            //广播通知History显示Loading
            Intent intent = new Intent(BaseApp.COUNTDOWN_ACTION);
            intent.putExtra("PreIssueNo", currentIssueNo);
            LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
            Log.i("store", "before finsih");
            handler2.sendEmptyMessageDelayed(0, 1000);
            //getNextIssueNo(false);
            //dialog.setMessage("第" + currentIssueNo + "期已截止");
            loadingView.show();
            loadingView.setVisibility(View.VISIBLE);
            nextIssueLayout.setVisibility(View.INVISIBLE);
            if (isAppOnForeground(getActivity()) && isForeground(getActivity(), getActivity().getClass().getName()) && lotteryID != 19 && lotteryID != 20) {
                //dialog.show();
                if (!MyToast.isShowing()) {
                    MyToast.show(getActivity(), "第" + currentIssueNo + "期已截止");
                }
            }
        }

        private boolean isForeground(Context context, String className) {
            if (context == null || TextUtils.isEmpty(className)) {
                return false;
            }

            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1);
            if (list != null && list.size() > 0) {
                ComponentName cpn = list.get(0).topActivity;
                if (className.equals(cpn.getClassName())) {
                    return true;
                }
            }

            return false;
        }

        public boolean isAppOnForeground(Context context) {
            if (context == null) {
                return false;
            }
            ActivityManager activityManager = (ActivityManager) context.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
            String packageName = context.getPackageName();

            List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                    .getRunningAppProcesses();
            if (appProcesses == null)
                return false;

            for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
                // The name of the process that this object is associated with.
                if (appProcess.processName.equals(packageName)
                        && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    return true;
                }
            }
            return false;
        }
    }

    public interface OnCurrentIssueNoCompletedListener {
        void onCompleted();
    }

    private void getMMCIssue() {
        mIsError = false;
        MyAsyncTask<LotteryDrawResultInfo> task = new MyAsyncTask<LotteryDrawResultInfo>(getActivity()) {
            @Override
            public LotteryDrawResultInfo callService() throws IOException, JsonParseException, BizException, ServiceException {
                return new LotteryService().getNextIssueNo(lotteryID, LotteryConfig.PLAY_MODE.CLASSIC);
            }

            @Override
            public void onLoaded(LotteryDrawResultInfo paramT) throws Exception {
                if (!mIsError) {
                    loadingView.hide();
                    loadingView.setVisibility(View.GONE);
                    nextIssueLayout.setVisibility(View.VISIBLE);
                    currentIssueNo = String.valueOf(Long.parseLong(paramT.getCurrentTime().getIssueNo()));
                    currentIssueNoTv.setText("第" + paramT.getCurrentTime().getIssueNo() + "期");
                    preIssueNo = paramT.getLatestTime().getIssueNo();
                    //preIssueNoTv.setText("第" + paramT.getLatestTime().getIssueNo() + "期");
                    showLotteryResult(paramT.getLatestTime().getCurrentLotteryNum(), paramT.getLotteryTypeName());
                } else {
                    BaseApp.changeUrl(getActivity(), new BaseApp.OnChangeUrlListener() {
                        @Override
                        public void changeSuccess() {
                            getMMCIssue();
                        }

                        @Override
                        public void changeFail() {
                        }
                    });
                }
            }
        };
        task.setOnError(new MyAsyncTask.OnError() {
            @Override
            public void handleError(Exception paramException) {
                mIsError = true;
            }
        });
        task.executeTask();
    }

    private void getServerTime() {
        mIsError = false;
        MyAsyncTask<ServerTimeResponseinfo> task = new MyAsyncTask<ServerTimeResponseinfo>(getActivity()) {
            @Override
            public ServerTimeResponseinfo callService() throws IOException, JsonParseException, BizException, ServiceException {
                return new HomeService().getServerTime();
            }

            @Override
            public void onLoaded(ServerTimeResponseinfo paramT) throws Exception {
                if (!mIsError) {
                    if (paramT.isSuccess()) {
                        serverTime_Diff = System.currentTimeMillis() - Long.parseLong(paramT.getMessage().substring(6, 19));
                        if (System.currentTimeMillis() - serverTime_Diff >= nextIssueTime) {
                            //we should get update data from server.
                            if (lotteryID == 19 || lotteryID == 20) {
                                getMMCIssue();
                            } else {
                                getNextIssueNo(true);
                            }
                        } else {
                            loadingView.hide();
                            loadingView.setVisibility(View.GONE);
                            nextIssueLayout.setVisibility(View.VISIBLE);
                            currentIssueNo = currentTime.getString("currentissueno", "");
                            preIssueNo = currentTime.getString("latestissueno", "");
                            currentIssueNoTv.setText("第" + currentIssueNo + "期");
                            // preIssueNoTv.setText("第" + preIssueNo + "期");
                            if (lotteryHistoryFragment != null) {
                                lotteryHistoryFragment.getNewHistory(currentIssueNo, preIssueNo);
                            }
                            long diff = nextIssueTime - System.currentTimeMillis() + serverTime_Diff;
                            if (countDown != null) {
                                countDown.stop();
                                countDown = null;
                            }
                            countDown = new MyCount(diff, 1000);
                            countDown.start();
                            showLotteryResult(currentTime.getString("latestlotterynum", "")
                                    , currentTime.getString("lotterytype", ""));
                        }
                        TestUtil.print("current issue no is null " + (currentIssueNo == null));
                    } else {
                        Log.i("patch", "3333");
                        getServerTime();
                    }
                } else {
                    BaseApp.changeUrl(getActivity(), new BaseApp.OnChangeUrlListener() {
                        @Override
                        public void changeSuccess() {
                            getServerTime();
                        }

                        @Override
                        public void changeFail() {
                        }
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

//    public class ServerTimeResponseinfo{
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    //    public class ServerTimeResponseinfo{
//        private boolean success;
//        private String message;
//
//        public boolean isSuccess() {
//            return success;
//        }
//
//        public void setSuccess(boolean success) {
//            this.success = success;
//        }
//
//        public String getMessage() {
//            return message;
//        }
//
//        public void setMessage(String message) {
//            this.message = message;
//        }
//    }
}
