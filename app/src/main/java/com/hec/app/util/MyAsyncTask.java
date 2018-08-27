package com.hec.app.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import com.google.gson.JsonParseException;
import com.hec.app.R;
import com.hec.app.activity.HomeActivity;
import com.hec.app.activity.LoginActivity;
import com.hec.app.activity.base.BaseApp;
import com.hec.app.config.CommonConfig;
import com.hec.app.entity.BizException;
import com.hec.app.framework.widget.MyToast;
import com.hec.app.webservice.ServiceException;

import junit.framework.Test;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public abstract class MyAsyncTask<T> extends AsyncTask<Void, Void, T> {
    private String mCode = null;
    protected Context mContext;
    private String mErrorMessage = null;
    private Exception mException;
    private OnError mOnError;
    private static Executor executor;
    private static Executor executorHomePage;
    private SharedPreferences token;

    public MyAsyncTask() {
    }

    public MyAsyncTask(Context paramContext) {
        this(paramContext, null);
        if (executor == null) {
            executor = Executors.newCachedThreadPool();
        }
        if (executorHomePage == null) {
            executorHomePage = Executors.newSingleThreadExecutor();
        }
    }

    public MyAsyncTask(Context paramContext, OnError paramOnError) {
        this.mContext = paramContext;
        this.mOnError = paramOnError;
    }

    public abstract T callService()
            throws IOException, JsonParseException, BizException, ServiceException;

    protected T doInBackground(Void[] paramArrayOfVoid) {
        try {
            T localObject = callService();
            return localObject;
        } catch (JsonParseException localJsonParseException) {
            TestUtil.print("JsonParseException");
            this.mException = localJsonParseException;
            if (this.mContext != null)
                this.mErrorMessage = this.mContext.getString(R.string.json_error_message);
//            if(!isCancelled())
//                this.cancel(true);
        } catch (IOException localIOException) {
            //TestUtil.print("localIOException");
            this.mException = localIOException;
            //activity maybe distroy
            if (this.mContext != null) {
                //TODO: check here for null point
                //this.mErrorMessage = this.mContext.getString(R.string.web_io_error_message);
            }
//            if(!isCancelled())
//                this.cancel(true);
        } catch (BizException localBizException) {
            this.mException = localBizException;
            this.mErrorMessage = localBizException.getDescription();
            this.mCode = localBizException.getCode();
//            if(!isCancelled())
//                this.cancel(true);
        } catch (ServiceException e) {
            mException = e;
            if (mContext != null) {
                if (e.isClientError()) {
                    mErrorMessage = mContext.getString(R.string.web_client_error_message);
                } else {
                    mErrorMessage = mContext.getString(R.string.web_server_error_message);
                }
            }
//            if(!isCancelled())
//                this.cancel(true);
        } catch (IllegalArgumentException e) {
//            if(!isCancelled())
//                this.cancel(true);
        }
        return null;
    }

    public void executeTask() {
        if (Build.VERSION.SDK_INT < 11) {
            execute();
            return;
        }
        //Executor executor = new ThreadPoolExecutor(10,50,10,
        //        TimeUnit.SECONDS,new LinkedBlockingDeque<Runnable>(100));
        //executeOnExecutor(executor);
        //executeOnExecutor(Executors.newCachedThreadPool());
        if (mContext instanceof HomeActivity) {
            executeOnExecutor(executorHomePage);
            Log.i("speed", "executorHomePage");
        } else {
            executeOnExecutor(executor);
            Log.i("speed", "execute normal");
        }
        //executeOnExecutor(Executors.newFixedThreadPool(20));
        //executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public String getCode() {
        return this.mCode;
    }

    public String getErrorMessage() {
        return this.mErrorMessage;
    }

    public abstract void onLoaded(T paramT)
            throws Exception;

    protected void onPostExecute(T paramT) {
        if (mContext == null)
            return;
        if (this.mException != null) {
            Log.i("store", "wrong: " + this.mException.toString());
        } else if (this.mException == null && !BaseApp.isGettingAvailableApiUrl) {
            BaseApp.retryCount = 0;
        }
        if (this.mException != null && !(this.mException instanceof BizException)) {
            if (this.mOnError == null) {
                if (!StringUtil.isEmpty(this.mErrorMessage)) {
                    MyToast.show(this.mContext, this.mErrorMessage);
                }
            } else {
                this.mOnError.handleError(this.mException);
            }
        }
        try {
            if (this.mException instanceof BizException) {
                if (this.mErrorMessage != null && this.mErrorMessage.contains("登录已过期")) {
                    if (this.mErrorMessage.contains("VIP")) {
                        this.mOnError.handleError(this.mException);
                    } else {
                        TestUtil.print("token expire");
                        token = mContext.getSharedPreferences(CommonConfig.KEY_TOKEN, Context.MODE_PRIVATE);
                        token.edit().putString(CommonConfig.KEY_TOKEN_TOKENS, "").commit();
                        CustomerAccountManager.getInstance().logOut();
                        if (BaseApp.rootActivity != null)
                            BaseApp.rootActivity.finish();
                        MyToast.show(this.mContext, this.mErrorMessage);
                        IntentUtil.redirectToNextActivity(this.mContext, LoginActivity.class);
                    }
                } else if (this.mErrorMessage != null && (this.mErrorMessage.contains("用户被冻结") || this.mErrorMessage.contains("充值太频繁")) ) {
                    this.mOnError.handleError(this.mException);
                    return;
                } else {
                    MyToast.show(this.mContext, this.mErrorMessage);
                    Log.i("wxj", "BizEx 503");
                    this.mOnError.handleError(this.mException);
                    return;
                }
            } else {
                onLoaded(paramT);
            }
        } catch (Exception localException) {

        }
    }

    public void setCode(String paramString) {
        this.mCode = paramString;
    }

    public void setErrorMessage(String paramString) {
        this.mErrorMessage = paramString;
    }

    public void setOnError(OnError paramOnError) {
        this.mOnError = paramOnError;
    }

    public interface OnError {
        void handleError(Exception paramException);
    }

    protected void onCancelled() {
        super.onCancelled();
        //MyToast.show(mContext,"我现在使用了asynctask的cancel方法来handle错误！");
    }
}