package com.hec.app.framework.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;

import com.hec.app.R;


public class CircleButton extends Button {
    private boolean word = false;
    private Boolean isSelected = false;
    private OnSelectedChangeListener onSelectedChangeListener;
    private int mNormalBackground;
    private int mSelectedBackground;
    private Context context;
    private PopupWindow popupWindow;

    public Boolean getIsSelected() {
        return isSelected;
    }

    public void setOnSelectedChangeListener(OnSelectedChangeListener listener) {
        this.onSelectedChangeListener = listener;
    }

    public void setIsSelected(Boolean isSelected) {
        this.isSelected = isSelected;
        if (isSelected) {
            setTextColor(Color.parseColor("white"));
            if (mSelectedBackground == 0 && !word) {
                setBackgroundResource(R.mipmap.number_selected);
            } else if(mSelectedBackground == 0 && word){
                setBackgroundResource(R.mipmap.word_selected);
            } else {
                setBackgroundResource(mSelectedBackground);
            }
        } else {
            setTextColor(Color.parseColor("white"));

            if (mNormalBackground == 0 && !word) {
                setBackgroundResource(R.mipmap.number_normal);
            } else if(mNormalBackground == 0 && word){
                setBackgroundResource(R.mipmap.word_normal);
            } else {
                setBackgroundResource(mNormalBackground);
            }
        }
    }

    public CircleButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.CircleButton);
        mNormalBackground = a.getResourceId(R.styleable.CircleButton_normalBackground, 0);
        mSelectedBackground = a.getResourceId(R.styleable.CircleButton_selectedBackground, 0);
        init();
    }

    public CircleButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleButton(Context context) {
        this(context, null);
    }

    private void init() {
        setIsSelected(false);
        setTextColor(Color.parseColor("white"));
        setGravity(Gravity.CENTER);

        if (mNormalBackground == 0 && !word) {
            setBackgroundResource(R.mipmap.number_normal);
        } else if(mNormalBackground == 0 && word){
            setBackgroundResource(R.mipmap.word_normal);
        }else {
            setBackgroundResource(mNormalBackground);
        }

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!getIsSelected()) {
                    setIsSelected(true);
                } else {
                    setIsSelected(false);
                }
                if (CircleButton.this.onSelectedChangeListener != null) {
                    CircleButton.this.onSelectedChangeListener.onChange(v);
                }
            }
        });

//        setOnTouchListener(new OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if (event.getAction() == MotionEvent.ACTION_DOWN) {
//                    popupWindow = new BallPopupWindow((Activity) context);
//                    int[] location = new int[2];
//                    v.getLocationOnScreen(location);
//                    popupWindow.showAtLocation(v, Gravity.NO_GRAVITY, location[0] - 5, location[1] - 374 + v.getHeight());
//                } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
//                    popupWindow.dismiss();
//                }


//                return false;
//            }
//        });
    }
    public boolean isWord() {
        return word;
    }
    public void setWord(boolean word) {
        this.word = word;
        init();
    }
    public interface OnSelectedChangeListener {
        void onChange(View v);
    }
}


