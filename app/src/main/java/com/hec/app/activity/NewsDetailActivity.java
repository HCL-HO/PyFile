package com.hec.app.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.hec.app.R;
import com.hec.app.entity.BizException;
import com.hec.app.entity.NewsInfo;
import com.hec.app.framework.content.CBContentResolver;
import com.hec.app.framework.content.ContentStateObserver;
import com.hec.app.webservice.NewsService;
import com.hec.app.webservice.ServiceException;

import java.io.IOException;

public class NewsDetailActivity extends AppCompatActivity {
    private CBContentResolver<NewsInfo> mResolver;
    private ContentStateObserver mObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.mipmap.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewsDetailActivity.this.finish();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        int id = getIntent().getIntExtra("rid", 0);

        getNewsDetailInfo(id);
    }

    private void getNewsDetailInfo(final int id) {
        mResolver = new CBContentResolver<NewsInfo>() {

            @Override
            public NewsInfo query() throws IOException,
                    ServiceException, BizException {
                return new NewsService().getNewsDetail(id);
            }

            @Override
            public void onLoaded(NewsInfo result) {
                if(NewsDetailActivity.this == null || NewsDetailActivity.this.isFinishing())
                    return;
                if (result != null) {
                    TextView titleView = (TextView) findViewById(R.id.detailTitle);
                    TextView contentView = (TextView) findViewById(R.id.detailContent);
                    TextView sourceView = (TextView) findViewById(R.id.source);
                    TextView dateView = (TextView) findViewById(R.id.detailDate);
                    titleView.setText(result.getTitle());
                    contentView.setText(result.getContent());
                    sourceView.setText(result.getSource());
                    dateView.setText(result.getInDate());
                }
            }

        };

        mObserver = new ContentStateObserver();
        mObserver.setView(
                getWindow().getDecorView().findViewById(android.R.id.content),
                R.id.newsDetailLayout, R.id.loading, R.id.error);
        mObserver.setResolver(mResolver);
        mResolver.setVisible(true);
        mResolver.startQuery();
    }
}
