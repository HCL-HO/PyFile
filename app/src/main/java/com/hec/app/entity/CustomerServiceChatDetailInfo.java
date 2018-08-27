package com.hec.app.entity;

import com.google.gson.annotations.SerializedName;

public class CustomerServiceChatDetailInfo{
    public final static int TYPE_DATE = 0;
    public final static int TYPE_SERVICE_AGENT = 1;
    public final static int TYPE_SERVICE_CUSTOMER = 2;

    //補充內容
    @SerializedName("ExtensionData")
    private Object ExtensionData;
    // 發言內容
    @SerializedName("Dialogue")
    private String Dialogue;
    // 發言時間
    @SerializedName("DialogueTime")
    private String DialogueTime;
    // 發言日期
    @SerializedName("EventDate")
    private String EventDate;
    // 圖片路徑
    @SerializedName("PictureURL")
    private String PictureURL;
    // 發言者
    @SerializedName("UserID")
    private int UserID;
    @SerializedName("MessageID")
    private int MessageID;

    private int type;
    private String date;
    private boolean isImageLoading;
    private boolean isImageError;
    private String imageName;
    private boolean isError;


    public Object getExtensionData() {
        return ExtensionData;
    }

    public void setExtensionData(Object extensionData) {
        this.ExtensionData = extensionData;
    }

    public String getDialogue() {
        return Dialogue;
    }

    public void setDialogue(String dialogue) {
        this.Dialogue = dialogue;
    }

    public String getDialogueTime() {
        return DialogueTime;
    }

    public void setDialogueTime(String dialogueTime) {
        this.DialogueTime = dialogueTime;
    }

    public String getEventDate() {
        return EventDate;
    }

    public void setEventDate(String eventDate) {
        this.EventDate = eventDate;
    }

    public String getPictureURL() {
        return PictureURL;
    }

    public void setPictureURL(String pictureURL) {
        this.PictureURL = pictureURL;
    }

    public int getUserID() {
        return UserID;
    }

    public void setUserID(int userID) {
        this.UserID = userID;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isImageLoading() {
        return isImageLoading;
    }

    public void setImageLoading(boolean imageLoading) {
        isImageLoading = imageLoading;
    }

    public boolean isImageError() {
        return isImageError;
    }

    public void setImageError(boolean imageError) {
        isImageError = imageError;
    }

    public boolean isError() {
        return isError;
    }

    public void setError(boolean error) {
        isError = error;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public int getMessageID() {
        return MessageID;
    }

    public void setMessageID(int messageID) {
        MessageID = messageID;
    }
}
