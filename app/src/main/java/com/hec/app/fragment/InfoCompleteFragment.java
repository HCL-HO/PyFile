package com.hec.app.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.google.gson.JsonParseException;
import com.hec.app.R;
import com.hec.app.activity.StartActivity;
import com.hec.app.activity.base.BaseApp;
import com.hec.app.config.CommonConfig;
import com.hec.app.entity.BizException;
import com.hec.app.entity.RecoverPWInfo;
import com.hec.app.entity.Response;
import com.hec.app.framework.widget.MyToast;
import com.hec.app.util.CustomerAccountManager;
import com.hec.app.util.DialogUtil;
import com.hec.app.util.MyAsyncTask;
import com.hec.app.webservice.AccountService;
import com.hec.app.webservice.ServiceException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.Context.MODE_PRIVATE;

/**
 * A placeholder fragment containing a simple view.
 */
public class InfoCompleteFragment extends Fragment {

    private View mView;
    private EditText mEtPwd;
    private EditText mEtPwdConfirm;
    private EditText mEtEmail;
    private EditText mEtPhone;
    private EditText mEtAnswerOne;
    private EditText mEtAnswerTwo;
    private ImageView mImgPwdError;
    private ImageView mImgPwdConfirmError;
    private ImageView mImgAnswerOneError;
    private ImageView mImgAnswerTwoError;
    private ImageView mImgEmailError;
    private ImageView mImgPhoneError;
    private Spinner mSpinnerValidIssueOne;
    private Spinner mSpinnerValidIssueTwo;
    private ProgressDialog mProgressDialog;

    private List<RecoverPWInfo> mQuestionList;
    private List<String> mVaildIssueList;

    private boolean mIsError = false;

