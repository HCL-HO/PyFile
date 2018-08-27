package com.hec.app.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.google.gson.JsonParseException;
import com.hec.app.R;
import com.hec.app.activity.base.BaseActivity;
import com.hec.app.activity.base.BaseActivityWithMenu;
import com.hec.app.entity.BizException;
import com.hec.app.entity.OnlineBankInfo;
import com.hec.app.framework.widget.MyToast;
import com.hec.app.framework.widget.ResideMenu;
import com.hec.app.util.DialogUtil;
import com.hec.app.util.MyAsyncTask;
import com.hec.app.webservice.RechargeService;
import com.hec.app.webservice.ServiceException;

import java.io.IOException;
import java.util.ArrayList;

public class RechargeOnlinebankActivity extends BaseActivity {

    private ImageView imgPerson;
    //private ResideMenu resideMenu;
    private Spinner sp_onlinebank;
    private EditText et_onlinebank_amount;
    private RelativeLayout recharge_500_btn, recharge_1000_btn, recharge_2000_btn,
            recharge_5000_btn, recharge_10000_btn, recharge_20000_btn, recharge_30000_btn,
            recharge_40000_btn,recharge_49999_btn;
    private LinearLayout onlinebank_recharge_btn;
    private int banktype = 1;
    private boolean mIsError = false;
    private ProgressDialog progressDialog;
    private SharedPreferences sharedPreferences;
    private int choice = 0;
    private String transfertype = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge_onlinebank);
        //resideMenu = super.getResidingMenu();
        imgPerson = (ImageView) findViewById(R.id.imgPersonalCenter);
        imgPerson.setVisibility(View.INVISIBLE);
        transfertype = getIntent().getStringExtra("transfertype");
        sharedPreferences = getSharedPreferences("bank",MODE_PRIVATE);
        choice = sharedPreferences.getInt("bankchoice",0);
        sp_onlinebank = (Spinner) findViewById(R.id.sp_choose_bank);
        et_onlinebank_amount = (EditText) findViewById(R.id.et_onlinebank_amount);
        onlinebank_recharge_btn = (LinearLayout) findViewById(R.id.onlinebank_recharge_btn);
        recharge_500_btn = (RelativeLayout) findViewById(R.id.recharge_500_btn);
        recharge_1000_btn = (RelativeLayout) findViewById(R.id.recharge_1000_btn);
        recharge_2000_btn = (RelativeLayout) findViewById(R.id.recharge_2000_btn);
        recharge_5000_btn = (RelativeLayout) findViewById(R.id.recharge_5000_btn);
        recharge_10000_btn = (RelativeLayout) findViewById(R.id.recharge_10000_btn);
        recharge_20000_btn = (RelativeLayout) findViewById(R.id.recharge_20000_btn);
        recharge_30000_btn = (RelativeLayout) findViewById(R.id.recharge_30000_btn);
        recharge_40000_btn = (RelativeLayout) findViewById(R.id.recharge_40000_btn);
        recharge_49999_btn = (RelativeLayout) findViewById(R.id.recharge_49999_btn);
        initbtn();
    }

    public void backClick (View v){
        finish();
    }

    private void initbtn(){
        recharge_40000_btn.setVisibility(View.GONE);
        recharge_49999_btn.setVisibility(View.GONE);

        recharge_500_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_onlinebank_amount.setText("500");
            }
        });
        recharge_1000_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_onlinebank_amount.setText("1000");
            }
        });
        recharge_2000_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_onlinebank_amount.setText("2000");
            }
        });
        recharge_5000_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_onlinebank_amount.setText("5000");
            }
        });
        recharge_10000_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_onlinebank_amount.setText("10000");
            }
        });
        recharge_20000_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_onlinebank_amount.setText("20000");
            }
        });
        recharge_30000_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_onlinebank_amount.setText("30000");
            }
        });
        recharge_40000_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_onlinebank_amount.setText("40000");
            }
        });
        recharge_49999_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_onlinebank_amount.setText("49999");
            }
        });
        ArrayList<String> list = new ArrayList<>();
        list.add("中国工商银行");
        list.add("跨行转账");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,list);
        sp_onlinebank.setAdapter(adapter);
        if(choice<2){
            sp_onlinebank.setSelection(choice);
        }
        onlinebank_recharge_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });
    }

    private void submit(){
        String amountStr = et_onlinebank_amount.getText().toString();
        if(amountStr.isEmpty()){
            MyToast.show(this,"请选择转账金额！");
            return;
        }

        int amount = Integer.parseInt(amountStr);
        if (amount < 1 || amount > 30000) {
            MyToast.show(this,"单笔限额 1 ~ 30000元");
            return;
        }

        showProgressDialog("请稍等！");

        mIsError = false;
        String s = (String) sp_onlinebank.getSelectedItem();
        Log.i("wxj","online " + s);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if("中国工商银行".equals(transfertype)){
            banktype = 1;
            editor.putInt("bankchoice",0);
        } else if("跨行转账".equals(transfertype)){
            banktype = 2;
            editor.putInt("bankchoice",1);
        }
        editor.commit();
        MyAsyncTask<OnlineBankInfo> task = new MyAsyncTask<OnlineBankInfo>(RechargeOnlinebankActivity.this) {
            @Override
            public OnlineBankInfo callService() throws IOException, JsonParseException, BizException, ServiceException {
                return new RechargeService().submitOnlineBank(banktype,Float.parseFloat(et_onlinebank_amount.getText().toString()));
            }

            @Override
            public void onLoaded(OnlineBankInfo paramT) throws Exception {
                closeProgressDialog();
                if(!mIsError){
                    Log.i("wxj","onlinebank ok");
                    Log.i("wxj","onlinebank " + paramT.getAttachWord());
                    Intent intent = new Intent();
                    intent.putExtra("onlinebank",paramT);
                    intent.setClass(RechargeOnlinebankActivity.this,OnlineBankSuccessActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    MyToast.show(RechargeOnlinebankActivity.this,getErrorMessage());
                }
            }
        };
        task.executeTask();
        task.setOnError(new MyAsyncTask.OnError() {
            @Override
            public void handleError(Exception paramException) {
                mIsError = true;
            }
        });
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
}
