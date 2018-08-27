package com.hec.app.activity.base;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.TextPaint;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.JsonParseException;
import com.hec.app.R;
import com.hec.app.activity.ArticalActivity;
import com.hec.app.activity.LotteryActivity;
import com.hec.app.activity.MoneyActivity;
import com.hec.app.activity.ProxyListActivity;
import com.hec.app.activity.RechargeMainActivity;
import com.hec.app.activity.RecordListActivity;
import com.hec.app.activity.SettingActivity;
import com.hec.app.activity.TransferActivity;
import com.hec.app.activity.WebchatActivity;
import com.hec.app.activity.WithdrawActivity;
import com.hec.app.config.CommonConfig;
import com.hec.app.customer_service.CustomerServiceActivity;
import com.hec.app.dialog.CustomerServiceAlertDialog;
import com.hec.app.dialog.WebchatDialog;
import com.hec.app.entity.ArticleCountInfo;
import com.hec.app.entity.BizException;
import com.hec.app.entity.CustomerInfo;
import com.hec.app.entity.EggInfo;
import com.hec.app.entity.HomeBalanceInfo;
import com.hec.app.entity.ServiceRequestResult;
import com.hec.app.fragment.ArticalFragment;
import com.hec.app.fragment.RecordListFragment;
import com.hec.app.framework.widget.MyToast;
import com.hec.app.framework.widget.ResideMenu;
import com.hec.app.util.CustomerAccountManager;
import com.hec.app.util.DialogUtil;
import com.hec.app.util.IntentUtil;
import com.hec.app.util.MyAsyncTask;
import com.hec.app.util.StringUtil;
import com.hec.app.util.SystemBarTintManager;
import com.hec.app.webservice.AccountService;
import com.hec.app.webservice.ArticleService;
import com.hec.app.webservice.HomeService;
import com.hec.app.webservice.ServiceException;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by asianark on 26/2/16.
 */
public class BaseActivityWithMenu extends BaseActivity implements View.OnClickListener {

    private LinearLayout afterHistoryLayout;
    private LinearLayout lotteryHistoryLayout;
    private LinearLayout SettingLayout;
    private LinearLayout moneyDetailLayout;
    private LinearLayout agentCenterLayout;
    private LinearLayout articalLayout;
    private LinearLayout easterEggLayout;
    private RelativeLayout transferLayout;
    private RelativeLayout rechargeLayout;
    private RelativeLayout withdrawLayout;
    private TextView connect_service;
    private ImageView menu_refresh;
    private boolean holdon = false;
    private Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            holdon = false;
        }
    };

    private ResideMenu resideMenu;
    private TextView mAvailableScores, mFreezeScores, mAllGain, mUserName;
    private boolean mIsError;
    private boolean firstin = true;
    private TextView count;

    private LinearLayout backToLottery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.colorPrimary);//通知栏所需颜色
        }
