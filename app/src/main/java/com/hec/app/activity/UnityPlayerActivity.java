package com.hec.app.activity;

import android.app.Activity;
import android.app.Fragment;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;


import com.hec.app.R;
import com.hec.app.util.DisplayUtil;
import com.hec.app.util.IntentUtil;
import com.unity3d.player.*;

public class UnityPlayerActivity extends Activity
{
	protected static UnityPlayer mUnityPlayer; // don't change the name of this variable; referenced from native code

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_unity);
		Log.i("unitys","unityplayer");
		Button back = (Button) findViewById(R.id.back);
		back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				IntentUtil.redirectToNextActivity(UnityPlayerActivity.this, HomeActivity.class, new Bundle());
				Log.i("unitys","unityplayer back");
			}
		});
		FrameLayout layout = (FrameLayout) findViewById( R.id.container);
		getWindow().setFormat(PixelFormat.RGBX_8888);
		mUnityPlayer = new UnityPlayer(this);
		int glesMode = mUnityPlayer.getSettings().getInt("gles_mode", 1);
		mUnityPlayer.init(glesMode, false);
		layout.addView(mUnityPlayer, 0);
		layout.getLayoutParams().width = DisplayUtil.getScreenWidth(this);
		layout.getLayoutParams().height = DisplayUtil.getScreenWidth(this) / 693 * 390;
	}

	public void onWindowFocusChanged(boolean hasFocus)
	{
		super.onWindowFocusChanged(hasFocus);
		mUnityPlayer.windowFocusChanged(hasFocus);
	}

	// Quit Unity
	@Override protected void onDestroy ()
	{
		mUnityPlayer.quit();
		super.onDestroy();
	}

	// Pause Unity
	@Override protected void onPause()
	{
		super.onPause();
		mUnityPlayer.pause();
	}

	// Resume Unity
	@Override protected void onResume()
	{
		super.onResume();
		mUnityPlayer.resume();
	}

	// This ensures the layout will be correct.
	@Override public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
		// Checks the orientation of the screen
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			RelativeLayout frame = (RelativeLayout) findViewById(R.id.frame);
			final int targtetHeight = frame.getHeight();
			FrameLayout layout = (FrameLayout) findViewById( R.id.container);
			layout.getLayoutParams().height = targtetHeight;
			layout.getLayoutParams().width = targtetHeight * 693 / 390;
		} else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
			FrameLayout layout = (FrameLayout) findViewById( R.id.container);
			layout.getLayoutParams().width = DisplayUtil.getScreenWidth(this);
			layout.getLayoutParams().height = DisplayUtil.getScreenWidth(this) / 693 * 390;
		}

		mUnityPlayer.configurationChanged(newConfig);
	}


	// For some reason the multiple keyevent type is not supported by the ndk.
	// Force event injection by overriding dispatchKeyEvent().
	@Override public boolean dispatchKeyEvent(KeyEvent event)
	{
		if (event.getAction() == KeyEvent.ACTION_MULTIPLE)
			return mUnityPlayer.injectEvent(event);
		return super.dispatchKeyEvent(event);
	}

	// Pass any events not handled by (unfocused) views straight to UnityPlayer
	@Override public boolean onKeyUp(int keyCode, KeyEvent event)     { return mUnityPlayer.injectEvent(event); }
	@Override public boolean onKeyDown(int keyCode, KeyEvent event)   { return mUnityPlayer.injectEvent(event); }
	@Override public boolean onTouchEvent(MotionEvent event)          { return mUnityPlayer.injectEvent(event); }
	/*API12*/ public boolean onGenericMotionEvent(MotionEvent event)  { return mUnityPlayer.injectEvent(event); }
}
