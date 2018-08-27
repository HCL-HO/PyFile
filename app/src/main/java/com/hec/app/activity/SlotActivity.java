package com.hec.app.activity;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.google.gson.Gson;
import com.hec.app.BuildConfig;
import com.hec.app.activity.base.BaseApp;
import com.hec.app.config.CommonConfig;
import com.hec.app.util.BackListener;
import com.hec.app.util.CustomerAccountManager;
import com.hec.app.util.MyFloatingActionButton;
import com.hec.app.util.SlotUtl;
import com.hec.app.webservice.BaseService;
import com.unity3d.player.UnityPlayer;
import com.unity3d.player.UnityPlayerActivity;
import java.io.Serializable;
import java.util.ArrayList;

import wxj.fliplog.View.LogFloatingActionButton;

public class SlotActivity extends UnityPlayerActivity {
    public static Context ctx;
    private SharedPreferences token;
    private MyFloatingActionButton floatingActionButton;
    private SlotLogPopupWindow popupWindow;
    private ArrayList<String> list = new ArrayList<>();
    private String scene = "";

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            list.add((String)msg.obj);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx = this;
        BaseApp.activityList.add(SlotActivity.this);
        if(BuildConfig.DEBUG){
            new LogFloatingActionButton.Builder(this)
                    .setPopupWindowBelow()
                    .setTagBeforeOpen(wxj.fliplog.Util.LogcatHelper.VERBOSE)
                    .setFilterBeforeOpen("Unity")
                    .openLogCat()
                    .build();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        scene = getIntent().getStringExtra(CommonConfig.BUNDLE_GOTIGER_SCENE);
        BaseApp.setCurrentActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //new LogFloatingActionButton.Builder(this).removeView().closeLogCat();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (intent != null)
            setIntent(intent);
    }

    public void gotoRecord(){
        Intent intent = new Intent(SlotActivity.this, HomeActivity.class);
        intent.putExtra("badslot",true);
        UnityPlayer.currentActivity.startActivity(intent);
    }

    /**
     * When the token is out of date, logout directly from game interface to Login page.Unity should invoke this method.
     */
    public void logout(){
        Intent intent = new Intent(SlotActivity.this,LoginActivity.class);
        UnityPlayer.currentActivity.startActivity(intent);
        token = ctx.getSharedPreferences(CommonConfig.KEY_TOKEN, Context.MODE_PRIVATE);
        token.edit().putString(CommonConfig.KEY_TOKEN_TOKENS, "").commit();
        CustomerAccountManager.getInstance().logOut();
    }

    /**
     * The most important method that Unity should invoke.Send data to Unity.
     */
    public void getData(){
        String UserName = getIntent().getStringExtra(CommonConfig.BUNDLE_GOTIGER_USERNAME);
        float Balance = (float) (getIntent().getIntExtra(CommonConfig.BUNDLE_GOTIGER_BALANCE, 0));
        String AASlotUrl = getIntent().getStringExtra(CommonConfig.BUNDLE_GOTIGER_AASLOTURL);
        String cdn = getIntent().getStringExtra("cdnurl");
        if(BaseApp.getAppBean()==null){
            return;
        }
        if(BaseApp.getAppBean().getIosAppUrl()==null){
            return;
        }
        String slotData = "";
        switch (scene){
            case CommonConfig.THREE_D_BACARRAT:
                slotData = SlotUtl.buildDataAccordingToScene(CommonConfig.THREE_D_BACARRAT,UserName,AASlotUrl,Balance);
                break;
            case CommonConfig.FISHING:
                slotData = SlotUtl.buildDataAccordingToScene(CommonConfig.FISHING,UserName,AASlotUrl,Balance);
                break;
            case CommonConfig.DEFAULT_TAG:
                slotData = SlotUtl.buildDataAccordingToScene(CommonConfig.DEFAULT_TAG,UserName,AASlotUrl,Balance);
                break;
        }
        //isBacarrat = false;
        if(BaseApp.getAppBean()!=null) {
            Log.i("wxj","slot" + slotData);
            UnityPlayer.UnitySendMessage("Preload"
                    , "getIntentData"
                    , slotData);
        }
    }

    public void GetUrlData(){

    }

    @Override
    public void onConfigurationChanged(Configuration configuration) {
        //new LogFloatingActionButton.Builder(this).setPopupWindowLandscape();
        super.onConfigurationChanged(configuration);
    }

}

