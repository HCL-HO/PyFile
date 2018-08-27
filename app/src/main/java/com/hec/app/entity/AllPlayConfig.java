package com.hec.app.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by jhezenhu on 2017/4/25.
 */

public class AllPlayConfig {
    @SerializedName("HashCode")
    private String mHashCode;
    @SerializedName("SSC")
    private List<PlayConfig> mSSC;
    @SerializedName("SelectFive")
    private List<PlayConfig> mSelectFive;
    @SerializedName("PK10")
    private List<PlayConfig> mPK10;
    @SerializedName("Welfare3D")
    private List<PlayConfig> mWelfare3D;
    @SerializedName("KuaiSan")
    private List<PlayConfig> mKuaiSan;

    public void setHashCode(String hashCode)
    {
        this.mHashCode = hashCode;
    }

    public String getHashCode()
    {
        return this.mHashCode;
    }

    public void setSSC(List<PlayConfig> ssc)
    {
        this.mSSC = ssc;
    }

    public List<PlayConfig> getSSC()
    {
        return this.mSSC;
    }

    public void setSelectFive(List<PlayConfig> selectFive)
    {
        this.mSelectFive = selectFive;
    }

    public List<PlayConfig> getSelectFive()
    {
        return this.mSelectFive;
    }

    public void setPK10(List<PlayConfig> pk10)
    {
        this.mPK10 = pk10;
    }

    public List<PlayConfig> getPK10()
    {
        return this.mPK10;
    }

    public void setWelfare3D(List<PlayConfig> welfare3D)
    {
        this.mWelfare3D = welfare3D;
    }

    public List<PlayConfig> getWelfare3D()
    {
        return this.mWelfare3D;
    }

    public List<PlayConfig> getKuaiSan() {
        return mKuaiSan;
    }

    public void setKuaiSan(List<PlayConfig> mKuaiSan) {
        this.mKuaiSan = mKuaiSan;
    }
}
