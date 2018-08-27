package com.hec.app.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.JsonParseException;
import com.hec.app.R;
import com.hec.app.activity.SetNewPasswordActivity;
import com.hec.app.activity.base.BaseApp;
import com.hec.app.config.CommonConfig;
import com.hec.app.entity.BizException;
import com.hec.app.entity.RecoverPWInfo;
import com.hec.app.entity.Response;
import com.hec.app.framework.widget.MyToast;
import com.hec.app.util.DialogUtil;
import com.hec.app.util.MyAsyncTask;
import com.hec.app.webservice.AccountService;
import com.hec.app.webservice.ServiceException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class ValidIssueFragment extends Fragment {
    private Spinner mSpinnerValidIssueOne;
    private Spinner mSpinnerValidIssueTwo;
    private EditText mEtAnswerOne;
    private EditText mEtAnswerTwo;
    private LinearLayout mLlAnswerErrorOne;
    private LinearLayout mLlAnswerErrorTwo;
    private ImageView mImgBtnDeleteOne;
    private ImageView mImgBtnDeleteTwo;
    private TextView mTvErrorMsgOne;
    private TextView mTvErrorMsgTwo;
    private RelativeLayout mRlEditFieldgOne;
    private RelativeLayout mRlEditFieldgTwo;
    private ProgressDialog mProgressDialog;
    private boolean mIsError = false;
    private String mUserName = "";

    private interface PingCallback
    {
        public void callback();
    }

    public ValidIssueFragment(){
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        showProgressDialog(getString(R.string.loading_message_ISSUEE));

        View view = inflater.inflate(R.layout.fragment_valid_issue, container, false);
        mLlAnswerErrorOne = (LinearLayout)view.findViewById(R.id.error1);
        mLlAnswerErrorTwo = (LinearLayout)view.findViewById(R.id.error2);
        mTvErrorMsgOne = (TextView)view.findViewById(R.id.error_msg1);
        mTvErrorMsgTwo = (TextView)view.findViewById(R.id.error_msg2);
        mRlEditFieldgOne = (RelativeLayout)view.findViewById(R.id.edit_field1);
        mRlEditFieldgTwo = (RelativeLayout)view.findViewById(R.id.edit_field2);
        mUserName = getArguments().getString(CommonConfig.BUNDLE_FIND_PASSWORD_USERNAME);

        final List<RecoverPWInfo> QuestionList = new ArrayList<RecoverPWInfo>();
        final List<String> VaildIssueList = new ArrayList<String>();
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, VaildIssueList);
        mSpinnerValidIssueOne = (Spinner)view.findViewById(R.id.valid_issue_spinner1);
        mSpinnerValidIssueOne.setAdapter(adapter);
        mSpinnerValidIssueTwo = (Spinner)view.findViewById(R.id.valid_issue_spinner2);
        mSpinnerValidIssueTwo.setAdapter(adapter);
        getQuestion(QuestionList, VaildIssueList, adapter);

        mEtAnswerOne = (EditText)view.findViewById(R.id.valid_issue_answer_edittext1);
        mEtAnswerOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAnswerOneErrorView(false);
            }
        });
        mEtAnswerTwo = (EditText)view.findViewById(R.id.valid_issue_answer_edittext2);
        mEtAnswerTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAnswerTwoErrorView(false);
            }
        });

        mImgBtnDeleteOne = (ImageView)view.findViewById(R.id.btn_delete1);
        mImgBtnDeleteOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEtAnswerOne.setText("");
                setAnswerOneErrorView(false);
            }
        });
        mImgBtnDeleteTwo = (ImageView)view.findViewById(R.id.btn_delete2);
        mImgBtnDeleteTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEtAnswerTwo.setText("");
                setAnswerTwoErrorView(false);
            }
        });

        RelativeLayout rlBtnValidIssue = (RelativeLayout)view.findViewById(R.id.btnValidIssue);
        rlBtnValidIssue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v){
                mIsError = false;
                boolean isAnswerOneEmpty = mEtAnswerOne.getText().toString().trim().isEmpty();
                boolean isAnswerTwoEmpty = mEtAnswerTwo.getText().toString().trim().isEmpty();

                if (isAnswerOneEmpty || isAnswerTwoEmpty) {
                    if (isAnswerOneEmpty) {
                        mTvErrorMsgOne.setText(R.string.valid_issue_empty_prompt1);
                        setAnswerOneErrorView(true);
                    }
                    if (isAnswerTwoEmpty) {
                        mTvErrorMsgTwo.setText(R.string.valid_issue_empty_prompt1);
                        setAnswerTwoErrorView(true);
                    }
                }
                else {
                    showProgressDialog(getString(R.string.loading_message_submitting));
                    checkQuestion();
                }
            }
        });

        return view;
    }

    private void checkQuestion(){
        mIsError = false;
        MyAsyncTask<Response> task = new MyAsyncTask<Response>(getActivity()) {
            @Override
            public Response callService() throws IOException, JsonParseException, BizException, ServiceException {
                return new AccountService().checkQuestion(mUserName
                        ,mSpinnerValidIssueOne.getSelectedItemPosition() + 1
                        ,mSpinnerValidIssueTwo.getSelectedItemPosition() + 1
                        ,mEtAnswerOne.getText().toString().trim()
                        ,mEtAnswerTwo.getText().toString().trim());
            }

            @Override
            public void onLoaded(Response data) throws Exception {
                if (getActivity() == null || getActivity().isFinishing()) {
                    return;
                }

                if (!mIsError) {
                    closeProgressDialog();
                    if (data.getSuccess()) {
                        setAnswerOneErrorView(false);
                        setAnswerTwoErrorView(false);

                        Intent it = new Intent(getActivity(), SetNewPasswordActivity.class);
                        it.putExtra("userName", mUserName);
                        it.putExtra("Question1ID", mSpinnerValidIssueOne.getSelectedItemPosition() + 1);
                        it.putExtra("Question2ID", mSpinnerValidIssueTwo.getSelectedItemPosition() + 1);
                        it.putExtra("Answer1", mEtAnswerOne.getText().toString().trim());
                        it.putExtra("Answer2", mEtAnswerTwo.getText().toString().trim());
                        startActivity(it);
                        getActivity().overridePendingTransition(R.anim.push_left_in_no_alpha, R.anim.push_left_out_no_alpha);
                    }
                    else{
                        MyToast.show(getActivity(),data.getMessage());
                        mTvErrorMsgOne.setText(R.string.valid_issue_answer_error1);
                        setAnswerOneErrorView(true);
                        mTvErrorMsgTwo.setText(R.string.valid_issue_answer_error1);
                        setAnswerTwoErrorView(true);
                    }
                }
                else {
                    BaseApp.changeUrl(getActivity(), new BaseApp.OnChangeUrlListener() {
                        @Override
                        public void changeSuccess() {
                            checkQuestion();
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

    private void getQuestion(final List<RecoverPWInfo> QuestionList, final List<String> VaildIssueList, final ArrayAdapter<String> adapter){
        mIsError = false;
        MyAsyncTask<List<RecoverPWInfo>> task = new MyAsyncTask<List<RecoverPWInfo>>(getActivity()) {
            @Override
            public  List<RecoverPWInfo> callService() throws IOException, JsonParseException, BizException, ServiceException {
                return new AccountService().getQuestion();
            }

            @Override
            public void onLoaded(List<RecoverPWInfo> data) throws Exception {
                if (getActivity() == null || getActivity().isFinishing()) {
                    return;
                }

                if(!mIsError) {
                    closeProgressDialog();
                    for (RecoverPWInfo info : data) {
                        QuestionList.add(info);
                        VaildIssueList.add(info.getQuestion());
                    }
                    adapter.notifyDataSetChanged();
                }
                else {
                    BaseApp.changeUrl(getActivity(), new BaseApp.OnChangeUrlListener() {
                        @Override
                        public void changeSuccess() {
                            getQuestion(QuestionList, VaildIssueList, adapter);
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

    private void setAnswerOneErrorView(boolean isOpen){
        if (isOpen) {
            mRlEditFieldgOne.setBackgroundResource(R.drawable.rect_no_round);
            mLlAnswerErrorOne.setVisibility(View.VISIBLE);
            mImgBtnDeleteOne.setVisibility(View.VISIBLE);
        }
        else {
            mRlEditFieldgOne.setBackgroundResource(R.color.light_gray);
            mLlAnswerErrorOne.setVisibility(View.GONE);
            mImgBtnDeleteOne.setVisibility(View.GONE);
        }
    }

    private void setAnswerTwoErrorView(boolean isOpen){
        if (isOpen) {
            mRlEditFieldgTwo.setBackgroundResource(R.drawable.rect_no_round);
            mLlAnswerErrorTwo.setVisibility(View.VISIBLE);
            mImgBtnDeleteTwo.setVisibility(View.VISIBLE);
        }
        else {
            mRlEditFieldgTwo.setBackgroundResource(R.color.light_gray);
            mLlAnswerErrorTwo.setVisibility(View.GONE);
            mImgBtnDeleteTwo.setVisibility(View.GONE);
        }
    }
}
