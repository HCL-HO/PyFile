package com.hec.app.activity.base;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.hec.app.R;
import com.hec.app.activity.LoginActivity;
import com.hec.app.entity.CustomerInfo;
import com.hec.app.framework.widget.MyToast;
import com.hec.app.util.CustomerAccountManager;
import com.hec.app.util.DialogUtil;
import com.hec.app.util.IntentUtil;
import com.hec.app.util.SystemBarTintManager;
import com.hec.app.util.TestUtil;
import com.hec.app.webservice.RequestAnno;
//import com.umeng.analytics.MobclickAgent;
//import com.pgyersdk.feedback.PgyFeedbackShakeManager;

import junit.framework.Test;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by hec on 2015/10/23.
 */
public class BaseActivity extends AppCompatActivity {
    protected static final String APPLICATION_FIRST_FILE_KEY="APPLICATION_FIRST_FILE";
    //第一次正常退出后，写false,默认为true
    protected static final String APPLICATION_FIRST_DATA_KEY="APPLICATION_FIRST_DATA";
    //应用启动就写这个标识
    protected static final String APPLICATION_HOME_STARTED_KEY="APPLICATION_HOME_STARTED_KEY";
    private ProgressDialog mLoadingDialog;
    protected SystemBarTintManager tintManager;
    private static boolean RECOVERED = true;

    //I try to resend request when network recover
//    private Handler handler = new Handler(){
//        @Override
//        public void handleMessage(Message msg) {
//            if(!isNetworkAvailbale(BaseActivity.this)){
//                RECOVERED = false;
//            }
//            if(isNetworkAvailbale(BaseActivity.this) && RECOVERED==false){
//                RECOVERED = true;
//                //Activity activity = baseApp.getCurrentActivity();
//                Method[] methods = activity.getClass().getDeclaredMethods();
//                for(Method m : methods){
//                    m.setAccessible(true);
//                    Annotation anno = m.getAnnotation(RequestAnno.class);
//                    if(anno != null){
//                        try {
//                            m.invoke(activity,"1");
//                        } catch (IllegalAccessException e) {
//                            e.printStackTrace();
//                        } catch (InvocationTargetException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            }
//        }
//    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //baseApp = (BaseApp) this.getApplicationContext();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.colorPrimary);//通知栏所需颜色
            //tintManager.setStatusBarAlpha(50f);
        }
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        checkLogin(this, null);
        if(!isNetworkAvailbale(this)){
            MyToast.cancel();
            MyToast.show(this,"请检查您的网络连接！");
//            MobclickAgent.onEvent(this,"nettotaldown");
        }
    }

    protected void setHomeStartedCount(int firstStarted) {
        SharedPreferences mysherPreferences = this.getSharedPreferences(APPLICATION_FIRST_FILE_KEY,Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = mysherPreferences.edit();
        editor.putInt(APPLICATION_HOME_STARTED_KEY, firstStarted);
        editor.commit();
    }

    protected int getHomeStartedCount() {
        SharedPreferences mysherPreferences = this.getSharedPreferences(APPLICATION_FIRST_FILE_KEY,Activity.MODE_PRIVATE);
        return mysherPreferences.getInt(APPLICATION_HOME_STARTED_KEY, 0);
    }

    protected void recycleBitmap(Bitmap bitmap){
        if(bitmap != null && !bitmap.isRecycled()){
            bitmap.recycle();
        }
    }

    public void showLoading(String tips) {
        closeLoading();
        try {
            if (mLoadingDialog == null) {
                mLoadingDialog = DialogUtil.getProgressDialog(this, tips);
            }
            mLoadingDialog.setMessage(tips);
            mLoadingDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showLoading(String tips, Boolean cancelable) {
        closeLoading();
        try {
            if (mLoadingDialog == null) {
                mLoadingDialog = DialogUtil.getProgressDialog(this, tips);
            }
            mLoadingDialog.setMessage(tips);
            mLoadingDialog.setCancelable(cancelable);
            mLoadingDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void closeLoading() {
        try {
            if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                mLoadingDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void setApplicationFirst() {
        SharedPreferences mysherPreferences = this.getSharedPreferences(APPLICATION_FIRST_FILE_KEY,Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = mysherPreferences.edit();
        editor.putBoolean(APPLICATION_FIRST_DATA_KEY, false);
        editor.commit();
    }

    protected Boolean getApplicationFirst(){
        SharedPreferences mysherPreferences = this.getSharedPreferences(APPLICATION_FIRST_FILE_KEY,Activity.MODE_PRIVATE);
        Boolean isAppStartFirst = mysherPreferences.getBoolean(APPLICATION_FIRST_DATA_KEY, true);

        return isAppStartFirst;
    }

    public boolean checkLogin(Activity activity, Class<?> loginBeforecCls) {

        CustomerInfo customer = CustomerAccountManager.getInstance()
                .getCustomer();
        if (customer == null || customer.getUserID() == null
                || customer.getUserID().length() == 0) {
            BaseApp.instance().setLoginBeforeCls(loginBeforecCls);
            activity.finish();
            IntentUtil.redirectToNextActivity(activity, LoginActivity.class);
            return false;
        }

        return true;
    }

    public static void forceLogin(Activity activity, Class<?> loginBeforecCls) {
        BaseApp.instance().setLoginBeforeCls(loginBeforecCls);
        activity.finish();
        IntentUtil.redirectToNextActivity(activity, LoginActivity.class);
    }

    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        BaseApp.setCurrentActivity(this);
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
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

//    @Override
//    protected void onStop() {
//        super.onStop();
//        handler.removeMessages(0);
//    }
//
//    @Override
//    protected void onDestroy() {
//        clearReferences();
//        super.onDestroy();
//    }
//
//    private void clearReferences(){
//        Activity currActivity = baseApp.getCurrentActivity();
//        if (this.equals(currActivity))
//            baseApp.setCurrentActivity(null);
//    }

    public void refresh(){
        onRestart();
    }
}