    public InfoCompleteFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_info_complete, container, false);
        mEtPwd = (EditText) mView.findViewById(R.id.new_initinfo_pwd);
        mEtPwdConfirm = (EditText) mView.findViewById(R.id.new_initinfo_pwd_confrimed);
        mEtAnswerOne = (EditText) mView.findViewById(R.id.initinfo_answer1);
        mEtAnswerTwo = (EditText) mView.findViewById(R.id.initinfo_answer2);
        mEtEmail = (EditText) mView.findViewById(R.id.init_email);
        mEtPhone = (EditText) mView.findViewById(R.id.init_sms);
        mImgPwdError = (ImageView) mView.findViewById(R.id.error_initinfo1);
        mImgPwdConfirmError = (ImageView) mView.findViewById(R.id.error_initinfo2);
        mImgAnswerOneError = (ImageView) mView.findViewById(R.id.error_initinfo3);
        mImgAnswerTwoError = (ImageView) mView.findViewById(R.id.error_initinfo4);
        mImgEmailError = (ImageView) mView.findViewById(R.id.error_initinfo5);
        mImgPhoneError = (ImageView) mView.findViewById(R.id.error_initinfo6);

        getValidissuelist();
        uploadUserInfo();
        return mView;
    }
    private boolean checkPwdandEmailLocal() {
        String password = mEtPwd.getText().toString();
        Pattern patternPwd = Pattern.compile("^[a-zA-Z0-9_]+$");
        Matcher matcherPwd = patternPwd.matcher(password);

        String psswordConfrim = mEtPwdConfirm.getText().toString();
        String answerOne = mEtAnswerOne.getText().toString();
        String answerTwo = mEtAnswerTwo.getText().toString();

        String email = mEtEmail.getText().toString().trim();
        Pattern patternEmail = Pattern.compile("^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$");
        Matcher matcherEmail = patternEmail.matcher(email);

        String phone = mEtPhone.getText().toString();
        Pattern patternPhone = Pattern.compile("^[1]{1}([3|5|8]{1}\\d{1}|[7]{1}[0]{1})\\d{8}");
        Matcher matcherPhone = patternPhone.matcher(phone);

        if (password.isEmpty() || psswordConfrim.isEmpty() || answerOne.isEmpty() || answerTwo.isEmpty() || email.isEmpty()) {
            if (password.isEmpty()) {
                mImgPwdError.setVisibility(View.VISIBLE);
                mEtPwd.setHint(R.string.fragment_infocomplete_pwd_error_hint);
                mEtPwd.setBackgroundResource(R.drawable.rect_no_round);
                cancelerr(mImgPwdError,mEtPwd);
            }
            if (psswordConfrim.isEmpty()) {
                mImgPwdConfirmError.setVisibility(View.VISIBLE);
                mEtPwdConfirm.setHint(R.string.fragment_infocomplete_pwdconfirm_error_hint);
                mEtPwdConfirm.setBackgroundResource(R.drawable.rect_no_round);
                cancelerr(mImgPwdConfirmError, mEtPwdConfirm);
            }
            if (answerOne.isEmpty()) {
                mImgAnswerOneError.setVisibility(View.VISIBLE);
                mEtAnswerOne.setHint(R.string.fragment_infocomplete_ans_error_hint);
                mEtAnswerOne.setBackgroundResource(R.drawable.rect_no_round);
                cancelerr(mImgAnswerOneError, mEtAnswerOne);
            }
            if (answerTwo.isEmpty()) {
                mImgAnswerTwoError.setVisibility(View.VISIBLE);
                mEtAnswerTwo.setHint(R.string.fragment_infocomplete_ans_error_hint);
                mEtAnswerTwo.setBackgroundResource(R.drawable.rect_no_round);
                cancelerr(mImgAnswerTwoError, mEtAnswerTwo);
            }
            if (email.isEmpty()) {
                mImgEmailError.setVisibility(View.VISIBLE);
                mEtEmail.setHint(R.string.fragment_infocomplete_email_error_hint);
                mEtEmail.setBackgroundResource(R.drawable.rect_no_round);
                cancelerr(mImgEmailError, mEtEmail);
            }

            MyToast.show(getContext(), getString(R.string.error_message_contents_empty));
            return false;
        }

        if (mSpinnerValidIssueOne == null || mSpinnerValidIssueTwo == null) {
            MyToast.show(getContext(), getString(R.string.error_message_pwd_ans_empty));
            return false;
        }
        if (mSpinnerValidIssueOne.getSelectedItemPosition() == mSpinnerValidIssueTwo.getSelectedItemPosition()) {
            MyToast.show(getContext(), getString(R.string.error_message_pwd_ans_different));
            return false;
        }

        if (!password.equals(psswordConfrim)) {
            mImgPwdConfirmError.setVisibility(View.VISIBLE);
            mEtPwdConfirm.setHint(R.string.fragment_infocomplete_pwd_repeat_hint);
            mEtPwdConfirm.setBackgroundResource(R.drawable.rect_no_round);
            cancelerr(mImgPwdConfirmError, mEtPwdConfirm);
            MyToast.show(getContext(), getString(R.string.error_message_pwd_input));
            return false;
        }
        if (!matcherPwd.matches()) {
            mImgPwdError.setVisibility(View.VISIBLE);
            mEtPwd.setHint(R.string.fragment_infocomplete_pwd_error_hint);
            mEtPwd.setBackgroundResource(R.drawable.rect_no_round);
            cancelerr(mImgPwdError, mEtPwd);
            MyToast.show(getContext(), getString(R.string.error_message_pwd_invaild_symbol));
            return false;
        }
        if (!matcherEmail.matches()) {
            mImgEmailError.setVisibility(View.VISIBLE);
            mEtEmail.setHint(R.string.fragment_infocomplete_email_error_hint);
            mEtEmail.setBackgroundResource(R.drawable.rect_no_round);
            cancelerr(mImgEmailError, mEtEmail);
            MyToast.show(getContext(), getString(R.string.error_message_email_input));
            return false;
        }

        if (password.length() < 6 || password.length() > 16) {
            MyToast.show(getContext(), "请输入6到16个字符");
            return false;
        }

        if (BaseApp.isPasswordEasy(password)) {
            MyToast.show(getContext(), getString(R.string.error_message_money_pw_stupid));
            return false;
        }

        // FIXME: 2017/9/6  簡訊找回密碼
        if (!phone.isEmpty()) {
            if (!matcherPhone.matches()) {
                mImgPhoneError.setVisibility(View.VISIBLE);
                mEtPhone.setHint(R.string.fragment_infocomplete_phone_error_hint);
                mEtPhone.setBackgroundResource(R.drawable.rect_no_round);
                cancelerr(mImgPhoneError, mEtPhone);
                MyToast.show(getContext(), getString(R.string.error_message_phone_input));
                return false;
            }
        }

        return true;
    }

    private void uploadUserInfo() {
        LinearLayout llNext = (LinearLayout) mView.findViewById(R.id.botton_btn_next);
        llNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkPwdandEmailLocal()) {
                    return;
                }

                String moneyPwd = mEtPwd.getText().toString().trim();
                String ans1 = mEtAnswerOne.getText().toString().trim();
                String ans2 = mEtAnswerTwo.getText().toString().trim();
                String email = mEtEmail.getText().toString().trim();
                String phone = mEtPhone.getText().toString().trim();
                int qid1 = mSpinnerValidIssueOne.getSelectedItemPosition();
                int qid2 = mSpinnerValidIssueTwo.getSelectedItemPosition();

                if (mQuestionList != null && qid1 >= 0 && qid1 < mQuestionList.size()) {
                    qid1 = mQuestionList.get(qid1).getQuestionID();
                }
                else {
                    return;
                }
                if (mQuestionList != null && qid2 >= 0 && qid2 < mQuestionList.size()) {
                    qid2 = mQuestionList.get(qid2).getQuestionID();
                }
                else {
                    return;
                }

                submitData(moneyPwd, qid1, qid2, ans1, ans2, email, phone);
            }
        });
    }

    private void submitData(final String moneyPwd, final int qid1, final int qid2, final String ans1, final String ans2, final String email, final String phone) {
        showProgressDialog(getString(R.string.loading_data));
        mIsError = false;
        MyAsyncTask<Response> task = new MyAsyncTask<Response>(getActivity()) {
            @Override
            public  Response callService() throws IOException, JsonParseException, BizException, ServiceException {
                return new AccountService().submitSecurityInfo(moneyPwd, qid1, qid2, ans1, ans2, email, phone);
            }

            @Override
            public void onLoaded(Response data) throws Exception {
                if(getActivity() == null || getActivity().isFinishing()) {
                    return;
                }

                closeProgressDialog();
                if (!mIsError) {
                    if (data.getSuccess()) {
                        MyToast.show(getContext(), getString(R.string.change_success));
                        CustomerAccountManager.getInstance().getCustomer().setInfoComplete(true);
                        SharedPreferences token = getActivity().getSharedPreferences(CommonConfig.KEY_TOKEN, MODE_PRIVATE);
                        SharedPreferences.Editor editor = token.edit();
                        editor.putBoolean(CommonConfig.KEY_TOKEN_INFOCOMPLETE, true);
                        editor.commit();
                        getActivity().finish();
                    }
                    else {
                        MyToast.show(getContext(), data.getMessage());
                    }
                }
                else {
                    BaseApp.changeUrl(getActivity(), new BaseApp.OnChangeUrlListener() {
                        @Override
                        public void changeSuccess() {
                            submitData(moneyPwd, qid1, qid2, ans1, ans2, email, phone);
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

    private void getValidissuelist() {
        mQuestionList = new ArrayList<RecoverPWInfo>();
        mVaildIssueList = new ArrayList<String>();
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, mVaildIssueList);

        mSpinnerValidIssueOne = (Spinner) mView.findViewById(R.id.initinfo_spinner1);
        mSpinnerValidIssueOne.setAdapter(adapter);
        mSpinnerValidIssueOne.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mQuestionList != null && position < mQuestionList.size()) {
                    mEtAnswerOne.setHint(mQuestionList.get(position).getLimitDes());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mSpinnerValidIssueTwo = (Spinner) mView.findViewById(R.id.initinfo_spinner2);
        mSpinnerValidIssueTwo.setAdapter(adapter);
        mSpinnerValidIssueTwo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mQuestionList != null && position < mQuestionList.size()) {
                    mEtAnswerTwo.setHint(mQuestionList.get(position).getLimitDes());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        getQuestion(adapter);
    }

    private void getQuestion(final ArrayAdapter<String> adapter){
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

                if (!mIsError) {
                    for (RecoverPWInfo info : data) {
                        mQuestionList.add(info);
                        mVaildIssueList.add(info.getQuestion());
                    }
                    mSpinnerValidIssueOne.setSelection(0);
                    mSpinnerValidIssueTwo.setSelection(0);
                    adapter.notifyDataSetChanged();
                }
                else{
                    if (BaseApp.getAppBean().resetApiUrl(getActivity())) {
                        getQuestion(adapter);
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

    private void cancelerr(final ImageView img, final EditText editText){
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                img.setVisibility(View.GONE);
                editText.setBackgroundResource(0);
                editText.setText("");
            }
        });

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                img.setVisibility(View.GONE);
                editText.setBackgroundResource(0);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
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
}
