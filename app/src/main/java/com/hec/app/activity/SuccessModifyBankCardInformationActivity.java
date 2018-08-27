package com.hec.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonParseException;
import com.hec.app.R;
import com.hec.app.activity.base.BaseActivity;
import com.hec.app.activity.base.BaseApp;
import com.hec.app.entity.BalanceInfo;
import com.hec.app.entity.BizException;
import com.hec.app.util.MyAsyncTask;
import com.hec.app.webservice.AccountService;
import com.hec.app.webservice.ServiceException;

import java.io.IOException;
import java.text.DecimalFormat;

public class SuccessModifyBankCardInformationActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success_modify_bank_card_information);
        initView();
    }

    private void initView() {
        ImageView imgBack = (ImageView) findViewById(R.id.imgBack);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Intent intent = getIntent();
        String cardUser = intent.getStringExtra("CardUser");
        String bankName = intent.getStringExtra("BankName");
        String bankCard = intent.getStringExtra("BankCard");
        String bankProvince = intent.getStringExtra("BankProvince");
        String bankCity = intent.getStringExtra("BankCity");

        TextView successTv1 = (TextView) findViewById(R.id.success_tv1);
        if (!TextUtils.isEmpty(cardUser) && !TextUtils.isEmpty(bankName)) {
            successTv1.setText(Html.fromHtml("戶主 : " + cardUser + " <font color=#54a5a5>[" + bankName + "]</font>"));
        } else {
            successTv1.setVisibility(View.GONE);
        }

        TextView successTv2 = (TextView) findViewById(R.id.success_tv2);
        if (!TextUtils.isEmpty(bankCard)) {
            successTv2.setText("卡号 : " + bankCard);
        } else {
            successTv2.setVisibility(View.GONE);
        }

        TextView successTv3 = (TextView) findViewById(R.id.success_tv3);
        if (!TextUtils.isEmpty(bankProvince) && !TextUtils.isEmpty(bankCity)) {
            successTv3.setText("开户银行所在地 : " + bankProvince + " " + bankCity);
        } else {
            successTv3.setVisibility(View.GONE);
        }

        LinearLayout bottonBtn = (LinearLayout) findViewById(R.id.botton_btn);
        bottonBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
