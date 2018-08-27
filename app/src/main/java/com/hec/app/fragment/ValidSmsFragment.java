package com.hec.app.fragment;


import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.JsonParseException;
import com.hec.app.R;
import com.hec.app.activity.SuccessActivity;
import com.hec.app.activity.base.BaseApp;
import com.hec.app.config.CommonConfig;
import com.hec.app.entity.BizException;
import com.hec.app.entity.Response;
import com.hec.app.framework.widget.MyToast;
import com.hec.app.util.DialogUtil;
import com.hec.app.util.MyAsyncTask;
import com.hec.app.webservice.AccountService;
import com.hec.app.webservice.ServiceException;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidSmsFragment extends Fragment {
    private LinearLayout mLlError;
    private EditText mEtValidSms;
    private ImageView mImgBtnDelete;
    private TextView mErrorMsg;
    private RelativeLayout mRlEditField;
    private ProgressDialog mProgressDialog;
    private boolean mIsError = false;
    private String mUserName = "";

    public ValidSmsFragment(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_valid_sms, container, false);
        mLlError = (LinearLayout)view.findViewById(R.id.error);
        mErrorMsg = (TextView)view.findViewById(R.id.error_msg);
        mRlEditField = (RelativeLayout)view.findViewById(R.id.edit_field);
        mUserName = getArguments().getString(CommonConfig.BUNDLE_FIND_PASSWORD_USERNAME);

        mEtValidSms = (EditText)view.findViewById(R.id.valid_sms_edittext);
        mEtValidSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setErrorView(false);
            }
        });

        mImgBtnDelete = (ImageView)view.findViewById(R.id.btn_delete);
        mImgBtnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEtValidSms.setText("");
                setErrorView(false);
            }
        });

        RelativeLayout rlBtnValidEmail = (RelativeLayout)view.findViewById(R.id.btnValidEmail);
        rlBtnValidEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsError = false;
                String sms = mEtValidSms.getText().toString().trim();

                if (sms.isEmpty()) {
                    mErrorMsg.setText(getString(R.string.valid_sms_sms_empty));
                    setErrorView(true);
                }
                else {
                    Pattern patternSms = Pattern.compile("^[1]{1}([3|5|8]{1}\\d{1}|[7]{1}[0]{1})\\d{8}");
                    Matcher matcherSms = patternSms.matcher(sms);
                    if (!matcherSms.matches()) {
                        mErrorMsg.setText(getString(R.string.error_message_phone_input));
                        setErrorView(true);
                        return;
                    }

                    setErrorView(false);
                    showProgressDialog(getString(R.string.loading_message_submitting));
                    changePwWithSms();
                }
            }
        });

        return view;
    }

    private void changePwWithSms(){
        mIsError = false;
        MyAsyncTask<Response> task = new MyAsyncTask<Response>(getActivity()) {
            @Override
            public  Response callService() throws IOException, JsonParseException, BizException, ServiceException {
                return new AccountService().findLoginPwdBySMS(mUserName, mEtValidSms.getText().toString().trim(), "0");
            }

            @Override
            public void onLoaded(Response data) throws Exception {
                if(getActivity() == null || getActivity().isFinishing()) {
                    return;
                }

                closeProgressDialog();
                if (!mIsError) {
                    if (data.getSuccess()) {
                        Intent it = new Intent(getActivity(), SuccessActivity.class);
                        it.putExtra(CommonConfig.INTENT_SUCCESS_TAG, 8);
                        startActivity(it);
                        getActivity().overridePendingTransition(R.anim.push_left_in_no_alpha, R.anim.push_left_out_no_alpha);
                    }
                    else {
                        MyToast.show(getActivity(),data.getMessage());
                        mErrorMsg.setText(getString(R.string.valid_sms_sms_error));
                        setErrorView(true);
                    }
                }
                else {
                    BaseApp.changeUrl(getActivity(), new BaseApp.OnChangeUrlListener() {
                        @Override
                        public void changeSuccess() {
                            changePwWithSms();
                        }

                        @Override
                        public void changeFail() {
                            closeProgressDialog();
                        }
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
            mProgressDialog = DialogUtil.getProgressDialog(getActivity(), loadingMessage);
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

    private void setErrorView(boolean isOpen){
        if (isOpen) {
            mRlEditField.setBackgroundResource(R.drawable.rect_no_round);
            mEtValidSms.setTextColor(Color.RED);
            mImgBtnDelete.setVisibility(View.VISIBLE);
            mLlError.setVisibility(View.VISIBLE);
        }
        else {
            mRlEditField.setBackgroundResource(0);
            mRlEditField.setBackgroundColor(Color.parseColor("#f2f2f2"));
            mEtValidSms.setTextColor(Color.BLACK);
            mImgBtnDelete.setVisibility(View.GONE);
            mLlError.setVisibility(View.GONE);
        }
    }
}
