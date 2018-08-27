package com.hec.app.framework.content;

import android.database.ContentObserver;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import com.hec.app.entity.BizException;
import com.hec.app.webservice.ServiceException;
import com.google.gson.JsonParseException;

import java.io.IOException;



public abstract class CBContentResolver<T> {
    private final static String LOG_TAG = "CBContentResolver";

    private boolean mIsLoading;
    private boolean mHasError;
    private String mErrorCode;
    private String mErrorDescription;
    private Exception mException;

    private boolean mIsVisible;

    private StateObserver mObserver;

    /**
     * 子类应重写此方法，实现自己不同的查询逻辑。
     *
     * @return 查询得到的业务实体。
     * @throws IOException
     * @throws BizException
     */
    public abstract T query() throws IOException, ServiceException, BizException;

    /*
     * 子类可重写此方法，实现自己不同的加载后逻辑。此方法是在{@link ContentObserver}触发
     * {@link ContentObserver#onChanged()}事件之前执行。
     *
     * @see #registerContentObserver(ContentObserver)
     * @see #onPostLoaded(Object)
     * @param result
     *            异步加载得到的数据
     */
    public void onLoaded(T result) {

    }

    /*
     * 子类可重写此方法，实现自己不同的加载后逻辑。此方法是在{@link ContentObserver}触发
     * {@link ContentObserver#onChanged()}事件之后执行。
     *
     * @see #registerContentObserver(ContentObserver)
     * @see #onLoaded(Object)
     * @param result
     *            异步加载得到的数据
     */
    public void onPostLoaded(T result) {

    }

    /**
     * 子类可重写此方法，实现自己不同的错误处理逻辑。
     *
     * @param code
     *            错误码
     * @param description
     *            错误描述
     */
    public void onError(String code, String description) {

    }

    /**
     * 获取当前的加载状态。
     *
     * @return {@code true}为正在加载；反之为{@code false}。
     */
    public final boolean isLoading() {
        return mIsLoading;
    }

    /**
     * 获取当前是否有错误发生。
     *
     * @return {@code true}为有错误发生；反之为{@code false}。
     */
    public final boolean hasError() {
        return mHasError;
    }

    /**
     * 获取异常错误码。
     *
     * @return 异常错误码。
     */
    public final String getErrorCode() {
        return mErrorCode;
    }

    /**
     * 获取异常错误描述。
     *
     * @return 异常描述。
     */
    public final String getErrorDescription() {
        return mErrorDescription;
    }

    /**
     * 获取异常。
     *
     * @return 异常实例。
     */
    public final Exception getException() {
        return mException;
    }

    /**
     * 设置当前{@link CBContentResolver}实例所在的View是否是显示状态，只有在显示状态，才会执行
     * {@link #onLoaded(Object)}，{@link #onPostLoaded(Object)}或
     * {@link #onError(String, String)}方法。
     *
     * @param isVisible
     *            是否处于显示状态。
     */
    public final void setVisible(boolean isVisible) {
        mIsVisible = isVisible;
    }

    /**
     * 获取当前是否处于显示状态。
     *
     * @return 是否处于显示状态，<code>true</code>表示处于，反之<code>false</code>不处于。
     */
    public final boolean isVisible() {
        return mIsVisible;
    }

    /**
     * 开始异步查询数据。
     */
    public final void startQuery() {
        mHasError = false;
        mIsLoading = true;
        if (mObserver != null) {
            mObserver.onLoading();
        }

        AsyncTask<Void, Void, T> task = new AsyncTask<Void, Void, T>() {

            @Override
            protected T doInBackground(Void... params) {
                try {
                    return query();
                } catch (IOException e) {
                    mException = e;
                    mHasError = true;
                    mErrorDescription = "网络错误，请稍后再试。";
                    Log.e(LOG_TAG, e.toString());
                } catch (JsonParseException e) {
                    mException = e;
                    mHasError = true;
                    mErrorDescription = "网络解析错误，请稍后再试。";
                    Log.e(LOG_TAG, e.toString());
                } catch (ServiceException e) {
                    mException = e;
                    mHasError = true;
                    if (e.isClientError()) {
                        mErrorDescription = "客户端错误，请稍后再试。";
                    } else {
                        mErrorDescription = "服务端错误，请稍后再试。";
                    }
                    Log.e(LOG_TAG, e.toString());
                } catch (BizException e) {
                    mException = e;
                    mHasError = true;
                    mErrorCode = e.getCode();
                    mErrorDescription = e.getDescription();
                }

                return null;
            }

            @Override
            protected void onPostExecute(T result) {
                if (!mIsVisible) {
                    return;
                }
                if (mHasError) {
                    onError(mErrorCode, mErrorDescription);
                } else {
                    onLoaded(result);
                }
                mIsLoading = false;
                if (mObserver != null) {
                    mObserver.onLoaded();
                }
                if (!mHasError) {
                    onPostLoaded(result);
                }
            }
        };

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            task.execute();
        } else {
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

    }

    /**
     * 注册一个{@link ContentObserver}，当开始加载，加载错误，加载完毕时，它会得到通知。
     *
     * @param observer
     *            注册的{@link ContentObserver}。
     */
    public final void registerContentObserver(StateObserver observer) {
        mObserver = observer;
    }
}