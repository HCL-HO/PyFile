package com.hec.app.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by wangxingjian on 16/2/22.
 */
public class MoneyDetailInfo {
    @SerializedName("Date")
    String date;
    @SerializedName("ItemName")
    String ItemName;
    @SerializedName("ItemDetails")
    String ItemDetails;
    @SerializedName("Amount")
    String Amount;

    public MoneyDetailInfo(String date, String itemName, String itemDetails, String amount) {
        this.date = date;
        ItemName = itemName;
        ItemDetails = itemDetails;
        Amount = amount;
    }

    public String getAmount() {
        return Amount;
    }

    public void setAmount(String amount) {
        Amount = amount;
    }

    public String getItemDetails() {
        return ItemDetails;
    }

    public void setItemDetails(String itemDetails) {
        ItemDetails = itemDetails;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getItemName() {
        return ItemName;
    }

    public void setItemName(String itemName) {
        ItemName = itemName;
    }
}
