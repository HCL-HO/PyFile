package com.hec.app.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hec.app.R;
import com.hec.app.activity.WebchatActivity;
import com.hec.app.activity.base.BaseActivityWithMenu;
import com.hec.app.config.CommonConfig;
import com.hec.app.customer_service.CustomerServiceActivity;
import com.hec.app.dialog.WebchatDialog;
import com.hec.app.entity.CustomerInfo;
import com.hec.app.util.ConstantProvider;
import com.hec.app.util.CustomerAccountManager;
import com.hec.app.util.DialogUtil;
import com.hec.app.util.OfflineTransferListener;

import java.util.ArrayList;
import java.util.List;

public class OfflinePhoneVerifyFragment extends Fragment implements View.OnClickListener {
    private TextView phoneNumTextView;
    private LinearLayout confirmSendCaptcha;
    private LinearLayout confirmToNext;
    private CheckBox captchaCheckbox;
    private String phoneNum = "";
    private List<EditText> etSettingCaptchaList;
    private boolean hasCaptcha;
    private String name;
    private String pw;
    private String amount;
    private TextView textViewSendCaptcha;
    private TextView tvCustomerService;
    private ImageView imageSendCaptcha;
    private CountDownTimer countDownTimer;

    private OfflineTransferListener listener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            phoneNum = bundle.getString("phoneNum", "");
            name = bundle.getString("name", "");
            pw = bundle.getString("pw", "");
            amount = bundle.getString("amount", "");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_offline_phone_verfiy, null);
        phoneNumTextView = (TextView) view.findViewById(R.id.phone_num);
        confirmSendCaptcha = (LinearLayout) view.findViewById(R.id.confirm_send_captcha);
        confirmToNext = (LinearLayout) view.findViewById(R.id.confirm_to_next);
        captchaCheckbox = (CheckBox) view.findViewById(R.id.captcha_checkbox);
        textViewSendCaptcha = (TextView) view.findViewById(R.id.textView_send_captcha);
        tvCustomerService = (TextView) view.findViewById(R.id.tv_customer_service);
        imageSendCaptcha = (ImageView) view.findViewById(R.id.image_send_captcha);

        etSettingCaptchaList = new ArrayList<>();
        etSettingCaptchaList.add((EditText) view.findViewById(R.id.setting_captcha_1));
        etSettingCaptchaList.add((EditText) view.findViewById(R.id.setting_captcha_2));
        etSettingCaptchaList.add((EditText) view.findViewById(R.id.setting_captcha_3));
        etSettingCaptchaList.add((EditText) view.findViewById(R.id.setting_captcha_4));
        etSettingCaptchaList.add((EditText) view.findViewById(R.id.setting_captcha_5));
        etSettingCaptchaList.add((EditText) view.findViewById(R.id.setting_captcha_6));

        for (int i = 0; i < etSettingCaptchaList.size(); ++i) {
            setEditTextListener(i);
        }

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        confirmSendCaptcha.setOnClickListener(this);
        confirmToNext.setOnClickListener(this);
        tvCustomerService.setOnClickListener(this);

        phoneNumTextView.setText(phoneNum);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_customer_service:
                CustomerInfo customer = CustomerAccountManager.getInstance().getCustomer();
                if (customer != null && customer.isVIP()) {
                    new WebchatDialog(getContext(), new WebchatDialog.OnGoToWebChatListener() {
                        @Override
                        public void onClick(int type) {
                            Intent intent = new Intent();
                            if (type == 1) {
                                intent.setClass(getContext(), CustomerServiceActivity.class);
                            } else {
                                intent.setClass(getContext(), WebchatActivity.class);
                                intent.putExtra(CommonConfig.WEBCHAT_TYPE, CommonConfig.WEBCHAT_TYPE_NORMAL);
                            }
                            startActivity(intent);
                            Intent data = new Intent();
                            getActivity().setResult(ConstantProvider.OFFLINE_TRANSFER_FINISH_RESULT_CODE, data);
                            getActivity().finish();
                        }
                    }).show();
                } else {
                    Intent intent = new Intent();
                    intent.setClass(getContext(), WebchatActivity.class);
                    intent.putExtra(CommonConfig.WEBCHAT_TYPE, CommonConfig.WEBCHAT_TYPE_NORMAL);
                    startActivity(intent);
                    Intent data = new Intent();
                    getActivity().setResult(ConstantProvider.OFFLINE_TRANSFER_FINISH_RESULT_CODE, data);
                    getActivity().finish();
                }

                break;
            case R.id.confirm_send_captcha:
                listener.onSendCaptcha();
                break;
            case R.id.confirm_to_next:
                if (countDownTimer != null) {
                    countDownTimer.cancel();
                }
                String captcha = "";
                for (EditText editText : etSettingCaptchaList) {
                    String editTextCaptcha = editText.getText().toString().trim();
                    if (TextUtils.isEmpty(editTextCaptcha)) {
                        DialogUtil.getAlertDialog(getActivity(), getString(R.string.friendly_reminder), getString(R.string.error_message_captcha_empty), getString(R.string.confirm_send), null, "", null).show();
                        return;
                    }
                    captcha = String.format("%s%s", captcha, editTextCaptcha);
                }

                listener.onTransferComplete(name, pw, amount, true, captcha, captchaCheckbox.isChecked());
                break;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (OfflineTransferListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement TransferListener");
        }
    }

    public void setHasCaptcha(boolean hasCaptcha) {
        this.hasCaptcha = hasCaptcha;
        if (hasCaptcha) {
            countDownTimer = new CountDownTimer(60000, 1000) {

                public void onTick(long millisUntilFinished) {
                    confirmSendCaptcha.setOnClickListener(null);

                    textViewSendCaptcha.setText(String.format(getString(R.string.offline_transfer_send_captcha_countdown), millisUntilFinished / 1000));
                    confirmSendCaptcha.setBackgroundColor(getResources().getColor(R.color.light_gray));
                    textViewSendCaptcha.setTextColor(getResources().getColor(R.color.white));
                    imageSendCaptcha.setImageResource(R.mipmap.icon_confirm_gray);
                    confirmSendCaptcha.setBackground(getResources().getDrawable(R.drawable.rect_gray_border));
                }

                public void onFinish() {
                    confirmSendCaptcha.setOnClickListener(OfflinePhoneVerifyFragment.this);

                    textViewSendCaptcha.setText(getString(R.string.offline_transfer_send_captcha_hint));
                    imageSendCaptcha.setImageResource(R.mipmap.icon_confirm);
                    textViewSendCaptcha.setTextColor(getResources().getColor(R.color.colorPrimary));
                    confirmSendCaptcha.setBackground(getResources().getDrawable(R.drawable.rect_green_border));
                }

            }.start();
        }
    }

    private void setEditTextListener(final int index) {
        etSettingCaptchaList.get(index).addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String captchaStr = etSettingCaptchaList.get(index).getText().toString();
                if (captchaStr.length() == 1) {
                    etSettingCaptchaList.get(index).clearFocus();
                    if (index + 1 < etSettingCaptchaList.size()) {
                        etSettingCaptchaList.get(index + 1).requestFocus();
                    } else {
                        if (etSettingCaptchaList.get(0).getText().toString().length() == 1 && etSettingCaptchaList.get(1).getText().toString().length() == 1 &&
                                etSettingCaptchaList.get(2).getText().toString().length() == 1 && etSettingCaptchaList.get(3).getText().toString().length() == 1 &&
                                etSettingCaptchaList.get(4).getText().toString().length() == 1 && etSettingCaptchaList.get(5).getText().toString().length() == 1) {

                            for (EditText editText : etSettingCaptchaList) {
                                editText.clearFocus();
                            }

                            IBinder mIBinder = getActivity().getCurrentFocus().getWindowToken();
                            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            inputMethodManager.hideSoftInputFromWindow(mIBinder, InputMethodManager.HIDE_NOT_ALWAYS);
                        }
                    }
                } else if (captchaStr.length() == 2) {
                    etSettingCaptchaList.get(index).setText(captchaStr.substring(1, 2));
                }
            }
        });
        if (index > 0) {
            etSettingCaptchaList.get(index).setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                        if (etSettingCaptchaList.get(index).getText().toString().length() == 0) {
                            etSettingCaptchaList.get(index).clearFocus();
                            etSettingCaptchaList.get(index - 1).setText("");
                            etSettingCaptchaList.get(index - 1).requestFocus();
                        }
                    }
                    return false;
                }
            });
        }
    }
}
