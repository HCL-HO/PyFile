package com.hec.app.activity;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
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
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonParseException;
import com.hec.app.R;
import com.hec.app.activity.base.BaseApp;
import com.hec.app.entity.BizException;
import com.hec.app.entity.RebateUtil;
import com.hec.app.framework.widget.MyToast;
import com.hec.app.util.DialogUtil;
import com.hec.app.util.IntentUtil;
import com.hec.app.util.MyAsyncTask;
import com.hec.app.webservice.AgentService;
import com.hec.app.webservice.ServiceException;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Callable;

public class ProxyLinkActivity extends AppCompatActivity {

    private ArrayList<String> returnValues;
    private int count = 0;
    private JSONArray quotas;
    private final int ON_ERROR = 3;

//    TextView tvQuotas;
    Spinner spinner;
    TextView tvLink;

    AgentService agentService;
    ArrayAdapter adapter;
    RebateUtil rebates;
    ProgressDialog mProgressDialog;
    MyAsyncTask.OnError onError;
    boolean mIsError = false, showing = false;
    String prevMessage = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proxy_link);
        spinner = (Spinner) findViewById(R.id.return_spinner);
        tvLink = (TextView) findViewById(R.id.link_proxy);

        returnValues = new ArrayList<>();
        quotas = new JSONArray();
        rebates = new RebateUtil();
        agentService = new AgentService();

        onError = new MyAsyncTask.OnError() {

            @Override
            public void handleError(Exception e) {
                closeProgressDialog();
                mIsError = true;
            }
        };

        adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, returnValues);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView adapterView, View view, int position, long id) {
                String selection = rebates.fromPercentage(returnValues.get(position));
//                tvQuotas.setText(getResources().getString(R.string.quotas) + rebates.getQuota(selection));
            }

            public void onNothingSelected(AdapterView arg0) {

            }
        });

        getRebateList();

//        tvQuotas = (TextView) findViewById(R.id.proxy_link_quotas);
//        tvQuotas.setText(getResources().getString(R.string.quotas));


        LinearLayout genLinkBtn = (LinearLayout) findViewById(R.id.gen_link);
        genLinkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = (String) spinner.getSelectedItem();
                tvLink.setText("");
                genLink(rebates.fromPercentage(str));
            }
        });

        LinearLayout copyLink = (LinearLayout) findViewById(R.id.copy_link);
        copyLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("link", tvLink.getText().toString());
                clipboard.setPrimaryClip(clip);
                MyToast.show(getBaseContext(), getResources().getString(R.string.copied_to_clipboard));
            //    Toast.makeText(getBaseContext(), ((TextView) findViewById(R.id.link_proxy)).toString(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void genLink(final String rebate) {
        showProgressDialog();
        mIsError = false;
        MyAsyncTask<String> task = new MyAsyncTask<String>(this) {

            @Override
            public String callService() throws IOException,
                    JsonParseException, BizException, ServiceException {
                return agentService.getReferralLink(rebate);
            }

            @Override
            public void onLoaded(String result) throws Exception {
                if(ProxyLinkActivity.this == null || ProxyLinkActivity.this.isFinishing())
                    return;
                if (!mIsError) {
                    Message msg = Message.obtain();
                    Bundle b = new Bundle();
                    msg.what = 1;
                    b.putString("url", result);
                    msg.setData(b);
                    mHandler.sendMessage(msg);
                }
                else {
                    BaseApp.changeUrl(ProxyLinkActivity.this, new BaseApp.OnChangeUrlListener() {
                        @Override
                        public void changeSuccess() {
                            genLink(rebate);
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
    private void getRebateList(){
        showProgressDialog();
        mIsError = false;
        MyAsyncTask<String> task2 = new MyAsyncTask<String>(this) {

            @Override
            public String callService() throws IOException,
                    JsonParseException, BizException, ServiceException {
                return agentService.getAvailableRebateList();
            }

            @Override
            public void onLoaded(String result) throws Exception {
                if(ProxyLinkActivity.this == null || ProxyLinkActivity.this.isFinishing())
                    return;
                if (!mIsError) {
                    JSONArray json = new JSONArray(result);
                    returnValues.clear();
                    for (int i = 0; i < json.length(); i++) {
                        if (Float.parseFloat(json.getString(i)) * 100 <= 7) {
                            returnValues.add(rebates.toPercentage(json.getString(i)));
                        }
                    }
                    adapter.notifyDataSetChanged();
                    mHandler.sendEmptyMessage(0);
                }
                else {
                    BaseApp.changeUrl(ProxyLinkActivity.this, new BaseApp.OnChangeUrlListener() {
                        @Override
                        public void changeSuccess() {
                            getRebateList();
                        }

                        @Override
                        public void changeFail() {}
                    });
                }
            }
        };
        task2.setOnError(onError);
        task2.executeTask();
        getRebateQuotas();
    }

    private void getRebateQuotas() {
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
                if(ProxyLinkActivity.this == null || ProxyLinkActivity.this.isFinishing())
                    return;
                closeProgressDialog();
                if (!mIsError) {
                    quotas = result.getQuotas();
                    rebates = result;
                    mHandler.sendEmptyMessage(0);
                }
                else {
                    BaseApp.changeUrl(ProxyLinkActivity.this, new BaseApp.OnChangeUrlListener() {
                        @Override
                        public void changeSuccess() {
                            getRebateQuotas();
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

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage (Message msg) {
            super.handleMessage(msg);
            //Update UI
            if (msg.what == 0) {
                    count++;
                if (count >= 2) {
                    closeProgressDialog();
                    String selection = rebates.fromPercentage((String) spinner.getSelectedItem());
//                    tvQuotas.setText(getResources().getString(R.string.quotas) + rebates.getQuota(selection));
                }
            }
            if (msg.what == 1) {
                closeProgressDialog();
                tvLink.setText(msg.getData().getString("url"));
            }
            if (msg.what == ON_ERROR) {
                String msgText = msg.getData().getString("Error");
                if (!showing || !prevMessage.equals(msgText)) {
                    prevMessage = msgText;
                    showing = true;
                    MyToast.show(getBaseContext(), msgText);
                }
            }
        }
    };

    private int getQuota() {
        //TODO get quotas
        return 20;
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

    public void backClick (View v){
        finish();
    }

}
