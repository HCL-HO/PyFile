package com.hec.app.entity;

/**
 * Created by hec on 2015/10/23.
 */

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PageInfo
        implements Serializable
{
    private static final long serialVersionUID = -4803759973924323430L;

    @SerializedName("PageNumber")
    private int mPageNumber;

    @SerializedName("PageSize")
    private int mPageSize;

    @SerializedName("TotalCount")
    private long mTotalCount;

    public int getPageNumber()
    {
        return this.mPageNumber;
    }

    public int getPageSize()
    {
        return this.mPageSize;
    }

    public long getTotalCount()
    {
        return this.mTotalCount;
    }

    public void setPageNumber(int paramInt)
    {
        this.mPageNumber = paramInt;
    }

    public void setPageSize(int paramInt)
    {
        this.mPageSize = paramInt;
    }

    public void setTotalCount(long paramLong)
    {
        this.mTotalCount = paramLong;
    }
}