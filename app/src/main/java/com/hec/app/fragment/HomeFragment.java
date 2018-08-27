package com.hec.app.fragment;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ScrollView;
import android.widget.Scroller;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.hec.app.BuildConfig;
import com.hec.app.R;
import com.hec.app.activity.AnnouncementActivity;
import com.hec.app.activity.DebugInfoPopupWindow;
import com.hec.app.activity.LotteryLeaderboardsActivity;
import com.hec.app.activity.RechargeWechatActivity;
import com.hec.app.activity.SlotActivity;
import com.hec.app.activity.base.BaseApp;
import com.hec.app.config.CommonConfig;
import com.hec.app.entity.BestPrizeInfo;
import com.hec.app.entity.BizException;
import com.hec.app.entity.BulletinInfo;
import com.hec.app.entity.CustomerInfo;
import com.hec.app.entity.GameListInfo;
import com.hec.app.entity.LotteryInfo;
import com.hec.app.entity.Result;
import com.hec.app.framework.adapter.CommonAdapter;
import com.hec.app.framework.adapter.ViewHolder;
import com.hec.app.framework.http.OkHttpClientManager;
import com.hec.app.framework.widget.BulletinView;
import com.hec.app.framework.widget.MyToast;
import com.hec.app.framework.widget.OnLotteryTypeClickedListener;
import com.hec.app.lottery.LotteryConfig;
import com.hec.app.util.BitmapUtil;
import com.hec.app.util.CustomerAccountManager;
import com.hec.app.util.DialogUtil;
import com.hec.app.util.DisplayUtil;
import com.hec.app.util.IntentUtil;
import com.hec.app.util.LotteryUtil;
import com.hec.app.util.MyAsyncTask;
import com.hec.app.util.SlotUtl;
import com.hec.app.util.TestUtil;
import com.hec.app.webservice.AccountService;
import com.hec.app.webservice.BaseService;
import com.hec.app.webservice.HomeService;
import com.hec.app.webservice.LotteryService;
import com.hec.app.webservice.ServiceException;
import com.unity3d.player.UnityPlayer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.sharesdk.onekeyshare.OnekeyShare;


