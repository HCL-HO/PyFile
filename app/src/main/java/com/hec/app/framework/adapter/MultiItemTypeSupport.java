package com.hec.app.framework.adapter;

/**
 * Created by hec on 2015/11/11.
 */
public interface MultiItemTypeSupport<T>
{
    int getLayoutId(int position, T t);

    int getViewTypeCount();

    int getItemViewType(int postion, T t);
}