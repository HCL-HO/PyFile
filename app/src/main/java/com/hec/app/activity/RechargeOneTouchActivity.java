package com.hec.app.activity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonParseException;
import com.hec.app.R;
import com.hec.app.activity.base.BaseActivityWithMenu;
import com.hec.app.activity.base.BaseApp;
import com.hec.app.config.CommonConfig;
import com.hec.app.entity.AlipayInfo;
import com.hec.app.entity.BankTypeInfo;
import com.hec.app.entity.BizException;
import com.hec.app.entity.Response;
import com.hec.app.framework.widget.MyToast;
import com.hec.app.framework.widget.ResideMenu;
import com.hec.app.util.MyAsyncTask;
import com.hec.app.webservice.RechargeService;
import com.hec.app.webservice.ServiceException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class RechargeOneTouchActivity extends BaseActivityWithMenu implements View.OnClickListener {
    private RechargeService mRechargeService;
    private ResideMenu mResideMenu;
    private List<BankTypeInfo> mBankTypeList;
    private List<String> mLines;

    private EditText mEditTextAmount;
    private ImageView mImgErrorCross;
    private RelativeLayout mRelativeLayoutMoney;
    private LinearLayout  mLayoutError;
    private Spinner mSpinnerRechargeLine;
    private ArrayAdapter mAdapter;
    private TextView mTextViewError;

    private boolean mIsError = false;
    private boolean mIsPressed = false;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage (Message msg) {
            super.handleMessage(msg);
            closeLoading();

            if (msg.what == CommonConfig.HANDLER_RECHARGE_RESULT) {
                String data = msg.getData().getString(CommonConfig.BUNDLE_RECHARGE_DATA);
                if (!data.isEmpty()) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(data));
                    startActivity(browserIntent);
                }
                else {
                    MyToast.show(getBaseContext(), getResources().getString(R.string.error_service));
                }
            }
            else if (msg.what == CommonConfig.HANDLER_RECHARGE_ADD_BANK_TYPE) {
                mLines.clear();
                if (mBankTypeList != null) {
                    for (BankTypeInfo info : mBankTypeList) {
                        mLines.add(info.getBankTypeName());
                    }
                }

                mAdapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge_one_touch);

        mRechargeService = new RechargeService();
        mResideMenu = super.getResidingMenu();

        mBankTypeList = new ArrayList<>();
        mLines = new ArrayList<>();

        ImageView imgBack = (ImageView)findViewById(R.id.imgBack);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        mImgErrorCross = (ImageView)findViewById(R.id.img_error_cross);
        mRelativeLayoutMoney = (RelativeLayout)findViewById(R.id.relative_layout_money);
        mLayoutError = (LinearLayout)findViewById(R.id.layout_error);
        mTextViewError = (TextView)findViewById(R.id.recharge_moneysum_textview_cant_empty);

        mEditTextAmount = (EditText)findViewById(R.id.recharge_moneysum_edittext);
        mEditTextAmount.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                setEditorAction();
                return false;
            }
        });

        RelativeLayout btn10 = (RelativeLayout)findViewById(R.id.recharge_10_btn);
        btn10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditTextAmount.setText("10");
                setEditorAction();
            }
        });

        RelativeLayout btn100 = (RelativeLayout)findViewById(R.id.recharge_100_btn);
        btn100.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditTextAmount.setText("100");
                setEditorAction();
            }
        });

        RelativeLayout btn200 = (RelativeLayout)findViewById(R.id.recharge_200_btn);
        btn200.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditTextAmount.setText("200");
                setEditorAction();
            }
        });

        RelativeLayout btn500 = (RelativeLayout)findViewById(R.id.recharge_500_btn);
        btn500.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditTextAmount.setText("500");
                setEditorAction();
            }
        });

        RelativeLayout btn1000 = (RelativeLayout)findViewById(R.id.recharge_1000_btn);
        btn1000.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditTextAmount.setText("1000");
                setEditorAction();
            }
        });

        mAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, mLines);
        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerRechargeLine = (Spinner)findViewById(R.id.recharge_line_spinner);
        mSpinnerRechargeLine.setAdapter(mAdapter);
        mSpinnerRechargeLine.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setAmountErrorView(position, false);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        LinearLayout btnRecharge = (LinearLayout)findViewById(R.id.alipay_recharge_btn);
        btnRecharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rechargeClicked();
            }
        });

        getBankTypes();
    }

    public void setEditorAction() {
        if (!mEditTextAmount.getText().toString().isEmpty()) {
            mImgErrorCross.setVisibility(View.GONE);
            mLayoutError.setVisibility(View.GONE);
            mRelativeLayoutMoney.setBackgroundResource(0);
            mRelativeLayoutMoney.setBackgroundColor(Color.parseColor("#f2f2f2"));
        }

        setAmountErrorView(mSpinnerRechargeLine.getSelectedItemPosition(), false);

        InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void rechargeClicked() {
        int position = mSpinnerRechargeLine.getSelectedItemPosition();
        if (mBankTypeList == null || mBankTypeList.isEmpty() || position >= mBankTypeList.size() || position < 0) {
            MyToast.show(RechargeOneTouchActivity.this, getResources().getString(R.string.recharge_line_empty));
            return;
        }

        String amount = mEditTextAmount.getText().toString();
        if (amount.isEmpty()) {
            mLayoutError.setVisibility(View.VISIBLE);
            mTextViewError.setText(R.string.recharge_amount_empty);

            mImgErrorCross.setVisibility(View.VISIBLE);
            mRelativeLayoutMoney.setBackgroundResource(R.drawable.rect_no_round);
            return;
        }
        else {
            if (!setAmountErrorView(position, true)) {
                return;
            }
        }

        if (!mIsPressed) {
            mIsPressed = true;
            showLoading(getString(R.string.loading_message_recharge));
            startRecharge(mBankTypeList.get(position).getBankTypeID(), amount);
        }
    }

    private boolean setAmountErrorView(int position, boolean isShowToast) {
        if (mBankTypeList == null) {
            return false;
        }

        if (!mBankTypeList.isEmpty() && position < mBankTypeList.size() && mBankTypeList.size() >= 0) {
            BankTypeInfo info = mBankTypeList.get(position);
            String amountStr = mEditTextAmount.getText().toString();

            if (!amountStr.isEmpty()) {
                double amount = Double.parseDouble(amountStr);
                int minAmount = 0;
                int maxAmount = 10000000;
                if (info.getMoneyInType() == CommonConfig.MONEY_IN_TYPE_ALIPAT) {
                    minAmount = 50;
                    maxAmount = 1000;
                }
                else if (info.getMoneyInType() == CommonConfig.MONEY_IN_TYPE_WECHAT) {
                    minAmount = 10;
                    maxAmount = 3000;
                }
                else if (info.getMoneyInType() == CommonConfig.MONEY_IN_TYPE_QQ) {
                    minAmount = 10;
                    maxAmount = 5000;
                }
                else if (info.getMoneyInType() == CommonConfig.MONEY_IN_TYPE_JD_PAY) {
                    minAmount = 10;
                    maxAmount = 5000;
                }
                else if (info.getMoneyInType() == 11) {
                    minAmount = 1;
                    maxAmount = 50000;
                }
                else if (info.getMoneyInType() == CommonConfig.MONEY_IN_TYPE_WL_PAY) {
                    minAmount = 10;
                    maxAmount = 1000;
                }



                if (amount > maxAmount || amount < minAmount) {
                    String errorStr = String.format(getString(R.string.recharge_amount_limit), minAmount, maxAmount);

                    mLayoutError.setVisibility(View.VISIBLE);
                    mTextViewError.setText(errorStr);
                    if (isShowToast) {
                        MyToast.show(RechargeOneTouchActivity.this, errorStr);
                    }
                    return false;
                }
                else {
                    mLayoutError.setVisibility(View.GONE);
                    return true;
                }
            }
        }

        return false;
    }

    public void startRecharge(final String bankTypeIDorName, final String amount) {
        mIsError = false;
        MyAsyncTask<Response<AlipayInfo>> task = new MyAsyncTask<Response<AlipayInfo>>(this) {

            @Override
            public Response<AlipayInfo> callService() throws IOException, JsonParseException, BizException, ServiceException {
                return mRechargeService.submitOneTouch(bankTypeIDorName, amount);
            }

            @Override
            public void onLoaded(Response<AlipayInfo> result) throws Exception {
                if(RechargeOneTouchActivity.this == null || RechargeOneTouchActivity.this.isFinishing()) {
                    return;
                }

                closeLoading();
                mIsPressed = false;
                if(!mIsError){
                    if(result.getSuccess()) {
                        Bundle bundle = new Bundle();
                        bundle.putString(CommonConfig.BUNDLE_RECHARGE_DATA, result.getData().getURL());

                        Message message = Message.obtain();
                        message.what = CommonConfig.HANDLER_RECHARGE_RESULT;
                        message.setData(bundle);
                        mHandler.sendMessage(message);
                    }
                    else {
                        MyToast.show(RechargeOneTouchActivity.this, result.getMessage());
                    }
                }
                else {
                    BaseApp.getAppBean().resetApiUrl(RechargeOneTouchActivity.this);
                    Toast.makeText(RechargeOneTouchActivity.this, getResources().getString(R.string.error_message_recharge_network), Toast.LENGTH_LONG).show();
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

    private void getBankTypes() {
        mIsError = false;
        MyAsyncTask<List<BankTypeInfo>> task = new MyAsyncTask<List<BankTypeInfo>>(this) {
            @Override
            public List<BankTypeInfo> callService() throws IOException, JsonParseException, BizException, ServiceException {
                return mRechargeService.getOneTouchRechargeList("0");
            }

            @Override
            public void onLoaded(List<BankTypeInfo> result) throws Exception {
                if(RechargeOneTouchActivity.this == null || RechargeOneTouchActivity.this.isFinishing()) {
                    return;
                }

                closeLoading();
                if (!mIsError) {
                    mBankTypeList = result;
                    mHandler.sendEmptyMessage(CommonConfig.HANDLER_RECHARGE_ADD_BANK_TYPE);
                }
                else {
                    BaseApp.changeUrl(RechargeOneTouchActivity.this, new BaseApp.OnChangeUrlListener() {
                        @Override
                        public void changeSuccess() {
                            getBankTypes();
                        }

                        @Override
                        public void changeFail() {}
                    });
                }
            }
        };
        task.setOnError(new MyAsyncTask.OnError() {
            @Override
            public void handleError(Exception e) {
                mBankTypeList = new ArrayList<>();
            }
        });
        task.executeTask();
    }

    @Override
    public void onClick(View v){
        super.onClick(v);
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
    }
}
