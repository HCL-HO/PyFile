package com.hec.app.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.hec.app.R;
import com.hec.app.activity.base.BaseActivity;
import com.hec.app.config.CommonConfig;
import com.hec.app.fragment.InfoCompleteFragment;

public class InfoCompleteActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_complete);

        InfoCompleteFragment infoCompleteFragment = (InfoCompleteFragment) getSupportFragmentManager().findFragmentByTag(CommonConfig.KEY_INFO_COMPLETE_FRAFMENT);
        if (infoCompleteFragment == null) {
            infoCompleteFragment = new InfoCompleteFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.infocomplete_container, infoCompleteFragment, CommonConfig.KEY_INFO_COMPLETE_FRAFMENT).commit();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_init);
        toolbar.setTitle("");
        toolbar.setNavigationIcon(R.mipmap.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void backClick (View v){
        finish();
    }

}