//        checkLogin(this, null);
        setupResideMenu();

    }


    private void getArticleCount() {
        mIsError = false;
        MyAsyncTask<ArticleCountInfo> task = new MyAsyncTask<ArticleCountInfo>(this) {
            @Override
            public ArticleCountInfo callService() throws IOException, JsonParseException, BizException, ServiceException {
                return new ArticleService().getArticleCount();
            }

            @Override
            public void onLoaded(ArticleCountInfo paramT) throws Exception {
                if (!mIsError) {
                    count.setVisibility(View.VISIBLE);
                    Log.i("wxj", "article result" + paramT.getTodayCount());
                    if (paramT.getTodayCount() < 10) {
                        TextPaint tp = count.getPaint();
                        tp.setFakeBoldText(true);
                    }
                    count.setText(String.valueOf(paramT.getTodayCount()));
                    Log.i("wxj", "article " + paramT.getTodayCount());
                } else {
                    MyToast.show(BaseActivityWithMenu.this, getErrorMessage());
                }
            }
        };
        task.executeTask();
        task.setOnError(new MyAsyncTask.OnError() {
            @Override
            public void handleError(Exception paramException) {
                mIsError = true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
//        if(firstin){
//            firstin = false;
//            getHomeBalanceInfo();
//        }

        // 自定义摇一摇的灵敏度，默认为950，数值越小灵敏度越高。
        //    PgyFeedbackShakeManager.setShakingThreshold(1000);

        // 以对话框的形式弹出
        //    PgyFeedbackShakeManager.register(BaseActivityWithMenu.this);

        // 以Activity的形式打开，这种情况下必须在AndroidManifest.xml配置FeedbackActivity
        // 打开沉浸式,默认为false
        // FeedbackActivity.setBarImmersive(true);
        //    PgyFeedbackShakeManager.register(BaseActivityWithMenu.this, false);

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


    private void setupResideMenu() {

        resideMenu = new ResideMenu(this, -1, R.layout.sliding_menu_new);
        resideMenu.setBackground(R.mipmap.slidemenu_bg);
        resideMenu.attachToActivity(this);
        resideMenu.setMenuListener(menuListener);

        //valid scale factor is between 0.0f and 1.0f. leftmenu'width is 150dip.
        resideMenu.setScaleValue(0.75f);
        resideMenu.setShadowVisible(true);

        resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_LEFT);
        afterHistoryLayout = (LinearLayout) resideMenu.getRightMenuView().findViewById(R.id.sliding_menu_after_layout);
        lotteryHistoryLayout = (LinearLayout) resideMenu.getRightMenuView().findViewById(R.id.sliding_menu_lottery_layout);
        SettingLayout = (LinearLayout) resideMenu.getRightMenuView().findViewById(R.id.sliding_menu_setting);
        moneyDetailLayout = (LinearLayout) resideMenu.getRightMenuView().findViewById(R.id.sliding_menu_money_layout);
        agentCenterLayout = (LinearLayout) resideMenu.getRightMenuView().findViewById(R.id.sliding_menu_agent_layout);
        articalLayout = (LinearLayout) resideMenu.getRightMenuView().findViewById(R.id.sliding_menu_artical_layout);
        rechargeLayout = (RelativeLayout) resideMenu.getRightMenuView().findViewById(R.id.sliding_menu_recharge);
        withdrawLayout = (RelativeLayout) resideMenu.getRightMenuView().findViewById(R.id.sliding_menu_withdraw);
        transferLayout = (RelativeLayout) resideMenu.getRightMenuView().findViewById(R.id.sliding_menu_transfer);
        backToLottery = (LinearLayout) resideMenu.getRightMenuView().findViewById(R.id.backToLottery);
        connect_service = (TextView) resideMenu.getRightMenuView().findViewById(R.id.connect_service);
        menu_refresh = (ImageView) resideMenu.getRightMenuView().findViewById(R.id.menu_refresh);
        menu_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!holdon) {
                    getMenuHomeBalanceInfo();
                    getEasterEgg();
                    getArticleCount();
                    holdon = true;
                    mhandler.sendEmptyMessageDelayed(0, 5000);
                }
            }
        });
        count = (TextView) resideMenu.getRightMenuView().findViewById(R.id.tv_article_count);
        lotteryHistoryLayout.setOnClickListener(this);
        afterHistoryLayout.setOnClickListener(this);
        moneyDetailLayout.setOnClickListener(this);
        SettingLayout.setOnClickListener(this);
        articalLayout.setOnClickListener(this);
        rechargeLayout.setOnClickListener(this);
        withdrawLayout.setOnClickListener(this);
        agentCenterLayout.setOnClickListener(this);
        backToLottery.setOnClickListener(this);
        transferLayout.setOnClickListener(this);
        connect_service.setOnClickListener(this);

        mAvailableScores = (TextView) resideMenu.getRightMenuView().findViewById(R.id.tv_availableScores);
        mFreezeScores = (TextView) resideMenu.getRightMenuView().findViewById(R.id.tv_freezeScores);
        mAllGain = (TextView) resideMenu.getRightMenuView().findViewById(R.id.tv_allGain);
        mUserName = (TextView) resideMenu.getRightMenuView().findViewById(R.id.tv_user_name);

        easterEggLayout = (LinearLayout) resideMenu.getRightMenuView().findViewById(R.id.easter_egg);

    }

    private ResideMenu.OnMenuListener menuListener = new ResideMenu.OnMenuListener() {
        @Override
        public void openMenu() {
            if (firstin) {
                firstin = false;
                getMenuHomeBalanceInfo();
                getEasterEgg();
                getArticleCount();
            }
        }

        @Override
        public void closeMenu() {
        }
    };


    @Override
    public void onClick(View v) {
        if (v == lotteryHistoryLayout) {
            IntentUtil.redirectToNextActivity(BaseActivityWithMenu.this, RecordListActivity.class,
                    RecordListFragment.ARGUMENT, "投注记录");
            this.finish();
        } else if (v == afterHistoryLayout) {
            IntentUtil.redirectToNextActivity(BaseActivityWithMenu.this, RecordListActivity.class,
                    RecordListFragment.ARGUMENT, "追号记录");
            this.finish();
        } else if (v == moneyDetailLayout) {
            IntentUtil.redirectToNextActivity(BaseActivityWithMenu.this, MoneyActivity.class);
            this.finish();
        } else if (v == SettingLayout) {
            IntentUtil.redirectToNextActivity(BaseActivityWithMenu.this, SettingActivity.class);
            this.finish();
        } else if (v == withdrawLayout) {
            IntentUtil.redirectToNextActivity(BaseActivityWithMenu.this, WithdrawActivity.class);
            Log.i("wxj", "withdraw");
            this.finish();
        } else if (v == rechargeLayout) {
            IntentUtil.redirectToNextActivity(BaseActivityWithMenu.this, RechargeMainActivity.class);
            this.finish();
        } else if (v == agentCenterLayout) {
            IntentUtil.redirectToNextActivity(BaseActivityWithMenu.this, ProxyListActivity.class,
                    "Param1", "Param2");
            this.finish();
        } else if (v == articalLayout) {
            IntentUtil.redirectToNextActivity(BaseActivityWithMenu.this, ArticalActivity.class,
                    ArticalFragment.ARGUMENT, "购彩交流");
            this.finish();
        } else if (v == backToLottery) {
            if (BaseApp.trace != null) {
                Intent it = new Intent();
                it.setClass(BaseActivityWithMenu.this, LotteryActivity.class);
                it.putExtra("LotteryID", BaseApp.trace.getLotteryId());
                it.putExtra("PlayTypeID", BaseApp.trace.getPlayTypeId());
                it.putExtra("PlayTypeRadioID", BaseApp.trace.getPlayTypeRadioId());
                it.putExtra("PlayMode", BaseApp.trace.getPlayMode());
                startActivity(it);
                this.finish();
            } else {
//                IntentUtil.redirectToNextActivity(BaseActivityWithMenu.this, HomeActivity.class);
                this.finish();
            }
        } else if (v == transferLayout) {
            IntentUtil.redirectToNextActivity(BaseActivityWithMenu.this, TransferActivity.class);
            this.finish();
        } else if (v == connect_service) {
            CustomerInfo customer = CustomerAccountManager.getInstance().getCustomer();
            if (customer != null && customer.isVIP()) {
                new WebchatDialog(BaseActivityWithMenu.this, new WebchatDialog.OnGoToWebChatListener() {
                    @Override
                    public void onClick(int type) {
                        Intent intent = new Intent();
                        if (type == 1) {
                            intent.setClass(BaseActivityWithMenu.this, CustomerServiceActivity.class);
                        } else {
                            intent.setClass(BaseActivityWithMenu.this, WebchatActivity.class);
                            intent.putExtra(CommonConfig.WEBCHAT_TYPE, CommonConfig.WEBCHAT_TYPE_NORMAL);
                        }
                        startActivity(intent);
                        BaseActivityWithMenu.this.finish();
                    }
                }).show();
            } else {
                DialogUtil.getCustomerServiceDialog(BaseActivityWithMenu.this, new CustomerServiceAlertDialog.CustomerServiceAlertDialogListener() {
                    @Override
                    public void onClick() {
                        Intent intent = new Intent();
                        intent.setClass(BaseActivityWithMenu.this, WebchatActivity.class);
                        intent.putExtra(CommonConfig.WEBCHAT_TYPE, CommonConfig.WEBCHAT_TYPE_NORMAL);
                        startActivity(intent);
                        BaseActivityWithMenu.this.finish();
                    }
                }).show();
            }
        }

    }

    public ResideMenu getResidingMenu() {
        return resideMenu;
    }

    public void getMenuHomeBalanceInfo() {
        mIsError = false;
        MyAsyncTask<HomeBalanceInfo> task = new MyAsyncTask<HomeBalanceInfo>(this) {

            @Override
            public HomeBalanceInfo callService() throws IOException,
                    JsonParseException, BizException, ServiceException {
                return new AccountService().getHomeBalanceInfo();
            }

            @Override
            public void onLoaded(HomeBalanceInfo result) throws Exception {
                if (BaseActivityWithMenu.this == null || BaseActivityWithMenu.this.isFinishing())
                    return;
                if (!mIsError) {
                    double d = Double.parseDouble(result.getAvailableScores().replace(",", ""));
                    DecimalFormat df = new DecimalFormat("#.##");
                    mAvailableScores.setText(df.format(d) + "");

                    d = Double.parseDouble(result.getFreezeScores().replace(",", ""));
                    mFreezeScores.setText(df.format(d) + "");

                    d = Double.parseDouble(result.getAllGain().replace(",", ""));
                    mAllGain.setText(df.format(d) + "");

                    CustomerInfo customer = CustomerAccountManager.getInstance().getCustomer();
                    if (customer != null) {
                        mUserName.setText(customer.getUserName());
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

    private void getEasterEgg() {
        mIsError = false;
        MyAsyncTask<List<EggInfo>> task = new MyAsyncTask<List<EggInfo>>(this) {

            @Override
            public List<EggInfo> callService() throws IOException,
                    JsonParseException, BizException, ServiceException {
                return new HomeService().getEasterEgg();
            }

            @Override
            public void onLoaded(List<EggInfo> result) throws Exception {
                if (BaseActivityWithMenu.this == null || BaseActivityWithMenu.this.isFinishing())
                    return;
                if (!mIsError) {
                    int[] arr = {R.mipmap.red_packet_money, R.mipmap.wage_money, R.mipmap.red_packet_money_for_games};
                    if (!result.isEmpty()) {
                        easterEggLayout.setVisibility(View.VISIBLE);
                    } else {
                        easterEggLayout.setVisibility(View.INVISIBLE);
                        return;
                    }
                    easterEggLayout.removeAllViews();
                    for (EggInfo info : result) {
                        View child = getLayoutInflater().inflate(R.layout.easter_egg_btn, null);
                        ImageView iv = (ImageView) child.findViewById(R.id.img_view);
                        iv.setImageResource(arr[info.getEggType() - 1]);
                        final EggInfo mInfo = info;
                        final View mChild = child;
                        child.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Log.i("EGG", mInfo.getEggMoney());
                                redeemEasterEgg(mInfo, mChild);
                            }
                        });
                        easterEggLayout.addView(child);
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

    private void redeemEasterEgg(final EggInfo mInfo, final View mChild) {
        mIsError = false;
        MyAsyncTask<ServiceRequestResult> task = new MyAsyncTask<ServiceRequestResult>(this) {

            @Override
            public ServiceRequestResult callService() throws IOException,
                    JsonParseException, BizException, ServiceException {
                return new HomeService().redeemEasterEgg(mInfo.getEggType());
            }

            @Override
            public void onLoaded(ServiceRequestResult result) throws Exception {
                if (BaseActivityWithMenu.this == null || BaseActivityWithMenu.this.isFinishing())
                    return;
                if (!mIsError && result.isSuccess()) {
                    mChild.setVisibility(View.GONE);
                    Dialog dialog = new Dialog(BaseActivityWithMenu.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.egg_success_dialog);
                    Log.i("hec", "egg:" + mInfo.getEggMoney());
                    TextView tv = (TextView) dialog.findViewById(R.id.egg_success_tv);
                    String str = "恭喜你! 成功领取" + StringUtil.joinHtmlColor(mInfo.getEggMoney() + "元", "#F32E42") + "礼金";
                    tv.setText(Html.fromHtml(str));
                    dialog.setCanceledOnTouchOutside(true);
                    dialog.show();
                } else if (result.getMessage() != null) {
                    MyToast.show(BaseActivityWithMenu.this, result.getMessage());
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


    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        //    PgyFeedbackShakeManager.unregister();
    }

}
