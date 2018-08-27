package com.hec.app.framework.widget;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.hec.app.R;


/**
 * MyToast for display message after action executed.
 *
 */
public class MyToast {


    static Toast toast;
    public static int count = 0;
    /*
     * show toast message after action executed. the toast display time is short
     * default.
     *
     * @param mContext
     * @param message
     */
    public static void show(Context mContext, String message) {
        if(message.contains("无法连接网络")||message.contains("不稳定")||message.contains("网络异常")||message.contains("期号")){
            if(count==0){
                count++;
                show(mContext, message, Toast.LENGTH_SHORT, Gravity.CENTER, 0, 0);
            }else{

            }
        }else{
            if(isShowing()){
                cancel();
            }else{
                show(mContext, message, Toast.LENGTH_SHORT, Gravity.CENTER, 0, 0);
            }
        }
    }

    /**
     * show toast message after action executed.
     *
     * @param mContext
     * @param message
     * @param duration
     */
    public static void show(Context mContext, String message, int duration) {

        show(mContext, message, duration, Gravity.CENTER, 0, 0);
    }

    /**
     *  show toast message after action executed
     *
     * @param mContext
     * @param message
     * 			the message to be shown in toast
     * @param duration
     * 			the toast show time length
     * @param gravity
     * 			the gravity of toast
     * @param xOffset
     * 			the x coordinate
     * @param yOffset
     * 			the y coordinate
     */
    public static void show(Context mContext, String message, int duration,int gravity,int xOffset,int yOffset){

        if (mContext == null) {
            return;
        }

        View viewContainer = LayoutInflater.from(mContext).inflate(R.layout.common_toast, null);
        TextView textView = (TextView) viewContainer.findViewById(R.id.common_toast_textview_message);
        textView.setText(message);

        toast = new Toast(mContext);
        toast.setGravity(gravity, xOffset, yOffset);
        toast.setView(viewContainer);
        toast.setDuration(duration);
        toast.show();
    }

    public static boolean isShowing(){
        if(toast != null){
            return toast.getView().isShown();
        }
        return false;
    }

    public static void cancel(){
        if(toast!=null){
            toast.cancel();
        }
    }
}