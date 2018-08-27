package com.hec.app.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonParseException;
import com.hec.app.R;
import com.hec.app.activity.base.BaseActivity;
import com.hec.app.activity.base.BaseApp;
import com.hec.app.adapter.BetSettleAdapter;
import com.hec.app.entity.BetSettleInfo;
import com.hec.app.entity.BizException;
import com.hec.app.entity.PlaceOrderInfo;
import com.hec.app.entity.Response;
import com.hec.app.entity.SettleInfo;
import com.hec.app.fragment.NextIssueNoFragment;
import com.hec.app.framework.widget.MyToast;
import com.hec.app.lottery.LotteryConfig;
import com.hec.app.lottery.LotteryManager;
import com.hec.app.util.DialogUtil;
import com.hec.app.util.DisplayUtil;
import com.hec.app.util.IntentUtil;
import com.hec.app.util.LotteryCalculator;
import com.hec.app.util.LotteryRandomGenerator;
import com.hec.app.util.LotteryUtil;
import com.hec.app.util.MyAsyncTask;
import com.hec.app.util.StringUtil;
import com.hec.app.util.TestUtil;
import com.hec.app.webservice.LotteryService;
import com.hec.app.webservice.ServiceException;
//import com.umeng.analytics.MobclickAgent;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LotterySettleActivity extends BaseActivity implements NextIssueNoFragment.OnCurrentIssueNoCompletedListener {

    private RelativeLayout nextIssueNoContainer;
    private LinearLayout layoutContainer;
    private PlaceOrderInfo orderInfo;
    private LinearLayout ll_ok;
    private boolean mIsError;
    private NextIssueNoFragment nextIssueNoFragment;
    private Toolbar toolbar;
    private LinearLayout btnReturn;
    private LinearLayout btnRandom;
    private LinearLayout btnClear;
    private TextView tvTotalAmount;
    private ListView listView_LotterySettle;
    private List<BetSettleInfo> settleBets = new ArrayList<>();
    private BetSettleAdapter adapter;
    private CheckBox chkIsWinStop;
    private EditText txtMultiple;
    private EditText txtPeriods;
    private ProgressDialog dialog;
    private LotteryConfirmPopupWindow confirmPopupWindow;
    private boolean buying;
    private String playradioname;
    private Set<String> hashset = new HashSet<>();
    private LinearLayout jackpotBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lottery_settle);

        Serializable serializableData = getIntent().getSerializableExtra("PlaceOrderInfo");
        if (serializableData != null) {
            orderInfo = (PlaceOrderInfo) serializableData;
        }

        findView();

        initView();
    }

    @Override
    public void onCompleted() {

    }

    private void findView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView config_hidden = (TextView) this.findViewById(R.id.config_hidden);
        config_hidden.requestFocus();

        jackpotBar = (LinearLayout) findViewById(R.id.jackpotbar);
        layoutContainer = (LinearLayout) findViewById(R.id.layoutContainer);
        nextIssueNoContainer = (RelativeLayout) findViewById(R.id.nextIssueNoContainer);
        ll_ok = (LinearLayout) findViewById(R.id.ll_ok);
        btnReturn = (LinearLayout) findViewById(R.id.btnReturn);
        btnRandom = (LinearLayout) findViewById(R.id.btnRandom);
        btnClear = (LinearLayout) findViewById(R.id.btnClear);
        tvTotalAmount = (TextView) findViewById(R.id.tvTotalAmount);
        listView_LotterySettle = (ListView) findViewById(R.id.listView_LotterySettle);
        txtMultiple = (EditText) findViewById(R.id.txtMultiple);
        txtPeriods = (EditText) findViewById(R.id.txtPeriods);
        chkIsWinStop = (CheckBox) findViewById(R.id.chkIsWinStop);

        txtMultiple.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                calculateAfterAmount();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        txtPeriods.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                calculateAfterAmount();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void initView() {
        dialog = DialogUtil.getProgressDialog(this, "正在提交您的投注信息...");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.mipmap.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        nextIssueNoContainer.removeAllViews();
        nextIssueNoFragment = NextIssueNoFragment.newInstance(orderInfo.getLotteryID());
        getFragmentManager().beginTransaction().replace(R.id.nextIssueNoContainer, nextIssueNoFragment).commit();

        double totalAmount = orderInfo.getQty() * orderInfo.getPrice();
        tvTotalAmount.setText(StringUtil.formatDoubleWith4Point(totalAmount));
        ll_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buy();
            }
        });
        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        btnRandom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateRandomBet();
            }
        });
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clear();
            }
        });

        if (orderInfo.getLotteryName().contains("秒秒彩") ||
                (orderInfo.getPlayMode() == LotteryConfig.PLAY_MODE.EXPERT && orderInfo.getLotteryName().contains("PK") &&
                        (orderInfo.getPlayTypeName().equals("定位胆") || orderInfo.getPlayTypeName().equals("冠亚和") || orderInfo.getPlayTypeName().equals("综合")))) {
            jackpotBar.setVisibility(View.GONE);
        }

        for (SettleInfo info : BaseApp.allSettleInfo) {
            //TODO
            String playTypeName = orderInfo.getPlayTypeName();
            if (playTypeName.equals("龙虎斗") || playTypeName.equals("大小") || playTypeName.equals("单双")) {
                if (settleBets.size() > 0) {
                    Toast.makeText(this, "此玩法只能投注一注！", Toast.LENGTH_SHORT).show();
                    break;
                }
            }

            List<String> ballList = info.getBallList();
            String selectedNums = getSelectedNumbers(ballList, info.getUnitList());
            TestUtil.print("betnums " + selectedNums);
            if (selectedNums.compareTo("") == 0) {
                continue;
            }

            if (orderInfo != null && orderInfo.getPlayTypeRadioName() != null) {
                if (isSingle()) {
                    btnRandom.setVisibility(View.GONE);
                    LinearLayout.LayoutParams btnLp = new LinearLayout.LayoutParams(0, DisplayUtil.getPxByDp(this, 25), 1);
                    btnLp.setMargins(20, 0, 20, 0);
                    btnClear.setLayoutParams(btnLp);
                    btnReturn.setLayoutParams(btnLp);
                }
            }
            playradioname = orderInfo.getPlayTypeRadioName();

            String unitStr = "";
            for (String unit : info.getUnitList()) {
                unitStr += unit;
            }
            if (!unitStr.isEmpty()) {
                unitStr += ":";
            }

            BetSettleInfo bet = new BetSettleInfo();
            bet.setLotteryID(orderInfo.getLotteryID());
            bet.setLotteryName(orderInfo.getLotteryName());
            bet.setSelectedNums(selectedNums);
            bet.setAmount(info.getBets());
            bet.setPlayTypeName(orderInfo.getPlayTypeName());
            bet.setPlayTypeRadioName(orderInfo.getPlayTypeRadioName());
            bet.setPrice(orderInfo.getPrice());
            bet.setManualBet(info.getManualBet());
            bet.setUnit(unitStr);
            settleBets.add(bet);
        }

        bindData(settleBets);

        calculateTotalAmount();

        buying = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        buying = false;
    }

    private String getSelectedNumbers(List<String> list, List<String> unitList) {
        String singleLine = "";
        StringBuilder builder = new StringBuilder();
        boolean isNotSub = false;

        for (String str : list) {
            if (orderInfo != null && orderInfo.getPlayTypeRadioName() != null) {
                if (isSingle()) {
                    if (!str.isEmpty()) {
                        isNotSub = true;

                        if (orderInfo.getLotteryName().contains("十一选五") || orderInfo.getLotteryName().contains("PK")) {
                            singleLine = str.replace(",", "|");
                            if (singleLine.equals("|")) {
                                builder.append(singleLine);
                            } else {
                                builder.append(" " + singleLine);
                            }
                        } else if (orderInfo.getPlayTypeName().equals("任二") || orderInfo.getPlayTypeName().equals("任三") ||
                                orderInfo.getPlayTypeName().equals("任四") || orderInfo.getPlayTypeName().equals("任选")) {
                            singleLine = str.replace(",", "");
                            builder.append(singleLine + ",");
                            isNotSub = false;
                        } else {
                            singleLine = str.replace(",", "|");
                            builder.append(singleLine);
                        }
                    }
                } else if (((orderInfo.getPlayTypeName().equals("定位胆") || orderInfo.getPlayTypeName().equals("冠亚和") || orderInfo.getPlayTypeName().equals("综合")) &&
                        (orderInfo.getPlayTypeRadioName().equals("定倍下单") || orderInfo.getPlayTypeRadioName().equals("手动倍数"))) ||
                        orderInfo.getPlayTypeRadioName().equals("定单双")) {
                    if (!StringUtil.isEmpty(str)) {
                        singleLine = str.replace(",", "|");
                        builder.append(singleLine + "|");
                    }
                } else {
                    if (orderInfo.getPlayTypeRadioName().contains("特殊号") || orderInfo.getPlayTypeRadioName().contains("龙虎和") || orderInfo.getPlayTypeRadioName().contains("总和大小单双")) {
                        if (!StringUtil.isEmpty(str)) {
                            singleLine = str.replace(",", " ");
                        } else {
                            singleLine = "";
                        }
                    } else if ((orderInfo.getPlayTypeRadioName().contains("直选和值") || orderInfo.getPlayTypeRadioName().contains("组选和值") ||
                            orderInfo.getLotteryName().contains("十一选五") || orderInfo.getLotteryName().contains("PK")) &&
                            !orderInfo.getPlayTypeRadioName().equals("猜中位")) {
                        if (!StringUtil.isEmpty(str)) {
                            singleLine = str.replace(",", " ");
                            singleLine = " " + singleLine;
                        } else {
                            singleLine = "";
                        }
                    } else if (orderInfo.getLotteryName().contains("江苏快三")) {
                        if (orderInfo.getPlayTypeRadioName().contains("胆拖选号") || orderInfo.getPlayTypeRadioName().contains("二同号单选")) {
                            singleLine = str.replace(",", ""); //拖沒有”,“
                        } else {
                            singleLine = str;
                        }
                    } else {
                        singleLine = str.replace(",", "");
                    }
                    builder.append(singleLine);
                    builder.append(",");
                }
            }
        }

        String selectedNums = "";
        if (builder.toString().length() > 0 && !isNotSub) {
            selectedNums = builder.toString().substring(0, builder.length() - 1);
        } else if (isNotSub) {
            selectedNums = builder.toString();
        }

        if ((orderInfo.getPlayTypeName().equals("任二") || orderInfo.getPlayTypeName().equals("任三") ||
                orderInfo.getPlayTypeName().equals("任四") || orderInfo.getPlayTypeName().equals("任选")) && isSingle()) {
            List<String> result = new ArrayList<>();
            for (String optionStr : selectedNums.split(",,")) {
                final List<String> buffer = new ArrayList<>();
                for (String str : optionStr.split(",")) {
                    buffer.add(str);
                }

                List<List<String>> array = new ArrayList<>();
                int size = unitList.size();
                for (int i = 0; i < size; ++i) {
                    for (int j = i + 1; j < size; ++j) {
                        if (orderInfo.getPlayTypeName().equals("任二") || orderInfo.getPlayTypeRadioName().equals("任选二单式")) {
                            List<String> value = new ArrayList<>();
                            for (int k = 0; k < size; ++k) {
                                value.add("");
                            }

                            value.set(i, buffer.get(0));
                            value.set(j, buffer.get(1));
                            array.add(value);
                        } else if (orderInfo.getPlayTypeName().equals("任三") || orderInfo.getPlayTypeRadioName().equals("任选三单式")) {
                            for (int k = j + 1; k < size; ++k) {
                                List<String> value = new ArrayList<>();
                                for (int l = 0; l < size; ++l) {
                                    value.add("");
                                }

                                value.set(i, buffer.get(0));
                                value.set(j, buffer.get(1));
                                value.set(k, buffer.get(2));
                                array.add(value);
                            }
                        } else if (orderInfo.getPlayTypeName().equals("任四") || orderInfo.getPlayTypeRadioName().equals("任选四单式")) {
                            for (int k = j + 1; k < size; ++k) {
                                for (int l = k + 1; l < size; ++l) {
                                    List<String> value = new ArrayList<>();
                                    for (int m = 0; m < size; ++m) {
                                        value.add("");
                                    }

                                    value.set(i, buffer.get(0));
                                    value.set(j, buffer.get(1));
                                    value.set(k, buffer.get(2));
                                    value.set(l, buffer.get(3));
                                    array.add(value);
                                }
                            }
                        }
                    }
                }

                if (array.size() > 0) {
                    List<Integer> unitIndexList = new ArrayList<>();
                    for (String str : unitList) {
                        unitIndexList.add(LotteryConfig.UNIT.UNIT_MAP.get(str));
                    }

                    for (List<String> value : array) {
                        StringBuilder resultBuilder = new StringBuilder();

                        int valueIndex = 0;
                        for (int i = 0; i < 5; ++i) {
                            boolean isAppend = false;

                            for (int unitIndex : unitIndexList) {
                                if (unitIndex == i) {
                                    resultBuilder.append(value.get(valueIndex));
                                    isAppend = true;
                                    ++valueIndex;
                                }
                            }

                            if (isAppend) {
                                resultBuilder.append("");
                            }

                            resultBuilder.append(",");
                        }

                        result.add(resultBuilder.toString().substring(0, resultBuilder.length() - 1));
                    }
                }
            }

            if (result.size() > 0) {
                String resultStr = "";
                for (String str : result) {
                    resultStr += str.replace("", "") + "|";
                }

                selectedNums = resultStr.substring(0, resultStr.length() - 1);
            }
        }

        return selectedNums;
    }

    private void generateRandomBet() {
        if (orderInfo != null && orderInfo.getPlayTypeRadioName() != null) {
            if (isSingle()) {
                MyToast.show(this, "目前子玩法为单式!");
                return;
            }
        }

        String playTypeName = orderInfo.getPlayTypeName();
        // 20180716 快三－“三连号通选”和“三同号通选” 也是不能隨選一注--Angela
        if (playTypeName.equals("龙虎斗") || playTypeName.equals("大小") || playTypeName.equals("单双") || playTypeName.equals("三连号通选") || playTypeName.equals("三同号通选")) {
            if (orderInfo.getQty() > 0) {
                Toast.makeText(this, "此玩法只能投注一注！", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        List<String> number = new ArrayList<>();
        if (orderInfo.getPlayMode() == LotteryConfig.PLAY_MODE.CLASSIC && !playTypeName.equals("任选")) {
            number = LotteryRandomGenerator.generateSingleBet(orderInfo.getLotteryName(), orderInfo.getPlayTypeName(), orderInfo.getPlayTypeRadioName());
        } else {
            number = LotteryManager.generateSingleBet(orderInfo.getLotteryID(), orderInfo.getPlayTypeName(), orderInfo.getPlayTypeRadioName());
        }

        //TODO
        TestUtil.print("calculate " + orderInfo.getLotteryName() + "," + orderInfo.getPlayTypeName() + "," + orderInfo.getPlayTypeRadioName());
        for (String s : number) {
            TestUtil.print("calculate " + s);
        }

        int qty = 0;
        int manualBet = 0;
        List<String> unitList = new ArrayList<>();
        if (orderInfo.getPlayMode() == LotteryConfig.PLAY_MODE.CLASSIC && !playTypeName.equals("任选")) {
            qty = LotteryCalculator.calculate(orderInfo.getLotteryName(), orderInfo.getPlayTypeName(), orderInfo.getPlayTypeRadioName(), number);
        } else {
            if (orderInfo.getPlayTypeName().equals("任二") ||
                    (playTypeName.equals("任选") && orderInfo.getPlayTypeRadioName().contains("任选二"))) {
                unitList.add("十");
                unitList.add("个");
            } else if (orderInfo.getPlayTypeName().equals("任三") ||
                    (playTypeName.equals("任选") && orderInfo.getPlayTypeRadioName().contains("任选三"))) {
                unitList.add("百");
                unitList.add("十");
                unitList.add("个");
            } else if (orderInfo.getPlayTypeName().equals("任四") ||
                    (playTypeName.equals("任选") && orderInfo.getPlayTypeRadioName().contains("任选四"))) {
                unitList.add("千");
                unitList.add("百");
                unitList.add("十");
                unitList.add("个");
            } else if (orderInfo.getPlayTypeName().equals("定位胆") || orderInfo.getPlayTypeName().equals("冠亚和") || orderInfo.getPlayTypeName().equals("综合")) {
                manualBet = 1;
            }

            qty = LotteryManager.calculate(orderInfo.getLotteryID(), orderInfo.getPlayTypeName(), orderInfo.getPlayTypeRadioName(), number, unitList.size());
        }

        String unitStr = "";
        for (String unit : unitList) {
            unitStr += unit;
        }
        if (!unitStr.isEmpty()) {
            unitStr += ":";
        }

        BetSettleInfo bet = new BetSettleInfo();
        bet.setSelectedNums(getSelectedNumbers(number, new ArrayList<String>()));
        bet.setAmount(qty);
        bet.setPlayTypeName(orderInfo.getPlayTypeName());
        bet.setPlayTypeRadioName(orderInfo.getPlayTypeRadioName());
        bet.setPrice(orderInfo.getPrice());
        bet.setManualBet(manualBet);
        bet.setUnit(unitStr);

        settleBets.add(bet);
        adapter.notifyDataSetChanged();

        LotteryUtil.addSettleInfoToLocalCache(number, unitList, 0.0, qty, orderInfo.getPlayTypeName(), orderInfo.getPlayTypeRadioName());

        calculateTotalAmount();
    }

    private void bindData(List<BetSettleInfo> list) {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        adapter = new BetSettleAdapter(this, dm.widthPixels, list);
        adapter.notifyDataSetChanged();
        listView_LotterySettle.setAdapter(adapter);
    }

    public void calculateTotalAmount() {
        int totalQty = 0;
        double totalAmount = 0;
        double totalManualBet = 0;
        for (BetSettleInfo b : settleBets) {
            totalQty += b.getAmount();
            totalManualBet += b.getManualBet();
            totalAmount += LotteryUtil.getTotalAmount(b.getAmount(), orderInfo.getPrice());
        }

        orderInfo.setQty(totalQty);
        orderInfo.setManualBet(totalManualBet);

        if (orderInfo.getMultiple() > 0 && orderInfo.getPeriods() > 0) {
            calculateAfterAmount();
        } else {
            tvTotalAmount.setText(StringUtil.formatDoubleWith4Point(totalAmount));
        }
    }

    public void removeOneBet(int position) {
        LotteryUtil.removeBetFromLocalCache(position);
    }

    private void clear() {
        if (settleBets.size() == 0) {
//            Toast.makeText(LotterySettleActivity.this, "没有选择号码！", Toast.LENGTH_LONG).show();
            return;
        }
        settleBets.clear();
        adapter.notifyDataSetChanged();
        BaseApp.allSettleInfo.clear();
        BaseApp.allUnits.clear();
        calculateTotalAmount();
        Log.i("lala", "in clear!");
    }

    private void buy() {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(LotterySettleActivity.this.getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {

        }

        mIsError = false;
        String selectedNumbers = "";
        for (BetSettleInfo s : settleBets) {
            if (playradioname != null) {
                if (!s.getUnit().isEmpty() && !isSingle() && !playradioname.equals("直选复式") && !orderInfo.getPlayTypeName().equals("任选")) {
                    selectedNumbers += s.getUnit();
                }

                if (isSingle()) {
                    if (orderInfo.getPlayTypeName().equals("任二") || orderInfo.getPlayTypeName().equals("任三") ||
                            orderInfo.getPlayTypeName().equals("任四") || orderInfo.getPlayTypeName().equals("任选")) {
                        selectedNumbers = selectedNumbers + s.getSelectedNums() + "|";
                    } else {
                        if (s.getSelectedNums() != null && s.getSelectedNums().contains(" ")) {
                            s.getSelectedNums().replace(" ", "");
                        }
                        if (s.getSelectedNums().contains(", ,")) {
                            selectedNumbers = selectedNumbers + s.getSelectedNums().replace(", ,", "|") + "|";
                        } else if (s.getSelectedNums().contains(",;,")) {
                            selectedNumbers = selectedNumbers + s.getSelectedNums().replace(",;,", "|") + "|";
                        } else if (s.getSelectedNums().contains(",,")) {
                            selectedNumbers = selectedNumbers + s.getSelectedNums().replace(",,", "|") + "|";
                        } else if (s.getSelectedNums().contains(",  ,")) {
                            selectedNumbers = selectedNumbers + s.getSelectedNums().replace(",  ,", "|") + "|";
                        } else {
                            selectedNumbers = selectedNumbers + s.getSelectedNums() + "|";
                        }
                    }
                } else {
                    String selectnum = "";
//                if (s.getSelectedNums().substring(0, 1).equals(",")) {
//                    selectnum = s.getSelectedNums().substring(1, s.getSelectedNums().length());
//                } else {
//                    selectnum = s.getSelectedNums();
//                }
                    selectnum = s.getSelectedNums();
                    selectedNumbers += selectnum + "|";
                    Log.i("real", "double " + selectedNumbers);
                }
            }
        }

        if (selectedNumbers.length() <= 0 || selectedNumbers.compareTo("&") == 0) {
            MyToast.show(getApplicationContext(), "投注项不能为空！");
            return;
        }
        Log.i("hec", "buy" + selectedNumbers);
        if (selectedNumbers.length() > 0) {

            //selectedNumbers maybe 0.
            hashset.clear();
            String[] ss = selectedNumbers.replace("|", "&").split("&");
            String finalselect = "";

            if (orderInfo.getPlayTypeName().equals("任二") || orderInfo.getPlayTypeName().equals("任三") || orderInfo.getPlayTypeName().equals("任四") || orderInfo.getPlayTypeName().equals("任选")) {
                if (isSingle()) {
                    for (int i = 0; i < ss.length; i++) {
                        hashset.add(ss[i]);
                    }
                    if (ss.length != hashset.size()) {
                        MyToast.show(getApplicationContext(), "不同投注中存在重复选号,请查看!");
                        return;
                    }
                }

                List<String> hashList = new ArrayList<>();
                for (int i = 0; i < ss.length; i++) {
                    hashList.add(ss[i]);
                }

                for (String hash : hashList) {
                    finalselect += hash + "|";
                }
            } else {
                if (orderInfo.getPlayMode() == LotteryConfig.PLAY_MODE.CLASSIC && orderInfo.getPlayTypeRadioName().equals("冠亚和值")) {
                    int allSize = 0;
                    Set<String> notReapetNums = new HashSet<>();

                    for (int i = 0; i < ss.length; i++) {
                        hashset.add(ss[i]);
                        for (String numStr : ss[i].split(" ")) {
                            if (!numStr.isEmpty()) {
                                notReapetNums.add(numStr);
                                allSize += 1;
                            }
                        }
                    }

                    if (allSize != notReapetNums.size()) {
                        MyToast.show(getApplicationContext(), "不同投注中存在重复选号,请查看!");
                        return;
                    }

                    for (String s : hashset) {
                        finalselect += s + "|";
                    }
                } else {
                    for (int i = 0; i < ss.length; i++) {
                        Log.i("hec", "array" + ss[i]);
                        hashset.add(ss[i]);
                    }
                    if (ss.length != hashset.size()) {
                        MyToast.show(getApplicationContext(), "不同投注中存在重复选号,请查看!");
                        return;
                    }
                    Log.i("hec", "hashset" + hashset.size() + "");
                    for (String s : hashset) {
                        Log.i("hec", "inhash" + s);
                        finalselect += s + "|";
                    }
                }
            }

            orderInfo.setSelectedNums(finalselect.substring(0, finalselect.length() - 1));
            Log.i("hec", "buyyyyyyy" + finalselect);
        }

        if (!txtPeriods.getText().toString().equals("")) {
            if (Integer.parseInt(txtPeriods.getText().toString()) > 1000) {
                return;
            }
        }

        orderInfo.setIsWinStop(chkIsWinStop.isChecked());
        try {
            orderInfo.setMultiple(Integer.parseInt(txtMultiple.getText().toString()));
            orderInfo.setPeriods(Integer.parseInt(txtPeriods.getText().toString()));
        } catch (Exception e) {
            orderInfo.setMultiple(0);
            orderInfo.setPeriods(0);
        }
        orderInfo.setIsAfter(orderInfo.getMultiple() > 0 && orderInfo.getPeriods() > 0);

        if (nextIssueNoFragment.getCurrentIssueNo() == null || (nextIssueNoFragment.getCurrentIssueNo().compareTo("") == 0)) {
            MyToast.show(this, "期号正在获取中，请稍后...");
            return;
        }

        orderInfo.setCurrentIssueNo(nextIssueNoFragment.getCurrentIssueNo());

        confirmPopupWindow = new LotteryConfirmPopupWindow(this, orderInfo.getLotteryName(), orderInfo.getPlayTypeName(), orderInfo.getPlayTypeRadioName(),
                orderInfo.getTotalAmount());
        confirmPopupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        confirmPopupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        confirmPopupWindow.backgroundAlpha(Float.valueOf("0.5"));
        confirmPopupWindow.setOnConfirmedListener(new LotteryConfirmPopupWindow.OnConfirmedListener() {
            @Override
            public void onConfirmed() {
                Map<String, String> map = new HashMap<String, String>();
                map.put("amount_money", String.valueOf(orderInfo.getPrice() * orderInfo.getQty()));
                map.put("lottery_type", orderInfo.getLotteryName()
                        + "-" + orderInfo.getPlayTypeName()
                        + "-" + orderInfo.getPlayTypeRadioName());
                if (buying)
                    return;
                buying = true;
                dialog.show();
                if (orderInfo.isAfter()) {
                    afterBet();
                } else {
                    placeOrder();
                }
            }
        });
        confirmPopupWindow.showAtLocation(listView_LotterySettle, Gravity.CENTER, 0, 0);
    }

    private void placeOrder() {
        MyAsyncTask<Response<?>> task = new MyAsyncTask<Response<?>>(LotterySettleActivity.this) {

            @Override
            public Response<?> callService() throws IOException,
                    JsonParseException, BizException, ServiceException {
                return new LotteryService().placeOrder(orderInfo, orderInfo.getPlayMode());
            }

            @Override
            public void onLoaded(Response<?> result) throws Exception {
                dialog.dismiss();
                if (LotterySettleActivity.this == null || LotterySettleActivity.this.isFinishing())
                    return;
                buying = false;
                if (!mIsError) {
                    if (result.getSuccess()) {
                        Bundle b = new Bundle();
                        b.putSerializable("PlaceOrderInfo", orderInfo);
                        if (orderInfo != null) {
                            Log.i("speed", "num" + orderInfo.getSelectedNums());
                            if (orderInfo.getLotteryName().contains("秒秒彩")) {
                                Runnable tmp = new Runnable() {

                                    @Override
                                    public void run() {
                                        dialog = DialogUtil.getProgressDialog(LotterySettleActivity.this, "正在载入秒秒彩...");
                                        dialog.show();
                                    }
                                };
                                runOnUiThread(tmp);
                                IntentUtil.redirectToNextActivity(LotterySettleActivity.this, PressToWinActivity.class, b);
                            } else
                                IntentUtil.redirectToNextActivity(LotterySettleActivity.this, LotteryResultActivity.class, b);
                            clear();
                            LotterySettleActivity.this.finish();
                        }
                    } else {
                        Toast.makeText(LotterySettleActivity.this, result.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    BaseApp.getAppBean().resetApiUrl(LotterySettleActivity.this);
                    Toast.makeText(LotterySettleActivity.this, "抱歉，网络异常，请查看记录是否已经投注", Toast.LENGTH_LONG).show();
                    BaseApp.getAppBean().setRetryCount(0);
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

    private void afterBet() {
        MyAsyncTask<Response<?>> task = new MyAsyncTask<Response<?>>(LotterySettleActivity.this) {

            @Override
            public Response<?> callService() throws IOException,
                    JsonParseException, BizException, ServiceException {
                return new LotteryService().afterBet(orderInfo, orderInfo.getPlayMode());
            }

            @Override
            public void onLoaded(Response<?> result) throws Exception {
                dialog.dismiss();
                if (LotterySettleActivity.this == null || LotterySettleActivity.this.isFinishing())
                    return;
                buying = false;

                if (!mIsError) {
                    if (result.getSuccess()) {

                        Bundle b = new Bundle();
                        b.putSerializable("PlaceOrderInfo", orderInfo);
                        TestUtil.print("PlaceOrderInfo: " + orderInfo.getLotteryName());
                        if (orderInfo != null) {
                            if (orderInfo.getLotteryName().contains("秒秒彩"))
                                IntentUtil.redirectToNextActivity(LotterySettleActivity.this, PressToWinActivity.class, b);
                            else
                                IntentUtil.redirectToNextActivity(LotterySettleActivity.this, LotteryResultActivity.class, b);
                            clear();
                            LotterySettleActivity.this.finish();
                        }
                    } else {
                        Toast.makeText(LotterySettleActivity.this, result.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    BaseApp.getAppBean().resetApiUrl(LotterySettleActivity.this);
                    Toast.makeText(LotterySettleActivity.this, "抱歉，网络异常，请查看记录是否已经投注", Toast.LENGTH_LONG).show();
                    BaseApp.getAppBean().setRetryCount(0);
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

    private void getAfterTotalAmount() {
        mIsError = false;
        MyAsyncTask<Response<Map<String, String>>> task = new MyAsyncTask<Response<Map<String, String>>>(LotterySettleActivity.this) {
            @Override
            public Response<Map<String, String>> callService() throws IOException,
                    JsonParseException, BizException, ServiceException {
                return new LotteryService().getAfterTotalAmount(orderInfo, orderInfo.getPlayMode());
            }

            @Override
            public void onLoaded(Response<Map<String, String>> result) throws Exception {
                dialog.dismiss();
                if (LotterySettleActivity.this == null || LotterySettleActivity.this.isFinishing())
                    return;
                if (!mIsError) {
                    if (result.getSuccess()) {
                        try {
                            double d = Double.parseDouble(result.getData().get("totalMoney"));
                            tvTotalAmount.setText(StringUtil.formatDoubleWith4Point(d));
                        } catch (Exception e) {
                            TestUtil.print(e.getLocalizedMessage());
                        }
                    } else {
                        Toast.makeText(LotterySettleActivity.this, result.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    BaseApp.changeUrl(LotterySettleActivity.this, new BaseApp.OnChangeUrlListener() {
                        @Override
                        public void changeSuccess() {
                            getAfterTotalAmount();
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


    public void calculateAfterAmount() {

        try {
            int multiple = Integer.parseInt(txtMultiple.getText().toString());
            int period = Integer.parseInt(txtPeriods.getText().toString());

            if (multiple <= 0 || period <= 0) {
                int totalQty = 0;
                double totalAmount = 0;
                double totalManualBet = 0;
                for (BetSettleInfo b : settleBets) {
                    totalQty += b.getAmount();
                    totalManualBet += b.getManualBet();
                    if (b.getManualBet() > 0.0) {
                        totalAmount += b.getManualBet() * orderInfo.getPrice();
                    } else {
                        totalAmount += b.getAmount() * orderInfo.getPrice();
                    }
                }
                orderInfo.setQty(totalQty);
                orderInfo.setManualBet(totalManualBet);
                tvTotalAmount.setText(StringUtil.formatDoubleWith4Point(totalAmount));
                return;
            }
            if (settleBets.size() == 0) {
//                Toast.makeText(LotterySettleActivity.this, "没有选择号码！", Toast.LENGTH_LONG).show();
                tvTotalAmount.setText("0.00");
                return;
            }
            String selectedNumbers = "";
            for (BetSettleInfo b : settleBets) {
                String selectnum = "";
                if (b.getSelectedNums().substring(0, 1).equals(",")) {
                    selectnum = b.getSelectedNums().substring(1, b.getSelectedNums().length());
                } else {
                    selectnum = b.getSelectedNums();
                }
                selectedNumbers += selectnum + "|";
            }
            if (selectedNumbers.length() <= 0) {
                MyToast.show(getApplicationContext(), "投注项不能为空！");
                return;
            }
            orderInfo.setSelectedNums(selectedNumbers.substring(0, selectedNumbers.length() - 1));
            orderInfo.setIsWinStop(chkIsWinStop.isChecked());
            orderInfo.setMultiple(multiple);
            orderInfo.setPeriods(period);
            orderInfo.setIsAfter(orderInfo.getMultiple() > 0 && orderInfo.getPeriods() > 0);
            if (nextIssueNoFragment.getCurrentIssueNo() == null || (nextIssueNoFragment.getCurrentIssueNo().compareTo("") == 0)) {
                MyToast.show(LotterySettleActivity.this, "期号正在获取中，请稍后...");
                return;
            }
            orderInfo.setCurrentIssueNo(nextIssueNoFragment.getCurrentIssueNo());
            getAfterTotalAmount();

        } catch (Exception e) {

        }
    }

    private boolean isSingle() {
        return (orderInfo.getPlayTypeRadioName().contains("单式") ||
                orderInfo.getPlayTypeRadioName().equals("混合组选") ||
                orderInfo.getPlayTypeName().contains("单式"));
    }
}
