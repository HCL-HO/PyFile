package com.hec.app.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hec.app.BuildConfig;
import com.hec.app.R;
import com.hec.app.activity.LoginActivity;
import com.hec.app.activity.MoneyActivity;
import com.hec.app.activity.PressToWinActivity;
import com.hec.app.activity.TransferActivity;
import com.hec.app.activity.base.BaseActivityWithMenu;
import com.hec.app.activity.base.BaseApp;
import com.hec.app.config.CommonConfig;
import com.hec.app.config.UrlConfig;
import com.hec.app.entity.MessagePushCZContentInfo;
import com.hec.app.entity.MessagePushInfo;
import com.hec.app.entity.MessagePushKJContentInfo;
import com.hec.app.entity.MessagePushTXContentInfo;
import com.hec.app.entity.MessagePushTransferNoticeContentInfo;
import com.hec.app.webservice.BaseService;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * Created by jhezenhu on 2017/7/12.
 */

public class MessagePushTask extends AsyncTask<Void, String, Void> {
    private final String HECBET_CLIENTS_WITH_WCF = "HECBET_CLIENTS_WITH_WCF";
    private final int TX = 0;
    private final int CZ = 1;
    private final int KJ = 2;
    private final int LEAVE_WHISPER = 3;
    private final int TRANSFER_NOTICE = 11;

