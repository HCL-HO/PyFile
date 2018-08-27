package com.hec.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hec.app.R;
import com.hec.app.config.CommonConfig;

import java.util.List;
import java.util.Map;

/**
 * Created by techbeck on 2016/1/20.
 */
public class FindPasswordAdapter extends BaseAdapter {
    private List<Map<String, Object>> data;
    private LayoutInflater layoutInflater;
    private Context context;

    private class Holder{
       public ImageView ItemImage;
       public TextView ItemTitle;
       public TextView ItemInfo;
    }
    public FindPasswordAdapter(Context context, List<Map<String, Object>> data){
        this.context = context;
        this.data = data;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount(){
        return data.size();
    }

    @Override
    public Object getItem(int position){
        return data.get(position);
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder = null;
        if (convertView == null) {
            holder = new Holder();
            convertView = layoutInflater.inflate(R.layout.list_find_password,null);
            holder.ItemImage =(ImageView) convertView.findViewById(R.id.ItemImage);
            holder.ItemTitle = (TextView)convertView.findViewById(R.id.ItemTitle);
            holder.ItemInfo = (TextView)convertView.findViewById(R.id.ItemInfo);
            convertView.setTag(holder);
        }
        else {
            holder = (Holder)convertView.getTag();
        }

        holder.ItemImage.setBackgroundResource((Integer)data.get(position).get(CommonConfig.MAP_FIND_PASSWORD_IMAGE));
        holder.ItemTitle.setText((String)data.get(position).get(CommonConfig.MAP_FIND_PASSWORD_TITLE));
        holder.ItemInfo.setText((String)data.get(position).get(CommonConfig.MAP_FIND_PASSWORD_INFO));
        return convertView;
    }
}
