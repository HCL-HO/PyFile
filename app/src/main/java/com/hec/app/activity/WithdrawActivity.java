package com.hec.app.activity;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.hec.app.R;
import com.hec.app.activity.base.BaseActivity;
import com.hec.app.fragment.WithdrawFragment;
import com.hec.app.util.SystemBarTintManager;

/**
 * Created by Joshua on 2016/1/14.
 */
public class WithdrawActivity extends BaseActivity{

    private WithdrawFragment mWithdrawFragment;
    private TextView title;
    private ImageView imgBack;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw);

        title = (TextView)findViewById(R.id.activity_title);
        title.setText(R.string.withdraw_title);
        imgBack = (ImageView)findViewById(R.id.imgBack);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.colorPrimary);//通知栏所需颜色
//            tintManager.setStatusBarAlpha(50f);
        }

        FragmentManager fm = getSupportFragmentManager();
        mWithdrawFragment = (WithdrawFragment) fm.findFragmentById(R.id.id_fragment_container);

        if(mWithdrawFragment == null )
        {
            mWithdrawFragment = new WithdrawFragment();
            fm.beginTransaction().add(R.id.id_fragment_container, mWithdrawFragment).commit();
        }
    }

    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }
}
