package com.hec.app.framework.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;

import com.hec.app.R;


public class MyDialog extends Dialog{

	public MyDialog(Context context,View contentView) {
		this(context, contentView, R.style.custom_dialog_style);
	}

	public MyDialog(Context context, View contentView,int theme){
		super(context,theme);
		this.setCancelable(true);
		this.setCanceledOnTouchOutside(true);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(contentView);
	}
}
