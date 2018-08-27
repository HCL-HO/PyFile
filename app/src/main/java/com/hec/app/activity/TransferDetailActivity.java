package com.hec.app.activity;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonParseException;
import com.hec.app.R;
import com.hec.app.activity.base.BaseActivity;
import com.hec.app.config.CommonConfig;
import com.hec.app.entity.BizException;
import com.hec.app.entity.Response;
import com.hec.app.framework.widget.MyToast;
import com.hec.app.util.DialogUtil;
import com.hec.app.util.MyAsyncTask;
import com.hec.app.util.SystemBarTintManager;
import com.hec.app.webservice.AccountService;
import com.hec.app.webservice.ServiceException;

import java.io.IOException;

public class TransferDetailActivity extends BaseActivity {
    private EditText mEtTransfeIn;
    private EditText mEtTransferOut;
    private boolean mIserror;
    private String mPlaytype = "";
    private String mAmount = "";
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_detail);
        mPlaytype = getIntent().getStringExtra(CommonConfig.INTENT_TRANSFER_PLAYTYPE);

        ImageView imgBack = (ImageView) findViewById(R.id.imgBack);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.colorPrimary);
        }

        initView(mPlaytype);
    }

    private void initView(String playtype) {
        TextView txPlaytype = (TextView) findViewById(R.id.transfer_playtype);
        TextView txAvailableTitle = (TextView) findViewById(R.id.record_header_label1);
        TextView txTransferOutTitle = (TextView) findViewById(R.id.transfer_text_out);
        TextView txTransferInTitle = (TextView) findViewById(R.id.transfer_text_in);
        TextView txAvailableScores = (TextView) findViewById(R.id.transfer_detail_head_value1);
        ImageView imgLogo = (ImageView) findViewById(R.id.transfer_detail_image);
        ImageView imgTransferOut = (ImageView) findViewById(R.id.transfer_button1);
        ImageView imgTransferIn = (ImageView) findViewById(R.id.transfer_button2);

        mEtTransfeIn = (EditText) findViewById(R.id.transfer_moneysum_edittext2);
        mEtTransferOut = (EditText) findViewById(R.id.transfer_moneysum_edittext1);

        if (getIntent().getStringExtra(CommonConfig.INTENT_TRANSFER_AVALIBALE) != null) {
            txAvailableScores.setText(getIntent().getStringExtra(CommonConfig.INTENT_TRANSFER_AVALIBALE));
        }

        if (playtype.equals(CommonConfig.TRANSFER_PLAYTYPE_REALMAN)) {
        }
        else if (playtype.equals(CommonConfig.TRANSFER_PLAYTYPE_PT)) {
            txPlaytype.setText(R.string.activity_transderdetail_pt_playtype);
            txAvailableTitle.setText(R.string.activity_transderdetail_pt_available);
            txTransferOutTitle.setText(R.string.activity_transderdetail_pt_out);
            txTransferInTitle.setText(R.string.activity_transderdetail_pt_in);
            imgLogo.setImageResource(R.mipmap.transfer_pt);
        }
        else if (playtype.equals(CommonConfig.TRANSFER_PLAYTYPE_SPORTS)) {
            txPlaytype.setText(R.string.activity_transderdetail_sports_playtype);
            txAvailableTitle.setText(R.string.activity_transderdetail_sports_available);
            txTransferOutTitle.setText(R.string.activity_transderdetail_sports_out);
            txTransferInTitle.setText(R.string.activity_transderdetail_sports_in);
            imgLogo.setImageResource(R.mipmap.transfer_sports);
        }

        imgTransferIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String scores = mEtTransfeIn.getText().toString();
                if (scores.isEmpty() || Integer.parseInt(scores) == 0) {
                    MyToast.show(TransferDetailActivity.this, getString(R.string.error_message_transder_amount));
                    return;
                }

                //TODO:检查平台账户是否有足够资金转入.
                createDialog(CommonConfig.TRANSFER_IN);
        }
        });
        imgTransferOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String scores = mEtTransferOut.getText().toString();
                if (scores.isEmpty() || Integer.parseInt(scores) == 0) {
                    MyToast.show(TransferDetailActivity.this, getString(R.string.error_message_transder_amount));
                    return;
                }

                //TODO:检查该娱乐账户是否有足够资金转出.
                createDialog(CommonConfig.TRANSFER_OUT);
            }
        });
    }

    private void createDialog(final String tag) {
        final Dialog resultbox = new Dialog(TransferDetailActivity.this);
        resultbox.requestWindowFeature(Window.FEATURE_NO_TITLE);
        resultbox.setCancelable(false);
        resultbox.setContentView(R.layout.transfer_dialog);

        TextView txPlaytype = (TextView) resultbox.findViewById(R.id.transfer_dialog_type);
        if(mPlaytype.equals(CommonConfig.TRANSFER_PLAYTYPE_REALMAN)){
        }
        else if(mPlaytype.equals(CommonConfig.TRANSFER_PLAYTYPE_PT)){
            txPlaytype.setText(R.string.dialog_transder_pt_playtype);
        }
        else if(mPlaytype.equals(CommonConfig.TRANSFER_PLAYTYPE_SPORTS)){
            txPlaytype.setText(R.string.dialog_transder_sports_playtype);
        }

        TextView txPlayAmount = (TextView) resultbox.findViewById(R.id.transfer_dialog_amount);
        if (tag.equals(CommonConfig.TRANSFER_IN)) {
            mAmount = mEtTransfeIn.getText().toString();
            txPlayAmount.setText(String.format(getString(R.string.dialog_transder_play_amount), mAmount));

        }
        else if (tag.equals(CommonConfig.TRANSFER_OUT)) {
            mAmount = mEtTransferOut.getText().toString();
            txPlayAmount.setText(String.format(getString(R.string.dialog_transder_play_amount), mAmount));
        }

        if(!mAmount.contains(".")){
            mAmount = mAmount + ".0";
        }

        LinearLayout llYes = (LinearLayout) resultbox.findViewById(R.id.transfer_yes);
        LinearLayout llNo = (LinearLayout) resultbox.findViewById(R.id.transfer_no);
        llYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double finalAmount = Double.valueOf(mAmount) / 1.00;
                double hehe = Math.round(finalAmount * 100) / 100.00;
                //TODO,maybe we have to change here.
                doTransfer(tag, mPlaytype, hehe);
                resultbox.dismiss();
            }
        });
        llNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resultbox.dismiss();
            }
        });
        resultbox.show();
    }

    private void doTransfer(final String tag, final String playtype, final double amount) {
        mIserror = false;
        showProgressDialog(getString(R.string.loading_message_transder));
        MyAsyncTask<Response> task = new MyAsyncTask<Response>(this) {
            @Override
            public Response callService() throws IOException, JsonParseException, BizException, ServiceException {
                return new AccountService().doTransfer(tag, playtype, amount);
            }

            @Override
            public void onLoaded(Response result) throws Exception {
                closeProgressDialog();
                if (!mIserror) {
                    if (result.getSuccess()) {
                        MyToast.show(TransferDetailActivity.this, getString(R.string.success_message_transder));
                        Intent intent = new Intent(TransferDetailActivity.this, SuccessActivity.class);
                        intent.putExtra(CommonConfig.INTENT_SUCCESS_TAG, 7);
                        startActivity(intent);
                        finish();
                    }
                    else {
                        if (result.getMessage() != null) {
                            MyToast.show(TransferDetailActivity.this, result.getMessage());
                        }
                    }
                } else {
                    MyToast.show(TransferDetailActivity.this,getErrorMessage());
                }
            }
        };
        task.setOnError(new MyAsyncTask.OnError() {
            @Override
            public void handleError(Exception paramException) {
                mIserror = true;
            }
        });
        task.executeTask();
    }

    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        }
        else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    private void showProgressDialog(String loadingMessage){
        try {
            mProgressDialog = DialogUtil.getProgressDialog(this, loadingMessage);
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
