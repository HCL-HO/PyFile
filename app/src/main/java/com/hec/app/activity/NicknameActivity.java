package com.hec.app.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.gson.JsonParseException;
import com.hec.app.R;
import com.hec.app.activity.base.BaseActivity;
import com.hec.app.config.CommonConfig;
import com.hec.app.entity.BizException;
import com.hec.app.entity.NicknameInfo;
import com.hec.app.entity.Response;
import com.hec.app.framework.widget.MyToast;
import com.hec.app.util.IntentUtil;
import com.hec.app.util.MyAsyncTask;
import com.hec.app.webservice.AccountService;
import com.hec.app.webservice.ServiceException;

import java.io.IOException;

public class NicknameActivity extends BaseActivity {

    private EditText mEtNickname;
    private boolean mIsError = false;
    private String mTag = "";
    private String mNickname = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nickname);

        mTag = getIntent().getStringExtra(CommonConfig.INTENT_NICKNAME_TAG);

        ImageView imgBack = (ImageView) findViewById(R.id.imgBack);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mEtNickname = (EditText) findViewById(R.id.et_nickname);
        Button btnChangeNickname = (Button) findViewById(R.id.btn_change_nickname);
        btnChangeNickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateNickname(mEtNickname.getText().toString());
            }
        });

        getNickname();
    }

    private void getNickname(){
        mIsError = false;
        MyAsyncTask<NicknameInfo> task = new MyAsyncTask<NicknameInfo>(NicknameActivity.this) {
            @Override
            public NicknameInfo callService() throws IOException, JsonParseException, BizException, ServiceException {
                return new AccountService().getNicknameInfo();
            }

            @Override
            public void onLoaded(NicknameInfo result) throws Exception {
                if (!mIsError) {
                    if (result.getNickname() != null) {
                        mNickname = result.getNickname();
                        if (mTag.equals(CommonConfig.NICKNAME_KEY_ADD)) {
                            if (!mNickname.equals("")) {
                                IntentUtil.redirectToNextNewActivity(NicknameActivity.this, InfoCompleteActivity.class);
                                finish();
                            }
                        }
                        else if (mTag.equals(CommonConfig.NICKNAME_KEY_FIX)) {
                            mEtNickname.setText(mNickname);
                        }
                    }
                }
            }
        };
        task.executeTask();
        task.setOnError(new MyAsyncTask.OnError() {
            @Override
            public void handleError(Exception paramException) {
                mIsError = true;
            }
        });
    }

    private void updateNickname(final String nickname){
        mIsError = false;
        if (nickname.isEmpty()) {
            MyToast.show(NicknameActivity.this, getString(R.string.error_message_nickname_empty));
            return;
        }
        else if (nickname.equals(mNickname)) {
            return;
        }
        else if (nickname.length() > 15) {
            MyToast.show(NicknameActivity.this, getString(R.string.error_message_big_size));
            return;
        }

        MyAsyncTask<Response> task = new MyAsyncTask<Response>(NicknameActivity.this) {
            @Override
            public Response callService() throws IOException, JsonParseException, BizException, ServiceException {
                return new AccountService().updateNicknameInfo(nickname);
            }

            @Override
            public void onLoaded(Response result) throws Exception {
                if (!mIsError) {
                    if (result.getSuccess()) {
                        if (mTag.equals(CommonConfig.NICKNAME_KEY_ADD)) {
                            MyToast.show(NicknameActivity.this, getString(R.string.success_message_add));
                            IntentUtil.redirectToNextNewActivity(NicknameActivity.this, InfoCompleteActivity.class);
                            finish();
                        }
                        else if (mTag.equals(CommonConfig.NICKNAME_KEY_FIX)) {
                            MyToast.show(NicknameActivity.this, getString(R.string.success_message_fix));
                            finish();
                        }
                    }
                    else{
                        MyToast.show(NicknameActivity.this, result.getMessage());
                    }
                }
            }
        };
        task.executeTask();
        task.setOnError(new MyAsyncTask.OnError() {
            @Override
            public void handleError(Exception paramException) {
                mIsError = true;
            }
        });
    }
}
