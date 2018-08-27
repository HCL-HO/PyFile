package com.hec.app.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.hec.app.R;
import com.hec.app.activity.base.BaseActivityWithMenu;
import com.hec.app.fragment.BrandFragment;
import com.hec.app.fragment.HomeFragment;
import com.hec.app.framework.widget.ResideMenu;


public class BrandActivity extends BaseActivityWithMenu {
    private ResideMenu resideMenu;
    private ImageView imgPerson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brand);
        initView();
    }

    private void initView() {
        resideMenu = super.getResidingMenu();
        imgPerson = (ImageView) findViewById(R.id.imgPersonalCenter);
        imgPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resideMenu.openMenu(ResideMenu.DIRECTION_RIGHT);
            }
        });

        BrandFragment brandFragment = new BrandFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, brandFragment).commit();
    }
}
