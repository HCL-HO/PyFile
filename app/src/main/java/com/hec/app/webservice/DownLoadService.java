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

public class DownLoadService extends Service {
    private MyBinder mBinder = new MyBinder();
    Notification note;
    NotificationManager noteMng;
    Notification.Builder builder;
    public DownLoadService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        return mBinder;
    }

    public class MyBinder extends Binder {

        public void startDownload(Context context,String filename,String path,String url) {
            Log.i("wxj", "startDownload() executed");
            final DownloadTask downloadTask = new DownloadTask(context, filename, path, null,noteMng,builder);
            downloadTask.execute(url);
        }

    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("store","service " + DownloadTask.Progress_Percent);
        noteMng = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        builder = new Notification.Builder(this).setTicker("已开始下载JX最新版!")
                .setSmallIcon(R.mipmap.icon_logo);
        Intent i = new Intent(this, LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 100, i, PendingIntent.FLAG_UPDATE_CURRENT);
        note = builder.setContentIntent(pendingIntent)
                .setContentTitle("JX最新版")
                .setContentText("正在下载中!")
                .setAutoCancel(false)
                .setProgress(100,DownloadTask.Progress_Percent,false)
                .build();
        noteMng.notify(10, note);

    }

}
