package com.hec.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.hec.app.R;
import com.hec.app.entity.LotteryInfo;
import com.hec.app.framework.adapter.DragAdapter;
import com.hec.app.util.LotteryUtil;

import java.util.List;

/**
 * Created by hec on 2015/12/25.
 */
public class MyDragAdapter extends DragAdapter<LotteryInfo> {
    private ImageView img_item;
    /**
     * 要删除的position
     */
    public int remove_position = -1;

    public MyDragAdapter(Context context, List<LotteryInfo> list) {
        super(context, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.subscribe_lottery_item, null);
        img_item = (ImageView) view.findViewById(R.id.img_item);
        LotteryInfo channel = getItem(position);
        img_item.setImageResource(LotteryUtil.getLotteryIcon(lotteryTypeList.get(position).getLotteryType()));
        //img_item.setImageResource(R.mipmap.icon_3d);
        if ((position == 0) || (position == 1)) {
            img_item.setEnabled(false);
        }
        if (isChanged && (position == holdPosition) && !isItemShow) {
            img_item.setSelected(true);
            img_item.setEnabled(true);
            isChanged = false;
        }
        if (!isVisible && (position == -1 + lotteryTypeList.size())) {
            img_item.setSelected(true);
            img_item.setEnabled(true);
        }
        if (remove_position == position) {
            // item_text.setText("");
        }
        return view;
    }
}
