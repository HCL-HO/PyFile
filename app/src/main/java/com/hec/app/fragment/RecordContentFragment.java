package com.hec.app.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonParseException;
import com.hec.app.R;
import com.hec.app.activity.base.BaseApp;
import com.hec.app.entity.AfterDetailLotteryInfo;
import com.hec.app.entity.BizException;
import com.hec.app.entity.DetailLotteryInfo;
import com.hec.app.framework.widget.MyToast;
import com.hec.app.util.DateTransmit;
import com.hec.app.util.DialogUtil;
import com.hec.app.util.LotteryUtil;
import com.hec.app.util.MyAsyncTask;
import com.hec.app.webservice.DetailLotteryService;
import com.hec.app.webservice.ServiceException;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.regex.Pattern;

/**
 * Created by Joshua on 2016/1/5.
 */
public class RecordContentFragment extends Fragment
{
    private boolean mIsError;
    private String mArgument;
    public static final String ARGUMENT = "argument";
    public static final String PLAYID = "playid";
    public static final String AFTERNOID = "afternoid";
    public static final String RESPONSE = "response";
    private Map<String, HashMap<String, String>> map = new HashMap<>();
    private DetailLotteryInfo detailLotteryInfo;
    private ProgressDialog progressDialog;
    private View view;
    private TextView playCurrentNum,status,winMoney,playTypeName,singleMoney,
            noteNum,noteMoney,rebatePro,rebateMoney,noteTime,palyNum,withdraw_or_stop,winNum,playOrderId;
    private ImageView imageView;
    private LinearLayout mLinearlayout, record_content_ll2;
    private DisplayMetrics displayMetrics;
    private int playid,afternoid;
    private int mOrderId = 0;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        HashMap<String, String> afterRecord = new HashMap<String, String>();
        afterRecord.put("header_label1", "追号期数");
        afterRecord.put("header_label2", "投注金额");
        map.put("追号详情", afterRecord);
        HashMap<String, String> lotteryRecord = new HashMap<String, String>();
        lotteryRecord.put("header_label1", "盈亏");
        lotteryRecord.put("header_label2", "开奖号码");
        map.put("投注详情", lotteryRecord);
        Bundle bundle = getArguments();
        if (bundle != null)
        {
            mArgument = bundle.getString(ARGUMENT);
        }
        if(mArgument.compareTo("投注详情")==0) {
            playid = getActivity().getIntent().getExtras().getInt(PLAYID);
            getDetailLotteryInfoByID(playid);
        }else if(mArgument.compareTo("追号详情") == 0){
            afternoid = getActivity().getIntent().getExtras().getInt(AFTERNOID);
            getAfterDetailInfoByID(afternoid);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(mArgument != null){
            customViewByType(mArgument);
        }
    }

