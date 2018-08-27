package com.hec.app.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hec.app.R;
import com.nostra13.universalimageloader.utils.L;

public class CustomerServiceAlertDialog extends Dialog implements View.OnClickListener {
    private Context context;
    private CustomerServiceAlertDialogListener listener;

    public interface CustomerServiceAlertDialogListener {
        void onClick();
    }

    public CustomerServiceAlertDialog(Context context, CustomerServiceAlertDialogListener listener) {
        super(context, android.R.style.Theme_Light_NoTitleBar_Fullscreen);
        this.context = context;
        this.listener = listener;
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_view, null);
        setContentView(view);
        initView(view);
    }

    private void initView(View view) {
        LinearLayout confirmBtn = (LinearLayout) view.findViewById(R.id.confirm_btn);
        TextView cancelBtn = (TextView) view.findViewById(R.id.cancel_btn);
        confirmBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.confirm_btn:
                if (listener != null) {
                    listener.onClick();
                    dismiss();
                }
                break;
            case R.id.cancel_btn:
                dismiss();
                break;
            default:
                dismiss();
                break;
        }
    }
}
