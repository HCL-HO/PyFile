package com.hec.app.activity.base;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.AssetManager;
import android.content.res.Resources;

import android.os.Build;
import android.os.Bundle;
import android.support.multidex.MultiDex;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.hec.app.R;
import com.hec.app.activity.ChatRoomActivity;
import com.hec.app.activity.HomeActivity;
import com.hec.app.activity.LoginActivity;
import com.hec.app.activity.MyAccountActivity;
import com.hec.app.config.UrlConfig;
import com.hec.app.entity.AppBean;
import com.hec.app.entity.BizException;
import com.hec.app.entity.CustomerInfo;
import com.hec.app.entity.Response;
import com.hec.app.entity.RestfulApiUrlEntity;
import com.hec.app.entity.SettleInfo;
import com.hec.app.entity.Trace;
import com.hec.app.framework.cache.MyFileCache;
import com.hec.app.util.CustomerAccountManager;
import com.hec.app.util.IntentUtil;
import com.hec.app.util.MyAsyncTask;
import com.hec.app.util.TestUtil;
import com.hec.app.util.TinkerManager;
import com.hec.app.webservice.BaseService;
import com.hec.app.webservice.HomeService;
import com.hec.app.webservice.ServiceException;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.tencent.tinker.anno.DefaultLifeCycle;
import com.tencent.tinker.lib.tinker.Tinker;
import com.tencent.tinker.loader.app.DefaultApplicationLike;
import com.tencent.tinker.loader.shareutil.ShareConstants;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import cn.sharesdk.framework.ShareSDK;

//import im.fir.sdk.FIR;
@DefaultLifeCycle(
        application = "com.hec.app.activity.base.BaseApplication",             //application name to generate
        flags = ShareConstants.TINKER_ENABLE_ALL)
public class BaseApp extends DefaultApplicationLike {
    private static String mUserAgent;
    private static BaseApp instance;

    public static HomeActivity rootActivity;
    @SuppressWarnings("rawtypes")
    private Class mLoginBeforeClass;

    private CustomerInfo mCustomerInfo;
    private boolean mIsFirstInCheckout;
    private Boolean mIsFirstInHomePage = true;
    private Bundle mLoginBundle;
    private static int mNum = 0;
    public static List<Activity> activityList = new LinkedList<>();
    public static ArrayList<Integer> allUnits = new ArrayList<>();
    public static ArrayList<SettleInfo> allSettleInfo = new ArrayList<>();

    public static ChatRoomActivity chatRoomActivity;

    private static AppBean appBean;
    private static List<String> brandUrlArray = new ArrayList<>();
    private static Map<String,Long> brandMap = new HashMap<>();
    public static boolean haveNetwork = true;
    public static Trace trace;

    public static boolean CHEN_MODE = false;

    public static String CAPTCHA_KEY;
    public static long SERVER_TIME_DIFF;
    public static CookieManager cookieManager;

    private static boolean isGetAvailableApiUrlError;
    private static int availableApiUrlCount;
    public static int retryCount = 0;
    public static boolean isGettingAvailableApiUrl = false;

    //倒计时完成广播
    public final static String COUNTDOWN_ACTION = "android.intent.action.COUNT_DOWN_BROADCAST";

    public static String version;

