package com.hec.app.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.hec.app.R;
import com.hec.app.activity.base.BaseActivityWithMenu;
import com.hec.app.entity.BizException;
import com.hec.app.entity.OfflineTransferInfo;
import com.hec.app.entity.Result;
import com.hec.app.entity.ServiceRequestResult;
import com.hec.app.fragment.OfflinePhoneVerifyFragment;
import com.hec.app.fragment.OfflineTransferFragment;
import com.hec.app.util.ConstantProvider;
import com.hec.app.util.DialogUtil;
import com.hec.app.util.MyAsyncTask;
import com.hec.app.util.OfflineTransferListener;
import com.hec.app.webservice.AgentService;
import com.hec.app.webservice.ServiceException;

import java.io.IOException;

import static com.hec.app.util.ConstantProvider.OFFLINE_TRANSFER_BACK_RESULT_CODE;
import static com.hec.app.util.ConstantProvider.OFFLINE_TRANSFER_FINISH_RESULT_CODE;

public class OfflineTransferActivity extends BaseActivityWithMenu implements OfflineTransferListener, View.OnClickListener {
    private AgentService agentService = new AgentService();
    private ProgressDialog mProgressDialog;
    private boolean mIsError = false;
    private ImageView backImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_transfer);

        backImg = (ImageView) findViewById(R.id.imgBack);
        backImg.setOnClickListener(this);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.offline_container, new OfflineTransferFragment()).commit();
    }

    @Override
    public void onClick(View v) {
        Intent data = new Intent();
        setResult(OFFLINE_TRANSFER_BACK_RESULT_CODE, data);
        finish();
    }

    @Override
    public void onConfirmTransfer(String userName, String userPwd, String transferAmount) {
        performTransfer(userName, userPwd, transferAmount, false, "", false);
    }

    @Override
    public void onSendCaptcha() {
        sendCaptcha();
    }

    @Override
    public void onTransferComplete(String userName, String userPwd, String transferAmount, boolean hasSMS, String captcha, boolean check30Min) {
        performTransfer(userName, userPwd, transferAmount, hasSMS, captcha, check30Min);
    }

    private void performTransferComplete(final String name, final String pw, final String amount) {
        showProgressDialog();
        mIsError = false;
        MyAsyncTask<ServiceRequestResult> task = new MyAsyncTask<ServiceRequestResult>(this) {

            @Override
            public ServiceRequestResult callService() throws IOException, JsonParseException, BizException, ServiceException {
                return agentService.transferMoney(name, pw, amount);
            }

            @Override
            public void onLoaded(ServiceRequestResult result) throws Exception {
                closeProgressDialog();
                if (OfflineTransferActivity.this == null || OfflineTransferActivity.this.isFinishing())
                    return;
                if (!mIsError && result.isSuccess()) {
                    closeProgressDialog();
                    DialogUtil.getAlertDialog(OfflineTransferActivity.this, getString(R.string.friendly_reminder), getString(R.string.offline_transfer_success), getString(R.string.confirm_send), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            OfflineTransferActivity.this.finish();
                        }
                    }, "", null).show();
                } else {
                    DialogUtil.getAlertDialog(OfflineTransferActivity.this, getString(R.string.friendly_reminder), result.getMessage(), getString(R.string.confirm_send), null, "", null).show();
                }
            }
        };
        task.setOnError(new MyAsyncTask.OnError() {
            @Override
            public void handleError(Exception paramException) {
                closeProgressDialog();
                mIsError = true;
                DialogUtil.getAlertDialog(OfflineTransferActivity.this, getString(R.string.friendly_reminder), paramException.toString(), getString(R.string.confirm_send), null, "", null).show();
            }
        });
        task.executeTask();
    }

    private void sendCaptcha() {
        showProgressDialog();
        mIsError = false;
        MyAsyncTask<Result<String>> task = new MyAsyncTask<Result<String>>(this) {

            @Override
            public Result<String> callService() throws IOException, JsonParseException, BizException, ServiceException {
                return agentService.sendCaptcha();
            }

            @Override
            public void onLoaded(Result<String> result) throws Exception {
                closeProgressDialog();
                if (OfflineTransferActivity.this == null || OfflineTransferActivity.this.isFinishing())
                    return;
                if (!mIsError && result.isSuccess()) {
                    closeProgressDialog();
                    Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.offline_container);
                    if (fragment != null && fragment instanceof OfflinePhoneVerifyFragment) {
                        ((OfflinePhoneVerifyFragment) fragment).setHasCaptcha(true);
                    }
                } else {
                    DialogUtil.getAlertDialog(OfflineTransferActivity.this, getString(R.string.friendly_reminder), result.getMessage(), getString(R.string.confirm_send), null, "", null).show();
                }
            }
        };
        task.setOnError(new MyAsyncTask.OnError() {
            @Override
            public void handleError(Exception paramException) {
                closeProgressDialog();
                mIsError = true;
                DialogUtil.getAlertDialog(OfflineTransferActivity.this, getString(R.string.friendly_reminder), paramException.toString(), getString(R.string.confirm_send), null, "", null).show();
            }
        });
        task.executeTask();
    }

    private void performTransfer(final String name, final String pw, final String amount, final boolean hasSMS, final String captcha, final boolean check30Min) {
        showProgressDialog();
        mIsError = false;
        MyAsyncTask<Result<OfflineTransferInfo>> task = new MyAsyncTask<Result<OfflineTransferInfo>>(this) {

            @Override
            public Result<OfflineTransferInfo> callService() throws IOException, JsonParseException, BizException, ServiceException {
                return agentService.transferMoneyWithCaptcha(name, pw, amount, hasSMS, captcha, check30Min);
            }

            @Override
            public void onLoaded(Result<OfflineTransferInfo> result) throws Exception {
                closeProgressDialog();
                if (OfflineTransferActivity.this == null || OfflineTransferActivity.this.isFinishing())
                    return;
                if (!mIsError && result.isSuccess() && result.getDataObj() != null) {
                    closeProgressDialog();
                    switch (result.getDataObj().getStep()) {
                        case "2":
                                Bundle bundle = new Bundle();
                                bundle.putString("phoneNum", result.getDataObj().getPhone());
                                bundle.putString("name", name);
                                bundle.putString("pw", pw);
                                bundle.putString("amount", amount);
                                OfflinePhoneVerifyFragment fragment = new OfflinePhoneVerifyFragment();
                                fragment.setArguments(bundle);
                                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                                ft.replace(R.id.offline_container, fragment).commit();
                            break;

                        case "3":
                            performTransferComplete(name, pw, amount);
                            break;

                        case "1":
                        default:
                            break;
                    }
                } else {
                    DialogUtil.getAlertDialog(OfflineTransferActivity.this, getString(R.string.friendly_reminder), result.getMessage(), getString(R.string.confirm_send), null, "", null).show();
                }
            }
        };
        task.setOnError(new MyAsyncTask.OnError() {
            @Override
            public void handleError(Exception paramException) {
                closeProgressDialog();
                mIsError = true;
                DialogUtil.getAlertDialog(OfflineTransferActivity.this, getString(R.string.friendly_reminder), paramException.toString(), getString(R.string.confirm_send), null, "", null).show();
            }
        });
        task.executeTask();
    }

    private void showProgressDialog() {
        try {
            mProgressDialog = DialogUtil.getProgressDialog(this, getResources().getString(R.string.msg_load_ing));
            mProgressDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void closeProgressDialog() {
        try {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ConstantProvider.CUSTOMER_SERVICE_REQUEST_CODE) {
            Intent intent = new Intent();
            setResult(OFFLINE_TRANSFER_FINISH_RESULT_CODE, intent);
            finish();
        }
    }
}