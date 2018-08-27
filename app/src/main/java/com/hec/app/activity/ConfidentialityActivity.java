package com.hec.app.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

import android.widget.TextView;

import com.google.gson.JsonParseException;
import com.hec.app.R;
import com.hec.app.activity.base.BaseActivity;
import com.hec.app.activity.base.BaseApp;
import com.hec.app.entity.BizException;
import com.hec.app.entity.Response;
import com.hec.app.entity.SecurityInfoFinishInfo;
import com.hec.app.util.DialogUtil;
import com.hec.app.util.MyAsyncTask;
import com.hec.app.webservice.AccountService;
import com.hec.app.webservice.ServiceException;

import java.io.IOException;
import java.util.regex.Pattern;

public class ConfidentialityActivity extends BaseActivity {
    private View phoneView;
    private TextView phoneLeftText;
    private TextView phoneRightText;
    private ProgressDialog mProgressDialog;
    private String phoneNumber;
    private boolean isPhoneRotection;
    private boolean mIsError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.form_show_email);
        showProgressDialog(getString(R.string.loading_message));

        View imgBack = findViewById(R.id.imgBack);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        TextView tvEmailRemark = (TextView) findViewById(R.id.pw_remark);
        tvEmailRemark.setText(Html.fromHtml("<font color=#cc0029>*</font> " + getString(R.string.remark4)));

        phoneView = findViewById(R.id.phone_rl);
        phoneLeftText = (TextView) findViewById(R.id.phone_left_message_text);
        phoneRightText = (TextView) findViewById(R.id.phone_right_message_text);
    }

    @Override
    protected void onResume() {
        super.onResume();
        securityInfoFinish();
    }

    private void securityInfoFinish(){
        mIsError = false;

        MyAsyncTask<SecurityInfoFinishInfo> task = new MyAsyncTask<SecurityInfoFinishInfo>(ConfidentialityActivity.this) {
            @Override
            public SecurityInfoFinishInfo callService() throws IOException, JsonParseException, BizException, ServiceException {
                return new AccountService().securityInfoFinish();
            }

            @Override
            public void onLoaded(SecurityInfoFinishInfo data) throws Exception {
                if(ConfidentialityActivity.this == null || ConfidentialityActivity.this.isFinishing()) {
                    return;
                }

                closeProgressDialog();
                if (!mIsError) {
                    final String email = data.getEmail();
                    if (email != null) {
                        TextView emailText = (TextView) findViewById(R.id.setting_email);
                        emailText.setText(email);
                    }

                    phoneNumber = data.getPhoneNumber();
                    isPhoneRotection = data.getIsPhoneRotection();
                    if (isPhoneRotection) {
                        if (!TextUtils.isEmpty(phoneNumber)) {
                            phoneLeftText.setText(phoneNumber);
                        } else {
                            phoneLeftText.setText("已设置");
                        }
                        phoneRightText.setText("解绑");
                    } else {
                        phoneLeftText.setText("尚未设置");
                        phoneRightText.setText("立即绑定");
                    }

                    phoneView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showAccountAuthenticationDialog();
                        }
                    });
                }
                else {
                    BaseApp.changeUrl(ConfidentialityActivity.this, new BaseApp.OnChangeUrlListener() {
                        @Override
                        public void changeSuccess() {
                            securityInfoFinish();
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
                mIsError = true;
            }
        });
        task.executeTask();
    }

    private void showProgressDialog(String loadingMessage){
        try {
            mProgressDialog = DialogUtil.getProgressDialog(ConfidentialityActivity.this, loadingMessage);
            mProgressDialog.show();
        } catch (Exception e) {
        }
    }

    private void closeProgressDialog() {
        try {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        } catch (Exception e) {
        }
    }

    private void showAccountAuthenticationDialog() {
        final Dialog dialog = new Dialog(ConfidentialityActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.account_authentication_dialog);

        final View cancel = dialog.findViewById(R.id.cancel);
        final View determine = dialog.findViewById(R.id.determine);
        final EditText pwEtitText = (EditText) dialog.findViewById(R.id.pw_etitText);
        determine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = pwEtitText.getText().toString();
                if (TextUtils.isEmpty(password)) {
                    DialogUtil.getErrorAlertDialog(ConfidentialityActivity.this, "密码不能为空").show();
                } else if (!Pattern.compile("^[a-zA-Z0-9_]+$").matcher(password).matches()) {
                    DialogUtil.getErrorAlertDialog(ConfidentialityActivity.this, "密码包含非法字符").show();
                } else if (password.length() < 6 || password.length() > 16) {
                    DialogUtil.getErrorAlertDialog(ConfidentialityActivity.this, "请输入6到16个字符").show();
                } else {
                    checkMoneyPwd(password);
                    dialog.dismiss();
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void checkMoneyPwd(final String moneyPassword){
        mIsError = false;

        showProgressDialog("正在确认资金密码");
        MyAsyncTask<Response> task = new MyAsyncTask<Response>(ConfidentialityActivity.this) {
            @Override
            public Response callService() throws IOException, JsonParseException, BizException, ServiceException {
                return new AccountService().checkMoneyPwd(moneyPassword);
            }

            @Override
            public void onLoaded(Response data) throws Exception {
                if(ConfidentialityActivity.this == null || ConfidentialityActivity.this.isFinishing()) {
                    return;
                }

                closeProgressDialog();
                if (!mIsError) {
                    if (data.getSuccess()) {
                        Intent next = new Intent(ConfidentialityActivity.this, PhoneActivity.class);
                        next.putExtra("Phone", phoneNumber);
                        next.putExtra("isPhoneRotection", isPhoneRotection);
                        startActivity(next);
                    }
                    else {
                        DialogUtil.getErrorAlertDialog(ConfidentialityActivity.this, data.getMessage()).show();
                    }
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
}