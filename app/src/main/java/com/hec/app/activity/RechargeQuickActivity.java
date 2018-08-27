package com.hec.app.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

import com.dinpay.plugin.activity.DinpayChannelActivity;
import com.google.gson.JsonParseException;
import com.hec.app.R;
import com.hec.app.activity.base.BaseApp;
import com.hec.app.entity.BankTypeInfo;
import com.hec.app.entity.BizException;
import com.hec.app.entity.DinpayInfo;
import com.hec.app.entity.OnlineInfo;
import com.hec.app.framework.widget.MyToast;
import com.hec.app.util.DialogUtil;
import com.hec.app.util.MyAsyncTask;
import com.hec.app.webservice.RechargeService;
import com.hec.app.webservice.ServiceException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.Callable;

public class RechargeQuickActivity extends AppCompatActivity {
    private LinearLayout btn;
    private ImageView btn_previous;
    private Spinner recharge_line;
    private EditText Charge_Money_Sum;
    private TextView Recharge_moneysum_textview_cant_null;
    private RelativeLayout Recharge_500_btn, Recharge_1000_btn, Recharge_2000_btn,
            Recharge_5000_btn, Recharge_10000_btn, Recharge_20000_btn,
            Recharge_30000_btn, Recharge_40000_btn, Recharge_49999_btn;
    private ProgressDialog progressDialog;
    RechargeService rechargeService;
    List<BankTypeInfo> bankTypeList;
    List<String> lines;
    ArrayAdapter adapter;
    ProgressDialog mProgressDialog;
    Timer timer;
    private boolean mIsError = false;
    private boolean pressed = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge_quick);

        rechargeService = new RechargeService();
        bankTypeList = new ArrayList<>();
        lines = new ArrayList<>();
        timer = new Timer();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        btn = (LinearLayout)findViewById(R.id.quick_recharge_btn);
        btn_previous = (ImageView)findViewById(R.id.btn_previous);
        recharge_line = (Spinner)findViewById(R.id.recharge_line_spinner);

