package com.hec.app.activity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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

public class RechargeJdPayActivity extends BaseActivityWithMenu implements View.OnClickListener {
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
        setContentView(R.layout.activity_recharge_jd_pay);

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

        LinearLayout btnRecharge = (LinearLayout)findViewById(R.id.alipay_recharge_btn);
        btnRecharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amount = mEditTextAmount.getText().toString();
                if(amount.isEmpty()) {
                    mLayoutError.setVisibility(View.VISIBLE);
                    mImgErrorCross.setVisibility(View.VISIBLE);
                    mTextViewError.setText(R.string.recharge_amount_empty);
                    mRelativeLayoutMoney.setBackgroundResource(R.drawable.rect_no_round);
                    return;
                }

                if(!amount.isEmpty() && mBankTypeList != null && !mBankTypeList.isEmpty() &&
                        mSpinnerRechargeLine.getSelectedItemPosition() < mBankTypeList.size() && mSpinnerRechargeLine.getSelectedItemPosition() >= 0) {

                    if(Double.parseDouble(amount) > 5000 || Double.parseDouble(amount) < 10){
                        MyToast.show(RechargeJdPayActivity.this, getResources().getString(R.string.recharge_qq_amount_limit));
                    }
                    else if (!mIsPressed) {
                        mIsPressed = true;
                        showLoading(getString(R.string.loading_message_recharge));
                        startRecharge(mBankTypeList.get(mSpinnerRechargeLine.getSelectedItemPosition()).getBankTypeID(), amount);
                    }
                }
                else {
                    MyToast.show(RechargeJdPayActivity.this, getResources().getString(R.string.recharge_line_empty));
                }
            }
        });

        LinearLayout linearLayout = (LinearLayout)findViewById(R.id.linear_layout);
        linearLayout.setVisibility(View.VISIBLE);
        getBankTypes();
    }

    public void setEditorAction() {
        if (!mEditTextAmount.getText().toString().isEmpty()) {
            mImgErrorCross.setVisibility(View.GONE);
            mLayoutError.setVisibility(View.GONE);
            mRelativeLayoutMoney.setBackgroundResource(0);
            mRelativeLayoutMoney.setBackgroundColor(Color.parseColor("#f2f2f2"));
        }

        mAdapter.notifyDataSetChanged();

        String amountStr = mEditTextAmount.getText().toString();
        if (!amountStr.isEmpty()) {
            double amount = Double.parseDouble(amountStr);
            if(amount > 5000 || amount < 10){
                mLayoutError.setVisibility(View.VISIBLE);
                mTextViewError.setText(R.string.recharge_qq_amount_limit);
            }
        }

        InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public void startRecharge(final String bankTypeIDorName, final String amount) {
        mIsError = false;
        MyAsyncTask<Response<AlipayInfo>> task = new MyAsyncTask<Response<AlipayInfo>>(this) {

            @Override
            public Response<AlipayInfo> callService() throws IOException, JsonParseException, BizException, ServiceException {
                return mRechargeService.submitJDPay(bankTypeIDorName, amount);
            }

            @Override
            public void onLoaded(Response<AlipayInfo> result) throws Exception {
                if(RechargeJdPayActivity.this == null || RechargeJdPayActivity.this.isFinishing()) {
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
                        MyToast.show(RechargeJdPayActivity.this, result.getMessage());
                    }
                }
                else {
                    BaseApp.getAppBean().resetApiUrl(RechargeJdPayActivity.this);
                    Toast.makeText(RechargeJdPayActivity.this, getResources().getString(R.string.error_message_recharge_network), Toast.LENGTH_LONG).show();
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

    private void getBankTypes () {
        mIsError = false;
        MyAsyncTask<List<BankTypeInfo>> task = new MyAsyncTask<List<BankTypeInfo>>(this) {
            @Override
            public List<BankTypeInfo> callService() throws IOException, JsonParseException, BizException, ServiceException {
                return mRechargeService.getJdPayRechargeList("10");
            }

            @Override
            public void onLoaded(List<BankTypeInfo> result) throws Exception {
                if(RechargeJdPayActivity.this == null || RechargeJdPayActivity.this.isFinishing()) {
                    return;
                }

                closeLoading();
                if (!mIsError) {
                    mBankTypeList = result;
                    mHandler.sendEmptyMessage(CommonConfig.HANDLER_RECHARGE_ADD_BANK_TYPE);
                }
                else {
                    BaseApp.changeUrl(RechargeJdPayActivity.this, new BaseApp.OnChangeUrlListener() {
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
