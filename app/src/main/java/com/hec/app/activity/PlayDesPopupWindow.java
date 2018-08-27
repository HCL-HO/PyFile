package com.hec.app.activity;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.hec.app.R;

/**
 * Created by hec on 2015/11/10.
 */
public class PlayDesPopupWindow extends PopupWindow {
    private Activity context;
    private String playDescription;
    private String lotteryExample;


    public PlayDesPopupWindow(Activity context, String playDescription, String lotteryExample) {
        this.context = context;
        this.playDescription = playDescription;
        this.lotteryExample = lotteryExample;

        init();
    }

    private void init() {
        View contentView = LayoutInflater.from(context).inflate(
                R.layout.pop_play_description, null);
        LinearLayout linearLayout = (LinearLayout) contentView.findViewById(R.id.popLayout);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlayDesPopupWindow.this.dismiss();
            }
        });
        this.setContentView(contentView);
        this.setWidth(GridLayout.LayoutParams.MATCH_PARENT);
        this.setHeight(GridLayout.LayoutParams.WRAP_CONTENT);

        this.setFocusable(true);
        this.setBackgroundDrawable(context.getResources().getDrawable(android.R.color.white));
        this.setTouchable(true);
        this.setOutsideTouchable(true);
        this.update();

        TextView tvDescription = (TextView) contentView.findViewById(R.id.tvPlayDescription);
        tvDescription.setText(playDescription);
        if(lotteryExample == null){
            LinearLayout ll = (LinearLayout) contentView.findViewById(R.id.example);
            ll.setVisibility(View.GONE);

        }else {
            TextView tvExample = (TextView) contentView.findViewById(R.id.tvLotteryExample);
            tvExample.setText(lotteryExample);
        }
    }
}

