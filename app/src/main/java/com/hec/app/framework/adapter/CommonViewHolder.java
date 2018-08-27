package com.hec.app.framework.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by wangxingjian on 16/1/25.
 */
public abstract class CommonViewHolder<T> extends RecyclerView.ViewHolder {
    public CommonViewHolder(Context context, ViewGroup root, int layoutRes) {
        super(LayoutInflater.from(context).inflate(layoutRes, root, false));
        initView();
    }

    public Context getContext() {
        return itemView.getContext();
    }

    /**
     * 用给定的 data 对 holder 的 view 进行赋值
     */
    public abstract void bindData(T t);

    protected abstract void initView();
    //public void setData(T t) {
    //    bindData(t);
    //}
}
