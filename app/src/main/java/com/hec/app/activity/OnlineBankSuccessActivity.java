package com.hec.app.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextPaint;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hec.app.R;
import com.hec.app.activity.base.BaseActivity;
import com.hec.app.entity.OnlineBankInfo;
import com.hec.app.framework.widget.MyToast;
import com.hec.app.util.IntentUtil;

public class OnlineBankSuccessActivity extends BaseActivity {

    TextView tv_fuyan, tv_accountname, tv_bankaccount, tv_bankname, fuzhi1, fuzhi2, fuzhi3, fuzhi5, tv_time, tv_amount;
    LinearLayout onlinebank_confirm_btn;
    OnlineBankInfo onlineBankInfo;
    CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onlineBankInfo = (OnlineBankInfo) getIntent().getSerializableExtra("onlinebank");
        setContentView(R.layout.activity_online_bank_success);
        tv_fuyan = (TextView) findViewById(R.id.tv_fuyan);
        tv_accountname = (TextView) findViewById(R.id.tv_accountname);
        tv_bankname = (TextView) findViewById(R.id.tv_bankname);
        tv_bankaccount = (TextView) findViewById(R.id.tv_bankaccount);
        tv_amount = (TextView) findViewById(R.id.tv_amount);
        fuzhi1 = (TextView) findViewById(R.id.fuzhi1);
        fuzhi2 = (TextView) findViewById(R.id.fuzhi2);
        fuzhi3 = (TextView) findViewById(R.id.fuzhi3);
        fuzhi5 = (TextView) findViewById(R.id.fuzhi5);
        tv_time = (TextView) findViewById(R.id.tv_time);
        onlinebank_confirm_btn = (LinearLayout) findViewById(R.id.onlinebank_confirm_btn);
        onlinebank_confirm_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentUtil.redirectToNextActivity(OnlineBankSuccessActivity.this, RecordListActivity.class, "argument", "充值&转入");
                finish();
            }
        });
        initView();
    }

    public void backClick(View v) {
        finish();
    }

    private void initView() {
        countDownTimer = new CountDownTimer(600 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                customClock(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                MyToast.show(OnlineBankSuccessActivity.this, "支付时间已过期！");
                finish();
            }
        };
        countDownTimer.start();
        if (onlineBankInfo != null) {
            Log.i("wxj", "onlineinfo not null" + onlineBankInfo.getAttachWord());
            tv_fuyan.setText(onlineBankInfo.getAttachWord());
            tv_accountname.setText(onlineBankInfo.getAdminBankBankUser());
            tv_bankname.setText(onlineBankInfo.getBankTypeName());
            tv_bankaccount.setText(onlineBankInfo.getAdminBankBankCard());
            tv_amount.setText(onlineBankInfo.getAmount() + "元");
            fuzhi1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!onlineBankInfo.getAttachWord().isEmpty()) {
                        String fuyan = onlineBankInfo.getAttachWord();
                        if (android.os.Build.VERSION.SDK_INT > 11) {
                            android.content.ClipboardManager clipboardManager = (android.content.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                            clipboardManager.setPrimaryClip(ClipData.newPlainText(null, fuyan));
                        } else {
                            android.text.ClipboardManager clipboardManager = (android.text.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                            clipboardManager.setText(fuyan);
                        }
                        MyToast.show(OnlineBankSuccessActivity.this, "已复制附言");
                    }
                }
            });
            fuzhi2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!onlineBankInfo.getAdminBankBankUser().isEmpty()) {
                        String accountName = onlineBankInfo.getAdminBankBankUser();
                        if (android.os.Build.VERSION.SDK_INT > 11) {
                            android.content.ClipboardManager clipboardManager = (android.content.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                            clipboardManager.setPrimaryClip(ClipData.newPlainText(null, accountName));
                        } else {
                            android.text.ClipboardManager clipboardManager = (android.text.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                            clipboardManager.setText(accountName);
                        }
                        MyToast.show(OnlineBankSuccessActivity.this, "已复制收款账户名");
                    }
                }
            });
            fuzhi3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!onlineBankInfo.getAdminBankBankCard().isEmpty()) {
                        String bankaAcount = onlineBankInfo.getAdminBankBankCard();
                        if (android.os.Build.VERSION.SDK_INT > 11) {
                            android.content.ClipboardManager clipboardManager = (android.content.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                            clipboardManager.setPrimaryClip(ClipData.newPlainText(null, bankaAcount));
                        } else {
                            android.text.ClipboardManager clipboardManager = (android.text.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                            clipboardManager.setText(bankaAcount);
                        }
                        MyToast.show(OnlineBankSuccessActivity.this, "已复制收款账户");
                    }
                }
            });
            fuzhi5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!onlineBankInfo.getAdminBankBankCard().isEmpty()) {
                        String amount = Float.toString(onlineBankInfo.getAmount());
                        if (android.os.Build.VERSION.SDK_INT > 11) {
                            android.content.ClipboardManager clipboardManager = (android.content.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                            clipboardManager.setPrimaryClip(ClipData.newPlainText(null, amount));
                        } else {
                            android.text.ClipboardManager clipboardManager = (android.text.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                            clipboardManager.setText(amount);
                        }
                        MyToast.show(OnlineBankSuccessActivity.this, "已复制转帐金额");
                    }
                }
            });
        }
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
