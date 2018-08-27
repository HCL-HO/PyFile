package com.hec.app.entity;

import com.networkbench.com.google.gson.annotations.SerializedName;

import java.util.List;

public class CustomServiceChatInfo {
    @SerializedName("ExtensionData")
    private Object ExtensionData;

    @SerializedName("Message")
    private List<CustomerServiceChatDetailInfo> Message;

    @SerializedName("MessageDate")
    private String MessageDate;

    public Object getExtensionData() {
        return ExtensionData;
    }

    public void setExtensionData(Object extensionData) {
        this.ExtensionData = extensionData;
    }

    public List<CustomerServiceChatDetailInfo> getMessage() {
        return Message;
    }

    public void setMessage(List<CustomerServiceChatDetailInfo> message) {
        this.Message = message;
    }

    public String getMessageDate() {
        return MessageDate;
    }

    public void setMessageDate(String messageDate) {
        this.MessageDate = messageDate;
    }
}
