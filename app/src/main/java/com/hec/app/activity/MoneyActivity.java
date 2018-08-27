package com.hec.app.activity;

import android.app.ProgressDialog;
import android.support.design.widget.TabLayout;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonParseException;
import com.hec.app.R;
import com.hec.app.activity.base.BaseActivityWithMenu;
import com.hec.app.activity.base.BaseApp;
import com.hec.app.entity.BizException;
import com.hec.app.entity.MoneyFundInfo;
import com.hec.app.fragment.RecordListFragment;
import com.hec.app.framework.widget.MyToast;
import com.hec.app.framework.widget.ResideMenu;
import com.hec.app.util.DialogUtil;
import com.hec.app.util.IntentUtil;
import com.hec.app.util.MyAsyncTask;
import com.hec.app.webservice.MoneyService;
import com.hec.app.webservice.RequestAnno;
import com.hec.app.webservice.ServiceException;

import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

public class MoneyActivity extends BaseActivityWithMenu implements View.OnClickListener{

    private ImageView imgPerson;
    private ImageView imgBack;
    private LinearLayout layout1;
    private LinearLayout layout2;
    private LinearLayout layout3;
    private LinearLayout layout4;
    private boolean mIsError;
    TabLayout tabLayout;
    private ProgressDialog progressDialog;
    private Map<String,MoneyFundInfo> MoneyFundInfoMap = new HashMap<>();
    TextView mBalance,mWinLoss,mPrice,mBet,mMoneyin,mMoneyout;
    private final static String TYPE_TODAY = "1";
    private final static String TYPE_THREEDAYS = "2";
    private final static String TYPE_WEEK = "3";
    private final static String TYPE_MONTH = "4";
    private LinearLayout button_money_withdraw;
    private LinearLayout button_money_recharge;
    private ResideMenu resideMenu;
    private ImageView imgUpDown;
    private String mType = TYPE_TODAY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_money);
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
        showProgressDialog("正在加载今日资金明细");
        tabLayout = (TabLayout) findViewById(R.id.moneyTabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("今日"));
        tabLayout.addTab(tabLayout.newTab().setText("3天内"));
        tabLayout.addTab(tabLayout.newTab().setText("本周"));
        //tabLayout.addTab(tabLayout.newTab().setText("本月"));
        setonTabClickListener();
        layout1 = (LinearLayout)findViewById(R.id.content_money_detail_ll1);
        layout2 = (LinearLayout)findViewById(R.id.content_money_detail_ll2);
        layout3 = (LinearLayout)findViewById(R.id.content_money_detail_ll3);
        layout4 = (LinearLayout)findViewById(R.id.content_money_detail_ll4);
        layout1.setOnClickListener(this);
        layout2.setOnClickListener(this);
        layout3.setOnClickListener(this);
        layout4.setOnClickListener(this);
        button_money_recharge = (LinearLayout) findViewById(R.id.button_money_recharge);
        button_money_withdraw = (LinearLayout) findViewById(R.id.button_money_withdraw);
        button_money_recharge.setOnClickListener(this);
        button_money_withdraw.setOnClickListener(this);
        //getMoneyFundInfo(TYPE_TODAY);
        //getMoneyFundInfo(TYPE_TODAY);
        imgUpDown = (ImageView)findViewById(R.id.imgIcon_up_down);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getMoneyFundInfo((tabLayout.getSelectedTabPosition()+1)+"");
    }

    private void setonTabClickListener() {
        if(tabLayout!=null){
            tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    if(tab.getText().equals("今日")){
                        showProgressDialog("正在加载今日资金明细");
                            getMoneyFundInfo(TYPE_TODAY);
                    }else if(tab.getText().equals("3天内")){
                        showProgressDialog("正在加载3天内资金明细");
                            getMoneyFundInfo(TYPE_THREEDAYS);
                    }else if(tab.getText().equals("本周")){
                        showProgressDialog("正在加载本周资金明细");
                            getMoneyFundInfo(TYPE_WEEK);
                    }else if(tab.getText().equals("本月")){
                        showProgressDialog("正在加载本月资金明细");
                            getMoneyFundInfo(TYPE_MONTH);
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

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v == layout1)
            IntentUtil.redirectToNextActivity(this, RecordListActivity.class, RecordListFragment.ARGUMENT, "奖金派送");
        else if(v == layout2)
            IntentUtil.redirectToNextActivity(this, RecordListActivity.class, RecordListFragment.ARGUMENT, "充值&转入");
        else if(v == layout3)
            IntentUtil.redirectToNextActivity(this, RecordListActivity.class, RecordListFragment.ARGUMENT, "投注支出");
        else if(v == layout4)
            IntentUtil.redirectToNextActivity(this, RecordListActivity.class, RecordListFragment.ARGUMENT, "提现&转出");
        else if(v == button_money_recharge)
            IntentUtil.redirectToNextActivity(this, RechargeMainActivity.class);
        else if(v == button_money_withdraw)
            IntentUtil.redirectToNextActivity(this, WithdrawActivity.class);
    }

    @RequestAnno
    public void getMoneyFundInfo(final String orderType){
        mIsError = false;
        mType = orderType;

        MyAsyncTask<MoneyFundInfo> task = new MyAsyncTask<MoneyFundInfo>(this) {

            @Override
            public MoneyFundInfo callService() throws IOException,
                    JsonParseException, BizException, ServiceException {
                return new MoneyService(MoneyActivity.this).getMoneyFundInfo(orderType);
            }

            @Override
            public void onLoaded(MoneyFundInfo result) throws Exception {
                if(MoneyActivity.this == null || MoneyActivity.this.isFinishing()) {
                    return;
                }

                if (!mIsError) {
                    MoneyFundInfoMap.put(orderType, result);
                    if (mBalance==null) {
                        initview();
                    }

                    DecimalFormat decimalFormatFour = new DecimalFormat("#.####");
                    decimalFormatFour.setRoundingMode(RoundingMode.DOWN);

                    DecimalFormat decimalFormatTwo = new DecimalFormat("#.##");
                    decimalFormatTwo.setRoundingMode(RoundingMode.DOWN);

                    mBalance.setText(decimalFormatFour.format(result.getBalance()));
                    mWinLoss.setText(decimalFormatFour.format(result.getWinLoss()));
                    mPrice.setText(decimalFormatTwo.format(result.getPrize()));
                    mBet.setText(decimalFormatTwo.format(result.getBet()));
                    mMoneyin.setText(decimalFormatTwo.format(result.getMoneyin()));
                    mMoneyout.setText(decimalFormatTwo.format(result.getMoneyout()));

                    if (result.getWinLoss() >= 0) {
                        imgUpDown.setImageResource(R.mipmap.icon_increase);
                    } else {
                        imgUpDown.setImageResource(R.mipmap.icon_decrease);
                    }
                }
                else {
                    BaseApp.changeUrl(MoneyActivity.this, new BaseApp.OnChangeUrlListener() {
                        @Override
                        public void changeSuccess() {
                            getMoneyFundInfo(orderType);
                        }

                        @Override
                        public void changeFail() {}
                    });
                }
                closeProgressDialog();
            }
        };
        task.setOnError(new MyAsyncTask.OnError() {

            @Override
            public void handleError(Exception e) {
                closeProgressDialog();
                mIsError = true;
            }
        });
        task.executeTask();
    }

    private void initview() {

        mBalance = (TextView) findViewById(R.id.money_balance_amount);
        mWinLoss = (TextView) findViewById(R.id.money_profitloss_amount);
        mPrice = (TextView) findViewById(R.id.money_bonus_amount);
        mBet = (TextView) findViewById(R.id.money_bet_amount);
        mMoneyin = (TextView) findViewById(R.id.money_recharge_amount);
        mMoneyout = (TextView) findViewById(R.id.money_withdraw_amount);
    }

    private void showProgressDialog(String loadingMessage){
        try {
            progressDialog = DialogUtil.getProgressDialog(this, loadingMessage);
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

    public String getType() {
        return mType;
    }
}
