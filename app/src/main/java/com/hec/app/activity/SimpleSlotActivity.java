package com.hec.app.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.hec.app.R;
import com.hec.app.activity.base.BaseApp;
import com.hec.app.entity.AppBean;
import com.hec.app.entity.CustomerInfo;
import com.hec.app.util.CustomerAccountManager;
import com.hec.app.util.IntentUtil;

import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.ZXingView;

public class SimpleSlotActivity extends AppCompatActivity {

    private ZXingView view;
    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_slot);
        view = (ZXingView) findViewById(R.id.zxingview);
        btn = (Button) findViewById(R.id.btn_start);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.startSpot();
            }
        });
        view.startCamera();
        view.setDelegate(new QRCodeView.Delegate() {
            @Override
            public void onScanQRCodeSuccess(String result) {
                Log.i("wxj",result);
                if(result!=null){
                    String[] infos = result.split("&");
                    Intent intent = new Intent(SimpleSlotActivity.this,SlotActivity.class);
                    intent.putExtra("UserName",infos[0].split("=")[1]);
                    CustomerInfo customerInfo = new CustomerInfo();
                    customerInfo.setAuthenticationKey(infos[1].split("=")[1]);
                    CustomerAccountManager.getInstance().setCustomer(customerInfo);
                    intent.putExtra("AASlotUrl",infos[2].split("=")[1]);
                    intent.putExtra("Balance",1000);
                    AppBean appBean = new AppBean();
                    appBean.setIosAppUrl("ios");
                    BaseApp.setAppBean(appBean);
                    intent.putExtra("cdnurl",infos[3].split("=")[1]);
                    startActivity(intent);
                }
                view.stopCamera();
            }

            @Override
            public void onScanQRCodeOpenCameraError() {
                view.stopCamera();
            }
        });
    }
}
