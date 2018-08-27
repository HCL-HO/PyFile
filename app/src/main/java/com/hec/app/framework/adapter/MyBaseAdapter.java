package com.hec.app.framework.adapter;

import android.widget.ListAdapter;

        import com.hec.app.framework.content.StateObserver;

/**
 * Created by hec on 2015/10/23.
 */
public interface MyBaseAdapter extends ListAdapter
{
    void clear();

    String getErrorCode();

    String getErrorDescription();

    Exception getException();

    boolean hasError();

    boolean hasMore();

    boolean isLoading();

    void registerStateObserver(StateObserver paramStateObserver);

    void remove(int paramInt);

    boolean retry();

    void unregisterStateObserver(StateObserver paramStateObserver);
}
