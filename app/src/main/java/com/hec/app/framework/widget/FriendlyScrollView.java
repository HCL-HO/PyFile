package com.hec.app.framework.widget;

import android.content.Context;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;

public class FriendlyScrollView extends android.widget.ScrollView {
	private GestureDetector gestureDetector;

	public FriendlyScrollView(Context context) {
		super(context);
	}

	public FriendlyScrollView(Context context, android.util.AttributeSet attrs) {
		super(context, attrs);
		gestureDetector = new GestureDetector(new YScrollDetector());
	}

	public FriendlyScrollView(Context context, android.util.AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// Call super first because it does some hidden motion event
		// handling
		boolean result = super.onInterceptTouchEvent(ev);
		// Now see if we are scrolling vertically with the custom gesture
		// detector
		if (gestureDetector.onTouchEvent(ev)) {
			return result;
		}
		// If not scrolling vertically (more y than x), don't hijack the
		// event.
		else {
			return false;
		}
	}

	static class YScrollDetector extends SimpleOnGestureListener {
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			try {
				return Math.abs(distanceY) > Math.abs(distanceX);
			} catch (Exception e) {
				// nothing
			}
			return false;
		}
	}
}