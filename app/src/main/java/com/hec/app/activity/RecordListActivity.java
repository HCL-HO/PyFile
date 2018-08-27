package com.hec.app.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hec.app.R;
import com.hec.app.activity.base.BaseActivityWithMenu;
import com.hec.app.fragment.LaohujiFragment;
import com.hec.app.fragment.RecordListFragment;
import com.hec.app.framework.widget.ResideMenu;

/**
 * Created by Joshua on 2016/1/5.
 */
public class RecordListActivity extends BaseActivityWithMenu implements View.OnClickListener{

    private RecordListFragment mListFragment;
    private LaohujiFragment laohujiFragment;
    private ImageView imgPerson;
    private TextView title;
    private ImageView back;
    private ResideMenu resideMenu;
    private ImageView caipiao,laohuji,img_title;
    private boolean badslot = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_fragment);
        badslot = getIntent().getBooleanExtra("badslot",false);
        resideMenu = super.getResidingMenu();
        imgPerson = (ImageView) findViewById(R.id.imgPersonalCenter);
        imgPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resideMenu.openMenu(ResideMenu.DIRECTION_RIGHT);
            }
        });
        caipiao = (ImageView) findViewById(R.id.img_lottery);
        laohuji = (ImageView) findViewById(R.id.img_laohuji);
        img_title = (ImageView) findViewById(R.id.img_title);
        back = (ImageView) findViewById(R.id.imgBack);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        title = (TextView)findViewById(R.id.activity_title);

        FragmentManager fm = getSupportFragmentManager();
        mListFragment = (RecordListFragment) fm.findFragmentById(R.id.id_fragment_container1);
        laohujiFragment = (LaohujiFragment) fm.findFragmentById(R.id.id_fragment_container2);
        if(mListFragment == null )
        {
            String argument = getIntent().getStringExtra(RecordListFragment.ARGUMENT);
            if("投注记录".equals(argument)){
                title.setVisibility(View.VISIBLE);
                title.setText("投注记录");
                title.setVisibility(View.GONE);
                caipiao.setVisibility(View.VISIBLE);
                laohuji.setVisibility(View.VISIBLE);
                img_title.setVisibility(View.VISIBLE);
                changeRecord();
            }else{
                title.setText(argument);
            }
            if(badslot){
                img_title.setImageResource(R.mipmap.button_slot_selected);
                laohujiFragment = new LaohujiFragment();
                fm.beginTransaction().add(R.id.id_fragment_container1, laohujiFragment).commit();
            }else{
                img_title.setImageResource(R.mipmap.button_lottery_selected);
                mListFragment = new RecordListFragment();
                fm.beginTransaction().add(R.id.id_fragment_container2, mListFragment).commit();
            }
        }
    }


    private void changeRecord(){
        caipiao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getSupportFragmentManager();
                img_title.setImageResource(R.mipmap.button_lottery_selected);
//                if(laohujiFragment != null){
//                    fm.beginTransaction().hide(laohujiFragment).commit();
//                    fm.beginTransaction().show(mListFragment).commit();
//                }
                if(laohujiFragment != null) {
                    fm.beginTransaction().hide(laohujiFragment).commit();
                }
                if(mListFragment == null){
                    mListFragment = new RecordListFragment();
                    fm.beginTransaction().add(R.id.id_fragment_container1,mListFragment).commit();
                }else{
                    fm.beginTransaction().show(mListFragment).commit();
                }
            }
        });
        laohuji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                img_title.setImageResource(R.mipmap.button_slot_selected);
                FragmentManager fm = getSupportFragmentManager();
                if(mListFragment != null) {
                    fm.beginTransaction().hide(mListFragment).commit();
                }
                if(laohujiFragment == null){
                    laohujiFragment = new LaohujiFragment();
                    fm.beginTransaction().add(R.id.id_fragment_container2,laohujiFragment).commit();
                }else{
                    fm.beginTransaction().show(laohujiFragment).commit();
                }
            }
        });
    }
    @Override
    public void onClick(View v) {
        super.onClick(v);
    }

}
