package com.hec.app.activity;

import android.content.ClipboardManager;
import android.content.Context;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hec.app.R;
import com.hec.app.entity.AliPayNewInfo;
import com.hec.app.framework.widget.MyToast;
import com.hec.app.util.DialogUtil;
import com.hec.app.util.ImageDialog;
import com.hec.app.util.IntentUtil;

public class RechargeAlipaySuccessActivity extends AppCompatActivity {
    private TextView tv_accountname, tv_bankaccount, tv_bankname, fuzhi2, fuzhi3, fuzhi5, tv_time, tv_amount;
    private LinearLayout confirmBtn, guideBtn;
    private AliPayNewInfo aliPayNewInfo;
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ali_success);
        aliPayNewInfo = (AliPayNewInfo) getIntent().getSerializableExtra("alipay");
        tv_accountname = (TextView) findViewById(R.id.tv_accountname);
        tv_bankname = (TextView) findViewById(R.id.tv_bankname);
        tv_bankaccount = (TextView) findViewById(R.id.tv_bankaccount);
        tv_amount = (TextView) findViewById(R.id.tv_amount);
        fuzhi2 = (TextView) findViewById(R.id.fuzhi2);
        fuzhi3 = (TextView) findViewById(R.id.fuzhi3);
        fuzhi5 = (TextView) findViewById(R.id.fuzhi5);
        tv_time = (TextView) findViewById(R.id.tv_time);
        guideBtn = (LinearLayout) findViewById(R.id.guide_btn);
        guideBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ImageDialog(RechargeAlipaySuccessActivity.this, R.mipmap.zfb_step_main).show();
            }
        });
        confirmBtn = (LinearLayout) findViewById(R.id.confirm_btn);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentUtil.redirectToNextActivity(RechargeAlipaySuccessActivity.this, RecordListActivity.class, "argument", "充值&转入");
                finish();
            }
        });

        initView();
    }

    public void backClick(View v) {
        finish();
    }

    private void initView() {
        countDownTimer = new CountDownTimer(300 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                customClock(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                MyToast.show(RechargeAlipaySuccessActivity.this, getString(R.string.recharge_success_time_up));
                finish();
            }
        };
        countDownTimer.start();
        if (aliPayNewInfo != null) {
            tv_accountname.setText(aliPayNewInfo.getBankUser());
            tv_bankname.setText(aliPayNewInfo.getBankName());
            tv_bankaccount.setText(aliPayNewInfo.getBankCard());
            tv_amount.setText(aliPayNewInfo.getAmount() + "元");
            fuzhi2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!aliPayNewInfo.getBankUser().isEmpty()) {
                        ClipboardManager cmb = (ClipboardManager) RechargeAlipaySuccessActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
                        cmb.setText(aliPayNewInfo.getBankUser());
                        MyToast.show(RechargeAlipaySuccessActivity.this, "已复制收款账户名");
                    }
                }
            });
            fuzhi3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!aliPayNewInfo.getBankCard().isEmpty()) {
                        ClipboardManager cmb = (ClipboardManager) RechargeAlipaySuccessActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
                        cmb.setText(aliPayNewInfo.getBankCard());
                        MyToast.show(RechargeAlipaySuccessActivity.this, "已复制收款账户");
                    }
                }
            });
            fuzhi5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!aliPayNewInfo.getBankCard().isEmpty()) {
                        ClipboardManager cmb = (ClipboardManager) RechargeAlipaySuccessActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
                        cmb.setText(Double.toString(aliPayNewInfo.getAmount()));
                        MyToast.show(RechargeAlipaySuccessActivity.this, "已复制转帐金额");
                    }
                }
            });
        }
        DialogUtil.getAlertDialog(this, getString(R.string.friendly_reminder), getString(R.string.recharge_hint), getString(R.string.confirm_send), null, null, null).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    private void customClock(long time) {
        int seconds = (int) time / 1000;
        String hour = "", minute = "", second = "";
        hour = String.valueOf((seconds / 60) / 60);
        if (hour.length() == 1) hour = "0" + hour;
        minute = String.valueOf((seconds / 60) % 60);
        if (minute.length() == 1) minute = "0" + minute;
        second = String.valueOf(seconds % 60);
        if (second.length() == 1) second = "0" + second;
        tv_time.setText(minute + ":" + second);
    }
}
