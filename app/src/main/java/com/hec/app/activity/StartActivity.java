package com.hec.app.activity;

import android.Manifest;

import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

import android.graphics.drawable.ColorDrawable;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.ndk.CrashlyticsNdk;
import com.crashlytics.android.answers.Answers;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.hec.app.BuildConfig;
import com.hec.app.R;
import com.hec.app.activity.base.BaseActivity;
import com.hec.app.activity.base.BaseApp;
import com.hec.app.config.CommonConfig;
import com.hec.app.config.UrlConfig;
import com.hec.app.entity.AllPlayConfig;
import com.hec.app.entity.AppBean;
import com.hec.app.entity.BasicDataInfo;
import com.hec.app.entity.BizException;
import com.hec.app.entity.Response;
import com.hec.app.entity.RestfulApiUrlEntity;
import com.hec.app.framework.cache.MyFileCache;
import com.hec.app.framework.widget.MyToast;
import com.hec.app.lottery.LotteryConfig;
import com.hec.app.util.DefaultDataCache;
import com.hec.app.util.IntentUtil;
import com.hec.app.util.MyAsyncTask;
import com.hec.app.util.SlotUtl;
import com.hec.app.webservice.BaseService;
import com.hec.app.webservice.DownLoadService;
import com.hec.app.webservice.HomeService;
import com.hec.app.webservice.LotteryService;
import com.hec.app.webservice.ServiceException;
import com.networkbench.agent.impl.NBSAppAgent;

import io.fabric.sdk.android.Fabric;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

public class StartActivity extends BaseActivity {

    private boolean mIsFirstStart = false;
    private boolean mIsBrandUrlsError = false;
    private boolean mIsAppInfoError = false;
    private boolean mIsBasicDataError = false;
    private boolean mIsAllPlayConfigError = false;

    private String mBasicDataHashCode = "";
    private String mAllPlayConfigHashCode = "";

    private View mContent;
    private Map<String,Long> mBrandMap = new ConcurrentHashMap<>();
    private String cdn = "";
    private SharedPreferences sharedPreferences;
    private SharedPreferences mHashCodeSharedPreferences;
    private static long realRandomTime;
    private int line = 0;
    private int REQUEST_CODE_PERMISSION = 1;
    private Dialog mDialog = null;
    private int connectionCount = 0;
    private boolean isFindAvailableApi = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NBSAppAgent.setLicenseKey("393c1b1d1f2c42bea804661e466f4bcb").withLocationServiceEnabled(true).start(this);
        sharedPreferences = getSharedPreferences(CommonConfig.KEY_ENTRANCE, MODE_PRIVATE);
        BaseService.RESTFUL_SERVICE_HOST = sharedPreferences.getString(CommonConfig.KEY_FIRST, UrlConfig.RESTFUL_SERVICE_HOST);
        BaseService.SLOT_URL = sharedPreferences.getString(CommonConfig.KEY_SLOT, "");

        mHashCodeSharedPreferences = getSharedPreferences(CommonConfig.KEY_HASHCODE, MODE_PRIVATE);
        mBasicDataHashCode = mHashCodeSharedPreferences.getString(CommonConfig.KEY_HASHCODE_BASICDATA_CACHE, "");
        mAllPlayConfigHashCode = mHashCodeSharedPreferences.getString(CommonConfig.KEY_HASHCODE_ALLPLAYCONFIG_CACHE, "");

        checkWritingPermission();

        if (super.tintManager != null) {
            super.tintManager.setStatusBarTintEnabled(false);
        }

        mContent = new View(this);
        mContent.setBackgroundResource(R.mipmap.splash_screen);
        setContentView(mContent);

        mIsFirstStart = isFirstStart();
        // 20180725 根據PM跟jason討論的結果, 先移除視窗
        // 20180726 再放回來
        showLoading(getString(R.string.loading_message_one), false);

