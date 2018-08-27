package com.hec.app.framework.adapter;

import android.content.Context;
import android.util.Log;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by hec on 2015/12/25.
 */
public abstract class DragAdapter<T> extends BaseAdapter {
    /**
     * TAG
     */
    private final static String TAG = "DragAdapter";
    /**
     * 是否显示底部的ITEM
     */
    protected boolean isItemShow = false;
    protected Context context;
    public List<T> lotteryTypeList;

    /**
     * 控制的postion
     */
    protected int holdPosition;
    /**
     * 是否改变
     */
    protected boolean isChanged = false;
    /**
     * 是否可见
     */
    protected boolean isVisible = true;

    /**
     * TextView 频道内容
     */
    private TextView item_text;
    /**
     * 要删除的position
     */
    public int remove_position = -1;

    public DragAdapter(Context context, List<T> channelList) {
        this.context = context;
        this.lotteryTypeList = channelList;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return lotteryTypeList == null ? 0 : lotteryTypeList.size();
    }

    @Override
    public T getItem(int position) {
        if (lotteryTypeList != null && lotteryTypeList.size() != 0) {
            return lotteryTypeList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * 添加频道列表
     */
    public void addItem(T channel) {
        lotteryTypeList.add(channel);
        notifyDataSetChanged();
    }

    /**
     * 拖动变更频道排序
     */
    public void exchange(int dragPostion, int dropPostion) {
        holdPosition = dropPostion;
        T dragItem = getItem(dragPostion);
        Log.d(TAG, "startPostion=" + dragPostion + ";endPosition=" + dropPostion);
        if (dragPostion < dropPostion) {
            lotteryTypeList.add(dropPostion + 1, dragItem);
            lotteryTypeList.remove(dragPostion);
        } else {
            lotteryTypeList.add(dropPostion, dragItem);
            lotteryTypeList.remove(dragPostion + 1);
        }
        isChanged = true;
        notifyDataSetChanged();
    }

    /**
     * 获取频道列表
     */
    public List<T> getLotteryTypeList() {
        return lotteryTypeList;
    }

    /**
     * 设置删除的position
     */
    public void setRemove(int position) {
        remove_position = position;
        notifyDataSetChanged();
    }

    /**
     * 删除频道列表
     */
    public void remove() {
        if(remove_position != -1) {
            lotteryTypeList.remove(remove_position);
        }
        remove_position = -1;
        notifyDataSetChanged();
    }

    /**
     * 设置频道列表
     */
    public void setLotteryTypeList(List<T> list) {
        lotteryTypeList = list;
    }

    /**
     * 获取是否可见
     */
    public boolean isVisible() {
        return isVisible;
    }

    /**
     * 设置是否可见
     */
    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    /**
     * 显示放下的ITEM
     */
    public void setShowDropItem(boolean show) {
        isItemShow = show;
    }
}