    public static RecordContentFragment newInstance(String argument)
    {
        Bundle bundle = new Bundle();
        bundle.putString(ARGUMENT, argument);
        RecordContentFragment contentFragment = new RecordContentFragment();
        contentFragment.setArguments(bundle);
        return contentFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_record_detail, container, false);
        view.setVisibility(View.INVISIBLE);
        mLinearlayout = (LinearLayout) view.findViewById(R.id.record_content_goback);
        mLinearlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        return view;
    }

    public void customViewByType(String mArgument){

        TextView label1 = (TextView)getView().findViewById(R.id.record_header_label1);
        label1.setText(map.get(mArgument).get("header_label1"));
        TextView label2 = (TextView)getView().findViewById(R.id.record_header_label2);
        label2.setText(map.get(mArgument).get("header_label2"));
    }

    private void getDetailLotteryInfoByID(final int playid){
        showProgressDialog("正在加载投注详情");
        mIsError = false;
        MyAsyncTask<DetailLotteryInfo> task = new MyAsyncTask<DetailLotteryInfo>(getActivity()) {

            @Override
            public DetailLotteryInfo callService() throws IOException,
                    JsonParseException, BizException, ServiceException {
                return new DetailLotteryService().getDetailLotteryInfoByOrder(playid);
            }

            @Override
            public void onLoaded(DetailLotteryInfo result) throws Exception {
                if(getActivity() == null || getActivity().isFinishing())
                    return;
                closeProgressDialog();
                if (!mIsError) {
                    initview();

                    if (result.getPalyCurrentNum() != null) {
                        playCurrentNum.setText("第" + result.getPalyCurrentNum() + "期");
                    }

                    if(result.getOrderID() != 0) {
                        mOrderId = result.getOrderID();
                        playOrderId.setText("注单号:" + result.getOrderID());
                        playOrderId.setVisibility(View.VISIBLE);
                    }

                    if (result.getOrderState() == 1) {
                        status.setText("已中奖");
                    }
                    else if (result.getOrderState() == 2) {
                        status.setText("未中奖");
                        status.setBackgroundResource(R.mipmap.icon_undrawn);
                    }
                    else if (result.getOrderState() == 3) {
                        status.setText("已撤单");
                    }
                    else if (result.getOrderState() == 0) {
                        status.setText("未开奖");
                        status.setBackgroundResource(R.mipmap.icon_pending);
                    }

                    winMoney.setText(String.valueOf(result.getWinMoney()));
                    TextPaint tp = winMoney.getPaint();
                    tp.setFakeBoldText(true);

                    if(result.getPalyNum() != null) {
                        palyNum.setText(result.getPalyNum());
                    }
                    if(result.getPlayTypeName()!= null) {
                        playTypeName.setText(result.getPlayTypeName());
                    }

                    singleMoney.setText(String.valueOf(result.getSingleMoney()));
                    noteNum.setText(String.valueOf(result.getNoteNum()));//int
                    noteMoney.setText(String.valueOf(result.getNoteMoney()));

                    if (result.getRebateProMoney() != null) {
                        winNum.setText(result.getRebateProMoney());
                    }

                    if (result.getNoteTime() != null) {
                        String dateStr = DateTransmit.dateTransmits(result.getNoteTime());
                        noteTime.setText(dateStr);
                    }

                    BigDecimal rebate = BigDecimal.valueOf(result.getRebatePro());
                    BigDecimal noteMoney = BigDecimal.valueOf(result.getNoteMoney());
                    rebatePro.setText(String.valueOf(rebate.multiply(noteMoney)));
                    rebateMoney.setText(String.valueOf(result.getWinNum()));

                    LinearLayout layout = (LinearLayout) getView().findViewById(R.id.record_header_ll1);
                    String currentLotteryNums = result.getCurrentLotteryNum();
                    if (currentLotteryNums == null || currentLotteryNums.length() == 0) {
                        TextView currentLotteryNum = new TextView(getContext());
                        currentLotteryNum.setText("还未开奖");
                        currentLotteryNum.setGravity(Gravity.CENTER_HORIZONTAL);
                        currentLotteryNum.setPadding(0, 10, 0, 0);
                        currentLotteryNum.setTextColor(getResources().getColor(R.color.white));
                        currentLotteryNum.setTextSize(18);
                        layout.addView(currentLotteryNum);
                    }
                    else if (currentLotteryNums.length() < 20 && currentLotteryNums.length() != 0) {
                        for (String s : currentLotteryNums.split(",")) {
                            TextView currentLotteryNum = new TextView(getContext());
                            currentLotteryNum.setBackgroundResource(R.mipmap.icon_winning_number_bg);
                            currentLotteryNum.setGravity(Gravity.CENTER_HORIZONTAL);
                            currentLotteryNum.setPadding(0, 6, 3, 0);
                            currentLotteryNum.setTextSize(18);
                            TextPaint paint = currentLotteryNum.getPaint();
                            paint.setFakeBoldText(true);
                            currentLotteryNum.setTextColor(getResources().getColor(R.color.white));
                            currentLotteryNum.setText(s);
                            layout.addView(currentLotteryNum);
                        }
                    }
                    else if (currentLotteryNums.length() >= 20) {
                        for(String numsStr : currentLotteryNums.split(",")) {
                            Pattern pattern = Pattern.compile("[0-9]*");
                            if (!pattern.matcher(numsStr).matches()) {
                                return;
                            }

                            TextView empty = new TextView(getContext());
                            empty.setWidth(5);
                            empty.setVisibility(View.INVISIBLE);

                            ImageView currentLotteryNum = new ImageView(getContext());
                            currentLotteryNum.setImageResource(LotteryUtil.getPK10NumberImage(Integer.parseInt(numsStr)));
                            currentLotteryNum.setAdjustViewBounds(true);
                            if (displayMetrics.heightPixels == 1920 || displayMetrics.widthPixels == 1280) {
                                currentLotteryNum.setMaxWidth(165);
                                currentLotteryNum.setMaxHeight(83);
                            }
                            else {
                                currentLotteryNum.setMaxWidth(105);
                                currentLotteryNum.setMaxHeight(53);
                            }

                            layout.addView(currentLotteryNum);
                            layout.addView(empty);
                        }
                    }

                    view.setVisibility(View.VISIBLE);
                    LotteryUtil lotteryUtil = new LotteryUtil(getActivity());
                    imageView.setImageResource(lotteryUtil.getLotteryIcon(result.getLotteryType()));
                }
                else {
                    BaseApp.changeUrl(getActivity(), new BaseApp.OnChangeUrlListener() {
                        @Override
                        public void changeSuccess() {
                            getDetailLotteryInfoByID(playid);
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

    private void getAfterDetailInfoByID(final int afternoid) {
        showProgressDialog("正在加载追号详情");
        mIsError = false;
        MyAsyncTask<AfterDetailLotteryInfo> task = new MyAsyncTask<AfterDetailLotteryInfo>(getActivity()) {

            @Override
            public AfterDetailLotteryInfo callService() throws IOException,
                    JsonParseException, BizException, ServiceException {
                return new DetailLotteryService().getAfterLotteryInfoByOrder(afternoid);
            }

            @Override
            public void onLoaded(AfterDetailLotteryInfo result) throws Exception {
                if(getActivity() == null || getActivity().isFinishing())
                    return;
                closeProgressDialog();
                if (!mIsError) {
                    TextView afterNum,orderAmount,stopCondition,totalWin,winLoss,stopbtn,miss1,miss2,miss3,matip,equal;
                    afterNum = (TextView) getActivity().findViewById(R.id.record_header_label1);
                    afterNum.setText("追号期数");
                    orderAmount = (TextView) getActivity().findViewById(R.id.record_header_label2);
                    orderAmount.setText("投注金额");
                    stopCondition = (TextView) getActivity().findViewById(R.id.record_content_label3);
                    stopCondition.setText("追号条件");
                    totalWin = (TextView) getActivity().findViewById(R.id.record_content_label4);
                    totalWin.setText("中奖金额");
                    winLoss = (TextView) getActivity().findViewById(R.id.record_content_label5);
                    winLoss.setText("盈亏金额");
                    stopbtn = (TextView) getActivity().findViewById(R.id.withdraw_or_stop);
                    stopbtn.setText("中止");
                    miss1 = (TextView) getActivity().findViewById(R.id.maybe_missing1);
                    miss1.setVisibility(View.GONE);
                    miss2 = (TextView) getActivity().findViewById(R.id.maybe_missing2);
                    miss2.setVisibility(View.GONE);
                    miss3 = (TextView) getActivity().findViewById(R.id.maybe_missing3);
                    miss3.setVisibility(View.GONE);
                    matip = (TextView) getActivity().findViewById(R.id.matip);
                    matip.setVisibility(View.GONE);
                    equal = (TextView) getActivity().findViewById(R.id.equal);
                    equal.setVisibility(View.GONE);
                    initview();
                    if(result.getPalyCurrentNum()!=null)
                        playCurrentNum.setText("第" + result.getPalyCurrentNum() + "期");
                    if(result.getAfterState()==0){
                        status.setText("进行中");
                        status.setBackgroundResource(R.mipmap.icon_afterchasing);
                    }else if(result.getAfterState()==1){
                        status.setText("已结束");
                        status.setBackgroundResource(R.mipmap.icon_afterfinish);
                    }else if(result.getAfterState()==2){
                        status.setText("已中止");
                        status.setBackgroundResource(R.mipmap.icon_drawn);
                    }
                    int i = result.getTotalPeriods()-result.getRestPeriods();
                    winMoney.setText(i+""+"/"+result.getTotalPeriods()+""+" "+"剩余"+result.getRestPeriods()+""+"期");
                    if(result.getPalyNum()!=null)
                        palyNum.setText(result.getPalyNum());
                    if(result.getPlayTypeName()!=null)
                        playTypeName.setText(result.getPlayTypeName());
                    if(result.getStopCondition() != null)
                        singleMoney.setText(result.getStopCondition());
                    singleMoney.setTextSize(15);
                    noteNum.setVisibility(View.GONE);
                    noteMoney.setVisibility(View.GONE);
                    rebateMoney.setText(String.valueOf(result.getTotalWin()));
                    rebatePro.setText(String.valueOf(result.getWinLoss()));
                    if(result.getOrderTime()!=null){
                        String dateStr = DateTransmit.dateTransmits(result.getOrderTime());
                        noteTime.setText(dateStr);}
                    record_content_ll2.setVisibility(View.INVISIBLE);
                    LinearLayout layout = (LinearLayout) getView().findViewById(R.id.record_header_ll1);
                    TextView orderMoney = new TextView(getContext());
                    orderMoney.setGravity(Gravity.CENTER_HORIZONTAL);
                    orderMoney.setPadding(0, 10, 0, 0);
                    orderMoney.setTextSize(18);
                    orderMoney.setTextColor(getResources().getColor(R.color.white));
                    String s = String.valueOf(result.getOrderMoney());
                    orderMoney.setText(s);
                    layout.addView(orderMoney);
                    LotteryUtil lotteryUtil = new LotteryUtil(getActivity());
                    imageView.setImageResource(lotteryUtil.getLotteryIcon(result.getLotteryType()));
                    view.setVisibility(View.VISIBLE);
                }
                else {
                    BaseApp.changeUrl(getActivity(), new BaseApp.OnChangeUrlListener() {
                        @Override
                        public void changeSuccess() {
                            getAfterDetailInfoByID(afternoid);
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

    private void withdrawLottery(final int playid, final int orderId){
        showProgressDialog("正在提交撤单申请!");
        mIsError = false;
        MyAsyncTask<com.hec.app.entity.Response<DetailLotteryInfo>> task = new MyAsyncTask<com.hec.app.entity.Response<DetailLotteryInfo>>(getActivity()) {

            @Override
            public com.hec.app.entity.Response<DetailLotteryInfo> callService() throws IOException,
                    JsonParseException, BizException, ServiceException {
                return new DetailLotteryService().withdrawLottery(playid);
            }

            @Override
            public void onLoaded(com.hec.app.entity.Response<DetailLotteryInfo> result) throws Exception {
                if(getActivity() == null || getActivity().isFinishing())
                    return;
                closeProgressDialog();
                if (!mIsError) {
                    String message = "";
                    if (orderId != 0) {
                        message = "\n注单号:" + orderId;
                    }

                    if (result.getSuccess()) {
                        MyToast.show(getContext(), "撤单成功!" + message);
                    }
                    else if (!result.getSuccess()) {
                        MyToast.show(getContext(), result.getMessage() + message);
                    }
                }
                else {
                    BaseApp.changeUrl(getActivity(), new BaseApp.OnChangeUrlListener() {
                        @Override
                        public void changeSuccess() {
                            withdrawLottery(playid, orderId);
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
    private void stopLottery(final int afternoid){
        showProgressDialog("正在提交中止申请!");
        mIsError = false;
        MyAsyncTask<com.hec.app.entity.Response<AfterDetailLotteryInfo>> task = new MyAsyncTask<com.hec.app.entity.Response<AfterDetailLotteryInfo>>(getActivity()) {

            @Override
            public com.hec.app.entity.Response<AfterDetailLotteryInfo> callService() throws IOException,
                    JsonParseException, BizException, ServiceException {
                return new DetailLotteryService().stopLottery(afternoid);
            }

            @Override
            public void onLoaded(com.hec.app.entity.Response<AfterDetailLotteryInfo> result) throws Exception {
                if(getActivity() == null || getActivity().isFinishing())
                    return;
                closeProgressDialog();
                if (!mIsError) {
                    if(result.getSuccess()){
                        MyToast.show(getContext(),"中止成功!");
                    }else if(!result.getSuccess()){
                        MyToast.show(getContext(),result.getMessage());
                    }
                }
                else {
                    BaseApp.changeUrl(getActivity(), new BaseApp.OnChangeUrlListener() {
                        @Override
                        public void changeSuccess() {
                            stopLottery(afternoid);
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

    private void initview(){
        imageView = (ImageView) getActivity().findViewById(R.id.record_header_lottery_image);
        playCurrentNum = (TextView) getActivity().findViewById(R.id.record_header_lottery_number);
        playOrderId = (TextView) getActivity().findViewById(R.id.record_header_order_id);
        status = (TextView) getActivity().findViewById(R.id.record_header_lottery_status);
        winMoney = (TextView) getActivity().findViewById(R.id.record_header_value1);
        palyNum = (TextView) getActivity().findViewById(R.id.record_content_value1);
        playTypeName = (TextView) getActivity().findViewById(R.id.record_content_value2);
        TextPaint textPaint = playTypeName.getPaint();
        textPaint.setFakeBoldText(true);
        singleMoney = (TextView) getActivity().findViewById(R.id.record_content_value31);
        noteNum = (TextView) getActivity().findViewById(R.id.record_content_value32);
        noteMoney = (TextView) getActivity().findViewById(R.id.record_content_value33);
        rebateMoney = (TextView) getActivity().findViewById(R.id.record_content_value4);
        rebatePro = (TextView) getActivity().findViewById(R.id.record_content_value5);
        noteTime = (TextView) getActivity().findViewById(R.id.record_content_value6);
        winNum = (TextView) getActivity().findViewById(R.id.record_content_value7);
        withdraw_or_stop = (TextView) getActivity().findViewById(R.id.withdraw_or_stop);
        record_content_ll2 = (LinearLayout)getActivity().findViewById(R.id.record_content_ll2);
        withdraw_or_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mArgument.compareTo("投注详情")==0) {
                    String message = "";
                    if (mOrderId != 0) {
                        message = "注单号:" + mOrderId;
                    }

                    new AlertDialog.Builder(getContext()).setCancelable(false).setTitle("确定进行操作吗?").setMessage(message).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            withdrawLottery(playid, mOrderId);
                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
                }
                else if (mArgument.compareTo("追号详情") == 0) {
                    new AlertDialog.Builder(getContext()).setCancelable(false).setTitle("确定进行操作吗?").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            stopLottery(afternoid);
                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
                }
            }
        });
    }

    private void showProgressDialog(String loadingMessage){
        try {
            progressDialog = DialogUtil.getProgressDialog(getContext(), loadingMessage);
            progressDialog.show();
            //TODO:这里可以防止用户点击对话框以外的屏幕导致对话框消失,但是这样也会导致如果不能及时加载数据,界面卡死的情况.
            //progressDialog.setCancelable(false);
        } catch (Exception e) {

        }
    }

    private void closeProgressDialog() {
        try {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        } catch (Exception e) {

        }
    }
}

