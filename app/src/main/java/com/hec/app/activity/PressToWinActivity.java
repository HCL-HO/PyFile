package com.hec.app.activity;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.UserManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonParseException;
import com.hec.app.R;
import com.hec.app.activity.base.BaseActivityWithMenu;
import com.hec.app.activity.base.BaseApp;
import com.hec.app.entity.BizException;
import com.hec.app.entity.CustomerInfo;
import com.hec.app.entity.HomeBalanceInfo;
import com.hec.app.entity.LayoutInfo;
import com.hec.app.entity.LotteryDrawResultInfo;
import com.hec.app.entity.MMCInfo;
import com.hec.app.entity.PartlyLotteryInfo;
import com.hec.app.entity.PlaceOrderInfo;
import com.hec.app.entity.Response;
import com.hec.app.entity.TrendHistoryInfo;
import com.hec.app.framework.adapter.CommonAdapter;
import com.hec.app.framework.adapter.ViewHolder;
import com.hec.app.framework.widget.MyToast;
import com.hec.app.framework.widget.ResideMenu;
import com.hec.app.lottery.LotteryConfig;
import com.hec.app.util.BitmapUtil;
import com.hec.app.util.CustomerAccountManager;
import com.hec.app.util.DialogUtil;
import com.hec.app.util.DisplayUtil;
import com.hec.app.util.IntentUtil;
import com.hec.app.util.LotteryUtil;
import com.hec.app.util.MyAsyncTask;

import com.hec.app.util.TestUtil;
import com.hec.app.webservice.AccountService;
import com.hec.app.webservice.LotteryService;
import com.hec.app.webservice.ServiceException;

import junit.framework.Test;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;


/**
 * Created by Isaac on 20/5/2016.
 */
public class PressToWinActivity extends BaseActivityWithMenu {

