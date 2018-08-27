package com.hec.app.entity;


import android.support.annotation.NonNull;

public class RestfulApiUrlEntity implements Comparable<RestfulApiUrlEntity>{
    private String url;
    private boolean isAvailable;
    private boolean isStickyApiUrl = false;
    private long responseTime;

    public RestfulApiUrlEntity(String url, boolean isAvailable) {
        this.url = url;
        this.isAvailable = isAvailable;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public long getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(long responseTime) {
        this.responseTime = responseTime;
    }

    public boolean isStickyApiUrl() {
        return isStickyApiUrl;
    }

    public void setStickyApiUrl(boolean stickyApiUrl) {
        isStickyApiUrl = stickyApiUrl;
    }
    @Override
    public int compareTo(@NonNull RestfulApiUrlEntity o) {
        if (isStickyApiUrl) {
            return -1;
        } else if (o.isStickyApiUrl) {
            return 1;
        }
        if (responseTime > o.getResponseTime()) {
            return 1;
        } else {
            return -1;
        }
    }
}
