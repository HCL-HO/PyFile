package com.hec.app.framework.adapter;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;

import com.hec.app.entity.BizException;
import com.hec.app.entity.HasCollection;
import com.hec.app.entity.HasPageInfo;
import com.hec.app.entity.PageInfo;
import com.hec.app.framework.content.CBCollectionResolver;
import com.hec.app.framework.content.StateObserver;
import com.hec.app.webservice.ServiceException;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;



public abstract class MyDecoratedAdapter<T> extends BaseAdapter implements MyBaseAdapter {
    protected Context mContext;
    private ArrayList<T> mList;

    private boolean mHasError;
    protected boolean mIsLoading;
    private boolean mHasMore;
    private boolean mNeedClearWhenComplete;

    private Exception mException;
    private String mErrorCode;
    private String mErrorDescription;

    private CBCollectionResolver<T> mResolver;

    private ArrayList<StateObserver> mObserver = new ArrayList<StateObserver>();

    /**
     * A {@link Future} for the active background task, or {@code null} if no
     * background task is currently executing.
     */
    private Future<HasCollection<T>> mTask;

    /**
     * Tracks whether or not the {@link Activity} is visible.
     */
    private boolean mVisible;

    @Override
    public String getErrorCode() {
        return mErrorCode;
    }

    public void setErrorCode(String mErrorCode) {
        this.mErrorCode = mErrorCode;
    }

    @Override
    public String getErrorDescription() {
        return mErrorDescription;
    }

    public void setErrorDescription(String mErrorDescription) {
        this.mErrorDescription = mErrorDescription;
    }

    @Override
    public Exception getException() {
        return mException;
    }

    public MyDecoratedAdapter(Context context) {
        this(context, new ArrayList<T>());
    }

    public MyDecoratedAdapter(Context context, List<T> list) {
        mContext = context;
        mList = new ArrayList<T>();
        mList.addAll(list);
    }

    public void addMore(HasCollection<T> result) {
        if (mNeedClearWhenComplete) {
            mList.clear();
            mNeedClearWhenComplete = false;
        }
        this.mList.addAll(result.getList());

    }

    @Override
    public void clear() {
        this.mList.clear();
        this.notifyDataSetChanged();
    }

    @Override
    public void remove(int index) {
        this.mList.remove(index);
        this.notifyDataSetChanged();
    }

    public final Context getContext() {
        return mContext;
    }

    public final Collection<T> getList() {
        return mList;
    }

    public final void setList(Collection<T> list) {
        this.mList.addAll(list);
    }

    @Override
    public final boolean isLoading() {
        return mIsLoading;
    }

    @Override
    public final boolean hasError() {
        return mHasError;
    }

    @Override
    public final boolean hasMore() {
        return mHasMore;
    }

    public void setHasMore(boolean hasMore) {
        mHasMore = hasMore;
    }

    public void setVisible(boolean visible) {
        mVisible = visible;
    }

    public synchronized void startQuery(CBCollectionResolver<T> resolver) {
        startQuery(resolver, false);
    }

    public synchronized void startQuery(CBCollectionResolver<T> resolver, boolean needClearWhenComplete) {
        mHasError = false;
        mIsLoading = true;
        mNeedClearWhenComplete = needClearWhenComplete;
        notifyDataSetChanged();
        notifyLoading();
        mTask = startQueryTask(resolver);
    }

    /**
     * Starts a task to execute a query asynchronously.
     * <p>
     * Mock adapters can override this method to control the query.
     *
     * @see #(Object, Cursor)
     */
    private Future<HasCollection<T>> startQueryTask(CBCollectionResolver<T> resolver) {
        mResolver = resolver;
        QueryTask task = new QueryTask(resolver);
        task.executeOnThreadPool();
        return task;
    }

    @Override
    public int getCount() {
        int count = mList.size();
        if (hasStatusView()) {
            count += 1;
        }
        return count;
    }

    @Override
    public T getItem(int position) {
        if (isStatusView(position)) {
            return null;
        } else {
            return mList.get(position);
        }
    }

