package com.hec.app.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.hec.app.R;
import com.hec.app.config.CommonConfig;
import com.hec.app.util.DisplayUtil;
import com.hec.app.util.IntentUtil;

import java.util.ArrayList;
import java.util.List;


public class StartGuideActivity extends AppCompatActivity {
	private ViewPager mViewPager;
	private RadioGroup mRadioGroup;
	private LayoutInflater mLayoutInflater;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.start_guide_layout);

		mLayoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		findView();
		setContView();
	}
	
	private void findView() {
		mViewPager = (ViewPager)findViewById(R.id.start_guide_viewpager);
		mRadioGroup = (RadioGroup)findViewById(R.id.start_guide_radiogroup);

		Button buttonGuide = (Button)findViewById(R.id.start_guide_button);
		buttonGuide.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				IntentUtil.redirectToNextActivity(StartGuideActivity.this, LoginActivity.class);
				StartGuideActivity.this.finish();
			}
		});

		Button buttonSkip = (Button)findViewById(R.id.skip);
		buttonSkip.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentUtil.redirectToNextActivity(StartGuideActivity.this, LoginActivity.class);
                StartGuideActivity.this.finish();
            }
        });

		buttonGuide.requestFocus();
		buttonSkip.requestFocus();

		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int arg0) {
				mRadioGroup.check(arg0);
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				
			}
		});
	}
	
	private void setContView() {
		List<Integer> list=getData();
		mViewPager.setAdapter(new StartGuideAdapter(list));
		generateIndicator(mRadioGroup,list.size(), R.drawable.home_banner_indicator_selector);
	}
	
	private void generateIndicator(RadioGroup radioGroup, int size, int selector) {
		radioGroup.removeAllViews();
		for (int i = 0; i < size; i++) {
			RadioButton radioButton = new RadioButton(StartGuideActivity.this);
			radioButton.setId(i);
			radioButton.setButtonDrawable(android.R.color.transparent);
			radioButton.setBackgroundResource(selector);
			radioButton.setClickable(false);
			int radius = DisplayUtil.getPxByDp(StartGuideActivity.this, 8);
			int margin = DisplayUtil.getPxByDp(StartGuideActivity.this, 3);
			RadioGroup.LayoutParams lp = new RadioGroup.LayoutParams(radius, radius);
			lp.setMargins(margin, margin, margin, margin);
			radioGroup.addView(radioButton, lp);
		}
		radioGroup.clearCheck();
		radioGroup.check(0);
	}
	
	private List<Integer> getData() {
		List<Integer> list=new ArrayList<Integer>();
		list.add(R.mipmap.navigation_p1);
		list.add(R.mipmap.navigation_p2);
		list.add(R.mipmap.navigation_p3);
		
		return list;
	}
	
	private void killProcessAndExit() {
		moveTaskToBack(true);
		android.os.Process.killProcess(android.os.Process.myPid());
		System.exit(10);
	}
	
	private Dialog buildExitConfirmDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.dialog_leave_title);
		builder.setMessage(R.string.dialog_leave_message);
		builder.setPositiveButton(R.string.dialog_determine, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				killProcessAndExit();
			}
		});
		builder.setNegativeButton(R.string.dialog_cancel, null);

		return builder.create();
	}
	
	private boolean needConfirmWhenExit() {
		SharedPreferences settings = getBaseContext().getSharedPreferences(CommonConfig.KEY_SETTING_PREFERENCE, MODE_PRIVATE);
		return settings.getBoolean(CommonConfig.KEY_CONFIRM_WHEN_EXIT, true);
	}
	
	@Override
	public void onBackPressed() {
		if (needConfirmWhenExit()) {
			buildExitConfirmDialog().show();
		}
		else {
			killProcessAndExit();
		}
	}
	
	public class StartGuideAdapter extends PagerAdapter {
		List<Integer> mList;
		public StartGuideAdapter(List<Integer> list){
			mList = list;
		}
		
		@Override
		public int getCount() {
			return mList.size();
		}
		
		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}
		
		@Override
		public Object instantiateItem(View container, int position) {
			View view = mLayoutInflater.inflate(R.layout.start_promote_image_layout, null);
			ImageView imageView=(ImageView)view.findViewById(R.id.start_promote_imageview);
			imageView.setImageResource(mList.get(position));
			
			((ViewPager) container).addView(view, 0);
			
			return view;
		}
				
		@Override
		public void destroyItem(View container, int position, Object object) {
			((ViewPager) container).removeView((View) object);
		}
	}
}
