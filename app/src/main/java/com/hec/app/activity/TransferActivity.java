package com.hec.app.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonParseException;
import com.hec.app.R;
import com.hec.app.activity.base.BaseActivity;
import com.hec.app.config.CommonConfig;
import com.hec.app.entity.BizException;
import com.hec.app.entity.CustomerInfo;
import com.hec.app.entity.HomeBalanceInfo;
import com.hec.app.util.CustomerAccountManager;
import com.hec.app.util.MyAsyncTask;
import com.hec.app.util.SystemBarTintManager;
import com.hec.app.webservice.AccountService;
import com.hec.app.webservice.ServiceException;

import java.io.IOException;
import java.text.DecimalFormat;

public class TransferActivity extends BaseActivity implements View.OnClickListener{
    private LinearLayout mLlRealman;
    private LinearLayout mLlPt;
    private LinearLayout mLlSports;
    private TextView mTxAvailableScores;
    private TextView mTxFreezeScores;
    private TextView mTxUserName;
    private TextView mTxRealmanRemain;
    private TextView mTxPtRemain;
    private TextView mTxSportsRemain;
    private boolean mIsError;
    private String mAgAvaliableScores;
    private String mSportAvaliableScores;
    private String mPTAvaliableScores;
    private String mAgFreezeScores;
    private String mSportFreezeScores;
    private String mPTFreezeScores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer);

        ImageView imgBack = (ImageView)findViewById(R.id.imgBack);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        getHomeBalanceInfo();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.colorPrimary);
        }

        mTxUserName = (TextView) findViewById(R.id.transfer_username);
        mTxAvailableScores = (TextView) findViewById(R.id.transfer_head_value1);
        mTxRealmanRemain = (TextView) findViewById(R.id.transfer_realman_remain);
        mTxPtRemain = (TextView) findViewById(R.id.transfer_pt_remain);
        mTxSportsRemain = (TextView) findViewById(R.id.transfer_sports_remain);
        mTxFreezeScores = (TextView) findViewById(R.id.transfer_head_value2);

        mLlRealman = (LinearLayout) findViewById(R.id.transfer_realman);
        mLlPt = (LinearLayout) findViewById(R.id.transfer_pt);
        mLlSports = (LinearLayout) findViewById(R.id.transfer_sports);
        mLlRealman.setOnClickListener(this);
        mLlPt.setOnClickListener(this);
        mLlSports.setOnClickListener(this);
    }

    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        }
        else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        if (v == mLlRealman) {
            intent.putExtra(CommonConfig.INTENT_TRANSFER_PLAYTYPE, CommonConfig.TRANSFER_PLAYTYPE_REALMAN);
            intent.putExtra(CommonConfig.INTENT_TRANSFER_AVALIBALE, mAgAvaliableScores);
            intent.putExtra(CommonConfig.INTENT_TRANSFER_FREEZE, mAgFreezeScores);
        }
        else if (v == mLlPt) {
            intent.putExtra(CommonConfig.INTENT_TRANSFER_PLAYTYPE, CommonConfig.TRANSFER_PLAYTYPE_PT);
            intent.putExtra(CommonConfig.INTENT_TRANSFER_AVALIBALE, mPTAvaliableScores);
            intent.putExtra(CommonConfig.INTENT_TRANSFER_FREEZE, mPTFreezeScores);
        }
        else if (v == mLlSports) {
            intent.putExtra(CommonConfig.INTENT_TRANSFER_PLAYTYPE, CommonConfig.TRANSFER_PLAYTYPE_SPORTS);
            intent.putExtra(CommonConfig.INTENT_TRANSFER_AVALIBALE, mSportAvaliableScores);
            intent.putExtra(CommonConfig.INTENT_TRANSFER_FREEZE, mSportFreezeScores);
        }

        //TODO:according to API,here we may also transfer remain and frozen data.
        intent.setClass(TransferActivity.this, TransferDetailActivity.class);
        startActivity(intent);
    }

    public void getHomeBalanceInfo(){
        mIsError = false;
        MyAsyncTask<HomeBalanceInfo> task = new MyAsyncTask<HomeBalanceInfo>(this) {
            @Override
            public HomeBalanceInfo callService() throws IOException, JsonParseException, BizException, ServiceException {
                return new AccountService().getTransferBalanceInfo();
            }

            @Override
            public void onLoaded(HomeBalanceInfo result) throws Exception {
                if (TransferActivity.this == null || TransferActivity.this.isFinishing()) {
                    return;
                }

                if (!mIsError) {
                    DecimalFormat decimalFormat = new DecimalFormat("#.##");
                    double scores = Double.parseDouble(result.getAvailableScores().replace(",", ""));
                    mTxAvailableScores.setText(decimalFormat.format(scores));
                    scores = Double.parseDouble(result.getFreezeScores().replace(",", ""));
                    mTxFreezeScores.setText(decimalFormat.format(scores));

                    CustomerInfo customer = CustomerAccountManager.getInstance().getCustomer();
                    if (customer != null) {
                        mTxUserName.setText(customer.getUserName());
                    }

                    mAgAvaliableScores = result.getAgAvaliableScores();
                    mAgFreezeScores = result.getAgFreezeScores();
                    mPTAvaliableScores = result.getPTAvaliableScores();
                    mPTFreezeScores = result.getPTFreezeScores();
                    mSportAvaliableScores = result.getSportAvaliableScores();
                    mSportFreezeScores = result.getSportFreezeScores();

                    mTxRealmanRemain.setText(mAgAvaliableScores);
                    mTxPtRemain.setText(mPTAvaliableScores);
                    mTxSportsRemain.setText(mSportAvaliableScores);
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

    @Override
    protected void onResume() {
        super.onResume();
        getHomeBalanceInfo();
    }
}