//        Login_user = (EditText)findViewById(R.id.recharge_login_user); Login_user.setText("wj123");
//        Recharge_login_textview_cant_null = (TextView)findViewById(R.id.recharge_login_textview_cant_null);


        Recharge_moneysum_textview_cant_null = (TextView)findViewById(R.id.recharge_moneysum_textview_cant_null);
        Charge_Money_Sum = (EditText)findViewById(R.id.recharge_moneysum_edittext);
        Recharge_500_btn = (RelativeLayout)findViewById(R.id.recharge_500_btn);
        Recharge_1000_btn = (RelativeLayout)findViewById(R.id.recharge_1000_btn);
        Recharge_2000_btn = (RelativeLayout)findViewById(R.id.recharge_2000_btn);
        Recharge_5000_btn = (RelativeLayout)findViewById(R.id.recharge_5000_btn);
        Recharge_10000_btn = (RelativeLayout)findViewById(R.id.recharge_10000_btn);
        Recharge_20000_btn = (RelativeLayout)findViewById(R.id.recharge_20000_btn);
        Recharge_30000_btn = (RelativeLayout)findViewById(R.id.recharge_30000_btn);
        Recharge_40000_btn = (RelativeLayout)findViewById(R.id.recharge_40000_btn);
        Recharge_49999_btn = (RelativeLayout)findViewById(R.id.recharge_49999_btn);
        Recharge_30000_btn.setVisibility(View.INVISIBLE);
        Recharge_40000_btn.setVisibility(View.INVISIBLE);
        Recharge_49999_btn.setVisibility(View.INVISIBLE);

        adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, lines);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        recharge_line.setAdapter(adapter);
        recharge_line.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Charge_Money_Sum.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                setEditorAction();
                return false;
            }
        });

        Recharge_500_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Charge_Money_Sum.setText("500");
                setEditorAction();
            }
        });
        Recharge_1000_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Charge_Money_Sum.setText("1000");
                setEditorAction();
            }
        });
        Recharge_2000_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Charge_Money_Sum.setText("2000");
                setEditorAction();
            }
        });
        Recharge_5000_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Charge_Money_Sum.setText("5000");
                setEditorAction();
            }
        });
        Recharge_10000_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Charge_Money_Sum.setText("10000");
                setEditorAction();
            }
        });
        Recharge_20000_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Charge_Money_Sum.setText("20000");
                setEditorAction();
            }
        });
        Recharge_30000_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Charge_Money_Sum.setText("30000");
                setEditorAction();
            }
        });
        Recharge_40000_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Charge_Money_Sum.setText("40000");
                setEditorAction();
            }
        });
        Recharge_49999_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Charge_Money_Sum.setText("49999");
                setEditorAction();
            }
        });

        btn_previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean Is_Null_ChargeMoneySum = "".equals(Charge_Money_Sum.getText().toString().trim());

                if(Is_Null_ChargeMoneySum){
                    Recharge_moneysum_textview_cant_null.setVisibility(View.VISIBLE);
                } else {
                    Recharge_moneysum_textview_cant_null.setVisibility(View.INVISIBLE);
                }

                if(!Is_Null_ChargeMoneySum){
                    if (bankTypeList != null && recharge_line.getSelectedItemPosition() < bankTypeList.size() && !bankTypeList.isEmpty()){
                        if((Double.parseDouble(Charge_Money_Sum.getText().toString()) < 1) || (Double.parseDouble(Charge_Money_Sum.getText().toString()) > 20000))
                            MyToast.show(RechargeQuickActivity.this, "单笔限额 1 ~ 20000元");
                        else if (!pressed) {
                            pressed = true;
                            //startRecharge(bankTypeList.get(recharge_line.getSelectedItemPosition()), Charge_Money_Sum.getText().toString().trim());
                            startRechargeNew(bankTypeList.get(recharge_line.getSelectedItemPosition()), Charge_Money_Sum.getText().toString().trim());
                        }
                    } else {
                        MyToast.show(RechargeQuickActivity.this, "充值线路不能为空！");
                    //    getBankTypes(Charge_Money_Sum.getText().toString().trim());
                    }
                }
            }
        });
        getBankTypes();
    }

    private void getBankTypes () {
        mIsError = false;
        showProgressDialog("正在获取充值线路！");
        MyAsyncTask<List<BankTypeInfo>> task = new MyAsyncTask<List<BankTypeInfo>>(this) {

            @Override
            public List<BankTypeInfo> callService() throws IOException,
                    JsonParseException, BizException, ServiceException {
                //return rechargeService.getDinpayRechargeList(amount);
                return rechargeService.getOnlineRechargeList("0");
            }

            @Override
            public void onLoaded(List<BankTypeInfo> result) throws Exception {
                closeProgressDialog();
                if(RechargeQuickActivity.this == null || RechargeQuickActivity.this.isFinishing())
                    return;
                closeProgressDialog();
                if (!mIsError) {
                    bankTypeList = result;
                    Log.i("Recharge", "Load");
                    mHandler.sendEmptyMessage(0);
                }
                else {
                    BaseApp.changeUrl(RechargeQuickActivity.this, new BaseApp.OnChangeUrlListener() {
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
                closeProgressDialog();
                bankTypeList = new ArrayList<>();
            }
        });
        task.executeTask();
    }

    private void startRechargeNew(final BankTypeInfo b, final String amount){
        showProgressDialog("正在跳转，请稍后!");
        mIsError = false;
        MyAsyncTask<OnlineInfo> task = new MyAsyncTask<OnlineInfo>(RechargeQuickActivity.this) {
            @Override
            public OnlineInfo callService() throws IOException, JsonParseException, BizException, ServiceException {
                if (b != null)
                    return rechargeService.submitOnline(b.getBankTypeID(), amount);
                else
                    return rechargeService.submitOnline(null, amount);
            }

            @Override
            public void onLoaded(OnlineInfo paramT) throws Exception {
                Log.i("wxj","paraT " + paramT);
                closeProgressDialog();
                pressed = false;
                if(!mIsError){
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(paramT.getUrl()));
                    startActivity(intent);
                }else{
                    BaseApp.getAppBean().resetApiUrl(RechargeQuickActivity.this);
                    Toast.makeText(RechargeQuickActivity.this, getResources().getString(R.string.error_message_recharge_network), Toast.LENGTH_LONG).show();
                    BaseApp.getAppBean().setRetryCount(0);
                }
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

    private void startRecharge (final BankTypeInfo b, final String amount) {
        showProgressDialog("正在提交申请!");
        mIsError = false;
        MyAsyncTask<DinpayInfo> task = new MyAsyncTask<DinpayInfo>(this) {

            @Override
            public DinpayInfo callService() throws IOException,
                    JsonParseException, BizException, ServiceException {
                if (b != null)
                    return rechargeService.submitDinpay(b.getBankTypeID(), amount);
                else
                    return rechargeService.submitDinpay(null, amount);
            }

            @Override
            public void onLoaded(DinpayInfo result) throws Exception {
                if(RechargeQuickActivity.this == null || RechargeQuickActivity.this.isFinishing())
                    return;
                closeProgressDialog();
                pressed = false;
                if(!mIsError){
                    startDinpayActivity(result);
                }else{
                    BaseApp.getAppBean().resetApiUrl(RechargeQuickActivity.this);
                    Toast.makeText(RechargeQuickActivity.this, getResources().getString(R.string.error_message_recharge_network), Toast.LENGTH_LONG).show();
                    BaseApp.getAppBean().setRetryCount(0);
                }
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

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage (Message msg) {
            super.handleMessage(msg);
            closeProgressDialog();
            //Update UI
            if (msg.what == 0) {
                lines.clear();
                if (bankTypeList != null) {
                    for (BankTypeInfo b : bankTypeList) {
                        lines.add(b.getBankTypeName());
                    }
                }
                adapter.notifyDataSetChanged();
            }
        }
    };
    private void showProgressDialog(String loadingMessage){
        try {
            progressDialog = DialogUtil.getProgressDialog(RechargeQuickActivity.this, loadingMessage);
            progressDialog.show();
            //progressDialog.setCancelable(false);
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

    private void startDinpayActivity(DinpayInfo info){
        String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                "<dinpay><request><merchant_code>"+info.getMerchant_code()+"</merchant_code>"+
                "<notify_url>"+info.getNotify_url()+"</notify_url>"+
                "<interface_version>"+info.getInterface_version()+"</interface_version>"+
                "<sign_type>"+info.getSign_type()+"</sign_type>"+
                "<sign>"+info.getSign()+"</sign>"+
                "<trade><order_no>"+info.getOrder_no()+"</order_no>"+
                "<order_time>"+info.getOrder_time()+"</order_time>"+
                "<order_amount>"+info.getOrder_amount()+"</order_amount>"+
                "<product_name>"+info.getProduct_name()+"</product_name>"+
                "<extra_return_param>"+info.getExtra_return_param()+"</extra_return_param>"+
                "</trade></request></dinpay>";
        Log.i("xml=", xml);
        Intent intent = new Intent(this, DinpayChannelActivity.class);
        intent.putExtra("xml", xml);
        intent.putExtra("ActivityName", "com.hec.app.activity.RechargeQuickMindActivity");
        startActivity(intent);
    }

    public void setEditorAction() {
        InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
