package com.hec.app.activity;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonParseException;
import com.hec.app.R;
import com.hec.app.activity.base.BaseApp;
import com.hec.app.entity.BizException;
import com.hec.app.entity.ChangePwInfo;
import com.hec.app.entity.Response;
import com.hec.app.framework.widget.MyToast;
import com.hec.app.util.IntentUtil;
import com.hec.app.util.MyAsyncTask;
import com.hec.app.webservice.AccountService;
import com.hec.app.webservice.ServiceException;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SetNewPasswordActivity extends AppCompatActivity {
    private ImageView imgBack,btn_delete1, btn_delete2;
    private EditText pw1, pw2;
    private RelativeLayout btnSetNewPassword, edit_field1, edit_field2;
    private String validEmail;
    private LinearLayout error1, error2;
    private TextView error_msg1,error_msg2;
    private boolean mIsError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_new_password);
        imgBack = (ImageView)findViewById(R.id.imgBack);
        edit_field1 = (RelativeLayout)findViewById(R.id.edit_field1);
        edit_field2 = (RelativeLayout)findViewById(R.id.edit_field2);
        error1 = (LinearLayout)findViewById(R.id.error1);
        error2 = (LinearLayout)findViewById(R.id.error2);
        error_msg1 = (TextView)findViewById(R.id.error_msg1);
        error_msg2 = (TextView)findViewById(R.id.error_msg2);
        btn_delete1 = (ImageView)findViewById(R.id.btn_delete1);
        btn_delete2 = (ImageView)findViewById(R.id.btn_delete2);
        pw1 = (EditText)findViewById(R.id.pw1);
        pw2 = (EditText)findViewById(R.id.pw2);
        btnSetNewPassword = (RelativeLayout)findViewById(R.id.btnSetNewPassword);

        validEmail = "wj123@gmail.com";

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.push_right_in_no_alpha, R.anim.push_right_out_no_alpha);
            }
        });
        btn_delete1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_field1.setBackgroundResource(R.color.light_gray);
                pw1.setText("");
                pw1.setTextColor(Color.BLACK);
                error1.setVisibility(View.GONE);
                btn_delete1.setVisibility(View.GONE);
            }
        });
        btn_delete2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_field1.setBackgroundResource(R.color.light_gray);
                pw2.setText("");
                pw2.setTextColor(Color.BLACK);
                error1.setVisibility(View.GONE);
                btn_delete1.setVisibility(View.GONE);
            }
        });
        pw1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_field1.setBackgroundResource(R.color.light_gray);
                pw1.setTextColor(Color.BLACK);
                error1.setVisibility(View.GONE);
                btn_delete1.setVisibility(View.GONE);
            }
        });

        pw2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_field2.setBackgroundResource(R.color.light_gray);
                pw2.setTextColor(Color.BLACK);
                error2.setVisibility(View.GONE);
                btn_delete2.setVisibility(View.GONE);
            }
        });
        btnSetNewPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String passwordStr1 = pw1.getText().toString();
                String passwordStr2 = pw2.getText().toString();
                Pattern pattern = Pattern.compile("^[a-zA-Z0-9_]+$");
                Matcher matcher = pattern.matcher(passwordStr1);

                if (passwordStr1.trim().isEmpty() || passwordStr2.trim().isEmpty()) {
                    if (passwordStr1.trim().isEmpty()){
                        error1.setVisibility(View.VISIBLE);
                        error_msg1.setText("密码不能为空");
                        btn_delete1.setVisibility(View.VISIBLE);
                        edit_field1.setBackgroundResource(R.drawable.rect_no_round);
                    }
                    if(passwordStr2.trim().isEmpty()) {
                        error2.setVisibility(View.VISIBLE);
                        error_msg2.setText("密码不能为空");
                        btn_delete2.setVisibility(View.VISIBLE);
                        edit_field2.setBackgroundResource(R.drawable.rect_no_round);
                    }
                }  else if(!passwordStr1.equals(passwordStr2)) {
                    error1.setVisibility(View.VISIBLE);
                    error_msg1.setText(R.string.confirm_pw_fail);
                    btn_delete1.setVisibility(View.VISIBLE);
                    edit_field1.setBackgroundResource(R.drawable.rect_no_round);
                    error2.setVisibility(View.VISIBLE);
                    error_msg2.setText(R.string.confirm_pw_fail);
                    btn_delete2.setVisibility(View.VISIBLE);
                    edit_field2.setBackgroundResource(R.drawable.rect_no_round);
                }  else if(passwordStr1.length() < 6 || passwordStr1.length() > 16) {
                    error1.setVisibility(View.VISIBLE);
                    error_msg1.setText("请输入6到16个字符");
                    btn_delete1.setVisibility(View.VISIBLE);
                    edit_field1.setBackgroundResource(R.drawable.rect_no_round);
                    error2.setVisibility(View.VISIBLE);
                    error_msg2.setText("请输入6到16个字符");
                    btn_delete2.setVisibility(View.VISIBLE);
                    edit_field2.setBackgroundResource(R.drawable.rect_no_round);
                }else if(!matcher.matches()) {
                    error1.setVisibility(View.VISIBLE);
                    error_msg1.setText("密码包含非法字符");
                    btn_delete1.setVisibility(View.VISIBLE);
                    edit_field1.setBackgroundResource(R.drawable.rect_no_round);
                    error2.setVisibility(View.VISIBLE);
                    error_msg2.setText("密码包含非法字符");
                    btn_delete2.setVisibility(View.VISIBLE);
                    edit_field2.setBackgroundResource(R.drawable.rect_no_round);
                } else if (BaseApp.isPasswordEasy(passwordStr1) || BaseApp.isPasswordEasy(passwordStr2)) {
                    error1.setVisibility(View.VISIBLE);
                    error_msg1.setText(R.string.error_message_login_pw_stupid);
                    btn_delete1.setVisibility(View.VISIBLE);
                    edit_field1.setBackgroundResource(R.drawable.rect_no_round);
                    error2.setVisibility(View.VISIBLE);
                    error_msg2.setText(R.string.error_message_login_pw_stupid);
                    btn_delete2.setVisibility(View.VISIBLE);
                    edit_field2.setBackgroundResource(R.drawable.rect_no_round);
                }
                else{
                    changePwWithQuestion();
                }
                }
        });
    }

    private void changePwWithQuestion(){
        mIsError = false;
        MyAsyncTask<Response> task = new MyAsyncTask<Response>(SetNewPasswordActivity.this) {
            @Override
            public Response callService() throws IOException, JsonParseException, BizException, ServiceException {
                return new AccountService().changePwWithQuestion(getIntent().getStringExtra("userName")
                        , pw1.getText().toString()
                        , getIntent().getIntExtra("Question1ID", 0)
                        , getIntent().getIntExtra("Question2ID", 0)
                        , getIntent().getStringExtra("Answer1")
                        , getIntent().getStringExtra("Answer2"));
            }

            @Override
            public void onLoaded(Response data) throws Exception {
                if (SetNewPasswordActivity.this == null || SetNewPasswordActivity.this.isFinishing())
                    return;
                if (!mIsError) {
                    if (data.getSuccess()) {
                        pw1.setTextColor(Color.BLACK);
                        edit_field1.setBackgroundResource(0);
                        error1.setVisibility(View.GONE);
                        btn_delete1.setVisibility(View.GONE);
                        pw2.setTextColor(Color.BLACK);
                        edit_field2.setBackgroundResource(0);
                        error2.setVisibility(View.GONE);
                        btn_delete2.setVisibility(View.GONE);

                        Intent it = new Intent(SetNewPasswordActivity.this, SuccessActivity.class);
                        it.putExtra("newPassword", pw1.getText().toString().trim());
                        it.putExtra("tag", 6);
                        startActivity(it);
                        finish();
                        overridePendingTransition(R.anim.push_left_in_no_alpha, R.anim.push_left_out_no_alpha);
                    } else {
                        pw1.setTextColor(Color.RED);
                        edit_field1.setBackgroundResource(R.drawable.rect_no_round);
                        error_msg1.setText("error?");
                        error1.setVisibility(View.VISIBLE);
                        btn_delete1.setVisibility(View.VISIBLE);
                        pw2.setTextColor(Color.RED);
                        edit_field2.setBackgroundResource(R.drawable.rect_no_round);
                        error_msg2.setText("error?");
                        error2.setVisibility(View.VISIBLE);
                        btn_delete2.setVisibility(View.VISIBLE);
                    }
                }
                else {
                    BaseApp.changeUrl(SetNewPasswordActivity.this, new BaseApp.OnChangeUrlListener() {
                        @Override
                        public void changeSuccess() {
                            changePwWithQuestion();
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
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        overridePendingTransition(R.anim.push_right_in_no_alpha, R.anim.push_right_out_no_alpha);
    }
}
