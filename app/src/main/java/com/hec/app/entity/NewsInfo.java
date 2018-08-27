package com.hec.app.entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by hec on 2015/10/15.
 */
public class NewsInfo implements Serializable {
    @SerializedName("ArticleID")
    private  int articleID;
    @SerializedName("Title")
    private String title;
    @SerializedName("Content")
    private String content;

    @SerializedName("InDate")
    private String inDate;

    @SerializedName("Summary")
    private String summary;

    @SerializedName("Source")
    private String source;

    public int getArticleID() {
        return articleID;
    }

    public void setArticleID(int id) {
        this.articleID = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getInDate() {
        return inDate;
    }

    public void setInDate(String inDate) {
        this.inDate = inDate;
    }

    public String getSource() {
        return "来源：新京报";
    }

    public void setSource(String source) {
        this.source = source;
    }
}


