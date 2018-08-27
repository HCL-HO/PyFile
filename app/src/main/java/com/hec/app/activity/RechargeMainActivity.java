package com.hec.app.activity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.hec.app.R;
import com.hec.app.activity.base.BaseActivity;
import com.hec.app.adapter.RechargeAdapter;
import com.hec.app.config.CommonConfig;
import com.hec.app.entity.BizException;
import com.hec.app.entity.CustomerInfo;
import com.hec.app.entity.Result;
import com.hec.app.util.CustomerAccountManager;
import com.hec.app.util.MyAsyncTask;
import com.hec.app.webservice.AccountService;
import com.hec.app.webservice.ServiceException;
//import com.umeng.analytics.MobclickAgent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class RechargeMainActivity extends BaseActivity {

    private ListView mListView;
    private CustomerInfo.BankShow bankShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge_main);

        ImageView imgBack = (ImageView)findViewById(R.id.btn_previous);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RechargeMainActivity.this.finish();
            }
        });

        mListView = (ListView)findViewById(R.id.recharge_choice);
        mListView.setOnItemClickListener(listview_listener);
        CustomerInfo customer = CustomerAccountManager.getInstance().getCustomer();
        if (customer != null) {
            bankShow = customer.getBankShow();
            if (bankShow == null) {
                getBankShow();
            } else {
                ShowRechargeChoiceList();
            }
        }


        TextView textView = (TextView) findViewById(R.id.open_tuto);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(RechargeMainActivity.this, RechargeQuickMindActivity.class);
                it.putExtra("scan", true);
                startActivity(it);
            }
        });
    }

    private void getBankShow() {
        MyAsyncTask<Result<String>> task = new MyAsyncTask<Result<String>>(this) {
            @Override
            public Result<String> callService() throws IOException, JsonParseException, BizException, ServiceException {
                return new AccountService().getBankShow();
            }

            @Override
            public void onLoaded(Result<String> paramT) throws Exception {
                if (paramT.isSuccess()) {
                    String jsonString = paramT.getData();
                    if (jsonString != null) {
                        bankShow = new Gson().fromJson(jsonString, CustomerInfo.BankShow.class);
                    }
                }
                ShowRechargeChoiceList();
            }
        };
        task.setOnError(new MyAsyncTask.OnError() {
            @Override
            public void handleError(Exception paramException) {
                ShowRechargeChoiceList();
            }
        });
        task.executeTask();
    }


    private ListView.OnItemClickListener listview_listener = new ListView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> adapterview, View view, int position , long l){
            Intent it = null;
            HashMap<String, Object> item = (HashMap<String, Object>) mListView.getAdapter().getItem(position);
            int type = (int) item.get("type");
            switch (type) {
                case CommonConfig.RECHARGE_QUICK:
                    it = new Intent(RechargeMainActivity.this, RechargeQuickActivity.class);
                    break;
                case CommonConfig.RECHARGE_JD_PAY:
                    it = new Intent(RechargeMainActivity.this,  RechargeJdPayActivity.class);
                    break;
                case CommonConfig.RECHARGE_ALIPAY:
                    it = new Intent(RechargeMainActivity.this,  AliNavigateActivity.class);
                    if (bankShow != null) {
                        it.putExtra("itemShowArray", bankShow.getAlipay());
                    }
                    break;
                case CommonConfig.RECHARGE_WECHAT:
                    it = new Intent(RechargeMainActivity.this,  RechargeWechatActivity.class);
                    if (bankShow != null) {
                        it.putExtra("itemShowArray", bankShow.getWechat());
                    }
                    break;
                case CommonConfig.RECHARGE_ONLINE_BANK:
                    it = new Intent(RechargeMainActivity.this, RechargeOnlinebankActivity.class);
                    it.putExtra("transfertype", "跨行转账");
                    break;
                case CommonConfig.RECHARGE_QQ:
                    it = new Intent(RechargeMainActivity.this,  RechargeQQActivity.class);
                    break;
                case CommonConfig.RECHARGE_ONE_TOUCH:
                    it = new Intent(RechargeMainActivity.this,  RechargeOneTouchActivity.class);
                    break;
            }

            if (it != null) {
                startActivity(it);
            }
        }
    };

    private void ShowRechargeChoiceList() {
        ArrayList<HashMap<String, Object>> Item = new ArrayList<HashMap<String, Object>>();
        AddItemInAdapter(Item, R.mipmap.icon_alipay, R.string.recharge_main_alipay, R.string.recharge_main_alipay_message, CommonConfig.RECHARGE_ALIPAY);
        AddItemInAdapter(Item, R.mipmap.icon_onlinebank, R.string.recharge_main_onlinebank, R.string.recharge_main_onlinebank_message, CommonConfig.RECHARGE_ONLINE_BANK);
        AddItemInAdapter(Item, R.mipmap.icon_wechat, R.string.recharge_main_wechat, R.string.recharge_main_wechat_message, CommonConfig.RECHARGE_WECHAT);
        AddItemInAdapter(Item, R.mipmap.icon_recharge_jd_pay, R.string.recharge_main_jd_pay, R.string.recharge_main_jd_pay_message, CommonConfig.RECHARGE_JD_PAY);
        AddItemInAdapter(Item, R.mipmap.icon_recharge_qq, R.string.recharge_main_qq, R.string.recharge_main_qq_message, CommonConfig.RECHARGE_QQ);
        AddItemInAdapter(Item, R.mipmap.icon_recharge_quick_logo, R.string.recharge_main_quick, R.string.recharge_main_quick_message, CommonConfig.RECHARGE_QUICK);
        AddItemInAdapter(Item, R.mipmap.icon_one_touch, R.string.recharge_main_one_touch, R.string.recharge_main_one_touch_message, CommonConfig.RECHARGE_ONE_TOUCH);


        RechargeAdapter mAdapter = new RechargeAdapter(this, Item, R.layout.list_item_recharge_main,
                new String[] {"ItemImage", "ItemName", "ItemInfo", "img_arrow"},
                new int[] {R.id.ItemImage, R.id.ItemName, R.id.ItemInfo, R.id.img_arrow} );
        mListView.setAdapter(mAdapter);
        setListViewHeightBasedOnChildren(mListView);
    }

    private void AddItemInAdapter(ArrayList<HashMap<String, Object>> Item, int icon_id, int name, int info, int type) {
        if (bankShow != null && !bankShow.isShow(type)) {
            return;
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("ItemImage", icon_id);
        map.put("ItemName", name);
        map.put("ItemInfo", info);
        map.put("img_arrow", R.mipmap.icon_front);
        map.put("type", type);
        Item.add(map);
    }

    private void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }
}
