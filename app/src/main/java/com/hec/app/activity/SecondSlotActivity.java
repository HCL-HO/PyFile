package com.hec.app.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.hec.app.R;
import com.unity3d.player.UnityPlayer;

public class SecondSlotActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_second_slot);
        UnityPlayer.UnitySendMessage("Preload"
                    , "getIntentData"
                    , getIntent().getStringExtra("joe"));
    }
}
