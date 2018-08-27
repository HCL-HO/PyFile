package com.hec.app.entity;


public class BizException extends Exception
{
    private static final long serialVersionUID = -2111092304574674326L;
    private final String mCode;
    private final String mDescription;

    public BizException(String paramString1, String paramString2)
    {
        this.mCode = paramString1;
        this.mDescription = paramString2;
    }

    public String getCode()
    {
        return this.mCode;
    }

    public String getDescription()
    {
        return this.mDescription;
    }
}