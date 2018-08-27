package com.hec.app.framework.widget;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;

import com.hec.app.R;
import com.hec.app.util.DisplayUtil;


public class MyMessageBox {

	public static void show(Activity context, String message) {
		View parent = context.findViewById(android.R.id.content);
		int titleHeight = DisplayUtil.getPxByDp(context, 70);
		try {
			LinearLayout layout=(LinearLayout)context.getLayoutInflater().inflate(R.layout.my_message_box_layout, null);
			TextView textView=(TextView)layout.findViewById(R.id.my_message_box_textview);
			textView.setText(message);
			final PopupWindow popupWindow = new PopupWindow(layout, android.view.ViewGroup.LayoutParams.FILL_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, true);
			popupWindow.setBackgroundDrawable(new BitmapDrawable());
			popupWindow.showAtLocation(parent, Gravity.TOP, 0, titleHeight);

			popupWindow.setOnDismissListener(new OnDismissListener() {

				@Override
				public void onDismiss() {

				}
			});

			popupWindow.setTouchInterceptor(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {

					if (popupWindow != null && popupWindow.isShowing()) {
						popupWindow.dismiss();
					}
					return true;
				}
			});

		} catch (Exception e) {

		}
	}

}
