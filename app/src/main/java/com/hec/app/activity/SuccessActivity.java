package com.hec.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonParseException;
import com.hec.app.R;
import com.hec.app.activity.base.BaseActivity;
import com.hec.app.activity.base.BaseApp;
import com.hec.app.entity.BalanceInfo;
import com.hec.app.entity.BizException;
import com.hec.app.util.MyAsyncTask;
import com.hec.app.webservice.AccountService;
import com.hec.app.webservice.ServiceException;

import java.io.IOException;
import java.text.DecimalFormat;

public class SuccessActivity extends BaseActivity {

    private ImageView imgBack;
    private boolean mIsError;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.success_two_button);
        Intent intent = getIntent();
        int tag = intent.getIntExtra("tag", -1);
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 328, getResources().getDisplayMetrics());
        float px2 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics());
        float px3 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics());
        float px4 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics());
        LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams((int)px, (int)px2);
        layout.setMargins((int)px3,0,(int)px3,(int)px4);
        //change login pw success
        if(tag == 0){
            setContentView(R.layout.success_two_button);
            imgBack = (ImageView) findViewById(R.id.imgBack);
            imgBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });

            TextView title = (TextView) findViewById(R.id.title);
            title.setText(R.string.title_success_change);
            TextView success_tv1 = (TextView) findViewById(R.id.success_tv1); success_tv1.setText(R.string.success_change);
            TextView success_tv2 = (TextView) findViewById(R.id.success_tv2); success_tv2.setVisibility(View.INVISIBLE);
