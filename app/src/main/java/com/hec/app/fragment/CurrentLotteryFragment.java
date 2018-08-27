package com.hec.app.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonParseException;
import com.hec.app.R;
import com.hec.app.activity.TrendHistoryActivity;
import com.hec.app.activity.base.BaseApp;
import com.hec.app.config.CommonConfig;
import com.hec.app.entity.BizException;
import com.hec.app.entity.LotteryDrawResultInfo;
import com.hec.app.entity.NextIssueInfoNew;
import com.hec.app.framework.widget.MyToast;
import com.hec.app.lottery.LotteryConfig;
import com.hec.app.util.DialogUtil;
import com.hec.app.util.LotteryUtil;
import com.hec.app.util.MyAsyncTask;
import com.hec.app.webservice.LotteryService;
import com.hec.app.webservice.ServiceException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class CurrentLotteryFragment extends Fragment {
    private final int INITIALIZE_FINISH = 0;
    private final int ON_ERROR          = 1;

    private ExpandableListView mExpandableListView;
    private ProgressDialog mProgressDialog;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private LotteryInfoAdapter mAdapter;
    private List<NextIssueInfoNew> mNextIssueInfoNews = new ArrayList<>();
    private List<String> mGroup;
    private List<List<NextIssueInfoNew>> mChild;
    private List<List<Integer>> mLstId;
    private List<List<Boolean>> mListIsError;
    private String mMessage = "";
    private boolean mIsError = false;
    private boolean mIsShowing = false;
    private float mDensity;
    private int mCounts = 0;
    private ScheduledExecutorService scheduledThreadPool;
    private boolean isPollingRefresh = true;


    public CurrentLotteryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDensity = getContext().getResources().getDisplayMetrics().density;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_current_lottery, container, false);
        mExpandableListView = (ExpandableListView) view.findViewById(R.id.current_lottery_lv);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mNextIssueInfoNews.clear();
        mCounts = 0;
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mCounts = 0;
                getResultFromServer();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        scheduledThreadPool = Executors.newSingleThreadScheduledExecutor();
        scheduledThreadPool.scheduleAtFixedRate(refreshIssueRunnable, 0, 10000, TimeUnit.MILLISECONDS);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onPause() {
        super.onPause();
        isPollingRefresh = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isHidden()) {
            isPollingRefresh = true;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (scheduledThreadPool != null) {
            scheduledThreadPool.shutdownNow();
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            mNextIssueInfoNews.clear();
            mCounts = 0;
            getResultFromServer();
            isPollingRefresh = true;
        } else {
            isPollingRefresh = false;
        }
    }

    private Runnable refreshIssueRunnable = new Runnable() {
        @Override
        public void run() {
            if (!isPollingRefresh) {
                return;
            }
            getResultFromServer();
//            getActivity().runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//
//                }
//            });

        }
    };

    private void startTrendHistory (int groupPosition, int childPosition) {
        ArrayList<Integer> idList = new ArrayList<>();
        for (List<Integer> id : mLstId) {
            idList.addAll(id);
        }

        ArrayList<String> nameList = new ArrayList<>();
        for (List<NextIssueInfoNew> child : mChild) {
            for (NextIssueInfoNew info : child) {
                nameList.add(info.getLotteryType());
            }
        }

        Intent intent = new Intent(getActivity(), TrendHistoryActivity.class);
        intent.putExtra(CommonConfig.INTENT_TREND_HISTORY_ID, mLstId.get(groupPosition).get(childPosition));
        intent.putExtra(CommonConfig.INTENT_TREND_HISTORY_TYPE, mChild.get(groupPosition).get(childPosition).getLotteryType());
        intent.putIntegerArrayListExtra(CommonConfig.INTENT_TREND_HISTORY_IDLIST, idList);
        intent.putStringArrayListExtra(CommonConfig.INTENT_TREND_HISTORY_NAMELIST, nameList);
        startActivity(intent);
    }

    private void initializeData() {
        if (mNextIssueInfoNews == null) {
            return;
        }

        mGroup = new ArrayList<>();
        mChild = new ArrayList<>();
        mLstId = new ArrayList<>();
        mListIsError = new ArrayList<>();

        List<NextIssueInfoNew> realtimeList = new ArrayList<>();
        List<NextIssueInfoNew> kuaiSanList = new ArrayList<>();
        List<NextIssueInfoNew> pk10List = new ArrayList<>();
        List<NextIssueInfoNew> hsList = new ArrayList<>();
        List<NextIssueInfoNew> selectFiveList = new ArrayList<>();
        List<NextIssueInfoNew> lowFrequeceList = new ArrayList<>();
        List<NextIssueInfoNew> mmcList = new ArrayList<>();

        addInfo(getResources().getString(R.string.title_pk10), pk10List);
        addInfo(getResources().getString(R.string.title_realtime), realtimeList);
        addInfo(getResources().getString(R.string.title_kuaiSan), kuaiSanList);
        addInfo(getResources().getString(R.string.title_hs), hsList);
        addInfo(getResources().getString(R.string.title_select_five), selectFiveList);
        addInfo(getResources().getString(R.string.title_low_frequece), lowFrequeceList);
        addInfo(getResources().getString(R.string.title_mmc), mmcList);

        for (int i = 0; i < mChild.size(); ++i) {
            mLstId.add(new ArrayList<Integer>());
            mListIsError.add(new ArrayList<Boolean>());
        }

        mIsShowing = false;
        try {
            for (NextIssueInfoNew info : mNextIssueInfoNews) {
                boolean isNotFindLottery = false;
                int index = 0;

                BaseApp.getHecReplaceString(info.getLotteryType());
                switch (info.getLotteryID()) {
                    case LotteryConfig.LOTTERY_ID.CHONGQING_REALTIME:
                    case LotteryConfig.LOTTERY_ID.XINJIANG_REALTIME:
                    case LotteryConfig.LOTTERY_ID.TIANJIN_REALTIME:
                    case LotteryConfig.LOTTERY_ID.TAIWAN_WF_REALTIME:
                    case LotteryConfig.LOTTERY_ID.BEIJING_KENO:
                    case LotteryConfig.LOTTERY_ID.KOREA_WF_REALTIME:
                    case LotteryConfig.LOTTERY_ID.QQ_REAMTIME:
                        realtimeList.add(info);
                        index = 1;
                        break;
                    case LotteryConfig.LOTTERY_ID.ITALY_REAMTIME:
                        realtimeList.add(0, info);
                        index = 1;
                        break;
                    case LotteryConfig.LOTTERY_ID.JIANG_SU_KUAI_SAN:
                        kuaiSanList.add(0, info);
                        index = 2;
                        break;
                    case LotteryConfig.LOTTERY_ID.BEIJING_PK10:
                    case LotteryConfig.LOTTERY_ID.GERMANY_PK10:
                    case LotteryConfig.LOTTERY_ID.ITALY_PK10:
                        pk10List.add(info);
                        index = 0;
                        break;
                    case LotteryConfig.LOTTERY_ID.HS_REALTIME:
                    case LotteryConfig.LOTTERY_ID.HS_SELECT_FIVE:
                    case LotteryConfig.LOTTERY_ID.HS_SF_REAMTIME:
                    case LotteryConfig.LOTTERY_ID.HS_PK10:
                        hsList.add(info);
                        index = 3;
                        break;
                    case LotteryConfig.LOTTERY_ID.GUANGDONG_SELECT_FIVE:
                    case LotteryConfig.LOTTERY_ID.SHANDONG_SELECT_FIVE:
                        selectFiveList.add(info);
                        index = 4;
                        break;
                    case LotteryConfig.LOTTERY_ID.WELFARE_LOTTERY_3D:
                    case LotteryConfig.LOTTERY_ID.SPORTS_LOTTERY:
                        lowFrequeceList.add(info);
                        index = 5;
                        break;
                    case LotteryConfig.LOTTERY_ID.HS_MMC:
                    case LotteryConfig.LOTTERY_ID.HS_MMC_PK10:
                        mmcList.add(info);
                        index = 6;
                        break;
                    default:
                        isNotFindLottery = true;
                        break;
                }

                if (!isNotFindLottery && info.getLotteryID() != LotteryConfig.LOTTERY_ID.ITALY_REAMTIME) {
                    mListIsError.get(index).add(true);
                    mLstId.get(index).add(info.getLotteryID());
                    insertResult(info, info.getLotteryID(), index);
                } else if (!isNotFindLottery) {
                    mListIsError.get(index).add(0, true);
                    mLstId.get(index).add(0, info.getLotteryID());
                    insertResult(info, info.getLotteryID(), index);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        removeEmptyResult();
        if (mAdapter == null) {
            mAdapter = new LotteryInfoAdapter();
            mExpandableListView.setAdapter(mAdapter);
            mExpandableListView.setCacheColorHint(0);  //设置拖动列表的时候防止出现黑色背景
        }
        for (int i = 0; i < mAdapter.getGroupCount(); i++) {
            mExpandableListView.expandGroup(i);
        }
        mExpandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return true;
            }
        });
        mExpandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                startTrendHistory(groupPosition, childPosition);
                return true;
            }
        });

        mHandler.sendEmptyMessage(INITIALIZE_FINISH);
    }

    private void insertResult(NextIssueInfoNew result, int id, int childId) {
        for (int i = 0; i < mLstId.get(childId).size(); i++) {
            if (id == mLstId.get(childId).get(i)) {
                mListIsError.get(childId).set(i, false);
                mLstId.get(childId).set(i, id);
                mChild.get(childId).set(i, result);
                return;
            }
        }
    }

    private void removeEmptyResult() {
        for (int i = 0; i < mChild.size(); ++i) {
            if (mChild.get(i).size() <= 0 && mGroup.size() > i && mLstId.size() > i && mListIsError.size() > i) {
                mGroup.remove(i);
                mChild.remove(i);
                mLstId.remove(i);
                mListIsError.remove(i);
            }
        }
    }

    private void addInfo(String group, List<NextIssueInfoNew> child) {
        mGroup.add(group);
        mChild.add(child);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage (Message msg) {
            super.handleMessage(msg);
            if (msg.what == INITIALIZE_FINISH) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.notifyDataSetChanged();
                    }
                });

