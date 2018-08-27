package com.hec.app.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by chejenhu on 12/7/17.
 */
public class MessagePushInfo<T> {

    @SerializedName("durable")
    private boolean mDurable;
    @SerializedName("MessageDes")
    private String mMessageDes;
    @SerializedName("MessageFrom")
    private String mMessageFrom;
    @SerializedName("MessageStatus")
    private String mMessageStatus;
    @SerializedName("MessageType")
    private int mMessageType;
    @SerializedName("Queue")
    private String mQueue;
    @SerializedName("SendContent")
    private T mSendContent;
    @SerializedName("SendExchange")
    private String mSendExchange;
    @SerializedName("SendRoutKey")
    private String mSendRoutKey;
    @SerializedName("SendTime")
    private String mSendTime;
    @SerializedName("SendType")
    private String mSendType;

    public boolean getDurable() {
        return mDurable;
    }

    public void setDurable(boolean durable) {
        mDurable = durable;
    }

    public String getMessageDes() {
        return mMessageDes;
    }

    public void setMessageDes(String messageDes) {
        mMessageDes = messageDes;
    }

    public String getMessageFrom() {
        return mMessageFrom;
    }

    public void setMessageFrom(String messageFrom) {
        mMessageFrom = messageFrom;
    }

    public String getMessageStatus() {
        return mMessageStatus;
    }

    public void setMessageStatus(String messageStatus) {
        mMessageStatus = messageStatus;
    }

    public int getMessageType() {
        return mMessageType;
    }

    public void setMessageType(int messageType) {
        mMessageType = messageType;
    }

    public String getQueue() {
        return mQueue;
    }

    public void setQueue(String queue) {
        mQueue = queue;
    }

    public T getSendContent() {
        return mSendContent;
    }

    public void setSendContent(T sendContent) {
        mSendContent = sendContent;
    }

    public String getSendExchange() {
        return mSendExchange;
    }

    public void setSendExchange(String sendExchange) {
        mSendExchange = sendExchange;
    }

    public String getSendRoutKey() {
        return mSendRoutKey;
    }

    public void setSendRoutKey(String sendRoutKey) {
        mSendRoutKey = sendRoutKey;
    }

    public String getSendTime() {
        return mSendTime;
    }

    public void setSendTime(String sendTime) {
        mSendTime = sendTime;
    }

    public String getSendType() {
        return mSendType;
    }

    public void setSendType(String sendType) {
        mSendType = sendType;
    }
}
