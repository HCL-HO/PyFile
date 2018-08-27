package com.hec.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.hec.app.R;
import com.hec.app.entity.LotteryInfo;
import com.hec.app.util.LotteryUtil;

import java.util.List;

/**
 * Created by hec on 2015/12/25.
 */
public class OtherAdapter extends BaseAdapter {
    private Context context;
    public List<LotteryInfo> channelList;
    private ImageView img_item;
    /**
     * 是否可见
     */
    boolean isVisible = true;
    /**
     * 要删除的position
     */
    public int remove_position = -1;

    public OtherAdapter(Context context, List<LotteryInfo> channelList) {
        this.context = context;
        this.channelList = channelList;
    }

    @Override
    public int getCount() {
        return channelList == null ? 0 : channelList.size();
    }

    @Override
    public LotteryInfo getItem(int position) {
        if (channelList != null && channelList.size() != 0) {
            return channelList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.subscribe_lottery_item, null);
        img_item = (ImageView) view.findViewById(R.id.img_item);
        LotteryInfo item = getItem(position);
        img_item.setImageResource(LotteryUtil.getLotteryIcon(item.getLotteryType()));
        if (!isVisible && (position == -1 + channelList.size())) {

        }
        if (remove_position == position) {

        }
        return view;
    }

    /**
     * 获取频道列表
     */
    public List<LotteryInfo> getChannnelLst() {
        return channelList;
    }

    /**
     * 添加频道列表
     */
    public void addItem(LotteryInfo channel) {
        channelList.add(channel);
        notifyDataSetChanged();
    }

    /**
     * 设置删除的position
     */
    public void setRemove(int position) {
        remove_position = position;
        notifyDataSetChanged();
        // notifyDataSetChanged();
    }

    /**
     * 删除频道列表
     */
    public void remove() {
        if(remove_position != -1) {
            channelList.remove(remove_position);
        }
        remove_position = -1;
        notifyDataSetChanged();
    }

    /**
     * 设置频道列表
     */
    public void setListDate(List<LotteryInfo> list) {
        channelList = list;
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
}
