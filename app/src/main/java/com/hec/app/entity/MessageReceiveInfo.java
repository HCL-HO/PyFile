package com.hec.app.entity;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class MessageReceiveInfo implements Serializable{
    @SerializedName("SendType")
    private String SendType;
    @SerializedName("SendExchange")
    private String SendExchange;
    @SerializedName("SendRoutKey")
    private String SendRoutKey;
    @SerializedName("SendContent")
    private String SendContent;
    @SerializedName("SendTime")
    private String SendTime;
    @SerializedName("MessageType")
    private int MessageType;

    public String getSendType() {
        return SendType;
    }

    public void setSendType(String sendType) {
        SendType = sendType;
    }

    public String getSendExchange() {
        return SendExchange;
    }

    public void setSendExchange(String sendExchange) {
        SendExchange = sendExchange;
    }

    public String getSendRoutKey() {
        return SendRoutKey;
    }

    public void setSendRoutKey(String sendRoutKey) {
        SendRoutKey = sendRoutKey;
    }

    public String getSendContent() {
        return SendContent;
    }

    public void setSendContent(String sendContent) {
        SendContent = sendContent;
    }

    public String getSendTime() {
        return SendTime;
    }

    public void setSendTime(String sendTime) {
        SendTime = sendTime;
    }

    public int getMessageType() {
        return MessageType;
    }

    public void setMessageType(int messageType) {
        MessageType = messageType;
    }

    public class ContentInfoRawData {
        @SerializedName("MessageID")
        private int MessageID;
        @SerializedName("MessageText")
        private String MessageText;
        @SerializedName("IsNew")
        private boolean IsNew;

        public int getMessageID() {
            return MessageID;
        }

        public void setMessageID(int messageID) {
            MessageID = messageID;
        }

        public String getMessageText() {
            return MessageText;
        }

        public void setMessageText(String messageText) {
            MessageText = messageText;
        }

        public boolean isNew() {
            return IsNew;
        }

        public void setNew(boolean aNew) {
            IsNew = aNew;
        }
    }
}
