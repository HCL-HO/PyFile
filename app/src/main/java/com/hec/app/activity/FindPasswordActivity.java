package com.hec.app.activity;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.JsonParseException;
import com.hec.app.R;
import com.hec.app.activity.base.BaseApp;
import com.hec.app.adapter.FindPasswordAdapter;
import com.hec.app.config.CommonConfig;
import com.hec.app.entity.BizException;
import com.hec.app.framework.widget.MyToast;
import com.hec.app.util.MyAsyncTask;
import com.hec.app.webservice.AccountService;
import com.hec.app.webservice.ServiceException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class FindPasswordActivity extends AppCompatActivity {
    private ImageView mImgBtnDelete;
    private LinearLayout mLlError;
    private TextView mTvErrorMsg;
    private RelativeLayout mRlEditField;
    private EditText mEtUserName;
    private boolean mIsError = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_password);

        ImageView imgBack = (ImageView)findViewById(R.id.imgBack);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mRlEditField = (RelativeLayout)findViewById(R.id.edit_field1);
        mLlError = (LinearLayout)findViewById(R.id.error);
        mTvErrorMsg = (TextView)findViewById(R.id.error_msg);

        mImgBtnDelete = (ImageView)findViewById(R.id.btn_delete);
        mImgBtnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEtUserName.setText("");
                setErrorView(false);
            }
        });

        mEtUserName = (EditText)findViewById(R.id.userNamepwd);
        mEtUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setErrorView(false);
            }
        });

        List<Map<String, Object>> list = getData();
        ListView lvFindPassword = (ListView)findViewById(R.id.findpassword_list);
        lvFindPassword.setAdapter(new FindPasswordAdapter(this, list));
        lvFindPassword.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                setErrorView(false);
                if (!mEtUserName.getText().toString().trim().isEmpty()) {
                    checkUserName(position);
                }
                else {
                    mTvErrorMsg.setText(R.string.userName_empty);
                    setErrorView(true);

                }
            }
        });
    }

    private void checkUserName(final int position){
        MyAsyncTask<com.hec.app.entity.Response> task = new MyAsyncTask<com.hec.app.entity.Response>(FindPasswordActivity.this) {
            @Override
            public com.hec.app.entity.Response callService() throws IOException, JsonParseException, BizException, ServiceException {
                return new AccountService().checkUserName(mEtUserName.getText().toString());
            }

            @Override
            public void onLoaded(com.hec.app.entity.Response data) throws Exception {
                if (FindPasswordActivity.this == null || FindPasswordActivity.this.isFinishing()) {
                    return;
                }

                if (!mIsError) {
                    if (data.getSuccess()) {
                        Bundle bundle = new Bundle();
                        if (position == CommonConfig.FIND_PASSWORD_EMAIL || position == CommonConfig.FIND_PASSWORD_ISSUE ||
                                position == CommonConfig.FIND_PASSWORD_MONEY_PWD || position == CommonConfig.FIND_PASSWORD_SMS) {
                            bundle.putInt(CommonConfig.BUNDLE_FIND_PASSWORD_POSITION, position);
                            bundle.putString(CommonConfig.BUNDLE_FIND_PASSWORD_USERNAME, mEtUserName.getText().toString().trim());
                        }

                        Intent it = new Intent(FindPasswordActivity.this, FindPasswordMethodActivity.class);
                        it.putExtras(bundle);
                        startActivity(it);
                        overridePendingTransition(R.anim.push_left_in_no_alpha, R.anim.push_left_out_no_alpha);
                    }
                    else {
                        mTvErrorMsg.setText(R.string.no_this_userName);
                        setErrorView(true);
                    }
                }
                else {
                    BaseApp.changeUrl(FindPasswordActivity.this, new BaseApp.OnChangeUrlListener() {
                        @Override
                        public void changeSuccess() {
                            checkUserName(position);
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
    public List<Map<String, Object>> getData(){
        List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
        addMap(list, R.mipmap.icon_valid_email, getString(R.string.find_password_item1), getString(R.string.find_password_info1));
        addMap(list, R.mipmap.icon_valid_issue, getString(R.string.find_password_item2), getString(R.string.find_password_info2));
        addMap(list, R.mipmap.icon_fund_password, getString(R.string.find_password_item3), getString(R.string.find_password_info3));
        // FIXME: 2017/9/6  簡訊找回密碼
        addMap(list, R.mipmap.icon_fund_sms, getString(R.string.find_password_item4), getString(R.string.find_password_info4));
        return list;
    }

    private void addMap(List<Map<String, Object>> list, int imgResourse, String title, String info){
        Map<String, Object>map = new HashMap<String, Object>();
        map.put(CommonConfig.MAP_FIND_PASSWORD_IMAGE, imgResourse);
        map.put(CommonConfig.MAP_FIND_PASSWORD_TITLE, title);
        map.put(CommonConfig.MAP_FIND_PASSWORD_INFO, info);
        list.add(map);
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        overridePendingTransition(R.anim.push_right_in_no_alpha, R.anim.push_right_out_no_alpha);
    }

    private void setErrorView(boolean isOpen){
        if (isOpen) {
            mRlEditField.setBackgroundResource(R.drawable.rect_no_round);
            mEtUserName.setTextColor(Color.RED);
            mLlError.setVisibility(View.VISIBLE);
            mImgBtnDelete.setVisibility(View.VISIBLE);
        }
        else {
            mRlEditField.setBackgroundResource(R.color.light_gray);
            mEtUserName.setTextColor(Color.BLACK);
            mLlError.setVisibility(View.GONE);
            mImgBtnDelete.setVisibility(View.GONE);
        }
    }
}
