package com.hec.app.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.hec.app.R;
import com.hec.app.activity.base.BaseActivity;
import com.hec.app.adapter.MyDragAdapter;
import com.hec.app.adapter.OtherAdapter;
import com.hec.app.entity.LotteryInfo;
import com.hec.app.framework.adapter.DragAdapter;
import com.hec.app.framework.widget.DragGridView;
import com.hec.app.framework.widget.MyGridView;
import com.hec.app.lottery.LotteryConfig;
import com.hec.app.util.LotteryUtil;
import com.hec.app.webservice.LotteryService;

import java.util.ArrayList;
import java.util.List;

public class OfenPlayLotteryActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    private DragGridView userGridView;
    private MyGridView otherGridView;
    DragAdapter selectedAdapter;
    OtherAdapter unSelectedAdapter;
    List<LotteryInfo> unSelectedLotteryTypeList = new ArrayList<>();
    List<LotteryInfo> selectedLotteryTypeList = new ArrayList<>();
    List<LotteryInfo> allLotteryList = new ArrayList<>();
    List<LotteryInfo> midLotteryList = new ArrayList<>();
    boolean isMove = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_often_play_lottery);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");
        toolbar.setNavigationIcon(R.mipmap.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        initView();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initData() {
        try {
            selectedLotteryTypeList = new LotteryService().getOftenPlayLotteryInfo(LotteryConfig.PLAY_MODE.CLASSIC);
            allLotteryList = new LotteryService().getLotteryInfo(LotteryConfig.PLAY_MODE.CLASSIC);

            if (allLotteryList == null) {
                return;
            }
            else if (selectedLotteryTypeList == null) {
                selectedLotteryTypeList = new ArrayList<>();
            }

            boolean isAddSlot = true;
            boolean isAddBjl = true;
            for (LotteryInfo lotteryInfo : allLotteryList) {
                switch (lotteryInfo.getLotteryID()) {
                    case LotteryConfig.LOTTERY_ID.SLOT:
                        isAddSlot = false;
                        break;
                    case LotteryConfig.LOTTERY_ID.BAIJIALE:
                        isAddBjl = false;
                        break;
                }
            }

            if (isAddSlot) {
                allLotteryList.add(0, new LotteryInfo(LotteryConfig.LOTTERY_ID.SLOT, getString(R.string.lottery_name_slot), null));
            }
            if (isAddBjl) {
                allLotteryList.add(0, new LotteryInfo(LotteryConfig.LOTTERY_ID.BAIJIALE, getString(R.string.lottery_name_bjl), null));
            }
        } catch (Exception e) {
        }

        for (LotteryInfo l : allLotteryList) {
            for(LotteryInfo ll : selectedLotteryTypeList){
                if (l.getLotteryType().equals(ll.getLotteryType())){
                    midLotteryList.add(l);
                }
            }
        }

        for(LotteryInfo l : allLotteryList){
            if(!midLotteryList.contains(l)) {
                unSelectedLotteryTypeList.add(l);
            }
        }

        List<LotteryInfo> tmp = new ArrayList<LotteryInfo>();
        for(LotteryInfo l : selectedLotteryTypeList){
            //Log.i("wxj","sel "+l.getLotteryType());
            if(!l.getLotteryType().contains("江西") && LotteryUtil.getLotteryIcon(l.getLotteryType()) != 0 ) {
                tmp.add(l);
            }
        }
        selectedLotteryTypeList = new ArrayList<>(tmp);
        tmp = new ArrayList<LotteryInfo>();
        for(LotteryInfo l : unSelectedLotteryTypeList){
            //Log.i("wxj","un "+l.getLotteryType());
            if(!l.getLotteryType().contains("江西") && LotteryUtil.getLotteryIcon(l.getLotteryType()) != 0 ) {
                tmp.add(l);
            }
        }
        unSelectedLotteryTypeList = new ArrayList<>(tmp);

        selectedAdapter = new MyDragAdapter(this, selectedLotteryTypeList);
        userGridView.setAdapter(selectedAdapter);
        unSelectedAdapter = new OtherAdapter(this, unSelectedLotteryTypeList);
        otherGridView.setAdapter(this.unSelectedAdapter);

        otherGridView.setOnItemClickListener(this);
        userGridView.setOnItemClickListener(this);
    }

    private void initView() {
        userGridView = (DragGridView) findViewById(R.id.userGridView);
        otherGridView = (MyGridView) findViewById(R.id.otherGridView);
    }

    /**
     * GRIDVIEW对应的ITEM点击监听接口
     */
    @Override
    public void onItemClick(AdapterView<?> parent, final View view, final int position, long id) {
        //如果点击的时候，之前动画还没结束，那么就让点击事件无效
        if (isMove) {
            return;
        }
        switch (parent.getId()) {
            case R.id.userGridView:
                final ImageView usermMoveImageView = getView(view);
                if (usermMoveImageView != null) {
                    ImageView newTextView = (ImageView) view.findViewById(R.id.img_item);
                    final int[] startLocation = new int[2];
                    newTextView.getLocationInWindow(startLocation);
                    final LotteryInfo channel = (LotteryInfo) parent.getAdapter().getItem(position);//获取点击的频道内容

                    unSelectedAdapter.setVisible(false);
                    //添加到最后一个
                    unSelectedAdapter.addItem(channel);
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            try {
                                int[] endLocation = new int[2];
                                //获取终点的坐标
                                otherGridView.getChildAt(otherGridView.getLastVisiblePosition()).getLocationInWindow(endLocation);
                                MoveAnim(usermMoveImageView, startLocation, endLocation, channel, userGridView);
                                selectedAdapter.setRemove(position);
                            } catch (Exception localException) {
                            }
                        }
                    }, 50L);
                }

                break;
            case R.id.otherGridView:
                final ImageView moveImageView = getView(view);
                if (moveImageView != null) {
                    ImageView imageView = (ImageView) view.findViewById(R.id.img_item);
                    final int[] startLocation = new int[2];
                    imageView.getLocationInWindow(startLocation);
                    final LotteryInfo lotteryInfo = ((OtherAdapter) parent.getAdapter()).getItem(position);
                    selectedAdapter.setVisible(false);
                    //添加到最后一个
                    selectedAdapter.addItem(lotteryInfo);
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            try {
                                int[] endLocation = new int[2];
                                //获取终点的坐标
                                userGridView.getChildAt(userGridView.getLastVisiblePosition()).getLocationInWindow(endLocation);
                                MoveAnim(moveImageView, startLocation, endLocation, lotteryInfo, otherGridView);
                                unSelectedAdapter.setRemove(position);
                            } catch (Exception localException) {
                            }
                        }
                    }, 50L);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 点击ITEM移动动画
     *
     * @param moveView
     * @param startLocation
     * @param endLocation
     * @param moveChannel
     * @param clickGridView
     */
    private void MoveAnim(View moveView, int[] startLocation, int[] endLocation, final LotteryInfo moveChannel,
                          final GridView clickGridView) {
        int[] initLocation = new int[2];
        //获取传递过来的VIEW的坐标
        moveView.getLocationInWindow(initLocation);
        //得到要移动的VIEW,并放入对应的容器中
        final ViewGroup moveViewGroup = getMoveViewGroup();
        final View mMoveView = getMoveView(moveViewGroup, moveView, initLocation);
        //创建移动动画
        TranslateAnimation moveAnimation = new TranslateAnimation(
                startLocation[0], endLocation[0], startLocation[1],
                endLocation[1]);
        moveAnimation.setDuration(300L);//动画时间
        //动画配置
        AnimationSet moveAnimationSet = new AnimationSet(true);
        moveAnimationSet.setFillAfter(false);//动画效果执行完毕后，View对象不保留在终止的位置
        moveAnimationSet.addAnimation(moveAnimation);
        mMoveView.startAnimation(moveAnimationSet);
        moveAnimationSet.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                isMove = true;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                moveViewGroup.removeView(mMoveView);
                // instanceof 方法判断2边实例是不是一样，判断点击的是DragGrid还是OtherGridView
                if (clickGridView instanceof DragGridView) {
                    unSelectedAdapter.setVisible(true);
                    unSelectedAdapter.notifyDataSetChanged();
                    selectedAdapter.remove();
                } else {
                    selectedAdapter.setVisible(true);
                    selectedAdapter.notifyDataSetChanged();
                    unSelectedAdapter.remove();
                }
                isMove = false;
            }
        });
    }

    /**
     * 获取移动的VIEW，放入对应ViewGroup布局容器
     *
     * @param viewGroup
     * @param view
     * @param initLocation
     * @return
     */
    private View getMoveView(ViewGroup viewGroup, View view, int[] initLocation) {
        int x = initLocation[0];
        int y = initLocation[1];
        viewGroup.addView(view);
        LinearLayout.LayoutParams mLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mLayoutParams.leftMargin = x;
        mLayoutParams.topMargin = y;
        view.setLayoutParams(mLayoutParams);
        return view;
    }

    /**
     * 创建移动的ITEM对应的ViewGroup布局容器
     */
    private ViewGroup getMoveViewGroup() {
        ViewGroup moveViewGroup = (ViewGroup) getWindow().getDecorView();
        LinearLayout moveLinearLayout = new LinearLayout(this);
        moveLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        moveViewGroup.addView(moveLinearLayout);
        return moveLinearLayout;
    }

    /**
     * 获取点击的Item的对应View，
     *
     * @param view
     * @return
     */
    private ImageView getView(View view) {
        view.destroyDrawingCache();
        view.setDrawingCacheEnabled(true);
        Bitmap cache = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);
        ImageView iv = new ImageView(this);
        iv.setImageBitmap(cache);
        return iv;
    }

    @Override
    public void onBackPressed() {
        saveOfenPlay();

        super.onBackPressed();
    }

    private void saveOfenPlay(){
        if(selectedAdapter!=null){
            List<LotteryInfo> list = selectedAdapter.getLotteryTypeList();
            for(LotteryInfo l : list){
                Log.i("quicksave",l.getLotteryType());
            }
            new LotteryService().setOftenPlayLotteryInfo(list);
        }else{
            new LotteryService().setOftenPlayLotteryInfo(new ArrayList<LotteryInfo>());
        }
    }
}

