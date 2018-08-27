package com.hec.app.activity;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.JsonParseException;
import com.hec.app.R;
import com.hec.app.activity.base.BaseActivity;
import com.hec.app.activity.base.BaseActivityWithMenu;
import com.hec.app.fragment.RecordContentFragment;
import com.hec.app.fragment.RecordListFragment;
import com.hec.app.fragment.SlotRecordFragment;
import com.hec.app.framework.widget.ResideMenu;

import java.io.IOException;

/**
 * Created by Joshua on 2016/1/5.
 */
public class RecordContentActivity extends BaseActivityWithMenu implements View.OnClickListener{

    private RecordContentFragment mContentFragment;
    private SlotRecordFragment slotRecordFragment;
    private TextView title;
    private ImageView imgPerson;
    private ImageView back;
    private TextView goback;
    private String SLOT = "";

    private ResideMenu resideMenu;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_single_fragment);

        resideMenu = super.getResidingMenu();
        imgPerson = (ImageView) findViewById(R.id.imgPersonalCenter);
        imgPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resideMenu.openMenu(ResideMenu.DIRECTION_RIGHT);
            }
        });

        back = (ImageView) findViewById(R.id.imgBack);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        title = (TextView)findViewById(R.id.activity_title);

        FragmentManager fm = getSupportFragmentManager();
        SLOT = getIntent().getStringExtra("slot");

        if("slot".equals(SLOT)){
            slotRecordFragment = (SlotRecordFragment) fm.findFragmentById(R.id.id_fragment_container1);

            if(slotRecordFragment == null )
            {
                final String argument = getIntent().getStringExtra(RecordContentFragment.ARGUMENT);
                title.setText("老虎机详情");
                slotRecordFragment = SlotRecordFragment.newInstance(argument);
                fm.beginTransaction().add(R.id.id_fragment_container1,slotRecordFragment).commit();
            }
        }else{
            mContentFragment = (RecordContentFragment) fm.findFragmentById(R.id.id_fragment_container1);

            if(mContentFragment == null )
            {
                String argument = getIntent().getStringExtra(RecordContentFragment.ARGUMENT);
                Log.e("RecordContentActivity", argument);
                title.setText(argument);
                mContentFragment = RecordContentFragment.newInstance(argument);
                fm.beginTransaction().add(R.id.id_fragment_container1,mContentFragment).commit();

            }
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
    }
}