    int betMultiple, currentLotteryID;
    ImageView imgPersonalCenter;
    LotteryDrawResultInfo lotteryDrawResultInfo;
    LotteryService lotteryService;
    boolean mIsError, firstTime;
    private ProgressDialog mProgressDialog;
    PlaceOrderInfo orderInfo;
    double betAmount;
    private ResideMenu resideMenu;
    private int[] yoffset = new int[10];
    private float[] speed = new float[10];
    int[] end = new int[10];
    //0 = idle  1 = playing  2 = startSlowing  3 = isSlowing
    int[] state = new int[10];
    Bitmap finalBitmap;
    Bitmap finalBitmap10;
    Boolean isPlaying = false;
    MyAsyncTask<LotteryDrawResultInfo> task;
    private ArrayList<ValueAnimator> animators;
    private Dialog windialog;
    private Dialog losedialog;
    private boolean canLeave = true;
    private Toolbar toolbar;
    private String lotteryName, playtype, playtyperadio;
    private ImageView multipleDigit1, multipleDigit2, multipleDigit3 ,multipleDigit4;
    private ImageView remaining1, remaining2, remaining3 ,remaining4, remaining5, remaining6, remaining7, remaining8, remaining9, remaining10;
    private ImageView total1, total2, total3 ,total4, total5, total6, total7, total8, total9, total10;
    private ListView history;
    private RelativeLayout pkup, pkdown,five;
    private ImageView pkupImg, pkdownImg;
    private boolean allstopped = false;
    private Double soloAmount;
    double userMoney;
    private String currentwinNum;
    private TextView presstowinnum;
    private String CurrentPlayNo;
    private Handler mHandler2 = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            getLatestLotteryResult();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_press_to_win);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.mipmap.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(canLeave)
                    onBackPressed();
            }
        });
        resideMenu = super.getResidingMenu();
        imgPersonalCenter = (ImageView)findViewById(R.id.imgPersonalCenter);
        imgPersonalCenter.setClickable(false);
        imgPersonalCenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(canLeave)
                    resideMenu.openMenu(ResideMenu.DIRECTION_RIGHT);
            }
        });
        lotteryService = new LotteryService();
        Serializable serializableData = getIntent().getSerializableExtra("PlaceOrderInfo");
        if (serializableData != null) {
            orderInfo = (PlaceOrderInfo) serializableData;
            currentLotteryID = orderInfo.getLotteryID();
            lotteryName = orderInfo.getLotteryName();
            soloAmount = orderInfo.getPrice() * orderInfo.getQty();
            playtype = orderInfo.getPlayTypeName();
            playtyperadio = orderInfo.getPlayTypeRadioName();

        }else{
            currentLotteryID = getIntent().getIntExtra("LotteryID",0);
            playtype = getIntent().getStringExtra("PlayTypeName");
            playtyperadio = getIntent().getStringExtra("PlayTypeRadioName");
            lotteryName = getIntent().getStringExtra("LotteryName");
            soloAmount = getIntent().getDoubleExtra("amount",0);
        }
        TextView title = (TextView) findViewById(R.id.title);
        title.setText(lotteryName.replace("和盛","聚星"));
        betAmount = soloAmount;

        setupDialog();

        try {
            //Bitmap SOURCE_BITMAP = BitmapFactory.decodeResource(getResources(), R.mipmap.rolling_num); // Get the source Bitmap using your favorite method :-)
            //change the bytes which used in inflating every px,avoid OOM.
            Bitmap SOURCE_BITMAP = BitmapUtil.readBitMap(this,R.mipmap.rolling_num, Bitmap.Config.ALPHA_8);
            finalBitmap = concat(SOURCE_BITMAP, SOURCE_BITMAP);
            //SOURCE_BITMAP = BitmapFactory.decodeResource(getResources(), R.mipmap.rolling_num_pk); // Get the source Bitmap using your favorite method :-)
            SOURCE_BITMAP = BitmapUtil.readBitMap(this,R.mipmap.rolling_num_pk, Bitmap.Config.ALPHA_8);
            finalBitmap10 = concat(SOURCE_BITMAP, SOURCE_BITMAP);
        }catch (OutOfMemoryError OOM){
            MyToast.show(this,"您的内存空间可能不足!");
            IntentUtil.redirectToNextActivity(PressToWinActivity.this,HomeActivity.class);
        }
        multipleDigit1 = (ImageView) findViewById(R.id.multiple1);
        multipleDigit2 = (ImageView) findViewById(R.id.multiple2);
        multipleDigit3 = (ImageView) findViewById(R.id.multiple3);
        multipleDigit4 = (ImageView) findViewById(R.id.multiple4);

        remaining1 = (ImageView) findViewById(R.id.remaining1);
        remaining2 = (ImageView) findViewById(R.id.remaining2);
        remaining3 = (ImageView) findViewById(R.id.remaining3);
        remaining4 = (ImageView) findViewById(R.id.remaining4);
        remaining5 = (ImageView) findViewById(R.id.remaining5);
        remaining6 = (ImageView) findViewById(R.id.remaining6);
        remaining7 = (ImageView) findViewById(R.id.remaining7);
        remaining8 = (ImageView) findViewById(R.id.remaining8);
        remaining9 = (ImageView) findViewById(R.id.remaining9);
        remaining10 = (ImageView) findViewById(R.id.remaining10);

        total1 = (ImageView) findViewById(R.id.total1);
        total2 = (ImageView) findViewById(R.id.total2);
        total3 = (ImageView) findViewById(R.id.total3);
        total4 = (ImageView) findViewById(R.id.total4);
        total5 = (ImageView) findViewById(R.id.total5);
        total6 = (ImageView) findViewById(R.id.total6);
        total7 = (ImageView) findViewById(R.id.total7);
        total8 = (ImageView) findViewById(R.id.total8);
        total9 = (ImageView) findViewById(R.id.total9);
        total10 = (ImageView) findViewById(R.id.total10);

        five = (RelativeLayout) findViewById(R.id.five);
        pkup = (RelativeLayout) findViewById(R.id.pk10up);
        pkdown = (RelativeLayout) findViewById(R.id.pk10down);
        pkupImg = (ImageView) findViewById(R.id.pk10upImg);
        pkdownImg = (ImageView) findViewById(R.id.pk10downImg);
        history = (ListView) findViewById(R.id.history);
        getNextIssueNo(true);
        initPressToWin();
    }
    private void initPressToWin () {

        if(lotteryName.contains("PK")) {
            pkup.setVisibility(View.VISIBLE);
            pkdown.setVisibility(View.VISIBLE);
        }else{
            five.setVisibility(View.VISIBLE);
        }
        animators = new ArrayList<ValueAnimator>();

        if(lotteryName.contains("PK")){
            int WIDTH_PX = finalBitmap10.getWidth();
            int HEIGHT_PX = finalBitmap10.getHeight() / 20;
            Bitmap newBitmap = Bitmap.createBitmap(finalBitmap10, 0 ,0, WIDTH_PX, HEIGHT_PX, null, false);
            for(int i = 0; i < 10; i ++){

                final ImageView image1;
                if(i < 5) {
                    LinearLayout ll = (LinearLayout) pkup.getChildAt(1);

                    image1 =(ImageView) ll.getChildAt(i );
                }else {
                    LinearLayout ll = (LinearLayout) pkdown.getChildAt(1);
                    image1 = (ImageView) ll.getChildAt(i - 5);
                }
                image1.setImageBitmap(newBitmap);

                ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
                final int index = i;
                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        int current_position = yoffset[index] % (finalBitmap10.getHeight() / 2);
                        int desired_position = finalBitmap10.getHeight() / 20 * ((end[index] - 1) % 10) ;
                        if (state[index] == 2){
                            int tmp = (1 + 60) * 30;
                            int difference = Math.abs(desired_position - (current_position + tmp) % (finalBitmap10.getHeight() / 2)) % (finalBitmap10.getHeight() / 2);
                            if(difference < 61 ){
                                state[index] = 3;
                            }

                        }else if (state[index] == 3){
                            //Log.d("hi",current_position + " " + desired_position + " " + speed[index]);
                            if(speed[index] > 0){
                                speed[index] -= 1f;
                            }else{
                                if(current_position != desired_position){
                                    yoffset[index] = desired_position;
                                }
                                speed[index] = 0;
                                state[index] = 0;
                                boolean allstop = true;
                                for(float sp: speed){
                                    if (sp > 0){
                                        allstop = false;
                                    }
                                }
                                if(allstop && !allstopped ){
                                    allstopped = true;
                                    mHandler.sendEmptyMessage(0);
                                }
                            }
                        }
                        yoffset[index] = (yoffset[index] + (int)speed[index]) % finalBitmap10.getHeight();
                        if (yoffset[index] >= finalBitmap10.getHeight() / 20 * 12) {
                            yoffset[index] -= finalBitmap10.getHeight() / 20 * 10;
                        }
                        if(yoffset[index]>=0) {
                            Bitmap newBitmap = Bitmap.createBitmap(finalBitmap10, 0, yoffset[index], finalBitmap10.getWidth(), finalBitmap10.getHeight() / 20, null, false);
                            image1.setImageBitmap(newBitmap);
                        }
                    }
                });
                valueAnimator.setDuration(5000);
                valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
                valueAnimator.setRepeatMode(ValueAnimator.RESTART);
                animators.add(valueAnimator);
            }

        }else{
            int WIDTH_PX = finalBitmap.getWidth();
            int HEIGHT_PX = finalBitmap.getHeight() / 20;
            Bitmap newBitmap = Bitmap.createBitmap(finalBitmap, 0 ,0, WIDTH_PX, HEIGHT_PX, null, false);
            for(int i = 0; i < 5; i ++){
                LinearLayout ll = (LinearLayout) five.getChildAt(1);
                final ImageView image1 = (ImageView)ll.getChildAt(i);
                image1.setImageBitmap(newBitmap);
                ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
                final int index = i;
                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        int current_position = yoffset[index] % (finalBitmap.getHeight() / 2);
                        int desired_position = finalBitmap.getHeight() / 20 * ((end[index]) % 10) ;
                        if (state[index] == 2){
                            int tmp = (1 + 60) * 30;
                            int difference = Math.abs(desired_position - (current_position + tmp) % (finalBitmap.getHeight() / 2)) % (finalBitmap.getHeight() / 2);
                            if(difference < 61 ){
                                state[index] = 3;
                            }

                        }else if (state[index] == 3){
                            //Log.d("hi",current_position + " " + desired_position + " " + speed[index]);
                            if(speed[index] > 0){
                                speed[index] -= 1f;
                            }else{
                                if(current_position != desired_position){
                                    yoffset[index] = desired_position;
                                }
                                speed[index] = 0;
                                state[index] = 0;
                                boolean allstop = true;
                                for(float sp: speed){
                                    if (sp > 0){
                                        allstop = false;
                                    }
                                }
                                if(allstop && !allstopped ){
                                    allstopped = true;
                                    mHandler.sendEmptyMessage(0);
                                }
                            }
                        }
                        yoffset[index] = (yoffset[index] + (int)speed[index]) % finalBitmap.getHeight();
                        if (yoffset[index] >= finalBitmap.getHeight() / 20 * 12) {
                            yoffset[index] -= finalBitmap.getHeight() / 20 * 10;
                        }
                        if(yoffset[index]>=0){
                            Bitmap newBitmap = Bitmap.createBitmap(finalBitmap, 0, yoffset[index], finalBitmap.getWidth(), finalBitmap.getHeight() / 20, null, false);
                            image1.setImageBitmap(newBitmap);
                        }

                    }
                });
                valueAnimator.setDuration(5000);
                valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
                valueAnimator.setRepeatMode(ValueAnimator.RESTART);
                animators.add(valueAnimator);
            }

        }
        firstTime = true;
        betMultiple = 1;
        updateMultiple();
        getHomeBalanceInfo();
        updateBetAmount();
        getLatestLotteryResult();
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(lotteryName.contains("PK")){
            for(int i = 0; i < 10; i ++){
                final ImageView image1;
                if(i < 5) {
                    LinearLayout ll = (LinearLayout) pkup.getChildAt(1);
                    image1 =(ImageView) ll.getChildAt(i );
                }else {
                    LinearLayout ll = (LinearLayout) pkdown.getChildAt(1);
                    image1 = (ImageView) ll.getChildAt(i - 5);
                }
                image1.setVisibility(View.VISIBLE);
                LinearLayout.LayoutParams param2 = new LinearLayout.LayoutParams((int)(pkupImg.getWidth() * 0.137 ), (int)(pkupImg.getWidth()  * 0.139 ));
//                TestUtil.print(pkup.getWidth()+"");
//                TestUtil.print(pkup.getHeight()+"");
//                TestUtil.print((pkup.getHeight() * 0.25)+"");
                if(i != 0 && i != 5)
                    param2.setMargins((int)(pkupImg.getWidth()  * 0.039 ),  0 , 0, (int)(pkupImg.getHeight() * 0.1));
                image1.setLayoutParams(param2);
            }

        }else{
            LinearLayout ll = (LinearLayout) five.getChildAt(1);
            for(int i = 0; i < 5; i ++){
                final ImageView image1 = (ImageView)ll.getChildAt(i);
                image1.setVisibility(View.VISIBLE);
                LinearLayout.LayoutParams param2 = new LinearLayout.LayoutParams((int)(five.getWidth() * 0.139 ), (int)(five.getWidth() * 0.14 ));
                if(i != 0)
                    param2.setMargins((int)(five.getWidth() * 0.0372) , 0, 0, (int)(five.getHeight()  * 0.15));
                image1.setLayoutParams(param2);
            }

        }
    }

    public void pressMultiple(View v){
        if(!firstTime) {
            int number = Integer.parseInt((String) v.getTag());
            if (number == 10) {
                betMultiple = 0;

            } else {
                if (betMultiple < 1000) {
                    betMultiple *= 10;
                    betMultiple += number;
                }
            }
            updateMultiple();
        }
    }
    private void updateMultiple(){
        String tmp = String.valueOf(betMultiple);
        multipleDigit1.setVisibility(View.INVISIBLE);
        multipleDigit2.setVisibility(View.INVISIBLE);
        multipleDigit3.setVisibility(View.INVISIBLE);
        multipleDigit4.setVisibility(View.INVISIBLE);
        for (int i = 0; i < tmp.length(); i++) {
            int id = getBaseContext().getResources().getIdentifier("d" + tmp.charAt(tmp.length() - i - 1), "mipmap",
                    getBaseContext().getPackageName());
            switch (i) {
                case 3:
                    multipleDigit1.setImageResource(id);
                    multipleDigit1.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    multipleDigit2.setImageResource(id);
                    multipleDigit2.setVisibility(View.VISIBLE);
                    break;
                case 1:
                    multipleDigit3.setImageResource(id);
                    multipleDigit3.setVisibility(View.VISIBLE);
                    break;
                case 0:
                    multipleDigit4.setImageResource(id);
                    multipleDigit4.setVisibility(View.VISIBLE);
                    break;

            }

        }
        if(betMultiple != 0){
            betAmount = soloAmount * betMultiple;
        }else{
            betAmount = soloAmount;
        }
        updateBetAmount();
    }
    public void openLottery(View v){
        if(!isPlaying) {

            if(betMultiple == 0){
                MyToast.show(PressToWinActivity.this, "倍数不能为零");
                return;
            }
            if(betAmount>userMoney){
                MyToast.show(PressToWinActivity.this,"投注金额不能超过账户余额");
                return;
            }
            allstopped = false;
            isPlaying = true;
            rolling();
            if (canLeave) {
                canLeave = false;
                toolbar.getNavigationIcon().setAlpha(50);
                imgPersonalCenter.setImageAlpha(50);
            }
            mIsError = false;

            MyAsyncTask<com.hec.app.entity.Response> task = new MyAsyncTask<com.hec.app.entity.Response>(this) {

                @Override
                public com.hec.app.entity.Response callService() throws IOException,
                        JsonParseException, BizException, ServiceException {
                    int iType = firstTime ? 1 : 2;
                    try{
                        return lotteryService.openMMCLottery(currentLotteryID, lotteryDrawResultInfo
                                .getCurrentTime().getIssueNo(), iType, betMultiple, LotteryConfig.PLAY_MODE.CLASSIC);
                    }catch (NullPointerException e){
                        return null;
                    }
                }

                @Override
                public void onLoaded(com.hec.app.entity.Response result) throws Exception {
                    if (!mIsError) {

                        if (result.getSuccess()) {
                            mHandler.sendEmptyMessageDelayed(5, 2000);
                        } else {
                            onErrorFunction();
                            MyToast.show(PressToWinActivity.this, result.getMessage());
                        }
                    }else{
                        if(BaseApp.getAppBean().resetApiUrl(PressToWinActivity.this)){
                            Toast.makeText(PressToWinActivity.this, "抱歉，网络错误，请查看记录是否已经投注", Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(PressToWinActivity.this, "抱歉，网络错误，请查看记录是否已经投注", Toast.LENGTH_LONG).show();
                        }

                    }
                }
            };
            task.setOnError(new MyAsyncTask.OnError() {

                @Override
                public void handleError(Exception e) {
                    closeProgressDialog();
                    mIsError = true;
                    onErrorFunction();
                }
            });
            task.executeTask();
        }
    }

    private void onErrorFunction(){
        for(int i = 0; i < animators.size(); i ++) {
            stopRolling(i);
        }
        if(!canLeave){
            imgPersonalCenter.setImageAlpha(255);
            toolbar.getNavigationIcon().setAlpha(255);
            canLeave = true;
        }
    }

    private void rolling (){
        for (int i = 0; i < animators.size(); i++) {
            state[i] = 1;
            end[i] = 0;
            speed[i] = 60;
            animators.get(i).start();
        }
    }

    private void stopRolling (int index){
        if(state[index] == 1)
            state[index] = 2;
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //Log.d("end","" + msg.what);
            switch (msg.what) {
                case 0:
                    for (int i = 0 ; i < animators.size() ; i++) {
                        animators.get(i).end();
                    }
                    break;
                case 5:
                    getNextIssueNo(false);
                    break;
                case 6:
                    checkMMC(currentLotteryID, lotteryDrawResultInfo.getLatestTime().getIssueNo(), null);
                    break;
            }
        }
    };

    public static Bitmap concat(Bitmap bmp1, Bitmap bmp2) {
        Bitmap bmConcat = Bitmap.createBitmap(bmp1.getWidth(), 2 * bmp1.getHeight(), bmp1.getConfig());
        Canvas canvas = new Canvas(bmConcat);
        canvas.drawBitmap(bmp1, new Matrix(), null);
        canvas.drawBitmap(bmp2, 0, bmp1.getHeight(), null);
        return bmConcat;
    }

    private void updateBetAmount () {
        TestUtil.print(betAmount+"");
        DecimalFormat format = new DecimalFormat("#.##");
        String tmp = String.valueOf(format.format(betAmount));
        if(tmp.length() > 10){
            tmp = tmp.substring(0,10);
        }
        total1.setVisibility(View.INVISIBLE);
        total2.setVisibility(View.INVISIBLE);
        total3.setVisibility(View.INVISIBLE);
        total4.setVisibility(View.INVISIBLE);
        total5.setVisibility(View.INVISIBLE);
        total6.setVisibility(View.INVISIBLE);
        total7.setVisibility(View.INVISIBLE);
        total8.setVisibility(View.INVISIBLE);
        total9.setVisibility(View.INVISIBLE);
        total10.setVisibility(View.INVISIBLE);

        for (int i = 0; i < tmp.length(); i++) {
            int id;
            if(tmp.charAt(i) == '.'){
                id = getBaseContext().getResources().getIdentifier("ddots", "mipmap",
                        getBaseContext().getPackageName());
            }else{
                id = getBaseContext().getResources().getIdentifier("d" + tmp.charAt(i) + "s", "mipmap",
                        getBaseContext().getPackageName());
            }
            switch (tmp.length() - i - 1) {
                case 9:
                    total1.setImageResource(id);
                    total1.setVisibility(View.VISIBLE);
                    break;
                case 8:
                    total2.setImageResource(id);
                    total2.setVisibility(View.VISIBLE);
                    break;
                case 7:
                    total3.setImageResource(id);
                    total3.setVisibility(View.VISIBLE);
                    break;
                case 6:
                    total4.setImageResource(id);
                    total4.setVisibility(View.VISIBLE);
                    break;
                case 5:
                    total5.setImageResource(id);
                    total5.setVisibility(View.VISIBLE);
                    break;
                case 4:
                    total6.setImageResource(id);
                    total6.setVisibility(View.VISIBLE);
                    break;
                case 3:
                    total7.setImageResource(id);
                    total7.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    total8.setImageResource(id);
                    total8.setVisibility(View.VISIBLE);
                    break;
                case 1:
                    total9.setImageResource(id);
                    total9.setVisibility(View.VISIBLE);
                    break;
                case 0:
                    total10.setImageResource(id);
                    total10.setVisibility(View.VISIBLE);
                    break;

            }

        }
    }

    public void getHomeBalanceInfo(){
        mIsError = false;
        MyAsyncTask<HomeBalanceInfo> task = new MyAsyncTask<HomeBalanceInfo>(this) {

            @Override
            public HomeBalanceInfo callService() throws IOException,
                    JsonParseException, BizException, ServiceException {
                return new AccountService().getHomeBalanceInfo();
            }

            @Override
            public void onLoaded(HomeBalanceInfo result) throws Exception {
                if(PressToWinActivity.this == null || PressToWinActivity.this.isFinishing())
                    return;
                if (!mIsError) {
                    String tmp = result.getAvailableScores();
                    tmp = tmp.replace(",","");
                    userMoney = Double.valueOf(tmp);
                    if(tmp.length() > 10){
                        tmp = tmp.substring(0,10);

                    }
                    remaining1.setVisibility(View.INVISIBLE);
                    remaining2.setVisibility(View.INVISIBLE);
                    remaining3.setVisibility(View.INVISIBLE);
                    remaining4.setVisibility(View.INVISIBLE);
                    remaining5.setVisibility(View.INVISIBLE);
                    remaining6.setVisibility(View.INVISIBLE);
                    remaining7.setVisibility(View.INVISIBLE);
                    remaining8.setVisibility(View.INVISIBLE);
                    remaining9.setVisibility(View.INVISIBLE);
                    remaining10.setVisibility(View.INVISIBLE);
                    for (int i = 0; i < tmp.length(); i++) {
                        int id;
                        if(tmp.charAt(i) == '.'){
                            id = getBaseContext().getResources().getIdentifier("ddots", "mipmap",
                                    getBaseContext().getPackageName());
                        }else{
                            id = getBaseContext().getResources().getIdentifier("d" + tmp.charAt(i) + "s", "mipmap",
                                    getBaseContext().getPackageName());
                        }
                        switch (tmp.length() - i - 1) {
                            case 9:
                                remaining1.setImageResource(id);
                                remaining1.setVisibility(View.VISIBLE);
                                break;
                            case 8:
                                remaining2.setImageResource(id);
                                remaining2.setVisibility(View.VISIBLE);
                                break;
                            case 7:
                                remaining3.setImageResource(id);
                                remaining3.setVisibility(View.VISIBLE);
                                break;
                            case 6:
                                remaining4.setImageResource(id);
                                remaining4.setVisibility(View.VISIBLE);
                                break;
                            case 5:
                                remaining5.setImageResource(id);
                                remaining5.setVisibility(View.VISIBLE);
                                break;
                            case 4:
                                remaining6.setImageResource(id);
                                remaining6.setVisibility(View.VISIBLE);
                                break;
                            case 3:
                                remaining7.setImageResource(id);
                                remaining7.setVisibility(View.VISIBLE);
                                break;
                            case 2:
                                remaining8.setImageResource(id);
                                remaining8.setVisibility(View.VISIBLE);
                                break;
                            case 1:
                                remaining9.setImageResource(id);
                                remaining9.setVisibility(View.VISIBLE);
                                break;
                            case 0:
                                remaining10.setImageResource(id);
                                remaining10.setVisibility(View.VISIBLE);
                                break;
                        }
                    }
                }
                else {
                    BaseApp.changeUrl(PressToWinActivity.this, new BaseApp.OnChangeUrlListener() {
                        @Override
                        public void changeSuccess() {
                            getHomeBalanceInfo();
                        }

                        @Override
                        public void changeFail() {}
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

    private void checkMMC(final int lotteryID, final String LastestIssueNo,final String CurrentIssueNo){

        MyAsyncTask<MMCInfo> task = new MyAsyncTask<MMCInfo>(this) {

            @Override
            public MMCInfo callService() throws IOException,
                    JsonParseException, BizException, ServiceException {
                if(LastestIssueNo != null) {
                    return new LotteryService().checkMMCLottery(lotteryID, LastestIssueNo, LotteryConfig.PLAY_MODE.CLASSIC);
                }else{
                    return new LotteryService().checkMMCLottery(lotteryID, CurrentIssueNo, LotteryConfig.PLAY_MODE.CLASSIC);
                }
            }

            @Override
            public void onLoaded(MMCInfo result2) throws Exception {
                if(PressToWinActivity.this == null || PressToWinActivity.this.isFinishing())
                    return;
                if (!mIsError) {
                    if(LastestIssueNo != null) {
                        if (result2.getState() == 1) {
                            getLatestLotteryResult();

                            windialog.show();


                            if (lotteryDrawResultInfo != null) {
                                if (lotteryDrawResultInfo.getLatestTime().getCurrentLotteryNum() != null && presstowinnum != null) {
                                    presstowinnum.setText("中奖号码: " + lotteryDrawResultInfo.getLatestTime().getCurrentLotteryNum());
                                }
                            }
                        }
                        else if (result2.getState() == 2) {
                            losedialog.show();
                        }
                    }else{
                        betAmount = result2.getAmount();
                        updateBetAmount();
                    }

                }
                else {
                    BaseApp.changeUrl(PressToWinActivity.this, new BaseApp.OnChangeUrlListener() {
                        @Override
                        public void changeSuccess() {
                            checkMMC(lotteryID, LastestIssueNo, CurrentIssueNo);
                        }

                        @Override
                        public void changeFail() {}
                    });
                }
            }
        };
        task.setOnError(new MyAsyncTask.OnError() {

            @Override
            public void handleError(Exception e) {
                mIsError = true;
                onErrorFunction();
            }
        });
        task.executeTask();
    }

    public void getLatestLotteryResult() {
        mIsError = false;
        final LotteryService lotteryService = new LotteryService();
        lotteryService.setDefaultSize(4);
        MyAsyncTask<List<TrendHistoryInfo>> task = new MyAsyncTask<List<TrendHistoryInfo>>(PressToWinActivity.this) {

            @Override
            public List<TrendHistoryInfo> callService() throws IOException,
                    JsonParseException, BizException, ServiceException {
                return lotteryService.GetTrendHistory(currentLotteryID, 1, 0, LotteryConfig.PLAY_MODE.CLASSIC);
            }

            @Override
            public void onLoaded(List<TrendHistoryInfo> result) throws Exception {
                if(PressToWinActivity.this == null || PressToWinActivity.this.isFinishing())
                    return;
                if (!mIsError) {
                    if(result != null ) {
                        if(!result.get(0).getIssueNo().equals(CurrentPlayNo)){
                            mHandler2.sendEmptyMessageDelayed(0,2000);
                        }else{
                            List<TrendHistoryInfo> results;
                            if (result.size() >= 4) {
                                results = result.subList(0, 4);
                            } else {
                                results = result.subList(0, result.size());
                            }
                            if(results != null)
                                currentwinNum = result.get(0).getCurrentLotteryNum();
                            if(currentwinNum.length()>10)
                                currentwinNum = currentwinNum.replace(",0",",");
                            if(currentwinNum.startsWith("0")) currentwinNum = currentwinNum.substring(1,currentwinNum.length());
                            if(presstowinnum!=null) presstowinnum.setText("中奖号码: " + currentwinNum);
                            Log.i("wxj","trend "+result.toString());
                            bindData(results);
                        }
                    }
                }
                else {
                    BaseApp.changeUrl(PressToWinActivity.this, new BaseApp.OnChangeUrlListener() {
                        @Override
                        public void changeSuccess() {
                            getLatestLotteryResult();
                        }

                        @Override
                        public void changeFail() {}
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

    private void bindData(List<TrendHistoryInfo> list) {
        if(PressToWinActivity.this == null)
            return;
        Log.i("wxj","trend in bind "+ list.size());
        CommonAdapter<TrendHistoryInfo> adapter = new CommonAdapter<TrendHistoryInfo>(this, list, R.layout.list_item_mmc_history) {
            @Override
            public void convert(ViewHolder helper, TrendHistoryInfo item, int position) {
                LinearLayout container = helper.getView(R.id.ll_LotteryResult);
                showLotteryResult(item.getCurrentLotteryNum(), container);
                Log.i("wxj","currentnum "+item.getCurrentLotteryNum());
                final LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)container.getLayoutParams();

                if (params != null) {
                    params.height = (int)(findViewById(R.id.dummy1).getHeight() / 5.5);
                }
                helper.setText(R.id.tvIssueNo, item.getIssueNo());
                helper.setText(R.id.tvprofit, item.getProfit());
            }
        };
        history.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void showLotteryResult(String result, LinearLayout ll_LotteryResult) {
        ll_LotteryResult.removeAllViews();

        Context context = this;
        List<String> list = Arrays.asList(result.split(","));
        for (String s :
                list) {
            if (lotteryName.contains("PK")) {
                ImageView imageView = new ImageView(context);
                imageView.setImageResource(LotteryUtil.getPK10NumberImage(Integer.parseInt(s)));
                int dp3 = DisplayUtil.getPxByDp(context, 1);
                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                p.setMargins(0, 0, dp3, 0);
                ll_LotteryResult.addView(imageView, p);
            } else {
                TextView t = new TextView(context);
                t.setText(String.valueOf(Integer.parseInt(s)));
                int dp2 = DisplayUtil.getPxByDp(context, 2);
                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                p.setMargins(dp2, 0, dp2, 0);
                t.setGravity(Gravity.CENTER);
                t.setLayoutParams(p);
                t.setTextSize(12);
                t.setTextAppearance(context, R.style.mmc_Lottery_Head_Result_Style);
                t.setBackgroundResource(R.mipmap.lottery_ball);
                ll_LotteryResult.addView(t);
            }
        }

    }

    private void getNextIssueNo(final boolean first) {
        mIsError = false;
        if(first)
            showProgressDialog();
        MyAsyncTask<LotteryDrawResultInfo> task = new MyAsyncTask<LotteryDrawResultInfo>(this) {

            @Override
            public LotteryDrawResultInfo callService() throws IOException,
                    JsonParseException, BizException, ServiceException {
                return new LotteryService().getNextIssueNo(currentLotteryID, LotteryConfig.PLAY_MODE.CLASSIC);
            }

            @Override
            public void onLoaded(LotteryDrawResultInfo result) throws Exception {
                if(PressToWinActivity.this == null || PressToWinActivity.this.isFinishing())
                    return;

                closeProgressDialog();

                if (!mIsError) {
                    if (result.getLatestTime().getCurrentLotteryNum() == null && !first) {
                        mHandler.sendEmptyMessageDelayed(5, 2000);
                    }else {
                        lotteryDrawResultInfo = result;
                        CurrentPlayNo = result.getLatestTime().getIssueNo();
                        if (!first && lotteryDrawResultInfo!=null) {
                            if(lotteryDrawResultInfo.getLatestTime().getCurrentLotteryNum() != null){
                                String[] tmp = lotteryDrawResultInfo.getLatestTime().getCurrentLotteryNum().split(",");
                                for (int i = 0; i < tmp.length; i++) {
                                    end[i] = Integer.parseInt(tmp[i]);
                                    stopRolling(i);
                                }
                            }else{
                                for (int i = 0; i < 10; i++) {
                                    end[i] = 0;
                                    stopRolling(i);
                                }
                            }
                            mHandler.sendEmptyMessageDelayed(6, 2000);
                            //checkMMC(currentLotteryID, lotteryDrawResultInfo.getLatestTime().getIssueNo(), null);
                        }else{
                            if(lotteryDrawResultInfo.getCurrentTime().getIssueNo() != null) {
                                TextView currentIssue = (TextView) findViewById(R.id.currentIssueNoTv);
                                currentIssue.setText(Html.fromHtml("第<font color='#08a09D'>" + lotteryDrawResultInfo.getCurrentTime().getIssueNo() + "</font>期开奖"));
                                checkMMC(currentLotteryID, null, lotteryDrawResultInfo.getCurrentTime().getIssueNo());
                            }
                        }
                    }
                }
                else {
                    BaseApp.changeUrl(PressToWinActivity.this, new BaseApp.OnChangeUrlListener() {
                        @Override
                        public void changeSuccess() {
                            getNextIssueNo(first);
                        }

                        @Override
                        public void changeFail() {}
                    });
                }
            }
        };
        task.setOnError(new MyAsyncTask.OnError() {
            @Override
            public void handleError(Exception e) {
                closeProgressDialog();
                mIsError = true;
                onErrorFunction();
            }
        });
        task.executeTask();
    }

    private void showProgressDialog() {
        try {
            mProgressDialog = DialogUtil.getProgressDialog(this, getResources().getString(R.string.loading_data));
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
    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Iterator<ValueAnimator> iter = animators.iterator();

        while (iter.hasNext()) {
            ValueAnimator a = iter.next();
            a.end();
            iter.remove();
        }
        for (ValueAnimator a : animators){
            a.end();
            animators.remove(a);
        }
    }

    private void resetAfterOpen(){

        isPlaying = false;
        firstTime = false;
        getHomeBalanceInfo();
        getLatestLotteryResult();
        if(betMultiple != 0){
            betAmount = soloAmount * betMultiple;
        }else{
            betAmount = soloAmount;
        }
        updateBetAmount();
        TextView currentIssue = (TextView) findViewById(R.id.currentIssueNoTv);
        currentIssue.setText(Html.fromHtml("第<font color='#08a09D'>"  + lotteryDrawResultInfo.getCurrentTime().getIssueNo() + "</font>期开奖"));
        if(!canLeave){
            imgPersonalCenter.setImageAlpha(255);
            toolbar.getNavigationIcon().setAlpha(255);
            canLeave = true;
        }
//        if(lotteryName.contains("PK")){
//            int WIDTH_PX = finalBitmap10.getWidth();
//            int HEIGHT_PX = finalBitmap10.getHeight() / 20;
//            Bitmap newBitmap = Bitmap.createBitmap(finalBitmap10, 0 ,0, WIDTH_PX, HEIGHT_PX, null, false);
//            for(int i = 0; i < 10; i ++){
//                final ImageView image1;
//                if(i < 5) {
//                    LinearLayout ll = (LinearLayout) pkup.getChildAt(1);
//
//                    image1 =(ImageView) ll.getChildAt(i );
//                }else {
//                    LinearLayout ll = (LinearLayout) pkdown.getChildAt(1);
//                    image1 = (ImageView) ll.getChildAt(i - 5);
//                }
//                image1.setImageBitmap(newBitmap);
//            }
//
//        }else{
//            int WIDTH_PX = finalBitmap.getWidth();
//            int HEIGHT_PX = finalBitmap.getHeight() / 20;
//            Bitmap newBitmap = Bitmap.createBitmap(finalBitmap, 0 ,0, WIDTH_PX, HEIGHT_PX, null, false);
//            for(int i = 0; i < 5; i ++){
//                LinearLayout ll = (LinearLayout) five.getChildAt(1);
//                final ImageView image1 = (ImageView)ll.getChildAt(i);
//                image1.setImageBitmap(newBitmap);
//            }
//
//        }
    }
    private void setupDialog(){
        windialog = new Dialog(PressToWinActivity.this,R.style.custom_dialog_style);
        windialog.setCanceledOnTouchOutside(false);
        windialog.setContentView(R.layout.press_to_win_dialog);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        LinearLayout ll = (LinearLayout)windialog.findViewById(R.id.lldialog);
        presstowinnum = (TextView) windialog.findViewById(R.id.press_to_win_num);
        ll.getLayoutParams().width= metrics.widthPixels - 32;
        ll.getLayoutParams().height = (metrics.widthPixels - 32) * 763 / 1083;
        windialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        Button btn = (Button)windialog.findViewById(R.id.back);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetAfterOpen();
                windialog.dismiss();
            }
        });

        losedialog = new Dialog(PressToWinActivity.this,R.style.custom_dialog_style);
        losedialog.setCanceledOnTouchOutside(false);
        losedialog.setContentView(R.layout.press_to_lose_dialog);
        LinearLayout ll2 = (LinearLayout)losedialog.findViewById(R.id.lldialog);
        ll2.getLayoutParams().width= metrics.widthPixels - 128;
        ll2.getLayoutParams().height = (metrics.widthPixels - 128) * 424 / 976;
        losedialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        Button btn2 = (Button)losedialog.findViewById(R.id.back);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetAfterOpen();
                losedialog.dismiss();
            }
        });
    }
}
