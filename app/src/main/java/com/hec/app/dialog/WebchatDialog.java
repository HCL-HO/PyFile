package com.hec.app.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.hec.app.R;
import com.hec.app.config.CommonConfig;

/**
 * Created by jhezenhu on 2018/1/29.
 */

public class WebchatDialog extends Dialog {
    private Context context;
    private OnGoToWebChatListener onGoToWebChatListener;

    public interface OnGoToWebChatListener {
        void onClick(int type);
    }

    public WebchatDialog(@NonNull Context context, OnGoToWebChatListener onGoToWebChatListener) {
        super(context, android.R.style.Theme_Light_NoTitleBar_Fullscreen);
        this.context = context;
        this.onGoToWebChatListener = onGoToWebChatListener;
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_webchat, null);
        setContentView(view);
        initView(view);
    }

    private void initView(View view) {
        RelativeLayout bgBtn = (RelativeLayout) view.findViewById(R.id.bg_rl);
        bgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        RelativeLayout webchatBtn = (RelativeLayout) view.findViewById(R.id.webchat_rl);
        webchatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onGoToWebChatListener != null) {
                    onGoToWebChatListener.onClick(CommonConfig.WEBCHAT_TYPE_NORMAL);
                }
                dismiss();
            }
        });

        RelativeLayout webchatVipBtn = (RelativeLayout) view.findViewById(R.id.webchat_vip_rl);
        webchatVipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onGoToWebChatListener != null) {
                    onGoToWebChatListener.onClick(CommonConfig.WEBCHAT_TYPE_VIP);
                }
                dismiss();
            }
        });
    }
}
