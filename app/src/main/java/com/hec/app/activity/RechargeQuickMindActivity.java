package com.hec.app.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hec.app.R;
import com.hec.app.util.StringUtil;

import org.w3c.dom.Text;

public class RechargeQuickMindActivity extends AppCompatActivity {
    private ImageView btn_previous, img_main;
    private LinearLayout keep_recharge_btn, recharge_record_btn;
    private String url;
    private TextView reminder, payStatus;
    private boolean scan;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge_quick_mind);
        scan = getIntent().getBooleanExtra("scan", false);
        Log.i("QuickMind", "Hi Quickmind");
        if (scan) {
            findViewById(R.id.scan_mind).setVisibility(View.VISIBLE);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            btn_previous = (ImageView) findViewById(R.id.btn_previous);
            setSupportActionBar(toolbar);
            btn_previous.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
            LinearLayout returnToPrevious = (LinearLayout) findViewById(R.id.botton_btn_scan);
            returnToPrevious.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });

            TextView iPhoneTv = (TextView) findViewById(R.id.iphone_tv);
            TextView androidTv = (TextView) findViewById(R.id.android_tv);
            String str;
            String colorGray = "#9e9e9e";
            String colorBlack = "#202020";
            String powerAndHome = StringUtil.joinHtmlColor(getResources().getString(R.string.on_off_button), colorBlack)
                                + StringUtil.joinHtmlColor("键+", colorGray)
                                + StringUtil.joinHtmlColor(getResources().getString(R.string.home_buttom), colorBlack)
                                + StringUtil.joinHtmlColor("键", colorGray);
            str = StringUtil.joinHtmlColor("同時按", colorGray) + powerAndHome;
            iPhoneTv.setText(Html.fromHtml(str));
            str = StringUtil.joinHtmlColor("新款", colorGray) + powerAndHome + "<br>";
            str += StringUtil.joinHtmlColor("老款", colorGray)
                    + StringUtil.joinHtmlColor(getResources().getString(R.string.power_or_home), colorBlack)
                    + StringUtil.joinHtmlColor("键+", colorGray)
                    + StringUtil.joinHtmlColor(getResources().getString(R.string.volume_minus), colorBlack)
                    + StringUtil.joinHtmlColor("键", colorGray);
            androidTv.setText(Html.fromHtml(str));

            TextView tuto21 = (TextView)findViewById(R.id.tuto_2_1_tv);
            TextView tuto22 = (TextView)findViewById(R.id.tuto_2_2_tv);
            str = StringUtil.joinHtmlColor("点击右上角的", colorGray)
                    + StringUtil.joinHtmlColor("【更多】", colorBlack)
                    + StringUtil.joinHtmlColor("小圆点, 然後选择", colorGray)
                    + StringUtil.joinHtmlColor("从相册中选取二维码", colorBlack);
            tuto21.setText(Html.fromHtml(str));

            str = StringUtil.joinHtmlColor("选择之前保存的", colorGray)
                    + StringUtil.joinHtmlColor("二维码图片", colorBlack);

            tuto22.setText(Html.fromHtml(str));
        } else {
            findViewById(R.id.quick_mind).setVisibility(View.VISIBLE);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            btn_previous = (ImageView) findViewById(R.id.btn_previous);
            img_main = (ImageView) findViewById(R.id.img_main);
            keep_recharge_btn = (LinearLayout) findViewById(R.id.keep_recharge_btn);
            recharge_record_btn = (LinearLayout) findViewById(R.id.botton_btn);
            payStatus = (TextView) findViewById(R.id.pay_status);
            reminder = (TextView) findViewById(R.id.notice);

            setSupportActionBar(toolbar);
            url = "";
            Bundle xmlData = getIntent().getExtras();
            if(xmlData!=null){
                String response = xmlData.getString("xml");
                Log.i("Quickmind", response);
                try{
                    String status = "<trade_status>";
                    int start = response.indexOf(status);
                    int end = response.indexOf("</trade_status>");
                    String str = response.substring(start + status.length(), end);
                    if("SUCCESS".equals(str)){
                        payStatus.setText("支付结果：支付成功");
                        img_main.setImageResource(R.mipmap.img_success);
                    }else if("UNPAY".equals(str)){
                        payStatus.setText("支付结果：未支付");
                        reminder.setVisibility(View.GONE);
                    }else{
                        payStatus.setText("支付结果：支付失败");
                        reminder.setVisibility(View.GONE);
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
            btn_previous.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });

            recharge_record_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent(RechargeQuickMindActivity.this, RechargeQuickActivity.class);
                    it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(it);
                    finish();
                }
            });
            reminder.setText(Html.fromHtml(
                    "充值完成后请耐心等待几分钟<br>或者在<font color='#08A09D'>充值记录</font>查看到帐号情況"));
        }
    }

}