    @Override
    public long getItemId(int position) {
        if (isStatusView(position)) {
            return AdapterView.INVALID_ROW_ID;
        } else {
            return position;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (isStatusView(position)) {
            // For simplicity, don't recycle the loading/error views
            return IGNORE_ITEM_VIEW_TYPE;
        } else {
            return super.getItemViewType(position);
        }
    }

    @Override
    public final View getView(int position, View convertView, ViewGroup parent) {
        if (isStatusView(position)) {
            Context context = parent.getContext();
            if (isLoading()) {
                return newLoadingView(context, parent);
            } else {
                return newErrorView(context, parent);
            }
        } else {
            return newNormalView(position, convertView, parent);
        }
    }

    @Override
    public boolean retry() {
        startQuery(mResolver);
        return true;
    }

    @Override
    public final void registerStateObserver(StateObserver observer) {
        mObserver.add(observer);
    }

    @Override
    public final void unregisterStateObserver(StateObserver observer) {
        mObserver.remove(observer);
    }

    /**
     * Creates a {@link View} that is shown at the end of the list if an error
     * occurs while loading more rows.
     * <p>
     * The view may include a button for retrying the failed request.
     */
    protected abstract View newErrorView(Context context, ViewGroup parent);

    /**
     * Creates a {@link View} that is shown while additional rows are being
     * loaded.
     */
    protected abstract View newLoadingView(Context context, ViewGroup parent);

    /**
     * Creates a {@link View} that is shown default data items;
     */
    protected abstract View newNormalView(int position, View convertView, ViewGroup parent);

    /**
     * Returns {@code true} if the adapter is currently showing an additional
     * row containing the adapter status, {@code false} otherwise.
     */
    protected final boolean hasStatusView() {
        switch (mList.size()) {
            case 0:
                // The status view is only shown when the adapter has data
                return false;
            default:
                return isLoading() || hasError();
        }
    }

    /**
     * Returns {@code true} if the specified position displays the adapter
     * status, {@code false} otherwise.
     *
     * @see #newLoadingView(Context, ViewGroup)
     * @see #newErrorView(Context, ViewGroup)
     */
    protected final boolean isStatusView(int position) {
        return hasStatusView() && position == lastPosition();
    }

    protected final boolean isVisible() {
        return mVisible;
    }

    /**
     * Returns the last position or {@code -1} if the adapter is empty.
     */
    private int lastPosition() {
        int count = getCount();
        return count - 1;
    }

    private void completeQuery(Future<HasCollection<T>> task, HasCollection<T> result) {
        if (task.equals(mTask)) {
            mTask = null;
            if (isVisible()) {
                if (hasError()) {
                    notifyDataSetChanged();
                    notifyLoaded();
                } else {
                    if (result != null && result.getList() != null && result.getList().size() > 0) {
                        if (result instanceof HasPageInfo) {
                            PageInfo pageInfo = ((HasPageInfo) result).getPageInfo();
                            addMore(result);
                            if (pageInfo != null) {
                                mHasMore = pageInfo.getTotalCount() > mList.size();
                            }

                        } else {
                            addMore(result);
                        }
                    }

                    notifyDataSetChanged();
                    notifyLoaded();
                }
            }
        }
    }

    private void notifyLoading() {
        for (StateObserver observer : mObserver) {
            observer.onLoading();
        }
    }

    private void notifyLoaded() {
        for (StateObserver observer : mObserver) {
            observer.onLoaded();
        }
    }

    private class QueryTask extends AsyncTask<Void, Void, HasCollection<T>> implements Future<HasCollection<T>> {

        private final CBCollectionResolver<T> resolver;

        private boolean mDone;

        public QueryTask(CBCollectionResolver<T> resolver) {
            this.resolver = resolver;
        }

        public final AsyncTask<Void, Void, HasCollection<T>> executeOnThreadPool(Void... params) {
            if (Build.VERSION.SDK_INT < 4) {
                // Thread pool size is 1
                return execute(params);
            } else if (Build.VERSION.SDK_INT < 11) {
                // The execute() method uses a thread pool
                return execute(params);
            } else {
                try {
                    Method method = android.os.AsyncTask.class.getMethod("executeOnExecutor", Executor.class,
                            Object[].class);
                    Field field = android.os.AsyncTask.class.getField("THREAD_POOL_EXECUTOR");
                    Object executor = field.get(null);
                    method.invoke(this, executor, params);
                } catch (NoSuchMethodException e) {
                    throw new RuntimeException("Unexpected NoSuchMethodException", e);
                } catch (NoSuchFieldException e) {
                    throw new RuntimeException("Unexpected NoSuchFieldException", e);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Unexpected IllegalAccessException", e);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException("Unexpected InvocationTargetException", e);
                }

                return this;
            }
        }

        @Override
        protected HasCollection<T> doInBackground(Void... params) {
            try {
                return this.resolver.query();
            } catch (IOException e) {
                mException = e;
                mHasError = true;
                mErrorDescription = "抱歉，网络错误，请稍后重试！";
            } catch (ServiceException e) {
                mException = e;
                mHasError = true;
                if (e.isClientError()) {
                    mErrorDescription = "抱歉，客户端错误，请稍后重试！";
                } else {
                    mErrorDescription = "抱歉，服务端错误，请稍后重试！";
                }

            } catch (BizException e) {
                mException = e;
                mHasError = true;
                mErrorCode = e.getCode();
                mErrorDescription = e.getDescription();
            }
            return null;
        }

        @Override
        protected void onPostExecute(HasCollection<T> result) {
            mDone = true;
            mIsLoading = false;
            completeQuery(this, result);
        }

        /** {@inheritDoc} */
        @Override
        public boolean isDone() {
            return mDone;
        }
    }

    @SuppressWarnings("rawtypes")
    public static class ListScrollListener implements AbsListView.OnScrollListener {

        private MyDecoratedAdapter mAdapter;
        private CBCollectionResolver mResolver;

        public ListScrollListener(MyDecoratedAdapter adapter, CBCollectionResolver resolver) {
            this.mAdapter = adapter;
            this.mResolver = resolver;
        }

        @SuppressWarnings("unchecked")
        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if (firstVisibleItem >= totalItemCount - visibleItemCount && mAdapter.getList() != null
                    && mAdapter.getList().size() > 0) {
                if (mAdapter.hasMore() && !mAdapter.isLoading()) {
                    mAdapter.startQuery(mResolver);
                }
            }
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {

        }
    }
}
