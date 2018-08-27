package com.hec.app.activity;

import android.graphics.Color;
import android.os.Handler;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.HorizontalScrollView;
import android.widget.Button;
import android.util.DisplayMetrics;

import com.hec.app.R;
import com.hec.app.entity.ExpertPlayTypeRadioInfo;
import com.hec.app.entity.PlayTypeInfo;
import com.hec.app.entity.PlayTypeRadioInfo;
import com.hec.app.framework.adapter.CommonAdapter;
import com.hec.app.framework.adapter.ViewHolder;
import com.hec.app.framework.widget.MyToast;
import com.hec.app.lottery.LotteryConfig;
import com.hec.app.util.DialogUtil;
import com.hec.app.util.DisplayUtil;
import com.hec.app.webservice.LotteryService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hec on 2015/11/10.
 */
public class PlayTypePopupWindow extends PopupWindow {
    private Activity mContext;
    private PlayTypeRadioInfo mPlayTypeRadioInfo;
    private int mLotteryID;
    private int mPlayTypeID;
    private int mPlayMode;
    private boolean mIsDoubleNoSingle;
    private TextView mTxtSingleSelecter;
    private TextView mTxtDoubleSelecter;
    private View mViewSingleSelecterBg;
    private View mViewDoubleSelecterBg;
    private DisplayMetrics mDisplayMetrics;
    private LinearLayout mLinearLayoutExpert;
    private LinearLayout mLinearLayoutClassic;
    private Button mBtnClassic;
    private Button mBtnExpert;
    private ListView mListViewPlayType;
    private ListView mListViewExpertPlayType;
    private HorizontalScrollView mScrollView;
    private List<PlayTypeInfo> mExpertPlayTypeInfoList;
    private List<ExpertPlayTypeRadioInfo> mExpertPlayTypeRadioInfoList;
    private OnPlayTypeRadioChangedListener onPlayTypeRadioChangedListener;
    private List<TextView> mTextViewList;
    private boolean needToCleanScreen = false;

    private static LotteryService lotteryService = new LotteryService();

    public interface OnPlayTypeRadioChangedListener {
        void onChange(PlayTypeInfo playTypeInfo, PlayTypeRadioInfo playTypeRadioInfo, int playMode);
    }

    public void setOnPlayTypeRadioChangedListener(OnPlayTypeRadioChangedListener listener) {
        this.onPlayTypeRadioChangedListener = listener;
    }

    public PlayTypePopupWindow(Activity _context, int lotteryID, int playTypeID, PlayTypeRadioInfo playTypeRadioInfo, int playMode, boolean _double_no_single, boolean needToCleanScreen) {
        this.mContext = _context;
        this.mLotteryID = lotteryID;
        this.mPlayTypeID = playTypeID;
        this.mPlayTypeRadioInfo = playTypeRadioInfo;
        this.mPlayMode = playMode;
        this.mIsDoubleNoSingle = _double_no_single;
        this.needToCleanScreen = needToCleanScreen;

        // 專家模式，預設選項為複式
        if (playMode == LotteryConfig.PLAY_MODE.EXPERT) {
            this.mIsDoubleNoSingle = true;
        }

        initView();
    }

    private void initView() {
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.pop_playtype, null);
        this.setContentView(contentView);
        this.setWidth(GridLayout.LayoutParams.MATCH_PARENT);
        this.setHeight(GridLayout.LayoutParams.MATCH_PARENT);
        this.setFocusable(true);
        this.setBackgroundDrawable(mContext.getResources().getDrawable(android.R.color.white));
        this.setTouchable(true);
        this.setOutsideTouchable(true);
        this.update();

        // 螢幕解析度
        mDisplayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(mDisplayMetrics);

        mLinearLayoutExpert = (LinearLayout) contentView.findViewById(R.id.expert_lottery);
        mLinearLayoutClassic = (LinearLayout) contentView.findViewById(R.id.hint_lottery);
        mBtnClassic = (Button) contentView.findViewById(R.id.btnClassic);
        mBtnClassic.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mPlayMode = LotteryConfig.PLAY_MODE.CLASSIC;
                onButtonClicked((String) v.getTag());

