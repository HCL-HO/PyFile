package com.hec.app.fragment;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonParseException;
import com.hec.app.R;
import com.hec.app.activity.base.BaseApp;
import com.hec.app.config.CommonConfig;
import com.hec.app.entity.BasicDataInfo;
import com.hec.app.entity.BizException;
import com.hec.app.entity.LotteryInfo;
import com.hec.app.framework.widget.OnLotteryTypeClickedListener;
import com.hec.app.lottery.LotteryConfig;
import com.hec.app.util.DialogUtil;
import com.hec.app.util.LotteryUtil;
import com.hec.app.util.MyAsyncTask;
import com.hec.app.util.TestUtil;
import com.hec.app.webservice.LotteryService;
import com.hec.app.webservice.ServiceException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class AllLotteryFragment extends Fragment {
    List<String> mGroup;
    List<List<LotteryInfo>> mChild;
    LotteryInfoAdapter mAdapter;
    List<Integer> mHotLottery;
    List<Integer> mNewLottery;
    private ExpandableListView mExpandableListView;
    private OnLotteryTypeClickedListener mListener;
    private Boolean mIsError;
    private ProgressDialog mLoadingDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_lottery, container, false);
        mExpandableListView = (ExpandableListView) view.findViewById(R.id.listView_Lottery);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //20180719 新增彩種-江蘇快三,熱門彩種-義大利分分彩, 重慶時時彩
        mHotLottery = new ArrayList<>();
//        mHotLottery.add(LotteryConfig.LOTTERY_ID.CHONGQING_REALTIME);
//        mHotLottery.add(LotteryConfig.LOTTERY_ID.BAIJIALE);
        mHotLottery.add(LotteryConfig.LOTTERY_ID.BEIJING_PK10);
        mHotLottery.add(LotteryConfig.LOTTERY_ID.GERMANY_PK10);
        mHotLottery.add(LotteryConfig.LOTTERY_ID.ITALY_REAMTIME);
        mHotLottery.add(LotteryConfig.LOTTERY_ID.CHONGQING_REALTIME);
        mNewLottery = new ArrayList<>();
