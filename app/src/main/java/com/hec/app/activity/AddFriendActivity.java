package com.hec.app.activity;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.JsonParseException;
import com.hec.app.R;
import com.hec.app.activity.base.BaseApp;
import com.hec.app.entity.BizException;
import com.hec.app.entity.RebateUtil;
import com.hec.app.entity.ServiceRequestResult;
import com.hec.app.framework.widget.MyToast;
import com.hec.app.util.DialogUtil;
import com.hec.app.util.IntentUtil;
import com.hec.app.util.MyAsyncTask;
import com.hec.app.webservice.AgentService;
import com.hec.app.webservice.BaseService;
import com.hec.app.webservice.ServiceException;

import org.json.JSONArray;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddFriendActivity extends AppCompatActivity {
    private ActionBar actionbar;
    private ArrayList<String> returnValues;
    private boolean loaded;
    private boolean pressed = false;
    private int count = 0;
    private JSONArray quotas;
    private final int ON_ERROR = 3;

    TextView tvQuotas;
    Spinner spinner;
    EditText acName, acPw, acConfirmPw;
    ImageView accountCross, passwordCross, confirmCross;
    TextView accountRedFilter, passwordRedFilter, confirmRedFilter, passwordWarningText, accountWarningText, confirmWarningText;
    LinearLayout accountWarning, passwordWarning, confirmWarning;

    AgentService agentService;
    ArrayAdapter adapter;
    RebateUtil rebates;
    ProgressDialog mProgressDialog;
    MyAsyncTask.OnError onError;
    boolean mIsError = false, showing = false;
    String prevMessage = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        spinner = (Spinner) findViewById(R.id.return_spinner);
        tvQuotas = (TextView) findViewById(R.id.add_friend_quotas);
        acPw = ((EditText) findViewById(R.id.add_enter_password));
        acConfirmPw = ((EditText) findViewById(R.id.add_enter_confirm_password));
        acName = ((EditText) findViewById(R.id.add_enter_account_name));

        passwordCross = (ImageView)findViewById(R.id.add_confirm_password_ok_1);
        passwordWarning = (LinearLayout)findViewById(R.id.add_password_warning_1);
        passwordWarningText = (TextView)findViewById(R.id.add_password_warning_text_1);
        passwordRedFilter = (TextView)findViewById(R.id.add_pw_red_filter);
        confirmCross = (ImageView)findViewById(R.id.add_confirm_password_ok);
        confirmWarning = (LinearLayout)findViewById(R.id.add_password_warning);
        confirmRedFilter = (TextView)findViewById(R.id.add_confirm_pw_red_filter);
        confirmWarningText = (TextView)findViewById(R.id.add_password_warning_text_2);
        accountCross = (ImageView)findViewById(R.id.add_account_cross);
        accountWarning = (LinearLayout)findViewById(R.id.add_account_warning);
        accountRedFilter = (TextView)findViewById(R.id.add_account_red_filter);
        accountWarningText = (TextView)findViewById(R.id.add_account_warning_text);

        agentService = new AgentService();
        returnValues = new ArrayList<>();
        quotas = new JSONArray();
        rebates = new RebateUtil();
        loaded = false;
        onError = new MyAsyncTask.OnError() {
            @Override
            public void handleError(Exception e) {
                closeProgressDialog();
                mIsError = true;
            }
        };
        adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, returnValues);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView adapterView, View view, int position, long id) {
                String selection = rebates.fromPercentage(returnValues.get(position));
                tvQuotas.setText(getResources().getString(R.string.quotas) + rebates.getQuota(selection));
            }

            public void onNothingSelected(AdapterView arg0) {

            }
        });

        getRebateList();

        EditText new_account_name = (EditText) findViewById(R.id.add_enter_account_name);
        new_account_name.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                if (accountNameOk(cs.toString())) {
                    hideWarnings(2);
                    findViewById(R.id.add_account_name_ok).setVisibility(View.VISIBLE);
                    findViewById(R.id.add_account_green_gilter).setVisibility(View.VISIBLE);
                } else {
                    if (!cs.toString().isEmpty())
                        showWarnings(4);
                    findViewById(R.id.add_account_name_ok).setVisibility(View.GONE);
                    findViewById(R.id.add_account_green_gilter).setVisibility(View.INVISIBLE);
                }
            }

            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub

            }

            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
        });
        acPw.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                if (!cs.toString().isEmpty()) {
                    hideWarnings(0);
                }
            }

            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub

            }

            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
        });
        acConfirmPw.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                if (!cs.toString().isEmpty()) {
                    hideWarnings(1);
                }
            }

            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub

            }

            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
        });
        LinearLayout confirmAdd = (LinearLayout) findViewById(R.id.confirm_add);
        confirmAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String first_pw = acPw.getText().toString();
                String confirm_pw = acConfirmPw.getText().toString();
                String account_name = acName.getText().toString();
                boolean proceed = true;

                if (!accountNameOk(account_name)) {
                    if (account_name.isEmpty())
                        showWarnings(2);
                    else
                        showWarnings(4);

                    proceed = false;
                }
                if (first_pw.isEmpty()) {
                    showWarnings(0);
                    proceed = false;
                }
                if (confirm_pw.isEmpty()){
                    showWarnings(3);
                    proceed = false;
                } else if (!first_pw.equals(confirm_pw)) {
                    showWarnings(1);
                    proceed = false;
                }
                if(!first_pw.isEmpty()&&first_pw.length() < 6 || !first_pw.isEmpty()&&first_pw.length() > 16){
                    showWarnings(6);
                    proceed = false;
                }
                if(!confirm_pw.isEmpty()&&confirm_pw.length() < 6 || !confirm_pw.isEmpty()&&confirm_pw.length() > 16){
                    showWarnings(7);
                    proceed = false;
                }

                Pattern p = Pattern.compile("^[a-zA-Z0-9_]+$");
                Matcher m = p.matcher(first_pw);
                if(!m.matches()){
                    showWarnings(5);
                    proceed = false;
                }

                if (BaseApp.isPasswordEasy(first_pw)) {
                    MyToast.show(AddFriendActivity.this, getString(R.string.error_message_login_pw_stupid));
                    proceed = false;
                }

                if (proceed && !pressed) {
                    hideWarnings(3);
                    pressed = true;
                    performAdd(account_name, first_pw, (String) spinner.getSelectedItem());
                }
            }
        });

        confirmCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acConfirmPw.getText().clear();
                hideWarnings(1);
            }
        });

        passwordCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acPw.getText().clear();
                hideWarnings(0);
            }
        });

        accountCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideWarnings(2);
            }
        });

    }

    private boolean accountNameOk (String s) {
        if (s.isEmpty())
            return false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if ((c >= 0 && c <= 47) || (c >= 58 && c <= 64) || (c >= 91 && c <= 96) || (c >= 123 && c <= 255))
                return false;
        }
        return true;
    }

    private void getRebateList(){
        showProgressDialog();
        mIsError = false;
        MyAsyncTask<String> task2 = new MyAsyncTask<String>(this) {

            @Override
            public String callService() throws IOException,
                    JsonParseException, BizException, ServiceException {
                return agentService.getAvailableRebateList();
            }

            @Override
            public void onLoaded(String result) throws Exception {
                if(AddFriendActivity.this == null || AddFriendActivity.this.isFinishing())
                    return;
                if (!mIsError) {
                    JSONArray json = new JSONArray(result);
                    returnValues.clear();
                    for (int i = 0; i < json.length(); i++) {
                        //         if (Float.parseFloat(json.getString(i)) <= 7)
                        returnValues.add(rebates.toPercentage(json.getString(i)));
                    }
                    adapter.notifyDataSetChanged();
                    mHandler.sendEmptyMessage(0);
                }
                else {
                    BaseApp.changeUrl(AddFriendActivity.this, new BaseApp.OnChangeUrlListener() {
                        @Override
                        public void changeSuccess() {
                            getRebateList();
                        }

                        @Override
                        public void changeFail() {}
                    });
                }
            }
        };
        task2.setOnError(onError);
        task2.executeTask();
        getRebateQuotas();
    }

    private void getRebateQuotas() {
        mIsError = false;
        showing = false;
        MyAsyncTask<RebateUtil> task = new MyAsyncTask<RebateUtil>(this) {

            @Override
            public RebateUtil callService() throws IOException,
                    JsonParseException, BizException, ServiceException {
                return agentService.getRebateQuotas();
            }

            @Override
            public void onLoaded(RebateUtil result) throws Exception {
                if(AddFriendActivity.this == null || AddFriendActivity.this.isFinishing())
                    return;
                closeProgressDialog();
                if (!mIsError) {
                    quotas = result.getQuotas();
                    rebates = result;
                    mHandler.sendEmptyMessage(0);
                }
                else {
                    BaseApp.changeUrl(AddFriendActivity.this, new BaseApp.OnChangeUrlListener() {
                        @Override
                        public void changeSuccess() {
                            getRebateQuotas();
                        }

                        @Override
                        public void changeFail() {}
                    });
                }
            }
        };
        task.setOnError(onError);
        task.executeTask();
    }

    private void showWarnings (int mode) {
        if (mode == 0) {
            passwordWarningText.setText(R.string.valid_fund_password_empty_prompt);
            passwordCross.setVisibility(View.VISIBLE);
            passwordWarning.setVisibility(View.VISIBLE);
            passwordRedFilter.setVisibility(View.VISIBLE);
        }
        if (mode == 1 || mode == 3) {
            if (mode == 3) {
                confirmWarningText.setText(R.string.confirm_pw_empty);
            }
            else {
                confirmWarningText.setText(R.string.confirm_pw_fail);
                passwordWarningText.setText(R.string.confirm_pw_fail);
                passwordCross.setVisibility(View.VISIBLE);
                passwordWarning.setVisibility(View.VISIBLE);
                passwordRedFilter.setVisibility(View.VISIBLE);
                acPw.setTextColor(Color.parseColor("#E9516A"));
            }
            confirmCross.setVisibility(View.VISIBLE);
            confirmWarning.setVisibility(View.VISIBLE);
            confirmRedFilter.setVisibility(View.VISIBLE);
            acConfirmPw.setTextColor(Color.parseColor("#E9516A"));
        }
        if (mode == 2) {
            accountWarningText.setText(R.string.warning_empty_account);
            accountCross.setVisibility(View.VISIBLE);
            accountRedFilter.setVisibility(View.VISIBLE);
            accountWarning.setVisibility(View.VISIBLE);
        }

        if (mode == 4) {
            accountWarningText.setText(R.string.name_contains_special_char);
            accountCross.setVisibility(View.VISIBLE);
            accountRedFilter.setVisibility(View.VISIBLE);
            accountWarning.setVisibility(View.VISIBLE);
            acName.setTextColor(Color.parseColor("#E9516A"));
        }

        if(mode == 5){
            passwordWarningText.setText("密码包含非法字符");
            passwordCross.setVisibility(View.VISIBLE);
            passwordWarning.setVisibility(View.VISIBLE);
            passwordRedFilter.setVisibility(View.VISIBLE);
        }

        if(mode == 6){
            passwordWarningText.setText("请输入6到16个字符");
            passwordCross.setVisibility(View.VISIBLE);
            passwordWarning.setVisibility(View.VISIBLE);
            passwordRedFilter.setVisibility(View.VISIBLE);
        }
        if(mode == 7){
            confirmWarningText.setText("请输入6到16个字符");
            confirmCross.setVisibility(View.VISIBLE);
            confirmWarning.setVisibility(View.VISIBLE);
            confirmRedFilter.setVisibility(View.VISIBLE);
        }
    }

    private void hideWarnings(int mode) {
        if (mode == 0 || mode == 3) {
            passwordCross.setVisibility(View.GONE);
            passwordWarning.setVisibility(View.GONE);
            passwordRedFilter.setVisibility(View.INVISIBLE);
            acPw.setTextColor(Color.BLACK);
        }
        if (mode == 1 || mode == 3) {
            confirmCross.setVisibility(View.GONE);
            confirmWarning.setVisibility(View.GONE);
            confirmRedFilter.setVisibility(View.INVISIBLE);
            acConfirmPw.setTextColor(Color.BLACK);
        }
        if (mode == 2 ||mode == 3) {
            accountCross.setVisibility(View.GONE);
            accountRedFilter.setVisibility(View.INVISIBLE);
            accountWarning.setVisibility(View.GONE);
            acName.setTextColor(Color.BLACK);
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage (Message msg) {
            super.handleMessage(msg);
            closeProgressDialog();
            //Update UI
            if (msg.what == 0) {
                count++;
                if (count >= 2) {
                    String selection = rebates.fromPercentage((String) spinner.getSelectedItem());
                    tvQuotas.setText(getResources().getString(R.string.quotas) + rebates.getQuota(selection));
                    loaded = true;
                }
            } else if (msg.what == 1) {
                findViewById(R.id.add_account_name_ok).setVisibility(View.GONE);
                findViewById(R.id.add_account_green_gilter).setVisibility(View.INVISIBLE);
            } else if (msg.what == ON_ERROR) {
                String msgText = msg.getData().getString("Error");
                if (!showing || !prevMessage.equals(msgText)) {
                    prevMessage = msgText;
                    showing = true;
                    MyToast.show(getBaseContext(), msgText);
                }
            }
        }
    };

    private void performAdd(final String name, final String pw, final String rebate) {
        showProgressDialog();
        mIsError = false;
        MyAsyncTask<ServiceRequestResult> task = new MyAsyncTask<ServiceRequestResult>(this) {

            @Override
            public ServiceRequestResult callService() throws IOException,
                    JsonParseException, BizException, ServiceException {
                if (rebate != null)
                    return agentService.addMember(name, pw, rebates.fromPercentage(rebate));
                else
                    return agentService.addMember(name, pw, rebate);
            }

            @Override
            public void onLoaded(ServiceRequestResult result) throws Exception {
                if(AddFriendActivity.this == null || AddFriendActivity.this.isFinishing())
                    return;
                closeProgressDialog();
                if (!mIsError) {
                    if (result.isSuccess()) {
                        startSuccessActivity(name, pw);
                    } else {
                        MyToast.show(getBaseContext(), result.getMessage());
                        mHandler.sendEmptyMessage(1);
                    }
                }
                else {
                    BaseApp.changeUrl(AddFriendActivity.this, new BaseApp.OnChangeUrlListener() {
                        @Override
                        public void changeSuccess() {
                            performAdd(name, pw, rebate);
                        }

                        @Override
                        public void changeFail() {}
                    });
                }
                pressed = false;
            }
        };
        task.setOnError(onError);
        task.executeTask();
    }


    private void showProgressDialog() {
        try {
            mProgressDialog = DialogUtil.getProgressDialog(this, getResources().getString(R.string.msg_load_ing));
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
    private void startSuccessActivity(String name, String pw) {
        Intent it = new Intent();
        it.setClass(AddFriendActivity.this, SuccessActivity.class);
        it.putExtra("name", name);
        it.putExtra("password", pw);
        it.putExtra("tag", 4);
        setResult(RESULT_OK);
        startActivity(it);
        finish();
    }

    public void backClick (View v){
        finish();
    }
}
