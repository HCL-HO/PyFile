package com.hec.app.activity;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.graphics.Point;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.gson.JsonParseException;
import com.hec.app.R;
import com.hec.app.activity.base.BaseActivityWithMenu;
import com.hec.app.activity.base.BaseApp;
import com.hec.app.config.CommonConfig;
import com.hec.app.entity.AllPlayConfig;
import com.hec.app.entity.BallRebates;
import com.hec.app.entity.BizException;
import com.hec.app.entity.Field;
import com.hec.app.entity.LayoutInfo;
import com.hec.app.entity.LotteryInfo;
import com.hec.app.entity.NewRebateQueryCriteria;
import com.hec.app.entity.PlaceOrderInfo;
import com.hec.app.entity.PlayConfig;
import com.hec.app.entity.PlayTypeInfo;
import com.hec.app.entity.PlayTypeRadioInfo;
import com.hec.app.entity.RebateQueryCriteria;
import com.hec.app.entity.Response;
import com.hec.app.entity.SelectListItem;
import com.hec.app.entity.SelectedBallInfo;
import com.hec.app.entity.SysSettings;
import com.hec.app.entity.Trace;
import com.hec.app.fragment.LotteryHistoryFragment;
import com.hec.app.fragment.NextIssueNoFragment;
import com.hec.app.framework.widget.CircleButton;
import com.hec.app.framework.widget.MyToast;
import com.hec.app.framework.widget.ResideMenu;
import com.hec.app.lottery.LotteryManager;
import com.hec.app.util.DefaultDataCache;
import com.hec.app.util.DialogUtil;
import com.hec.app.util.DisplayUtil;
import com.hec.app.util.IntentUtil;
import com.hec.app.util.LotteryCalculator;
import com.hec.app.util.LotteryUtil;
import com.hec.app.util.MyAsyncTask;

import com.hec.app.util.MyFloatingActionButton;
import com.hec.app.util.RebateSortComparator;
import com.hec.app.util.StringUtil;
import com.hec.app.util.TestUtil;
import com.hec.app.webservice.LotteryService;
import com.hec.app.webservice.ServiceException;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apmem.tools.layouts.FlowLayout;

import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import de.greenrobot.event.EventBus;
import github.chenupt.dragtoplayout.AttachUtil;
import github.chenupt.dragtoplayout.DragTopLayout;

import static com.hec.app.lottery.LotteryConfig.*;

public class LotteryActivity extends BaseActivityWithMenu implements NextIssueNoFragment.OnCurrentIssueNoCompletedListener {
    private String TAG = LotteryActivity.class.getName();
    private DragTopLayout dragLayout;
    private ImageView expandImage;
    private LinearLayout ll_clearAll;
    private LinearLayout ll_ok;
    private LinearLayout ll_bottom;
    private LinearLayout layoutContainer;
    private LinearLayout layoutOptionContainer;
    private LinearLayout layoutRebate;
    private LinearLayout layoutPopExport;
    private ScrollView scrollView;
    private LinearLayout playTypeLayout;
    private TextView tvPlayType;
    private TextView tvPlayTypeRadio;
    private int currentLotteryID;
    private int currentPlayTypeID;
    private int currentPlayTypeRadioID;
    private LotteryInfo currentLotteryInfo;
    private PlayTypeInfo currentPlayTypeInfo;
    private PlayTypeRadioInfo currentPlayTypeRadioInfo;
    private TextView tvTotalNums;
    private TextView tvTotalAmount;
    private EditText txtPrice;
    private CircleButton lastBold;
    private Spinner rebateSelectList;
    private boolean mIsError;
    private boolean mIsAllPlayConfigError;
    private ImageView imgPlayTypeDes;
    private LotteryHistoryFragment historyFragment;
    private NextIssueNoFragment nextIssueNoFragment;
    private ImageView imgPersonalCenter;
    private ResideMenu resideMenu;
    private String playradioname = "";
    private String playtypename = "";
    private List<String> editNumbers = new ArrayList<>();
    private List<String> groupOfNum = new ArrayList<>();
    private List<String> rebateValues = new ArrayList<>();
    private List<PlayConfig> mPlayConfigList = new ArrayList<>();
    private int totalNums = 0;
    private String[] tube = null;
    private Set<String> checkIfDuplicate = new HashSet<>();
    private CharSequence coerceToText;
    double amount = 0.0;
    double price = 0.0;
    private String lotteryType = "";
    private boolean formatErr = false;
    private String typeurl = "";
    private int userSelectRebate = 0;
    private SharedPreferences sharedPreferences;
    private int mPlayMode = PLAY_MODE.CLASSIC;
    private Editable mSigleEditable;
    private ProgressDialog mProgressDialog;
    public Handler mHandler;
    private boolean mIsFirstOpenLottery = false;
    private MyFloatingActionButton fab_cahtroom;
    private int width, height;
    private List<SelectListItem> mSelectListItemList = new ArrayList<>();
    private List<BallRebates> ballRebatesList = new ArrayList<>();
    private MyAsyncTask<List<SelectListItem>> getRebateTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        sharedPreferences = getSharedPreferences("rebate", MODE_PRIVATE);
        TestUtil.print("lottery activity onCreate");
        BaseApp.allSettleInfo.clear();
        BaseApp.allUnits.clear();
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        currentLotteryID = getIntent().getIntExtra("LotteryID", 0);
        typeurl = getIntent().getStringExtra("typeurl");

        boolean isClassicPlayTypesInfo = true;
        try {
            isClassicPlayTypesInfo = new LotteryService().isClassicPlayTypesInfo(currentLotteryID);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (isClassicPlayTypesInfo) {
            mPlayMode = getIntent().getIntExtra("PlayMode", PLAY_MODE.CLASSIC);
        } else {
            mPlayMode = getIntent().getIntExtra("PlayMode", PLAY_MODE.EXPERT);
        }

        currentPlayTypeID = getIntent().getIntExtra("PlayTypeID", 0);
        currentPlayTypeRadioID = getIntent().getIntExtra("PlayTypeRadioID", 0);
        setContentView(R.layout.activity_lottery);
        findView();
        resideMenu = super.getResidingMenu();

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;

        fab_cahtroom = (MyFloatingActionButton) findViewById(R.id.fab_chatroom);
        if (currentLotteryID == 19 || currentLotteryID == 20) {
            fab_cahtroom.setVisibility(View.GONE);
        }
        fab_cahtroom.getWindow();
        fab_cahtroom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("typeurl", typeurl);
                intent.setClass(LotteryActivity.this, ChatRoomActivity.class);
                startActivity(intent);
            }
        });

        mPlayConfigList = new LotteryService().getPlayConfigInfo(currentLotteryID);

        initView();
        initHandler();
        initBallLayoutV1(currentLotteryID, currentPlayTypeID, currentPlayTypeRadioID);

        getRebateSelectList();
    }

    @Override
    protected void onResume() {
        TestUtil.print("lottery activity onresume");
        super.onResume();
        //如果之前选过号码，回退到投注界面，按钮应该可用
        if (BaseApp.allSettleInfo.size() > 0) {
            tvTotalNums.setText("0");
            tvTotalAmount.setText("0");
            ll_ok.setBackgroundResource(R.mipmap.lottery_settle_disabled);
            ll_ok.setEnabled(false);

            // 防止聊天室衍生問題
            if (isSingle()) {
                if (mSigleEditable != null) {
                    singleAfterTextChanged(mSigleEditable);
                }
            } else {
                calculateTotalAmount();
            }
        }
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
        if (getRebateTask != null) {
            getRebateTask.cancel(true);
            getRebateTask = null;
        }
    }

    @Override
    public void onCompleted() {
        historyFragment.getLatestLotteryResult();
    }

    private void findView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.mipmap.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        imgPlayTypeDes = (ImageView) findViewById(R.id.imgPlayTypeDes);
        imgPersonalCenter = (ImageView) findViewById(R.id.imgPersonalCenter);
        dragLayout = (DragTopLayout) findViewById(R.id.dragTop);
        expandImage = (ImageView) findViewById(R.id.daletou_pull_iv);
        playTypeLayout = (LinearLayout) findViewById(R.id.playTypeLayout);
        tvPlayType = (TextView) findViewById(R.id.tvPlayType);
        tvPlayTypeRadio = (TextView) findViewById(R.id.tvPlayTypeRadio);
        scrollView = (ScrollView) findViewById(R.id.scroll_view);
        //btnClear = (Button) findViewById(R.id.btnClear);
        ll_clearAll = (LinearLayout) findViewById(R.id.ll_clearAll);
        ll_ok = (LinearLayout) findViewById(R.id.ll_ok);
        TextView config_hidden = (TextView) this.findViewById(R.id.config_hidden);
        config_hidden.requestFocus();
        layoutContainer = (LinearLayout) findViewById(R.id.layoutContainer);
        layoutOptionContainer = (LinearLayout) findViewById(R.id.layoutOptionContainer);
        layoutRebate = (LinearLayout) findViewById(R.id.rebate);
        tvTotalNums = (TextView) findViewById(R.id.tvTotalNums);
        tvTotalAmount = (TextView) findViewById(R.id.tvTotalAmount);
        txtPrice = (EditText) findViewById(R.id.txtPrice);
        txtPrice.addTextChangedListener(textWatcher);
        txtPrice.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    txtPrice.setText("");
                }
            }
        });
        txtPrice.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    //Clear focus here from edittext
                    txtPrice.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                return false;
            }
        });
        LinearLayout nextIssueNoContainer = (LinearLayout) findViewById(R.id.nextIssueNoContainer);
        nextIssueNoContainer.removeAllViews();
        if (historyFragment == null) {
            historyFragment = LotteryHistoryFragment.newInstance(currentLotteryID);
        }
        getFragmentManager().beginTransaction().replace(R.id.lotteryHistoryContainer, historyFragment).commit();
        if (nextIssueNoFragment == null) {
            nextIssueNoFragment = NextIssueNoFragment.newInstance(currentLotteryID);
            nextIssueNoFragment.getHistory(historyFragment);
        }
        getFragmentManager().beginTransaction().replace(R.id.nextIssueNoContainer, nextIssueNoFragment).commit();

        LinearLayout lotteryHistoryContainer = (LinearLayout) findViewById(R.id.lotteryHistoryContainer);
        lotteryHistoryContainer.removeAllViews();

        rebateSelectList = (Spinner) findViewById(R.id.rebateSelectList);

        ll_bottom = (LinearLayout) findViewById(R.id.ll_bottom);

        mIsFirstOpenLottery = isFirstOpenLottery();
        if (mIsFirstOpenLottery) {
            setFirstOpenLottery();

            layoutPopExport = (LinearLayout) findViewById(R.id.layoutPopExport);
            layoutPopExport.setVisibility(View.VISIBLE);
            layoutPopExport.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });

            View btnClose = layoutPopExport.findViewById(R.id.buttonPopClose);
            btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    closeLayoutPopExport();
                }
            });

            LinearLayout btnGo = (LinearLayout) layoutPopExport.findViewById(R.id.buttonPopGo);
            btnGo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    playTypeLayout.callOnClick();
                    closeLayoutPopExport();
                }
            });

            imgPlayTypeDes.setEnabled(false);
            imgPersonalCenter.setEnabled(false);
            playTypeLayout.setEnabled(false);
        }
    }

    private void initView() {
        ll_bottom.setVisibility(View.GONE);

        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                EventBus.getDefault().post(AttachUtil.isScrollViewAttach(scrollView));
                return false;
            }
        });
        dragLayout.toggleTopView();
        dragLayout.listener(new DragTopLayout.PanelListener() {
            @Override
            public void onPanelStateChanged(DragTopLayout.PanelState panelState) {
                if (panelState == DragTopLayout.PanelState.EXPANDED) {
                    expandImage.setImageResource(R.mipmap.icon_arrow_up);
                } else if (panelState == DragTopLayout.PanelState.COLLAPSED) {
                    expandImage.setImageResource(R.mipmap.icon_arrow_down);
                }
            }

            @Override
            public void onSliding(float ratio) {
            }

            @Override
            public void onRefresh() {
            }
        });
        ll_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goSettle();
            }
        });
        ll_clearAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearAll();
            }
        });

        playTypeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean double_no_single = !isSingle();
                boolean needToCleanScreen = false;
                if(isSingle()){
                    if(mSigleEditable != null && !mSigleEditable.toString().isEmpty()){
                        needToCleanScreen = true;
                    }
                } else {
                    if (getSelectedBall(false) != null && getSelectedBall(false).getSelectedBall() != null && !getSelectedBall(false).getSelectedBall().isEmpty()) {
                        for (String ballString : getSelectedBall(false).getSelectedBall()) {
                            if (!ballString.isEmpty()) {
                                needToCleanScreen = true;
                            }
                        }
                    }
                }
                PlayTypePopupWindow pop = new PlayTypePopupWindow(LotteryActivity.this, currentLotteryID, currentPlayTypeInfo.getPlayTypeID(), currentPlayTypeRadioInfo, mPlayMode, double_no_single, needToCleanScreen);
                pop.setOnPlayTypeRadioChangedListener(new PlayTypePopupWindow.OnPlayTypeRadioChangedListener() {
                    @Override
                    public void onChange(PlayTypeInfo playTypeInfo, PlayTypeRadioInfo playTypeRadioInfo, int playMode) {
                        BaseApp.allSettleInfo.clear();
                        BaseApp.allUnits.clear();
                        BaseApp.trace = new Trace(currentLotteryInfo.getLotteryID(), playTypeInfo.getPlayTypeID(), playTypeRadioInfo.getPlayTypeRadioID(), playMode);
                        if(mSigleEditable != null){
                            mSigleEditable.clear();
                        }
                        currentPlayTypeInfo = playTypeInfo;
                        currentPlayTypeRadioInfo = playTypeRadioInfo;
                        tvPlayType.setText(playTypeInfo.getPlayTypeName());
                        playtypename = playTypeInfo.getPlayTypeName();
                        playradioname = playTypeRadioInfo.getPlayTypeRadioName();
                        mPlayMode = playMode;

                        if ("".equals(playradioname)) {
                            tvPlayTypeRadio.setText(playTypeRadioInfo.getPlayTypeRadioName());
                        } else {
                            tvPlayTypeRadio.setText(playradioname);
                        }

                        if (playTypeInfo != null && mSelectListItemList != null) {
                            mSelectListItemList.clear();
                            initBallLayoutV1(currentLotteryID, playTypeInfo.getPlayTypeID(), playTypeRadioInfo.getPlayTypeRadioID());
                        }

                        calculateTotalAmount();
                        getRebateSelectList();

                        if (playtypename.equals("冠亚和") || playtypename.equals("综合")) {
                            layoutRebate.setVisibility(View.GONE);
                        } else {
                            layoutRebate.setVisibility(View.VISIBLE);
                        }
                    }
                });
                pop.showAsDropDown(playTypeLayout);
            }

        });
        imgPlayTypeDes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgPlayTypeDes.setBackgroundResource(R.mipmap.bg_play_description);
                PlayDesPopupWindow pop = new PlayDesPopupWindow(LotteryActivity.this,
                        currentPlayTypeRadioInfo.getPlayDescription(), currentPlayTypeRadioInfo.getWinExample());
                pop.showAsDropDown(imgPlayTypeDes);
                pop.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        imgPlayTypeDes.setBackgroundResource(R.color.transparent);
                    }
                });
                //test for chatroom
