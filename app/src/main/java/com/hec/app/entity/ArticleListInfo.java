package com.hec.app.entity;

/**
 * Created by wangxingjian on 2017/2/15.
 */

public class ArticleListInfo {
    private String Body;
    private int CategoryId;
    private int Id;
    private boolean IsDeleted;
    private boolean IsEssence;
    private boolean IsOriginal;
    private String ModifiedTime;
    private String PublishedTime;
    private String RejectedReason;
    private int Score;
    private int Status;
    private int ThumbsDown;
    private int ThumbsUp;
    private String Title;
    private int Userid;
    private String UserName;
    private int Views;
    private int MyThumbsType;
    private boolean MyCollectedType;
    private boolean isRead = false;

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public boolean isMyCollectedType() {
        return MyCollectedType;
    }

    public void setMyCollectedType(boolean myCollectedType) {
        MyCollectedType = myCollectedType;
    }

    public int getMyThumbsType() {
        return MyThumbsType;
    }

    public void setMyThumbsType(int myThumbsType) {
        MyThumbsType = myThumbsType;
    }

    public String getBody() {
        return Body;
    }

    public void setBody(String body) {
        Body = body;
    }

    public int getCategoryId() {
        return CategoryId;
    }

    public void setCategoryId(int categoryId) {
        CategoryId = categoryId;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public boolean isDeleted() {
        return IsDeleted;
    }

    public void setDeleted(boolean deleted) {
        IsDeleted = deleted;
    }

    public boolean isEssence() {
        return IsEssence;
    }

    public void setEssence(boolean essence) {
        IsEssence = essence;
    }

    public boolean isOriginal() {
        return IsOriginal;
    }

    public void setOriginal(boolean original) {
        IsOriginal = original;
    }

    public String getModifiedTime() {
        return ModifiedTime;
    }

    public void setModifiedTime(String modifiedTime) {
        ModifiedTime = modifiedTime;
    }

    public String getPublishedTime() {
        return PublishedTime;
    }

    public void setPublishedTime(String publishedTime) {
        PublishedTime = publishedTime;
    }

    public String getRejectedReason() {
        return RejectedReason;
    }

    public void setRejectedReason(String rejectedReason) {
        RejectedReason = rejectedReason;
    }

    public int getScore() {
        return Score;
    }

    public void setScore(int score) {
        Score = score;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }

    public int getThumbsDown() {
        return ThumbsDown;
    }

    public void setThumbsDown(int thumbsDown) {
        ThumbsDown = thumbsDown;
    }

    public int getThumbsUp() {
        return ThumbsUp;
    }

    public void setThumbsUp(int thumbsUp) {
        ThumbsUp = thumbsUp;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public int getUserid() {
        return Userid;
    }

    public void setUserid(int userid) {
        Userid = userid;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public int getViews() {
        return Views;
    }

    public void setViews(int views) {
        Views = views;
    }
}
