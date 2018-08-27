package com.hec.app.util;
import android.content.Intent;
import com.hec.app.activity.HomeActivity;
import com.unity3d.player.UnityPlayer;

/**
 * Created by wangxingjian on 2017/9/5.
 */

public class BackListener {
    /**
        GO back from game interface to app.Unity should find this class and invoke this method.
     */
    public static void goback(){
        Intent intent = new Intent(UnityPlayer.currentActivity, HomeActivity.class);
        UnityPlayer.currentActivity.startActivity(intent);
    }
}