                mListViewPlayType.setVisibility(View.VISIBLE);
                mListViewExpertPlayType.setVisibility(View.GONE);
                return false;
            }
        });
        mBtnExpert = (Button) contentView.findViewById(R.id.btnExpert);
        mBtnExpert.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mPlayMode = LotteryConfig.PLAY_MODE.EXPERT;
                onButtonClicked((String) v.getTag());

                mListViewPlayType.setVisibility(View.GONE);
                mListViewExpertPlayType.setVisibility(View.VISIBLE);
                return false;
            }
        });

        mExpertPlayTypeInfoList = new ArrayList<>();
        try {
            mExpertPlayTypeInfoList = lotteryService.getPlayTypesInfo(mLotteryID, LotteryConfig.PLAY_MODE.EXPERT);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (mExpertPlayTypeInfoList.isEmpty()) {
            mBtnExpert.setVisibility(View.GONE);
        }

        // 玩法
        initListViewClassicPlayType(contentView);
        initListViewExpertPlayType(contentView);

        if (mPlayMode == LotteryConfig.PLAY_MODE.CLASSIC) {
            onButtonClicked((String) mBtnClassic.getTag());
        } else {
            onButtonClicked((String) mBtnExpert.getTag());
        }

        // 專家
        int chooseIndex = 0;
        int index = 0;
        final List<String> expertPlayTypeName = new ArrayList<>();
        expertPlayTypeName.add("");
        expertPlayTypeName.add("");
        for (PlayTypeInfo info : mExpertPlayTypeInfoList) {
            expertPlayTypeName.add(info.getPlayTypeName());
            if (mPlayTypeID == info.getPlayTypeID()) {
                chooseIndex = index;
            }
            ++index;
        }
        expertPlayTypeName.add("");
        expertPlayTypeName.add("");

        mScrollView = (HorizontalScrollView) contentView.findViewById(R.id.expertScrollView);
        LinearLayout scrollViewLayout = (LinearLayout) mScrollView.findViewById(R.id.expertLayout);
        mScrollView.setOnTouchListener(new OnTouchListener() {
            private int lastX = 0;
            private int touchEventId = -9983761;

            Handler handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    View scroller = (View) msg.obj;
                    if (msg.what == touchEventId) {
                        if (lastX == scroller.getScrollX()) {
                            handleStop(scroller);
                        } else {
                            handler.sendMessageDelayed(handler.obtainMessage(touchEventId, scroller), 5);
                            lastX = scroller.getScrollX();
                        }
                    }
                }
            };

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    handler.sendMessageDelayed(handler.obtainMessage(touchEventId, v), 5);
                }
                return false;
            }

            private void handleStop(Object view) {
                double value = (double) mScrollView.getScrollX() / (double) (mDisplayMetrics.widthPixels / 5);
                int round = (int) Math.round(value);
                int scrollToX = mDisplayMetrics.widthPixels / 5 * round;
                mScrollView.smoothScrollTo(scrollToX, 0);

                if (mExpertPlayTypeInfoList.size() >= round && round >= 0) {
                    mPlayTypeID = mExpertPlayTypeInfoList.get(round).getPlayTypeID();

                    updateExpertPlayTypeRadioInfo();
                    CommonAdapter<ExpertPlayTypeRadioInfo> adapter = (CommonAdapter<ExpertPlayTypeRadioInfo>) mListViewExpertPlayType.getAdapter();
                    adapter.notifyDataSetChanged();

                    for (TextView textView : mTextViewList) {
                        if ((int) textView.getTag() == round) {
                            textView.setTextColor(Color.parseColor("#08A09D"));
                        } else {
                            textView.setTextColor(Color.parseColor("#A38644"));
                        }
                    }
                }
            }
        });

        index = -2;
        mTextViewList = new ArrayList<>();
        for (final String name : expertPlayTypeName) {
            TextView textView = new TextView(mContext);
            textView.setText(name);
            textView.setTextColor(Color.parseColor("#A38644"));
            textView.setBackground(null);
            textView.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
            textView.setWidth(mDisplayMetrics.widthPixels / 5);
            textView.setHeight((int) (32 * mContext.getResources().getDisplayMetrics().density));
            textView.setTag(index);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int tag = (int) v.getTag();
                    if (tag >= 0 && tag < expertPlayTypeName.size() - 2) {
                        mScrollView.smoothScrollTo(mDisplayMetrics.widthPixels / 5 * tag, 0);

                        for (PlayTypeInfo info : mExpertPlayTypeInfoList) {
                            if (info.getPlayTypeName() == name) {
                                mPlayTypeID = info.getPlayTypeID();
                            }
                        }

                        for (TextView view : mTextViewList) {
                            if ((int) view.getTag() == tag) {
                                view.setTextColor(Color.parseColor("#08A09D"));
                            } else {
                                view.setTextColor(Color.parseColor("#A38644"));
                            }
                        }

                        updateExpertPlayTypeRadioInfo();
                        CommonAdapter<ExpertPlayTypeRadioInfo> adapter = (CommonAdapter<ExpertPlayTypeRadioInfo>) mListViewExpertPlayType.getAdapter();
                        adapter.notifyDataSetChanged();
                    }
                }
            });
            mTextViewList.add(textView);
            scrollViewLayout.addView(textView);

            ++index;
        }

        final int finalChooseIndex = chooseIndex;
        mScrollView.post(new Runnable() {
            @Override
            public void run() {
                mScrollView.scrollTo(mDisplayMetrics.widthPixels / 5 * finalChooseIndex, 0);
                for (TextView view : mTextViewList) {
                    if ((int) view.getTag() == finalChooseIndex) {
                        view.setTextColor(Color.parseColor("#08A09D"));
                    } else {
                        view.setTextColor(Color.parseColor("#A38644"));
                    }
                }
            }
        });

        // 經典
        mTxtSingleSelecter = (TextView) contentView.findViewById(R.id.select_item1);
        mTxtDoubleSelecter = (TextView) contentView.findViewById(R.id.select_item2);
        mViewSingleSelecterBg = contentView.findViewById(R.id.select_item_bg1);
        mViewDoubleSelecterBg = contentView.findViewById(R.id.select_item_bg2);
        if (!mIsDoubleNoSingle) {
            mViewSingleSelecterBg.setVisibility(View.INVISIBLE);
            mTxtSingleSelecter.setTextColor(mContext.getResources().getColor(R.color.white));
            mViewDoubleSelecterBg.setVisibility(View.VISIBLE);
            mTxtDoubleSelecter.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
        }

        RelativeLayout singleDoubleSelector = (RelativeLayout) contentView.findViewById(R.id.single_double_selector);
        singleDoubleSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsDoubleNoSingle) {
                    mViewSingleSelecterBg.setVisibility(View.INVISIBLE);
                    mTxtSingleSelecter.setTextColor(mContext.getResources().getColor(R.color.white));
                    mViewDoubleSelecterBg.setVisibility(View.VISIBLE);
                    mTxtDoubleSelecter.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
                    mIsDoubleNoSingle = false;
                } else {
                    mViewSingleSelecterBg.setVisibility(View.VISIBLE);
                    mTxtSingleSelecter.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
                    mViewDoubleSelecterBg.setVisibility(View.INVISIBLE);
                    mTxtDoubleSelecter.setTextColor(mContext.getResources().getColor(R.color.white));
                    mIsDoubleNoSingle = true;
                }

                mListViewPlayType.invalidate();
            }
        });
    }

    private void initListViewClassicPlayType(View contentView) {
        List<PlayTypeInfo> playTypeInfoList = new ArrayList<>();
        try {
            playTypeInfoList = lotteryService.getPlayTypesInfo(mLotteryID, LotteryConfig.PLAY_MODE.CLASSIC);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (playTypeInfoList.size() <= 0) {
            mBtnClassic.setVisibility(View.GONE);
        }

        final CommonAdapter<PlayTypeInfo> adapter = new CommonAdapter<PlayTypeInfo>(mContext, playTypeInfoList, R.layout.list_item_play_type) {
            @Override
            public void convert(ViewHolder helper, PlayTypeInfo item, int position) {
                convertClassic(helper, item);
            }
        };

        mListViewPlayType = (ListView) contentView.findViewById(R.id.listPlayType);
        mListViewPlayType.setAdapter(adapter);

        if (mPlayMode == LotteryConfig.PLAY_MODE.CLASSIC) {
            mListViewPlayType.setVisibility(View.VISIBLE);
        } else {
            mListViewPlayType.setVisibility(View.GONE);
        }
    }

    private void initListViewExpertPlayType(View contentView) {
        updateExpertPlayTypeRadioInfo();
        final CommonAdapter<ExpertPlayTypeRadioInfo> adapter = new CommonAdapter<ExpertPlayTypeRadioInfo>(mContext, mExpertPlayTypeRadioInfoList, R.layout.list_item_play_type) {
            @Override
            public void convert(ViewHolder helper, ExpertPlayTypeRadioInfo info, int position) {
                convertExpert(helper, info);
            }
        };

        mListViewExpertPlayType = (ListView) contentView.findViewById(R.id.listExpertPlayType);
        mListViewExpertPlayType.setAdapter(adapter);

        if (mPlayMode == LotteryConfig.PLAY_MODE.EXPERT) {
            mListViewExpertPlayType.setVisibility(View.VISIBLE);
        } else {
            mListViewExpertPlayType.setVisibility(View.GONE);
        }
    }

    private void updateExpertPlayTypeRadioInfo() {
        int playTypeID = mPlayTypeID;
        if (mPlayMode == LotteryConfig.PLAY_MODE.CLASSIC) {
            if (mExpertPlayTypeInfoList.size() > 0) {
                playTypeID = mExpertPlayTypeInfoList.get(0).getPlayTypeID();
            }
        }

        List<PlayTypeRadioInfo> playTypeRadioInfos = new ArrayList<>();
        try {
            playTypeRadioInfos = lotteryService.getPlayTypeRadiosInfo((Integer) playTypeID, LotteryConfig.PLAY_MODE.EXPERT);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (mExpertPlayTypeRadioInfoList == null) {
            mExpertPlayTypeRadioInfoList = new ArrayList<>();
        } else {
            mExpertPlayTypeRadioInfoList.clear();
        }

        for (PlayTypeRadioInfo info : playTypeRadioInfos) {
            if (mExpertPlayTypeRadioInfoList == null) {
                ExpertPlayTypeRadioInfo expertInfo = new ExpertPlayTypeRadioInfo();
                expertInfo.setPlayTypeModel(info.getPlayTypeModel());
                expertInfo.addPlayTypeRadioInfo(info);
                mExpertPlayTypeRadioInfoList.add(expertInfo);
            } else {
                boolean isRepeat = false;
                for (ExpertPlayTypeRadioInfo expertInfo : mExpertPlayTypeRadioInfoList) {
                    if (expertInfo.getPlayTypeModel().equals(info.getPlayTypeModel())) {
                        expertInfo.addPlayTypeRadioInfo(info);
                        isRepeat = true;
                        break;
                    }
                }

                if (!isRepeat) {
                    ExpertPlayTypeRadioInfo expertInfo = new ExpertPlayTypeRadioInfo();
                    expertInfo.setPlayTypeModel(info.getPlayTypeModel());
                    expertInfo.addPlayTypeRadioInfo(info);
                    mExpertPlayTypeRadioInfoList.add(expertInfo);
                }
            }
        }
    }

    private void convertClassic(ViewHolder helper, PlayTypeInfo item) {
        final PlayTypeInfo playTypeInfo = item;
        final String playtypename = item.getPlayTypeName();

        LinearLayout linearLayout = helper.getView(R.id.indicate_circle);
        ImageView imgSelect1 = helper.getView(R.id.select_img1);
        ImageView imgSelect2 = helper.getView(R.id.select_img2);
        ImageView imgSelect3 = helper.getView(R.id.select_img3);
        ImageView imgSelect4 = helper.getView(R.id.select_img4);
        ImageView imgSelect5 = helper.getView(R.id.select_img5);

        if (playtypename.contains("星")) {
            if (playtypename.contains("五")) {
                imgSelect1.setImageResource(R.mipmap.shixin);
                imgSelect2.setImageResource(R.mipmap.shixin);
                imgSelect3.setImageResource(R.mipmap.shixin);
                imgSelect4.setImageResource(R.mipmap.shixin);
                imgSelect5.setImageResource(R.mipmap.shixin);
            } else if (playtypename.contains("四")) {
                imgSelect1.setImageResource(R.mipmap.shixin);
                imgSelect2.setImageResource(R.mipmap.shixin);
                imgSelect3.setImageResource(R.mipmap.shixin);
                imgSelect4.setImageResource(R.mipmap.shixin);
                imgSelect5.setImageResource(R.mipmap.kongxin);
            } else if (playtypename.contains("三")) {
                if (playtypename.contains("直")) {
                    imgSelect1.setImageResource(R.mipmap.shixin);
                    imgSelect2.setImageResource(R.mipmap.shixin);
                    imgSelect3.setImageResource(R.mipmap.shixin);
                    imgSelect4.setImageResource(R.mipmap.kongxin);
                    imgSelect5.setImageResource(R.mipmap.kongxin);
                } else if (playtypename.contains("组")) {
                    imgSelect1.setImageResource(R.mipmap.wenhao);
                    imgSelect2.setImageResource(R.mipmap.wenhao);
                    imgSelect3.setImageResource(R.mipmap.wenhao);
                    imgSelect4.setImageResource(R.mipmap.kongxin);
                    imgSelect5.setImageResource(R.mipmap.kongxin);
                }
            } else if (playtypename.contains("二")) {
                if (playtypename.contains("直")) {
                    imgSelect1.setImageResource(R.mipmap.shixin);
                    imgSelect2.setImageResource(R.mipmap.shixin);
                    imgSelect3.setImageResource(R.mipmap.kongxin);
                    imgSelect4.setImageResource(R.mipmap.kongxin);
                    imgSelect5.setImageResource(R.mipmap.kongxin);
                } else if (playtypename.contains("组")) {
                    imgSelect1.setImageResource(R.mipmap.wenhao);
                    imgSelect2.setImageResource(R.mipmap.wenhao);
                    imgSelect3.setImageResource(R.mipmap.kongxin);
                    imgSelect4.setImageResource(R.mipmap.kongxin);
                    imgSelect5.setImageResource(R.mipmap.kongxin);
                }
            }
        } else {
            imgSelect1.setImageResource(0);
            imgSelect2.setImageResource(0);
            imgSelect3.setImageResource(0);
            imgSelect4.setImageResource(0);
            imgSelect5.setImageResource(0);
        }

        TextView txtPlayTypeName = helper.getView(R.id.tvPlayTypeName);
        txtPlayTypeName.setText(playtypename);
        txtPlayTypeName.setTextAppearance(mContext, R.style.Lottery_PlayType_Text_Style);
        txtPlayTypeName.setTag(item.getPlayTypeID());

        //获取子玩法
        List<PlayTypeRadioInfo> playTypeRadioInfos = null;
        try {
            playTypeRadioInfos = lotteryService.getPlayTypeRadiosInfo((Integer) txtPlayTypeName.getTag(), LotteryConfig.PLAY_MODE.CLASSIC);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (playTypeRadioInfos != null) {
            GridLayout gridLayout = helper.getView(R.id.playTypeRadioContainer);
            gridLayout.removeAllViews();

            int index = 0;
            for (PlayTypeRadioInfo info : playTypeRadioInfos) {
                index++;
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(DisplayUtil.getPxByDp(mContext, 76), DisplayUtil.getPxByDp(mContext, 24));
                LinearLayout layout = new LinearLayout(mContext);
                int dp40 = DisplayUtil.getPxByDp(mContext, 40);
                int dp12 = DisplayUtil.getPxByDp(mContext, 12);
                int rightMargin = 0;
                if (index % 2 != 0) {
                    rightMargin = dp40;
                }
                params.setMargins(0, 0, rightMargin, dp12);
                layout.setLayoutParams(params);
                layout.setGravity(Gravity.CENTER);
                layout.setBackgroundResource(R.drawable.rect_playtype);

                final TextView textView = new TextView(mContext);
                final PlayTypeRadioInfo radioInfo = info;

                String pn = "复式";
                if (info.getPlayTypeRadioName().contains(pn)) {
                    textView.setText(info.getPlayTypeRadioName().substring(0, info.getPlayTypeRadioName().length() - 2));
                } else {
                    textView.setText(info.getPlayTypeRadioName());
                }

                textView.setTextAppearance(mContext, R.style.Lottery_PlayTypeRadio_Text_Style);
                textView.setTag(info.getPlayTypeRadioID());

                if (mPlayTypeRadioInfo.getPlayTypeRadioID() == info.getPlayTypeRadioID() && mPlayTypeRadioInfo.getPlayTypeRadioName() == info.getPlayTypeRadioName()) {
                    layout.setBackgroundResource(R.drawable.rect_playtype_selected);
                    textView.setTextAppearance(mContext, R.style.Lottery_PlayTypeRadio_Text_Selected_Style);
                }

                layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                                if(playTypeInfo.getPlayTypeName().contains("前六")){
//                                    if(!MyToast.isShowing())
//                                        MyToast.show(context,"该玩法即将上线！");
//                                    return;
//                                }
                        if (mIsDoubleNoSingle) {
                            if (needToCleanScreen) {
                                DialogUtil.getConfirmAlertDialogWithNegativeBtn(mContext, "提示信息", "切换玩法会清空当前已经选择的号码，是否继续？", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (onPlayTypeRadioChangedListener != null) {
                                            onPlayTypeRadioChangedListener.onChange(playTypeInfo, radioInfo, LotteryConfig.PLAY_MODE.CLASSIC);
                                        }
                                        PlayTypePopupWindow.this.dismiss();
                                    }
                                }, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        PlayTypePopupWindow.this.dismiss();
                                    }
                                }).show();
                            } else {
                                if (onPlayTypeRadioChangedListener != null) {
                                    onPlayTypeRadioChangedListener.onChange(playTypeInfo, radioInfo, LotteryConfig.PLAY_MODE.CLASSIC);
                                }
                                PlayTypePopupWindow.this.dismiss();
                            }
                        } else if (!mIsDoubleNoSingle) {
                            List<PlayTypeRadioInfo> ptr = null;
                            try {
                                ptr = lotteryService.getPlayTypeRadiosInfoALL(playTypeInfo.getPlayTypeID(), LotteryConfig.PLAY_MODE.CLASSIC);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            Boolean contain = false;
//                                    TestUtil.print("t " + t.getText());
                            for (PlayTypeRadioInfo item : ptr) {
//                                        TestUtil.print(item.getPlayTypeRadioName());
                                if (!item.getPlayTypeRadioName().contains("单式"))
                                    continue;
                                String tmp = item.getPlayTypeRadioName().replace("单式", "");
                                // 20180723 快三的二同號單選 PlayTypeRadioName只有二同號單式, 因此列出例外
                                if (tmp.equals(textView.getText()) ||
                                        (mLotteryID == 18 && textView.getText().toString().contains(tmp))) {
                                    contain = true;
                                }
                            }
                            if (!contain) {
                                MyToast.show(mContext, "您所选的子玩法没有单式!", 1000);
                            } else {
                                if (needToCleanScreen) {
                                    DialogUtil.getConfirmAlertDialogWithNegativeBtn(mContext, "提示信息", "切换玩法会清空当前已经选择的号码，是否继续？", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            PlayTypeRadioInfo rInfo = new PlayTypeRadioInfo(0, "", 0, "", "");
                                            List<PlayTypeRadioInfo> ptr = null;
                                            try {
                                                ptr = lotteryService.getPlayTypeRadiosInfoALL(playTypeInfo.getPlayTypeID(), LotteryConfig.PLAY_MODE.CLASSIC);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            for (PlayTypeRadioInfo item : ptr) {

                                                // 20180723 快三的二同號單選 PlayTypeRadioName只有二同號單式, 因此列出例外
                                                String tmp = item.getPlayTypeRadioName().replace("单式", "");
                                                if (tmp.equals(textView.getText()) ||
                                                        (mLotteryID == 18 && textView.getText().toString().contains(tmp))) {
                                                    rInfo.setPlayDescription(item.getPlayDescription());
                                                    rInfo.setPlayTypeID(item.getPlayTypeID());
                                                    rInfo.setPlayTypeRadioID(item.getPlayTypeRadioID());
                                                    rInfo.setPlayTypeRadioName(item.getPlayTypeRadioName());
                                                    rInfo.setWinExample(item.getWinExample());
                                                }
                                            }
                                            if (onPlayTypeRadioChangedListener != null) {
                                                onPlayTypeRadioChangedListener.onChange(playTypeInfo, rInfo, LotteryConfig.PLAY_MODE.CLASSIC);
                                            }
                                            PlayTypePopupWindow.this.dismiss();
                                        }
                                    }, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            PlayTypePopupWindow.this.dismiss();
                                        }
                                    }).show();
                                } else {
                                    PlayTypeRadioInfo rInfo = new PlayTypeRadioInfo(0, "", 0, "", "");
                                    List<PlayTypeRadioInfo> playTypeRadioInfoList = null;
                                    try {
                                        playTypeRadioInfoList = lotteryService.getPlayTypeRadiosInfoALL(playTypeInfo.getPlayTypeID(), LotteryConfig.PLAY_MODE.CLASSIC);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    for (PlayTypeRadioInfo item : playTypeRadioInfoList) {

                                        // 20180723 快三的二同號單選 PlayTypeRadioName只有二同號單式, 因此列出例外
                                        String tmp = item.getPlayTypeRadioName().replace("单式", "");
                                        if (tmp.equals(textView.getText()) ||
                                                (mLotteryID == 18 && textView.getText().toString().contains(tmp))) {
                                            rInfo.setPlayDescription(item.getPlayDescription());
                                            rInfo.setPlayTypeID(item.getPlayTypeID());
                                            rInfo.setPlayTypeRadioID(item.getPlayTypeRadioID());
                                            rInfo.setPlayTypeRadioName(item.getPlayTypeRadioName());
                                            rInfo.setWinExample(item.getWinExample());
                                        }
                                    }
                                    if (onPlayTypeRadioChangedListener != null) {
                                        onPlayTypeRadioChangedListener.onChange(playTypeInfo, rInfo, LotteryConfig.PLAY_MODE.CLASSIC);
                                    }
                                    PlayTypePopupWindow.this.dismiss();
                                }
                            }
                        }
                    }
                });

                layout.addView(textView);
                gridLayout.addView(layout);
            }
        }
    }

    private void convertExpert(ViewHolder helper, ExpertPlayTypeRadioInfo info) {
        LinearLayout layoutIndicateCircle = helper.getView(R.id.indicate_circle);
        layoutIndicateCircle.setVisibility(View.GONE);

        TextView txtPlayTypeName = helper.getView(R.id.tvPlayTypeName);
        txtPlayTypeName.setText(info.getPlayTypeModel());
        txtPlayTypeName.setTextAppearance(mContext, R.style.Lottery_PlayType_Text_Style);

        GridLayout gridLayout = helper.getView(R.id.playTypeRadioContainer);
        gridLayout.removeAllViews();
        for (PlayTypeRadioInfo radioInfo : info.getPlayTypeRadioInfo()) {
            int dp40 = DisplayUtil.getPxByDp(mContext, 40);
            int dp12 = DisplayUtil.getPxByDp(mContext, 12);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(DisplayUtil.getPxByDp(mContext, 76), DisplayUtil.getPxByDp(mContext, 24));
            params.setMargins(0, 0, dp40, dp12);
            LinearLayout layout = new LinearLayout(mContext);
            layout.setLayoutParams(params);
            layout.setGravity(Gravity.CENTER);
            layout.setBackgroundResource(R.drawable.rect_playtype);

            final TextView textView = new TextView(mContext);
            textView.setText(radioInfo.getPlayTypeRadioName());
            textView.setTextAppearance(mContext, R.style.Lottery_PlayTypeRadio_Text_Style);
            textView.setTag(radioInfo.getPlayTypeRadioID());

            if (mPlayTypeRadioInfo.getPlayTypeRadioID() == radioInfo.getPlayTypeRadioID() && mPlayTypeRadioInfo.getPlayTypeRadioName() == radioInfo.getPlayTypeRadioName()) {
                layout.setBackgroundResource(R.drawable.rect_playtype_selected);
                textView.setTextAppearance(mContext, R.style.Lottery_PlayTypeRadio_Text_Selected_Style);
            }

            final PlayTypeRadioInfo playTypeRadioInfo = radioInfo;
            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (needToCleanScreen) {
                        DialogUtil.getConfirmAlertDialogWithNegativeBtn(mContext, "提示信息", "切换玩法会清空当前已经选择的号码，是否继续？", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (onPlayTypeRadioChangedListener != null) {
                                    PlayTypeInfo typeInfo = null;
                                    for (PlayTypeInfo playTypeInfo : mExpertPlayTypeInfoList) {
                                        if (playTypeInfo.getPlayTypeID() == playTypeRadioInfo.getPlayTypeID()) {
                                            typeInfo = playTypeInfo;
                                        }
                                    }

                                    onPlayTypeRadioChangedListener.onChange(typeInfo, playTypeRadioInfo, LotteryConfig.PLAY_MODE.EXPERT);
                                }
                                PlayTypePopupWindow.this.dismiss();
                            }
                        }, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                PlayTypePopupWindow.this.dismiss();
                            }
                        }).show();
                    } else {
                        if (onPlayTypeRadioChangedListener != null) {
                            PlayTypeInfo typeInfo = null;
                            for (PlayTypeInfo playTypeInfo : mExpertPlayTypeInfoList) {
                                if (playTypeInfo.getPlayTypeID() == playTypeRadioInfo.getPlayTypeID()) {
                                    typeInfo = playTypeInfo;
                                }
                            }

                            onPlayTypeRadioChangedListener.onChange(typeInfo, playTypeRadioInfo, LotteryConfig.PLAY_MODE.EXPERT);
                        }
                        PlayTypePopupWindow.this.dismiss();
                    }
                }
            });

            layout.addView(textView);
            gridLayout.addView(layout);
        }
    }

    private void onButtonClicked(String tag) {
        if (tag.contains("Classic")) {
            mLinearLayoutExpert.setVisibility(View.GONE);
            mLinearLayoutClassic.setVisibility(View.VISIBLE);

            mBtnClassic.setEnabled(false);
            mBtnClassic.setBackgroundColor(Color.parseColor("#0a335a"));
            mBtnClassic.setTextColor(Color.parseColor("#fffffd"));
            mBtnExpert.setEnabled(true);
            mBtnExpert.setBackgroundColor(Color.parseColor("#183c64"));
            mBtnExpert.setTextColor(Color.parseColor("#7597b8"));
        } else if (tag.contains("Expert")) {
            mLinearLayoutExpert.setVisibility(View.VISIBLE);
            mLinearLayoutClassic.setVisibility(View.GONE);

            mBtnClassic.setEnabled(true);
            mBtnClassic.setBackgroundColor(Color.parseColor("#183c64"));
            mBtnClassic.setTextColor(Color.parseColor("#7597b8"));
            mBtnExpert.setEnabled(false);
            mBtnExpert.setBackgroundColor(Color.parseColor("#0a335a"));
            mBtnExpert.setTextColor(Color.parseColor("#fffffd"));
        }
    }

    public String getSingleorDouble() {
        if (mIsDoubleNoSingle)
            return "复式";
        else
            return "单式";
    }
}

