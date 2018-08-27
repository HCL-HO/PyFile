package com.hec.app.activity;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hec.app.R;
import com.hec.app.activity.base.BaseActivityWithMenu;
import com.hec.app.fragment.SlotRecordFragment;
import com.hec.app.framework.widget.ResideMenu;

public class SlotRecordActivity extends BaseActivityWithMenu implements View.OnClickListener {

    private SlotRecordFragment mContentFragment;
    private TextView title;
    private ImageView imgPerson;
    private ImageView back;
    private TextView goback;

    private ResideMenu resideMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slot_record);

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
        mContentFragment = (SlotRecordFragment) fm.findFragmentById(R.id.id_fragment_container1);

        if(mContentFragment == null )
        {
            String argument = getIntent().getStringExtra(SlotRecordFragment.ARGUMENT);
            //Log.e("RecordContentActivity", argument);
            title.setText("老虎机详情");
            mContentFragment = SlotRecordFragment.newInstance(argument);
            fm.beginTransaction().add(R.id.id_fragment_container1,mContentFragment).commit();

        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
    }
}
