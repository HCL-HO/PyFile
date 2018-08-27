package com.hec.app.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.gson.JsonParseException;
import com.hec.app.R;
import com.hec.app.activity.base.BaseActivityWithMenu;
import com.hec.app.entity.AliPayNewInfo;
import com.hec.app.entity.BizException;
import com.hec.app.entity.WechatPayResponse;
import com.hec.app.fragment.WechatRechargeWayFragment;
import com.hec.app.fragment.WechatTransferFragment;
import com.hec.app.fragment.WechatTransferSuccessFragment;
import com.hec.app.util.DialogUtil;
import com.hec.app.util.IntentUtil;
import com.hec.app.util.MyAsyncTask;
import com.hec.app.util.WechatRechargeListener;
import com.hec.app.webservice.RechargeService;
import com.hec.app.webservice.ServiceException;

import java.io.IOException;

public class RechargeWechatActivity extends FragmentActivity implements View.OnClickListener, WechatRechargeListener {
    private ImageView backBtn;
    private boolean mIsError = false;
    private RechargeService rechargeService = new RechargeService();
    private boolean[] itemShowArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge_wechat);

        backBtn = (ImageView) findViewById(R.id.back_btn);
        backBtn.setOnClickListener(this);
        itemShowArray = getIntent().getBooleanArrayExtra("itemShowArray");

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        WechatRechargeWayFragment f = new WechatRechargeWayFragment();
        f.setItemShowArray(itemShowArray);
        ft.add(R.id.content_container, f);
        ft.commit();
    }

    @Override
    public void onWechatRecharged(String userName, String amount) {
        submitRecharge(userName, amount);
    }

    @Override
    public void onWechatRechargeFinished() {
        finish();
        IntentUtil.redirectToNextActivity(this, MoneyActivity.class);
    }

    @Override
    public void onWechatRechargeTimesUp() {
        finish();
    }

    private void submitRecharge(final String name, final String amount) {
        mIsError = false;
        final Dialog dialog = DialogUtil.getProgressDialog(RechargeWechatActivity.this, "正在提交！");
        dialog.show();
        final MyAsyncTask<WechatPayResponse> task = new MyAsyncTask<WechatPayResponse>(this) {
            @Override
            public WechatPayResponse callService() throws IOException, JsonParseException, BizException, ServiceException {
                return rechargeService.submitWechatTransfer(name, amount);
            }

            @Override
            public void onLoaded(WechatPayResponse paramT) throws Exception {
                dialog.dismiss();
                if (!mIsError && paramT != null) {
                    if (!paramT.getSuccess().isEmpty() && paramT.getSuccess().equals("true")) {
                        boolean transferSuccess = false;
                        try {
                            Float.valueOf(paramT.getMessage());
                            transferSuccess = true;
                        } catch (NumberFormatException e) {
                            transferSuccess = false;
                        }
                        if (transferSuccess) {
                            Bundle bundle = new Bundle();
                            bundle.putString("bankUser", paramT.getResult().getAdminBankBankUser().isEmpty() ? "" : paramT.getResult().getAdminBankBankUser());
                            bundle.putString("bankCard", paramT.getResult().getAdminBankBankCard().isEmpty() ? "" : paramT.getResult().getAdminBankBankCard());
                            bundle.putString("bankType", paramT.getResult().getBankTypeName().isEmpty() ? "" : paramT.getResult().getBankTypeName());
                            bundle.putString("amount", paramT.getMessage().isEmpty() ? "0.00" : paramT.getMessage());
                            WechatTransferSuccessFragment fragment = new WechatTransferSuccessFragment();
                            fragment.setArguments(bundle);
                            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                            ft.replace(R.id.content_container, fragment);
                            ft.commit();
                        } else {
                            DialogUtil.getAlertDialog(RechargeWechatActivity.this, getResources().getString(R.string.friendly_reminder),
                                    paramT.getMessage(), getResources().getString(R.string.dialog_determine),
                                    null, "", null).show();
                        }
                    } else {
                        DialogUtil.getAlertDialog(RechargeWechatActivity.this, getResources().getString(R.string.friendly_reminder),
                                paramT.getMessage(), getResources().getString(R.string.dialog_determine),
                                null, "", null).show();
                    }
                }
            }
        };
        task.executeTask();
        task.setOnError(new MyAsyncTask.OnError() {
            @Override
            public void handleError(Exception paramException) {
                mIsError = true;
                dialog.dismiss();
                DialogUtil.getErrorAlertDialog(RechargeWechatActivity.this, paramException.getMessage()).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content_container);
        if (fragment != null) {
            if (fragment instanceof WechatTransferFragment) {
                WechatRechargeWayFragment f = new WechatRechargeWayFragment();
                f.setItemShowArray(itemShowArray);
                getSupportFragmentManager().beginTransaction().replace(R.id.content_container, f).commit();
            } else if (fragment instanceof WechatTransferSuccessFragment) {
                finish();
            } else if (fragment instanceof WechatRechargeWayFragment) {
                finish();
            }
        }
    }
}