    private Connection mConnection = null;
    private Channel mChannel = null;
    private Context mContext = null;
    private Dialog mDialog = null;
    private Dialog mLogoutDialog = null;
    private String mQueue = "";
    private String mUserId = "";
    private boolean mIsLeaveNotice = false;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (isDialogCorrect(mDialog)) {
                mDialog.cancel();
            }
        }
    };

    public MessagePushTask(Context context, String userId) {
        mContext = context;
        mUserId = userId;
    }

    @Override
    protected Void doInBackground(Void... urls) {
        try {
            String uri = BaseService.MQ_URL;
            if (BuildConfig.SIT || BuildConfig.UAT) {
                uri = UrlConfig.TEST_MQ_URL;
            }
            else if (uri.isEmpty()) {
                uri = UrlConfig.MQ_URL;
            }

            ConnectionFactory factory = new ConnectionFactory();
            factory.setUri(uri);
            mConnection = factory.newConnection();
            mChannel = mConnection.createChannel();
            mQueue = mChannel.queueDeclare("", false, false, true, null).getQueue();
            mChannel.basicConsume(mQueue, true, new DefaultConsumer(mChannel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String message = new String(body, "UTF-8");
                    if (!message.isEmpty()) {
                        publishProgress(message, envelope.getExchange());
                    }
                }
            });

            mChannel.exchangeDeclare(HECBET_CLIENTS_WITH_WCF, BuiltinExchangeType.DIRECT, true);
            mChannel.queueBind(mQueue, HECBET_CLIENTS_WITH_WCF, mUserId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onProgressUpdate(String... progress) {
        String body = progress[0];
        if (body.isEmpty()) {
            return;
        }

        String exchange = progress[1];
        if (exchange.isEmpty()) {
            return;
        }

        boolean isCreateDialog = false;
        if (mContext != BaseApp.getCurrentActivity()) {
            isCreateDialog = true;
        }
        mContext = BaseApp.getCurrentActivity();

        hecbetClientsWithWCF(body, isCreateDialog);
    }

    public void closeRabbitMQ() {
        try {
            if (mChannel != null) {
                mChannel.close();
            }

            if (mConnection != null) {
                mConnection.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void hecbetClientsWithWCF(String body, boolean isCreateDialog) {
        if (mIsLeaveNotice) {
            return;
        }

        Gson gson = new Gson();
        Type messageType = new TypeToken<MessagePushInfo>() {}.getType();
        MessagePushInfo response = gson.fromJson(body, messageType);

        switch (response.getMessageType()) {
            case TX:
                messageType = new TypeToken<MessagePushInfo<MessagePushTXContentInfo>>() {}.getType();
                MessagePushInfo<MessagePushTXContentInfo> txContent = gson.fromJson(body, messageType);

                showDialog(R.string.dialog_message_push_tx_title, txContent.getSendContent().getSummary(), isCreateDialog);
                updateAmount();
                break;
            case CZ:
                messageType = new TypeToken<MessagePushInfo<MessagePushCZContentInfo>>() {}.getType();
                MessagePushInfo<MessagePushCZContentInfo> czContent = gson.fromJson(body, messageType);

                showDialog(R.string.dialog_message_push_cz_title,
                        String.format(mContext.getResources().getString(R.string.dialog_message_push_cz), czContent.getSendContent().getAvailableScore()), isCreateDialog);
                updateAmount();
                break;
            case KJ:
                messageType = new TypeToken<MessagePushInfo<MessagePushKJContentInfo>>() {}.getType();
                MessagePushInfo<MessagePushKJContentInfo> kjContent = gson.fromJson(body, messageType);

                if (!kjContent.getSendContent().getSummary().contains("秒秒彩")) {
                    showDialog(R.string.dialog_message_push_kj_title, kjContent.getSendContent().getSummary().replaceFirst("，", "\n"), isCreateDialog);
                    updateAmount();
                }
                break;
            case LEAVE_WHISPER:
                mIsLeaveNotice = true;
                showLogoutDialog();
                break;
            case TRANSFER_NOTICE:
                messageType = new TypeToken<MessagePushInfo<MessagePushTransferNoticeContentInfo>>() {}.getType();
                MessagePushInfo<MessagePushTransferNoticeContentInfo> transferNoticeContent = gson.fromJson(body, messageType);

                showDialog(R.string.dialog_message_push_transfer_notice_title, transferNoticeContent.getSendContent().getSummary(), isCreateDialog);
                updateAmount();
                break;
        }
    }

    private void updateAmount() {
        if (mContext instanceof BaseActivityWithMenu) {
            BaseActivityWithMenu activity = (BaseActivityWithMenu) mContext;
            if (activity != null) {
                activity.getMenuHomeBalanceInfo();
            }
        }

        if (mContext instanceof TransferActivity) {
            TransferActivity activity = (TransferActivity) mContext;
            if (activity != null) {
                activity.getHomeBalanceInfo();
            }
        }
        else if (mContext instanceof MoneyActivity) {
            MoneyActivity activity = (MoneyActivity) mContext;
            if (activity != null) {
                activity.getMoneyFundInfo(activity.getType());
            }
        }
        else if (mContext instanceof PressToWinActivity) {
            PressToWinActivity activity = (PressToWinActivity) mContext;
            if (activity != null) {
                activity.getHomeBalanceInfo();
            }
        }
    }

    private void showDialog(int id, String message, boolean isCreateDialog) {
        if (isActivityFinishing(mContext)) {
            return;
        }
        mHandler.removeMessages(0);
        if (mDialog == null) {
            createDialog();
        } else if(isCreateDialog) {
            if (isDialogCorrect(mDialog) && !isActivityFinishing(mDialog.getContext())) {
                mDialog.dismiss();
            }
            createDialog();
        }

        TextView dialogTitle = (TextView)mDialog.findViewById(R.id.dialogTitle);
        dialogTitle.setText(id);

        TextView dialogMessage = (TextView)mDialog.findViewById(R.id.dialogMessage);
        dialogMessage.setText(message);
        dialogMessage.setLineSpacing(5.0f, 1.0f);

        if (!isActivityFinishing(mDialog.getContext())) {
            mDialog.show();
            mHandler.sendEmptyMessageDelayed(0, 3000);
        }
    }

    private void createDialog() {
        mDialog = new Dialog(mContext, R.style.custom_dialog_style);
        mDialog.setCanceledOnTouchOutside(true);
        mDialog.setCancelable(true);
        mDialog.setContentView(R.layout.forced_update_dialog);

        LinearLayout dialogBg = (LinearLayout)mDialog.findViewById(R.id.dialogBg);
        dialogBg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isDialogCorrect(mDialog)) {
                    mHandler.removeMessages(0);
                    mDialog.cancel();
                }
            }
        });

        Window dialogWindow = mDialog.getWindow();
        dialogWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Button determine = (Button)mDialog.findViewById(R.id.determine);
        determine.setVisibility(View.GONE);
    }

    private void showLogoutDialog() {
        if (isActivityFinishing(mContext)) {
            return;
        }

        mHandler.removeMessages(0);

        if (isDialogCorrect(mDialog)) {
            mDialog.dismiss();
        }
        if (isDialogCorrect(mLogoutDialog)) {
            mLogoutDialog.dismiss();
        }

        mLogoutDialog = new Dialog(mContext, R.style.custom_dialog_style);
        mLogoutDialog.setCanceledOnTouchOutside(false);
        mLogoutDialog.setCancelable(true);
        mLogoutDialog.setContentView(R.layout.forced_update_dialog);

        Window dialogWindow = mLogoutDialog.getWindow();
        dialogWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Button determine = (Button)mLogoutDialog.findViewById(R.id.determine);
        determine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isDialogCorrect(mLogoutDialog)) {
                    mLogoutDialog.dismiss();
                }
                logout();
            }
        });

        TextView dialogTitle = (TextView)mLogoutDialog.findViewById(R.id.dialogTitle);
        dialogTitle.setText(R.string.dialog_message_push_system_title);

        TextView dialogMessage = (TextView)mLogoutDialog.findViewById(R.id.dialogMessage);
        dialogMessage.setText(R.string.dialog_message_push_leave);
        dialogMessage.setLineSpacing(5.0f, 1.0f);

        mLogoutDialog.show();
    }

    private void logout() {
        if (BaseApp.rootActivity != null) {
            BaseApp.rootActivity.finish();
        }

        SharedPreferences token = mContext.getSharedPreferences(CommonConfig.KEY_TOKEN, Context.MODE_PRIVATE);
        token.edit().putString(CommonConfig.KEY_TOKEN_TOKENS, "").commit();
        Intent next = new Intent(mContext, LoginActivity.class);
        mContext.startActivity(next);
    }

    private boolean isActivityFinishing(Context context){
        if (context == null) {
            return true;
        }

        Activity activity = (Activity) mContext;
        if (activity == null) {
            return true;
        }

        return activity.isFinishing();
    }

    private boolean isDialogCorrect(Dialog dialog){
        return (dialog != null && !isActivityFinishing(dialog.getContext()) && dialog.isShowing());
    }
}
