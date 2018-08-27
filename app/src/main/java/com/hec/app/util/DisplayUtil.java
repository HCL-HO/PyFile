package com.hec.app.util;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;

public class DisplayUtil {

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素) 
	 * @param context
	 * @param dp
	 * @return
	 */
	public static int getPxByDp(Context context, int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
	}
	
	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp 
	 * @param context
	 * @param px
	 * @return
	 */
	public static int getDpByPx(Context context, int px){
		
		 final float scale = context.getResources().getDisplayMetrics().density;  
		 return (int) (px / scale + 0.5f);  
	}
	
	/**
	 * 获取屏幕宽度
	 * 
	 * @param activity
	 * @return 屏幕宽度(像素)
	 */
	public static int getScreenWidth(Activity activity) {
		
		DisplayMetrics  dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		
		return dm.widthPixels;
	}

	/**
	 * 获取屏幕高度
	 * 
	 * @param activity
	 * @return 屏幕高度(像素)
	 */
	public static int getScreenHeight(Activity activity) {
		
		DisplayMetrics  dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		
		return dm.heightPixels;
	}

	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	/**
	 *  px תΪ dip
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	public static int getScale(Context context, int width, float pic_width){
		Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		Double val = new Double(width)/new Double(pic_width);
		val = val * 100d;
		return val.intValue();
	}
}