public class HomeFragment extends Fragment {
    private OnLotteryTypeClickedListener listener;
    private boolean mIsError;
    ListAdapter adapter;
    private GridView myFavorite;
    private List<LotteryInfo> lotteryInfos;
    private BulletinView noticeView;
    private LinearLayout mAnnoucement_totle;
    private List<BulletinInfo> bulletinInfos;
    private TextView annoucement;
    private TextView todayBestUser;
    private TextView todayBestAmount;
    private TextView todayBestType;
    private TextView userBest;
    private TextView userBestAmount;
    private TextView userBestType;
    private ImageView homeimg;
    private BestPrizeInfo info;
    private SharedPreferences userbestwin;
    private ProgressDialog mLoadingDialog;
    private DebugInfoPopupWindow popupWindow;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    HomeService homeService;
    private LinearLayout lotteryLeaderboardsLl;
    private ViewPager bannerViewPager;
    private BannerPagerAdapter bannerPagerAdapter;
    private int currentPage = 1;
    private List<View> pageList;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            currentPage++;
            loopBanner();
        }
    };
    private Bitmap bitmap;
    private ScrollView scroll_view;
    private boolean is3DBacarratOnline = false;
    private boolean isFishing = false;
    private ProgressDialog mProgressDialog;

    public HomeFragment() {

    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        homeService = new HomeService();
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        TestUtil.print("HomeFragment");
        userbestwin = getActivity().getSharedPreferences("userbestwin", Context.MODE_PRIVATE);
        todayBestUser = (TextView) view.findViewById(R.id.today_best_prize_user);
        todayBestAmount = (TextView) view.findViewById(R.id.today_best_prize_amount);
        todayBestType = (TextView) view.findViewById(R.id.today_best_type);
        userBestAmount = (TextView) view.findViewById(R.id.user_best_amount);
        userBestType = (TextView) view.findViewById(R.id.user_best_type);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_for_best);
        userBestType.setText(userbestwin.getString("lotterytype", ""));
        userBestAmount.setText(convertString(userbestwin.getString("amount", "")));
        initViewPager(view);
        try {
            homeimg = (ImageView) view.findViewById(R.id.home_img);
            Bitmap b = BitmapUtil.readBitMap(getContext(), R.mipmap.banner, Bitmap.Config.ALPHA_8);
            homeimg.setImageBitmap(b);
        } catch (OutOfMemoryError oom) {
            MyToast.show(getContext(), "请您清理内存");
        }
        if (BuildConfig.DEBUG || BuildConfig.SIT || BuildConfig.UAT) {
            homeimg.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    popupWindow = new DebugInfoPopupWindow(getContext());
                    popupWindow.showAtLocation(scroll_view, Gravity.CENTER, 0, 0);
                    return true;
                }
            });
        }
        myFavorite = (GridView) view.findViewById(R.id.gridviewFavorite);
        myFavorite.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LotteryInfo l = lotteryInfos.get(position);
                if (listener != null) {
                    listener.onClicked(l.getLotteryID(), l.getTypeUrl());
                }
            }
        });
        noticeView = (BulletinView) view.findViewById(R.id.noticeView);
        mAnnoucement_totle = (LinearLayout) view.findViewById(R.id.announcement_totle);
        mAnnoucement_totle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setClass(getActivity(), AnnouncementActivity.class);
                startActivity(intent);
            }
        });

        scroll_view = (ScrollView) view.findViewById(R.id.scroll_view);
        scroll_view.smoothScrollTo(0, 0);

        lotteryLeaderboardsLl = (LinearLayout) view.findViewById(R.id.lottery_leaderboards_ll);
        lotteryLeaderboardsLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), LotteryLeaderboardsActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    private void initViewPager(View view) {
        String[] urls = {};
        HashMap<Integer, String> urlMap = new HashMap<>();
        int key = 0;

        //Here we may use an empty AppBean,so should check if its attributes are set correctly
        if (BaseApp.getAppBean() != null && BaseApp.getAppBean().getHomeBannersUrl() != null) {
            urls = BaseApp.getAppBean().getHomeBannersUrl().split(";");
        }
        bannerViewPager = (ViewPager) view.findViewById(R.id.home_banner);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        if (urls == null || urls.length == 0) {
            return;
        }
        pageList = new ArrayList<>();
        View pagedefault;
        pagedefault = inflater.inflate(R.layout.binner1, null);
        urlMap.put(++key, urls[0]);
        try {
            ImageView imageViewdefault = (ImageView) pagedefault.findViewById(R.id.banner_image);
            if (urls.length > 0) {
                getURLImage(urls[0], imageViewdefault);
            } else {
                bitmap = createBitmapBySuitableSize(getResources(), R.mipmap.banner, bannerViewPager);
                imageViewdefault.setImageBitmap(bitmap);
            }
        } catch (OutOfMemoryError oom) {
            MyToast.show(getContext(), "请您清理内存!");
        }

        pageList.add(pagedefault);
        for (int i = 1; i < urls.length; ++i) {
            View page;
            page = inflater.inflate(R.layout.binner1, null);
            ImageView imageView = (ImageView) page.findViewById(R.id.banner_image);
            getURLImage(urls[i], imageView);
            pageList.add(page);
            urlMap.put(++key, urls[i]);
        }
        /**
         * add last picture to the head of the image list
         */
        View pagefirst = inflater.inflate(R.layout.binner1, null);
        ImageView imageViewfirst = (ImageView) pagefirst.findViewById(R.id.banner_image);
        if (urls.length > 0) {
            getURLImage(urls[urls.length - 1], imageViewfirst);
        }
        pageList.add(0, pagefirst);
        /**
         * add first picture to the tail of the image list
         */
        View pagelast = inflater.inflate(R.layout.binner1, null);
        ImageView imagelast = (ImageView) pagelast.findViewById(R.id.banner_image);
        if (urls.length > 0) {
            getURLImage(urls[0], imagelast);
        } else {
            imagelast.setImageBitmap(bitmap);
        }
        pageList.add(pagelast);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        bannerPagerAdapter = new BannerPagerAdapter(pageList, urlMap);
        bannerViewPager.setAdapter(bannerPagerAdapter);
        bannerViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    if (bannerViewPager.getCurrentItem() == 0) {
                        bannerViewPager.setCurrentItem(pageList.size() - 2, false);
                    }
                    if (bannerViewPager.getCurrentItem() == pageList.size() - 1) {
                        bannerViewPager.setCurrentItem(1, false);
                    }
                    currentPage = bannerViewPager.getCurrentItem();
                    handler.removeMessages(0);
                    handler.sendEmptyMessageDelayed(0, 3000);
                }
                if (state == ViewPager.SCROLL_STATE_SETTLING) {
                    handler.removeMessages(0);
                }
                if (state == ViewPager.SCROLL_STATE_DRAGGING) {
                    handler.removeMessages(0);
                }
            }
        });

        try {
            Field mScroller;
            mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            FixedSpeedScroller scroller = new FixedSpeedScroller(bannerViewPager.getContext());
            mScroller.set(bannerViewPager, scroller);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        loopBanner();
    }

    private void loopBanner() {
        bannerViewPager.setCurrentItem(currentPage % pageList.size());
        handler.sendEmptyMessageDelayed(0, 3000);
    }

    class BannerPagerAdapter extends PagerAdapter {

        private List<View> viewList;
        private HashMap<Integer, String> urlMap;

        public BannerPagerAdapter(List<View> viewList, HashMap<Integer, String> urlMap) {
            this.viewList = viewList;
            this.urlMap = urlMap;
        }

        @Override
        public int getCount() {
            return viewList != null ? viewList.size() : 0;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(viewList.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            container.addView(viewList.get(position));
            viewList.get(position).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String url = urlMap.get(position);
                    analyzeUrl(url);
                }
            });
            return viewList.get(position);
        }
    }

    private void analyzeUrl(String urlStr) {

        final String BACARRAT_CODE = "code=1";
        final String FISHING_CODE = "code=2";
        final String WECHAT_CODE = "code=3";
        final String DEFAULT_CODE4 = "code=4";
        Log.i("wxj", "banner " + urlStr);

        if ("1".equals(urlStr)) {
            if (BuildConfig.DEBUG || BuildConfig.SIT || BuildConfig.UAT) {
                popupWindow = new DebugInfoPopupWindow(getContext());
                popupWindow.showAtLocation(scroll_view, Gravity.CENTER, 0, 0);
            }
            return;
        }
        URL url;
        try {
            url = new URL(urlStr);
            if (null == url.getQuery())
                return;
            String actionCodes[] = url.getQuery().split("&");
            String actionCode = actionCodes[actionCodes.length - 1];
            Log.i("wxj", "banner " + actionCode);
            if (null != actionCode) {
                if (actionCode.startsWith("AdUrl")) {
                    String adUrls[] = actionCode.split("=");
                    if (adUrls.length == 2) {
                        int adIndex = Integer.parseInt(adUrls[1]);
                        jumpToAdWebSite(adIndex);
                    }
                } else {
                    switch (actionCode) {
                        case BACARRAT_CODE:
                            jumpToSpecificGame(CommonConfig.THREE_D_BACARRAT);
                            break;
                        case FISHING_CODE:
                            jumpToSpecificGame(CommonConfig.FISHING);
                            break;
                        case WECHAT_CODE:
                            jumpToWechatRecharge();
                            break;
                        case DEFAULT_CODE4:
                            //TODO do other things;
                            break;
                    }
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private void jumpToWechatRecharge() {
        Intent intent = new Intent();
        intent.setClass(getContext(), RechargeWechatActivity.class);

        CustomerInfo.BankShow bankShow;
        CustomerInfo customer = CustomerAccountManager.getInstance().getCustomer();
        if (customer != null) {
            bankShow = null;//customer.getBankShow();
            if (bankShow == null) {
                getBankShow(intent);
            } else {
                boolean[] wechat = bankShow.getWechat();
                if (wechat != null && wechat.length == 2 && (wechat[0] || wechat[1])) {
                    intent.putExtra("itemShowArray", bankShow.getWechat());
                    startActivity(intent);
                }
            }
        }
    }

    private void getBankShow(final Intent intent) {
        MyAsyncTask<Result<String>> task = new MyAsyncTask<Result<String>>(getContext()) {
            @Override
            public Result<String> callService() throws IOException, JsonParseException, BizException, ServiceException {
                return new AccountService().getBankShow();
            }

            @Override
            public void onLoaded(Result<String> paramT) throws Exception {
                if (paramT.isSuccess()) {
                    String jsonString = paramT.getData();
                    if (jsonString != null) {
                        CustomerInfo.BankShow bankShow = new Gson().fromJson(jsonString, CustomerInfo.BankShow.class);
                        boolean[] wechat = bankShow.getWechat();
                        if (wechat != null && wechat.length == 2 && (wechat[0] || wechat[1])) {
                            intent.putExtra("itemShowArray", bankShow.getWechat());
                            startActivity(intent);
                        }
                    }
                }
            }
        };
        task.setOnError(new MyAsyncTask.OnError() {
            @Override
            public void handleError(Exception paramException) {
            }
        });
        task.executeTask();

    }

    private void jumpToAdWebSite(final int index) {
        if (BaseApp.getAppBean() != null && BaseApp.getAppBean().getADUrl() != null) {
            String[] adUrls = BaseApp.getAppBean().getADUrl().split(";");
            if (index >= 0 && index < adUrls.length) {
                String url = adUrls[index];
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        }

    }

    private void jumpToSpecificGame(final String theme) {
        mIsError = false;
        showProgressDialog();
        MyAsyncTask<List<GameListInfo>> task = new MyAsyncTask<List<GameListInfo>>(getActivity()) {
            @Override
            public List<GameListInfo> callService() throws IOException, JsonParseException, BizException, ServiceException {
                return new HomeService().getGameList();
            }

            @Override
            public void onLoaded(List<GameListInfo> paramT) throws Exception {
                closeProgressDialog();
                if (!mIsError) {
                    for (GameListInfo gameListInfo : paramT) {
                        if (CommonConfig.THREE_D_BACARRAT.equals(theme)) {
                            is3DBacarratOnline = true;
                        } else if (CommonConfig.FISHING.equals(theme)) {
                            isFishing = true;
                        }
                        goTiger(theme);
                        break;
                    }
                    if (!is3DBacarratOnline) {
                        MyToast.show(getActivity(), "多人3D百家乐全新上线！");
                    }
                }
            }
        };
        task.setOnError(new MyAsyncTask.OnError() {
            @Override
            public void handleError(Exception paramException) {
                mIsError = true;
            }
        });
        task.executeTask();
    }

    private void goTiger(final String theme) {
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
        if (CommonConfig.THREE_D_BACARRAT.equals(theme)) {
            bundle.putString(CommonConfig.BUNDLE_GOTIGER_SCENE, CommonConfig.THREE_D_BACARRAT);
        } else if (CommonConfig.FISHING.equals(theme)) {
            bundle.putString(CommonConfig.BUNDLE_GOTIGER_SCENE, CommonConfig.FISHING);
        }
        IntentUtil.redirectToNextActivity(getActivity(), SlotActivity.class, bundle);
        String slotData = SlotUtl.buildDataAccordingToScene(
                theme,
                OkHttpClientManager.getInstance().getUserName(),
                AASlotUrl,
                BaseService.BASE_BALANCE
        );
        UnityPlayer.UnitySendMessage("Preload"
                , "getIntentData"
                , slotData);
        getActivity().finish();

    }

    public class FixedSpeedScroller extends Scroller {

        private int mDuration = 1000;

        public FixedSpeedScroller(Context context) {
            super(context);
        }

        public FixedSpeedScroller(Context context, Interpolator interpolator) {
            super(context, interpolator);
        }

        public FixedSpeedScroller(Context context, Interpolator interpolator, boolean flywheel) {
            super(context, interpolator, flywheel);
        }


        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            // Ignore received duration, use fixed one instead
            super.startScroll(startX, startY, dx, dy, mDuration);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy) {
            // Ignore received duration, use fixed one instead
            super.startScroll(startX, startY, dx, dy, mDuration);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof OnLotteryTypeClickedListener) {
            listener = (OnLotteryTypeClickedListener) activity;
        } else {
            throw new IllegalArgumentException("activity must implements FragmentInteraction");
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLatestNews();
        getBestPrizes();
    }

    @Override
    public void onResume() {
        getOftenPlayLotteryList();
        super.onResume();
    }

    private void getLatestNews() {
        mIsError = false;
        MyAsyncTask<List<BulletinInfo>> task = new MyAsyncTask<List<BulletinInfo>>(getActivity()) {

            @Override
            public List<BulletinInfo> callService() throws IOException,
                    JsonParseException, BizException, ServiceException {
                return new HomeService().getBulletins(0, 10);
            }

            @Override
            public void onLoaded(List<BulletinInfo> result) throws Exception {
                if (getActivity() == null || getActivity().isFinishing())
                    return;
                if (!mIsError) {
                    noticeView.setPublicNotices(result);
                } else {
                    BaseApp.changeUrl(getActivity(), new BaseApp.OnChangeUrlListener() {
                        @Override
                        public void changeSuccess() {
                            getLatestNews();
                        }

                        @Override
                        public void changeFail() {
                        }
                    });
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


    private void getOftenPlayLotteryList() {
        TestUtil.print("getOftenPlayLotteryList");
        LotteryService lotteryService = new LotteryService();

        try {
            lotteryInfos = new LotteryService().getOftenPlayLotteryInfo(LotteryConfig.PLAY_MODE.CLASSIC);
        } catch (Exception e) {
            lotteryInfos = null;
        }

        if (lotteryInfos == null) {
            lotteryInfos = new ArrayList<>();
        }

        try {
            List<LotteryInfo> list = lotteryService.getLotteryInfo(LotteryConfig.PLAY_MODE.CLASSIC);
            if (list != null) {
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences(CommonConfig.KEY_CATCH_SLOT_BJL, getActivity().MODE_PRIVATE);

                if (lotteryInfos.size() <= 0) {
                    for (LotteryInfo info : list) {
                        if (info.getLotteryID() == LotteryConfig.LOTTERY_ID.CHONGQING_REALTIME ||
                                info.getLotteryID() == LotteryConfig.LOTTERY_ID.GERMANY_PK10 ||
                                info.getLotteryID() == LotteryConfig.LOTTERY_ID.ITALY_REAMTIME ||
                                info.getLotteryID() == LotteryConfig.LOTTERY_ID.ITALY_PK10) {
                            lotteryInfos.add(info);
                        }
                    }

                    lotteryInfos.add(0, new LotteryInfo(LotteryConfig.LOTTERY_ID.BAIJIALE, getString(R.string.lottery_name_bjl), ""));
                    lotteryInfos.add(0, new LotteryInfo(LotteryConfig.LOTTERY_ID.SLOT, getString(R.string.lottery_name_slot), ""));

                    lotteryService.setOftenPlayLotteryInfo(lotteryInfos);

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean(CommonConfig.KEY_IS_CATCH_ADD_SLOT_BJL, true);
                } else {
                    boolean isCatchAddSlotBjl = sharedPreferences.getBoolean(CommonConfig.KEY_IS_CATCH_ADD_SLOT_BJL, false);

                    if (!isCatchAddSlotBjl) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean(CommonConfig.KEY_IS_CATCH_ADD_SLOT_BJL, true);
                        editor.commit();

                        boolean isAddSlot = true;
                        boolean isAddBjl = true;
                        for (LotteryInfo info : lotteryInfos) {
                            if (info.getLotteryID() == LotteryConfig.LOTTERY_ID.BAIJIALE) {
                                isAddBjl = false;
                            }
                            if (info.getLotteryID() == LotteryConfig.LOTTERY_ID.SLOT) {
                                isAddSlot = false;
                            }
                        }

                        if (isAddBjl) {
                            lotteryInfos.add(0, new LotteryInfo(LotteryConfig.LOTTERY_ID.BAIJIALE, getString(R.string.lottery_name_bjl), ""));
                        }
                        if (isAddSlot) {
                            lotteryInfos.add(0, new LotteryInfo(LotteryConfig.LOTTERY_ID.SLOT, getString(R.string.lottery_name_slot), ""));
                        }

                        lotteryService.setOftenPlayLotteryInfo(lotteryInfos);
                    }
                }
            }
        } catch (Exception e) {
        }

        lotteryInfos.add(new LotteryInfo(0, "Add", ""));
        lotteryInfos.add(0, new LotteryInfo(-1, "Brand", ""));

        List<LotteryInfo> tmp = new ArrayList<>();
        for (LotteryInfo l : lotteryInfos) {
            tmp.add(l);
        }
        lotteryInfos = new ArrayList<>(tmp);
        adapter = new CommonAdapter<LotteryInfo>(getActivity(), lotteryInfos, R.layout.gridview_item_ofen_play_lottery) {

            @Override
            public void convert(ViewHolder helper, LotteryInfo item, int position) {

                ImageView imgView = helper.getView(R.id.imgLotteryInfo);
                imgView.setImageResource(LotteryUtil.getLotteryIcon(item.getLotteryType()));

            }
        };
        myFavorite.setAdapter(adapter);
    }

    private void getBestPrizes() {
        mIsError = false;
        MyAsyncTask<BestPrizeInfo> task = new MyAsyncTask<BestPrizeInfo>(getContext()) {

            @Override
            public BestPrizeInfo callService() throws IOException,
                    JsonParseException, BizException, ServiceException {
                return homeService.getTodayBestPrize();
            }

            @Override
            public void onLoaded(BestPrizeInfo result) throws Exception {
                if (getActivity() == null || getActivity().isFinishing())
                    return;
                if (!mIsError) {
                    Bundle b = new Bundle();
                    b.putSerializable("data", result.toHashMap());
                    Message msg = Message.obtain();
                    msg.setData(b);
                    msg.what = 0;
                    mHandler.sendMessage(msg);
                } else {
                    BaseApp.changeUrl(getActivity(), new BaseApp.OnChangeUrlListener() {
                        @Override
                        public void changeSuccess() {
                            getBestPrizes();
                        }

                        @Override
                        public void changeFail() {
                        }
                    });
                }
            }
        };
        task.setOnError(new MyAsyncTask.OnError() {
            @Override
            public void handleError(Exception paramException) {
                mIsError = true;
            }
        });
        task.executeTask();
    }

    public void getUserBestPrice() {
        MyAsyncTask<BestPrizeInfo> task2 = new MyAsyncTask<BestPrizeInfo>(getContext()) {

            @Override
            public BestPrizeInfo callService() throws IOException,
                    JsonParseException, BizException, ServiceException {
                return homeService.getUserBestPrize();
            }

            @Override
            public void onLoaded(BestPrizeInfo result) throws Exception {
                if (getActivity() == null || getActivity().isFinishing())
                    return;
                Bundle b = new Bundle();
                b.putSerializable("data", result.toHashMap());
                Message msg = Message.obtain();
                msg.setData(b);
                msg.what = 1;
                mHandler.sendMessage(msg);
            }
        };
        task2.executeTask();
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            HashMap<String, String> data = (HashMap<String, String>) msg.getData().getSerializable("data");
            String winMoney = "0.00";
            if (data.get("WinMoney") != null) {
                winMoney = String.format("%.2f", Float.parseFloat(data.get("WinMoney")));
            }
            //Update UI
            if (msg.what == 0) {
                String username = data.get("UserName");
                int length = username.length();
                TestUtil.print("username " + username + " len " + length);
                if (length > 3)
                    username = username.substring(0, length - 3) + "***";
                todayBestUser.setText(username);
                todayBestType.setText(data.get("LotteryType").replace("和盛", "聚星"));
                todayBestAmount.setText(convertString(winMoney));
            } else if (msg.what == 1) {
                userBestType.setText(data.get("LotteryType").replace("和盛", "聚星"));
                userBestAmount.setText(convertString(winMoney));
            }
        }
    };

    private String convertString(String moneyString) {
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(Double.valueOf(moneyString));
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!BuildConfig.DEBUG || BuildConfig.SIT || BuildConfig.UAT) {
            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    getBestPrizes();
                    getUserBestPrice();
                    getLatestNews();
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            });
        }
    }

    private Bitmap b1;
    private Bitmap b2;

    private void buildImage() {
        b1 = wordToImage();
        Log.i("wxj", "wx 000");
        MyAsyncTask<Bitmap> task = new MyAsyncTask<Bitmap>(getActivity()) {
            @Override
            public Bitmap callService() throws IOException, JsonParseException, BizException, ServiceException {
                return new HomeService().getBannerImage("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1491463876&di=b51f5eeebe04ebd6d4b4a084d179c801&imgtype=jpg&er=1&src=http%3A%2F%2Fneuralnetworksanddeeplearning.com%2Fimages%2Ftikz40.png");
            }

            @Override
            public void onLoaded(Bitmap paramT) throws Exception {
                b2 = paramT;
                Bitmap b3 = newBitmap(b1, b2);
                OnekeyShare oks = new OnekeyShare();
                oks.setText("http://home.bigbrothers.info:19088/article/details/1");
                saveBitmap("awx", b3);
                oks.setImagePath(Environment.getExternalStorageDirectory().getAbsolutePath() + "/awx.png");
                oks.show(getActivity());
            }
        };
        task.executeTask();
    }

    private File saveBitmap(String filename, Bitmap bitmap) {
        String extStorageDirectory = Environment.getExternalStorageDirectory().getAbsolutePath();
        OutputStream outStream = null;
        Log.i("wxj", "wx haha " + extStorageDirectory);
        File file = new File(extStorageDirectory, filename + ".png");
        Log.i("wxj", "wx hehe" + file + ",Bitmap= " + filename);
        try {
            outStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.flush();
            outStream.close();
            Log.i("wxj", "wx heihei");
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e("file", "" + file);
        return file;
    }

    private Bitmap newBitmap(Bitmap bit1, Bitmap bit2) {
        int width = bit1.getWidth();
        int height = bit1.getHeight() + bit2.getHeight();
        //创建一个空的Bitmap(内存区域),宽度等于第一张图片的宽度，高度等于两张图片高度总和
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        //将bitmap放置到绘制区域,并将要拼接的图片绘制到指定内存区域
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(bit1, 0, 0, null);
        canvas.drawBitmap(bit2, 0, bit1.getHeight(), null);
        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();
        return bitmap;
    }

    private Bitmap wordToImage() {
        int x = 20, y = 40;
        Bitmap bitmap;
        try {
            TextProperty tp = new TextProperty("之前有玩家说找不到一个好的地方可以交流的，我们就想能不能给大家提供一个交流的平台。所以购彩交流这个板块就上线了。让我们没有想到的是，很多会员朋友纷纷发文，讲述自己的心得与故事。我们JX风风雨雨这么多年原来有这么多的朋友在支持我们。非常非常感谢这些朋友对JX的支持，你们的支持给了我们莫大的鼓励。也感谢你们对我们做得不好地方的宽容与大度。回顾这几年，JX在产品质量上面并不是最好，但是我们始终在信誉上坚持一流，致力于做一个公平、安全的购彩平台。这里不得不提起一件让我们大家都难以忘记的事情，在平台开业初期的时候，我们出款系统有疏漏，给一个玩家的一个提款请求，连续处理了三次，我们当时并没有发现，反而是玩家找到客服告诉我们他只提了一笔款，却收到了三笔，不但退还了多出的钱，还帮助我们查找问题。这件事情对这个玩家可能是个小事，但是对我们却有莫大的影响，让我们更加感受到玩家就是和我们身边的朋友一样。让我们形成了对待玩家就像对待我们自己朋友的理念。\n" +
                    "另外就近日玩家朋友们最关心的一些问题我们进行回复：\n" +
                    "    第一，韩国1.5分彩。由于市面上流行的韩国1.5分彩开奖数据混乱，甚至大部分平台都不是出自于官方数据，所以我们暂时没有增加这个彩种。我们会在能稳定得到官方数据的时候，开放给大家。与此同时，我们近期会增加台湾的时时彩。因为台湾时时彩官方数据稳定，并且玩家朋友也可以直接打电话去台湾核实也没有语言障碍，所以真实性是比较有保障的。其他的高频彩我们也会逐步添加，并且会附上官方的开奖网址。\n" +
                    "    第二，很多玩家朋友提出希望增加更多的玩法。我们首先要为没有及时增加更多的玩法为大家道歉，我们会尽快增加更加丰富的玩法。\n" +
                    "    今后我们会增加更多贴近玩家的功能，让大家在JX有更多的选择和更多的乐趣。\n" +
                    "    最后，我们也想提醒大家，彩票只是为生活增加少许乐趣的项目，切勿沉迷其中，也不建议作为理财目的。投注金额请依据自身情况量力而行，切勿影响工作与家庭。");
            //bitmap = Bitmap.createBitmap(20*tp.getWidth(), 20*tp.getHeight(), Bitmap.Config.ARGB_8888);
            bitmap = Bitmap.createBitmap(500, 25 * tp.getHeight(), Bitmap.Config.ARGB_8888);
            Log.i("wxj", "wxxxx " + tp.getWidth() + " " + tp.getHeight());
            Canvas canvas = new Canvas(bitmap);
            canvas.drawColor(getResources().getColor(R.color.white));
            Paint paint = new Paint();
            paint.setColor(getResources().getColor(R.color.article_green));
            paint.setTextSize(18);
            paint.setAntiAlias(true);
            paint.setSubpixelText(true);
            paint.setFlags(Paint.ANTI_ALIAS_FLAG);
            paint.setFlags(Paint.DITHER_FLAG);
            paint.setHinting(Paint.HINTING_ON);
            String[] ss = tp.getContext();
            int k = 0;
            for (int i = 1; i < tp.getHeight(); i++) {
                if (k < tp.getString().length()) {
                    canvas.drawText(tp.getString().substring(k, k + tp.getWidth()), x, y, paint);
                } else {
                    canvas.drawText(tp.getString().substring(k, tp.getString().length()), x, y, paint);
                }
                k = k + tp.getWidth();
                y = y + 20;
            }
            canvas.save(Canvas.ALL_SAVE_FLAG);
            canvas.restore();
            return bitmap;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public void getURLImage(final String url, final ImageView imageView) {
        /**
         * Use Glide.
         */
        Glide.with(getActivity())
                .load(url)
                .placeholder(R.mipmap.placeholder_pic)
                .error(R.mipmap.error_pic)
                .into(imageView);
    }

    public class TextProperty {
        private int height;      //读入文本的行数
        private int width = 25;      //一行文字数
        private String[] context = new String[2048];
        private String s = "";//存储读入的文本

        /*
         *@parameter wordNum  设置每行显示的字数
         * 构造函数将文本读入，将每行字符串切割成小于等于35个字符的字符串  存入字符数组
         *
         */
        public TextProperty(String s) {
            this.s = s;
            for (int i = 0; i < s.length(); i++) {
                context[i] = s.substring(i, i + 1);
            }
            height = (s.length() / width) + 1;
        }


        public int getHeight() {
            return height;
        }

        public int getWidth() {
            return width;
        }

        public String[] getContext() {
            return context;
        }

        public String getString() {
            return s;
        }
    }


    private Bitmap createBitmapBySuitableSize(Resources res, int id, final ViewPager viewPager) {
        int vpHeight, vpWidth;
        vpWidth = getActivity().getWindowManager().getDefaultDisplay().getWidth();
        vpHeight = DisplayUtil.getPxByDp(getActivity(), 150);
        return BitmapUtil.decodeSampledBitmapFromResource(res, id, vpWidth, vpHeight);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
            bitmap = null;
        }
    }

    private void showProgressDialog() {
        try {
            mProgressDialog = DialogUtil.getProgressDialog(getActivity(), getResources().getString(R.string.loading_list));
            mProgressDialog.show();
        } catch (Exception e) {

        }
    }

    private void closeProgressDialog() {
        try {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        } catch (Exception e) {

        }
    }
}
