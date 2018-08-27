package com.hec.app.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.*;
import android.os.Process;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonParseException;
import com.hec.app.BuildConfig;
//import com.hec.app.MyCustomNativeScriptActivity;
import com.hec.app.R;
import com.hec.app.activity.base.BaseActivityWithMenu;
import com.hec.app.activity.base.BaseApp;
import com.hec.app.config.CommonConfig;
import com.hec.app.customer_service.CustomerServiceActivity;
import com.hec.app.customer_service.CustomerServiceFragment;
import com.hec.app.dialog.CustomerServiceAlertDialog;
import com.hec.app.dialog.WebchatDialog;
import com.hec.app.entity.BalanceInfo;
import com.hec.app.entity.BizException;
import com.hec.app.entity.CustomerInfo;
import com.hec.app.entity.LotteryInfo;
import com.hec.app.entity.Response;
import com.hec.app.entity.SecurityInfoFinishInfo;
import com.hec.app.entity.VipInfo;
import com.hec.app.fragment.AllLotteryFragment;
import com.hec.app.fragment.ArticalFragment;
import com.hec.app.fragment.BrandFragment;
import com.hec.app.fragment.CurrentLotteryFragment;
import com.hec.app.fragment.GuajiFragment;
import com.hec.app.fragment.HomeFragment;
import com.hec.app.fragment.RecordListFragment;
import com.hec.app.fragment.WebchatFragment;
import com.hec.app.framework.http.OkHttpClientManager;
import com.hec.app.framework.widget.MyToast;
import com.hec.app.framework.widget.OnLotteryTypeClickedListener;
import com.hec.app.framework.widget.ResideMenu;
import com.hec.app.lottery.LotteryConfig;
import com.hec.app.util.BitmapUtil;
import com.hec.app.util.CustomerAccountManager;
import com.hec.app.util.DialogUtil;
import com.hec.app.util.IntentUtil;
import com.hec.app.util.KeyBoardListener;
import com.hec.app.util.MessagePushTask;
import com.hec.app.util.MyAsyncTask;
import com.hec.app.util.SlotUtl;
import com.hec.app.util.TestUtil;
import com.hec.app.util.URLPickingUtil;
import com.hec.app.webservice.AccountService;
import com.hec.app.webservice.BaseService;
import com.hec.app.webservice.DownLoadService;
import com.hec.app.webservice.HomeService;
import com.hec.app.webservice.LotteryService;
import com.hec.app.webservice.RealmanDownLoad;
import com.hec.app.webservice.ServiceException;
//import com.umeng.analytics.MobclickAgent;
import com.unity3d.player.UnityPlayer;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


/**
 * Created by asianark on 26/2/16.
 */
