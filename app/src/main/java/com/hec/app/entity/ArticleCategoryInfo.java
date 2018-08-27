package com.hec.app.entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by wangxingjian on 2017/2/14.
 */

public class ArticleCategoryInfo {
    @SerializedName("Id")
    private int id;
    @SerializedName("Name")
    private String name;
    @SerializedName("Total")
    private int total;
    @SerializedName("TotalByToday")
    private int TotalByToday;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getTotalByToday() {
        return TotalByToday;
    }

    public void setTotalByToday(int totalByToday) {
        TotalByToday = totalByToday;
    }
}
