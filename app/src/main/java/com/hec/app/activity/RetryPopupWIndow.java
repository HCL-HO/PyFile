package com.hec.app.activity;

import android.app.Activity;
import android.content.Context;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.GridLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.hec.app.R;
import com.hec.app.util.DisplayUtil;

/**
 * Created by wangxingjian on 2016/12/19.
 */

public class RetryPopupWIndow extends PopupWindow {
    private Context context;
    private TextView cancel,retry;
    private OnRetryListener onRetryListener;
    private CountDownTimer countDownTimer;

    public RetryPopupWIndow(Context context) {
        this.context = context;
        init();
    }

    public void setOnRetryListener(OnRetryListener onRetryListener){
        this.onRetryListener = onRetryListener;
    }

    private void init() {
        View contentView = LayoutInflater.from(context).inflate(
                R.layout.pop_retry, null);
        cancel = (TextView) contentView.findViewById(R.id.retry_cancel);
        retry = (TextView) contentView.findViewById(R.id.retry_go);
        this.setContentView(contentView);
        this.setWidth(DisplayUtil.getPxByDp(context, 320));
        this.setHeight(GridLayout.LayoutParams.WRAP_CONTENT);
        this.backgroundAlpha(Float.valueOf("0.5"));
        this.setFocusable(true);
        this.setBackgroundDrawable(context.getResources().getDrawable(android.R.color.white));
        this.setTouchable(true);
        this.setOutsideTouchable(false);
        retry.setClickable(false);
        this.update();
        countDownTimer = new CountDownTimer(5000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                retry.setText("请" + millisUntilFinished/1000 + "秒后重试");
            }

            @Override
            public void onFinish() {
                Log.i("speed","init4");
                retry.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                retry.setClickable(true);
                retry.setText("重试");
                retry.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismiss();
                        onRetryListener.retry();
                    }
                });
            }
        };
        countDownTimer.start();
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    @Override
    public void dismiss() {
        super.dismiss();
        WindowManager.LayoutParams lp = ((Activity)context).getWindow().getAttributes();
        lp.alpha = 1;
        ((Activity)context).getWindow().setAttributes(lp);
        if(countDownTimer!=null)
            countDownTimer.cancel();
    }

    public interface OnRetryListener{
        void retry();
    }

    public void backgroundAlpha(float bgAlpha)
    {
        WindowManager.LayoutParams lp = ((Activity)context).getWindow().getAttributes();
        lp.alpha = bgAlpha;
        ((Activity)context).getWindow().setAttributes(lp);
    }
}