//        mNewLottery.add(LotteryConfig.LOTTERY_ID.CHATROOM_BJL);
//        mNewLottery.add(LotteryConfig.LOTTERY_ID.ITALY_PK10);
//        mNewLottery.add(LotteryConfig.LOTTERY_ID.ITALY_REAMTIME);
        mNewLottery.add(LotteryConfig.LOTTERY_ID.JIANG_SU_KUAI_SAN);
        mAdapter = new LotteryInfoAdapter();
        initializeData();
        mExpandableListView.setAdapter(mAdapter);
        mExpandableListView.setCacheColorHint(0);  //设置拖动列表的时候防止出现黑色背景
        for (int i = 0; i < mAdapter.getGroupCount(); i++) {
            mExpandableListView.expandGroup(i);
        }
        mExpandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                LotteryInfo lotteryInfo = mChild.get(groupPosition).get(childPosition);

                if (mListener != null && lotteryInfo != null) {
                    mListener.onClicked(lotteryInfo.getLotteryID(), lotteryInfo.getTypeUrl());
                }

                return false;
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof OnLotteryTypeClickedListener) {
            mListener = (OnLotteryTypeClickedListener) activity;
        } else {
            throw new IllegalArgumentException("activity must implements FragmentInteraction");
        }
    }

    /**
     * 初始化组、子列表数据
     */
    private void initializeData() {
        mGroup = new ArrayList<>();
        mChild = new ArrayList<>();
        List<LotteryInfo> list = new ArrayList<>();

        try {
            list = new LotteryService().getLotteryInfo(LotteryConfig.PLAY_MODE.CLASSIC);
        } catch (Exception ex) {
            if (ex.getLocalizedMessage() != null) {
                TestUtil.print(ex.getLocalizedMessage());
            }
            return;
        }

        if (list == null) {
            getBasicData();
            return;
        }

        List<LotteryInfo> slotList = new ArrayList<>();
        List<LotteryInfo> realtimeList = new ArrayList<>();
        List<LotteryInfo> pk10List = new ArrayList<>();
        List<LotteryInfo> hsList = new ArrayList<>();
        List<LotteryInfo> selectFiveList = new ArrayList<>();
        List<LotteryInfo> lowFrequeceList = new ArrayList<>();
        List<LotteryInfo> mmcList = new ArrayList<>();
        List<LotteryInfo> realmanList = new ArrayList<>();
        List<LotteryInfo> kuaiSanList = new ArrayList<>();

        LotteryInfo realMan = new LotteryInfo(LotteryConfig.LOTTERY_ID.REAL_MAN, getResources().getString(R.string.lottery_name_realman), null);
        realmanList.add(realMan);

        LotteryInfo slot = new LotteryInfo(LotteryConfig.LOTTERY_ID.SLOT, getResources().getString(R.string.lottery_name_slot), null);
        LotteryInfo bjl = new LotteryInfo(LotteryConfig.LOTTERY_ID.BAIJIALE, getResources().getString(R.string.lottery_name_bjl), null);
        slotList.add(slot);
        slotList.add(bjl);

        for (LotteryInfo info : list) {
            switch (info.getLotteryID()) {
                case LotteryConfig.LOTTERY_ID.CHONGQING_REALTIME: // 20180720 GD指示義大利分分彩放置於重慶時時彩之後
                    realtimeList.add(0, info);
                    break;
                case LotteryConfig.LOTTERY_ID.ITALY_REAMTIME:
                    realtimeList.add(1, info);
                    break;
                case LotteryConfig.LOTTERY_ID.XINJIANG_REALTIME:
                case LotteryConfig.LOTTERY_ID.TIANJIN_REALTIME:
                case LotteryConfig.LOTTERY_ID.TAIWAN_WF_REALTIME:
                case LotteryConfig.LOTTERY_ID.BEIJING_KENO:
                case LotteryConfig.LOTTERY_ID.KOREA_WF_REALTIME:
                case LotteryConfig.LOTTERY_ID.QQ_REAMTIME:
                    realtimeList.add(info);
                    break;
                case LotteryConfig.LOTTERY_ID.BEIJING_PK10:
                case LotteryConfig.LOTTERY_ID.GERMANY_PK10:
                case LotteryConfig.LOTTERY_ID.ITALY_PK10:
                    pk10List.add(info);
                    break;
                case LotteryConfig.LOTTERY_ID.HS_REALTIME:
                case LotteryConfig.LOTTERY_ID.HS_SELECT_FIVE:
                case LotteryConfig.LOTTERY_ID.HS_SF_REAMTIME:
                case LotteryConfig.LOTTERY_ID.HS_PK10:
                    hsList.add(info);
                    break;
                case LotteryConfig.LOTTERY_ID.GUANGDONG_SELECT_FIVE:
                case LotteryConfig.LOTTERY_ID.SHANDONG_SELECT_FIVE:
                    selectFiveList.add(info);
                    break;
                case LotteryConfig.LOTTERY_ID.WELFARE_LOTTERY_3D:
                case LotteryConfig.LOTTERY_ID.SPORTS_LOTTERY:
                    lowFrequeceList.add(info);
                    break;
                case LotteryConfig.LOTTERY_ID.HS_MMC:
                case LotteryConfig.LOTTERY_ID.HS_MMC_PK10:
                    mmcList.add(info);
                    break;
                case LotteryConfig.LOTTERY_ID.JIANG_SU_KUAI_SAN:
                    kuaiSanList.add(info);
            }
        }

        addInfo(getString(R.string.title_slot), slotList);
        addInfo(getString(R.string.title_pk10), pk10List);
        addInfo(getString(R.string.title_realtime), realtimeList);
        addInfo(getString(R.string.title_kuaiSan), kuaiSanList);
        addInfo(getString(R.string.title_hs), hsList);
        addInfo(getString(R.string.title_select_five), selectFiveList);
        addInfo(getString(R.string.title_low_frequece), lowFrequeceList);
        if (mmcList.size() > 0) {
            addInfo(getString(R.string.title_mmc), mmcList);
        }
        addInfo(getString(R.string.title_realman), realmanList);
    }

    private void addInfo(String g, List<LotteryInfo> c) {
        mGroup.add(g);
        mChild.add(c);
    }

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
            LotteryInfo lotteryInfo = mChild.get(groupPosition).get(childPosition);
            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.list_item_lottery, parent, false);

            TextView tvLotteryType = (TextView) view.findViewById(R.id.tvLotteryType);
            tvLotteryType.setText(BaseApp.getHecReplaceString(lotteryInfo.getLotteryType()));

            ImageView img = (ImageView) view.findViewById(R.id.imgLotteryIcon);
            img.setImageResource(LotteryUtil.getLotteryIcon(lotteryInfo.getLotteryType()));

            ImageView iconHotNew = (ImageView) view.findViewById(R.id.icon_hot_new);
            if (mNewLottery.contains(lotteryInfo.getLotteryID())) {
                iconHotNew.setImageResource(R.mipmap.icon_new_lottery);
                iconHotNew.setVisibility(View.VISIBLE);
            }
            if (mHotLottery.contains(lotteryInfo.getLotteryID())) {
                iconHotNew.setImageResource(R.mipmap.icon_hot_lottery);
                iconHotNew.setVisibility(View.VISIBLE);
            }

            TextView tvLotteryDescription = (TextView) view.findViewById(R.id.tvLotteryDescription);
            tvLotteryDescription.setText(LotteryUtil.getLotteryMessage(lotteryInfo.getLotteryID()));

            return view;
        }

        //----------------Group----------------//
        @Override
        public Object getGroup(int groupPosition) {
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
        public View getGroupView(int groupPosition, boolean isExpanded,
                                 View convertView, ViewGroup parent) {
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

    private void getBasicData() {
        showLoading(getString(R.string.loading_message_getbasicdata));
        mIsError = false;
        if (getActivity() == null) {
            return;
        }

        MyAsyncTask<List<BasicDataInfo>> task = new MyAsyncTask<List<BasicDataInfo>>(getActivity()) {
            @Override
            public List<BasicDataInfo> callService() throws IOException, JsonParseException, BizException, ServiceException {
                SharedPreferences hashCodeSharedPreferences = getActivity().getSharedPreferences(CommonConfig.KEY_HASHCODE, getActivity().MODE_PRIVATE);
                return new LotteryService().getBasicData(hashCodeSharedPreferences.getString(CommonConfig.KEY_HASHCODE_BASICDATA_CACHE, ""));
            }

            @Override
            public void onLoaded(List<BasicDataInfo> result) throws Exception {
                if (getActivity() == null || getActivity().isFinishing()) {
                    return;
                }

                closeLoading();
                if (!mIsError) {
                    BasicDataInfo basicDataInfo = new LotteryService().getCachedBasicDataInfo(LotteryConfig.PLAY_MODE.CLASSIC);
                    if (basicDataInfo != null) {
                        SharedPreferences.Editor editor = getActivity().getSharedPreferences(CommonConfig.KEY_HASHCODE, getActivity().MODE_PRIVATE).edit();
                        editor.putString(CommonConfig.KEY_HASHCODE_BASICDATA_CACHE, basicDataInfo.getHashCode());
                        editor.commit();
                    }

                    initializeData();
                } else {
                    BaseApp.changeUrl(getActivity(), new BaseApp.OnChangeUrlListener() {
                        @Override
                        public void changeSuccess() {
                            getBasicData();
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

    public void showLoading(String tips) {
        closeLoading();
        try {
            if (mLoadingDialog == null) {
                mLoadingDialog = DialogUtil.getProgressDialog(getActivity(), tips);
            }
            mLoadingDialog.setMessage(tips);
            mLoadingDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void closeLoading() {

        try {
            if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                mLoadingDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