        Fabric.with(this, new Answers(), new Crashlytics(), new CrashlyticsNdk());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (grantResults.length >= 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i("permission","permission was granted");
                // permission was granted
            }
            else {
                Log.i("permission","permission wasn't granted");
                // permission wasn't granted
            }
        }
    }

    private void checkWritingPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // permission wasn't granted
                Log.e("permission","checkWritingPermission 11");
            }
            else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSION);
                Log.e("permission","checkWritingPermission 22");
            }
        }
    }

    private void redirect() {
        closeLoading();

        if (mIsFirstStart) {
            setIsFirstStart();
            IntentUtil.redirectToNextActivity(StartActivity.this, StartGuideActivity.class);
            finish();
        } else {
            IntentUtil.redirectToNextActivity(StartActivity.this, LoginActivity.class);
            finish();
        }
    }

    private void getBasicData() {
        mIsBasicDataError = false;
        line++;

        closeLoading();
        showLoading(getString(R.string.loading_message_two), false);

        if(!MyToast.isShowing() && line < 10) {
            // 20180726 PM討論後決定不顯示
//            MyToast.show(StartActivity.this, String.format(getString(R.string.loading_message_line), line));
        }
        else {
            String basicData = MyFileCache.getInstance().get(CommonConfig.KEY_BASICDATA_EXPERT, "");
            if (basicData.isEmpty()) {
                basicData = DefaultDataCache.getDataCache(StartActivity.this, DefaultDataCache.BASEICDATA_CACHE);
            }

            Gson gson = new Gson();
            Type messageType = new TypeToken<Response<List<BasicDataInfo>>>() {}.getType();
            Response<List<BasicDataInfo>> response = gson.fromJson(basicData, messageType);
            new LotteryService().basicDataInfo = response.getData();

            getAllPlayConfig();
            return;
        }

        MyAsyncTask<List<BasicDataInfo>> task = new MyAsyncTask<List<BasicDataInfo>>(StartActivity.this) {
            @Override
            public List<BasicDataInfo> callService() throws IOException, JsonParseException, BizException, ServiceException {
                return new LotteryService().getBasicData(mBasicDataHashCode);
            }

            @Override
            public void onLoaded(List<BasicDataInfo> result) throws Exception {
                if (mIsBasicDataError) {
                    if(BaseApp.getAppBean().resetApiUrl(StartActivity.this, new Callable<Integer>() {
                        @Override
                        public Integer call() throws Exception {
                            return null;
                        }
                    })){
                        getBasicData();
                    }
                    else {
                        MyToast.show(StartActivity.this, getString(R.string.error_message_unstable_network_leave));
                    }
                }
                else {
                    BasicDataInfo basicDataInfo = new LotteryService().getCachedBasicDataInfo(LotteryConfig.PLAY_MODE.CLASSIC);
                    if (basicDataInfo != null) {
                        SharedPreferences.Editor editor = mHashCodeSharedPreferences.edit();
                        editor.putString(CommonConfig.KEY_HASHCODE_BASICDATA_CACHE, basicDataInfo.getHashCode());
                        Log.i("wxj","basic hash " + basicDataInfo.getHashCode());
                        editor.commit();
                    }

                    getAllPlayConfig();
                }
            }
        };
        task.setOnError(new MyAsyncTask.OnError() {
            @Override
            public void handleError(Exception e) {
                mIsBasicDataError = true;
            }
        });
        task.executeTask();
    }

    private void getAllPlayConfig() {
        mIsAllPlayConfigError = false;
        line++;

        if (line >= 10) {
            String basicData = MyFileCache.getInstance().get(CommonConfig.KEY_ALLPLAYCONFIG, "");
            if (basicData.isEmpty()) {
                basicData = DefaultDataCache.getDataCache(StartActivity.this, DefaultDataCache.ALLPLAYCONFIG_CACHE);
            }

            Gson gson = new Gson();
            Type messageType = new TypeToken<Response<AllPlayConfig>>() {}.getType();
            Response<AllPlayConfig> response = gson.fromJson(basicData, messageType);
            new LotteryService().allPlayConfigInfo = response.getData();

            getBrandUrls();
            redirect();
            return;
        }

        MyAsyncTask<AllPlayConfig> task = new MyAsyncTask<AllPlayConfig>(StartActivity.this) {
            @Override
            public AllPlayConfig callService() throws IOException, JsonParseException, BizException, ServiceException {
                return new LotteryService().getAllPlayConfig(mAllPlayConfigHashCode);
            }

            @Override
            public void onLoaded(AllPlayConfig result) throws Exception {;
                if (mIsAllPlayConfigError) {
                    if(BaseApp.getAppBean().resetApiUrl(StartActivity.this, new Callable<Integer>() {
                        @Override
                        public Integer call() throws Exception {
                            return null;
                        }
                    })){
                        getAllPlayConfig();
                    }
                    else {
                        MyToast.show(StartActivity.this, getString(R.string.error_message_unstable_network_leave));
                    }
                }
                else {
                    AllPlayConfig allPlayConfig = new LotteryService().getAllPlayConfigInfo();
                    if (allPlayConfig != null) {
                        SharedPreferences.Editor editor = mHashCodeSharedPreferences.edit();
                        editor.putString(CommonConfig.KEY_HASHCODE_ALLPLAYCONFIG_CACHE, allPlayConfig.getHashCode());
                        editor.commit();
                    }
                    getBrandUrls();
                    redirect();
                }
            }
        };
        task.setOnError(new MyAsyncTask.OnError() {
            @Override
            public void handleError(Exception e) {
                mIsAllPlayConfigError = true;
            }
        });
        task.executeTask();
    }

    private void setIsFirstStart() {
        SharedPreferences mysherPreferences = StartActivity.this.getSharedPreferences(CommonConfig.KEY_HOME_FIRST_FILE, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = mysherPreferences.edit();
        editor.putBoolean(CommonConfig.KEY_HOME_FIRST_DATA, false);
        editor.commit();
    }

    private boolean isFirstStart() {
        SharedPreferences mysherPreferences = StartActivity.this.getSharedPreferences(CommonConfig.KEY_HOME_FIRST_FILE, Activity.MODE_PRIVATE);
        return mysherPreferences.getBoolean(CommonConfig.KEY_HOME_FIRST_DATA, true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Bitmap bitmap = ((BitmapDrawable) mContent.getBackground()).getBitmap();
        bitmap = null;
        mContent = null;
        System.gc();
    }

    @Override
    public boolean checkLogin(Activity activity, Class<?> loginBeforecCls) {
        return true;
    }

    private void getAppInfo() {
        mIsAppInfoError = false;
        MyAsyncTask<AppBean> task = new MyAsyncTask<AppBean>(StartActivity.this) {

            @Override
            public AppBean callService() throws IOException, JsonParseException, BizException, ServiceException {
                return new HomeService().getAppInfo();
            }

            @Override
            public void onLoaded(AppBean result) throws Exception {
                if (StartActivity.this == null || StartActivity.this.isFinishing()) {
                    return;
                }

                if (!mIsAppInfoError) {
                    BaseApp.setAppBean(result);
                    Log.i("wxj", "unity " + BaseApp.getAppBean().getIosAppUrl());

                    boolean isShowUpdateApp = false;
                    String forcedVersion = BaseApp.getAppBean().getForceVersion();
                    if (forcedVersion != null) {
                        if (!forcedVersion.isEmpty()) {
                            String[] array = forcedVersion.split(";");
                            for (String version : array) {
                                if (BaseApp.instance().getVersionCode().equals(version)) {
                                    isShowUpdateApp = true;
                                    break;
                                }
                            }
                        }
                    }

                    if (mDialog != null) {
                        if (mDialog.isShowing()) {
                            closeLoading();
                            return;
                        }
                    }

                    if (isShowUpdateApp) {
                        closeLoading();

                        mDialog = new Dialog(StartActivity.this, R.style.custom_dialog_style);
                        mDialog.setCanceledOnTouchOutside(false);
                        mDialog.setCancelable(false);
                        mDialog.setContentView(R.layout.forced_update_dialog);

                        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                        Button determine = (Button) mDialog.findViewById(R.id.determine);
                        determine.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String version = BaseApp.getAppBean().getAaMinAvailableVersion();
                                final String downloadUrl = BaseApp.getAppBean().getAppUrl();

                                if (!downloadUrl.isEmpty()) {
                                    String[] strArray = downloadUrl.split("app-");
                                    for (String str : strArray) {
                                        if (str.contains(".apk")) {
                                            String[] array = str.split(".apk");
                                            version = array[0];
                                        }
                                    }
                                }

                                String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/hec";
                                String fileName = "hec-" + version + ".apk";
                                File file = new File(filePath + "/" + fileName);

                                if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                                    MyToast.show(StartActivity.this, getString(R.string.error_message_sd_card));
                                } else if (file.exists()) {
                                    Uri uri = Uri.fromFile(file);
                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.setDataAndType(uri, "application/vnd.android.package-archive");
                                    startActivity(intent);
                                } else {
                                    MyToast.show(StartActivity.this, getString(R.string.loading_message_download_app));
                                    startDownload(downloadUrl, fileName, filePath);
                                }

                                mDialog.cancel();
                            }
                        });

                        mDialog.show();
                        return;
                    }

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    if (result.getAASlotUrlNew() != null) {
                        BaseService.SLOT_URL = result.getAASlotUrlNew();
                    } else {
                        BaseService.SLOT_URL = result.getAASlotUrl();
                    }

                    if(result.getAASlotWSUrl() != null){
                        BaseService.SLOT_WS_URL = result.getAASlotWSUrl();
                    }

                    if(result.getAASlotCDNUrl() != null){
                        BaseService.SLOT_CDN_URL = result.getAASlotCDNUrl();
                        //BaseService.SLOT_CDN_URL = "http://192.168.0.221:2762/";
                    }

                    if(result.getChatUrl() != null){
                        BaseService.CHAT_URL = result.getChatUrl();
                    }

                    if(result.getAAFishingHTTPUrl() != null){
                        BaseService.SLOT_FISHING_URL = result.getAAFishingHTTPUrl();
                    }

                    if(result.getAAFishingWSUrl() != null){
                        BaseService.SLOT_FISHING_WS = result.getAAFishingWSUrl();
                    }

                    /**
                        We should commend this method when build.
                     */
                    if(BuildConfig.DEBUG || BuildConfig.SIT || BuildConfig.UAT) {
                        SlotUtl.switchUrlForTest();
                    }
                    editor.putString(CommonConfig.KEY_FIRST, cdn);
                    editor.putString(CommonConfig.KEY_SECOND, result.getApiUrls());
                    editor.putString(CommonConfig.KEY_SLOT, result.getAASlotUrlNew());
                    editor.commit();

                    getApiUrlListFromAppInfo();
                } else {
                    //TODO network error!!
                    BaseApp.setAppBean(new AppBean());
                    MyToast.show(StartActivity.this, getString(R.string.error_message_entrance_retry));
                    final List<RestfulApiUrlEntity> list = new ArrayList<>();
                    for (String url : UrlConfig.apiList) {
                        url = url.contains("http") ? url : "http://" + url;
                        url = url.endsWith("/") ? url : url + "/";
                        list.add(new RestfulApiUrlEntity(url, true));
                    }
                    BaseApp.getAppBean().setAvailableUrlList(list);
                    getApiResponseTime(list);
                }

            }
        };
        task.setOnError(new MyAsyncTask.OnError() {
            @Override
            public void handleError(Exception e) {
                mIsAppInfoError = true;
            }
        });
        task.executeTask();
    }

    public void getApiUrlListFromAppInfo() {
        final List<RestfulApiUrlEntity> list = new ArrayList<>();
        AppBean appBean = BaseApp.getAppBean();
        if (appBean != null && appBean.getApiUrl() != null) {
            String mainApiUrl = appBean.getApiUrl().contains("http") ? appBean.getApiUrl() : "http://" + appBean.getApiUrl();
            mainApiUrl = mainApiUrl.endsWith("/") ? mainApiUrl : mainApiUrl + "/";
            RestfulApiUrlEntity mainApiUrlInfo = new RestfulApiUrlEntity(mainApiUrl, true);
            list.add(mainApiUrlInfo);
        }
        if (appBean != null && appBean.getApiUrls() != null) {
            for (String url : appBean.getApiUrls().split(";")) {
                url = url.contains("http") ? url : "http://" + url;
                url = url.endsWith("/") ? url : url + "/";
                list.add(new RestfulApiUrlEntity(url, true));
            }
        }
        BaseApp.getAppBean().setAvailableUrlList(list);
        connectionCount = list.size();
        getApiResponseTime(list);
    }

    private void getApiResponseTime(final List<RestfulApiUrlEntity> list) {
        if (connectionCount != 0 && (!BuildConfig.DEBUG || !BuildConfig.SIT || !BuildConfig.UAT)) {
            for (final RestfulApiUrlEntity info : list) {
                final long requestTime = System.currentTimeMillis();
                MyAsyncTask<Response> task = new MyAsyncTask<Response>(StartActivity.this) {
                    @Override
                    public Response callService() throws IOException, JsonParseException, BizException, ServiceException {
                        return new HomeService().testURLSpeed(info.getUrl());
                    }

                    @Override
                    public void onLoaded(Response paramT) throws Exception {
                        info.setResponseTime(System.currentTimeMillis() - requestTime);
                        connectionCount--;
                        if (!isFindAvailableApi && info.isAvailable()) {
                            isFindAvailableApi = true;
                            BaseService.RESTFUL_SERVICE_HOST = info.getUrl();
                            getBasicData();
                        }
                        if (connectionCount == 0 && !isFindAvailableApi) {
                            BaseApp.showNoAvailableApiUrlErrorDialog(StartActivity.this);
                        }
                    }
                };
                task.executeTask();
                task.setOnError(new MyAsyncTask.OnError() {
                    @Override
                    public void handleError(Exception paramException) {
                        info.setAvailable(false);
                    }
                });
            }
        } else {
            getBasicData();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getAppInfo();
    }

    private void getBrandUrls(){
        mIsBrandUrlsError = false;

        MyAsyncTask<String> task = new MyAsyncTask<String>(StartActivity.this) {
            @Override
            public String callService() throws IOException, JsonParseException, BizException, ServiceException {
                return new HomeService().getUrls();
            }

            @Override
            public void onLoaded(String data) throws Exception {
                if (!mIsBrandUrlsError) {
                    List<String> brandUrlArray = new ArrayList<>();
                    String[] strArray = data.split("url: \"");
                    for (String str : strArray) {
                        if (str.contains("http")) {
                            String[] array = str.split("\", ms:");
                            brandUrlArray.add(array[0]);
                        }
                    }

                    BaseApp.setBrandUrlArray(brandUrlArray);
                    selectBrandFastestURL();
                }
            }
        };
        task.setOnError(new MyAsyncTask.OnError() {
            @Override
            public void handleError(Exception e) {
                mIsBrandUrlsError = true;
            }
        });
        task.executeTask();
    }

    public void selectBrandFastestURL(){
        if(!BuildConfig.DEBUG || !BuildConfig.SIT || !BuildConfig.UAT){
            for(final String url : BaseApp.getBrandUrlArray()) {
                MyAsyncTask<Response> task = new MyAsyncTask<Response>(StartActivity.this) {
                    long requestTime, responseTime;

                    @Override
                    public Response callService() throws IOException, JsonParseException, BizException, ServiceException {
                        requestTime = System.currentTimeMillis();

                        Random random = new Random();
                        //为了单次并发有时间间隔，这里在request前等待一个random值
                        int randomTime = random.nextInt(5) + 1;
                        realRandomTime = randomTime * 1000;
                        synchronized (this){
                            try {
                                wait(realRandomTime);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        return new HomeService().testBrandSpeed(url);
                    }

                    @Override
                    public void onLoaded(Response result) throws Exception {
                        responseTime = System.currentTimeMillis();
                        mBrandMap.put(url, responseTime - requestTime);
                        BaseApp.setBrandMap(mBrandMap);
                    }
                };
                task.executeTask();
                task.setOnError(new MyAsyncTask.OnError() {
                    @Override
                    public void handleError(Exception paramException) {
                    }
                });
            }
        }
    }

    public void startDownload(final String url, final String filename, final String path){
        ServiceConnection connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                DownLoadService.MyBinder binder = (DownLoadService.MyBinder) service;
                binder.startDownload(StartActivity.this, filename, path, url);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };

        Intent startIntent = new Intent(this, DownLoadService.class);
        bindService(startIntent, connection, BIND_AUTO_CREATE);
    }
}