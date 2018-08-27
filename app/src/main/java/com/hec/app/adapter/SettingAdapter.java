package com.hec.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;


import com.hec.app.R;
import com.hec.app.entity.ObjectItem;

import java.util.ArrayList;

// here's our beautiful adapter
public class SettingAdapter extends ArrayAdapter<ObjectItem> implements Switch.OnCheckedChangeListener {
    public interface SettingAdapterListener {
        void onSwitchStatusChanged(String head, boolean isTurnOn);
    }

    private Context mContext;
    private ArrayList<ObjectItem> data = null;
    private SettingAdapterListener listener;

    public SettingAdapter(Context mContext, ArrayList<ObjectItem> data, SettingAdapterListener listener) {

        super(mContext, 0, data);
        this.mContext = mContext;
        this.data = data;
        this.listener = listener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_setting, parent, false);
        }
        TextView head = (TextView) convertView.findViewById(R.id.head);
        TextView tail = (TextView) convertView.findViewById(R.id.tail);
        ImageView arrow = (ImageView) convertView.findViewById((R.id.arrow));
        Switch notice = (Switch) convertView.findViewById(R.id.notice_switch);

        notice.setVisibility(View.VISIBLE);
        arrow.setVisibility(View.VISIBLE);

        ObjectItem objectItem = getItem(position);
        if (!objectItem.isHasSwitch()) {
            notice.setVisibility(View.GONE);
        } else {
            notice.setChecked(objectItem.isSwitchStatus());
            notice.setOnCheckedChangeListener(this);
            notice.setTag(objectItem.getHead());
        }
        if (!objectItem.isHasArrow()) {
            arrow.setVisibility(View.GONE);
        }

        head.setText(objectItem.getHead());

        tail.setText(objectItem.getTail());

        arrow.setImageResource(R.mipmap.icon_arrow_right);

        return convertView;

    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (listener != null) {
            listener.onSwitchStatusChanged((String) compoundButton.getTag(), b);
        }
    }
}
