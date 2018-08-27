package com.hec.app.framework.widget;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.hec.app.R;
import com.hec.app.entity.BulletinInfo;

import java.util.List;

/**
 * Created by hec on 2015/11/19.
 */
public class BulletinView extends LinearLayout {

    private static final String TAG = "LILITH";
    private Context mContext;
    private ViewFlipper viewFlipper;
    private View scrollTitleView;
    private Intent intent;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    //bindNotices();
                    break;
                case -1:
                    break;
            }
        }
    };

    /**
     * 构造
     *
     * @param context
     */
    public BulletinView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public BulletinView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    /**
     * 网络请求后返回公告内容进行适配
     */
    protected void bindNotices(List<BulletinInfo> list) {
        viewFlipper.removeAllViews();

        for (BulletinInfo b :
                list) {
            String text = b.getBulletinTitle();
            TextView textView = new TextView(mContext);
            textView.setGravity(Gravity.LEFT);
            textView.setText(text);
            textView.setTextAppearance(mContext, R.style.Bulletin_Title_Style);
            //textView.setOnClickListener(new NoticeTitleOnClickListener(mContext, String.valueOf(b.getBulletinID())));
            LayoutParams lp = new LinearLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            viewFlipper.addView(textView, lp);
        }
    }

    private void init() {
        bindLinearLayout();
        Message msg = new Message();
        msg.what = 1;
        mHandler.sendMessageDelayed(msg, 3000);
    }

    public void bindLinearLayout() {
        scrollTitleView = LayoutInflater.from(mContext).inflate(
                R.layout.scroll_bulletin_title, null);
        LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        addView(scrollTitleView, layoutParams);

        viewFlipper = (ViewFlipper) scrollTitleView.findViewById(R.id.flipper_scrollTitle);

        viewFlipper.setInAnimation(inFromUpAnimation(500));
        viewFlipper.setOutAnimation(outToDownAnimation(500));
        viewFlipper.startFlipping();
    }

    private static Animation inFromUpAnimation(int duration) {
        Animation inFromRight = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, +1.0f, Animation.RELATIVE_TO_PARENT, 0.0f
        );
        inFromRight.setDuration(duration);
        inFromRight.setInterpolator(new AccelerateInterpolator());
        return inFromRight;
    }

    private static Animation outToDownAnimation(int duration) {
        Animation outtoLeft = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, -1.0f
        );
        outtoLeft.setDuration(duration);
        outtoLeft.setInterpolator(new AccelerateInterpolator());
        return outtoLeft;
    }

    public void setPublicNotices(List<BulletinInfo> list) {
        bindNotices(list);
    }

    class NoticeTitleOnClickListener implements OnClickListener {
        private Context context;
        private String titleid;

        public NoticeTitleOnClickListener(Context context, String titleid) {
            this.context = context;
            this.titleid = titleid;
        }

        public void onClick(View v) {
            disPlayNoticeContent(context, titleid);
        }
    }

    public void disPlayNoticeContent(Context context, String titleid) {
        Toast.makeText(context, titleid, Toast.LENGTH_SHORT).show();
    }
}