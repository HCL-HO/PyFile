package com.hec.app.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonParseException;
import com.hec.app.R;
import com.hec.app.activity.base.BaseActivity;
import com.hec.app.entity.BizException;
import com.hec.app.entity.Response;
import com.hec.app.framework.widget.MyToast;
import com.hec.app.util.DialogUtil;
import com.hec.app.util.MyAsyncTask;
import com.hec.app.webservice.AccountService;
import com.hec.app.webservice.ServiceException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PhoneActivity extends BaseActivity {
    private TextView title;
    private TextView phoneErrorMsgText;
    private TextView sendPhoneBtnText;
    private ImageView sendPhoneBtnIcon;
    private EditText mEtSettingPhone;
    private List<EditText> etSettingCaptchaList;
    private View phoneErrorView;
    private View sendPhoneBtn;
    private View sendCaptchaBtn;
    private View phoneDeleteBtn;
    private View phoneView;
    private ProgressDialog mProgressDialog;
    private CountDownTimer countDownTimer;
    private String phoneNumber;
    private boolean isPhoneRotection;
    private boolean mIsError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);

        phoneNumber = getIntent().getStringExtra("Phone");
        isPhoneRotection = getIntent().getBooleanExtra("isPhoneRotection", false);

        View imgBack = findViewById(R.id.imgBack);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        TextView tvPhonezRemark = (TextView) findViewById(R.id.phone_remark);
        tvPhonezRemark.setText(Html.fromHtml("<font color=#cc0029>*</font> " + getString(R.string.remark3)));

        title = (TextView) findViewById(R.id.title);
        phoneView = findViewById(R.id.phone_view);
        phoneErrorView = findViewById(R.id.phone_error);
        phoneErrorMsgText = (TextView) findViewById(R.id.phone_error_msg);
        sendPhoneBtnText = (TextView) findViewById(R.id.textView_send_captcha);
        sendPhoneBtnIcon = (ImageView) findViewById(R.id.image_send_captcha);

        mEtSettingPhone = (EditText) findViewById(R.id.setting_phone);
        mEtSettingPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPhoneErrorView(false, "");
            }
        });

        etSettingCaptchaList = new ArrayList<>();
        etSettingCaptchaList.add((EditText) findViewById(R.id.setting_captcha_1));
        etSettingCaptchaList.add((EditText) findViewById(R.id.setting_captcha_2));
        etSettingCaptchaList.add((EditText) findViewById(R.id.setting_captcha_3));
        etSettingCaptchaList.add((EditText) findViewById(R.id.setting_captcha_4));
        etSettingCaptchaList.add((EditText) findViewById(R.id.setting_captcha_5));
        etSettingCaptchaList.add((EditText) findViewById(R.id.setting_captcha_6));

        for (int i = 0; i < etSettingCaptchaList.size(); ++i) {
            setEditTextLinistener(i);
        }

        sendPhoneBtn = findViewById(R.id.confirm_send_captcha);
        sendPhoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPhoneRotection) {
                    // 解綁 - 發送驗證碼
                    showProgressDialog(getString(R.string.loading_message_send_number_sms));
                    sendSMSCode();
                } else {
                    String phone = mEtSettingPhone.getText().toString().trim();
                    if (TextUtils.isEmpty(phone)) {
                        setPhoneErrorView(true, "手机号码不能空白");
                    }
                    else {
                        Pattern patternSms = Pattern.compile("^[1]{1}([3|5|8]{1}\\d{1}|[7]{1}[0]{1})\\d{8}");
                        Matcher matcherSms = patternSms.matcher(phone);
                        if (!matcherSms.matches()) {
                            setPhoneErrorView(true, "手机号码格式错误");
                            return;
                        }

                        // 綁定 - 發送驗證碼
                        showProgressDialog(getString(R.string.loading_message_send_number_sms));
                        sendNumberSMS(phone);
                    }
                }

            }
        });

        sendCaptchaBtn = findViewById(R.id.confirm_change);
        sendCaptchaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String captcha = "";
                for (EditText editText : etSettingCaptchaList) {
                    String editTextCaptcha = editText.getText().toString().trim();
                    if (TextUtils.isEmpty(editTextCaptcha)) {
                        DialogUtil.getErrorAlertDialog(PhoneActivity.this, "验证码不能空白").show();
                        return;
                    }

                    captcha = String.format("%s%s", captcha, editTextCaptcha);
                }

                showProgressDialog(getString(R.string.loading_message_update_phone_rotection));
                if (isPhoneRotection) {
                    // 解綁 - 發送簡訊驗證碼
                    checkSMSCodeAndUnPhoneRotection(captcha);
                } else {
                    // 綁定 - 發送簡訊驗證碼
                    updateIsPhoneRotection(captcha);
                }

                IBinder mIBinder = PhoneActivity.this.getCurrentFocus().getWindowToken();
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(mIBinder, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });

        phoneDeleteBtn = findViewById(R.id.phone_btn_delete);
        phoneDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEtSettingPhone.setText("");
                setPhoneErrorView(false, "");
            }
        });

        updateView();
    }

    private void setEditTextLinistener(final int index) {
        etSettingCaptchaList.get(index).addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,int count) {
                String captchaStr = etSettingCaptchaList.get(index).getText().toString();
                if(captchaStr.length() == 1) {
                    etSettingCaptchaList.get(index).clearFocus();
                    if (index+1 < etSettingCaptchaList.size()) {
                        etSettingCaptchaList.get(index+1).requestFocus();
                    } else {
                        closeKeyboard();
                    }
                } else if(captchaStr.length() == 2) {
                    etSettingCaptchaList.get(index).setText(captchaStr.substring(1, 2));
                }
            }
        });
        if (index > 0) {
            etSettingCaptchaList.get(index).setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                        if(etSettingCaptchaList.get(index).getText().toString().length() == 0) {
                            etSettingCaptchaList.get(index).clearFocus();
                            etSettingCaptchaList.get(index-1).setText("");
                            etSettingCaptchaList.get(index-1).requestFocus();
                        }
                    }
                    return false;
                }
            });
        }
    }

    private void sendNumberSMS(final String phone){
        mIsError = false;

        MyAsyncTask<Response> task = new MyAsyncTask<Response>(PhoneActivity.this) {
            @Override
            public Response callService() throws IOException, JsonParseException, BizException, ServiceException {
                return new AccountService().sendNumberSMS(phone);
            }

            @Override
            public void onLoaded(Response data) throws Exception {
                if(PhoneActivity.this == null || PhoneActivity.this.isFinishing()) {
                    return;
                }

                closeProgressDialog();
                if (!mIsError) {
                    if (data.getSuccess()) {
                        setButtonBackground(false);
                        MyToast.show(PhoneActivity.this, data.getMessage());

                        if (countDownTimer != null) {
                            countDownTimer.cancel();
                        }

                        setButtonBackground(true);
                        sendPhoneBtnText.setText(getString(R.string.activity_form_showemail_send_captcha) + " (60)");
                        countDownTimer = new CountDownTimer(60000, 1000){
                            @Override
                            public void onFinish() {
                                sendPhoneBtnText.setText(R.string.activity_form_showemail_send_captcha);
                                setButtonBackground(false);
                            }

                            @Override
                            public void onTick(long millisUntilFinished) {
                                sendPhoneBtnText.setText(getString(R.string.activity_form_showemail_send_captcha) + " (" + millisUntilFinished/1000 + ")");
                            }
                        };
                        countDownTimer.start();
                    }
                    else {
                        MyToast.show(PhoneActivity.this, data.getMessage());
                    }
                }
                else {
                    MyToast.show(PhoneActivity.this, data.getMessage());
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

    private void sendSMSCode(){
        mIsError = false;

        MyAsyncTask<Response> task = new MyAsyncTask<Response>(PhoneActivity.this) {
            @Override
            public Response callService() throws IOException, JsonParseException, BizException, ServiceException {
                return new AccountService().sendSMSCode();
            }

            @Override
            public void onLoaded(Response data) throws Exception {
                if(PhoneActivity.this == null || PhoneActivity.this.isFinishing()) {
                    return;
                }

                closeProgressDialog();
                if (!mIsError) {
                    if (data.getSuccess()) {
                        setButtonBackground(false);
                        MyToast.show(PhoneActivity.this, data.getMessage());

                        if (countDownTimer != null) {
                            countDownTimer.cancel();
                        }

                        setButtonBackground(true);
                        sendPhoneBtnText.setText(getString(R.string.activity_form_showemail_send_captcha) + " (60)");
                        countDownTimer = new CountDownTimer(60000, 1000){
                            @Override
                            public void onFinish() {
                                sendPhoneBtnText.setText(R.string.activity_form_showemail_send_captcha);
                                setButtonBackground(false);
                            }

                            @Override
                            public void onTick(long millisUntilFinished) {
                                sendPhoneBtnText.setText(getString(R.string.activity_form_showemail_send_captcha) + " (" + millisUntilFinished/1000 + ")");
                            }
                        };
                        countDownTimer.start();
                    }
                    else {
                        MyToast.show(PhoneActivity.this, data.getMessage());
                    }
                }
                else {
                    MyToast.show(PhoneActivity.this, data.getMessage());
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

    private void updateIsPhoneRotection(final String verificationCode){
        mIsError = false;

        MyAsyncTask<Response> task = new MyAsyncTask<Response>(PhoneActivity.this) {
            @Override
            public Response callService() throws IOException, JsonParseException, BizException, ServiceException {
                return new AccountService().updateIsPhoneRotection(verificationCode);
            }

            @Override
            public void onLoaded(Response data) throws Exception {
                if(PhoneActivity.this == null || PhoneActivity.this.isFinishing()) {
                    return;
                }

                closeProgressDialog();
                if (!mIsError) {
                    if (data.getSuccess()) {
                        isPhoneRotection = true;
                        showCaptchaSuccess();
                    }
                    else {
                        DialogUtil.getErrorAlertDialog(PhoneActivity.this, data.getMessage()).show();
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

    private void checkSMSCodeAndUnPhoneRotection(final String verificationCode){
        mIsError = false;

        MyAsyncTask<Response> task = new MyAsyncTask<Response>(PhoneActivity.this) {
            @Override
            public Response callService() throws IOException, JsonParseException, BizException, ServiceException {
                return new AccountService().checkSMSCodeAndUnPhoneRotection(verificationCode);
            }

            @Override
            public void onLoaded(Response data) throws Exception {
                if(PhoneActivity.this == null || PhoneActivity.this.isFinishing()) {
                    return;
                }

                closeProgressDialog();
                if (!mIsError) {
                    if (data.getSuccess()) {
                        isPhoneRotection = false;
                        showCaptchaSuccess();
                    }
                    else {
                        DialogUtil.getErrorAlertDialog(PhoneActivity.this, data.getMessage()).show();
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

    private void showProgressDialog(String loadingMessage){
        try {
            mProgressDialog = DialogUtil.getProgressDialog(PhoneActivity.this, loadingMessage);
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

    private void updateView() {
        if (isPhoneRotection) {
            title.setText("手机解绑");
            phoneView.setBackgroundColor(Color.parseColor("#e7f5f6"));
        } else {
            title.setText("手机绑定");
            phoneView.setBackgroundColor(Color.parseColor("#f2f2f2"));
        }

        if (!TextUtils.isEmpty(phoneNumber)) {
            mEtSettingPhone.setText(phoneNumber);
        }

        mEtSettingPhone.setFocusable(!isPhoneRotection);
        mEtSettingPhone.setFocusableInTouchMode(!isPhoneRotection);
        mEtSettingPhone.setEnabled(!isPhoneRotection);
    }

    private void setPhoneErrorView(boolean isShow, String errorMsg) {
        if (isShow) {
            phoneView.setBackgroundResource(R.drawable.rect_no_round);
            phoneDeleteBtn.setVisibility(View.VISIBLE);
            phoneErrorView.setVisibility(View.VISIBLE);
            phoneErrorMsgText.setText(errorMsg);
        }
        else {
            phoneView.setBackgroundResource(0);
            phoneView.setBackgroundColor(Color.parseColor("#f2f2f2"));
            phoneDeleteBtn.setVisibility(View.GONE);
            phoneErrorView.setVisibility(View.GONE);
            phoneErrorMsgText.setText("");
        }
    }

    private void closeKeyboard() {
        if(etSettingCaptchaList.get(0).getText().toString().length() == 1 && etSettingCaptchaList.get(1).getText().toString().length() == 1 &&
                etSettingCaptchaList.get(2).getText().toString().length() == 1 && etSettingCaptchaList.get(3).getText().toString().length() == 1 &&
                etSettingCaptchaList.get(4).getText().toString().length() == 1 && etSettingCaptchaList.get(5).getText().toString().length() == 1) {

            for (EditText editText : etSettingCaptchaList) {
                editText.clearFocus();
            }

            IBinder mIBinder = PhoneActivity.this.getCurrentFocus().getWindowToken();
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(mIBinder, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void setButtonBackground(boolean isCountDownTimer) {
        sendPhoneBtn.setFocusable(!isCountDownTimer);
        sendPhoneBtn.setFocusableInTouchMode(!isCountDownTimer);
        sendPhoneBtn.setEnabled(!isCountDownTimer);

        if (isCountDownTimer) {
            sendPhoneBtnText.setTextColor(Color.parseColor("#9b9b9c"));
            sendPhoneBtn.setBackgroundResource(R.drawable.rect_gray_border);
            sendPhoneBtnIcon.setImageResource(R.mipmap.icon_confirm_gray);
        } else {
            sendPhoneBtnText.setTextColor(Color.parseColor("#08A09D"));
            sendPhoneBtn.setBackgroundResource(R.drawable.rect_green_border);
            sendPhoneBtnIcon.setImageResource(R.mipmap.icon_confirm);
        }
    }

    private void showCaptchaSuccess() {
        AlertDialog dialog;
        if (isPhoneRotection) {
            dialog = DialogUtil.getPromptAlertDialog(PhoneActivity.this, "您已成功绑定手机", "手机绑定后可以透过手机短信，修改或找回：登入密码、资金密码、修改银行卡资料，帐户安全更有保障", null);
        } else {
            dialog = DialogUtil.getPromptAlertDialog(PhoneActivity.this, "您已成功解绑手机", "为了您的帐户安全，请尽速完成新手机绑定", null);
        }
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                finish();
            }
        });
        dialog.show();

        for (EditText editText : etSettingCaptchaList) {
            editText.setText("");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}