public class HomeActivity extends BaseActivityWithMenu implements OnLotteryTypeClickedListener {
    private RadioGroup myTabRg;
    private HomeFragment home;
    private AllLotteryFragment lottery;
    private AllLotteryFragment trend;
    private CurrentLotteryFragment current_lottery;
    private BrandFragment current_brand;
    private CustomerServiceFragment customerServiceFragment;
    private WebchatFragment webchatFragment;
    private GuajiFragment guajiFragment;
    private ImageView imgPerson;
    private int tabIds[] = new int[]{
            R.id.rbHome,
            R.id.rbLottery,
            R.id.rbTrend,
            //R.id.rbBrand,
            R.id.rb_guaji,
            R.id.rbWebchat,
    };
    private int currentTabId = tabIds[0];
    private ResideMenu resideMenu;
    private LinearLayout afterHistoryLayout;
    private LinearLayout lotteryHistoryLayout;
    private LinearLayout SettingLayout;
    private RelativeLayout RetrievalLayout;
    private LinearLayout articalLayout;
    private LinearLayout moneyDetailLayout;
    private LinearLayout agentCenterLayout;
    private RelativeLayout rechargeLayout;
    private RelativeLayout withdrawLayout;
    private RelativeLayout transeferLayout;
    private LinearLayout backToLottery;
    private LinearLayout home_main;
    private Boolean mIsError;
    private int errorCount = 0;
    private RealmanDownLoad.RealmanBinder myBinder;
    private DownLoadService.MyBinder myBinder2;
    private Handler mHandler;
    private boolean isDownLoading = false;
    public static boolean backtwice = false;
    private long realrandomTime;
    private TextView connect_service;
    private SharedPreferences token;
    private MessagePushTask mMessagePushTask;
    private static final String BACCARAT = "baccarat";
    //private com.tns.Runtime nsRuntime;
    private enum VIPServerLoginEntrance {
        NORMAL,
        HOME_TAB,
        SLIDE_MENU
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_new);
//        findViewById(R.id.imgLogo).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (nsRuntime == null) {
//                    nsRuntime = com.tns.RuntimeHelper.initRuntime(HomeActivity.this.getApplication());
//                    if (nsRuntime != null) {
//
//                        nsRuntime.run();
//                    } else {
//                        return;
//                    }
//                }
//
//                android.content.Intent intent = new android.content.Intent(HomeActivity.this, MyCustomNativeScriptActivity.class);
//                intent.setAction(android.content.Intent.ACTION_DEFAULT);
//                startActivity(intent);
//            }
//        });
        if (getIntent().getBooleanExtra("badslot", false)) {
            Intent intent = new Intent(HomeActivity.this, RecordListActivity.class);
            intent.putExtra(RecordListFragment.ARGUMENT, "投注记录");
            intent.putExtra("badslot", true);
            startActivity(intent);
        }
        BaseApp.rootActivity = HomeActivity.this;
        ActivityManager activityManager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        Log.i("wxj", "heap size:" + activityManager.getMemoryClass());
        //EventBus.getDefault().register(this);
        checkUpdate();
        resideMenu = super.getResidingMenu();
        imgPerson = (ImageView) findViewById(R.id.imgPersonalCenter);
        imgPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resideMenu.openMenu(ResideMenu.DIRECTION_RIGHT);
            }
        });
        home = new HomeFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.main_content, home).commit();

        currentTabId = tabIds[0];
        myTabRg = (RadioGroup) findViewById(R.id.tab_menu);
        for (final int id : tabIds) {
            RadioButton radioButton = (RadioButton) findViewById(id);
            radioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentTabId = id;
                    setSelection(id);
                }
            });
        }
        setSelection(currentTabId);

        afterHistoryLayout = (LinearLayout) resideMenu.getRightMenuView().findViewById(R.id.sliding_menu_after_layout);
        lotteryHistoryLayout = (LinearLayout) resideMenu.getRightMenuView().findViewById(R.id.sliding_menu_lottery_layout);
        SettingLayout = (LinearLayout) resideMenu.getRightMenuView().findViewById(R.id.sliding_menu_setting);
        moneyDetailLayout = (LinearLayout) resideMenu.getRightMenuView().findViewById(R.id.sliding_menu_money_layout);
        agentCenterLayout = (LinearLayout) resideMenu.getRightMenuView().findViewById(R.id.sliding_menu_agent_layout);
        articalLayout = (LinearLayout) resideMenu.getRightMenuView().findViewById(R.id.sliding_menu_artical_layout);
        rechargeLayout = (RelativeLayout) resideMenu.getRightMenuView().findViewById(R.id.sliding_menu_recharge);
        withdrawLayout = (RelativeLayout) resideMenu.getRightMenuView().findViewById(R.id.sliding_menu_withdraw);
        backToLottery = (LinearLayout) resideMenu.getRightMenuView().findViewById(R.id.backToLottery);
        transeferLayout = (RelativeLayout) resideMenu.getRightMenuView().findViewById(R.id.sliding_menu_transfer);
        connect_service = (TextView) resideMenu.getRightMenuView().findViewById(R.id.connect_service);
        home_main = (LinearLayout) findViewById(R.id.home_main_layout);
        try {
            Bitmap b = BitmapUtil.readBitMap(this, R.mipmap.bg2, Bitmap.Config.ALPHA_8);
            findViewById(R.id.main_content).setBackground(new BitmapDrawable(getResources(), b));
        } catch (OutOfMemoryError oom) {
            MyToast.show(this, "请您清理内存!");
        }
        setHandler();
        checkBasicData();
        sendHeartBeat();
        getBalance();
        Log.i("wxj", "oncreate heartbeat");

        CustomerInfo customer = CustomerAccountManager.getInstance().getCustomer();
        if (customer != null && customer.getUserID() != null) {
            mMessagePushTask = new MessagePushTask(this, customer.getUserID());
            mMessagePushTask.execute();
        }

        isMoneyPwdEasy();
        isVIP();
        //KeyBoardListener.getInstance(this).init();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void setSelection(int id) {
        if (imgPerson != null) {
            imgPerson.setVisibility(View.VISIBLE);
        }
        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        switch (id) {
            case R.id.rbHome:
                hideAllFragments(ft);
                if (home == null) {
                    home = new HomeFragment();
                    ft.add(R.id.main_content, home);
                } else {
                    ft.show(home);
                }
                ft.commit();
                break;

            case R.id.rbLottery:
                hideAllFragments(ft);
                if (lottery == null) {
                    lottery = new AllLotteryFragment();
                    ft.add(R.id.main_content, lottery);
                } else {
                    ft.show(lottery);
                }
                ft.commit();
                break;

            case R.id.rbTrend:
                hideAllFragments(ft);
                if (current_lottery == null) {
                    current_lottery = new CurrentLotteryFragment();
                    ft.add(R.id.main_content, current_lottery);
                } else {
                    ft.show(current_lottery);
                }
                ft.commit();
                break;

//            case R.id.rbBrand:
//                hideAllFragments(ft);
//                if (current_brand == null) {
//                    current_brand = new BrandFragment();
//                    ft.add(R.id.main_content, current_brand);
//                } else {
//                    ft.show(current_brand);
//                }
//                ft.commit();
//                break;
            case R.id.rb_guaji:
                hideAllFragments(ft);
                if (guajiFragment == null) {
                    guajiFragment = new GuajiFragment();
                    ft.add(R.id.main_content, guajiFragment);
                } else {
                    ft.show(guajiFragment);
                }
                ft.commit();
                break;
            case R.id.rbWebchat:
                CustomerInfo customer = CustomerAccountManager.getInstance().getCustomer();
                if (customer != null && customer.isVIP()) {
                    myTabRg.clearCheck();
                    myTabRg.check(currentTabId);

                    new WebchatDialog(HomeActivity.this, new WebchatDialog.OnGoToWebChatListener() {
                        @Override
                        public void onClick(int type) {
                            if (type == 1) {
                                myTabRg.clearCheck();
                                myTabRg.check(R.id.rbWebchat);
                                checkIsVIPServerStatus(VIPServerLoginEntrance.HOME_TAB);
                            } else {
                                hideAllFragments(ft);
                                if (webchatFragment == null) {
                                    webchatFragment = new WebchatFragment();
                                    webchatFragment.setWebchatType(CommonConfig.WEBCHAT_TYPE_NORMAL);
                                    ft.add(R.id.main_content, webchatFragment);
                                    ft.commit();
                                } else {
                                    webchatFragment.setWebchatType(CommonConfig.WEBCHAT_TYPE_NORMAL);
                                    ft.show(webchatFragment);
                                    ft.commit();
                                }
                            }
                        }
                    }).show();
                } else {
                    DialogUtil.getCustomerServiceDialog(HomeActivity.this, new CustomerServiceAlertDialog.CustomerServiceAlertDialogListener() {
                        @Override
                        public void onClick() {
                            hideAllFragments(ft);
                            if (webchatFragment == null) {
                                webchatFragment = new WebchatFragment();
                                webchatFragment.setWebchatType(CommonConfig.WEBCHAT_TYPE_NORMAL);
                                ft.add(R.id.main_content, webchatFragment);
                                ft.commit();
                            } else {
                                webchatFragment.setWebchatType(CommonConfig.WEBCHAT_TYPE_NORMAL);
                                ft.show(webchatFragment);
                                ft.commit();
                            }
                        }
                    }).show();
                }
                break;
            default:
                break;
        }
    }

    private void checkIsVIPServerStatus(VIPServerLoginEntrance vipServerLoginEntrance) {
        CustomerInfo customer = CustomerAccountManager.getInstance().getCustomer();
        if (customer != null) {
            if (customer.isVIPLonIn() && URLPickingUtil.getInstance().getVipMqUrl() != null && !URLPickingUtil.getInstance().getVipMqUrl().isEmpty()) {
                switch (vipServerLoginEntrance) {
                    case HOME_TAB:
                        FragmentManager fm = getSupportFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();
                        hideAllFragments(ft);
                        if (customerServiceFragment == null) {
                            customerServiceFragment = new CustomerServiceFragment();
                            ft.add(R.id.main_content, customerServiceFragment);
                        } else {
                            ft.show(customerServiceFragment);
                        }
                        ft.commit();
                        break;
                    case SLIDE_MENU:
                        Intent intent = new Intent();
                        intent.setClass(HomeActivity.this, CustomerServiceActivity.class);
                        intent.putExtra(CommonConfig.WEBCHAT_TYPE, CommonConfig.WEBCHAT_TYPE_NORMAL);
                        startActivity(intent);
                        break;
                    default:
                        break;
                }
            } else if (!customer.isVIPLonIn()) {
                vipServerLogin(vipServerLoginEntrance);
            } else {
                getVipMqUrl(vipServerLoginEntrance);
            }
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        TestUtil.print("new home activity onStop");
        try {
            if (connection != null) {
                unbindService(connection);
            }
            if (isDownLoading) {
                ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).cancel(15);
                TestUtil.print("delete ag");
                new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/hec" + "/ag_setup" + ".apk").delete();
            }
        } catch (IllegalArgumentException e) {
            //catch first,cause I can not find why the service unregister :(
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TestUtil.print("new home activity onDestroy");
        if (mMessagePushTask != null) {
            mMessagePushTask.closeRabbitMQ();
        }

        if (connection2 != null) {
            unbindService(connection2);
        }
    }

    public void setDownLoading(boolean b) {
        isDownLoading = b;
    }

    @Override
    protected void onPause() {
        super.onPause();

        //unbindService(connection);
        TestUtil.print("new home activity onPause");
    }


    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private static final int REQUEST_CODE = 0x11;


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // save file
            } else {
                Toast.makeText(this, "PERMISSION_DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void checkBasicData() {
        List<LotteryInfo> list = null;
        try {
            list = new LotteryService().getLotteryInfo(LotteryConfig.PLAY_MODE.CLASSIC);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ServiceException e) {
            e.printStackTrace();
        } catch (JsonParseException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onClicked(final int lotteryID, final String typeurl) {
        if (lotteryID > 0 && lotteryID < 100) {
            final Bundle bundle = new Bundle();
            bundle.putInt("LotteryID", lotteryID);
            bundle.putString("typeurl", typeurl);

            boolean isClassicPlayTypesInfo = true;
            try {
                isClassicPlayTypesInfo = new LotteryService().isClassicPlayTypesInfo(lotteryID);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            if (isClassicPlayTypesInfo) {
                bundle.putInt("PlayMode", LotteryConfig.PLAY_MODE.CLASSIC);
            } else {
                bundle.putInt("PlayMode", LotteryConfig.PLAY_MODE.EXPERT);
            }
            IntentUtil.redirectToNextActivity(HomeActivity.this, LotteryActivity.class, bundle);
        } else if (lotteryID == LotteryConfig.LOTTERY_ID.REAL_MAN) {
            //MyToast.show(this,"开始下载真人娱乐!");
            checkIfDownlaodorPlay();
        } else if (lotteryID == LotteryConfig.LOTTERY_ID.SLOT) {
            //MyToast.show(this,"TIGER!");
            //MyToast.show(HomeActivity.this,"即将上线，敬请期待！");
            goTiger();
        } else if (lotteryID == LotteryConfig.LOTTERY_ID.BAIJIALE) {
            //MyToast.show(HomeActivity.this,"多人百家乐即将上线！");
            IntentUtil.redirectToNextActivity(HomeActivity.this, SlotNewActivity.class);
        }
//        else if (lotteryID == LotteryConfig.LOTTERY_ID.CHATROOM_BJL) {
//            Intent intent = new Intent();
//            intent.putExtra("typeurl", BACCARAT);
//            intent.setClass(HomeActivity.this,BJLActivity.class);
//            startActivity(intent);
//        }
        else if (lotteryID == -1) {   //星女孩
            IntentUtil.redirectToNextActivity(HomeActivity.this, BrandActivity.class);
        }
        else {
            //Intent intent = new Intent(HomeActivity.this, OfenPlayLotteryActivity.class);
            //startActivity(intent);
            IntentUtil.redirectToNextActivity(HomeActivity.this, OfenPlayLotteryActivity.class);
        }
    }

    private boolean isAvilible(Context context, String packageName) {
        final PackageManager packageManager = context.getPackageManager();
        // 获取所有已安装程序的包信息
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        for (int i = 0; i < pinfo.size(); i++) {
            if (pinfo.get(i).packageName.equalsIgnoreCase(packageName))
                return true;
        }
        return false;
    }

    private boolean mIsWait = false;

    private void goTiger() {
//        if(!mIsWait) {
//            mIsWait = true;
//            mIsError = false;
//
//            MyAsyncTask<BalanceInfo> task = new MyAsyncTask<BalanceInfo>(HomeActivity.this) {
//                @Override
//                public BalanceInfo callService() throws IOException, JsonParseException, BizException, ServiceException {
//                    return new AccountService().getBalance();
//                }
//
//                @Override
//                public void onLoaded(BalanceInfo data) throws Exception {
//                    mIsWait = false;
//                    if (!mIsError) {
        String AASlotUrl = BaseService.SLOT_URL;
        if (!AASlotUrl.contains("http://")) {
            AASlotUrl = "http://" + AASlotUrl;
        }
        if (AASlotUrl.charAt(AASlotUrl.length() - 1) != '/') {
            AASlotUrl = AASlotUrl + "/";
        }


        Bundle bundle = new Bundle();
        bundle.putString(CommonConfig.BUNDLE_GOTIGER_USERNAME, OkHttpClientManager.getInstance().getUserName());
        bundle.putInt(CommonConfig.BUNDLE_GOTIGER_BALANCE, (int) BaseService.BASE_BALANCE);
        bundle.putString(CommonConfig.BUNDLE_GOTIGER_AASLOTURL, AASlotUrl);
        bundle.putString(CommonConfig.BUNDLE_GOTIGER_SCENE, CommonConfig.DEFAULT_TAG);
        IntentUtil.redirectToNextActivity(HomeActivity.this, SlotActivity.class, bundle);
        String slotData = SlotUtl.buildDataAccordingToScene(
                CommonConfig.DEFAULT_TAG,
                OkHttpClientManager.getInstance().getUserName(),
                AASlotUrl,
                BaseService.BASE_BALANCE
        );
        UnityPlayer.UnitySendMessage("Preload"
                , "getIntentData"
                , slotData);
        finish();
//                        }
//                }
//            };
//            task.setOnError(new MyAsyncTask.OnError() {
//                @Override
//                public void handleError(Exception e) {
//                    mIsError = true;
//                }
//            });
//            task.executeTask();
//        }
    }

    private void checkIfDownlaodorPlay() {


//        Intent i = new Intent(Intent.ACTION_MAIN);
//        i.addCategory(Intent.CATEGORY_LAUNCHER);
//        try{
//            ComponentName cn = new ComponentName("com.aaslot.androidapp",
//                    "com.aaslot.androidapp.index.IndexActivity");
//            i.setComponent(cn);
//            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            i.setType("text/plain");
//            i.putExtra("username", "dk");
//            i.putExtra("token","BD4B89601F6146B5581E443F8D506DFC");
//            i.putExtra("merchant_code", "201703101");
//            i.putExtra("sign", "5AE64DF104F98681A6A028BBCFF9C4FC");
//            startActivityForResult(i, RESULT_OK);
//        }catch (ActivityNotFoundException e){
//            MyToast.show(HomeActivity.this,"您的AA老虎机安装出现问题！");
//        }


        if (isAvilible(HomeActivity.this, "com.aggaming.androidapp")) {
            Intent i = new Intent(Intent.ACTION_MAIN);
            i.addCategory(Intent.CATEGORY_LAUNCHER);
            try {
                ComponentName cn = new ComponentName("com.aggaming.androidapp",
                        "com.aggaming.androidapp.login.LoginActivity");
                i.setComponent(cn);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.setType("text/plain");
                i.putExtra("username", CustomerAccountManager.getInstance().getCustomer().getUserName());
                i.putExtra("token", "bs");
                i.putExtra("merchant_code", "201703101");
                startActivityForResult(i, RESULT_OK);
            } catch (ActivityNotFoundException e) {
                MyToast.show(HomeActivity.this, "您的AG真人游戏安装出现问题！");
            }
        } else {
            if (!isDownLoading) {
                //final String downloadUrl = "http://ihome.ust.hk/~xwangcc/AG_setup.apk";
                final String downloadUrl = "http://agmbet.com/universal/AG_setup.apk";
                android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(HomeActivity.this)
                        .setTitle("您还没有下载或安装AG真人娱乐!")
                        .setMessage("请下载安装后使用")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                                    MyToast.show(HomeActivity.this, getResources().getString(R.string.error_message_sd_card));
                                } else if (new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/hec" + "/ag_setup" + ".apk").exists()) {
                                    Uri uri = Uri.fromFile(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/hec" + "/ag_setup" + ".apk"));
                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.setDataAndType(uri, "application/vnd.android.package-archive");
                                    startActivity(intent);
                                } else {
                                    String filepath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/hec";
                                    String filename = "ag_setup" + ".apk";
                                    startDownload(downloadUrl, filename, filepath);
                                    isDownLoading = true;
                                    Log.i("real", "start download");
                                }
                            }
                        }).setCancelable(false);

                if (BuildConfig.DEBUG || BuildConfig.SIT || BuildConfig.UAT) {
                    dialog.setNegativeButton("取消", null);
                }
                dialog.show();
            } else {
                MyToast.show(HomeActivity.this, "正在下载真人娱乐,请耐心等待");
            }
        }
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        if (home == null && fragment instanceof HomeFragment) {
            home = (HomeFragment) fragment;
        } else if (lottery == null && fragment instanceof AllLotteryFragment) {
            lottery = (AllLotteryFragment) fragment;
        } else if (current_lottery == null && fragment instanceof CurrentLotteryFragment) {
            current_lottery = (CurrentLotteryFragment) fragment;
//        } else if (current_brand == null && fragment instanceof BrandFragment) {
//            current_brand = (BrandFragment) fragment;
        } else if (webchatFragment == null && fragment instanceof WebchatFragment) {
            webchatFragment = (WebchatFragment) fragment;
        } else if (customerServiceFragment == null && fragment instanceof CustomerServiceFragment) {
            customerServiceFragment = (CustomerServiceFragment) fragment;
        } else if (guajiFragment == null && fragment instanceof CustomerServiceFragment) {
            guajiFragment = (GuajiFragment) fragment;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (isExitingAppliation(keyCode, event)) {
//            if (needConfirmWhenExit()) {
//                buildExitConfirmDialog().show();
//            } else {
//                killProcessAndExit();
//                return true;
//            }
//        }
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (backtwice && MyToast.isShowing()) {
                backtwice = false;  //reset here
                killProcessAndExit();
            } else {
                backtwice = true;
                MyToast.show(HomeActivity.this, "再次点击退出！");
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private boolean needConfirmWhenExit() {
        SharedPreferences settings = getBaseContext().getSharedPreferences(CommonConfig.KEY_SETTING_PREFERENCE, MODE_PRIVATE);
        return settings.getBoolean(CommonConfig.KEY_CONFIRM_WHEN_EXIT, true);
    }

    private Dialog buildExitConfirmDialog() {
        //change to double click to exit
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_leave_title);
        builder.setMessage(R.string.dialog_leave_message);
        builder.setPositiveButton(R.string.dialog_determine, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                killProcessAndExit();
            }
        });
        builder.setNegativeButton(R.string.dialog_cancel, null);

        return builder.create();
    }

    private void killProcessAndExit() {
        setApplicationFirst();
        moveTaskToBack(true);
        if (BaseApp.CHEN_MODE) {
            token = getSharedPreferences(CommonConfig.KEY_TOKEN, MODE_PRIVATE);
            token.edit().putString(CommonConfig.KEY_TOKEN_TOKENS, "").commit();
        }
        if (BaseApp.activityList.size() != 0) {
            for (Activity activity : BaseApp.activityList) {
                if (activity != null) {
                    activity.finish();
                }
            }
        }
        android.os.Process.killProcess(Process.myPid());
        System.exit(10);
    }

    private boolean isExitingAppliation(int keyCode, KeyEvent event) {
        return keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0;
    }

    private void hideAllFragments(final FragmentTransaction ft) {
        if (home != null) {
            ft.hide(home);
        }
        if (lottery != null) {
            ft.hide(lottery);
        }
        if (current_lottery != null) {
            ft.hide(current_lottery);
        }
//        if (current_brand != null) {
//            ft.hide(current_brand);
//        }
        if (customerServiceFragment != null) {
            ft.hide(customerServiceFragment);
        }
        if (webchatFragment != null) {
            ft.hide(webchatFragment);
        }
        if (guajiFragment != null) {
            ft.hide(guajiFragment);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == lotteryHistoryLayout) {
            IntentUtil.redirectToNextActivity(HomeActivity.this, RecordListActivity.class,
                    RecordListFragment.ARGUMENT, "投注记录");
        } else if (v == afterHistoryLayout) {
            IntentUtil.redirectToNextActivity(HomeActivity.this, RecordListActivity.class,
                    RecordListFragment.ARGUMENT, "追号记录");
        } else if (v == moneyDetailLayout) {
            IntentUtil.redirectToNextActivity(HomeActivity.this, MoneyActivity.class);
        } else if (v == SettingLayout) {
            IntentUtil.redirectToNextActivity(HomeActivity.this, SettingActivity.class);
        } else if (v == articalLayout) {
            IntentUtil.redirectToNextActivity(HomeActivity.this, ArticalActivity.class,
                    ArticalFragment.ARGUMENT, "购彩交流");
        } else if (v == withdrawLayout) {
            IntentUtil.redirectToNextActivity(HomeActivity.this, WithdrawActivity.class);
        } else if (v == rechargeLayout) {
            IntentUtil.redirectToNextActivity(HomeActivity.this, RechargeMainActivity.class);
        } else if (v == agentCenterLayout) {
            IntentUtil.redirectToNextActivity(HomeActivity.this, ProxyListActivity.class,
                    "Param1", "Param2");
        } else if (v == backToLottery) {
            if (BaseApp.trace != null) {
                Intent it = new Intent();
                it.setClass(HomeActivity.this, LotteryActivity.class);
                it.putExtra("LotteryID", BaseApp.trace.getLotteryId());
                it.putExtra("PlayTypeID", BaseApp.trace.getPlayTypeId());
                it.putExtra("PlayTypeRadioID", BaseApp.trace.getPlayTypeRadioId());
                it.putExtra("PlayMode", BaseApp.trace.getPlayMode());
                startActivity(it);
            } else {
                resideMenu.closeMenu();
            }
        } else if (v == transeferLayout) {
            //MyToast.show(this,"暂不支持此功能!");
            IntentUtil.redirectToNextActivity(HomeActivity.this, TransferActivity.class);
        } else if (v == connect_service) {
            CustomerInfo customer = CustomerAccountManager.getInstance().getCustomer();
            if (customer != null && customer.isVIP()) {
                new WebchatDialog(HomeActivity.this, new WebchatDialog.OnGoToWebChatListener() {
                    @Override
                    public void onClick(int type) {
                        if (type == 1) {
                            checkIsVIPServerStatus(VIPServerLoginEntrance.SLIDE_MENU);
                        } else {
                            Intent intent = new Intent();
                            intent.setClass(HomeActivity.this, WebchatActivity.class);
                            intent.putExtra(CommonConfig.WEBCHAT_TYPE, CommonConfig.WEBCHAT_TYPE_NORMAL);
                            startActivity(intent);
                        }
                    }
                }).show();
            } else {
                DialogUtil.getCustomerServiceDialog(HomeActivity.this, new CustomerServiceAlertDialog.CustomerServiceAlertDialogListener() {
                    @Override
                    public void onClick() {
                        Intent intent = new Intent();
                        intent.setClass(HomeActivity.this, WebchatActivity.class);
                        intent.putExtra(CommonConfig.WEBCHAT_TYPE, CommonConfig.WEBCHAT_TYPE_NORMAL);
                        startActivity(intent);
                    }
                }).show();
            }
        }
    }

    private void sendHeartBeat() {
        mIsError = false;
        MyAsyncTask<Response<?>> task = new MyAsyncTask<Response<?>>(HomeActivity.this) {
            @Override
            public Response<?> callService() throws IOException,
                    JsonParseException, BizException, ServiceException {
                return new AccountService().sendHeartBeat();
                //return new HomeService().getServerTime();
            }

            @Override
            public void onLoaded(Response<?> result) throws Exception {
                if (HomeActivity.this == null || HomeActivity.this.isFinishing())
                    return;
                Message message = new Message();
                message.what = 1;
                Random random = new Random();
                int randomTime = random.nextInt(40) + 30;
                realrandomTime = randomTime * 1000;
                //mHandler.sendMessageDelayed(message, realrandomTime);
                mHandler.sendMessageDelayed(message, realrandomTime);
                Map<String, String> map = new HashMap<>();
                map.put("urlinuse", BaseService.RESTFUL_SERVICE_HOST);
            }
        };
        task.setOnError(new MyAsyncTask.OnError() {
            @Override
            public void handleError(Exception e) {
                mIsError = true;
            }
        });
        task.executeTask();
    }

    ServiceConnection connection;

    public void startDownload(final String url, final String filename, final String path) {
        Intent startIntent = new Intent(this, RealmanDownLoad.class);
        connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                myBinder = (RealmanDownLoad.RealmanBinder) service;
                myBinder.startDownload(HomeActivity.this, filename, path, url);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
            }
        };
        bindService(startIntent, connection, BIND_AUTO_CREATE);
    }

    private void setHandler() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg != null) {
                    if (CustomerAccountManager.getInstance().getCustomer() != null) {
                        sendHeartBeat();
                    }
                }
            }
        };
    }

    public void checkUpdate() {
        if (BaseApp.getAppBean() != null) {
            try {
                final int v = Integer.parseInt(BaseApp.getAppBean().getAaMinAvailableVersion());
                final int cV = Integer.parseInt(BaseApp.instance().getVersionCode());
                TestUtil.print("before invalid");
                TestUtil.print("v:" + v + ",cV:" + cV);

                if (cV < v) {
                    //final String downloadUrl = "http://ceshi.lisun1.com/app-29.apk";//
                    final String downloadUrl = BaseApp.getAppBean().getAppUrl();
                    android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(HomeActivity.this)
                            .setTitle(R.string.dialog_update_version_title)
                            .setMessage(R.string.dialog_update_version_message)
                            .setPositiveButton(R.string.dialog_determine, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                                        MyToast.show(HomeActivity.this, getResources().getString(R.string.error_message_sd_card));
                                    } else if (new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/hec" + "/hec-" + v + ".apk").exists()) {
                                        Uri uri = Uri.fromFile(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/hec" + "/hec-" + v + ".apk"));
                                        Intent intent = new Intent(Intent.ACTION_VIEW);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.setDataAndType(uri, "application/vnd.android.package-archive");
                                        startActivity(intent);
                                    } else {
                                        String filepath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/hec";
                                        String filename = "hec-" + v + ".apk";
                                        startDownload2(downloadUrl, filename, filepath);
                                    }
                                }
                            }).setCancelable(false);

                    if (BuildConfig.DEBUG || BuildConfig.SIT || BuildConfig.UAT) {
                        dialog.setNegativeButton(R.string.dialog_cancel, null);
                    }
                    dialog.show();
                }
            } catch (Exception e) {
                TestUtil.print(e.getLocalizedMessage());
            }
        }
    }


    ServiceConnection connection2;

    public void startDownload2(final String url, final String filename, final String path) {
        //do in backgroud

        Intent startIntent = new Intent(this, DownLoadService.class);
        connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                myBinder2 = (DownLoadService.MyBinder) service;
                myBinder2.startDownload(HomeActivity.this, filename, path, url);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
        bindService(startIntent, connection, BIND_AUTO_CREATE);
    }

    public void saveStupidPasswordFlag(boolean isFixMoneyPassword) {
        SharedPreferences token = getSharedPreferences("token", MODE_PRIVATE);
        String account = token.getString(CommonConfig.KEY_TOKEN_USER_NAME, "");
        SharedPreferences sharedPreferences = getSharedPreferences(CommonConfig.KEY_DATA, MODE_PRIVATE);
        String password = sharedPreferences.getString(account, "");

        boolean isFix = false;
        if (BaseApp.isPasswordEasy(password)) {
            isFix = true;
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(CommonConfig.KEY_DATA_STUPID_LOGIN_PASSWORD_FLAG, isFix);
        editor.putBoolean(CommonConfig.KEY_DATA_STUPID_MONEY_PASSWORD_FLAG, isFixMoneyPassword);
        editor.putBoolean(CommonConfig.KEY_DATA_STUPID_PASSWORD_SHOW_DIALOG, (isFix && isFixMoneyPassword));
        editor.commit();

        if (isFix) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(HomeActivity.this)
                    .setMessage(getString(R.string.confirm_pw_stupid))
                    .setPositiveButton(R.string.dialog_determine, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            IntentUtil.redirectToNextActivity(HomeActivity.this, SettingActivity.class);
                        }
                    }).setCancelable(false);
            dialog.show();
        } else if (isFixMoneyPassword) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(HomeActivity.this)
                    .setMessage(getString(R.string.confirm_money_pw_stupid))
                    .setPositiveButton(R.string.dialog_determine, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            IntentUtil.redirectToNextActivity(HomeActivity.this, SettingActivity.class);
                        }
                    }).setCancelable(false);
            dialog.show();
        } else {
            isNoPhone();
        }
    }

    private void isMoneyPwdEasy() {
        mIsError = false;
        MyAsyncTask<Response> task = new MyAsyncTask<Response>(HomeActivity.this) {
            @Override
            public Response callService() throws IOException, JsonParseException, BizException, ServiceException {
                return new AccountService().isMoneyPwdEasy();
            }

            @Override
            public void onLoaded(Response data) throws Exception {
                if (HomeActivity.this == null || HomeActivity.this.isFinishing()) {
                    return;
                }

                if (!mIsError) {
                    boolean isFixMoneyPassword = false;
                    if (data.getSuccess()) {
                        isFixMoneyPassword = true;
                    }

                    saveStupidPasswordFlag(isFixMoneyPassword);
                } else {
                    isNoPhone();
                }
            }
        };
        task.setOnError(new MyAsyncTask.OnError() {

            @Override
            public void handleError(Exception e) {
                mIsError = true;
            }
        });
        task.executeTask();
    }

    private void isVIP() {
        mIsError = false;
        MyAsyncTask<VipInfo> task = new MyAsyncTask<VipInfo>(HomeActivity.this) {
            @Override
            public VipInfo callService() throws IOException, JsonParseException, BizException, ServiceException {
                return new AccountService().isVIP();
            }

            @Override
            public void onLoaded(VipInfo data) throws Exception {
                if (HomeActivity.this == null || HomeActivity.this.isFinishing()) {
                    return;
                }

                if (!mIsError) {
                    CustomerInfo customerInfo = CustomerAccountManager.getInstance().getCustomer();
                    if (customerInfo != null) {
//                        customerInfo.setVIP(true);
                        customerInfo.setVIP(data.isVIP());
                    }
                    if (customerInfo.isVIP()) {
                        vipServerLogin(VIPServerLoginEntrance.NORMAL);
                    }
                }
            }
        };
        task.setOnError(new MyAsyncTask.OnError() {

            @Override
            public void handleError(Exception e) {
                mIsError = true;
            }
        });
        task.executeTask();
    }

    private void vipServerLogin(final VIPServerLoginEntrance vipServerLoginEntrance) {
        mIsError = false;
        MyAsyncTask<Response> task = new MyAsyncTask<Response>(HomeActivity.this) {
            @Override
            public Response callService() throws IOException, JsonParseException, BizException, ServiceException {
                return new HomeService().vipServerLogin();
            }

            @Override
            public void onLoaded(Response data) throws Exception {
                if (HomeActivity.this == null || HomeActivity.this.isFinishing()) {
                    return;
                }

                if (!mIsError) {
                    errorCount = 0;
                    CustomerInfo customerInfo = CustomerAccountManager.getInstance().getCustomer();
                    if (customerInfo != null) {
                        customerInfo.setVIPLonIn(data.getSuccess());
                    }
                    if (vipServerLoginEntrance != VIPServerLoginEntrance.NORMAL) {
                        getVipMqUrl(vipServerLoginEntrance);
                    }
                }
            }
        };
        task.setOnError(new MyAsyncTask.OnError() {

            @Override
            public void handleError(Exception e) {
                mIsError = true;
                CustomerInfo customerInfo = CustomerAccountManager.getInstance().getCustomer();
                if (customerInfo != null) {
                    customerInfo.setVIPLonIn(false);
                }
                if (vipServerLoginEntrance != VIPServerLoginEntrance.NORMAL && errorCount < 1) {
                    vipServerLogin(vipServerLoginEntrance);
                } else {
                    Toast.makeText(getApplicationContext(), getResources().getText(R.string.activity_customer_service_error), Toast.LENGTH_SHORT).show();
                }
                errorCount++;
            }
        });
        task.executeTask();
    }

    private void getVipMqUrl(final VIPServerLoginEntrance vipServerLoginEntrance) {
        mIsError = false;
        MyAsyncTask<Response> task = new MyAsyncTask<Response>(HomeActivity.this) {
            @Override
            public Response callService() throws IOException, JsonParseException, BizException, ServiceException {
                return new HomeService().getVipMqUrl();
            }

            @Override
            public void onLoaded(Response data) throws Exception {
                if (HomeActivity.this == null || HomeActivity.this.isFinishing()) {
                    return;
                }

                if (!mIsError) {
                    if (data.getSuccess() && data.getMessage() != null && !data.getMessage().isEmpty()) {
                        URLPickingUtil.getInstance().setVipMqUrl(data.getMessage());
                        if (vipServerLoginEntrance != VIPServerLoginEntrance.NORMAL) {
                            checkIsVIPServerStatus(vipServerLoginEntrance);
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), getResources().getText(R.string.activity_customer_service_error), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };
        task.setOnError(new MyAsyncTask.OnError() {

            @Override
            public void handleError(Exception e) {
                mIsError = true;
                Toast.makeText(getApplicationContext(), getResources().getText(R.string.activity_customer_service_error), Toast.LENGTH_SHORT).show();
            }
        });
        task.executeTask();
    }

    private void isNoPhone() {
        SharedPreferences keyData = getSharedPreferences(CommonConfig.KEY_DATA, MODE_PRIVATE);
        CustomerInfo customerInfo = CustomerAccountManager.getInstance().getCustomer();

        String key = "DATA_INFO_";
        if (customerInfo != null) {
            key += CustomerAccountManager.getInstance().getCustomer().getUserName();
        }
        final SharedPreferences info = getSharedPreferences(key, MODE_PRIVATE);
        boolean isShow = keyData.getBoolean(CommonConfig.KEY_IS_LOGIN, true) && !info.getBoolean(CommonConfig.KEY_CONFIDENTIALITY_DIALOG_CHECKBOX, false);

        SharedPreferences.Editor editor = keyData.edit();
        editor.putBoolean(CommonConfig.KEY_IS_LOGIN, false);
        editor.commit();

        // 1.新創帳戶不用檢查.
        // 2.密碼檢查優先度高於手機檢查.
        // 3.有勾選不要提示訊息時，不檢查.
        if (!(customerInfo != null && !customerInfo.getIsInfoComplete()) && isShow) {
            MyAsyncTask<SecurityInfoFinishInfo> task = new MyAsyncTask<SecurityInfoFinishInfo>(HomeActivity.this) {
                @Override
                public SecurityInfoFinishInfo callService() throws IOException, JsonParseException, BizException, ServiceException {
                    return new AccountService().securityInfoFinish();
                }

                @Override
                public void onLoaded(SecurityInfoFinishInfo data) throws Exception {
                    if (HomeActivity.this == null || HomeActivity.this.isFinishing()) {
                        return;
                    }

                    if (!mIsError) {
                        final String phoneNumber = data.getPhoneNumber();
                        if (TextUtils.isEmpty(phoneNumber)) {
                            final Dialog confidentialityDialog = new Dialog(HomeActivity.this);
                            confidentialityDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            confidentialityDialog.setCancelable(false);
                            confidentialityDialog.setContentView(R.layout.confidentiality_dialog);
                            LinearLayout cancel = (LinearLayout) confidentialityDialog.findViewById(R.id.cancel);
                            LinearLayout determine = (LinearLayout) confidentialityDialog.findViewById(R.id.determine);
                            final CheckBox checkBox = (CheckBox) confidentialityDialog.findViewById(R.id.rmb);
                            determine.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    SharedPreferences.Editor editor = info.edit();
                                    editor.putBoolean(CommonConfig.KEY_CONFIDENTIALITY_DIALOG_CHECKBOX, checkBox.isChecked());
                                    editor.commit();
                                    IntentUtil.redirectToNextActivity(HomeActivity.this, ConfidentialityActivity.class);
                                    confidentialityDialog.dismiss();
                                }
                            });
                            cancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    SharedPreferences.Editor editor = info.edit();
                                    editor.putBoolean(CommonConfig.KEY_CONFIDENTIALITY_DIALOG_CHECKBOX, checkBox.isChecked());
                                    editor.commit();
                                    confidentialityDialog.dismiss();
                                }
                            });
                            confidentialityDialog.show();
                        }
                    }
                }
            };
            task.setOnError(new MyAsyncTask.OnError() {

                @Override
                public void handleError(Exception e) {
                    mIsError = true;
                }
            });
            task.executeTask();
        }
    }

    private void getBalance() {
        if (!mIsWait) {
            mIsWait = true;
            mIsError = false;

            MyAsyncTask<BalanceInfo> task = new MyAsyncTask<BalanceInfo>(HomeActivity.this) {
                @Override
                public BalanceInfo callService() throws IOException, JsonParseException, BizException, ServiceException {
                    return new AccountService().getBalance();
                }

                @Override
                public void onLoaded(BalanceInfo data) throws Exception {
                    mIsWait = false;
                    if (!mIsError) {
                        BaseService.BASE_BALANCE = (float) Double.parseDouble(data.getAvailableScores().replace(",", ""));
                    }
                }
            };
            task.setOnError(new MyAsyncTask.OnError() {
                @Override
                public void handleError(Exception e) {
                    mIsError = true;
                }
            });
            task.executeTask();
        }
    }
}


