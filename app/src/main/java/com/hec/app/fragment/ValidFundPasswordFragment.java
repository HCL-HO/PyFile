package com.hec.app.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
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

public class ValidFundPasswordFragment extends Fragment {
    private LinearLayout mLlError;
    private EditText mEtValidFundPassword;
    private TextView mTvErrorMsg;
    private ImageView mImgBtnDelete;
    private RelativeLayout mRlEditField;
    private ProgressDialog mProgressDialog;
    private boolean mIsError = false;
    private String mUserName = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_valid_fund_password, container, false);
        mLlError = (LinearLayout)view.findViewById(R.id.error);
        mTvErrorMsg = (TextView)view.findViewById(R.id.error_msg);
        mRlEditField = (RelativeLayout)view.findViewById(R.id.edit_field);
        mUserName = getArguments().getString(CommonConfig.BUNDLE_FIND_PASSWORD_USERNAME);

        mEtValidFundPassword = (EditText)view.findViewById(R.id.set_new_password_edittext);
        mEtValidFundPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setErrorView(false);
            }
        });

        mImgBtnDelete = (ImageView)view.findViewById(R.id.btn_delete);
        mImgBtnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEtValidFundPassword.setText("");
                setErrorView(false);
            }
        });

        RelativeLayout rlBtnValidFundPassword = (RelativeLayout)view.findViewById(R.id.btnValidFundPassword);
        rlBtnValidFundPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsError = false;
                if (mEtValidFundPassword.getText().toString().trim().isEmpty()) {
                    mTvErrorMsg.setText(getResources().getString(R.string.valid_fund_password_empty_prompt));
                    setErrorView(true);
                }
                else {
                    setErrorView(false);
                    showProgressDialog(getString(R.string.loading_message_submitting));
                    changePwWithFundPw();
                }
            }
        });

        return view;
    }

    private void changePwWithFundPw(){
        mIsError = false;
        MyAsyncTask<Response> task = new MyAsyncTask<Response>(getActivity()) {
            @Override
            public  Response callService() throws IOException, JsonParseException, BizException, ServiceException {
                return new AccountService().changePwWithFundPw(mUserName, mEtValidFundPassword.getText().toString().trim());
            }

            @Override
            public void onLoaded(Response data) throws Exception {
                if(getActivity() == null || getActivity().isFinishing()) {
                    return;
                }

                closeProgressDialog();
                if (!mIsError) {
                    if (data.getSuccess()){
                        Intent it = new Intent(getActivity(), SuccessActivity.class);
                        it.putExtra(CommonConfig.INTENT_SUCCESS_VALID_EMAIL, "0");
                        it.putExtra(CommonConfig.INTENT_SUCCESS_TAG, 5);
                        startActivity(it);
                    }
                    else{
                        MyToast.show(getActivity(),data.getMessage());
                        mTvErrorMsg.setText(getString(R.string.valid_fund_password_error_prompt));
                        setErrorView(true);
                    }
                }
                else {
                    BaseApp.changeUrl(getActivity(), new BaseApp.OnChangeUrlListener() {
                        @Override
                        public void changeSuccess() {
                            changePwWithFundPw();
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
            mEtValidFundPassword.setTextColor(Color.RED);
            mImgBtnDelete.setVisibility(View.VISIBLE);
            mLlError.setVisibility(View.VISIBLE);
        }
        else {
            mRlEditField.setBackgroundResource(0);
            mRlEditField.setBackgroundColor(Color.parseColor("#f2f2f2"));
            mEtValidFundPassword.setTextColor(Color.BLACK);
            mImgBtnDelete.setVisibility(View.GONE);
            mLlError.setVisibility(View.GONE);
        }
    }
}
