package com.hec.app.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashMap;
import com.hec.app.R;
/**
 * Created by techbeck on 2015/12/30.
 */
public class RechargeAdapter extends BaseAdapter {
    private ArrayList<HashMap<String, Object>> mAppList;
    private LayoutInflater mInflater;
    private Context mContext;
    private String[] keyString;
    private int[] valueViewID;
    private ItemView  listViewItem;

    private class ItemView{
        ImageView ItemImage;
        TextView ItemName;
        TextView ItemInfo;
        ImageView img_arrow;
    }

    public RechargeAdapter(Context c, ArrayList<HashMap<String, Object>> AppList, int resourse, String[] from, int[] to){
        mAppList = AppList;
        mContext = c;
        mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        keyString = new String[from.length];
        valueViewID = new int[to.length];
        System.arraycopy(from, 0, keyString, 0, from.length);
        System.arraycopy(to, 0, valueViewID, 0, to.length);
    }

    @Override
    public int getCount(){
        return  mAppList.size();
    }
    @Override
    public Object getItem(int position){
        return mAppList.get(position);
    }

    @Override
    public long getItemId(int position){
        return  position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        if(convertView != null){
            listViewItem = (ItemView)convertView.getTag();
        }else{
            convertView = mInflater.inflate(R.layout.list_item_recharge_main, null);
            listViewItem = new ItemView();
            listViewItem.ItemImage = (ImageView)convertView.findViewById(valueViewID[0]);
            listViewItem.ItemName = (TextView)convertView.findViewById(valueViewID[1]);
            listViewItem.ItemInfo = (TextView) convertView.findViewById(valueViewID[2]);
            listViewItem.img_arrow = (ImageView)convertView.findViewById(valueViewID[3]);
            convertView.setTag( listViewItem);
        }

        HashMap<String, Object> appInfo = mAppList.get(position);
        if(appInfo != null){

            int mid = (Integer)appInfo.get(keyString[0]);
            int name = (Integer)appInfo.get(keyString[1]);
            int info = (Integer)appInfo.get(keyString[2]);
            int bid = (Integer)appInfo.get(keyString[3]);
            listViewItem.ItemImage.setImageDrawable( listViewItem.ItemImage.getResources().getDrawable(mid));
            listViewItem.ItemName.setText(name);
            listViewItem.ItemName.setTextSize(18);
            listViewItem.ItemInfo.setText(info);
            listViewItem.ItemInfo.setTextSize(12);
            listViewItem.img_arrow.setImageDrawable(listViewItem.img_arrow.getResources().getDrawable(bid));
            //listViewItem.img_arrow.setOnClickListener(new ItemButton_Click(position));
        }
        return convertView;
    }
/*
    class ItemButton_Click implements View.OnClickListener {
        private int position;

        ItemButton_Click(int pos){
            position = pos;
        }

        @Override
        public void onClick(View v){
            int vid = v.getId();
            if (vid ==  listViewItem.img_arrow.getId()){
                Log.v("ola_log", String.valueOf(position));
            }
        }
    }*/
}
