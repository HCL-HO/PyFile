package com.hec.app.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.hec.app.R;
import com.hec.app.util.TestUtil;

import android.content.SharedPreferences;

import junit.framework.Test;

import java.util.ArrayList;
import java.util.Arrays;

public class RmbAdapter extends ArrayAdapter<String>  {

    Context context;
    int layoutResourceId;
    ArrayList<String> data;
    private BtnClickListener mClickListener = null;
    private Filter filter = new KNoFilter();

    public RmbAdapter(Context context, int layoutResourceId, ArrayList<String> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }
    @Override
    public Filter getFilter() {
        return filter;
    }

    private class KNoFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence arg0) {
            FilterResults result = new FilterResults();
            result.values = data;
            result.count = data.size();
            return result;
        }

        @Override
        protected void publishResults(CharSequence arg0, FilterResults arg1) {
            notifyDataSetChanged();
        }
    }
    public void setListener( BtnClickListener listener){
        mClickListener = listener;

    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = convertView;
        final Holder holder = new Holder();
        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder.imgIcon = (ImageView)row.findViewById(R.id.btn_delete_rmb);
            holder.txtTitle = (TextView)row.findViewById(R.id.text1);
            holder.imgIcon.setVisibility(View.GONE);
            if(data.size() > position) {
                holder.txtTitle.setText(data.get(position));
                holder.imgIcon.setVisibility(View.VISIBLE);
                holder.imgIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mClickListener != null)
                            mClickListener.onBtnClick(position);
                    }
                });

            }

        }
        return row;
    }

    static class Holder
    {
        ImageView imgIcon;
        TextView txtTitle;
    }
    public interface BtnClickListener {
        void onBtnClick(int position);
    }
}