package com.hec.app.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import com.hec.app.activity.HomeActivity;
import com.hec.app.activity.SettingActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

import de.greenrobot.event.EventBus;

/**
 * Created by asianark on 21/3/16.
 */

public class DownloadTask extends AsyncTask<String, Integer, String> {

    private Context context;
    private PowerManager.WakeLock mWakeLock;
    private String filename;
    private String filepath;
    private ProgressDialog mProgressDialog;
    private NotificationManager noteMng;
    public static int Progress_Percent = 0;
    private Notification.Builder builder;
    private OnFinishPatchDownload onFinishPatchDownload;
    private int lastProgress = 100;


    public DownloadTask(Context context,String filename, String filepath,
                        ProgressDialog mProgressDialog,NotificationManager
                                noteMng,Notification.Builder builder) {
        this.context = context;
        this.filename = filename;
        this.filepath = filepath;
        this.mProgressDialog = mProgressDialog;
        this.noteMng = noteMng;
        this.builder = builder;
    }

    @Override
    protected String doInBackground(String... sUrl) {
        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        File dir = new File(filepath);
        dir.mkdirs();
        File file = new File(filepath, filename);
        try {
            URL url = new URL(sUrl[0]);
            connection = (HttpURLConnection) url.openConnection();

            connection.connect();

            // expect HTTP 200 OK, so we don't mistakenly save error report
            // instead of the file
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return "Server returned HTTP " + connection.getResponseCode()
                        + " " + connection.getResponseMessage();
            }
            // this will be useful to display download percentage
            // might be -1: server did not report the length
            int fileLength = connection.getContentLength();
            // download the file
            input = connection.getInputStream();
            output = new FileOutputStream(file);
            //output = new FileOutputStream(randomAccessFile);

            byte data[] = new byte[4096];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                // allow canceling with back button
                if (isCancelled()) {
                    input.close();
                    return null;
                }
                total += count;
                // publishing the progress....
                if (fileLength > 0) // only if total length is known
                    publishProgress((int) (total * 100 / fileLength));
                output.write(data, 0, count);
            }
        } catch (Exception e) {
            return e.toString();
        } finally {
            try {
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
            } catch (IOException ignored) {
            }

            if (connection != null)
                connection.disconnect();
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // take CPU lock to prevent CPU from going off if the user
        // presses the power button during download
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                getClass().getName());
        mWakeLock.acquire();
        if(onFinishPatchDownload != null){
            mProgressDialog.show();
        }
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        Progress_Percent = progress[0];
        if(builder != null & Progress_Percent != lastProgress){
            noteMng.notify(10,builder.setProgress(100,Progress_Percent,false).setContentText("已下载" + Progress_Percent + "%").build());
            lastProgress = Progress_Percent;
        }
        super.onProgressUpdate(progress);
        // if we get here, length is known, now set indeterminate to false

        //we do not need it when downloading background
        if(onFinishPatchDownload != null) {
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMax(100);
            mProgressDialog.setProgress(progress[0]);
        }
    }

    @Override
    protected void onPostExecute(String result) {
//        if(onFinishPatchDownload != null){
//            onFinishPatchDownload.onfinish();
//            mProgressDialog.dismiss();
//        }else {
            mWakeLock.release();
            try {
                //mProgressDialog.dismiss();
            } catch (Exception e) {

            }
            if (result != null) {
                Log.i("wxj", "Download error: " + result);
                new File(this.filepath + "/" + this.filename).delete();
                if (context instanceof HomeActivity) {
                    ((HomeActivity) context).setDownLoading(false);
                    Toast.makeText(context, "下载错误：" + "请连接VPN后下载！", Toast.LENGTH_LONG).show();
                } else {
                    if (context instanceof SettingActivity) {
                        ((SettingActivity) context).setIsDownLoading(false);
                    }

                    Toast.makeText(context, "下载错误：" + "网络断开", Toast.LENGTH_LONG).show();
                    if(builder != null){
                        noteMng.notify(10,builder.setContentText("下载失败，请重新下载！").build());
                    }
                }
                noteMng.cancel(10);
                noteMng.cancel(15);

            } else {
                Toast.makeText(context, "下载成功！", Toast.LENGTH_SHORT).show();
                //EventBus.getDefault().post(false);
                if (context instanceof HomeActivity) {
                    ((HomeActivity) context).setDownLoading(false);
                }
                else if (context instanceof SettingActivity) {
                    ((SettingActivity) context).setIsDownLoading(false);
                }

                startInstall(this.filepath + "/" + this.filename);
                if (noteMng != null) {
                    noteMng.cancel(10);
                    noteMng.cancel(15);
                }
            }
        //}
    }

    public void startInstall(String filepath){
        Progress_Percent = 0;
        Uri uri = Uri.fromFile(new File(filepath));
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    public static void getNote(Notification note, NotificationManager noteMng){

    }

    public void setOnFinishPatchDownload(OnFinishPatchDownload onFinishPatchDownload){
        this.onFinishPatchDownload = onFinishPatchDownload;
    }

    public interface OnFinishPatchDownload{
        void onfinish();
    }
}