//                closeProgressDialog();
            }
            else if (msg.what == ON_ERROR) {
                String msgText = msg.getData().getString("Error");
                if (!mIsShowing || !mMessage.equals(msgText)) {
                    mMessage = msgText;
                    mIsShowing = true;
                    MyToast.show(getContext(), msgText);
                }
            }
        }
    };

    class LotteryInfoAdapter extends BaseExpandableListAdapter {

        //-----------------Child----------------//
        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return mChild.get(groupPosition).get(childPosition);
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return mChild.get(groupPosition).size();
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            NextIssueInfoNew lotteryInfo;

            try {
                if (mChild != null && mChild.size() > 0) {
                    lotteryInfo = mChild.get(groupPosition).get(childPosition);
                }
                else{
                    lotteryInfo = new NextIssueInfoNew();
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                e.printStackTrace();
                return convertView;
            }

            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_current_lottery, parent, false);

            TextView tvLotteryType = (TextView) convertView.findViewById(R.id.current_lottery_type);
            tvLotteryType.setText(lotteryInfo.getLotteryType());

            if (lotteryInfo != null) {
                ViewGroup viewGroup = (ViewGroup) convertView.findViewById(R.id.current_lottery_numbers_ll);
                String lotteryNum = lotteryInfo.getLatesttLotteryNum();
                String issueNo = lotteryInfo.getLatesttIssueNo();

                if (issueNo != null) {
                    ((TextView) convertView.findViewById(R.id.current_lottery_issue_no)).setText(issueNo + "期");
                }

                if (!"".equals(lotteryNum) && lotteryNum != null) {
                    convertView.findViewById(R.id.pending_ll).setVisibility(View.GONE);
                    convertView.findViewById(R.id.current_lottery_numbers_ll).setVisibility(View.VISIBLE);
                    String str[] = lotteryNum.split(",");

                    View number;
                    for (int i = 0; i < str.length; i++) {
                        number = inflater.inflate(R.layout.result_solid_circle, viewGroup, false);

                        if (!lotteryInfo.getLotteryType().contains("PK")) {
                            ((TextView) number.findViewById(R.id.result_number)).setText(Integer.toString(Integer.parseInt(str[i])));
                        } else {
                            ImageView img = (ImageView) number.findViewById(R.id.result_num_bg);
                            img.setImageResource(LotteryUtil.getPK10NumberImage(Integer.parseInt(str[i])));
                            img.getLayoutParams().width = (int) (20 * mDensity);

                            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            layoutParams.setMargins(0, 0, (int) (4 * mDensity), 0);
                            number.setLayoutParams(layoutParams);

                            ((TextView) number.findViewById(R.id.result_number)).setText("");
                        }

                        viewGroup.addView(number);
                    }
                }
                else {
                    convertView.findViewById(R.id.pending_ll).setVisibility(View.VISIBLE);
                    convertView.findViewById(R.id.current_lottery_numbers_ll).setVisibility(View.GONE);
                }
            }
            return convertView;
        }

        //----------------Group----------------//
        @Override
        public Object getGroup ( int groupPosition) {
            return mGroup.get(groupPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public int getGroupCount() {
            return mGroup.size();
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            String string = mGroup.get(groupPosition);
            return getGroupView(string, parent);
        }

        public View getGroupView(String s, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.list_group_item_lottery, parent, false);
            TextView textView = (TextView) view.findViewById(R.id.tvGroupName);
            textView.setText(s);
            return view;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }

    private void getResultFromServer(){
        mIsError = false;

        MyAsyncTask<List<NextIssueInfoNew>> task = new MyAsyncTask<List<NextIssueInfoNew>>(getContext()) {
            @Override
            public List<NextIssueInfoNew> callService() throws IOException, JsonParseException, BizException, ServiceException {
                return new LotteryService().getCommonNextIssue();
            }

            @Override
            public void onLoaded(List<NextIssueInfoNew> paramT) throws Exception {
                if (!mIsError) {
                    mNextIssueInfoNews = paramT;
                    Log.i("wxj","nextissue size "+mNextIssueInfoNews.size());
                    //getMMCREsultFromServer(19);
                    //getMMCREsultFromServer(20);
                    initializeData();

                }
                else {
                    BaseApp.changeUrl(getActivity(), new BaseApp.OnChangeUrlListener() {
                        @Override
                        public void changeSuccess() {
                            getResultFromServer();
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

    private void getMMCREsultFromServer(final int lottery){
        mIsError = false;
        MyAsyncTask<LotteryDrawResultInfo> task = new MyAsyncTask<LotteryDrawResultInfo>(getContext()) {
            @Override
            public LotteryDrawResultInfo callService() throws IOException, JsonParseException, BizException, ServiceException {
                return new LotteryService().getNextIssueNo(lottery, LotteryConfig.PLAY_MODE.CLASSIC);
            }

            @Override
            public void onLoaded(LotteryDrawResultInfo paramT) throws Exception {
                mCounts++;
                if (!mIsError) {
                    NextIssueInfoNew infoNew = new NextIssueInfoNew();
                    infoNew.setLatesttIssueNo(paramT.getLatestTime().getIssueNo());
                    infoNew.setLatesttLotteryNum(paramT.getLatestTime().getCurrentLotteryNum());
                    infoNew.setLotteryType(paramT.getLotteryTypeName());
                    infoNew.setLotteryID(lottery);

                    mNextIssueInfoNews.add(infoNew);
                    if(mCounts == 1){
                        initializeData();
                    }
                }
                else {
                    BaseApp.changeUrl(getActivity(), new BaseApp.OnChangeUrlListener() {
                        @Override
                        public void changeSuccess() {
                            getMMCREsultFromServer(lottery);
                        }

                        @Override
                        public void changeFail() {
                            if(getErrorMessage()!=null) {
                                Bundle bundle = new Bundle();
                                bundle.putString("Error", getErrorMessage());

                                Message msg = Message.obtain();
                                msg.setData(bundle);
                                msg.what = ON_ERROR;
                                mHandler.sendMessage(msg);
                            }
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
}
