package com.hec.app.activity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.hec.app.R;
import com.hec.app.activity.base.BaseActivityWithMenu;
import com.hec.app.framework.widget.ResideMenu;

public class ProxyListActivity extends BaseActivityWithMenu implements View.OnClickListener{

    private ImageView imgPerson;
    private TextView title;
    private ResideMenu resideMenu;
    private boolean disabled = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proxy_list);

        resideMenu = super.getResidingMenu();
        imgPerson = (ImageView) findViewById(R.id.imgPersonalCenter);
        imgPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resideMenu.openMenu(ResideMenu.DIRECTION_RIGHT);
            }
        });

        final View rootView = findViewById(R.id.proxy_list_root);
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int heightDiff = rootView.getRootView().getHeight() - rootView.getHeight();
                if (heightDiff > 200 && !disabled) { // if more than 100 pixels, its probably a keyboard...
                    disableMenu(rootView);
                    disabled = true;
                } else if (heightDiff <= 200 && disabled){
                    enableMenu(rootView);
                    disabled = false;
                }
            }
        });

    }

    public void backClick (View v){
        finish();
    }

    public void disableMenu(View v) {
        getResidingMenu().addIgnoredView(v);
        imgPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //         resideMenu.openMenu(ResideMenu.DIRECTION_RIGHT);
            }
        });
    }
    public void enableMenu(View v) {
        getResidingMenu().removeIgnoredView(v);
        imgPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resideMenu.openMenu(ResideMenu.DIRECTION_RIGHT);
            }
        });
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
    }
}
