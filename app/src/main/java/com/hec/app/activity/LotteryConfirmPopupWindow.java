package com.hec.app.activity;

import android.app.Activity;
import android.content.Context;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.hec.app.R;
import com.hec.app.util.DisplayUtil;

/**
 * Created by hec on 2015/11/10.
 */
public class LotteryConfirmPopupWindow extends PopupWindow {
    private Activity context;
    private String lotteryType;
    private String playTypeName;
    private String playTypeRadioName;
    private double amount;
    private TextView amok;
    private OnConfirmedListener listener;

    public interface OnConfirmedListener {
        void onConfirmed();
    }

    public void setOnConfirmedListener(OnConfirmedListener listener) {
        this.listener = listener;
    }

    public LotteryConfirmPopupWindow(Activity context, String lotteryType, String playTypeName, String playTypeRadioName, double amount) {
        this.context = context;
        this.lotteryType = lotteryType;
        this.playTypeName = playTypeName;
        this.playTypeRadioName = playTypeRadioName;
        this.amount = amount;
        init();
    }

    private void init() {
        View contentView = LayoutInflater.from(context).inflate(
                R.layout.pop_lottery_confirm, null);
        TextView tv = (TextView) contentView.findViewById(R.id.I_am_chose);
        TextPaint tp = tv.getPaint();
        tp.setFakeBoldText(true);
        LinearLayout ll_ok = (LinearLayout) contentView.findViewById(R.id.ll_ok);
        LinearLayout ll_cancel = (LinearLayout) contentView.findViewById(R.id.ll_cancel);
        ll_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onConfirmed();
                }
                LotteryConfirmPopupWindow.this.dismiss();
            }
        });
        ll_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LotteryConfirmPopupWindow.this.dismiss();
            }
        });
        this.setContentView(contentView);
        this.setWidth(DisplayUtil.getPxByDp(context, 320));
        this.setHeight(GridLayout.LayoutParams.WRAP_CONTENT);

        this.setFocusable(true);
        this.setBackgroundDrawable(context.getResources().getDrawable(android.R.color.white));
        this.setTouchable(true);
        this.setOutsideTouchable(true);
        this.update();

        TextView tvLotteryType = (TextView) contentView.findViewById(R.id.tvLotteryType);
        TextView tvTotalAmount = (TextView) contentView.findViewById(R.id.tvTotalAmount);
        tvLotteryType.setText(String.format("玩法：%1$s-%2$s-%3$s", this.lotteryType.replace("和盛","聚星"), this.playTypeName, this.playTypeRadioName));
        tvTotalAmount.setText(String.format("单期总金额：%1$.4f", this.amount));
    }

    public void backgroundAlpha(float bgAlpha)
    {
        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        lp.alpha = bgAlpha;
        context.getWindow().setAttributes(lp);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        lp.alpha = 1;
        context.getWindow().setAttributes(lp);
    }
}

