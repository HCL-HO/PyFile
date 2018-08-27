package com.hec.app.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.hec.app.R;
import com.hec.app.activity.base.BaseApp;
import com.hec.app.config.CommonConfig;
import com.hec.app.entity.BalanceInfo;
import com.hec.app.entity.BizException;
import com.hec.app.entity.PlaceOrderInfo;
import com.hec.app.fragment.RecordListFragment;
import com.hec.app.framework.http.OkHttpClientManager;
import com.hec.app.util.CustomerAccountManager;
import com.hec.app.util.IntentUtil;
import com.hec.app.util.LotteryUtil;
import com.hec.app.util.MyAsyncTask;
import com.hec.app.util.SlotUtl;
import com.hec.app.util.TestUtil;
import com.hec.app.webservice.AccountService;
import com.hec.app.webservice.BaseService;
import com.hec.app.webservice.ServiceException;
import com.unity3d.player.UnityPlayer;

import java.io.IOException;
import java.io.Serializable;

public class LotteryResultActivity extends AppCompatActivity {

    private PlaceOrderInfo mOrderInfo;
    private boolean mIsWait = false;
    private boolean mIsError = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lottery_result);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");
        toolbar.setNavigationIcon(R.mipmap.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Serializable serializableData = getIntent().getSerializableExtra("PlaceOrderInfo");
        if (serializableData != null) {
            mOrderInfo = (PlaceOrderInfo) serializableData;
        }

        findView();
    }

    private void findView() {
        if (mOrderInfo == null) {
            return;
        }

        TextView tvLotteryType = (TextView) findViewById(R.id.tvLotteryType);
        TextView tvPlayType = (TextView) findViewById(R.id.tvPlayType);
        TextView tvTotalAmount = (TextView) findViewById(R.id.tvTotalAmount);
        ImageView imgLottery = (ImageView) findViewById(R.id.lottery_result_image);
        tvLotteryType.setText(BaseApp.getHecReplaceString(mOrderInfo.getLotteryName()));
        tvPlayType.setText(String.format(getString(R.string.activity_lotteryresult_play_type), mOrderInfo.getPlayTypeName(), mOrderInfo.getPlayTypeRadioName()));
        tvTotalAmount.setText(String.format(getString(R.string.activity_lotteryresult_total_amount), mOrderInfo.getTotalAmount()));
        imgLottery.setImageResource(LotteryUtil.getLotteryIcon(mOrderInfo.getLotteryName()));

        TextView tvAfterTitle = (TextView) findViewById(R.id.tvAfterTitle);
        TextView tvAfterContent = (TextView) findViewById(R.id.tvAfterContent_start);
        TextView tvAfterNumber = (TextView) findViewById(R.id.tvAfterContent_follow);
        LinearLayout llFollow = (LinearLayout) findViewById(R.id.ll_follow);
        tvAfterTitle.setVisibility(View.GONE);
        tvAfterContent.setVisibility(View.GONE);
        tvAfterNumber.setVisibility(View.GONE);
        llFollow.setVisibility(View.GONE);

        if (mOrderInfo.getPeriods() > 0 && mOrderInfo.getMultiple() > 0) {
            tvAfterTitle.setVisibility(View.VISIBLE);
            tvAfterContent.setVisibility(View.VISIBLE);
            tvAfterNumber.setVisibility(View.VISIBLE);
            llFollow.setVisibility(View.VISIBLE);

            String last3 = mOrderInfo.getCurrentIssueNo().substring(mOrderInfo.getCurrentIssueNo().length() - 3, mOrderInfo.getCurrentIssueNo().length());
            int end = Integer.parseInt(last3) - 1 + mOrderInfo.getPeriods();

            tvAfterContent.setText(String.format(getString(R.string.activity_lotteryresult_after_content), mOrderInfo.getCurrentIssueNo(), end));
            tvAfterNumber.setText(String.format(getString(R.string.activity_lotteryresult_after_numbers), mOrderInfo.getPeriods()));
        }


        TextView tvHyperLink = (TextView)findViewById(R.id.hyperLinkTv);
        TextView goTiger = (TextView) findViewById(R.id.gotiger);
        LinearLayout llReturn = (LinearLayout) findViewById(R.id.ll_return);
        tvHyperLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentUtil.redirectToNextActivity(LotteryResultActivity.this, RecordListActivity.class, RecordListFragment.ARGUMENT, "投注记录");
                finish();
            }
        });
        goTiger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (!mIsWait) {
//                    mIsWait = true;
//                    mIsError = false;
//
//                    MyAsyncTask<BalanceInfo> task = new MyAsyncTask<BalanceInfo>(LotteryResultActivity.this) {
//                        @Override
//                        public BalanceInfo callService() throws IOException, JsonParseException, BizException, ServiceException {
//                            return new AccountService().getBalance();
//                        }
//
//                        @Override
//                        public void onLoaded(BalanceInfo data) throws Exception {
//                            mIsWait = false;
//                            if (!mIsError) {
                                String AASlotUrl = BaseService.SLOT_URL;
                                if (!AASlotUrl.contains("http://")) {
                                    AASlotUrl = "http://" + AASlotUrl;
                                }
                                if (AASlotUrl.charAt(AASlotUrl.length() - 1) != '/') {
                                    AASlotUrl = AASlotUrl + "/";
                                }
                                Bundle bundle = new Bundle();
                                bundle.putString(CommonConfig.BUNDLE_GOTIGER_USERNAME, OkHttpClientManager.getInstance().getUserName());
                                bundle.putInt(CommonConfig.BUNDLE_GOTIGER_BALANCE, (int)BaseService.BASE_BALANCE);
                                bundle.putString(CommonConfig.BUNDLE_GOTIGER_AASLOTURL, AASlotUrl);
                                bundle.putString(CommonConfig.BUNDLE_GOTIGER_SCENE,CommonConfig.DEFAULT_TAG);
                                IntentUtil.redirectToNextActivity(LotteryResultActivity.this, SlotActivity.class, bundle);
                                String slotData = SlotUtl.buildDataAccordingToScene(
                                        CommonConfig.DEFAULT_TAG,
                                        OkHttpClientManager.getInstance().getUserName(),
                                        AASlotUrl,
                                        BaseService.BASE_BALANCE
                                );
                                UnityPlayer.UnitySendMessage("Preload"
                                        , "getIntentData"
                                        , slotData);
                                finish();
//                            }
//                        }
//                    };
//                    task.setOnError(new MyAsyncTask.OnError() {
//                        @Override
//                        public void handleError(Exception e) {
//                            mIsError = true;
//                        }
//                    });
//                    task.executeTask();
//                }
            }
        });
        llReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        BaseApp.setCurrentActivity(this);
    }
}
