package com.hec.app.activity;

import android.util.Log;

import com.google.gson.JsonParseException;
import com.hec.app.activity.base.BaseActivity;
import com.hec.app.activity.base.BaseApp;
import com.hec.app.entity.AppBean;
import com.hec.app.entity.BizException;
import com.hec.app.entity.Response;
import com.hec.app.util.MyAsyncTask;
import com.hec.app.webservice.HomeService;
import com.hec.app.webservice.ServiceException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;


public class TestUrlActivity extends BaseActivity {

    private boolean mIsSelectFastestURLError = false;
    private boolean mIsAppInfoError = false;
    private Map<String, Long> mMap = new ConcurrentHashMap<>();
    private Map<String, Boolean> mAvailableURLs = new HashMap<>();
    private int mCount = 0;
    private long mFastestTime = 5000;
    private int mLocalCount = 0;
    private String mCdn;
    private String mFastestUrl;

    public void getAppInfo(){
        mIsAppInfoError = false;
        mAvailableURLs.clear();

        MyAsyncTask<AppBean> task = new MyAsyncTask<AppBean>(TestUrlActivity.this) {
            public AppBean callService() throws IOException, JsonParseException, BizException, ServiceException {
                return new HomeService().getAppInfo();
            }

            @Override
            public void onLoaded(AppBean result) throws Exception {
                if (TestUrlActivity.this == null || TestUrlActivity.this.isFinishing()) {
                    showLog("GetAppInfo TestUrlActivity Error");
                    return;
                }

                if (!mIsAppInfoError) {
                    BaseApp.setAppBean(result);

                    if (result.getApiUrl() != null && !result.getApiUrl().contains("http")) {
                        mAvailableURLs.put("http://"+result.getApiUrl(), true);
                        mCdn = "http://"+result.getApiUrl();
                    }
                    else if (result.getApiUrl() != null && result.getApiUrl().contains("http"))  {
                        mAvailableURLs.put(result.getApiUrl(),true);
                        mCdn = result.getApiUrl();
                    }

                    for (String url : result.getApiUrls().split(";")) {
                        if (!url.contains("http")) {
                            url = "http://" + url;
                        }
                        mAvailableURLs.put(url, true);
                    }

                    for (final String url : mAvailableURLs.keySet()) {
                        showLog(url);
                    }

                    showLog("GetAppInfo Finish");
                    selectFastestURL();
                }
                else {
                    showLog("GetAppInfo OnLoaded Error");
                }
            }
        };
        task.setOnError(new MyAsyncTask.OnError() {
            @Override
            public void handleError(Exception e) {
                mIsAppInfoError = true;
            }
        });
        task.executeTask();
    }

    public void selectFastestURL(){
        mMap.clear();
        mCount = 0;
        mLocalCount = mAvailableURLs.size();

        for(final String url : mAvailableURLs.keySet()) {
            if (mAvailableURLs.get(url)) {
                mIsSelectFastestURLError = false;
                MyAsyncTask<Response> task = new MyAsyncTask<Response>(TestUrlActivity.this) {
                    long requestTime, responseTime;

                    @Override
                    public Response callService() throws IOException, JsonParseException, BizException, ServiceException {
                        requestTime = System.currentTimeMillis();
                        Random random = new Random();
                        int randomTime = random.nextInt(5) + 1;

                        synchronized (this) {
                            try {
                                wait(randomTime * 1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                        return new HomeService().testURLSpeed(url);
                    }

                    @Override
                    public void onLoaded(Response result) throws Exception {
                        if (!mIsSelectFastestURLError) {
                            responseTime = System.currentTimeMillis();
                            mMap.put(url, (responseTime - requestTime));
                            showLog(url + "  time = " + mMap.get(url));
                        }
                        else {
                            showLog("SelectFastestURL [" + url + "] OnLoaded Error");
                        }

                        if (mCount != mLocalCount - 1) {
                            synchronized (this) {
                                mCount++;
                            }
                        }
                        else {
                            if (!mMap.isEmpty()) {
                                for (String key : mMap.keySet()) {
                                    if (mMap.get(key) < mFastestTime && !key.equals(mCdn)) {
                                        mFastestTime = mMap.get(key);
                                        mFastestUrl = key;
                                    }
                                }
                            }
                            showLog("FASTEST_URL [" + mFastestUrl + "]");
                        }
                    }
                };
                task.executeTask();
                task.setOnError(new MyAsyncTask.OnError() {
                    @Override
                    public void handleError(Exception paramException) {
                        mIsSelectFastestURLError = true;
                    }
                });
            }
        }
    }

    private void showLog(String str) {
        Log.e("TEST URL", "[TEST URL] " + str);
    }
}