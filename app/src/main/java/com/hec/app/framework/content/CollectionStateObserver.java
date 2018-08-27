package com.hec.app.framework.content;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.hec.app.R;
import com.hec.app.entity.BizException;
import com.hec.app.framework.adapter.MyBaseAdapter;
import com.hec.app.framework.widget.PullToRefreshBase;
import com.hec.app.framework.widget.PullToRefreshView;


/**
 * Computes the aggregate query state of one or more adapters.
 * <p>
 * Shows a loading banner when loading.
 * <p>
 * Shows a title bar spinner when reloading.
 * <p>
 * Shows an error message and retry button when there is an error.
 */
public class CollectionStateObserver implements View.OnClickListener {

    private static void setViewVisible(View view, boolean visible) {
        if (view != null) {
            int visibility = visible ? View.VISIBLE : View.GONE;
            if (visibility != view.getVisibility()) {
                view.setVisibility(visibility);
            }
        }
    }

    private static void setWindowIndeterminateProgressVisible(Window window, boolean visible) {
        int featureId = Window.FEATURE_INDETERMINATE_PROGRESS;
        int value = visible ? Window.PROGRESS_VISIBILITY_ON : Window.PROGRESS_VISIBILITY_OFF;
        window.setFeatureInt(featureId, value);
    }

    private final Observer mObserver = new Observer();

    private View mLoading;

    private View mError;

    private View mContent;

    private Window mWindow;

    private MyBaseAdapter[] mAdapters;

    @SuppressWarnings("rawtypes")
    private PullToRefreshBase mPullToRefreshView;

    private PullToRefreshView mPullToRefreshNewView;

    public void setAdapters(MyBaseAdapter... adapters) {
        if (mAdapters != null) {
            for (MyBaseAdapter adapter : mAdapters) {
                adapter.unregisterStateObserver(mObserver);
            }
        }
        mAdapters = adapters;
        if (mAdapters != null) {
            for (MyBaseAdapter adapter : mAdapters) {
                adapter.registerStateObserver(mObserver);
            }
        }
    }

    public void setPullToRefreshView(@SuppressWarnings("rawtypes") PullToRefreshBase pullToRefreshView) {
        mPullToRefreshView = pullToRefreshView;
    }

    public void setPullToRefreshView(@SuppressWarnings("rawtypes") PullToRefreshView pullToRefreshView) {
        mPullToRefreshNewView = pullToRefreshView;
    }

    public void setActivity(Activity activity) {
        if (activity != null) {
            mLoading = activity.findViewById(R.id.loading);
            mError = activity.findViewById(R.id.error);
            mContent = activity.findViewById(R.id.content);
            mWindow = activity.getWindow();
            mError.findViewById(R.id.retry).setOnClickListener(this);
        } else {
            mLoading = null;
            mError = null;
            mContent = null;
            mWindow = null;
            mError = null;
        }
    }

    public void setView(View view) {
        if (view != null) {
            mLoading = view.findViewById(R.id.loading);
            mError = view.findViewById(R.id.error);
            mContent = view.findViewById(R.id.content);
            mError.findViewById(R.id.retry).setOnClickListener(this);
        } else {
            mLoading = null;
            mError = null;
            mContent = null;
            mWindow = null;
            mError = null;
        }
    }

    public boolean isLoading() {
        for (MyBaseAdapter adapter : mAdapters) {
            if (adapter.isLoading()) {
                return true;
            }
        }
        return false;
    }

    public boolean hasError() {
        for (MyBaseAdapter adapter : mAdapters) {
            if (adapter.hasError()) {
                return true;
            }
        }
        return false;
    }

    public boolean isEmpty() {
        for (MyBaseAdapter adapter : mAdapters) {
            if (0 != adapter.getCount()) {
                return false;
            }
        }
        return true;
    }

    public void retry() {
        for (MyBaseAdapter adapter : mAdapters) {
            adapter.retry();
        }
    }

    public void showLoading() {
        hideAllViews();

        setViewVisible(mLoading, true);
    }

    public void showError() {
        hideAllViews();

        setViewVisible(mError, true);

        if (mError instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) mError;
            setErrorDescription(group);
        }
    }

    private void setErrorDescription(ViewGroup viewGroup){

        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);

            if (child instanceof TextView) {
                if (child instanceof Button) {
                    for (MyBaseAdapter adapter : mAdapters) {
                        if (adapter.getException() instanceof BizException) {

                            child.setVisibility(View.GONE); // BizException?????��???�??�??以�????
                        }
                    }
                } else {
                    for (MyBaseAdapter adapter : mAdapters) {
                        ((TextView) child).setText(adapter.getErrorDescription());
                    }
                }
            } else if (child instanceof ViewGroup) {
                setErrorDescription((ViewGroup) child);
            }
        }
    }

    public void showContent() {
        hideAllViews();

        setViewVisible(mContent, true);
    }

    private void hideAllViews() {
        setViewVisible(mLoading, false);
        setViewVisible(mError, false);
        setViewVisible(mContent, false);
    }

    private void updateViewsWhenLoading() {
        if (mWindow != null) {
            setWindowIndeterminateProgressVisible(mWindow, false);
        }

        if (isEmpty()) {
            showLoading();
        } else {
            showContent();
            if (mWindow != null) {
                setWindowIndeterminateProgressVisible(mWindow, true);
            }
        }
    }

    private void updateViewsWhenLoaded() {
        if (mWindow != null) {
            setWindowIndeterminateProgressVisible(mWindow, false);
        }
        if (isEmpty()) {
            if (hasError()) {
                showError();
            } else {
                showContent();
            }
        } else {
            showContent();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.retry) {
            retry();
        }
    }

    private class Observer implements StateObserver {

        @Override
        public void onLoading() {
            updateViewsWhenLoading();
        }

        @Override
        public void onLoaded() {
            updateViewsWhenLoaded();

            if (mPullToRefreshView != null) {
                mPullToRefreshView.onRefreshComplete();
            }
            if (mPullToRefreshNewView != null) {
                mPullToRefreshNewView.onHeaderRefreshComplete();
                mPullToRefreshNewView.onFooterRefreshComplete();
            }
        }

    }
}