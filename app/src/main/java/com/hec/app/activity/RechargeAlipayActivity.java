package com.hec.app.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.text.Html;
import android.text.method.DigitsKeyListener;
import android.util.Log;
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
import com.hec.app.entity.AliPayNewInfo;
import com.hec.app.entity.AlipayInfo;
import com.hec.app.entity.BankTypeInfo;
import com.hec.app.entity.BizException;
import com.hec.app.entity.Response;
import com.hec.app.entity.ServiceRequestResult;
import com.hec.app.framework.widget.MyToast;
import com.hec.app.framework.widget.ResideMenu;
import com.hec.app.util.DialogUtil;
import com.hec.app.util.MyAsyncTask;
import com.hec.app.webservice.RechargeService;
import com.hec.app.webservice.ServiceException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RechargeAlipayActivity extends BaseActivityWithMenu implements View.OnClickListener {
    private LinearLayout btn;
    private ImageView imgBack;

    private RelativeLayout btn10, btn100, btn200, btn500, btn1000, btn2000, btn3000, btn4000, btn5000, btn10000;
    private EditText alipayLoginEditText, amountEditText;
    private ImageView title_icon;
    private TextView amountHint;

    private LinearLayout afterHistoryLayout;
    private LinearLayout lotteryHistoryLayout;
    private LinearLayout SettingLayout;
    private LinearLayout moneyDetailLayout;
    private LinearLayout agentCenterLayout;
    private RelativeLayout rechargeLayout;
    private RelativeLayout withdrawLayout;

    private ImageView errCross1, errCross2;
    private RelativeLayout rl1, rl2;
    private TextView errText1, errText2;
    private LinearLayout err1, err2;
    private ProgressDialog progressDialog;

    private Spinner recharge_line;
    private RelativeLayout mLayoutRechargeLine;
    RechargeService rechargeService;
    private TextView secondtitle;
    ProgressDialog mProgressDialog;
    List<BankTypeInfo> bankTypeList;
    List<String> lines;
    ArrayAdapter adapter;
    private boolean mIsError = false, wechat, pressed = false;
    private ResideMenu resideMenu;
    private int tag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge_alipay);
        secondtitle = (TextView) findViewById(R.id.alipay_textview);
        wechat = getIntent().getBooleanExtra("wechat", false);
        tag = getIntent().getIntExtra("tag", 0);
        title_icon = (ImageView) findViewById(R.id.icon_wechat_or_Alipay);
        if (wechat) {
            ((TextView) findViewById(R.id.title_tv)).setText("微信充值");
            title_icon.setImageResource(R.mipmap.icon_wechat);
        } else {
            title_icon.setImageResource(R.mipmap.icon_ailpay_little);
        }

        imgBack = (ImageView) findViewById(R.id.imgBack);
        resideMenu = super.getResidingMenu();
        btn = (LinearLayout) findViewById(R.id.alipay_recharge_btn);

        errCross1 = (ImageView) findViewById(R.id.error_cross1);
        errText1 = (TextView) findViewById(R.id.recharge_moneysum_textview_cant_empty);
        rl1 = (RelativeLayout) findViewById(R.id.rl1);
        err1 = (LinearLayout) findViewById(R.id.error1);
        errCross2 = (ImageView) findViewById(R.id.error_cross2);
        errText2 = (TextView) findViewById(R.id.recharge_alipay_login_textview_cant_empty);
        rl2 = (RelativeLayout) findViewById(R.id.rl2);
        err2 = (LinearLayout) findViewById(R.id.error2);
        errText1.setText("充值金额不能为空");
        errText2.setText("充值账号不能为空");

        rechargeService = new RechargeService();
        bankTypeList = new ArrayList<>();
        lines = new ArrayList<>();
        mLayoutRechargeLine = (RelativeLayout) findViewById(R.id.recharge_line_layout);
        recharge_line = (Spinner) findViewById(R.id.recharge_line_spinner);
        if (wechat || tag == 1) {
            secondtitle.setText("请选择充值线路");
            mLayoutRechargeLine.setVisibility(View.VISIBLE);
        } else {
            secondtitle.setVisibility(View.GONE);
        }
        adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, lines);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        recharge_line.setAdapter(adapter);

        alipayLoginEditText = (EditText) findViewById(R.id.alipay_recharge_login_edittext);
        amountEditText = (EditText) findViewById(R.id.recharge_moneysum_edittext);

        btn10 = (RelativeLayout) findViewById(R.id.recharge_10_btn);
        btn100 = (RelativeLayout) findViewById(R.id.recharge_100_btn);
        btn200 = (RelativeLayout) findViewById(R.id.recharge_200_btn);
        btn500 = (RelativeLayout) findViewById(R.id.recharge_500_btn);
        btn1000 = (RelativeLayout) findViewById(R.id.recharge_1000_btn);
        btn2000 = (RelativeLayout) findViewById(R.id.recharge_2000_btn);
        btn3000 = (RelativeLayout) findViewById(R.id.recharge_3000_btn);
        btn4000 = (RelativeLayout) findViewById(R.id.recharge_4000_btn);
        btn5000 = (RelativeLayout) findViewById(R.id.recharge_5000_btn);
        btn10000 = (RelativeLayout) findViewById(R.id.recharge_10000_btn);

        String text = "<font color=\"#ff0000\">*</font> <font color=\"#000000\">小提示：使用非整数金额充值能更快到帐哦～<br/>(例如:</font>" +
                "<font color=\"#ff0000\">101、1002、2011</font> <font color=\"#000000\">)</font>";
        amountHint = (TextView) findViewById(R.id.amount_hint);
        amountHint.setText(Html.fromHtml(text));

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        amountEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                setEditorAction();
                return false;
            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (amountEditText.getText().toString().isEmpty()) {
                    err2.setVisibility(View.VISIBLE);
                    errCross2.setVisibility(View.VISIBLE);
                    rl2.setBackgroundResource(R.drawable.rect_no_round);
                    return;
                }

                if (!wechat && tag != 1) {
                    double amount = Double.parseDouble(amountEditText.getText().toString());
                    if (amount > 10000 || amount < 50) {
                        MyToast.show(RechargeAlipayActivity.this, "单笔限额 50 ~ 10000元");
                    } else {
                        startAlipayRechargeNew("", amountEditText.getText().toString());
                    }
                    return;
                }

                if (wechat || tag == 1 && !amountEditText.getText().toString().isEmpty()
                        && bankTypeList != null && !bankTypeList.isEmpty() && recharge_line.getSelectedItemPosition() < bankTypeList.size()
                        && recharge_line.getSelectedItemPosition() >= 0) {

                    double amount = Double.parseDouble(amountEditText.getText().toString());
                    if (wechat && (amount > 3000 || amount < 10)) {
                        MyToast.show(RechargeAlipayActivity.this, getString(R.string.recharge_wechat_amount_limit));
                    } else if (tag == 1 && (amount > 5000 || amount < 50)) {
                        MyToast.show(RechargeAlipayActivity.this, getString(R.string.recharge_alipay_amount_limit));
                    } else if (!pressed) {
                        pressed = true;
                        showProgressDialog("正在提交申请");
                        if (recharge_line.getSelectedItemPosition() != -1) {
                            if (tag == 1) {
                                startAlipayRecharge(bankTypeList.get(recharge_line.getSelectedItemPosition()).getBankTypeID(), amountEditText.getText().toString());
                            }
                            if (wechat) {
                                startAlipayRecharge(bankTypeList.get(recharge_line.getSelectedItemPosition()).getBankTypeID(), amountEditText.getText().toString());
                            }
                        } else {
                            MyToast.show(RechargeAlipayActivity.this, "还未获取银行列表");
                        }
                    }
                } else {
                    MyToast.show(RechargeAlipayActivity.this, "充值线路不能为空");
                }
            }
        });

        btn10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                amountEditText.setText("10");
                setEditorAction();
            }
        });
        btn100.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                amountEditText.setText("100");
                setEditorAction();
            }
        });
        btn200.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                amountEditText.setText("200");
                setEditorAction();
            }
        });
        btn500.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                amountEditText.setText("500");
                setEditorAction();
            }
        });
        btn1000.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                amountEditText.setText("1000");
                setEditorAction();
            }
        });
        btn2000.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                amountEditText.setText("2000");
                setEditorAction();
            }
        });
        btn3000.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                amountEditText.setText("3000");
                setEditorAction();
            }
        });
        btn4000.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                amountEditText.setText("4000");
                setEditorAction();
            }
        });
        btn5000.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                amountEditText.setText("5000");
                setEditorAction();
            }
        });
        btn10000.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                amountEditText.setText("10000");
                setEditorAction();
            }
        });

        LinearLayout linear2 = (LinearLayout) findViewById(R.id.linear2);
        if (wechat) {
            linear2.setVisibility(View.GONE);
            if (tag != 1) {
                getBankTypes();
            }
        } else if (!wechat && tag != 1) {
            btn10.setVisibility(View.GONE);
            btn3000.setVisibility(View.GONE);
            btn4000.setVisibility(View.GONE);
            linear2.setVisibility(View.VISIBLE);
            amountEditText.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
        } else {
            btn10.setVisibility(View.GONE);
            linear2.setVisibility(View.VISIBLE);
            btn10000.setVisibility(View.GONE);
            getBankTypes();
        }
    }

    public void startAlipayRechargeNew(final String name, final String amount) {
        mIsError = false;
        showProgressDialog("正在提交");
        final MyAsyncTask<AliPayNewInfo> task = new MyAsyncTask<AliPayNewInfo>(RechargeAlipayActivity.this) {
            @Override
            public AliPayNewInfo callService() throws IOException, JsonParseException, BizException, ServiceException {
                return rechargeService.submitAlipayNew(name, amount);
            }

            @Override
            public void onLoaded(AliPayNewInfo paramT) throws Exception {
                closeProgressDialog();
                if (!mIsError) {
                    Intent intent = new Intent();
                    intent.putExtra("alipay", paramT);
                    intent.setClass(RechargeAlipayActivity.this, RechargeAlipaySuccessActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };
        task.executeTask();
        task.setOnError(new MyAsyncTask.OnError() {
            @Override
            public void handleError(Exception paramException) {
                closeProgressDialog();
                if (task.getErrorMessage() != null) {
                    DialogUtil.getErrorAlertDialog(RechargeAlipayActivity.this, task.getErrorMessage()).show();
                }
                mIsError = true;
            }
        });
    }

    public void startAlipayRecharge(final String bankTypeIDorName, final String amount) {
        mIsError = false;
        MyAsyncTask<Response<AlipayInfo>> task = new MyAsyncTask<Response<AlipayInfo>>(this) {

            @Override
            public Response<AlipayInfo> callService() throws IOException, JsonParseException, BizException, ServiceException {
                if (wechat)
                    return rechargeService.submitWechat(bankTypeIDorName, amount);
                else
                    return rechargeService.submitAlipay(bankTypeIDorName, amount);
            }

            @Override
            public void onLoaded(Response<AlipayInfo> result) throws Exception {
                if (RechargeAlipayActivity.this == null || RechargeAlipayActivity.this.isFinishing())
                    return;
                closeProgressDialog();
                pressed = false;
                if (!mIsError) {
                    if (result.getSuccess()) {
                        Bundle b = new Bundle();
                        b.putString("data", result.getData().getURL());
                        Message msg = Message.obtain();
                        msg.what = 0;
                        msg.setData(b);
                        mHandler.sendMessage(msg);
                    } else {
                        if (result.getMessage().contains(getResources().getString(R.string.error_pending_alipay))) {
                            startCancelDialog(result.getMessage());
                        } else
                            MyToast.show(RechargeAlipayActivity.this, result.getMessage());
                    }
                } else {
                    BaseApp.getAppBean().resetApiUrl(RechargeAlipayActivity.this);
                    Toast.makeText(RechargeAlipayActivity.this, getResources().getString(R.string.error_message_recharge_network), Toast.LENGTH_LONG).show();
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

        //showProgressDialog("正在提交申请!");
        mIsError = false;
        MyAsyncTask<List<BankTypeInfo>> task = new MyAsyncTask<List<BankTypeInfo>>(this) {

            @Override
            public List<BankTypeInfo> callService() throws IOException,  JsonParseException, BizException, ServiceException {
                if (wechat)
                    return rechargeService.getWechatRechargeList("10", isAvilible(CommonConfig.PACKAGE_NAME_WECHAT));
                else
                    return rechargeService.getAlipayRechargeList("10", isAvilible(CommonConfig.PACKAGE_NAME_ALIPAY));
            }

            @Override
            public void onLoaded(List<BankTypeInfo> result) throws Exception {
                if (RechargeAlipayActivity.this == null || RechargeAlipayActivity.this.isFinishing())
                    return;
                closeProgressDialog();
                if (!mIsError) {
                    bankTypeList = result;
                    mHandler.sendEmptyMessage(1);
                } else {
                    BaseApp.changeUrl(RechargeAlipayActivity.this, new BaseApp.OnChangeUrlListener() {
                        @Override
                        public void changeSuccess() {
                            getBankTypes();
                        }

                        @Override
                        public void changeFail() {
                        }
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

    private void startRechargeSuccess(String data) {

        try {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(data));
            startActivity(browserIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startCancelDialog(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(msg);

        builder.setPositiveButton(R.string.confirm_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                cancelPendingAlipay();
            }
        });
        builder.setNegativeButton(R.string.not_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void cancelPendingAlipay() {
        mIsError = false;
        MyAsyncTask<ServiceRequestResult> task = new MyAsyncTask<ServiceRequestResult>(this) {

            @Override
            public ServiceRequestResult callService() throws IOException,
                    JsonParseException, BizException, ServiceException {
                return rechargeService.cancelAlipay();
            }

            @Override
            public void onLoaded(ServiceRequestResult result) throws Exception {
                if (RechargeAlipayActivity.this == null || RechargeAlipayActivity.this.isFinishing())
                    return;
                closeProgressDialog();
                if (!mIsError)
                    MyToast.show(getBaseContext(), result.getMessage(), Toast.LENGTH_SHORT);
                else {
                    BaseApp.changeUrl(RechargeAlipayActivity.this, new BaseApp.OnChangeUrlListener() {
                        @Override
                        public void changeSuccess() {
                            cancelPendingAlipay();
                        }

                        @Override
                        public void changeFail() {
                        }
                    });
                }
            }
        };
        task.setOnError(new MyAsyncTask.OnError() {
            @Override
            public void handleError(Exception paramException) {
                closeProgressDialog();
                mIsError = true;
            }
        });
        task.executeTask();
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            closeProgressDialog();
            if (msg.what == 0) {
                //Update UI
                String data = msg.getData().getString("data");
                if (!data.isEmpty()) {
                    startRechargeSuccess(data);
                } else {
                    MyToast.show(getBaseContext(), getResources().getString(R.string.error_service));
                }
            } else if (msg.what == 1) {
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

    @Override
    public void onClick(View v) {
        super.onClick(v);
    }

    private void showProgressDialog(String loadingMessage) {
        try {
            progressDialog = DialogUtil.getProgressDialog(RechargeAlipayActivity.this, loadingMessage);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void setEditorAction() {
        if (wechat || tag == 1) {
            if (!amountEditText.getText().toString().isEmpty()) {
                errCross2.setVisibility(View.GONE);
                err2.setVisibility(View.GONE);
                rl2.setBackgroundResource(0);
                rl2.setBackgroundColor(Color.parseColor("#f2f2f2"));
            }

            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private String isAvilible(String packageName) {
        try {
            final PackageManager packageManager = getBaseContext().getPackageManager();
            List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);

            for (int i = 0; i < pinfo.size(); ++i) {
                if (pinfo.get(i).packageName.equalsIgnoreCase(packageName)) {
                    return "1";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "0";
    }
}