//                Intent intent = new Intent();
//                intent.putExtra("typeurl",typeurl);
//                intent.setClass(LotteryActivity.this,ChatRoomActivity.class);
//                startActivity(intent);
            }
        });
        imgPersonalCenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (resideMenu != null)
                    resideMenu.openMenu(ResideMenu.DIRECTION_RIGHT);
            }
        });
    }

    private void initHandler() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (mPlayConfigList == null) {
                    // showProgressDialog();
                }
            }
        };
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (Integer.parseInt(android.os.Build.VERSION.SDK) > 5
                && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onBackPressed() {
        if (mIsFirstOpenLottery) {
            return;
        }

        BaseApp.trace = null;

        if (txtPrice.hasFocus()) {
            txtPrice.clearFocus();
        } else {
            supportFinishAfterTransition();

        }
        if (BaseApp.chatRoomActivity != null) {
            BaseApp.chatRoomActivity.finish();
            BaseApp.chatRoomActivity = null;
        }
    }

    private void initBallLayoutV1(int lotteryID, int playTypeID, int playTypeRadioID) {
        try {
            if (mPlayConfigList == null) {
                getAllPlayConfig();
                return;
            }

            BaseApp.trace = new Trace(lotteryID, playTypeID, playTypeRadioID, mPlayMode);
            LayoutInfo data = getLayoutData(lotteryID, playTypeID, playTypeRadioID);
            layoutContainer.removeAllViews();
            layoutOptionContainer.removeAllViews();

            if (data == null) {
                return;
            }

            currentLotteryInfo = data.getLotteryInfo();
            if (currentLotteryInfo == null) {
                return;
            }

            lotteryType = currentLotteryInfo.getLotteryType();
            currentPlayTypeInfo = data.getPlayTypeInfo();
            if (currentPlayTypeInfo.getPlayTypeName().equals("冠亚和") || currentPlayTypeInfo.getPlayTypeName().equals("综合")) {
                layoutRebate.setVisibility(View.GONE);
            } else {
                layoutRebate.setVisibility(View.VISIBLE);
            }
            playtypename = currentPlayTypeInfo.getPlayTypeName();

            if (currentPlayTypeRadioInfo == null) {
                currentPlayTypeRadioInfo = data.getPlayTypeRadioInfo();
            }
            playradioname = currentPlayTypeRadioInfo.getPlayTypeRadioName();

            tvPlayType.setText(data.getPlayTypeInfo().getPlayTypeName());
            if (playradioname.isEmpty()) {
                tvPlayTypeRadio.setText(data.getPlayTypeRadioInfo().getPlayTypeRadioName());
            } else {
                tvPlayTypeRadio.setText(playradioname);
            }
            /*      *
             * 选择不同的彩种
             *       */
            LinearLayout linearLayout;
            if (isSingle()) {
                linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.lottery_position_single, null);
                layoutContainer.addView(linearLayout);
                getEditSelected();

                if (currentLotteryInfo.getLotteryType().contains("韩国") ||
                        currentLotteryInfo.getLotteryType().contains("快乐8") ||
                        currentLotteryInfo.getLotteryType().contains("台湾") ||
                        currentLotteryInfo.getLotteryType().contains("时时彩") ||
                        currentLotteryInfo.getLotteryType().contains("三分彩") ||
                        (currentLotteryInfo.getLotteryType().contains("秒秒彩") ||
                                currentLotteryInfo.getLotteryType().contains("分分彩") && !currentLotteryInfo.getLotteryType().contains("PK拾"))) {
                    LinearLayout optionLayout = (LinearLayout) linearLayout.findViewById(R.id.layoutOptionContainer);
                    optionLayout.setVisibility(View.GONE);

                    if (currentPlayTypeInfo.getPlayTypeName().equals("任二") || currentPlayTypeRadioInfo.getPlayTypeRadioName().contains("任选二")) {
                        setCheckBox(optionLayout, R.id.checkBoxOneSigle, true);
                        setCheckBox(optionLayout, R.id.checkBoxTenSigle, true);
                        setCheckBox(optionLayout, R.id.checkBoxHundredSigle, false);
                        setCheckBox(optionLayout, R.id.checkBoxThousandSigle, false);
                        setCheckBox(optionLayout, R.id.checkBoxTenThousandSigle, false);
                        optionLayout.setVisibility(View.VISIBLE);
                    } else if (currentPlayTypeInfo.getPlayTypeName().equals("任三") || currentPlayTypeRadioInfo.getPlayTypeRadioName().contains("任选三")) {
                        setCheckBox(optionLayout, R.id.checkBoxOneSigle, true);
                        setCheckBox(optionLayout, R.id.checkBoxTenSigle, true);
                        setCheckBox(optionLayout, R.id.checkBoxHundredSigle, true);
                        setCheckBox(optionLayout, R.id.checkBoxThousandSigle, false);
                        setCheckBox(optionLayout, R.id.checkBoxTenThousandSigle, false);
                        optionLayout.setVisibility(View.VISIBLE);
                    } else if (currentPlayTypeInfo.getPlayTypeName().equals("任四") || currentPlayTypeRadioInfo.getPlayTypeRadioName().contains("任选四")) {
                        setCheckBox(optionLayout, R.id.checkBoxOneSigle, true);
                        setCheckBox(optionLayout, R.id.checkBoxTenSigle, true);
                        setCheckBox(optionLayout, R.id.checkBoxHundredSigle, true);
                        setCheckBox(optionLayout, R.id.checkBoxThousandSigle, true);
                        setCheckBox(optionLayout, R.id.checkBoxTenThousandSigle, false);
                        optionLayout.setVisibility(View.VISIBLE);
                    }
                }
            } else {
                if (currentLotteryInfo.getLotteryType().contains("韩国") ||
                        currentLotteryInfo.getLotteryType().contains("快乐8") ||
                        currentLotteryInfo.getLotteryType().contains("台湾") ||
                        currentLotteryInfo.getLotteryType().contains("时时彩") ||
                        currentLotteryInfo.getLotteryType().contains("三分彩") ||
                        (currentLotteryInfo.getLotteryType().contains("秒秒彩") ||
                                currentLotteryInfo.getLotteryType().contains("分分彩") && !currentLotteryInfo.getLotteryType().contains("PK拾"))) {
                    if (currentPlayTypeInfo.getPlayTypeName().equals("任二") && !currentPlayTypeRadioInfo.getPlayTypeRadioName().equals("直选复式")) {
                        linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.lottery_position_line_option, null);
                        layoutOptionContainer.addView(linearLayout);

                        setCheckBox(linearLayout, R.id.checkBoxOne, true);
                        setCheckBox(linearLayout, R.id.checkBoxTen, true);
                        setCheckBox(linearLayout, R.id.checkBoxHundred, false);
                        setCheckBox(linearLayout, R.id.checkBoxThousand, false);
                        setCheckBox(linearLayout, R.id.checkBoxTenThousand, false);
                    } else if (currentPlayTypeInfo.getPlayTypeName().equals("任三") && !currentPlayTypeRadioInfo.getPlayTypeRadioName().equals("直选复式")) {
                        linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.lottery_position_line_option, null);
                        layoutOptionContainer.addView(linearLayout);

                        setCheckBox(linearLayout, R.id.checkBoxOne, true);
                        setCheckBox(linearLayout, R.id.checkBoxTen, true);
                        setCheckBox(linearLayout, R.id.checkBoxHundred, true);
                        setCheckBox(linearLayout, R.id.checkBoxThousand, false);
                        setCheckBox(linearLayout, R.id.checkBoxTenThousand, false);
                    } else if (currentPlayTypeInfo.getPlayTypeName().equals("任四") && !currentPlayTypeRadioInfo.getPlayTypeRadioName().equals("直选复式")) {
                        linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.lottery_position_line_option, null);
                        layoutOptionContainer.addView(linearLayout);

                        setCheckBox(linearLayout, R.id.checkBoxOne, true);
                        setCheckBox(linearLayout, R.id.checkBoxTen, true);
                        setCheckBox(linearLayout, R.id.checkBoxHundred, true);
                        setCheckBox(linearLayout, R.id.checkBoxThousand, true);
                        setCheckBox(linearLayout, R.id.checkBoxTenThousand, false);
                    }
                }

                List<Field> playConfig = new ArrayList<>();
                if (mPlayConfigList != null && mPlayConfigList.size() > 0) {
                    for (PlayConfig config : mPlayConfigList) {
                        if (config.getPlayTypeName().equals(currentPlayTypeInfo.getPlayTypeName()) && config.getPlayTypeRadioName().equals(currentPlayTypeRadioInfo.getPlayTypeRadioName()) && config.getPlayMode() == mPlayMode) {
                            playConfig = config.getFields();
                        }
                    }
                }

                for (int i = 0; i < playConfig.size(); i++) {
                    if (currentPlayTypeRadioInfo.getPlayTypeRadioName().equals("龙虎和")) {
                        linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.lottery_position_line_dragon_tiger_tie, null);
                    } else if (currentPlayTypeInfo.getPlayTypeName().equals("龙虎斗")) {
                        linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.lottery_position_line_pk10_dragon_tiger, null);
                    } else if (currentPlayTypeInfo.getPlayTypeName().equals("大小")) {
                        linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.lottery_position_line_pk10_big_small, null);
                    } else if (currentPlayTypeInfo.getPlayTypeName().equals("单双")) {
                        linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.lottery_position_line_pk10_odd_even, null);
                    } else {
                        boolean isWord = false;
                        if (currentPlayTypeRadioInfo.getPlayTypeRadioName().equals("特殊号") || currentPlayTypeRadioInfo.getPlayTypeRadioName().equals("总和大小单双") ||
                                currentPlayTypeRadioInfo.getPlayTypeRadioName().equals("定单双") || currentPlayTypeRadioInfo.getPlayTypeRadioName().equals("三连号通选") ||
                                currentPlayTypeRadioInfo.getPlayTypeRadioName().equals("三同号通选")) {
                            isWord = true;
                        }

                        linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.lottery_position_line, null);
                        LinearLayout quickSelect = (LinearLayout) linearLayout.findViewWithTag("quickSelect");
                        FlowLayout flowLayout = (FlowLayout) linearLayout.findViewWithTag("ballLayout");
                        if (currentPlayTypeRadioInfo.getPlayTypeRadioName().equals("组选包胆") || currentPlayTypeRadioInfo.getPlayTypeRadioName().contains("大小单双") ||
                                currentPlayTypeRadioInfo.getPlayTypeRadioName().equals("特殊号") || currentPlayTypeRadioInfo.getPlayTypeRadioName().equals("定单双") ||
                                currentPlayTypeRadioInfo.getPlayTypeRadioName().contains("组选胆拖") || currentPlayTypeInfo.getPlayTypeName().contains("任选胆拖") ||
                                currentPlayTypeRadioInfo.getPlayTypeRadioName().contains("和值") || currentPlayTypeInfo.getPlayTypeName().equals("综合") ||
                                currentPlayTypeInfo.getPlayTypeName().equals("冠亚和") || currentPlayTypeInfo.getPlayTypeName().equals("综合") ||
                                currentPlayTypeInfo.getPlayTypeName().contains("大小单双") ||
                                (currentPlayTypeInfo.getPlayTypeName().equals("定位胆") && currentPlayTypeRadioInfo.getPlayTypeRadioName().equals("手动倍数")) ||
                                currentPlayTypeRadioInfo.getPlayTypeRadioName().contains("胆拖选号") || currentPlayTypeRadioInfo.getPlayTypeRadioName().equals("三连号通选") ||
                                currentPlayTypeRadioInfo.getPlayTypeRadioName().equals("三同号通选") || currentPlayTypeRadioInfo.getPlayTypeRadioName().equals("二同号单选") ||
                                currentPlayTypeRadioInfo.getPlayTypeRadioName().equals("三同号单选") || currentPlayTypeRadioInfo.getPlayTypeRadioName().equals("二同号复选") ||
                                currentPlayTypeRadioInfo.getPlayTypeRadioName().equals("猜一个号")) {
                            quickSelect.setVisibility(View.INVISIBLE);
                        }

                        String[] nums = playConfig.get(i).getNums().split(" ");
                        for (String num : nums) {
                            if (currentPlayTypeInfo.getPlayTypeName().equals("冠亚和") || currentPlayTypeInfo.getPlayTypeName().equals("综合") ||
                                    (currentPlayTypeInfo.getPlayTypeName().equals("定位胆") && currentPlayTypeRadioInfo.getPlayTypeRadioName().equals("手动倍数")) ||
                                    (currentLotteryInfo.getLotteryType().contains("快三") && (currentPlayTypeRadioInfo.getPlayTypeRadioName().equals("和值") || currentPlayTypeRadioInfo.getPlayTypeRadioName().equals("三不同和值")))) {
                                LinearLayout layout = getBetCircleButton(num, isWord);
                                flowLayout.addView(layout);
                            } else {
                                CircleButton button;
                                Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
                                if (pattern.matcher(num).matches()) {
                                    int number = Integer.valueOf(num);
                                    button = getCircleButton(String.valueOf(number), isWord);
                                } else {
                                    button = getCircleButton(num, isWord);
                                }
                                flowLayout.addView(button);
                            }
                        }
                    }

                    if (linearLayout == null) {
                        break;
                    }

                    if (linearLayout.findViewWithTag("quickSelect") != null) {
                        final FlowLayout layout = (FlowLayout) linearLayout.findViewWithTag("ballLayout");

                        TextView tvPlayNum = (TextView) linearLayout.findViewWithTag("playNum");
                        tvPlayNum.setText(playConfig.get(i).getPrompt());

                        LinearLayout quickLayout = (LinearLayout) linearLayout.findViewWithTag("quickSelect");
                        for (int k = 0; k < quickLayout.getChildCount(); k++) {
                            TextView t = (TextView) quickLayout.getChildAt(k);
                            t.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (v.getTag().toString().equals("large")) {
                                        selectBig(v, layout.getChildCount());
                                    } else if (v.getTag().toString().equals("small")) {
                                        selectSmall(v, layout.getChildCount());
                                    } else if (v.getTag().toString().equals("all")) {
                                        selectAll(v);
                                    } else if (v.getTag().toString().equals("single")) {
                                        selectOdd(v);
                                    } else if (v.getTag().toString().equals("double")) {
                                        selectEven(v);
                                    } else if (v.getTag().toString().equals("clear")) {
                                        selectNone(v);
                                    }
                                    setQuickChooseBg(v);
                                }
                            });
                        }
                    }

                    //选号区域
                    FlowLayout ballLayout = (FlowLayout) linearLayout.findViewWithTag("ballLayout");
                    ballLayout.setId(i);
                    for (int j = 0; j < ballLayout.getChildCount(); j++) {
                        View view = ballLayout.getChildAt(j);

                        if (view instanceof CircleButton) {
                            CircleButton ball = (CircleButton) view;
                            if (currentPlayTypeInfo.getPlayTypeName().equals("三同号通选") || currentPlayTypeInfo.getPlayTypeName().equals("三连号通选")) {
                                ball.setWord(true);
                            }
                            ball.setOnSelectedChangeListener(new CircleButton.OnSelectedChangeListener() {
                                @Override
                                public void onChange(View v) {
                                    if (currentPlayTypeInfo.getPlayTypeName().equals("任选胆拖")) {
                                        processTowedLogics((CircleButton) v);
                                    }
                                    if (currentPlayTypeRadioInfo.getPlayTypeRadioName().equals("胆拖选号")) {
                                        processTowedLogics((CircleButton) v);
                                    }
                                    if (currentPlayTypeRadioInfo.getPlayTypeRadioName().equals("前三组选胆拖") ||
                                            currentPlayTypeRadioInfo.getPlayTypeRadioName().equals("前二组选胆拖")) {
                                        processTowedLogics((CircleButton) v);
                                    }
                                    if (currentPlayTypeRadioInfo.getPlayTypeRadioName().equals("二同号单选")) {
                                        processETHDXLogics((CircleButton) v);
                                    }
                                    // 快三的大小和單雙要能多選
                                    if ((currentLotteryID != 18 && currentPlayTypeInfo.getPlayTypeName().equals("大小"))
                                            || (currentLotteryID != 18 && currentPlayTypeInfo.getPlayTypeName().equals("单双"))
                                            || currentPlayTypeInfo.getPlayTypeName().equals("龙虎斗")
                                            || currentPlayTypeRadioInfo.getPlayTypeRadioName().equals("组选包胆")) {
                                        processPK10SingleSelectLogics((CircleButton) v);
                                    }
                                    calculateTotalAmount();
                                }
                            });
                        } else if (view instanceof RelativeLayout) {
                            RelativeLayout relativeLayout = (RelativeLayout) view;
                            for (int k = 0; k < relativeLayout.getChildCount(); k++) {
                                View buttonView = relativeLayout.getChildAt(k);

                                if (buttonView instanceof CircleButton) {
                                    CircleButton ball = (CircleButton) buttonView;
                                    ball.setOnSelectedChangeListener(new CircleButton.OnSelectedChangeListener() {
                                        @Override
                                        public void onChange(View v) {
                                            calculateTotalAmount();
                                        }
                                    });
                                }
                            }
                        } else if (view instanceof LinearLayout) {
                            LinearLayout gunYaSumLayout = (LinearLayout) view;
                            final View buttonView = gunYaSumLayout.getChildAt(0);
                            final View textView = gunYaSumLayout.getChildAt(1);

                            if (textView instanceof EditText && buttonView instanceof CircleButton) {
                                final CircleButton ball = (CircleButton) buttonView;
                                final EditText editText = (EditText) textView;

                                final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                ball.setOnSelectedChangeListener(new CircleButton.OnSelectedChangeListener() {
                                    @Override
                                    public void onChange(View v) {
                                        if (ball.getIsSelected()) {
                                            closeEditText();
                                            ball.setIsSelected(true);
                                            editText.setEnabled(true);
                                            editText.setBackgroundResource(R.drawable.rect_manual);
                                            editText.requestFocus();

                                            if (imm.isActive(editText)) {
                                                imm.showSoftInput(getCurrentFocus(), InputMethodManager.SHOW_FORCED);
                                            }
                                        } else {
                                            closeEditText();
                                            editText.setText("");
                                            editText.setBackgroundResource(R.drawable.rect_manual_enable);

                                            if (!imm.isActive(editText)) {
                                                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                                            }
                                        }

                                        calculateTotalAmount();
                                    }
                                });

                                editText.setOnTouchListener(new View.OnTouchListener() {
                                    @Override
                                    public boolean onTouch(View v, MotionEvent event) {
                                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                                            closeEditText();

                                            editText.setBackgroundResource(R.drawable.rect_manual);
                                            ball.setIsSelected(true);
                                            editText.requestFocus();

                                            if (imm.isActive(editText)) {
                                                imm.showSoftInput(getCurrentFocus(), InputMethodManager.SHOW_FORCED);
                                            }
                                        }
                                        return false;
                                    }
                                });

                                editText.addTextChangedListener(new TextWatcher() {
                                    @Override
                                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                    }

                                    @Override
                                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                                    }

                                    @Override
                                    public void afterTextChanged(Editable editable) {
                                        String temp = editable.toString();
                                        int posDot = temp.indexOf(".");

                                        int max = 4;
                                        if (posDot > 0) {
                                            if (temp.length() - posDot - 1 > max) {
                                                editable.delete(posDot + max + 1, posDot + max + 2);
                                            }
                                        }

                                        if (temp.equals(".")) {
                                            return;
                                        }

                                        calculateTotalAmount();
                                    }
                                });

                                editText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
                                    @Override
                                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                                            closeEditText();

                                            if (!imm.isActive(editText)) {
                                                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                                            }
                                        }
                                        return false;
                                    }
                                });

                                editText.setOnKeyListener(new View.OnKeyListener() {
                                    @Override
                                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                                        if (keyCode == KeyEvent.KEYCODE_DPAD_UP || keyCode == KeyEvent.KEYCODE_DPAD_DOWN ||
                                                keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                                            return true;
                                        }

                                        return false;
                                    }
                                });
                            } else if (textView instanceof TextView && buttonView instanceof CircleButton) {
                                final CircleButton ball = (CircleButton) buttonView;
                                ball.setOnSelectedChangeListener(new CircleButton.OnSelectedChangeListener() {
                                    @Override
                                    public void onChange(View v) {
                                        calculateTotalAmount();
                                    }
                                });
                            }
                        }
                    }
                    layoutContainer.addView(linearLayout);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setQuickChooseBg(View v) {
        LinearLayout linearLayout = (LinearLayout) v.getParent();
        for (int i = 0; i < linearLayout.getChildCount(); i++) {
            TextView tv = (TextView) linearLayout.getChildAt(i);
            tv.setBackgroundResource(R.color.transparent);
        }

        if (v.getTag().equals("clear")) {
            v.setBackgroundResource(R.mipmap.icon_quick_clear);
        } else {
            v.setBackgroundResource(R.mipmap.icon_quick_choose);
        }
    }

    private void clearQuickChosseBg(View v) {
        LinearLayout linearLayout = (LinearLayout) v;
        for (int i = 0; i < linearLayout.getChildCount(); i++) {
            View vv = linearLayout.getChildAt(i);
            vv.setBackgroundResource(R.color.transparent);
        }
    }

    private void processPK10SingleSelectLogics(CircleButton button) {
        FlowLayout flowLayout = (FlowLayout) button.getParent();
        int count = flowLayout.getChildCount();
        boolean selected = button.getIsSelected();
        for (int i = 0; i < count; i++) {
            View v = flowLayout.getChildAt(i);
            if (v instanceof CircleButton) {
                CircleButton b = (CircleButton) v;
                b.setIsSelected(false);
            }
        }
        button.setIsSelected(selected);
    }

    private void processETHDXLogics(CircleButton button) {
        if (!isSingle()) {
            FlowLayout flowLayout = (FlowLayout) button.getParent();
            int index = flowLayout.getId();
            LinearLayout linearLayout = (LinearLayout) layoutContainer.getChildAt(1 - index);
            FlowLayout firstFlowLayout = (FlowLayout) linearLayout.findViewWithTag("ballLayout");
            for (int k = 0; k < firstFlowLayout.getChildCount(); k++) {
                CircleButton b = (CircleButton) firstFlowLayout.getChildAt(k);
                if (b.getText().charAt(0) == button.getText().charAt(0))
                    b.setIsSelected(false);
            }
        }
    }

    //十一选五胆拖 和 快三胆拖选号
    private void processTowedLogics(CircleButton button) {
        if (!isSingle()) {
            FlowLayout flowLayout = (FlowLayout) button.getParent();
            int index = flowLayout.getId();
            if (index == 0) {
                int selectedCount = 0;
                int count = flowLayout.getChildCount();
                for (int i = 0; i < count; i++) {
                    CircleButton ball = (CircleButton) flowLayout.getChildAt(i);
                    if (ball.getIsSelected()) {
                        selectedCount++;
                    }
                }
                if (selectedCount > getMaxNumsInBet() - 1) {
                    if (lastBold != null) {
                        lastBold.setIsSelected(false);
                    }
                }
                lastBold = button;
                //找到拖码
                LinearLayout linearLayout = (LinearLayout) layoutContainer.getChildAt(1);
                FlowLayout secondFlowLayout = (FlowLayout) linearLayout.findViewWithTag("ballLayout");
                for (int k = 0; k < secondFlowLayout.getChildCount(); k++) {
                    CircleButton b = (CircleButton) secondFlowLayout.getChildAt(k);
                    if (b.getText().equals(button.getText())) {
                        b.setIsSelected(false);
                    }
                }
            } else {
                LinearLayout linearLayout = (LinearLayout) layoutContainer.getChildAt(0);
                FlowLayout firstFlowLayout = (FlowLayout) linearLayout.findViewWithTag("ballLayout");
                for (int k = 0; k < firstFlowLayout.getChildCount(); k++) {
                    CircleButton b = (CircleButton) firstFlowLayout.getChildAt(k);
                    if (b.getText().equals(button.getText())) {
                        b.setIsSelected(false);
                    }
                }
            }
        }
    }

    //十一选五任选胆拖
    private int getMaxNumsInBet() {

        if (currentLotteryInfo.getLotteryType().equals("江苏快三")) {
            if (currentPlayTypeInfo.getPlayTypeName().equals("三不同号"))
                return 3;
            else if (currentPlayTypeInfo.getPlayTypeName().equals("二不同号"))
                return 2;
        }

        Map<String, Integer> map = new HashMap<>();
        map.put("二中二", 1);
        map.put("三中三", 2);
        map.put("四中四", 3);
        map.put("五中五", 4);
        map.put("六中五", 5);
        map.put("七中五", 6);
        map.put("八中五", 7);
        map.put("前三组选胆拖", 15);
        map.put("前二组选胆拖", 16);
        map.put("任选二中二", 8);
        map.put("任选三中三", 9);
        map.put("任选四中四", 10);
        map.put("任选五中五", 11);
        map.put("任选六中五", 12);
        map.put("任选七中五", 13);
        map.put("任选八中五", 14);

        switch (map.get(currentPlayTypeRadioInfo.getPlayTypeRadioName())) {
            case 1:
            case 8:
            case 16:
                return 2;
            case 2:
            case 9:
            case 15:
                return 3;
            case 3:
            case 10:
                return 4;
            case 4:
            case 11:
                return 5;
            case 5:
            case 12:
                return 6;
            case 6:
            case 13:
                return 7;
            case 7:
            case 14:
                return 8;
            default:
                return 0;
        }
    }

    private void goSettle() {
        //calculateTotalAmount();
        int nums = Integer.parseInt(tvTotalNums.getText().toString());
        if (nums <= 0 && BaseApp.allSettleInfo.size() <= 0) {
            //Toast.makeText(LotteryActivity.this, "没有选择投注号码", Toast.LENGTH_SHORT).show();
            return;
        }
        if (nums <= 0) {
            return;
        }
        if (txtPrice.getText().toString().compareTo("") == 0) {
            MyToast.show(this, "投注金额不能为空!");
            return;
        }
        double price = 0.0;
        if (!txtPrice.getText().toString().isEmpty()) {
            try {
                price = Double.parseDouble(txtPrice.getText().toString());
            } catch (NumberFormatException e) {
                MyToast.show(this, "您的填写有误,请检查!");
            }
        }

        if (rebateSelectList.getSelectedItem() == null && !playtypename.equals("冠亚和") && !playtypename.equals("综合")) {
            MyToast.show(this, "奖金项不能为空!");
            getRebateSelectList();
            return;
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(String.valueOf(currentPlayTypeRadioInfo.getPlayTypeRadioID()), userSelectRebate);
        editor.commit();
        if (isSingle()) {
            if (tube != null) {
                if (lotteryType.contains("十一选五") || lotteryType.contains("PK拾")) {
                    //TODO
                    int max;
                    if (lotteryType.contains("十一选五")) {
                        max = 11;
                    } else {
                        max = 10;
                    }
                    try {
                        String ggs = "";
                        for (String s : groupOfNum) {
                            ggs += s + " ";
                            if (!s.equals(",") && (Integer.parseInt(s) > max || Integer.parseInt(s) <= 0)) {
                                MyToast.show(this, "不能包含 1~" + String.valueOf(max) + " 以外数字!");
                                return;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (formatErr) {
                        MyToast.show(this, "投注格式不正确!");
                        return;
                    }
                } else if (lotteryType.contains("快三")) {
                    for (String s : tube) {
                        if (playtypename.contains("三") || playradioname.contains("三")) {
                            if (s.length() != 3 || (s.charAt(0) == s.charAt(1) || s.charAt(1) == s.charAt(2) || s.charAt(0) == s.charAt(2))) {
                                MyToast.show(this, "投注格式不正确!");
                                return;
                            }
                        } else if (playtypename.contains("二同号") || playradioname.contains("二同号")) {
                            if (s.length() != 3 || (s.charAt(0) != s.charAt(1))) {
                                MyToast.show(this, "投注格式不正确!");
                                return;
                            }
                        } else if (playtypename.contains("二不同号") || playradioname.contains("二不同号")) {
                            if (s.length() != 2 || (s.charAt(0) == s.charAt(1))) {
                                MyToast.show(this, "投注格式不正确!");
                                return;
                            }
                        }
                    }
                } else {
                    for (String s : tube) {
                        if (playtypename.contains("五")) {
                            if (s.length() != 5) {
                                MyToast.show(this, "投注格式不正确!");
                                return;
                            }
                        } else if (playtypename.contains("四") || playradioname.contains("四")) {
                            if (s.length() != 4) {
                                MyToast.show(this, "投注格式不正确!");
                                return;
                            }
                        } else if (playtypename.contains("三") || playradioname.contains("三")) {
                            if (s.length() != 3) {
                                MyToast.show(this, "投注格式不正确!");
                                return;
                            }
                        } else if (playtypename.contains("二") || playradioname.contains("二")) {
                            if (s.length() != 2) {
                                MyToast.show(this, "投注格式不正确!");
                                return;
                            }
                        }
                    }
                }
            }
        }
        //    double habitRebatePro = Double.parseDouble(rebateSelectList.getSelectedItem().toString().split("-")[1].replace("%", "")) / 100;
        double habitRebatePro = 0, prizeMoney = 0;

        try {
            if (!playtypename.equals("冠亚和") && !playtypename.equals("综合")) {
                habitRebatePro = Double.parseDouble(rebateValues.get(rebateSelectList.getSelectedItemPosition()));

                String rebateStr = rebateSelectList.getSelectedItem().toString();
                if (rebateStr.contains("~")) {
                    for (String str : rebateStr.split("~")) {
                        prizeMoney = Double.parseDouble(str.split("-")[0]);
                    }
                } else {
                    prizeMoney = Double.parseDouble(rebateStr.split("-")[0]);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Bundle bundle = new Bundle();
        PlaceOrderInfo order = new PlaceOrderInfo();
        order.setLotteryID(currentLotteryID);
        order.setLotteryName(currentLotteryInfo.getLotteryType());
        order.setPlayTypeID(currentPlayTypeInfo.getPlayTypeID());
        order.setPlayTypeName(currentPlayTypeInfo.getPlayTypeName());
        order.setPlayTypeRadioID(currentPlayTypeRadioInfo.getPlayTypeRadioID());
        if (isSingle()) {
            order.setPlayTypeRadioName(playradioname);
        } else {
            order.setPlayTypeRadioName(currentPlayTypeRadioInfo.getPlayTypeRadioName());
        }
        order.setQty(nums);
        order.setPrice(price);
        order.setRebatePro(habitRebatePro);
        order.setRebateProMoney(prizeMoney);
        order.setHabitRebatePro(habitRebatePro);
        order.setPlayMode(mPlayMode);

        if (!preCheck(order)) {
            return;
        }

        SelectedBallInfo selectedBallInfo = getSelectedBall(true);
        List<String> selectedBall = selectedBallInfo.getSelectedBall();
        List<String> localgroupofum = new ArrayList<>();
        localgroupofum.addAll(groupOfNum);

        if (nums > 0) {
            if (isSingle()) {
                LotteryUtil.addSettleInfoToLocalCache(localgroupofum, getSelectedUnit(), 0.0, totalNums, playtypename, playradioname);
            } else {
                if (selectedBall != null) {
                    if (selectedBall.size() != 0) {
                        LotteryUtil.addSettleInfoToLocalCache(selectedBall, getSelectedUnit(), selectedBallInfo.getBet(), totalNums, playtypename, playradioname);
                    }
                }
            }
        }

        bundle.putSerializable("PlaceOrderInfo", order);

        IntentUtil.redirectToNextActivity(LotteryActivity.this, LotterySettleActivity.class, bundle);
        clearAll();
    }

    private void clearAll() {
        if (isSingle()) {
            LinearLayout linearLayout = (LinearLayout) layoutContainer.getChildAt(0);
            ((EditText) linearLayout.findViewById(R.id.lottery_input)).setText("");

            LinearLayout optionLayout = (LinearLayout) linearLayout.findViewById(R.id.layoutOptionContainer);
            if (optionLayout.getVisibility() == View.VISIBLE) {
                ((CheckBox) optionLayout.findViewById(R.id.checkBoxOneSigle)).setChecked(true);
                ((CheckBox) optionLayout.findViewById(R.id.checkBoxTenSigle)).setChecked(true);
                ((CheckBox) optionLayout.findViewById(R.id.checkBoxHundredSigle)).setChecked(true);
                ((CheckBox) optionLayout.findViewById(R.id.checkBoxThousandSigle)).setChecked(true);
                ((CheckBox) optionLayout.findViewById(R.id.checkBoxTenThousandSigle)).setChecked(true);

                if (currentPlayTypeInfo.getPlayTypeName().equals("任二") || currentPlayTypeRadioInfo.getPlayTypeRadioName().contains("任选二")) {
                    ((CheckBox) optionLayout.findViewById(R.id.checkBoxHundredSigle)).setChecked(false);
                    ((CheckBox) optionLayout.findViewById(R.id.checkBoxThousandSigle)).setChecked(false);
                    ((CheckBox) optionLayout.findViewById(R.id.checkBoxTenThousandSigle)).setChecked(false);
                } else if (currentPlayTypeInfo.getPlayTypeName().equals("任三") || currentPlayTypeRadioInfo.getPlayTypeRadioName().contains("任选三")) {
                    ((CheckBox) optionLayout.findViewById(R.id.checkBoxThousandSigle)).setChecked(false);
                    ((CheckBox) optionLayout.findViewById(R.id.checkBoxTenThousandSigle)).setChecked(false);
                } else if (currentPlayTypeInfo.getPlayTypeName().equals("任四") || currentPlayTypeRadioInfo.getPlayTypeRadioName().contains("任选四")) {
                    ((CheckBox) optionLayout.findViewById(R.id.checkBoxTenThousandSigle)).setChecked(false);
                }
            }
        } else {
            int layoutOptionCount = layoutOptionContainer.getChildCount();
            for (int i = 0; i < layoutOptionCount; i++) {
                LinearLayout optionLayout = (LinearLayout) layoutOptionContainer.getChildAt(i);
                if (optionLayout.getVisibility() == View.VISIBLE) {
                    ((CheckBox) optionLayout.findViewById(R.id.checkBoxOne)).setChecked(true);
                    ((CheckBox) optionLayout.findViewById(R.id.checkBoxTen)).setChecked(true);
                    ((CheckBox) optionLayout.findViewById(R.id.checkBoxHundred)).setChecked(true);
                    ((CheckBox) optionLayout.findViewById(R.id.checkBoxThousand)).setChecked(true);
                    ((CheckBox) optionLayout.findViewById(R.id.checkBoxTenThousand)).setChecked(true);

                    if (currentPlayTypeInfo.getPlayTypeName().equals("任二") && !currentPlayTypeRadioInfo.getPlayTypeRadioName().equals("直选复式")) {
                        ((CheckBox) optionLayout.findViewById(R.id.checkBoxHundred)).setChecked(false);
                        ((CheckBox) optionLayout.findViewById(R.id.checkBoxThousand)).setChecked(false);
                        ((CheckBox) optionLayout.findViewById(R.id.checkBoxTenThousand)).setChecked(false);
                    } else if (currentPlayTypeInfo.getPlayTypeName().equals("任三") && !currentPlayTypeRadioInfo.getPlayTypeRadioName().equals("直选复式")) {
                        ((CheckBox) optionLayout.findViewById(R.id.checkBoxThousand)).setChecked(false);
                        ((CheckBox) optionLayout.findViewById(R.id.checkBoxTenThousand)).setChecked(false);
                    } else if (currentPlayTypeInfo.getPlayTypeName().equals("任四") && !currentPlayTypeRadioInfo.getPlayTypeRadioName().equals("直选复式")) {
                        ((CheckBox) optionLayout.findViewById(R.id.checkBoxTenThousand)).setChecked(false);
                    }
                }
            }

            int countL = layoutContainer.getChildCount();
            for (int k = 0; k < countL; k++) {
                LinearLayout line = (LinearLayout) layoutContainer.getChildAt(k);
                LinearLayout l = (LinearLayout) line.findViewWithTag("ballLinearLayout");
                FlowLayout ballLayout = (FlowLayout) l.findViewWithTag("ballLayout");
                int count = ballLayout.getChildCount();

                for (int i = 0; i < count; i++) {
                    View view = ballLayout.getChildAt(i);
                    if (view instanceof CircleButton) {
                        CircleButton ball = (CircleButton) view;
                        ball.setIsSelected(false);
                    } else if (view instanceof LinearLayout) {
                        LinearLayout layout = (LinearLayout) view;

                        View buttonView = layout.getChildAt(0);
                        if (buttonView instanceof CircleButton) {
                            CircleButton ball = (CircleButton) buttonView;
                            ball.setIsSelected(false);
                        }

                        View textView = layout.getChildAt(1);
                        if (textView instanceof EditText) {
                            EditText editText = (EditText) textView;
                            editText.setText("");
                            editText.setBackgroundResource(R.drawable.rect_manual_enable);
                        }
                    } else if (view instanceof RelativeLayout) {
                        RelativeLayout relativeLayout = (RelativeLayout) view;
                        for (int j = 0; j < relativeLayout.getChildCount(); ++j) {
                            View buttonView = relativeLayout.getChildAt(j);

                            if (buttonView instanceof CircleButton) {
                                CircleButton ball = (CircleButton) buttonView;
                                ball.setIsSelected(false);
                            }
                        }
                    }
                }

                LinearLayout quickLayout = (LinearLayout) line.findViewWithTag("quickSelect");
                if (quickLayout == null) {
                    TestUtil.print("quicklayout is null");
                    continue;
                }
                clearQuickChosseBg(quickLayout);

            }
            calculateTotalAmount();
        }
    }

    private int singleCalculateTotalAmount() {
        int nums = 0;

        if (isSingle()) {
            int units = getSelectedUnit().size();

            if (mPlayMode == PLAY_MODE.CLASSIC && !playtypename.equals("任选")) {
                nums = LotteryCalculator.calculate(currentLotteryInfo.getLotteryType(), currentPlayTypeInfo.getPlayTypeName(), currentPlayTypeRadioInfo.getPlayTypeRadioName(), editNumbers);
            } else {
                nums = LotteryManager.calculate(currentLotteryInfo.getLotteryID(), currentPlayTypeInfo.getPlayTypeName(), currentPlayTypeRadioInfo.getPlayTypeRadioName(), editNumbers, units);
            }
        }

        return nums;
    }

    private void calculateTotalAmount() {

        if (txtPrice.getText().toString().trim().compareTo("") == 0) {
            MyToast.show(LotteryActivity.this, "请输入投注金额！");
            return;
        }

        SelectedBallInfo selectedBallInfo = getSelectedBall(false);
        List<String> selectedBall = selectedBallInfo.getSelectedBall();
        double bet = selectedBallInfo.getBet();
        int units = getSelectedUnit().size();
        int nums = 0;

        try {
            price = Double.parseDouble(txtPrice.getText().toString());
        } catch (NumberFormatException n) {
            MyToast.show(this, "您所输入的金额有误,请重新输入!");
        }

        if (selectedBall != null) {
            //后加
            if (selectedBall.size() != 0) {
                TestUtil.print("calculate " + nums + "," + currentLotteryInfo.getLotteryType() + "," + currentPlayTypeInfo.getPlayTypeName() + "," + currentPlayTypeRadioInfo.getPlayTypeRadioName());
                if (mPlayMode == PLAY_MODE.CLASSIC) {
                    nums = LotteryCalculator.calculate(currentLotteryInfo.getLotteryType(), currentPlayTypeInfo.getPlayTypeName(), currentPlayTypeRadioInfo.getPlayTypeRadioName(), selectedBall);
                } else {
                    nums = LotteryManager.calculate(currentLotteryInfo.getLotteryID(), currentPlayTypeInfo.getPlayTypeName(), currentPlayTypeRadioInfo.getPlayTypeRadioName(), selectedBall, units);
                }
                TestUtil.print("calculate " + nums + "," + currentLotteryInfo.getLotteryType() + "," + currentPlayTypeInfo.getPlayTypeName() + "," + currentPlayTypeRadioInfo.getPlayTypeRadioName());
                for (String s : selectedBall) {
                    TestUtil.print("calculate " + s);
                }

                totalNums = nums;
                tvTotalNums.setText(String.valueOf(totalNums));
                amount = LotteryUtil.getTotalAmount(totalNums, price);
            }
        } else {
            if (isSingle()) {
                if (mPlayMode == PLAY_MODE.CLASSIC) {
                    nums = LotteryCalculator.calculate(currentLotteryInfo.getLotteryType(), currentPlayTypeInfo.getPlayTypeName(), currentPlayTypeRadioInfo.getPlayTypeRadioName(), editNumbers);
                } else {
                    nums = LotteryManager.calculate(currentLotteryInfo.getLotteryID(), currentPlayTypeInfo.getPlayTypeName(), currentPlayTypeRadioInfo.getPlayTypeRadioName(), editNumbers, units);
                }

                totalNums = nums;
                tvTotalNums.setText(String.valueOf(totalNums));
                amount = nums * price;
            }
        }
        if (amount > 0) {
            tvTotalAmount.setText(StringUtil.formatDoubleWith4Point(amount));
        } else {
            tvTotalAmount.setText(String.valueOf(amount));
        }

        int visibility = nums > 0 ? View.VISIBLE : View.GONE;
        if (nums > 0) {
            ll_ok.setBackgroundResource(R.drawable.bg_lottery_btn_settle);
            ll_ok.setEnabled(true);
        } else {
            ll_ok.setBackgroundResource(R.mipmap.lottery_settle_disabled);
            ll_ok.setEnabled(false);
        }
        ll_bottom.setVisibility(visibility);
    }

    private SelectedBallInfo getSelectedBall(boolean isNextLotterySettle) {
        SelectedBallInfo info = new SelectedBallInfo();

        if (!isSingle()) {
            int count = layoutContainer.getChildCount();
            List<String> selectedNumbers = new ArrayList<>();
            double betTotal = 0.0;

            for (int k = 0; k < count; k++) {
                LinearLayout line = (LinearLayout) layoutContainer.getChildAt(k);
                TextView playNum = (TextView) line.findViewWithTag("playNum");
                LinearLayout ballLinearLayout = (LinearLayout) line.findViewWithTag("ballLinearLayout");
                FlowLayout ballLayout = (FlowLayout) ballLinearLayout.findViewWithTag("ballLayout");
                String lineSelected = "";

                for (int i = 0; i < ballLayout.getChildCount(); i++) {
                    View view = ballLayout.getChildAt(i);

                    if (view instanceof CircleButton) {
                        CircleButton ball = (CircleButton) view;
                        if (ball.getIsSelected()) {
                            if (!StringUtil.isEmpty(ball.getText().toString())) {
                                lineSelected += getLineSelectedStr(isNextLotterySettle, playNum.getText().toString(), ball.getText().toString(), "1");
                            } else {
                                lineSelected += ball.getTag().toString() + ",";
                            }

                            if (playtypename.equals("定位胆") && playradioname.equals("定倍下单")) {
                                betTotal += 1;
                            }
                        }
                    } else if (view instanceof RelativeLayout) {
                        RelativeLayout relativeLayout = (RelativeLayout) view;
                        for (int j = 0; j < relativeLayout.getChildCount(); ++j) {
                            View buttonView = relativeLayout.getChildAt(j);

                            if (buttonView instanceof CircleButton) {
                                CircleButton ball = (CircleButton) buttonView;
                                if (ball.getIsSelected()) {
                                    if (!StringUtil.isEmpty(ball.getText().toString())) {
                                        lineSelected += ball.getText().toString() + ",";
                                    } else {
                                        lineSelected += ball.getTag().toString() + ",";
                                    }
                                }
                            }
                        }
                    } else if (view instanceof LinearLayout) {
                        LinearLayout linearLayout = (LinearLayout) view;

                        final View buttonView = linearLayout.getChildAt(0);
                        final View textView = linearLayout.getChildAt(1);

                        if (buttonView instanceof CircleButton && textView instanceof EditText) {
                            CircleButton ball = (CircleButton) buttonView;
                            EditText editText = (EditText) textView;
                            String betStr = editText.getText().toString();

                            double bet = 0.0;
                            if (!betStr.isEmpty() && !betStr.equals(".")) {
                                bet = Double.parseDouble(betStr);
                            }

                            if (ball.getIsSelected() && bet > 0.0) {
                                lineSelected += getLineSelectedStr(isNextLotterySettle, playNum.getText().toString(), ball.getText().toString(), betStr);
                                betTotal += bet;
                            }
                        } else if (buttonView instanceof CircleButton && textView instanceof TextView) {
                            CircleButton ball = (CircleButton) buttonView;

                            if (ball.getIsSelected()) {
                                lineSelected += getLineSelectedStr(isNextLotterySettle, playNum.getText().toString(), ball.getText().toString(), "1");
                                betTotal += 1;
                            }
                        }
                    }
                }

                if (lineSelected.length() > 0) {
                    lineSelected = lineSelected.substring(0, lineSelected.length() - 1);
                }

                if (count == 1 && lineSelected.trim().compareTo("") == 0) {
                    continue;
                }

                selectedNumbers.add(lineSelected);
            }

            info.setSelectedBall(selectedNumbers);
            info.setBet(betTotal);
        }

        return info;
    }

    private List<String> getSelectedUnit() {
        List<String> unitList = new ArrayList<>();
        CheckBox checkBoxOne;
        CheckBox checkBoxTen;
        CheckBox checkBoxHundred;
        CheckBox checkBoxThousand;
        CheckBox checkBoxTenThousand;

        if (isSingle()) {
            LinearLayout optionLayout = (LinearLayout) layoutContainer.getChildAt(0).findViewById(R.id.layoutOptionContainer);
            if (optionLayout.getVisibility() != View.VISIBLE) {
                return unitList;
            }

            checkBoxOne = (CheckBox) findViewById(R.id.checkBoxOneSigle);
            checkBoxTen = (CheckBox) findViewById(R.id.checkBoxTenSigle);
            checkBoxHundred = (CheckBox) findViewById(R.id.checkBoxHundredSigle);
            checkBoxThousand = (CheckBox) findViewById(R.id.checkBoxThousandSigle);
            checkBoxTenThousand = (CheckBox) findViewById(R.id.checkBoxTenThousandSigle);
        } else {
            if (layoutOptionContainer.getChildCount() <= 0) {
                return unitList;
            }

            checkBoxOne = (CheckBox) findViewById(R.id.checkBoxOne);
            checkBoxTen = (CheckBox) findViewById(R.id.checkBoxTen);
            checkBoxHundred = (CheckBox) findViewById(R.id.checkBoxHundred);
            checkBoxThousand = (CheckBox) findViewById(R.id.checkBoxThousand);
            checkBoxTenThousand = (CheckBox) findViewById(R.id.checkBoxTenThousand);
        }

        if (checkBoxTenThousand.isChecked()) {
            unitList.add("万");
        }
        if (checkBoxThousand.isChecked()) {
            unitList.add("千");
        }
        if (checkBoxHundred.isChecked()) {
            unitList.add("百");
        }
        if (checkBoxTen.isChecked()) {
            unitList.add("十");
        }
        if (checkBoxOne.isChecked()) {
            unitList.add("个");
        }

        return unitList;
    }

    private void getEditSelected() {
        LinearLayout linearLayout = (LinearLayout) layoutContainer.getChildAt(0);
        final EditText editText = (EditText) linearLayout.findViewById(R.id.lottery_input);
        editText.setHorizontallyScrolling(false);
        editText.setMaxLines(8);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                return false;
            }
        });
        TextView btn_prase = (TextView) linearLayout.findViewById(R.id.niantie);
        TextView btn_clear = (TextView) linearLayout.findViewById(R.id.qingkong);
        btn_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText("");
            }
        });
        btn_prase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager manager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                if (manager.hasPrimaryClip()) {
                    ClipData clip = manager.getPrimaryClip();
                    coerceToText = clip.getItemAt(0).coerceToText(getApplicationContext());
                    editText.setText(String.valueOf(editText.getText()) + coerceToText);
                    editText.setSelection(editText.getText().length());
                }
            }

        });
        //    if (playtypename.contains("任选")) {
        //         linearLayout.findViewById(R.id.check_group).setVisibility(View.VISIBLE);
        //   }
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                singleAfterTextChanged(s);
            }
        });
    }

    private LayoutInfo getLayoutData(int lotteryID, int playTypeID, int playTypeRadioID) {
        try {
            return new LotteryService().getLayoutInfo(lotteryID, playTypeID, playTypeRadioID, mPlayMode);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void selectAll(View view) {
        LinearLayout container = (LinearLayout) view.getParent().getParent().getParent();
        LinearLayout linearLayout = (LinearLayout) container.getChildAt(1);
        FlowLayout flowLayout = (FlowLayout) linearLayout.getChildAt(1);
        for (int i = 0; i < flowLayout.getChildCount(); i++) {
            CircleButton circleButton = (CircleButton) flowLayout.getChildAt(i);
            circleButton.setIsSelected(true);
        }

        calculateTotalAmount();
    }

    private void selectBig(View view, int ballNo) {
        LinearLayout container = (LinearLayout) view.getParent().getParent().getParent();
        LinearLayout linearLayout = (LinearLayout) container.getChildAt(1);
        FlowLayout flowLayout = (FlowLayout) linearLayout.getChildAt(1);
        int boundary = ballNo / 2 - 1;
        if (currentLotteryInfo.getLotteryType().contains("江苏快三"))
            boundary = 2;
        for (int i = 0; i < flowLayout.getChildCount(); i++) {
            CircleButton circleButton = (CircleButton) flowLayout.getChildAt(i);
            circleButton.setIsSelected(false);
            if (i > boundary) {
                circleButton.setIsSelected(true);
            }
        }

        calculateTotalAmount();
    }

    private void selectSmall(View view, int ballNo) {
        LinearLayout container = (LinearLayout) view.getParent().getParent().getParent();
        LinearLayout linearLayout = (LinearLayout) container.getChildAt(1);
        FlowLayout flowLayout = (FlowLayout) linearLayout.getChildAt(1);
        int boundary = ballNo / 2;
        if (currentLotteryInfo.getLotteryType().contains("江苏快三"))
            boundary = 3;
        for (int i = 0; i < flowLayout.getChildCount(); i++) {
            CircleButton circleButton = (CircleButton) flowLayout.getChildAt(i);
            circleButton.setIsSelected(false);
            if (i < boundary) {
                circleButton.setIsSelected(true);
            }
        }

        calculateTotalAmount();
    }

    private void selectOdd(View view) {
        LinearLayout container = (LinearLayout) view.getParent().getParent().getParent();
        LinearLayout linearLayout = (LinearLayout) container.getChildAt(1);
        FlowLayout flowLayout = (FlowLayout) linearLayout.getChildAt(1);
        for (int i = 0; i < flowLayout.getChildCount(); i++) {
            CircleButton circleButton = (CircleButton) flowLayout.getChildAt(i);
            String text = circleButton.getText().toString();

            if (text.equals("0")) {
                circleButton.setIsSelected(false);
            } else if (Integer.parseInt(text) % 2 != 0) {
                circleButton.setIsSelected(true);
            } else {
                circleButton.setIsSelected(false);
            }
        }

        calculateTotalAmount();
    }

    private void selectEven(View view) {
        LinearLayout container = (LinearLayout) view.getParent().getParent().getParent();
        LinearLayout linearLayout = (LinearLayout) container.getChildAt(1);
        FlowLayout flowLayout = (FlowLayout) linearLayout.getChildAt(1);
        for (int i = 0; i < flowLayout.getChildCount(); i++) {
            CircleButton circleButton = (CircleButton) flowLayout.getChildAt(i);
            String text = circleButton.getText().toString();

            if (text.equals("0")) {
                circleButton.setIsSelected(true);
            } else if (Integer.parseInt(text) % 2 != 0) {
                circleButton.setIsSelected(false);
            } else {
                circleButton.setIsSelected(true);
            }
        }

        calculateTotalAmount();
    }

    private void selectNone(View view) {
        LinearLayout container = (LinearLayout) view.getParent().getParent().getParent();
        LinearLayout linearLayout = (LinearLayout) container.getChildAt(1);
        FlowLayout flowLayout = (FlowLayout) linearLayout.getChildAt(1);
        for (int i = 0; i < flowLayout.getChildCount(); i++) {
            CircleButton circleButton = (CircleButton) flowLayout.getChildAt(i);
            String text = circleButton.getText().toString();

            circleButton.setIsSelected(false);
        }

        calculateTotalAmount();
    }

    private void getBallRebateList() {
        MyAsyncTask<List<BallRebates>> task = new MyAsyncTask<List<BallRebates>>(LotteryActivity.this) {

            @Override
            public List<BallRebates> callService() throws IOException, JsonParseException, BizException, ServiceException {
                RebateQueryCriteria query = new RebateQueryCriteria(currentPlayTypeInfo.getPlayTypeID(), currentPlayTypeInfo.getPlayTypeName(), currentPlayTypeRadioInfo.getPlayTypeRadioName());
                return new LotteryService().getBallRebateList(query, currentLotteryID);
            }

            @Override
            public void onLoaded(List<BallRebates> paramT) throws Exception {
                if (paramT != null && !paramT.isEmpty()) {
                    ballRebatesList = paramT;
                    setKeyRebate();
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

    private void getRebateSelectList() {
        if (currentPlayTypeInfo == null || currentPlayTypeRadioInfo == null) {
            return;
        }

        rebateValues = new ArrayList<>();
        mSelectListItemList.clear();

        mIsError = false;
        getRebateTask = new MyAsyncTask<List<SelectListItem>>(LotteryActivity.this) {
            @Override
            public List<SelectListItem> callService() throws IOException, JsonParseException, BizException, ServiceException {
                if (mPlayMode == PLAY_MODE.CLASSIC) {
                    RebateQueryCriteria query = new RebateQueryCriteria(currentPlayTypeInfo.getPlayTypeID(), currentPlayTypeInfo.getPlayTypeName(), currentPlayTypeRadioInfo.getPlayTypeRadioName());
                    return new LotteryService().getRebateSelectList(query, currentLotteryID);
                } else {
                    NewRebateQueryCriteria query = new NewRebateQueryCriteria(currentPlayTypeInfo.getPlayTypeID(), currentPlayTypeRadioInfo.getPlayTypeRadioID(), currentPlayTypeInfo.getPlayTypeName(), currentPlayTypeRadioInfo.getPlayTypeRadioName());
                    return new LotteryService().getNewRebateSelectList(query, currentLotteryID);
                }
            }

            @Override
            public void onLoaded(List<SelectListItem> result) throws Exception {
                if (LotteryActivity.this == null || LotteryActivity.this.isFinishing()) {
                    return;
                }

                if (!mIsError) {
                    userSelectRebate = sharedPreferences.getInt(String.valueOf(currentPlayTypeRadioInfo.getPlayTypeRadioID()), 0);
                    List<String> list = new ArrayList<>();
                    rebateValues = new ArrayList<>();

                    mSelectListItemList.clear();
                    mSelectListItemList = result;
                    Collections.sort(mSelectListItemList, new RebateSortComparator());

                    int selectedIndex = mSelectListItemList.size() - 1;
                    int index = 0;
                    for (SelectListItem item : mSelectListItemList) {
                        index++;
                        rebateValues.add(item.getValue());

                        if (item.getText() != null) {
                            list.add(item.getText());
                        }

                        if (item.getSelected() != null) {
                            if (item.getSelected()) {
                                selectedIndex = index - 1;
                            }
                        }
                    }
                    if (currentLotteryID == 18 && ((currentPlayTypeRadioInfo.getPlayTypeRadioID() == 624 && currentPlayTypeRadioInfo.getPlayTypeRadioName().equals("和值")) ||
                            (currentPlayTypeRadioInfo.getPlayTypeRadioID() == 615 && currentPlayTypeRadioInfo.getPlayTypeRadioName().equals("三不同和值")))) {
                        getBallRebateList();
                    } else {
                        setKeyRebate();
                    }

                    String[] mItems = list.toArray(new String[list.size()]);
                    ArrayAdapter<String> _Adapter = new ArrayAdapter<>(LotteryActivity.this, android.R.layout.simple_spinner_item, mItems);
                    _Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    _Adapter.notifyDataSetChanged();
                    rebateSelectList.setAdapter(_Adapter);
                    //rebateSelectList.setSelection(selectedIndex, true);
                    rebateSelectList.setSelection(userSelectRebate, true);
                    rebateSelectList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            userSelectRebate = position;
                            setKeyRebate();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                } else {
                    BaseApp.changeUrl(LotteryActivity.this, new BaseApp.OnChangeUrlListener() {
                        @Override
                        public void changeSuccess() {
                            getRebateSelectList();
                        }

                        @Override
                        public void changeFail() {
                        }
                    });
                }
            }
        };
        getRebateTask.setOnError(new MyAsyncTask.OnError() {

            @Override
            public void handleError(Exception e) {
                mIsError = true;
            }
        });
        getRebateTask.executeTask();
    }

    private void setSelectedBall(List<String> selectedBall) {
        int countL = layoutContainer.getChildCount();
        for (int k = 0; k < countL; k++) {
            LinearLayout line = (LinearLayout) layoutContainer.getChildAt(k);
            LinearLayout l = (LinearLayout) line.getChildAt(1);
            FlowLayout ballLayout = (FlowLayout) l.getChildAt(1);
            int count = ballLayout.getChildCount();
            if (k < selectedBall.size()) {
                String balls = selectedBall.get(k);

                for (int i = 0; i < count; i++) {
                    CircleButton ball = (CircleButton) ballLayout.getChildAt(i);
                    if (balls.contains(ball.getText().toString())) {
                        ball.setIsSelected(true);
                    }
                }
            }
        }
    }

    public void onEvent(Boolean b) {
        dragLayout.setTouchMode(b);
    }

    private TextWatcher textWatcher = new TextWatcher() {

        @Override
        public void afterTextChanged(Editable s) {

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            try {
                if (txtPrice.getText().toString().trim().compareTo("") == 0) {
                    tvTotalAmount.setText(StringUtil.formatDoubleWith4Point(0.00));
                    return;
                }

                if (isSingle()) {
                    if (mSigleEditable != null) {
                        singleAfterTextChanged(mSigleEditable);
                    }
                } else {
                    calculateTotalAmount();
                }
            } catch (Exception e) {
                tvTotalAmount.setText(StringUtil.formatDoubleWith4Point(0.00));
            }

        }
    };

    private boolean preCheck(PlaceOrderInfo orderInfo) {
        SysSettings settings = null;
        try {
            settings = new LotteryService().getSysSettings(PLAY_MODE.CLASSIC);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (settings == null)
            return true;
        double minOneBetMoney = settings.getMinOneBetMoney();
        double maxOneBetMoney = settings.getMaxOneBetMoney();
        double maxBonusMoney = settings.getMaxBonusMoney();
        String playTypeName = orderInfo.getPlayTypeName();
        String playTypeRadioName = orderInfo.getPlayTypeRadioName();
        if (playTypeName.equals("五星") || playTypeName.equals("四星") || (playTypeName.equals("任选") && playTypeRadioName.indexOf("任选四") >= 0)) {
            minOneBetMoney = 0.001;
        }
        BigDecimal bd = new BigDecimal(String.valueOf(orderInfo.getPrice()));
        if (bd.scale() > 3) {
            Toast.makeText(this, "投注单价最多支持3位小数", Toast.LENGTH_LONG).show();
            return false;
        }
        double prizeMoney = orderInfo.getRebateProMoney();
        if ((prizeMoney / 2) * orderInfo.getPrice() > maxBonusMoney) {
            Toast.makeText(this, "不能超过理论最大中奖金额" + maxBonusMoney + "，请修改投注方案", Toast.LENGTH_LONG).show();
            return false;
        }
        if (orderInfo.getPrice() > maxOneBetMoney || orderInfo.getPrice() < minOneBetMoney) {
            Toast.makeText(this, "投注单价须介于" + minOneBetMoney + "和" + maxOneBetMoney + "之间", Toast.LENGTH_LONG).show();
            return false;
        }
        TestUtil.print("投注数：" + orderInfo.getQty() + ", " + settings.getMaxBetCount());
        if (orderInfo.getQty() > settings.getMaxBetCount()) {
            Toast.makeText(this, "总投注数不能大于" + settings.getMaxBetCount(), Toast.LENGTH_LONG).show();
            return false;
        }

        if (!playTypeName.equals("五星") && !playTypeName.equals("四星") && !playTypeName.equals("任选")) {
            if (bd.scale() > 2) {
                Toast.makeText(this, "投注单价不能超过两位小数", Toast.LENGTH_LONG).show();
                return false;
            }
        }
        return true;
    }

    private void singleAfterTextChanged(Editable s) {
        editNumbers.clear();
        checkIfDuplicate.clear();
        groupOfNum.clear();
        mSigleEditable = s;
        formatErr = false;

        String text = mSigleEditable.toString();
        text = text.replaceAll("[, ;\\\n]", ",");
        String splittag = ",";
        totalNums = 0;

        StringBuffer finalSelectedBuffer = new StringBuffer();
        tube = text.split(splittag);
        if (mPlayMode == PLAY_MODE.EXPERT && (playradioname.contains("组三") || playradioname.contains("组六") || playradioname.contains("组选"))) {
            // 不限順序處理
            int fixedSize = 1;
            String fomatStr = "%d";
            if (lotteryType.contains("十一选五") || lotteryType.contains("PK")) {
                fixedSize = 2;
                fomatStr = "%02d";
            }

            for (String tubeStr : tube) {
                if (!tubeStr.isEmpty()) {
                    String[] tmp = Iterables.toArray(Splitter.fixedLength(fixedSize).split(tubeStr), String.class);

                    List<Integer> buffer = new ArrayList<>();
                    for (String tmpStr : tmp) {
                        buffer.add(Integer.parseInt(tmpStr));
                    }
                    Collections.sort(buffer);

                    String numStr = "";
                    for (Integer index : buffer) {
                        numStr += String.format(fomatStr, index);
                    }

                    checkIfDuplicate.add(numStr);
                }
            }
        } else {
            for (String tubeStr : tube) {
                checkIfDuplicate.add(tubeStr);
            }
        }

        if (lotteryType.contains("十一选五") && playtypename.equals("任选单式")) {
            int textSize = 0;
            switch (SELECT_FILE.PLAY_TYPE_REDIO_MAP.get(playradioname)) {
                case SELECT_FILE.OPTIONAL_ONE_OF_ONE:
                    textSize = 1;
                    break;
                case SELECT_FILE.OPTIONAL_TWO_OF_TWO:
                    textSize = 2;
                    break;
                case SELECT_FILE.OPTIONAL_THREE_OF_THREE:
                    textSize = 3;
                    break;
                case SELECT_FILE.OPTIONAL_FOUR_OF_FOUR:
                    textSize = 4;
                    break;
                case SELECT_FILE.OPTIONAL_FIVE_OF_FIVE:
                    textSize = 5;
                    break;
                case SELECT_FILE.OPTIONAL_FIVE_OF_SIX:
                    textSize = 6;
                    break;
                case SELECT_FILE.OPTIONAL_FIVE_OF_SEVEN:
                    textSize = 7;
                    break;
                case SELECT_FILE.OPTIONAL_FIVE_OF_EIGHT:
                    textSize = 8;
                    break;
                default:
                    formatErr = true;
                    break;
            }

            if (!formatErr) {
                for (String str : checkIfDuplicate) {
                    String[] tmp = Iterables.toArray(Splitter.fixedLength(2).split(str), String.class);
                    if (tmp.length == textSize) {
                        for (String tmpStr : tmp) {
                            if (tmpStr.length() != 2) {
                                editNumbers.clear();
                                formatErr = true;
                                break;
                            } else {
                                editNumbers.add(String.valueOf(Integer.parseInt(tmpStr)));
                            }
                        }
                    } else {
                        editNumbers.clear();
                        formatErr = true;
                    }

                    int nums = singleCalculateTotalAmount();
                    if (nums > 0) {
                        totalNums += nums;

                        String numStr = "";
                        for (int i = 0; i < editNumbers.size() - 1; ++i) {
                            numStr += editNumbers.get(i) + " ";
                        }
                        if (editNumbers.size() > 0) {
                            numStr += editNumbers.get(editNumbers.size() - 1);
                        }

                        finalSelectedBuffer.append(numStr + splittag);
                    }

                    editNumbers.clear();
                }
            }
        } else if (lotteryType.contains("十一选五") || lotteryType.contains("PK拾")) {
            int textSize = 0;
            if (playtypename.contains("六")) {
                textSize = 6;
            } else if (playtypename.contains("五")) {
                textSize = 5;
            } else if (playtypename.contains("四")) {
                textSize = 4;
            } else if (playtypename.contains("三")) {
                textSize = 3;
            } else if (playtypename.contains("二")) {
                textSize = 2;
            } else if (playtypename.contains("一")) {
                textSize = 1;
            } else {
                formatErr = true;
            }

            if (!formatErr) {
                for (String str : checkIfDuplicate) {
                    String[] tmp = Iterables.toArray(Splitter.fixedLength(2).split(str), String.class);

                    if (tmp.length == textSize) {
                        for (String tmpStr : tmp) {
                            if (tmpStr.length() != 2) {
                                editNumbers.clear();
                                formatErr = true;
                                break;
                            } else {
                                editNumbers.add(String.valueOf(Integer.parseInt(tmpStr)));
                            }
                        }
                    } else {
                        editNumbers.clear();
                        formatErr = true;
                    }

                    int nums = singleCalculateTotalAmount();
                    if (nums > 0) {
                        totalNums += nums;

                        String numStr = "";
                        for (int i = 0; i < editNumbers.size() - 1; ++i) {
                            numStr += editNumbers.get(i) + " ";
                        }
                        if (editNumbers.size() > 0) {
                            numStr += editNumbers.get(editNumbers.size() - 1);
                        }

                        finalSelectedBuffer.append(numStr + splittag);
                    }

                    editNumbers.clear();
                }
            }
        } else if (lotteryType.contains("快三")) {
            int textSize = 3;
            if (playtypename.contains("三") || playradioname.contains("三")) {
                textSize = 3;
            } else if (playtypename.contains("二同号") || playradioname.contains("二同号")) {
                textSize = 3;
            } else if (playtypename.contains("二不同号") || playradioname.contains("二不同号")) {
                textSize = 2;
            } else {
                formatErr = true;
            }

            if (!formatErr) {
                for (String ss : checkIfDuplicate) {
                    if (ss.length() == textSize) {
                        for (int i = 0; i < ss.length(); ++i) {
                            editNumbers.add(String.valueOf(ss.charAt(i)));
                        }
                    } else {
                        editNumbers.clear();
                        formatErr = true;
                    }

                    int nums = singleCalculateTotalAmount();
                    if (nums > 0) {
                        totalNums += nums;

                        String numStr = "";
                        for (int i = 0; i < editNumbers.size() - 1; ++i) {
                            numStr += editNumbers.get(i) + " ";
                        }
                        if (editNumbers.size() > 0) {
                            numStr += editNumbers.get(editNumbers.size() - 1);
                        }

                        finalSelectedBuffer.append(numStr + splittag);
                    }

                    editNumbers.clear();
                }
            }

        } else {
            int textSize = 5;
            if (playtypename.contains("五") || playradioname.contains("五")) {
                textSize = 5;
            } else if (playtypename.contains("四") || playradioname.contains("四")) {
                textSize = 4;
            } else if (playtypename.contains("三") || playradioname.contains("三")) {
                textSize = 3;
            } else if (playtypename.contains("二") || playradioname.contains("二")) {
                textSize = 2;
            } else {
                formatErr = true;
            }

            if (!formatErr) {
                for (String ss : checkIfDuplicate) {
                    if (ss.length() == textSize) {
                        for (int i = 0; i < ss.length(); ++i) {
                            editNumbers.add(String.valueOf(ss.charAt(i)));
                        }
                    } else {
                        editNumbers.clear();
                        formatErr = true;
                    }

                    int nums = singleCalculateTotalAmount();
                    if (nums > 0) {
                        totalNums += nums;

                        String numStr = "";
                        for (int i = 0; i < editNumbers.size() - 1; ++i) {
                            numStr += editNumbers.get(i) + " ";
                        }
                        if (editNumbers.size() > 0) {
                            numStr += editNumbers.get(editNumbers.size() - 1);
                        }

                        finalSelectedBuffer.append(numStr + splittag);
                    }

                    editNumbers.clear();
                }
            }
        }

        String finalSelected = finalSelectedBuffer.toString();
        String[] finalBets = finalSelected.split(splittag);
        for (int i = 0; i < finalBets.length; ++i) {
            groupOfNum.addAll(Arrays.asList(finalBets[i].split(" ")));

            if (i < finalBets.length - 1) {
                groupOfNum.add(splittag);
            }
        }

        try {
            price = Double.parseDouble(txtPrice.getText().toString());
        } catch (NumberFormatException e) {
            price = 0.0;
        }
        amount = totalNums * price;

        tvTotalNums.setText(String.valueOf(totalNums));
        tvTotalAmount.setText(StringUtil.formatDoubleWith4Point(amount));

        int visibility = totalNums > 0 ? View.VISIBLE : View.GONE;
        if (totalNums > 0) {
            ll_ok.setBackgroundResource(R.drawable.bg_lottery_btn_settle);
            ll_ok.setEnabled(true);
        } else {
            ll_ok.setBackgroundResource(R.mipmap.lottery_settle_disabled);
            ll_ok.setEnabled(false);
        }
        ll_bottom.setVisibility(visibility);
    }

    private boolean isSingle() {
        return (playradioname.contains("单式") || playradioname.equals("混合组选") || currentPlayTypeInfo.getPlayTypeName().contains("单式"));
    }

    private void setCheckBox(LinearLayout layout, int id, boolean checked) {
        CheckBox checkBox = (CheckBox) layout.findViewById(id);
        checkBox.setChecked(checked);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSingle()) {
                    if (mSigleEditable != null) {
                        singleAfterTextChanged(mSigleEditable);
                    }
                } else {
                    calculateTotalAmount();
                }
            }
        });
    }

    private CircleButton getCircleButton(String num, boolean isWord) {
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        CircleButton button = new CircleButton(this);
        button.setLayoutParams(params);
        button.setGravity(Gravity.CENTER);
        button.setTextColor(Color.parseColor("#FFFFFF"));
        button.setText(num);
        button.setWord(isWord);
        button.setPadding(5, 5, 5, 5);

        if (isWord) {
            button.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        } else {
            button.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 22);
        }

        return button;
    }

    private LinearLayout getBetCircleButton(String num, boolean isWord) {
        LinearLayout layout = new LinearLayout(this);

        if (currentPlayTypeRadioInfo.getPlayTypeRadioName().equals("定倍下单") || currentLotteryInfo.getLotteryType().contains("快三")) {
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(DisplayUtil.getPxByDp(this, 65), DisplayUtil.getPxByDp(this, 70));

            layout.setGravity(Gravity.CENTER);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setLayoutParams(params);

            CircleButton button = getCircleButton(num, isWord);
            layout.addView(button);

            TextView text = new TextView(this);
            text.setTextColor(Color.WHITE);
            text.setGravity(Gravity.CENTER);
            text.setTextColor(Color.parseColor("#3B5E7E"));
            text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
            text.setText("0.0");
            layout.addView(text);
        } else if (currentPlayTypeRadioInfo.getPlayTypeRadioName().equals("手动倍数")) {
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(DisplayUtil.getPxByDp(this, 65), DisplayUtil.getPxByDp(this, 80));
            layout = (LinearLayout) getLayoutInflater().inflate(R.layout.lottery_position_edittext_button, null);
            layout.setLayoutParams(params);

            CircleButton button = (CircleButton) layout.getChildAt(0);
            button.setText(num);

            EditText editText = (EditText) layout.getChildAt(1);
            editText.setBackgroundResource(R.drawable.rect_manual_enable);
            editText.setHint("0.0");
        }

        setKeyRebate();

        return layout;
    }

    private String getLineSelectedStr(boolean isNextLotterySettle, String playNum, String ball, String bet) {
        String lineSelected = "";

        if (isNextLotterySettle) {
            if ((currentPlayTypeInfo.getPlayTypeName().equals("定位胆") || currentPlayTypeInfo.getPlayTypeName().equals("综合")) && (currentPlayTypeRadioInfo.getPlayTypeRadioName().equals("定倍下单") || currentPlayTypeRadioInfo.getPlayTypeRadioName().equals("手动倍数"))) {
                lineSelected += playNum + " " + ball + " " + bet + ",";
            } else if (currentPlayTypeInfo.getPlayTypeName().equals("冠亚和") && (currentPlayTypeRadioInfo.getPlayTypeRadioName().equals("定倍下单") || currentPlayTypeRadioInfo.getPlayTypeRadioName().equals("手动倍数"))) {
                lineSelected += "冠亚和" + " ";

                Pattern pattern = Pattern.compile("[0-9]*");
                if (!pattern.matcher(ball).matches()) {
                    lineSelected += "冠亚";
                }

                lineSelected += ball + " " + bet + ",";
            } else {
                lineSelected += ball + ",";
            }
        } else {
            lineSelected += ball + ",";
        }

        return lineSelected;
    }

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

    private void closeEditText() {
        for (int i = 0; i < layoutContainer.getChildCount(); ++i) {
            LinearLayout line = (LinearLayout) layoutContainer.getChildAt(i);
            LinearLayout l = (LinearLayout) line.findViewWithTag("ballLinearLayout");
            FlowLayout ballLayout = (FlowLayout) l.findViewWithTag("ballLayout");

            for (int j = 0; j < ballLayout.getChildCount(); ++j) {
                View view = ballLayout.getChildAt(j);
                if (view instanceof LinearLayout) {
                    LinearLayout layout = (LinearLayout) view;
                    View buttonView = layout.getChildAt(0);
                    View textView = layout.getChildAt(1);

                    if (buttonView instanceof CircleButton && textView instanceof EditText) {
                        CircleButton ball = (CircleButton) buttonView;
                        EditText editText = (EditText) textView;

                        if (editText.getText().toString().isEmpty()) {
                            ball.setIsSelected(false);
                            editText.setBackgroundResource(R.drawable.rect_manual_enable);
                        }
                        editText.clearFocus();
                    }
                }
            }
        }
    }

    private boolean isFirstOpenLottery() {
        SharedPreferences mysherPreferences = this.getSharedPreferences("FIRST_OPEN_LOTERY_KEY", MODE_PRIVATE);
        return mysherPreferences.getBoolean("FIRST_OPEN_LOTERY_KEY", true);
    }

    private void setFirstOpenLottery() {
        SharedPreferences mysherPreferences = this.getSharedPreferences("FIRST_OPEN_LOTERY_KEY", MODE_PRIVATE);
        SharedPreferences.Editor editor = mysherPreferences.edit();
        editor.putBoolean("FIRST_OPEN_LOTERY_KEY", false);
        editor.commit();
    }

    private void closeLayoutPopExport() {
        mIsFirstOpenLottery = false;
        layoutPopExport.setVisibility(View.GONE);
        imgPlayTypeDes.setEnabled(true);
        imgPersonalCenter.setEnabled(true);
        playTypeLayout.setEnabled(true);
    }

    private void setKeyRebate() {
        // 20180716 快三和值不用key 需根據獎金選單動態改變---Angela
        if (currentLotteryID == 18 && ((currentPlayTypeRadioInfo.getPlayTypeRadioID() == 624 && currentPlayTypeRadioInfo.getPlayTypeRadioName().equals("和值")) ||
                (currentPlayTypeRadioInfo.getPlayTypeRadioID() == 615 && currentPlayTypeRadioInfo.getPlayTypeRadioName().equals("三不同和值")))) {
            if (ballRebatesList != null && !ballRebatesList.isEmpty()) {
                for (BallRebates ballRebates : ballRebatesList) {
                    for (int i = 0; i < layoutContainer.getChildCount(); ++i) {
                        LinearLayout line = (LinearLayout) layoutContainer.getChildAt(i);
                        LinearLayout ballLinearLayout = (LinearLayout) line.findViewWithTag("ballLinearLayout");
                        FlowLayout ballLayout = (FlowLayout) ballLinearLayout.findViewWithTag("ballLayout");

                        for (int j = 0; j < ballLayout.getChildCount(); ++j) {
                            LinearLayout buttonLayout = (LinearLayout) ballLayout.getChildAt(j);
                            CircleButton button = (CircleButton) buttonLayout.getChildAt(0);
                            View view = buttonLayout.getChildAt(1);
                            if (view instanceof TextView) {
                                TextView text = (TextView) view;
                                if (button.getText().equals(ballRebates.getNumber()) && mSelectListItemList.get(rebateSelectList.getSelectedItemPosition()).getValue().contains(ballRebates.getRebatePro())) {
                                    text.setText(ballRebates.getRebateProMoney());
                                }
                            }
                        }
                    }
                }
            }
        } else {
            for (SelectListItem item : mSelectListItemList) {
                if (item.getKey() != null && !item.getKey().isEmpty()) {
                    for (int i = 0; i < layoutContainer.getChildCount(); ++i) {
                        LinearLayout line = (LinearLayout) layoutContainer.getChildAt(i);
                        LinearLayout ballLinearLayout = (LinearLayout) line.findViewWithTag("ballLinearLayout");
                        FlowLayout ballLayout = (FlowLayout) ballLinearLayout.findViewWithTag("ballLayout");

                        for (int j = 0; j < ballLayout.getChildCount(); ++j) {
                            LinearLayout buttonLayout = (LinearLayout) ballLayout.getChildAt(j);
                            CircleButton button = (CircleButton) buttonLayout.getChildAt(0);
                            View view = buttonLayout.getChildAt(1);

                            if (item.getKey().equals("PK10_Mix")) {
                                if (view instanceof EditText) {
                                    EditText editText = (EditText) view;
                                    editText.setHint(item.getValue());
                                } else if (view instanceof TextView) {
                                    TextView text = (TextView) view;
                                    text.setText(item.getValue());
                                }
                            } else {
                                if (button.getText().equals(item.getKey())) {
                                    if (view instanceof EditText) {
                                        EditText editText = (EditText) view;
                                        editText.setHint(item.getValue());
                                    } else if (view instanceof TextView) {
                                        TextView text = (TextView) view;
                                        text.setText(item.getValue());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void getAllPlayConfig() {
        mHandler.sendEmptyMessageDelayed(0, 1000);
        mIsAllPlayConfigError = false;

        MyAsyncTask<AllPlayConfig> task = new MyAsyncTask<AllPlayConfig>(LotteryActivity.this) {
            @Override
            public AllPlayConfig callService() throws IOException, JsonParseException, BizException, ServiceException {
                return new LotteryService().getAllPlayConfig("");
            }

            @Override
            public void onLoaded(AllPlayConfig result) throws Exception {
                closeProgressDialog();

                if (!mIsAllPlayConfigError) {
                    AllPlayConfig allPlayConfig = new LotteryService().getAllPlayConfigInfo();
                    if (allPlayConfig != null) {
                        SharedPreferences.Editor editor = getSharedPreferences(CommonConfig.KEY_HASHCODE, MODE_PRIVATE).edit();
                        editor.putString(CommonConfig.KEY_HASHCODE_ALLPLAYCONFIG_CACHE, allPlayConfig.getHashCode());
                        editor.commit();
                    }
                } else {
                    Type messageType = new TypeToken<Response<AllPlayConfig>>() {
                    }.getType();
                    Gson gson = new Gson();
                    Response<AllPlayConfig> info = gson.fromJson(DefaultDataCache.getDataCache(LotteryActivity.this, DefaultDataCache.ALLPLAYCONFIG_CACHE), messageType);
                    new LotteryService().allPlayConfigInfo = info.getData();
                }

                mPlayConfigList = new LotteryService().getPlayConfigInfo(currentLotteryID);
                int currentPlayTypeID = getIntent().getIntExtra("PlayTypeID", 0);
                int currentPlayTypeRadioID = getIntent().getIntExtra("PlayTypeRadioID", 0);
                initBallLayoutV1(currentLotteryID, currentPlayTypeID, currentPlayTypeRadioID);
            }
        };
        task.setOnError(new MyAsyncTask.OnError() {
            @Override
            public void handleError(Exception e) {
                mIsAllPlayConfigError = true;
            }
        });
        task.executeTask();
    }
}
