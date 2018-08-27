package com.hec.app.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonParseException;
import com.hec.app.R;
import com.hec.app.activity.base.BaseActivity;
import com.hec.app.entity.BizException;
import com.hec.app.entity.NewBankInfo;
import com.hec.app.entity.Response;
import com.hec.app.framework.widget.MyToast;
import com.hec.app.util.DialogUtil;
import com.hec.app.util.MyAsyncTask;
import com.hec.app.webservice.AccountService;
import com.hec.app.webservice.ServiceException;
import com.hec.app.webservice.WithdrawService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class VerifyPhoneActivity extends BaseActivity {
    private LinearLayout sendCaptchaLl;
    private TextView sendCaptchaText;
    private ImageView sendCaptchaImage;
    private List<EditText> captchaEditTextList;
    private LinearLayout nextLl;
    private CountDownTimer countDownTimer;
    private boolean mIsError = false;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone);
        initView();
    }

    private void initView() {
        sendCaptchaLl = (LinearLayout) findViewById(R.id.confirm_send_captcha);
        sendCaptchaText = (TextView) findViewById(R.id.textView_send_captcha);
        sendCaptchaImage = (ImageView) findViewById(R.id.image_send_captcha);
        nextLl = (LinearLayout) findViewById(R.id.next_ll);

        captchaEditTextList = new ArrayList<>();
        captchaEditTextList.add((EditText) findViewById(R.id.setting_captcha_1));
        captchaEditTextList.add((EditText) findViewById(R.id.setting_captcha_2));
        captchaEditTextList.add((EditText) findViewById(R.id.setting_captcha_3));
        captchaEditTextList.add((EditText) findViewById(R.id.setting_captcha_4));
        captchaEditTextList.add((EditText) findViewById(R.id.setting_captcha_5));
        captchaEditTextList.add((EditText) findViewById(R.id.setting_captcha_6));

        for (int i = 0; i < captchaEditTextList.size(); ++i) {
            setEditTextLinistener(i);
        }

        ImageView imgBack = (ImageView) findViewById(R.id.imgBack);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        sendCaptchaLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 綁定 - 發送驗證碼
                showProgressDialog(getString(R.string.loading_message_send_number_sms));
                sendSMSCode();;
            }
        });

        nextLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String captcha = "";
                for (EditText editText : captchaEditTextList) {
                    String editTextCaptcha = editText.getText().toString().trim();
                    if (TextUtils.isEmpty(editTextCaptcha)) {
                        DialogUtil.getErrorAlertDialog(VerifyPhoneActivity.this, "验证码不能空白").show();
                        return;
                    }

                    captcha = String.format("%s%s", captcha, editTextCaptcha);
                }

                showProgressDialog(getString(R.string.loading_message_update_phone_rotection));
                checkSMSCode(captcha);
            }
        });
    }

    private void closeKeyboard() {
        if(captchaEditTextList.get(0).getText().toString().length() == 1 && captchaEditTextList.get(1).getText().toString().length() == 1 &&
                captchaEditTextList.get(2).getText().toString().length() == 1 && captchaEditTextList.get(3).getText().toString().length() == 1 &&
                captchaEditTextList.get(4).getText().toString().length() == 1 && captchaEditTextList.get(5).getText().toString().length() == 1) {

            for (EditText editText : captchaEditTextList) {
                editText.clearFocus();
            }

            IBinder mIBinder = VerifyPhoneActivity.this.getCurrentFocus().getWindowToken();
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(mIBinder, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void setButtonBackground(boolean isCountDownTimer) {
        sendCaptchaLl.setFocusable(!isCountDownTimer);
        sendCaptchaLl.setFocusableInTouchMode(!isCountDownTimer);
        sendCaptchaLl.setEnabled(!isCountDownTimer);

        if (isCountDownTimer) {
            sendCaptchaText.setTextColor(Color.parseColor("#9b9b9c"));
            sendCaptchaLl.setBackgroundResource(R.drawable.rect_gray_border);
            sendCaptchaImage.setImageResource(R.mipmap.icon_confirm_gray);
        } else {
            sendCaptchaText.setTextColor(Color.parseColor("#08A09D"));
            sendCaptchaLl.setBackgroundResource(R.drawable.rect_green_border);
            sendCaptchaImage.setImageResource(R.mipmap.icon_confirm);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    private void showProgressDialog(String loadingMessage){
        try {
            progressDialog = DialogUtil.getProgressDialog(VerifyPhoneActivity.this, loadingMessage);
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

    private void sendSMSCode(){
        mIsError = false;

        MyAsyncTask<Response> task = new MyAsyncTask<Response>(VerifyPhoneActivity.this) {
            @Override
            public Response callService() throws IOException, JsonParseException, BizException, ServiceException {
                return new AccountService().sendSMSCode();
            }

            @Override
            public void onLoaded(Response data) throws Exception {
                if(VerifyPhoneActivity.this == null || VerifyPhoneActivity.this.isFinishing()) {
                    return;
                }

                closeProgressDialog();
                if (!mIsError) {
                    if (data.getSuccess()) {
                        setButtonBackground(false);
                        MyToast.show(VerifyPhoneActivity.this, data.getMessage());

                        if (countDownTimer != null) {
                            countDownTimer.cancel();
                        }

                        setButtonBackground(true);
                        sendCaptchaText.setText(getResources().getString(R.string.activity_form_showemail_send_captcha) + " (60)");
                        countDownTimer = new CountDownTimer(60000, 1000){
                            @Override
                            public void onFinish() {
                                sendCaptchaText.setText("发送验证码至手机");
                                setButtonBackground(false);
                            }

                            @Override
                            public void onTick(long millisUntilFinished) {
                                sendCaptchaText.setText(getResources().getString(R.string.activity_form_showemail_send_captcha) + " (" + millisUntilFinished/1000 + ")");
                            }
                        };
                        countDownTimer.start();
                    }
                    else {
                        MyToast.show(VerifyPhoneActivity.this, data.getMessage());
                    }
                }
                else {
                    MyToast.show(VerifyPhoneActivity.this, data.getMessage());
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

    private void getBankInfo(final int bankId, final String bankName, final String moneyPwd){
        mIsError = false;

        showProgressDialog("正在加载");
        MyAsyncTask<Response<NewBankInfo>> task = new MyAsyncTask<Response<NewBankInfo>>(VerifyPhoneActivity.this) {
            @Override
            public Response callService() throws IOException, JsonParseException, BizException, ServiceException {
                return new WithdrawService().getBankInfo(bankId, bankName);
            }

            @Override
            public void onLoaded(Response<NewBankInfo> data) throws Exception {
                if(VerifyPhoneActivity.this == null || VerifyPhoneActivity.this.isFinishing()) {
                    return;
                }

                closeProgressDialog();
                if (!mIsError) {
                    if (data.getSuccess()) {
                        Bundle bundle = new Bundle();
                        bundle.putString("BankCard", data.getData().getBankCard());
                        bundle.putInt("BankID", data.getData().getBankID());
                        bundle.putInt("BankTypeID", data.getData().getBankTypeID());
                        bundle.putString("Bankname", data.getData().getBankname());
                        bundle.putString("CardUser", data.getData().getCardUser());
                        bundle.putString("City", data.getData().getCardUser());
                        bundle.putInt("CityID", data.getData().getCityID());
                        bundle.putString("MobileNo", data.getData().getMobileNo());
                        bundle.putString("Province", data.getData().getProvince());
                        bundle.putInt("ProvinceID", data.getData().getProvinceID());
                        bundle.putString("SiteName", data.getData().getSiteName());
                        bundle.putString("UserID", data.getData().getUserID());
                        bundle.putString("ApplyTime", data.getData().getApplyTime());
                        bundle.putString("moneyPwd", moneyPwd);

                        Intent next = new Intent(VerifyPhoneActivity.this, ModifyBankCardInformationActivity.class);
                        next.putExtra("BankInfo", bundle);
                        startActivity(next);
                        finish();
                    }
                    else {
                        MyToast.show(VerifyPhoneActivity.this, data.getMessage());
                    }
                }
                else {
                    MyToast.show(VerifyPhoneActivity.this, data.getMessage());
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

    private void checkSMSCode(final String verificationCode){
        mIsError = false;

        MyAsyncTask<Response> task = new MyAsyncTask<Response>(VerifyPhoneActivity.this) {
            @Override
            public Response callService() throws IOException, JsonParseException, BizException, ServiceException {
                return new AccountService().checkSMSCode(verificationCode);
            }

            @Override
            public void onLoaded(Response data) throws Exception {
                if(VerifyPhoneActivity.this == null || VerifyPhoneActivity.this.isFinishing()) {
                    return;
                }

                closeProgressDialog();
                if (!mIsError) {
                    if (data.getSuccess()) {
                        DialogUtil.getPromptAlertDialog(VerifyPhoneActivity.this, "提示", "您已成功通过身分验证，请按确定，进入银行卡信息管理页", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Bundle bankInfo = getIntent().getBundleExtra("BankInfo");
                                int bankId = bankInfo.getInt("BankId", 0);
                                String cardUser = bankInfo.getString("CardUser", "");
                                String moneyPwd = getIntent().getCharSequenceExtra("moneyPwd").toString();
                                getBankInfo(bankId, cardUser, moneyPwd);
                            }
                        }).show();
                    }
                    else {
                        DialogUtil.getErrorAlertDialog(VerifyPhoneActivity.this, data.getMessage()).show();
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

    private void setEditTextLinistener(final int index) {
        captchaEditTextList.get(index).addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,int count) {
                String captchaStr = captchaEditTextList.get(index).getText().toString();
                if (captchaStr.length() == 1) {
                    captchaEditTextList.get(index).clearFocus();
                    if (index+1 < captchaEditTextList.size()) {
                        captchaEditTextList.get(index+1).requestFocus();
                    } else {
                        closeKeyboard();
                    }
                } else if(captchaStr.length() == 2) {
                    captchaEditTextList.get(index).setText(captchaStr.substring(1, 2));
                }
            }
        });
        if (index > 0) {
            captchaEditTextList.get(index).setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                        if(captchaEditTextList.get(index).getText().toString().length() == 0) {
                            captchaEditTextList.get(index).clearFocus();
                            captchaEditTextList.get(index-1).setText("");
                            captchaEditTextList.get(index-1).requestFocus();
                        }
                    }
                    return false;
                }
            });
        }
    }
}
