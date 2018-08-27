package com.hec.app.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonParseException;
import com.hec.app.R;
import com.hec.app.activity.base.BaseApp;
import com.hec.app.entity.AgentListInfo;
import com.hec.app.entity.BizException;
import com.hec.app.entity.MemberInfo;
import com.hec.app.entity.RebateUtil;
import com.hec.app.entity.ServiceRequestResult;
import com.hec.app.framework.widget.MyToast;
import com.hec.app.util.DialogUtil;
import com.hec.app.util.IntentUtil;
import com.hec.app.util.MyAsyncTask;
import com.hec.app.webservice.AgentService;
import com.hec.app.webservice.ServiceException;

import org.json.JSONArray;

import java.io.IOException;
import java.lang.reflect.Member;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;

public class DownloadTransferActivity extends AppCompatActivity {

    private ArrayList<String> returnValues, tempReturn;
    private final String[] fields = {"Name", "ID", "Amount", "Change", "Profit"};
    private JSONArray quotas;
    private HashMap<String, String> data;
    private int count = 0, saveCount = 0;
    private final int ON_ERROR = 3;

    TextView name, remainder, profit, change, rebateQuota;
    Spinner spinner;
    Switch transferSwitch;
    LinearLayout transferButton;
    LinearLayout saveButton;

