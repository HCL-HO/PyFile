package com.hec.app.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by jhezenhu on 2017/4/25.
 */

public class PlayConfig {
    @SerializedName("LotteryType")
    private int mLotteryType;
    @SerializedName("DigitNums")
    private int mDigitNums;
    @SerializedName("CanInput")
    private String mCanInput;
    @SerializedName("CName")
    private String mCName;
    @SerializedName("PlayTypeID")
    private int mPlayTypeID;
    @SerializedName("PlayTypeName")
    private String mPlayTypeName;
    @SerializedName("PlayTypeRadioID")
    private int mPlayTypeRadioID;
    @SerializedName("PlayTypeRadioName")
    private String mPlayTypeRadioName;
    @SerializedName("Fields")
    private List<Field> mFields;
    @SerializedName("ShowQuickSelect")
    private boolean mShowQuickSelect;
    @SerializedName("NumberSeprator")
    private String mNumberSeprator;
    @SerializedName("ShowRenXuanPosition")
    private boolean mShowRenXuanPosition;
    @SerializedName("MinPositions")
    private int mMinPositions;
    @SerializedName("PlayMode")
    private int mPlayMode;

    public void setLotteryType(int lotteryType)
    {
        this.mLotteryType = lotteryType;
    }

    public int getLotteryType()
    {
        return this.mLotteryType;
    }

    public void setDigitNums(int digitNums)
    {
        this.mDigitNums = digitNums;
    }

    public int getDigitNums()
    {
        return this.mDigitNums;
    }

    public void setCanInput(String canInput)
    {
        this.mCanInput = canInput;
    }

    public String getCanInput()
    {
        return this.mCanInput;
    }

    public void setCName(String cName)
    {
        this.mCName = cName;
    }

    public String getCName()
    {
        return this.mCName;
    }

    public void setPlayTypeID(int playTypeID)
    {
        this.mPlayTypeID = playTypeID;
    }

    public int getPlayTypeID()
    {
        return this.mPlayTypeID;
    }

    public void setPlayTypeName(String playTypeName)
    {
        this.mPlayTypeName = playTypeName;
    }

    public String getPlayTypeName()
    {
        return this.mPlayTypeName;
    }

    public void setPlayTypeRadioID(int playTypeRadioID)
    {
        this.mPlayTypeRadioID = playTypeRadioID;
    }

    public int getPlayTypeRadioID()
    {
        return this.mPlayTypeRadioID;
    }

    public void setPlayTypeRadioName(String playTypeRadioName)
    {
        this.mPlayTypeRadioName = playTypeRadioName;
    }

    public String getPlayTypeRadioName()
    {
        return this.mPlayTypeRadioName;
    }

    public void setFields(List<Field> fields)
    {
        this.mFields = fields;
    }

    public List<Field> getFields()
    {
        return this.mFields;
    }

    public void setShowQuickSelect(boolean showQuickSelect)
    {
        this.mShowQuickSelect = showQuickSelect;
    }

    public boolean getShowQuickSelect()
    {
        return this.mShowQuickSelect;
    }

    public void setNumberSeprator(String numberSeprator)
    {
        this.mNumberSeprator = numberSeprator;
    }

    public String getNumberSeprator()
    {
        return this.mNumberSeprator;
    }

    public void setShowRenXuanPosition(boolean showRenXuanPosition)
    {
        this.mShowRenXuanPosition = showRenXuanPosition;
    }

    public boolean getShowRenXuanPosition()
    {
        return this.mShowRenXuanPosition;
    }

    public void setMinPositions(int minPositions)
    {
        this.mMinPositions = minPositions;
    }

    public int getMinPositions()
    {
        return this.mMinPositions;
    }

    public void setPlayMode(int playMode)
    {
        this.mPlayMode = playMode;
    }

    public int getPlayMode()
    {
        return this.mPlayMode;
    }
}
