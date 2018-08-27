
package com.hec.app.framework.widget;

import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

public class StrikethroughTextView extends TextView {
    public StrikethroughTextView(Context context) {
        super(context);
        init();
    }

    public StrikethroughTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public StrikethroughTextView(Context context, AttributeSet attrs,
            int defStyle) {
        super(context, attrs, defStyle);
        init();
    }
    
    private void init() {
        setPaintFlags(getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
    }
}