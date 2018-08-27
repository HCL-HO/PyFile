package com.hec.app.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.gson.JsonParseException;
import com.hec.app.R;
import com.hec.app.activity.base.BaseActivity;
import com.hec.app.entity.BizException;
import com.hec.app.entity.NewBankInfo;
import com.hec.app.entity.Response;
import com.hec.app.entity.SecurityInfoFinishInfo;
import com.hec.app.framework.widget.MyToast;
import com.hec.app.util.DialogUtil;
import com.hec.app.util.IntentUtil;
import com.hec.app.util.MyAsyncTask;
import com.hec.app.webservice.AccountService;
import com.hec.app.webservice.ServiceException;
import com.hec.app.webservice.WithdrawService;

import java.io.IOException;
import java.util.regex.Pattern;

public class VerifyMoneyPasswordActivity extends BaseActivity {
    private EditText moneyPasswordEditText;
    private EditText bankCardNoEditText;
    private EditText nameEditText;
    private View confirmLl;
    private boolean mIsError = false;
    private ProgressDialog progressDialog;
    private String moneyPassword = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_money_password);
        initView();
    }

    private void initView() {
        moneyPasswordEditText = (EditText) findViewById(R.id.money_password_edittext);
        bankCardNoEditText = (EditText) findViewById(R.id.bank_card_no_edittext);
        nameEditText = (EditText) findViewById(R.id.name_edittext);
        confirmLl = findViewById(R.id.confirm_ll);

        ImageView imgBack = (ImageView) findViewById(R.id.imgBack);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        confirmLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = moneyPasswordEditText.getText().toString();
                String bankCardNo = bankCardNoEditText.getText().toString();
                String cardUser = nameEditText.getText().toString();
                String errorMessage = "";

                if (TextUtils.isEmpty(password)) {
                    errorMessage = "资金密码不能空白";
                } else if (!Pattern.compile("^[a-zA-Z0-9_]+$").matcher(password).matches()) {
                    errorMessage = "资金密码包含非法字符";
                } else if (password.length() < 6 || password.length() > 16) {
                    errorMessage = "资金密码限制6到16个字符";
                }

                if (TextUtils.isEmpty(bankCardNo)) {
                    if (TextUtils.isEmpty(errorMessage)) {
                        errorMessage = "银行卡号不能空白";
                    } else {
                        errorMessage += "\n银行卡号不能空白";
                    }
                }

                if (TextUtils.isEmpty(cardUser)) {
                    if (TextUtils.isEmpty(errorMessage)) {
                        errorMessage = "银行卡姓名不能空白";
                    } else {
                        errorMessage += "\n银行卡姓名不能空白";
                    }
                } else if (!checkChinese(cardUser)) {
                    if (TextUtils.isEmpty(errorMessage)) {
                        errorMessage = "银行卡姓名应为中文";
                    } else {
                        errorMessage += "\n银行卡姓名应为中文";
                    }
                }

                if (!TextUtils.isEmpty(errorMessage)) {
                    DialogUtil.getErrorAlertDialog(VerifyMoneyPasswordActivity.this, errorMessage).show();
                } else {
                    checkMoneyPwdAndBankInfo(password, bankCardNo, cardUser);
                }
            }
        });
    }

    private boolean checkChinese(String inputStr){
        inputStr = inputStr.replace(" ","");
        char[] ch = inputStr.toCharArray();
        for (int i = 0; i < ch.length; i++) {
            char c = ch[i];
            if (isChinese(c)) {
                if(isChinesePunctuation(c)){
                    return false;
                }else{
                    continue;
                }
            } else{
                return false;
            }
        }
        return true;
    }

    private boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION) {
            return true;
        }
        return false;
    }

    private boolean isChinesePunctuation(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_FORMS) {
            return true;
        } else {
            return false;
        }
    }

    private void securityInfoFinish() {
        MyAsyncTask<SecurityInfoFinishInfo> task = new MyAsyncTask<SecurityInfoFinishInfo>(VerifyMoneyPasswordActivity.this) {
            @Override
            public SecurityInfoFinishInfo callService() throws IOException, JsonParseException, BizException, ServiceException {
                return new AccountService().securityInfoFinish();
            }

            @Override
            public void onLoaded(SecurityInfoFinishInfo data) throws Exception {
                if(VerifyMoneyPasswordActivity.this == null || VerifyMoneyPasswordActivity.this.isFinishing()) {
                    return;
                }

                closeProgressDialog();
                if (!mIsError) {
                    if (!data.getIsPhoneRotection()) {
                        final Dialog dialog = new Dialog(VerifyMoneyPasswordActivity.this);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setCancelable(false);
                        dialog.setContentView(R.layout.phone_confidentiality_dialog);

                        View determine = dialog.findViewById(R.id.determine);
                        determine.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                IntentUtil.redirectToNextActivity(VerifyMoneyPasswordActivity.this, ConfidentialityActivity.class);
                                finish();
                            }
                        });

                        View cancel = dialog.findViewById(R.id.cancel);
                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                    } else {
                        Intent next = new Intent(VerifyMoneyPasswordActivity.this, VerifyPhoneActivity.class);
                        next.putExtra("BankInfo", getIntent().getBundleExtra("BankInfo"));
                        next.putExtra("moneyPwd", moneyPassword);
                        startActivity(next);
                        finish();
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

    private void checkMoneyPwdAndBankInfo(final String moneyPwd, final String bankCard, final String cardUser){
        mIsError = false;

        showProgressDialog("正在提交");
        MyAsyncTask<Response> task = new MyAsyncTask<Response>(VerifyMoneyPasswordActivity.this) {
            @Override
            public Response callService() throws IOException, JsonParseException, BizException, ServiceException {
                return new AccountService().checkMoneyPwdAndBankInfo(moneyPwd, bankCard, cardUser);
            }

            @Override
            public void onLoaded(Response data) throws Exception {
                if(VerifyMoneyPasswordActivity.this == null || VerifyMoneyPasswordActivity.this.isFinishing()) {
                    return;
                }

                if (!mIsError) {
                    if (data.getSuccess()) {
                        moneyPassword = moneyPwd;
                        securityInfoFinish();
                    }
                    else {
                        moneyPassword = "";
                        closeProgressDialog();
                        DialogUtil.getErrorAlertDialog(VerifyMoneyPasswordActivity.this, data.getMessage()).show();
                    }
                } else {
                    closeProgressDialog();
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
            progressDialog = DialogUtil.getProgressDialog(VerifyMoneyPasswordActivity.this, loadingMessage);
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
