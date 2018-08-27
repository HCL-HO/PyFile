package com.hec.app.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.hec.app.R;
import com.hec.app.activity.base.BaseActivity;
import com.hec.app.activity.base.BaseActivityWithMenu;
import com.hec.app.activity.base.BaseApp;
import com.hec.app.adapter.BJLAdapter;
import com.hec.app.config.CommonConfig;
import com.hec.app.entity.BalanceInfo;
import com.hec.app.entity.BizException;
import com.hec.app.entity.GameListInfo;
import com.hec.app.entity.LotteryInfo;
import com.hec.app.framework.http.OkHttpClientManager;
import com.hec.app.framework.widget.MyToast;
import com.hec.app.lottery.LotteryConfig;
import com.hec.app.util.CustomerAccountManager;
import com.hec.app.util.DialogUtil;
import com.hec.app.util.IntentUtil;
import com.hec.app.util.MyAsyncTask;
import com.hec.app.util.SlotUtl;
import com.hec.app.util.SystemBarTintManager;
import com.hec.app.webservice.AccountService;
import com.hec.app.webservice.BaseService;
import com.hec.app.webservice.HomeService;
import com.hec.app.webservice.ServiceException;
import com.unity3d.player.UnityPlayer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.hec.app.config.CommonConfig.FISHING;
import static com.hec.app.config.CommonConfig.THREE_D_BACARRAT;

public class SlotNewActivity extends BaseActivity {

    private RecyclerView rl_bjl;
    private BJLAdapter bjlAdapter;
    private boolean mIsError;
    private boolean is3DBacarratOnline = false;
    private boolean isFishing = false;
    private ProgressDialog mProgressDialog;

    private static final String BACCARAT = "baccarat";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slot_new);
        rl_bjl = (RecyclerView) findViewById(R.id.rl_bjl);
        initView();
    }

    private void initView(){
        LotteryInfo lotteryInfo1 = new LotteryInfo(LotteryConfig.LOTTERY_ID.BAIJIALES, getResources().getString(R.string.lottery_name_bjls), null);
        LotteryInfo lotteryInfo2 = new LotteryInfo(LotteryConfig.LOTTERY_ID.CHATROOM_BJL, getResources().getString(R.string.lottery_name_chatroom_bjl),null);
        LotteryInfo lotteryInfo3 = new LotteryInfo(LotteryConfig.LOTTERY_ID.FISHING, getResources().getString(R.string.lottery_name_fishing), null);
        ArrayList<LotteryInfo> lotteryInfos = new ArrayList<>();
        lotteryInfos.add(lotteryInfo2);
        lotteryInfos.add(lotteryInfo1);
        lotteryInfos.add(lotteryInfo3);
        bjlAdapter = new BJLAdapter(lotteryInfos,this);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        rl_bjl.setAdapter(bjlAdapter);
        rl_bjl.setLayoutManager(manager);
        bjlAdapter.setOnItemClickListener(new BJLAdapter.OnItemClickListener() {
            @Override
            public void onclick(int position) {
                switch (position){
                    case 1:
                        shouldGoTiger(CommonConfig.THREE_D_BACARRAT);
                        break;
                    case 0:
                        Intent intent = new Intent();
                        intent.putExtra("typeurl", BACCARAT);
                        intent.setClass(SlotNewActivity.this,BJLActivity.class);
                        startActivity(intent);
                        break;
                    case 2:
                        shouldGoTiger(CommonConfig.FISHING);
                        break;
                }
            }
        });
    }

    public void backClick (View v){
        finish();
    }

    private boolean mIsWait = false;
    private void goTiger(final String theme) {
//        if(!mIsWait) {
//            mIsWait = true;
//            mIsError = false;
//
//            MyAsyncTask<BalanceInfo> task = new MyAsyncTask<BalanceInfo>(SlotNewActivity.this) {
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
                        bundle.putString(CommonConfig.BUNDLE_GOTIGER_SCENE,CommonConfig.THREE_D_BACARRAT);
                        if(CommonConfig.THREE_D_BACARRAT.equals(theme)){
                            bundle.putString(CommonConfig.BUNDLE_GOTIGER_SCENE,CommonConfig.THREE_D_BACARRAT);
                        }else if(CommonConfig.FISHING.equals(theme)){
                            bundle.putString(CommonConfig.BUNDLE_GOTIGER_SCENE,CommonConfig.FISHING);
                        }
                        IntentUtil.redirectToNextActivity(SlotNewActivity.this, SlotActivity.class, bundle);
                        String slotData = SlotUtl.buildDataAccordingToScene(
                                theme,
                                OkHttpClientManager.getInstance().getUserName(),
                                AASlotUrl,
                                BaseService.BASE_BALANCE);
                        UnityPlayer.UnitySendMessage("Preload"
                                , "getIntentData"
                                , slotData);
                        finish();
//                    }
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

    private void shouldGoTiger(final String theme){
        mIsError = false;
        showProgressDialog();
        MyAsyncTask<List<GameListInfo>> task = new MyAsyncTask<List<GameListInfo>>(SlotNewActivity.this) {
            @Override
            public List<GameListInfo> callService() throws IOException, JsonParseException, BizException, ServiceException {
                return new HomeService().getGameList();
            }

            @Override
            public void onLoaded(List<GameListInfo> paramT) throws Exception {
                closeProgressDialog();
                if(!mIsError){
                    for(GameListInfo gameListInfo : paramT){
                        if(theme.equals(gameListInfo.getGamecode())){
                            if(CommonConfig.THREE_D_BACARRAT.equals(theme)){
                                is3DBacarratOnline = true;
                            }else if(CommonConfig.FISHING.equals(theme)){
                                isFishing = true;
                            }
                            goTiger(theme);
                            break;
                        }
                    }
                    if(!is3DBacarratOnline && !isFishing){
                        MyToast.show(SlotNewActivity.this,"聚星捕鱼王即将上线！");
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
    private void showProgressDialog() {
        try {
            mProgressDialog = DialogUtil.getProgressDialog(this, getResources().getString(R.string.loading_list));
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
