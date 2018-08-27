package com.hec.app.entity;

import java.io.Serializable;

public class ServiceMessage<T>
        implements Serializable
{
    private static final long serialVersionUID = -2822558985195026018L;
    private int Code;
    private T Data;
    private String Description;

    public int getCode()
    {
        return this.Code;
    }

    public T getData()
    {
        return this.Data;
    }

    public String getDescription()
    {
        return this.Description;
    }

    public void setCode(int paramInt)
    {
        this.Code = paramInt;
    }

    public void setData(T paramT)
    {
        this.Data = paramT;
    }

    public void setDescription(String paramString)
    {
        this.Description = paramString;
    }
}