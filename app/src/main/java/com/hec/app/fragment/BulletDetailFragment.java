package com.hec.app.fragment;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.hec.app.R;
import com.hec.app.entity.BulletinInfo;
import com.hec.app.util.DateTransmit;

import java.io.Serializable;

/**
 * A placeholder fragment containing a simple view.
 */
public class BulletDetailFragment extends Fragment {
    TextView mTitle,mTime,mContent;
    ScrollView mScrollView;
    public BulletDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_bullet_detail, container, false);
        BulletinInfo iteminfo = (BulletinInfo) getActivity().getIntent().getSerializableExtra("iteminfo");
        mTitle = (TextView) v.findViewById(R.id.bullet_detail_title);
        mTime = (TextView) v.findViewById(R.id.bullet_detail_time);
        mContent = (TextView) v.findViewById(R.id.bullet_detail_content);
        mTitle.setText(iteminfo.getBulletinTitle());
        mContent.setText(Html.fromHtml(iteminfo.getBulletinText()));
        mContent.setMovementMethod(ScrollingMovementMethod.getInstance());
        String dateStr = DateTransmit.dateTransmits(iteminfo.getBulletinTime());
        mTime.setText(dateStr);
        return v;
    }
}
