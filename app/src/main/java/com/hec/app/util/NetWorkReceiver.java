package com.hec.app.util;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.hec.app.activity.base.BaseActivity;
import com.hec.app.activity.base.BaseApp;
import com.hec.app.framework.widget.MyToast;

/**
 * Created by wangxingjian on 2016/10/28.
 */

public class NetWorkReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)){
            /*
            * when network status has changed,this receiver will receive this intent and we may do something here.
            * */
            if(!isNetworkAvailbale(context)){
                //MyToast.show(context,"您的网络状况不佳，请检查网络设置！");
                BaseApp.haveNetwork = false;
            }else{
                BaseApp.haveNetwork = true;
            }
            Log.i("receiverok","get intent");
        }
    }

    public boolean isNetworkAvailbale(Context context){
        //handler.sendEmptyMessageDelayed(0,5000);
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager != null){
            NetworkInfo info = connectivityManager.getActiveNetworkInfo();
            if(info != null && info.isConnected()){
                // 当前网络是连接的
                if (info.getState() == NetworkInfo.State.CONNECTED)
                {
                    // 当前所连接的网络可用
                    return true;
                }
            }
        }
        return false;
    }
}
