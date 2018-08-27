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
import android.util.Log;
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
import com.hec.app.customer_service.CustomerServiceFragment;
import com.hec.app.entity.MessagePushCZContentInfo;
import com.hec.app.entity.MessagePushInfo;
import com.hec.app.entity.MessagePushKJContentInfo;
import com.hec.app.entity.MessagePushTXContentInfo;
import com.hec.app.entity.MessagePushTransferNoticeContentInfo;
import com.hec.app.entity.MessageReceiveInfo;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by jhezenhu on 2017/7/12.
 */

public class ChatMessageGetTask extends AsyncTask<Void, String, Void> {
    private final String HECBET_VIP_MESSAGE = "HECBET_VIP_MESSAGE";
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
    private CustomerServiceFragment.OnMessageReceiveListener listener;

    public ChatMessageGetTask(Context context, String userId, CustomerServiceFragment.OnMessageReceiveListener listener) {
        mContext = context;
        mUserId = userId;
        this.listener = listener;
    }

    @Override
    protected Void doInBackground(Void... urls) {
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setUri(URLPickingUtil.getInstance().getVipMqUrl());
            mConnection = factory.newConnection();
            mChannel = mConnection.createChannel();
            mChannel.exchangeDeclare(HECBET_VIP_MESSAGE,
                    BuiltinExchangeType.DIRECT,
                    true,
                    false,
                    null);
            mQueue = mChannel.queueDeclare().getQueue();
            mChannel.queueBind(mQueue, HECBET_VIP_MESSAGE, mUserId);
            mChannel.basicConsume(mQueue, true, new DefaultConsumer(mChannel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String message = new String(body, "UTF-8");
                    if (!message.isEmpty()) {
                        publishProgress(message);
                    }
                }
            });
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
        Gson gson = new Gson();
        MessageReceiveInfo info = gson.fromJson(body, MessageReceiveInfo.class);
        if (info != null && listener != null && info.getSendContent() != null) {
            MessageReceiveInfo.ContentInfoRawData rawData = gson.fromJson(info.getSendContent(), MessageReceiveInfo.ContentInfoRawData.class);
            listener.onMessageReceive(rawData);
        }
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
}
