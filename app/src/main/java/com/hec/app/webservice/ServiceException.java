package com.hec.app.webservice;


public class ServiceException extends Exception
{
    private static final long serialVersionUID = -3693672283333003372L;
    private int responseCode;

    public ServiceException(int paramInt)
    {
        this.responseCode = paramInt;
    }

    public int getResponseCode()
    {
        return this.responseCode;
    }

    public boolean isClientError()
    {
        return (this.responseCode >= 400) && (this.responseCode < 500);
    }

    public boolean isServerError()
    {
        return (this.responseCode >= 500) && (this.responseCode < 600);
    }
}