//            success_tv2.setText(getString(R.string.new_pw) + ": " + Html.fromHtml("<font color=#08A09D>"+ getIntent().getStringExtra("pw") +"</font>"));
            TextView success_tv3 = (TextView) findViewById(R.id.success_tv3); success_tv3.setVisibility(View.GONE);
            TextView confirm = (TextView) findViewById(R.id.confirm_change); confirm.setText(R.string.back_setting);
            LinearLayout left_btn = (LinearLayout) findViewById(R.id.botton_left_btn); left_btn.setVisibility(View.GONE);
            LinearLayout ll = (LinearLayout) findViewById(R.id.botton_btn);
            ll.setLayoutParams(layout);
            ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        //change fund pw success
        }else if (tag == 1){
            setContentView(R.layout.success_two_button);
            imgBack = (ImageView) findViewById(R.id.imgBack);
            imgBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
            TextView title = (TextView) findViewById(R.id.title);
            title.setText(R.string.title_success_change);
            TextView success_tv1 = (TextView) findViewById(R.id.success_tv1); success_tv1.setText(R.string.success_change);
            TextView success_tv2 = (TextView) findViewById(R.id.success_tv2); success_tv2.setText(getString(R.string.new_pw) + ": " + Html.fromHtml("<font color=#08A09D>"+ getIntent().getStringExtra("pw") +"</font>"));
            success_tv2.setVisibility(View.INVISIBLE);
            TextView success_tv3 = (TextView) findViewById(R.id.success_tv3); success_tv3.setVisibility(View.GONE);
            TextView confirm = (TextView) findViewById(R.id.confirm_change); confirm.setText(R.string.back_setting);
            LinearLayout left_btn = (LinearLayout) findViewById(R.id.botton_left_btn); left_btn.setVisibility(View.GONE);
            LinearLayout ll = (LinearLayout) findViewById(R.id.botton_btn);
            ll.setLayoutParams(layout);
            ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        //bind bank card success
        }else if (tag == 2){
            setContentView(R.layout.success_two_button);
            imgBack = (ImageView) findViewById(R.id.imgBack);
            imgBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
            TextView title = (TextView) findViewById(R.id.title);
            title.setText(R.string.bind_success);

            String name = intent.getStringExtra("name");
            String bank = intent.getStringExtra("bank");
            String card = intent.getStringExtra("card");
            int type = intent.getIntExtra("btn_type", -1);

            TextView success_tv1 = (TextView) findViewById(R.id.success_tv1); success_tv1.setText(R.string.success_bind);
            TextView success_tv2 = (TextView) findViewById(R.id.success_tv2); success_tv2.setText(Html.fromHtml(getString(R.string.ac_user_name) + " " + name + " <font color=#08A09D>[" + bank + "]</font>"));
            TextView success_tv3 = (TextView) findViewById(R.id.success_tv3); success_tv3.setText(getString(R.string.card_number) + card);
            TextView confirm = (TextView) findViewById(R.id.confirm_change);

            if (type == 0) {
                confirm.setText("返回银行卡管理");
            } else {
                confirm.setText(R.string.now_go_withdraw);
            }

            LinearLayout left_btn = (LinearLayout) findViewById(R.id.botton_left_btn); left_btn.setVisibility(View.GONE);

            LinearLayout ll = (LinearLayout) findViewById(R.id.botton_btn);
            ll.setLayoutParams(layout);
            ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        //withdraw success
        }else if (tag == 3){
            getBalance();

            Double amount = intent.getDoubleExtra("amount", 0);
            setContentView(R.layout.success_two_button);
            imgBack = (ImageView) findViewById(R.id.imgBack);
            imgBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
            TextView title = (TextView) findViewById(R.id.title);
            title.setText(R.string.withdraw_success);
            TextView success_tv1 = (TextView) findViewById(R.id.success_tv1); success_tv1.setText(R.string.success_withdraw);
            TextView success_tv2 = (TextView) findViewById(R.id.success_tv2); success_tv2.setText(Html.fromHtml(getString(R.string.this_time_withdraw) + " <font color=#08A09D>" + amount + "</font>" + "元"));
            TextView confirm = (TextView) findViewById(R.id.confirm_change); confirm.setText(R.string.back_to_lottering);
            TextView withdraw = (TextView) findViewById(R.id.withdraw); withdraw.setText(R.string.withdraw_money);

            LinearLayout leftBtn = (LinearLayout)findViewById(R.id.botton_left_btn);
            leftBtn.setVisibility(View.GONE);

            LinearLayout ll = (LinearLayout) findViewById(R.id.botton_btn);
            ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            LinearLayout ll1 = (LinearLayout) findViewById(R.id.botton_left_btn);
            ll1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

        } else if (tag == 4){

            setContentView(R.layout.success_one_button);
            imgBack = (ImageView) findViewById(R.id.imgBack);
            imgBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
            String pw = getIntent().getStringExtra("password");
            String name = getIntent().getStringExtra("name");
            TextView title = (TextView) findViewById(R.id.title);
            title.setText(R.string.title_activity_add_success);
            TextView success_tv1 = (TextView) findViewById(R.id.success_tv1); success_tv1.setText(R.string.congrats_add_success);
            TextView success_tv2 = (TextView) findViewById(R.id.success_tv2); success_tv2.setText(getResources().getString(R.string.name) + ":" + name);
            TextView success_tv3 = (TextView) findViewById(R.id.success_tv3); success_tv3.setText(getResources().getString(R.string.password) + ":" + pw);
            TextView confirm = (TextView) findViewById(R.id.confirm_change);  confirm.setText(R.string.back_to_proxy_list);

            LinearLayout ll = (LinearLayout) findViewById(R.id.botton_btn);
            ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

        } else if(tag == 5){
            setContentView(R.layout.success_two_button);
            imgBack = (ImageView) findViewById(R.id.imgBack);
            imgBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
            TextView title = (TextView) findViewById(R.id.title);
            title.setText(R.string.valid_email_success);
            TextView success_tv1 = (TextView) findViewById(R.id.success_tv1);
            success_tv1.setText(R.string.valid_email_success_long);
            TextView success_tv2 = (TextView) findViewById(R.id.success_tv2);
            if (getIntent().getStringExtra("validEmail").equals("0")){
                success_tv2.setVisibility(View.GONE);

            }else{
                success_tv2.setText(getString(R.string.find_password_item1) + ": " +Html.fromHtml("<font color=#08A09D>"+getIntent().getStringExtra("validEmail")+"</font>"));

            }

            TextView success_tv3 = (TextView) findViewById(R.id.success_tv3);
            success_tv3.setVisibility(View.GONE);

            TextView confirm = (TextView) findViewById(R.id.confirm_change);
            confirm.setText(R.string.send_success_btnBackLoginTextView);

            LinearLayout left_btn = (LinearLayout) findViewById(R.id.botton_left_btn);
            left_btn.setVisibility(View.GONE);

            LinearLayout ll = (LinearLayout) findViewById(R.id.botton_btn);
            ll.setLayoutParams(layout);
            ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent(SuccessActivity.this, LoginActivity.class);
                    it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(it);
//                    overridePendingTransition(R.anim.push_right_in_no_alpha,R.anim.push_right_out_no_alpha);
                }
            });
        }else if(tag == 6){
            String npd = intent.getStringExtra("newPassword");

            setContentView(R.layout.success_two_button);
            imgBack = (ImageView) findViewById(R.id.imgBack);
            imgBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });

            TextView title = (TextView) findViewById(R.id.title);
            title.setText(R.string.title_success_change);
            TextView success_tv1 = (TextView) findViewById(R.id.success_tv1);
            success_tv1.setText(R.string.success_change);
            TextView success_tv2 = (TextView) findViewById(R.id.success_tv2);
            success_tv2.setText(getString(R.string.new_pw) + ": " + Html.fromHtml("<font color=#08A09D>"+npd+"</font>"));
            TextView success_tv3 = (TextView) findViewById(R.id.success_tv3);
            success_tv3.setVisibility(View.GONE);
            TextView confirm = (TextView) findViewById(R.id.confirm_change);
            confirm.setText(R.string.send_success_btnBackLoginTextView);

            LinearLayout left_btn = (LinearLayout) findViewById(R.id.botton_left_btn);
            left_btn.setVisibility(View.GONE);

            LinearLayout ll = (LinearLayout) findViewById(R.id.botton_btn);
            ll.setLayoutParams(layout);
            ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent(SuccessActivity.this, LoginActivity.class);
                    it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(it);
