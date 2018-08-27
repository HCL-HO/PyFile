package com.hec.app.webservice;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.hec.app.R;
import com.hec.app.activity.LoginActivity;
import com.hec.app.util.DownloadTask;

/**
 * Created by wangxingjian on 16/8/5.
 */
public class RealmanDownLoad extends Service {
    private RealmanBinder mBinder = new RealmanBinder();
    Notification note;
    NotificationManager noteMng;
    Notification.Builder builder;
    public RealmanDownLoad() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        return mBinder;
    }

    public class RealmanBinder extends Binder {

        public void startDownload(Context context,String filename,String path,String url) {
            Log.i("wxj", "realman download");
            final DownloadTask downloadTask = new DownloadTask(context, filename, path, null,noteMng,builder);
            downloadTask.execute(url);
        }

    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("wxj","realman_service");
        noteMng = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        builder = new Notification.Builder(this).setTicker("已开始下载AG真人娱乐!")
                .setSmallIcon(R.mipmap.icon_logo);
        Intent i = new Intent(this, LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 100, i, PendingIntent.FLAG_UPDATE_CURRENT);
        note = builder.setContentIntent(pendingIntent).setContentTitle("AG真人娱乐").setContentText("正在下载中!").build();
        noteMng.notify(15, note);
    }
}
