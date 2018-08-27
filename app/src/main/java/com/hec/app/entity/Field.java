package com.hec.app.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jhezenhu on 2017/4/25.
 */

public class Field {
    @SerializedName("Prompt")
    private String mPrompt;
    @SerializedName("Nums")
    private String mNums;
    @SerializedName("ShowQuickSelect")
    private boolean mShowQuickSelect;

    public void setPrompt(String prompt)
    {
        this.mPrompt = prompt;
    }

    public String getPrompt()
    {
        return this.mPrompt;
    }

    public void setNums(String nums)
    {
        this.mNums = nums;
    }

    public String getNums()
    {
        return this.mNums;
    }

    public void setShowQuickSelect(boolean showQuickSelect)
    {
        this.mShowQuickSelect = showQuickSelect;
    }

    public boolean getShowQuickSelect()
    {
        return this.mShowQuickSelect;
    }
}