//                    overridePendingTransition(R.anim.push_right_in_no_alpha,R.anim.push_right_out_no_alpha);
                }
            });

        }else if(tag == 7){
            setContentView(R.layout.success_one_button);
            imgBack = (ImageView) findViewById(R.id.imgBack);
            imgBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
            String pw = getIntent().getStringExtra("password");
            String name = getIntent().getStringExtra("name");
            TextView title = (TextView) findViewById(R.id.title);
            title.setText("转账成功!");
            TextView success_tv1 = (TextView) findViewById(R.id.success_tv1); success_tv1.setText("转账成功!");
            TextView success_tv2 = (TextView) findViewById(R.id.success_tv2); success_tv2.setText("正常情况下会有10秒左右的延迟,请您耐心等待!");
            TextView success_tv3 = (TextView) findViewById(R.id.success_tv3);
            success_tv3.setVisibility(View.GONE);
            //success_tv3.setText(getResources().getString(R.string.password) + ":" + pw);
            TextView confirm = (TextView) findViewById(R.id.confirm_change);  confirm.setText("返回转账主界面");

            LinearLayout ll = (LinearLayout) findViewById(R.id.botton_btn);
            ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

        } else if(tag == 8){
            setContentView(R.layout.success_two_button);
            imgBack = (ImageView) findViewById(R.id.imgBack);
            imgBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });

            TextView title = (TextView) findViewById(R.id.title);
            title.setText(R.string.valid_sms_success_title);

            TextView success_tv1 = (TextView) findViewById(R.id.success_tv1);
            success_tv1.setText(R.string.valid_sms_success_message_title);

            TextView success_tv2 = (TextView) findViewById(R.id.success_tv2);
            success_tv2.setText(R.string.valid_sms_success_message);
            TextView success_tv3 = (TextView) findViewById(R.id.success_tv3);
            success_tv3.setVisibility(View.GONE);

            TextView confirm = (TextView) findViewById(R.id.confirm_change);
            confirm.setText(R.string.send_success_btnBackLoginTextView);

            LinearLayout left_btn = (LinearLayout) findViewById(R.id.botton_left_btn);
            left_btn.setVisibility(View.GONE);

            LinearLayout ll = (LinearLayout) findViewById(R.id.botton_btn);
            ll.setLayoutParams(layout);
            ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent(SuccessActivity.this, LoginActivity.class);
                    it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(it);
                }
            });
        }
    }

    private void getBalance(){
        mIsError = false;
        MyAsyncTask<BalanceInfo> task = new MyAsyncTask<BalanceInfo>(SuccessActivity.this) {
            @Override
            public BalanceInfo callService() throws IOException, JsonParseException, BizException, ServiceException {
                return new AccountService().getBalance();
            }
            @Override
            public void onLoaded(BalanceInfo data) throws Exception {
                if(SuccessActivity.this == null || SuccessActivity.this.isFinishing())
                    return;
                if(!mIsError) {
                    DecimalFormat decim = new DecimalFormat("0.00");
                    ((TextView) SuccessActivity.this.findViewById(R.id.success_tv3))
                            .setText(SuccessActivity.this.getString(R.string.account_balance) + " " +
                                    decim.format(Double.parseDouble(data.getAvailableScores().replace(",", ""))) + "元");
                }
                else {
                    BaseApp.changeUrl(SuccessActivity.this, new BaseApp.OnChangeUrlListener() {
                        @Override
                        public void changeSuccess() {
                            getBalance();
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
    @Override
    public boolean checkLogin(Activity activity, Class<?> loginBeforecCls) {
        return true;
    }
}