    ArrayAdapter adapter;
    AgentService agentService;
    MemberInfo memberInfo;
    RebateUtil rebates = new RebateUtil();
    ProgressDialog mProgressDialog;
    MyAsyncTask.OnError onError;
    boolean mIsError = false, showing = false;
    String prevMessage = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_transfer);


        spinner = (Spinner) findViewById(R.id.return_spinner);
        name = (TextView) findViewById(R.id.download_transfer_user_name);
        remainder = (TextView) findViewById(R.id.download_transfer_remainder);
        profit = (TextView) findViewById(R.id.download_transfer_profit);
        change = (TextView) findViewById(R.id.download_transfer_change);
        transferSwitch = (Switch) findViewById(R.id.activate_transfer_switch);
        transferButton = (LinearLayout) findViewById(R.id.transfer_btn);
        rebateQuota = (TextView) findViewById(R.id.download_transfer_quota);
        saveButton = (LinearLayout) findViewById(R.id.confirm_save);

        agentService = new AgentService();
        returnValues = new ArrayList<>();
        tempReturn = new ArrayList<>();
        memberInfo = new MemberInfo();
        quotas = new JSONArray();

        onError = new MyAsyncTask.OnError() {

            @Override
            public void handleError(Exception e) {
                closeProgressDialog();
                mIsError = true;
            }
        };

        data = (HashMap<String, String>) getIntent().getSerializableExtra("data");

        getResultData();

        adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, returnValues);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView adapterView, View view, int position, long id) {
                String selection = rebates.fromPercentage(returnValues.get(position));
                rebateQuota.setText(getResources().getString(R.string.quotas) + rebates.getQuota(selection));
                //            updateRebate(data.get(fields[1]), selection);
            }

            public void onNothingSelected(AdapterView arg0) {

            }
        });


    /*    transferSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });*/

        transferButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (memberInfo.getIslowMoneyIn()) {
                    startOfflineTransfer();
                }
                else
                    MyToast.show(getBaseContext(), getResources().getString(R.string.not_activated));
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChanges();
            }
        });


        rebateQuota.setText(getResources().getString(R.string.quotas));
        name.setText(data.get(fields[0]));
        remainder.setText(data.get(fields[2]));
        profit.setText(data.get(fields[4]));
        change.setText(data.get(fields[3]));
        try {
            Double profit = Double.parseDouble(data.get(fields[4]));
            if (profit < 0)
                findViewById(R.id.icon_increase).setVisibility(View.GONE);
            if (profit > 0)
                findViewById(R.id.icon_decrease).setVisibility(View.GONE);
        } catch (Exception e) {

        }
    }

    private String getQuota(String rebate) {
        //TODO get quotas

    /*    for (int i = 0; i < quotas.length(); i++) {

        }*/

        return getResources().getString(R.string.no_limit);
    }


    private void toggleTransfer (final String ID, final boolean enable) {
        mIsError = false;
        showing = false;
        MyAsyncTask<ServiceRequestResult> task = new MyAsyncTask<ServiceRequestResult>(this) {

            @Override
            public ServiceRequestResult callService() throws IOException,
                    JsonParseException, BizException, ServiceException {
                return agentService.toggleTransfer(ID, enable);
            }

            @Override
            public void onLoaded(ServiceRequestResult result) throws Exception {
                if(DownloadTransferActivity.this == null || DownloadTransferActivity.this.isFinishing())
                    return;
                if (!mIsError) {
                    if (result.isSuccess()) {
                        memberInfo.setIslowMoneyIn(enable);
                        mHandler.sendEmptyMessage(1);
                        if(!enable){
                            transferButton.setBackgroundResource(R.color.gray);
                        }else{
                            transferButton.setBackgroundResource(R.drawable.button_gen_link);
                        }
                    } else {
                        mHandler.sendEmptyMessage(2);
                    }
                }
                else {
                    BaseApp.changeUrl(DownloadTransferActivity.this, new BaseApp.OnChangeUrlListener() {
                        @Override
                        public void changeSuccess() {
                            toggleTransfer(ID, enable);
                        }

                        @Override
                        public void changeFail() {}
                    });
                }
            }
        };
        task.setOnError(onError);
        task.executeTask();
    }

    private void updateRebate(final String ID, final String rebate){
        mIsError = false;
        showing = false;
        MyAsyncTask<ServiceRequestResult> task = new MyAsyncTask<ServiceRequestResult>(this) {

            @Override
            public ServiceRequestResult callService() throws IOException,
                    JsonParseException, BizException, ServiceException {
                return agentService.updateRebate(ID, rebate);
            }

            @Override
            public void onLoaded(ServiceRequestResult result) throws Exception {
                if(DownloadTransferActivity.this == null || DownloadTransferActivity.this.isFinishing())
                    return;
                if (!mIsError) {
                    if (result.isSuccess()) {
                        rebates.updateQuota(memberInfo.getRebatePro(), rebate);
                        memberInfo.setRebatePro(Float.parseFloat(rebate));
                        mHandler.sendEmptyMessage(1);
                    } else if (result.getMessage() != null){
                        MyToast.show(getBaseContext(), result.getMessage());
                    }
                }
                else {
                    BaseApp.changeUrl(DownloadTransferActivity.this, new BaseApp.OnChangeUrlListener() {
                        @Override
                        public void changeSuccess() {
                            updateRebate(ID, rebate);
                        }

                        @Override
                        public void changeFail() {}
                    });
                }
            }
        };
        task.setOnError(onError);
        task.executeTask();
    }

    private void getResultData(){
        count = 0;
        showProgressDialog();
        mIsError = false;
        showing = false;
        MyAsyncTask<RebateUtil> task = new MyAsyncTask<RebateUtil>(this) {

            @Override
            public RebateUtil callService() throws IOException,
                    JsonParseException, BizException, ServiceException {
                return agentService.getRebateQuotas();
            }

            @Override
            public void onLoaded(RebateUtil result) throws Exception {
                if(DownloadTransferActivity.this == null || DownloadTransferActivity.this.isFinishing())
                    return;
                if (!mIsError) {
                    quotas = result.getQuotas();
                    rebates = result;
                    mHandler.sendEmptyMessage(0);
                }
                else {
                    BaseApp.changeUrl(DownloadTransferActivity.this, new BaseApp.OnChangeUrlListener() {
                        @Override
                        public void changeSuccess() {
                            getResultData();
                        }

                        @Override
                        public void changeFail() {}
                    });
                }
            }
        };
        task.setOnError(onError);
        task.executeTask();

        MyAsyncTask<MemberInfo> task2 = new MyAsyncTask<MemberInfo>(this) {

            @Override
            public MemberInfo callService() throws IOException,
                    JsonParseException, BizException, ServiceException {
                return agentService.getMemberInfo(data.get(fields[1]));
            }

            @Override
            public void onLoaded(MemberInfo result) throws Exception {
                if(DownloadTransferActivity.this == null || DownloadTransferActivity.this.isFinishing())
                    return;
                if (!mIsError) {
                    memberInfo = result;
                    mHandler.sendEmptyMessage(0);
                }
            }
        };
        task2.setOnError(onError);
        task2.executeTask();

        MyAsyncTask<String> task3 = new MyAsyncTask<String>(this) {

            @Override
            public String callService() throws IOException,
                    JsonParseException, BizException, ServiceException {
                return agentService.getAvailableRebateList();
            }

            @Override
            public void onLoaded(String result) throws Exception {
                if(DownloadTransferActivity.this == null || DownloadTransferActivity.this.isFinishing())
                    return;
                if (!mIsError) {
                    JSONArray json = new JSONArray(result);
                    tempReturn.clear();
                    for (int i = 0; i < json.length(); i++) {
                        tempReturn.add(json.getString(i));
                    }
                    mHandler.sendEmptyMessage(0);
                }
            }
        };
        task3.setOnError(onError);
        task3.executeTask();
    }



    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage (Message msg) {
            super.handleMessage(msg);
            closeProgressDialog();
            //Update UI
            if (msg.what == 0) {
                count++;
                if (count >= 3) {
                    returnValues.clear();
                    for (String s : tempReturn) {
                        if (Float.parseFloat(s) >= Float.parseFloat(memberInfo.getRebatePro())) {
                            returnValues.add(rebates.toPercentage(s));
                        }
                    }
                    adapter.notifyDataSetChanged();
                    DecimalFormat df = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
                    df.setMaximumFractionDigits(340);
                    df.setMaximumIntegerDigits(340);
                    spinner.setSelection(indexOf(rebates.toPercentage(memberInfo.getRebatePro()), returnValues));
                    name.setText(memberInfo.getUserName());
                    profit.setText(Double.toString(memberInfo.getWinLoss()));
                    double money = memberInfo.getAvailableScores();
                    remainder.setText(df.format(money));
                    change.setText(Double.toString(memberInfo.getTurnover()));
                    transferSwitch.setChecked(memberInfo.getIslowMoneyIn());
                    if(!memberInfo.getIslowMoneyIn()){
                        transferButton.setBackgroundResource(R.color.gray);
                    }else{
                        transferButton.setBackgroundResource(R.drawable.button_gen_link);
                    }
                    count++;
                    showing = false;
                }
            } else if (msg.what == 1) {
                saveCount++;
                if (saveCount >= 2) {
                    if (memberInfo.getRebatePro() != null) {
                        rebateQuota.setText(getResources().getString(R.string.quotas) + rebates.getQuota(memberInfo.getRebatePro()));
                    }
                    else
                        rebateQuota.setText(getResources().getString(R.string.quotas));
                    MyToast.show(getBaseContext(), getResources().getString(R.string.change_success));
                    saveCount = 0;
                }
            } else if (msg.what == 2) {
                transferSwitch.setChecked(memberInfo.getIslowMoneyIn());
                if (memberInfo.getRebatePro() != null) {
                    rebateQuota.setText(getResources().getString(R.string.quotas) + rebates.getQuota(memberInfo.getRebatePro()));
                }
                else
                    rebateQuota.setText(getResources().getString(R.string.quotas));
                MyToast.show(getBaseContext(), getResources().getString(R.string.change_fail));
            } else if (msg.what == ON_ERROR) {
                String msgText = msg.getData().getString("Error");
                if (!showing || !prevMessage.equals(msgText)) {
                    prevMessage = msgText;
                    showing = true;
                    MyToast.show(getBaseContext(), msgText);
                }
            }
        }
    };

    int indexOf(String s, List<String> a) {
        for (int i = 0; i < a.size(); i++) {
            if (a.get(i).equals(s))
                return i;
        }
        return 0;
    }

    private void showProgressDialog() {
        try {
            mProgressDialog = DialogUtil.getProgressDialog(this, getResources().getString(R.string.msg_load_ing));
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
    private void startOfflineTransfer() {
        Intent it = new Intent(this, OfflineTransferActivity.class);
        it.putExtra("hasData", true);
        it.putExtra("name", data.get(fields[0]));
        startActivityForResult(it, 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            getResultData();
            setResult(RESULT_OK);
        }
    }

    private void saveChanges(){
//        if (count >= 4)
            saveCount = 0;
            if (spinner.getSelectedItem() != null) {
                showProgressDialog();
                String selection = rebates.fromPercentage((String) spinner.getSelectedItem());
                if (!selection.equals(memberInfo.getRebatePro()))
                    updateRebate(data.get(fields[1]), selection);
                else
                    mHandler.sendEmptyMessage(1);
                toggleTransfer(data.get(fields[1]), transferSwitch.isChecked());
            } else {
                showProgressDialog();
                mHandler.sendEmptyMessage(1);
                toggleTransfer(data.get(fields[1]), transferSwitch.isChecked());
            }
//        }
    }

    public void backClick (View v){
        finish();
    }
}
