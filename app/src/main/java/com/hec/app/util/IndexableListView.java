/*
 * Copyright 2011 woozzu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hec.app.util;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;

public class IndexableListView extends ListView{
	
	private boolean mIsFastScrollEnabled = false;
	private IndexScroller mScroller = null;
	private GestureDetector mGestureDetector = null;
	private final int SWIPE_MIN_DISTANCE = 24;
	private int preLast;

	public IndexableListView(Context context) {
		super(context);
		if (mScroller != null)
			mScroller.show();
	}

	public IndexableListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		if (mScroller != null)
			mScroller.show();
	}

	public IndexableListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		if (mScroller != null)
			mScroller.show();
	}

	@Override
	public boolean isFastScrollEnabled() {
		return mIsFastScrollEnabled;
	}

	@Override
	public void setFastScrollEnabled(boolean enabled) {
		mIsFastScrollEnabled = enabled;
		if (mIsFastScrollEnabled) {
			if (mScroller == null) {
				mScroller = new IndexScroller(getContext(), this);
				mScroller.show();
			}
		} else {
			if (mScroller != null) {
				mScroller.hide();
				mScroller = null;
			}
		}
	}

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
		
		// Overlay index bar
		if (mScroller != null)
			mScroller.draw(canvas);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {

		// Intercept ListView's touch event
		if (mScroller != null && mScroller.onTouchEvent(ev)) {
			invalidate();
			return super.onTouchEvent(ev);
		}


		return super.onTouchEvent(ev);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (mScroller != null) {
			if (!mScroller.contains(ev.getX(), ev.getY()))
				//return true;
			mScroller.hide();
		}

		return super.onInterceptTouchEvent(ev);
	}

	@Override
	public void setAdapter(ListAdapter adapter) {
		super.setAdapter(adapter);
		if (mScroller != null)
			mScroller.setAdapter(adapter);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		if (mScroller != null)
			mScroller.onSizeChanged(w, h, oldw, oldh);
	}


	public void hidePreview() {
		if (mScroller != null)
			mScroller.hide();
	}

}