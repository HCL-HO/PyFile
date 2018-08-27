package com.hec.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hec.app.R;
import com.hec.app.entity.NewsInfo;

import java.util.List;

/**
 * Created by hec on 2015/10/16.
 */
public class NewsAdapter extends ArrayAdapter<NewsInfo> {
    int resource;

    public NewsAdapter(Context context, int _resource, List<NewsInfo> items){
        super(context, _resource, items);
        resource = _resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        LinearLayout newView;
        NewsInfo news = getItem(position);
        String title = news.getTitle();
        String summary = news.getSummary();
        if(convertView == null){
            newView = new LinearLayout(getContext());
            String inflater = Context.LAYOUT_INFLATER_SERVICE;
            LayoutInflater li;
            li = (LayoutInflater)getContext().getSystemService(inflater);
            li.inflate(resource, newView, true);
        }
        else{
            newView = (LinearLayout)convertView;
        }

        TextView titleView = (TextView)newView.findViewById(R.id.titleTextView);
        TextView contentView = (TextView)newView.findViewById(R.id.contentTextView);

        titleView.setText(title);
        contentView.setText(summary);

        return newView;
    }


}
