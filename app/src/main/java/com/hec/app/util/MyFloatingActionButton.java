package com.hec.app.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;

import com.github.clans.fab.FloatingActionButton;
import com.hec.app.R;

/**
 * Created by wangxingjian on 2017/4/11.
 */

public class MyFloatingActionButton extends FloatingActionButton {
    private Context context;
    private int width,height;
    private int ori_left,ori_right,ori_top,ori_buttom;
    private boolean no_onlayout_anymore = false;

    public MyFloatingActionButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        Log.i("wxj","chat init2");
    }

    public MyFloatingActionButton(Context context) {
        super(context);
        this.context = context;
        Log.i("wxj","chat init");
    }

    public void getWindow(){
        Display display = ((Activity)context).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;
    }
    private int oriX,oriY,pointX,pointY,oriRawX,oriRawY;
    private boolean clickable;
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                pointX = (int) event.getRawX();
                pointY = (int) event.getRawY();
                oriRawX = (int) event.getRawX();
                oriRawY = (int) event.getRawY();
                oriX = (int) getX();
                oriY = (int) getY();
                clickable = true;
                Log.i("wxj","chat down");
                break;

            case MotionEvent.ACTION_MOVE:
                bringToFront();
                int rawX = oriRawX - (int)event.getRawX();
                int rawY = oriRawY - (int)event.getRawY();
                if(rawX > 100 || rawY > 100 || rawX < -100 || rawY < -100){
                    clickable = false;
                }

                int currentX = oriX + (int)event.getRawX() - pointX;
                int maxWidth = width - this.getWidth();
                if (currentX > maxWidth) {
                    this.setX(maxWidth);
                }
                else if (currentX < 0) {
                    this.setX(0);
                }
                else {
                    this.setX(currentX);
                }

                int currentY = oriY + (int)event.getRawY() - pointY;
                int maxHeight = height - this.getHeight();
                int toolbarHeight = (int)getResources().getDimension(R.dimen.abc_action_bar_default_height_material);
                if (currentY > maxHeight) {
                    this.setY(maxHeight);
                }
                else if (currentY < toolbarHeight) {
                    this.setY(toolbarHeight);
                }
                else {
                    this.setY(currentY);
                }
                return true;

            case MotionEvent.ACTION_UP:
                bringToFront();
                no_onlayout_anymore = true;

                if (event.getRawX() > width/2) {
                    this.setX(width-this.getWidth());
                }
                else {
                    this.setX(0);
                }

                if(!clickable){
                    Log.i("wxj","chat up unclick width = " + width);
                    return true;
                }
                else{
                    Log.i("wxj","chat up click");
                }
                break;

            case MotionEvent.ACTION_HOVER_MOVE:
                break;
        }
        return super.dispatchTouchEvent(event);
    }
}