    public BaseApp(Application application, int tinkerFlags, boolean tinkerLoadVerifyFlag, long applicationStartElapsedTime, long applicationStartMillisTime, Intent tinkerResultIntent, Resources[] resources, ClassLoader[] classLoader, AssetManager[] assetManager) {
        super(application, tinkerFlags, tinkerLoadVerifyFlag, applicationStartElapsedTime, applicationStartMillisTime, tinkerResultIntent, resources, classLoader, assetManager);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        cookieManager = new CookieManager(null, CookiePolicy.ACCEPT_ALL);
        ShareSDK.initSDK(getApplication());
        if (mNum == 0) {
            TestUtil.print("mNum == 0");
            instance = this;
            MyFileCache.install(getApplication().getApplicationContext());
            mUserAgent = getApplication().getResources().getString(R.string.user_agent);
            createImageLoader();
            restoreUser();
            mIsFirstInCheckout = true;
            mNum = mNum + 1;
        }
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void onBaseContextAttached(Context base) {
        super.onBaseContextAttached(base);
        //you must install multiDex whatever tinker is installed!
        MultiDex.install(base);

//        SampleApplicationContext.application = getApplication();
//        SampleApplicationContext.context = getApplication();
        TinkerManager.setTinkerApplicationLike(this);

        TinkerManager.initFastCrashProtect();
        //should set before tinker is installed
        TinkerManager.setUpgradeRetryEnable(true);

        //installTinker after load multiDex
        //or you can put com.tencent.tinker.** to main dex
        TinkerManager.installTinker(this);
        Tinker tinker = Tinker.with(getApplication());
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public void registerActivityLifecycleCallbacks(Application.ActivityLifecycleCallbacks callback) {
        getApplication().registerActivityLifecycleCallbacks(callback);
    }

    public static BaseApp instance() {
        return instance;
    }

    private void restoreUser() {
        SharedPreferences pref = getApplication().getSharedPreferences(
                MyAccountActivity.ACCOUNT_CUSTOMER_INFO, Context.MODE_PRIVATE);
        String customer = pref.getString(
                MyAccountActivity.ACCOUNT_CUSTOMER_INFO_DETAIL, "");
        CustomerInfo customerInfo = new Gson().fromJson(customer,
                CustomerInfo.class);
        if (customerInfo == null) {
            customerInfo = new CustomerInfo();
        }

        CustomerAccountManager.getInstance().setAuthenticationKey(
                customerInfo.getAuthenticationKey());

        if (customerInfo.getUserID() != null) {
            CustomerAccountManager.getInstance().setCustomer(customerInfo);
        }
    }

    @Override
    public void onTerminate() {
        mIsFirstInCheckout = false;
        super.onTerminate();
    }

    public static String getUserAgent() {
        return mUserAgent;
    }

    public static boolean isIntentAvailable(final Intent intent) {
        final PackageManager manager = getCurrentActivity().getPackageManager();
        List<ResolveInfo> list = manager.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    // isLogin
    public boolean checkLogin(Activity activity, Class<?> loginBeforecCls) {

        CustomerInfo customer = CustomerAccountManager.getInstance()
                .getCustomer();

        if (customer == null || customer.getUserID() == null
                || customer.getUserID().length() == 0) {
            setLoginBeforeCls(loginBeforecCls);
            activity.finish();
            IntentUtil.redirectToNextActivity(activity, LoginActivity.class);
            return false;
        }

        return true;
    }

    @SuppressWarnings("rawtypes")
    public void setLoginBeforeCls(Class cls) {
        mLoginBeforeClass = cls;
    }

    @SuppressWarnings("rawtypes")
    public Class getLoginBeforeCls() {
        return this.mLoginBeforeClass;
    }

    public void setCustomerInfo(CustomerInfo mCustomerInfo) {
        this.mCustomerInfo = mCustomerInfo;
    }

    public CustomerInfo getCustomerInfo() {
        return mCustomerInfo;
    }

    public void setIsFirstInCheckout(boolean mIsFirstInCheckout) {
        this.mIsFirstInCheckout = mIsFirstInCheckout;
    }

    public boolean isIsFirstInCheckout() {
        return mIsFirstInCheckout;
    }

    public void setIsFirstInHomePage(Boolean isFirstInHomePage) {
        mIsFirstInHomePage = isFirstInHomePage;
    }

    public Boolean isFirstInHomePage() {
        return mIsFirstInHomePage;
    }

    public void setLoginBundle(Bundle bundle) {
        this.mLoginBundle = bundle;
    }

    public Bundle getLoginBundle() {
        return this.mLoginBundle;
    }

    private void createImageLoader() {
        // 初始化ImageLoader
        @SuppressWarnings("deprecation")
        DisplayImageOptions options = new DisplayImageOptions.Builder().showStubImage(R.mipmap.ic_stub) // 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.mipmap.ic_empty) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.mipmap.ic_error) // 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisc(true) // 设置下载的图片是否缓存在SD卡中
                        // .displayer(new RoundedBitmapDisplayer(20)) // 设置成圆角图片
                .build(); // 创建配置过得DisplayImageOption对象

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplication().getApplicationContext()).defaultDisplayImageOptions(options)
                .threadPriority(Thread.NORM_PRIORITY - 2).denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(new Md5FileNameGenerator()).tasksProcessingOrder(QueueProcessingType.LIFO).build();
        ImageLoader.getInstance().init(config);
    }

    public String getVersionCode(){
        try {
            if(version == null){
                PackageManager manager = getApplication().getPackageManager();
                PackageInfo info = manager.getPackageInfo(getApplication().getPackageName(), 0);
                String versionCode = ""+info.versionCode;
                version = versionCode;
            }
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return "找不到版本号";
        }
    }

    public static AppBean getAppBean() {
        return appBean;
    }

    public static void setAppBean(AppBean appBean) {
        BaseApp.appBean = appBean;
    }

    public static void setBrandUrlArray(List<String> array) {
        BaseApp.brandUrlArray = array;
    }

    public static List<String> getBrandUrlArray() {
        if (brandMap.size() > 0) {
            List<Map.Entry<String, Long>> mapList = new ArrayList<Map.Entry<String, Long>>(brandMap.entrySet());
            Collections.sort(mapList,new Comparator<Map.Entry<String, Long>> (){
                @Override
                public int compare(Map.Entry<String, Long> o1, Map.Entry<String, Long> o2) {
                    return (int)(o1.getValue() - o2.getValue());
                }
            });

            List<String> brandUrl = new ArrayList<>();
            for(int i = 0; i < mapList.size(); ++i) {
                brandUrl.add(mapList.get(i).getKey());
            }

            return brandUrl;
        }

        return brandUrlArray;
    }

    public static void setBrandMap(Map<String,Long> map) {
        BaseApp.brandMap = map;
    }

    private static Activity mCurrentActivity = null;
    public static Activity getCurrentActivity(){
        return mCurrentActivity;
    }
    public static void setCurrentActivity(Activity CurrentActivity){
        mCurrentActivity = CurrentActivity;
    }

    public static String getHecReplaceString(String text) {
        return text.replace("和盛","聚星");
    }

    public static boolean isPasswordEasy(String password) {
        if (password.equals("a123456") || password.equals("123456") || password.equals("a654321") || password.equals("654321")) {
            return true;
        }
        else if (!password.isEmpty() && password.length() >= 6) {
            char[] passwordChar = password.toCharArray();
            for (char c : passwordChar) {
                if (passwordChar[0] != c) {
                    return false;
                }
            }

            return true;
        }

        return false;
    }

    public interface OnChangeUrlListener{
        void changeSuccess();
        void changeFail();
    }

    private static void getAvailableApiUrl(final Context context) {
        isGetAvailableApiUrlError = false;
        isGettingAvailableApiUrl = true;
        MyAsyncTask<AppBean> task = new MyAsyncTask<AppBean>(context) {
            @Override
            public AppBean callService() throws IOException, JsonParseException, BizException, ServiceException {
                return new HomeService().getAppInfo();
            }

            @Override
            public void onLoaded(AppBean result) throws Exception {
                if (context == null) {
                    isGettingAvailableApiUrl = false;
                    return;
                }
                if (!isGetAvailableApiUrlError) {
                    final List<RestfulApiUrlEntity> list = new ArrayList<>();
                    if (result != null && result.getApiUrl() != null) {
                        String mainApiUrl = result.getApiUrl().contains("http") ? result.getApiUrl() : "http://" + result.getApiUrl();
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
                    getApiResponseTime(context, list);
                } else {
                    final List<RestfulApiUrlEntity> list = new ArrayList<>();
                    for (String url : UrlConfig.apiList) {
                        list.add(new RestfulApiUrlEntity(url, true));
                    }
                    if (appBean == null) {
                        appBean = new AppBean();
                    }
                    BaseApp.getAppBean().setAvailableUrlList(list);
                    getApiResponseTime(context, list);

                }

            }
        };
        task.setOnError(new MyAsyncTask.OnError() {
            @Override
            public void handleError(Exception e) {
                isGetAvailableApiUrlError = true;
            }
        });
        task.executeTask();
    }

    private static void getApiResponseTime(Context context, final List<RestfulApiUrlEntity> list) {
        availableApiUrlCount = list.size();
        if (true) {//connectonCount != 0 && !BuildConfig.DEBUG) {
            for (final RestfulApiUrlEntity info : list) {
                final long requestTime = System.currentTimeMillis();
                MyAsyncTask<Response> task = new MyAsyncTask<Response>(context) {
                    @Override
                    public Response callService() throws IOException, JsonParseException, BizException, ServiceException {
                        return new HomeService().testURLSpeed(info.getUrl());
                    }

                    @Override
                    public void onLoaded(Response paramT) throws Exception {
                        info.setResponseTime(System.currentTimeMillis() - requestTime);
                        availableApiUrlCount--;
                        if (availableApiUrlCount == 0) {
                            Collections.sort(list);
                            isGettingAvailableApiUrl = false;
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
        }
    }

    private static void setResfulUrlUnavailable() {
        for (RestfulApiUrlEntity entity : appBean.getAvailableUrlList()) {
            if (entity.isAvailable() && entity.getUrl().equals(BaseService.RESTFUL_SERVICE_HOST)) {
                entity.setAvailable(false);
            }
        }
    }

    private static boolean changeNextUrl(final Activity activity) {
        for (RestfulApiUrlEntity entity : appBean.getAvailableUrlList()) {
            if (entity.isAvailable()) {
                BaseService.RESTFUL_SERVICE_HOST = entity.getUrl();
                return true;
            }
        }
        return false;
    }

    private static void waittingTestConnection(final Activity activity, final OnChangeUrlListener onChangeUrlListener) {
        final ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(1);
        scheduledThreadPool.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if (!isGettingAvailableApiUrl) {
                    boolean hasAvailableApiUrl = changeNextUrl(activity);
                    if (!hasAvailableApiUrl) {
                        //TODO no available line error!!
                        showNoAvailableApiUrlErrorDialog(activity);
                        onChangeUrlListener.changeFail();
                    } else {
                        onChangeUrlListener.changeSuccess();
                    }
                    scheduledThreadPool.shutdownNow();
                }
            }
        }, 0,200, TimeUnit.MILLISECONDS);
    }

    public static void changeUrl(final Activity activity, final OnChangeUrlListener onChangeUrlListener) {
        retryCount++;
        if (retryCount == 1) {
            setResfulUrlUnavailable();
            boolean hasAvailableApiUrl = changeNextUrl(activity);
            getAvailableApiUrl(activity);
            if (!hasAvailableApiUrl) {
                //TODO no available line error!!
                waittingTestConnection(activity, onChangeUrlListener);
            } else {
                onChangeUrlListener.changeSuccess();
            }
        } else {
            waittingTestConnection(activity, onChangeUrlListener);
        }
    }

    public static void showNoAvailableApiUrlErrorDialog(final Activity activity) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(activity)
                        .setTitle("错误")
                        .setMessage(activity.getString(R.string.error_message_network))
                        .setPositiveButton(activity.getString(R.string.dialog_determine), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 20180725 根據GD指示代理反應關閉APP作法感受不好,故將此code mark
//                                Intent intent= new Intent(Intent.ACTION_MAIN);
//                                intent.addCategory(Intent.CATEGORY_HOME);
//                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                activity.startActivity(intent);
//                                activity.finish();
//                                android.os.Process.killProcess(android.os.Process.myPid());
                                dialog.dismiss();
                            }
                        })
                        .setCancelable(false);
                dialog.show();
            }
        });

    }

}