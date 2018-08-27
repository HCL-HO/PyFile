package com.hec.app.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.hec.app.R;
import com.hec.app.activity.base.BaseActivity;
import com.hec.app.activity.base.BaseApp;
import com.hec.app.config.CommonConfig;
import com.hec.app.entity.BankInfo;
import com.hec.app.entity.BizException;
import com.hec.app.entity.ChangePwInfo;
import com.hec.app.entity.CityInfo;
import com.hec.app.entity.CustomerInfo;
import com.hec.app.entity.ProvinceInfo;
import com.hec.app.entity.Response;
import com.hec.app.framework.widget.MyToast;
import com.hec.app.util.CustomerAccountManager;
import com.hec.app.util.DialogUtil;
import com.hec.app.util.MyAsyncTask;
import com.hec.app.webservice.AccountService;
import com.hec.app.webservice.ServiceException;
import com.hec.app.webservice.WithdrawService;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FormActivity extends BaseActivity {

    private ImageView imgBack;
    private TextView title;

    private ArrayList<String> bank = new ArrayList<String>();
    private ArrayList<Integer> bankTypeId = new ArrayList<Integer>();
    Intent next = new Intent();

    private TextView pwr;
    private EditText et1;private EditText et2;private EditText et3;
    private LinearLayout err1;private LinearLayout err2;private LinearLayout err3;
    private TextView errText1;private TextView errText2;private TextView errText3;
    private ImageView errCross1;private ImageView errCross2;private ImageView errCross3;
    private RelativeLayout rl2;private RelativeLayout rl4;private RelativeLayout rl6;
    private LinearLayout llLogout;
    private TextView tvLogout;
    private LinearLayout ll;
    private ProgressDialog progressDialog;
    private boolean mIsError;
    private boolean pressed = false;
    private ArrayAdapter banklist;
    private Spinner provinceSpinner;
    private Spinner citySpinner;
    private EditText telephoneNo;
    private EditText branchbank;
    private TextView telephonecheck;
    private List<String> cityname;
    private List<String> provincename;
    private List<ProvinceInfo> provinceInfos;
    private List<CityInfo> cityInfos;
    private boolean iscomplete = true;
    private boolean phoneNo = true;
    private boolean allowWithdraw = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout whole = (LinearLayout) findViewById(R.id.whole);
        Intent intent = getIntent();
        int tag = intent.getIntExtra("tag", -1);
        final int btnType = intent.getIntExtra("btn_type", -1);

        //change login password
        if(tag == 0){
            setContentView(R.layout.form_password);
            getId();
            imgBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
            title.setText(R.string.title_change_ac_pw);
            pwr.setText(Html.fromHtml("<font color=#cc0029>*</font> " + getString(R.string.remark1)));
            pressed = false;
            errCross1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    err1.setVisibility(View.GONE);
                    errCross1.setVisibility(View.GONE);

                    rl2.setBackgroundResource(0);
                    rl2.setBackgroundColor(Color.parseColor("#f2f2f2"));
                }
            });
            errCross2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    err2.setVisibility(View.GONE);
                    errCross2.setVisibility(View.GONE);
                    rl4.setBackgroundResource(0);
                    rl4.setBackgroundColor(Color.parseColor("#f2f2f2"));
                    et2.setText("");
                }
            });
            errCross3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    err3.setVisibility(View.GONE);
                    errCross3.setVisibility(View.GONE);
                    rl6.setBackgroundResource(0);
                    rl6.setBackgroundColor(Color.parseColor("#f2f2f2"));
                    et3.setText("");
                }
            });
            ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mIsError = false;
                    boolean proceed = true;
                    if(et1.getText().toString().trim().length() == 0 || et2.getText().toString().trim().length() == 0 || et3.getText().toString().trim().length() == 0) {
                        proceed = false;
                        if (et1.getText().toString().trim().length() == 0) {
                            err1.setVisibility(View.VISIBLE);
                            errCross1.setVisibility(View.VISIBLE);
                            rl2.setBackgroundResource(R.drawable.rect_no_round);
                            errText1.setText("密码不能为空");
                        } else {
                            err1.setVisibility(View.GONE);
                            errCross1.setVisibility(View.GONE);
                            rl2.setBackgroundResource(0);
                            rl2.setBackgroundColor(Color.parseColor("#f2f2f2"));
                        }
                        if (et2.getText().toString().trim().length() == 0) {
                            err2.setVisibility(View.VISIBLE);
                            errCross2.setVisibility(View.VISIBLE);
                            rl4.setBackgroundResource(R.drawable.rect_no_round);
                            errText2.setText("密码不能为空");
                        } else {
                            err2.setVisibility(View.GONE);
                            errCross2.setVisibility(View.GONE);
                            rl4.setBackgroundResource(0);
                            rl4.setBackgroundColor(Color.parseColor("#f2f2f2"));

                        }
                        if (et3.getText().toString().trim().length() == 0) {
                            err3.setVisibility(View.VISIBLE);
                            errCross3.setVisibility(View.VISIBLE);
                            rl6.setBackgroundResource(R.drawable.rect_no_round);
                            errText3.setText("密码不能为空");

                        } else {
                            err3.setVisibility(View.GONE);
                            errCross3.setVisibility(View.GONE);
                            rl6.setBackgroundResource(0);
                            rl6.setBackgroundColor(Color.parseColor("#f2f2f2"));
                        }
                        closeProgressDialog();
                    }

                    if (!et2.getText().toString().equals(et3.getText().toString())
                            && et2.getText().toString().length() > 0
                            && et3.getText().toString().length() > 0) {
                        proceed = false;
                        err2.setVisibility(View.VISIBLE);
                        errCross2.setVisibility(View.VISIBLE);
                        rl4.setBackgroundResource(R.drawable.rect_no_round);
                        errText2.setText(R.string.confirm_pw_fail);
                        err3.setVisibility(View.VISIBLE);
                        errCross3.setVisibility(View.VISIBLE);
                        rl6.setBackgroundResource(R.drawable.rect_no_round);
                        errText3.setText(R.string.confirm_pw_fail);
                        closeProgressDialog();
                    }

                    Pattern p = Pattern.compile("^[a-zA-Z0-9_]+$");
                    Matcher m = p.matcher(et2.getText().toString());
                    if(!m.matches()){
                        closeProgressDialog();
                        proceed = false;
                        err2.setVisibility(View.VISIBLE);
                        errCross2.setVisibility(View.VISIBLE);
                        rl4.setBackgroundResource(R.drawable.rect_no_round);
                        errText2.setText("密码包含非法字符");
                        err3.setVisibility(View.VISIBLE);
                        errCross3.setVisibility(View.VISIBLE);
                        rl6.setBackgroundResource(R.drawable.rect_no_round);
                        errText3.setText("密码包含非法字符");
                    }

                    String password = et2.getText().toString().trim();
                    if (password.length() < 6 || password.length() > 16) {
                        closeProgressDialog();
                        err2.setVisibility(View.VISIBLE);
                        errCross2.setVisibility(View.VISIBLE);
                        rl4.setBackgroundResource(R.drawable.rect_no_round);
                        errText2.setText("请输入6到16个字符");
                        proceed = false;
                    }

                    String password2 = et3.getText().toString().trim();
                    if (password2.length() < 6 || password2.length() > 16) {
                        closeProgressDialog();
                        err3.setVisibility(View.VISIBLE);
                        errCross3.setVisibility(View.VISIBLE);
                        rl6.setBackgroundResource(R.drawable.rect_no_round);
                        errText3.setText("请输入6到16个字符");
                        proceed = false;
                    }

                    if (BaseApp.isPasswordEasy(password)) {
                        closeProgressDialog();
                        proceed = false;
                        MyToast.show(FormActivity.this, getString(R.string.error_message_login_pw_stupid), Toast.LENGTH_LONG);
                    }

                    if (proceed && !pressed) {
                        showProgressDialog("正在修改账户密码!");
                        pressed = true;
                        mIsError = false;
                        err2.setVisibility(View.GONE);
                        errCross2.setVisibility(View.GONE);
                        rl4.setBackgroundResource(0);
                        rl4.setBackgroundColor(Color.parseColor("#f2f2f2"));
                        err3.setVisibility(View.GONE);
                        errCross3.setVisibility(View.GONE);
                        rl6.setBackgroundResource(0);
                        rl6.setBackgroundColor(Color.parseColor("#f2f2f2"));
                        changePw(0);
                    }
                }
            });

            SharedPreferences sharedPreferences = getSharedPreferences(CommonConfig.KEY_DATA, MODE_PRIVATE);
            if (sharedPreferences.getBoolean(CommonConfig.KEY_DATA_STUPID_LOGIN_PASSWORD_FLAG, false)) {
                String logoutStr = getString(R.string.title_logout);
                if (CustomerAccountManager.getInstance() != null && CustomerAccountManager.getInstance().getCustomer() != null) {
                    logoutStr += " " + CustomerAccountManager.getInstance().getCustomer().getUserName();
                }
                imgBack.setVisibility(View.GONE);
                tvLogout.setText(logoutStr);

                llLogout.setVisibility(View.VISIBLE);
                llLogout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Dialog resultbox = new Dialog(FormActivity.this);
                        resultbox.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        resultbox.setCancelable(false);
                        resultbox.setContentView(R.layout.setting_dialog);

                        LinearLayout yes = (LinearLayout) resultbox.findViewById(R.id.logout_yes);
                        LinearLayout no = (LinearLayout) resultbox.findViewById(R.id.logout_no);
                        yes.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (CustomerAccountManager.getInstance() != null) {
                                    CustomerAccountManager.getInstance().logOut();
                                }

                                finish();
                                if(BaseApp.rootActivity != null) {
                                    BaseApp.rootActivity.finish();
                                }

                                SharedPreferences token = getSharedPreferences(CommonConfig.KEY_TOKEN, Context.MODE_PRIVATE);
                                token.edit().putString(CommonConfig.KEY_TOKEN_TOKENS, "").commit();
                                Intent next = new Intent(FormActivity.this, LoginActivity.class);
                                startActivity(next);

                                resultbox.dismiss();
                            }
                        });
                        no.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                resultbox.dismiss();
                            }
                        });

                        resultbox.show();
                    }
                });
            }

        } //change fund account password
        else if (tag == 1){
            setContentView(R.layout.form_password);
            getId();
            pressed = false;
            imgBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
            title.setText(R.string.title_change_fund_pw);
            pwr.setText(Html.fromHtml("<font color=#cc0029>*</font> " + getString(R.string.remark2)));
            errCross1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    err1.setVisibility(View.GONE);
                    errCross1.setVisibility(View.GONE);
                    rl2.setBackgroundResource(0);
                    rl2.setBackgroundColor(Color.parseColor("#f2f2f2"));
                }
            });
            errCross2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    err2.setVisibility(View.GONE);
                    errCross2.setVisibility(View.GONE);
                    rl4.setBackgroundResource(0);
                    rl4.setBackgroundColor(Color.parseColor("#f2f2f2"));
                    et2.setText("");
                }
            });
            errCross3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    err3.setVisibility(View.GONE);
                    errCross3.setVisibility(View.GONE);
                    rl6.setBackgroundResource(0);
                    rl6.setBackgroundColor(Color.parseColor("#f2f2f2"));
                    et3.setText("");
                }
            });
            ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showProgressDialog("正在修改资金密码");
                    mIsError = false;
                    boolean proceed = true;
                    if(et1.getText().toString().trim().length() == 0 || et2.getText().toString().trim().length() == 0 || et3.getText().toString().trim().length() == 0) {
                        proceed = false;
                        if (et1.getText().toString().trim().length() == 0) {
                            err1.setVisibility(View.VISIBLE);
                            errCross1.setVisibility(View.VISIBLE);
                            rl2.setBackgroundResource(R.drawable.rect_no_round);
                            errText1.setText("密码不能为空");
                        } else {
                            err1.setVisibility(View.GONE);
                            errCross1.setVisibility(View.GONE);
                            rl2.setBackgroundResource(0);
                            rl2.setBackgroundColor(Color.parseColor("#f2f2f2"));
                        }
                        if (et2.getText().toString().trim().length() == 0) {
                            err2.setVisibility(View.VISIBLE);
                            errCross2.setVisibility(View.VISIBLE);
                            rl4.setBackgroundResource(R.drawable.rect_no_round);
                            errText2.setText("密码不能为空");
                        } else {
                            err2.setVisibility(View.GONE);
                            errCross2.setVisibility(View.GONE);
                            rl4.setBackgroundResource(0);
                            rl4.setBackgroundColor(Color.parseColor("#f2f2f2"));

                        }
                        if (et3.getText().toString().trim().length() == 0) {
                            err3.setVisibility(View.VISIBLE);
                            errCross3.setVisibility(View.VISIBLE);
                            rl6.setBackgroundResource(R.drawable.rect_no_round);
                            errText3.setText("密码不能为空");

                        } else {
                            err3.setVisibility(View.GONE);
                            errCross3.setVisibility(View.GONE);
                            rl6.setBackgroundResource(0);
                            rl6.setBackgroundColor(Color.parseColor("#f2f2f2"));
                        }
                        closeProgressDialog();
                    }

                    if (!et2.getText().toString().equals(et3.getText().toString())
                            && et2.getText().toString().length() > 0
                            && et3.getText().toString().length() > 0) {
                        proceed = false;
                        err2.setVisibility(View.VISIBLE);
                        errCross2.setVisibility(View.VISIBLE);
                        rl4.setBackgroundResource(R.drawable.rect_no_round);
                        errText2.setText(R.string.confirm_pw_fail);
                        err3.setVisibility(View.VISIBLE);
                        errCross3.setVisibility(View.VISIBLE);
                        rl6.setBackgroundResource(R.drawable.rect_no_round);
                        errText3.setText(R.string.confirm_pw_fail);
                        closeProgressDialog();
                    }

                    Pattern p = Pattern.compile("^[a-zA-Z0-9_]+$");
                    Matcher m = p.matcher(et2.getText().toString());
                    if(!m.matches()){
                        closeProgressDialog();
                        proceed = false;
                        err2.setVisibility(View.VISIBLE);
                        errCross2.setVisibility(View.VISIBLE);
                        rl4.setBackgroundResource(R.drawable.rect_no_round);
                        errText2.setText("密码包含非法字符");
                        err3.setVisibility(View.VISIBLE);
                        errCross3.setVisibility(View.VISIBLE);
                        rl6.setBackgroundResource(R.drawable.rect_no_round);
                        errText3.setText("密码包含非法字符");
                    }

                    String password = et2.getText().toString().trim();
                    if (password.length() < 6 || password.length() > 16) {
                        closeProgressDialog();
                        err2.setVisibility(View.VISIBLE);
                        errCross2.setVisibility(View.VISIBLE);
                        rl4.setBackgroundResource(R.drawable.rect_no_round);
                        errText2.setText("请输入6到16个字符");
                        proceed = false;
                    }

                    String password2 = et3.getText().toString().trim();
                    if (password2.length() < 6 || password2.length() > 16) {
                        closeProgressDialog();
                        err3.setVisibility(View.VISIBLE);
                        errCross3.setVisibility(View.VISIBLE);
                        rl6.setBackgroundResource(R.drawable.rect_no_round);
                        errText3.setText("请输入6到16个字符");
                        proceed = false;
                    }

                    if (BaseApp.isPasswordEasy(password)) {
                        closeProgressDialog();
                        proceed = false;
                        MyToast.show(FormActivity.this, getString(R.string.error_message_money_pw_stupid), Toast.LENGTH_LONG);
                    }

                    if (proceed && !pressed) {
                        err2.setVisibility(View.GONE);
                        errCross2.setVisibility(View.GONE);
                        rl4.setBackgroundResource(0);
                        rl4.setBackgroundColor(Color.parseColor("#f2f2f2"));
                        err3.setVisibility(View.GONE);
                        errCross3.setVisibility(View.GONE);
                        rl6.setBackgroundResource(0);
                        rl6.setBackgroundColor(Color.parseColor("#f2f2f2"));
                        pressed = true;
                        mIsError = false;
                        changePw(1);
                    }
                }
            });

            SharedPreferences sharedPreferences = getSharedPreferences(CommonConfig.KEY_DATA, MODE_PRIVATE);
            if (sharedPreferences.getBoolean(CommonConfig.KEY_DATA_STUPID_MONEY_PASSWORD_FLAG, false)) {
                String logoutStr = getString(R.string.title_logout);
                if (CustomerAccountManager.getInstance() != null && CustomerAccountManager.getInstance().getCustomer() != null) {
                    logoutStr += " " + CustomerAccountManager.getInstance().getCustomer().getUserName();
                }
                imgBack.setVisibility(View.GONE);
                tvLogout.setText(logoutStr);

                llLogout.setVisibility(View.VISIBLE);
                llLogout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Dialog resultbox = new Dialog(FormActivity.this);
                        resultbox.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        resultbox.setCancelable(false);
                        resultbox.setContentView(R.layout.setting_dialog);

                        LinearLayout yes = (LinearLayout) resultbox.findViewById(R.id.logout_yes);
                        LinearLayout no = (LinearLayout) resultbox.findViewById(R.id.logout_no);
                        yes.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (CustomerAccountManager.getInstance() != null) {
                                    CustomerAccountManager.getInstance().logOut();
                                }

                                finish();
                                if(BaseApp.rootActivity != null) {
                                    BaseApp.rootActivity.finish();
                                }

                                SharedPreferences token = getSharedPreferences(CommonConfig.KEY_TOKEN, Context.MODE_PRIVATE);
                                token.edit().putString(CommonConfig.KEY_TOKEN_TOKENS, "").commit();
                                Intent next = new Intent(FormActivity.this, LoginActivity.class);
                                startActivity(next);

                                resultbox.dismiss();
                            }
                        });
                        no.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                resultbox.dismiss();
                            }
                        });

                        resultbox.show();
                    }
                });
            }
        }
        else {
            //add new bank card
            if (tag == 3) {
                pressed = false;
                mIsError = false;
                setContentView(R.layout.form_add_bank_card);
                imgBack = (ImageView) findViewById(R.id.imgBack);
                imgBack.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                });
                title = (TextView) findViewById(R.id.title);
                title.setText(R.string.withdraw_bind_bank);
                final Spinner list_bank = (Spinner) findViewById(R.id.list_bank);
                TextView bank_remark = (TextView) findViewById(R.id.bank_remark);
                banklist = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, bank);

                provinceSpinner = (Spinner) findViewById(R.id.province);
                citySpinner = (Spinner) findViewById(R.id.city);
                telephoneNo = (EditText) findViewById(R.id.telephoneNo);
                branchbank = (EditText) findViewById(R.id.brach_bank);
                telephonecheck = (TextView) findViewById(R.id.telephonecheck);

                getBankList();
                getProvinceInfo();
                banklist.setDropDownViewResource(R.layout.spinner_item);
                list_bank.setAdapter(banklist);
                bank_remark.setText(Html.fromHtml("<font color=#cc0029>*</font> " + "重复验证可防止误操作导致卡号信息错误"));
                et1 = (EditText) findViewById(R.id.retrieval_name);
                et2 = (EditText) findViewById(R.id.retrieval_bank_no);
                et3 = (EditText) findViewById(R.id.retrieval_re_bank_no);
                ll = (LinearLayout) findViewById(R.id.retrieval_confirm_change);
                ll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if ((et1.getText().toString().trim().length() == 0) || (et2.getText().toString().trim().length() == 0) || (et3.getText().toString().trim().length() == 0)) {
                            MyToast.show(FormActivity.this, "请填写所有空格!", Toast.LENGTH_LONG);
                        } else if (!et2.getText().toString().equals(et3.getText().toString())) {
                            MyToast.show(FormActivity.this, "卡号不匹配!", Toast.LENGTH_LONG);
                        } else if (!checkChinese(et1.getText().toString())) {
                            MyToast.show(FormActivity.this, "开户人姓名应为中文！");
                        } else {
                            mIsError = false;
                            if(!((String)provinceSpinner.getSelectedItem()).equals("请选择")
                                    && citySpinner.getSelectedItem() != null
                                    && (telephoneNo.getText().toString().isEmpty()
                                    || branchbank.getText().toString().isEmpty())){
                                if(!phoneNo){
                                    MyToast.show(FormActivity.this,"您输入的电话号码有误！");
                                    return;
                                }
                                android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(FormActivity.this)
                                        .setTitle("确认信息")
                                        .setMessage("您的极速提现资料尚未完善，会影响极速提现功能，是否继续？")
                                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                addquick(list_bank, btnType);
                                            }
                                        })
                                        .setNegativeButton("取消", new DialogInterface.OnClickListener(){
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        })
                                        .setCancelable(false);
                                dialog.show();
                            }else if(((String)provinceSpinner.getSelectedItem()).equals("请选择")
                                    && citySpinner.getSelectedItem() == null
                                    && telephoneNo.getText().toString().isEmpty()
                                    && branchbank.getText().toString().isEmpty()){
                                android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(FormActivity.this)
                                        .setTitle("确认信息")
                                        .setMessage("您的极速提现资料尚未完善，会影响极速提现功能，是否继续？")
                                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                addBankCard(list_bank, btnType);
                                            }
                                        })
                                        .setNegativeButton("取消", new DialogInterface.OnClickListener(){
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        })
                                        .setCancelable(false);
                                dialog.show();
                            }else if(!((String)provinceSpinner.getSelectedItem()).equals("请选择")
                                    && citySpinner.getSelectedItem() != null
                                    && !telephoneNo.getText().toString().isEmpty()
                                    && !branchbank.getText().toString().isEmpty()){
                                if(!phoneNo){
                                    MyToast.show(FormActivity.this,"您输入的电话号码有误！");
                                    return;
                                }
                                addquick(list_bank, btnType);
                            }else{
                                MyToast.show(FormActivity.this,"您尚有资料未完善！");
                            }

                        }
                    }
                });

                //for more strict check
                String pattern = "^((13[0-9])|(15[0-9])|(18[0-9])|(17[0-9])|(14)[0-9])\\d{8}$";
                final Pattern p = Pattern.compile(pattern);


                telephoneNo.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        String telephone = s.toString();
                        Matcher m = p.matcher(telephone);
                        if(telephone.length() == 0){
                            phoneNo = true;
                            telephonecheck.setVisibility(View.INVISIBLE);
                        }else if(m.matches()){
                            phoneNo = true;
                            phoneNo = true;
                            telephonecheck.setVisibility(View.VISIBLE);
                            telephonecheck.setText("号码格式正确");
                            telephonecheck.setTextColor(getResources().getColor(R.color.green));
                        }else{
                            phoneNo = false;
                            telephonecheck.setVisibility(View.VISIBLE);
                            telephonecheck.setText("号码格式有误");
                            telephonecheck.setTextColor(getResources().getColor(R.color.red));
                        }
                    }
                });
                //withdraw money
            } else {
                if (tag == 4) {
                    setContentView(R.layout.form_withdraw_money);
                    getId();
                    imgBack.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onBackPressed();
                        }
                    });
                    title.setText(R.string.withdraw_title);
                    final String name = intent.getStringExtra("name");
                    final String bank = intent.getStringExtra("bank");
                    final String cardNo = intent.getStringExtra("cardNo");
                    final int bankId = intent.getIntExtra("bankId", 0);
                    final int banktypeid = intent.getIntExtra("banktypeid",0);
                    Log.i("transfer","lala" + " form "+banktypeid);
                    TextView user_tv = (TextView) findViewById(R.id.user_tv);
                    user_tv.setText("戶主: " + name + " [" + bank + "]");
                    TextView card_no_tv = (TextView) findViewById(R.id.card_no_tv);
                    card_no_tv.setText("卡号: " + cardNo);

                    final String BankBranch = intent.getStringExtra("bankbranch");
                    final String BankCity = intent.getStringExtra("bankcity");
                    final String Phone = intent.getStringExtra("phone");
                    final String BankProvince = intent.getStringExtra("bankprovince");

                    TextView quickinfo1 = (TextView) findViewById(R.id.quickinfo1);
                    TextView quickinfo2 = (TextView) findViewById(R.id.quickinfo2);
                    TextView quickinfo3 = (TextView) findViewById(R.id.quickinfo3);
                    TextView quickinfo4 = (TextView) findViewById(R.id.quickinfo4);
                    LinearLayout gotoupdatebankinfo = (LinearLayout) findViewById(R.id.gotoupdatebankinfo);



                    if(BankProvince == null){
                       iscomplete = false;
                        allowWithdraw = false;
                        quickinfo1.setVisibility(View.GONE);
                    }else{
                        if(BankProvince.isEmpty()){
                            iscomplete = false;
                            allowWithdraw = false;
                            quickinfo1.setVisibility(View.GONE);
                        }else{
                            quickinfo1.setText("省份： " + BankProvince);
                        }
                    }

                    if(BankCity == null){
                        iscomplete = false;
                        allowWithdraw = false;
                        quickinfo2.setVisibility(View.GONE);
                    }else{
                        if(BankCity.isEmpty()){
                            iscomplete = false;
                            quickinfo2.setVisibility(View.GONE);
                        }else{
                            quickinfo2.setText("城市： " + BankCity);
                        }
                    }

                    if(Phone == null){
                        iscomplete = false;
                        quickinfo3.setVisibility(View.GONE);
                    }else{
                        if(Phone.isEmpty()){
                            iscomplete = false;
                            quickinfo3.setVisibility(View.GONE);
                        }else{
                            quickinfo3.setText("绑定电话号码： " + Phone);
                        }
                    }

                    if(BankBranch == null){
                        iscomplete = false;
                        quickinfo4.setVisibility(View.GONE);
                    }else{
                        if(BankBranch.isEmpty()){
                            iscomplete = false;
                            quickinfo4.setVisibility(View.GONE);
                        }else{
                            quickinfo4.setText("支行所在地： " + BankBranch);
                        }
                    }

                        gotoupdatebankinfo.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent next = new Intent(FormActivity.this, FormActivity.class);
                                next.putExtra("tag", 5);
                                next.putExtra("name", name);
                                next.putExtra("bank", bank);
                                next.putExtra("cardNo", cardNo);
                                next.putExtra("banktypeid", banktypeid);
                                next.putExtra("bankbranch", BankBranch);
                                next.putExtra("bankcity", BankCity);
                                next.putExtra("phone", Phone);
                                next.putExtra("bankprovince", BankProvince);
                                startActivity(next);
                                finish();
                            }
                        });
                    LinearLayout ll = (LinearLayout) findViewById(R.id.confirm_change);
                    if(!allowWithdraw){
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                            ll.setBackground(getDrawable(R.drawable.rect_graw));
                        }else{
                            ll.setBackground(getResources().getDrawable(R.drawable.rect_graw));
                        }
                    }
                    ll.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (et1.getText().toString().trim().length() == 0 || et2.getText().toString().trim().length() == 0) {

                                if (et1.getText().toString().trim().length() == 0) {
                                    err1.setVisibility(View.VISIBLE);
                                    errCross1.setVisibility(View.VISIBLE);
                                    rl2.setBackgroundResource(R.drawable.rect_no_round);
                                    errText1.setText("请输入提款金额");
                                }
                                if (et2.getText().toString().trim().length() == 0) {
                                    err2.setVisibility(View.VISIBLE);
                                    errCross2.setVisibility(View.VISIBLE);
                                    rl4.setBackgroundResource(R.drawable.rect_no_round);
                                    errText2.setText("资金密码有误,请重新输入");
                                }

                            } else if (Double.parseDouble(et1.getText().toString()) < 1) {
                                err1.setVisibility(View.VISIBLE);
                                errCross1.setVisibility(View.VISIBLE);
                                rl2.setBackgroundResource(R.drawable.rect_no_round);
                                errText1.setText("提现金额需大于1元！");
                            } else if (!pressed) {
                                mIsError = false;
                                pressed = true;
                                if(iscomplete){
                                    if(allowWithdraw){
                                        showProgressDialog("正在提交申请!");
                                        withdrawquick(bankId, bank);
                                    }else{
                                        MyToast.show(FormActivity.this,"请先完善银行卡资料！");
                                    }
                                }else{
                                    if(allowWithdraw){
                                        showProgressDialog("正在提交申请!");
                                        withdrawquick(bankId, bank);
                                    }else{
                                        MyToast.show(FormActivity.this,"请先完善银行卡资料！");
                                    }
//                                    android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(FormActivity.this)
//                                            .setTitle("确认信息")
//                                            .setMessage("您的极速提现资料尚未完善，会影响极速提现功能，是否继续？")
//                                            .setPositiveButton("确认", new DialogInterface.OnClickListener() {
//                                                @Override
//                                                public void onClick(DialogInterface dialog, int which) {
//                                                    withdraw(bankId, bank);
//                                                }
//                                            })
//                                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                                                @Override
//                                                public void onClick(DialogInterface dialog, int which) {
//                                                    dialog.dismiss();
//                                                }
//                                            })
//                                            .setCancelable(false);
//                                    dialog.show();
                                }
                            }
                        }
                    });
                }else if(tag == 5){

                    final String name = intent.getStringExtra("name");
                    final String bank = intent.getStringExtra("bank");
                    final String cardNo = intent.getStringExtra("cardNo");
                    final int bankId = intent.getIntExtra("bankId", 0);
                    final int banktypeid = intent.getIntExtra("banktypeid",0);
                    Log.i("transfer","form 5" + banktypeid);
                    final String BankBranch = intent.getStringExtra("bankbranch");
                    final String BankCity = intent.getStringExtra("bankcity");
                    final String Phone = intent.getStringExtra("phone");
                    final String BankProvince = intent.getStringExtra("bankprovince");
                    pressed = false;
                    mIsError = false;
                    setContentView(R.layout.form_add_bank_card);
                    imgBack = (ImageView) findViewById(R.id.imgBack);
                    imgBack.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onBackPressed();
                        }
                    });
                    title = (TextView) findViewById(R.id.title);
                    title.setText("更新银行卡信息");
                    final Spinner list_bank = (Spinner) findViewById(R.id.list_bank);
                    TextView bank_remark = (TextView) findViewById(R.id.bank_remark);
                    List<String> bankone = new ArrayList<>();
                    bankone.add(bank);
                    banklist = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, bankone);

                    provinceSpinner = (Spinner) findViewById(R.id.province);
                    citySpinner = (Spinner) findViewById(R.id.city);
                    telephoneNo = (EditText) findViewById(R.id.telephoneNo);
                    branchbank = (EditText) findViewById(R.id.brach_bank);
                    telephonecheck = (TextView) findViewById(R.id.telephonecheck);

                    //getBankList();
                    //getProvinceInfo();
                    banklist.setDropDownViewResource(R.layout.spinner_item);
                    list_bank.setAdapter(banklist);
                    list_bank.setClickable(false);

                    bank_remark.setText(Html.fromHtml("<font color=#cc0029>*</font> " + "重复验证可防止误操作导致卡号信息错误"));
                    et1 = (EditText) findViewById(R.id.retrieval_name);
                    et1.setText(name);
                    et1.setClickable(false);
                    et1.setFocusable(false);
                    et1.setFocusableInTouchMode(false);
                    et2 = (EditText) findViewById(R.id.retrieval_bank_no);
                    et2.setText(cardNo);
                    et2.setClickable(false);
                    et2.setFocusable(false);
                    et2.setFocusableInTouchMode(false);
                    et3 = (EditText) findViewById(R.id.retrieval_re_bank_no);
                    et3.setText(cardNo);
                    et3.setClickable(false);
                    et3.setFocusable(false);
                    et3.setFocusableInTouchMode(false);
                    ll = (LinearLayout) findViewById(R.id.retrieval_confirm_change);
                    TextView okupdate = (TextView) findViewById(R.id.okupdate);
                    okupdate.setText("更新信息");
                    ll.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if ((et1.getText().toString().trim().length() == 0) || (et2.getText().toString().trim().length() == 0) || (et3.getText().toString().trim().length() == 0)) {
                                MyToast.show(FormActivity.this, "请填写所有空格!", Toast.LENGTH_LONG);
                            } else if (!et2.getText().toString().equals(et3.getText().toString())) {
                                MyToast.show(FormActivity.this, "卡号不匹配!", Toast.LENGTH_LONG);
                            } else {
                                mIsError = false;
                                if(!((String)provinceSpinner.getSelectedItem()).equals("请选择")
                                        && citySpinner.getSelectedItem() != null
                                        && (telephoneNo.getText().toString().isEmpty()
                                        || branchbank.getText().toString().isEmpty())){
                                    if(!phoneNo){
                                        MyToast.show(FormActivity.this,"您输入的电话号码有误！");
                                        return;
                                    }
                                    android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(FormActivity.this)
                                            .setTitle("确认信息")
                                            .setMessage("您的极速提现资料尚未完善，会影响极速提现功能，是否继续？")
                                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    quickupdate(list_bank,banktypeid,bank);
                                                }
                                            })
                                            .setNegativeButton("取消", new DialogInterface.OnClickListener(){
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            })
                                            .setCancelable(false);
                                    dialog.show();
                                }else if(((String)provinceSpinner.getSelectedItem()).equals("请选择")
                                        && citySpinner.getSelectedItem() == null
                                        && telephoneNo.getText().toString().isEmpty()
                                        && branchbank.getText().toString().isEmpty()){
                                    MyToast.show(FormActivity.this,"您还没有填写更新信息！");
                                }else if(!((String)provinceSpinner.getSelectedItem()).equals("请选择")
                                        && citySpinner.getSelectedItem() != null
                                        && !telephoneNo.getText().toString().isEmpty()
                                        && !branchbank.getText().toString().isEmpty()){
                                    if(!phoneNo){
                                        MyToast.show(FormActivity.this,"您输入的电话号码有误！");
                                        return;
                                    }
                                    quickupdate(list_bank,banktypeid,bank);
                                }else{
                                    MyToast.show(FormActivity.this,"您尚有资料未完善！");
                                }

                            }
                        }
                    });

//                    if(BankProvince == null){
                        getProvinceInfo();
//                    }else{
//                        List<String> provinceone = new ArrayList<>();
//                        provinceone.add(BankProvince);
//                        List<String> cityone = new ArrayList<>();
//                        cityone.add(BankCity);
//                        ArrayAdapter<String> ProvinceAdapter
//                                = new ArrayAdapter<String>(FormActivity.this, android.R.layout.simple_spinner_dropdown_item, provinceone);
//                        ArrayAdapter<String> cityAdapter
//                                = new ArrayAdapter<String>(FormActivity.this, android.R.layout.simple_spinner_dropdown_item, cityone);
//                        provinceSpinner.setAdapter(ProvinceAdapter);
//                        citySpinner.setAdapter(cityAdapter);
//                        provinceSpinner.setClickable(false);
//                        citySpinner.setClickable(false);
//                    }
                    if(Phone == null){

                    }else{
                        telephoneNo.setText(Phone);
//                        telephoneNo.setClickable(false);
//                        telephoneNo.setFocusable(false);
//                        telephoneNo.setFocusableInTouchMode(false);
                    }
                    if(BankBranch == null){

                    }else{
                        branchbank.setText(BankBranch);
//                        telephoneNo.setClickable(false);
//                        telephoneNo.setFocusable(false);
//                        telephoneNo.setFocusableInTouchMode(false);
                    }
                    //for more strict check
                    String pattern = "^((13[0-9])|(15[0-9])|(18[0-9])|(17[0-9])|(14)[0-9])\\d{8}$";
                    final Pattern p = Pattern.compile(pattern);
                    telephoneNo.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            String telephone = s.toString();
                            Matcher m = p.matcher(telephone);
                            if(telephone.length() == 0){
                                phoneNo = true;
                                telephonecheck.setVisibility(View.INVISIBLE);
                            }else if(m.matches()){
                                phoneNo = true;
                                telephonecheck.setVisibility(View.VISIBLE);
                                telephonecheck.setText("号码格式正确");
                                telephonecheck.setTextColor(getResources().getColor(R.color.green));
                            }else{
                                phoneNo = false;
                                telephonecheck.setVisibility(View.VISIBLE);
                                telephonecheck.setText("号码格式有误");
                                telephonecheck.setTextColor(getResources().getColor(R.color.red));
                            }
                        }
                    });
                }
            }
        }
    }

    private void withdraw(final int bankId, final String bank){
        MyAsyncTask<Response> task = new MyAsyncTask<Response>(FormActivity.this) {
            @Override
            public Response callService() throws IOException, JsonParseException, BizException, ServiceException {
                return new WithdrawService().withdraw(bankId, bank, Double.valueOf(Double.parseDouble(et1.getText().toString())), et2.getText().toString());
            }

            @Override
            public void onLoaded(Response data) throws Exception {
                if(FormActivity.this == null || FormActivity.this.isFinishing())
                    return;
                closeProgressDialog();
                if (!mIsError) {
                    if (data.getSuccess()) {
                        Intent next = new Intent(FormActivity.this, SuccessActivity.class);
                        next.putExtra("tag", 3);
                        next.putExtra("amount", Double.parseDouble(et1.getText().toString()));
                        startActivity(next);
                        finish();
                    } else {
                        MyToast.show(FormActivity.this, data.getMessage());
                    }
                } else {
                    BaseApp.getAppBean().resetApiUrl(FormActivity.this);
                    Toast.makeText(FormActivity.this, "抱歉，网络异常，请查看记录是否已经提款", Toast.LENGTH_LONG).show();
                    BaseApp.getAppBean().setRetryCount(0);
                }
                pressed = false;
            }
        };
        task.setOnError(new MyAsyncTask.OnError() {

            @Override
            public void handleError(Exception e) {
                closeProgressDialog();
                mIsError = true;
            }
        });
        task.executeTask();
    }
    private void addBankCard(final Spinner list_bank, final int btnType){
        showProgressDialog("正在提交申请!");
        MyAsyncTask<Response> task = new MyAsyncTask<Response>(FormActivity.this) {
            @Override
            public Response callService() throws IOException, JsonParseException, BizException, ServiceException {
                if(list_bank.getSelectedItemPosition() != -1)
                    return new WithdrawService().addBankCard((bankTypeId.get(list_bank.getSelectedItemPosition())).intValue(), list_bank.getSelectedItem().toString(),et2.getText().toString(), et1.getText().toString());
                return null;
            }

            public void onLoaded(Response data) throws Exception {
                if(FormActivity.this == null || FormActivity.this.isFinishing())
                    return;
                closeProgressDialog();
                if(!mIsError){
                    if (data.getSuccess()) {
                        Intent next = new Intent(FormActivity.this, SuccessActivity.class);
                        next.putExtra("tag", 2);
                        next.putExtra("name", et1.getText().toString());
                        next.putExtra("bank", list_bank.getSelectedItem().toString());
                        next.putExtra("card", et2.getText().toString());
                        next.putExtra("btn_type", btnType);
                        startActivity(next);
                        finish();
                    }else{
                        MyToast.show(FormActivity.this,data.getMessage());
                    }
                }else{
                    BaseApp.getAppBean().resetApiUrl(FormActivity.this);
                    Toast.makeText(FormActivity.this, "抱歉，网络异常，请查看记录是否已经添卡", Toast.LENGTH_LONG).show();
                    BaseApp.getAppBean().setRetryCount(0);
                }
            }
        };
        task.setOnError(new MyAsyncTask.OnError() {

            @Override
            public void handleError(Exception e) {
                closeProgressDialog();
                mIsError = true;
            }
        });
        task.executeTask();

    }
    private void getBankList(){
        MyAsyncTask<List<BankInfo.BankList>> task = new MyAsyncTask<List<BankInfo.BankList>>(FormActivity.this) {
            @Override
            public  List<BankInfo.BankList> callService() throws IOException, JsonParseException, BizException, ServiceException {
                return new WithdrawService().getBankList();
            }

            @Override
            public void onLoaded(List<BankInfo.BankList> data) throws Exception {
                if(FormActivity.this == null || FormActivity.this.isFinishing())
                    return;
                closeProgressDialog();
                if(!mIsError) {
                    for (BankInfo.BankList info : data) {
                        bank.add(info.getBankTypeName());
                        bankTypeId.add(Integer.valueOf(info.getBankTypeID()));
                    }
                    banklist.notifyDataSetChanged();
                }
                else {
                    BaseApp.changeUrl(FormActivity.this, new BaseApp.OnChangeUrlListener() {
                        @Override
                        public void changeSuccess() {
                            getBankList();
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

    private void changePw(final int tag){
        MyAsyncTask<Response<ChangePwInfo>> task = new MyAsyncTask<Response<ChangePwInfo>>(FormActivity.this) {
            @Override
            public Response<ChangePwInfo> callService() throws IOException, JsonParseException, BizException, ServiceException {
                return new AccountService().changePw(tag, et1.getText().toString(), et2.getText().toString());
            }

            @Override
            public void onLoaded(Response<ChangePwInfo> data) throws Exception {
                if(FormActivity.this == null || FormActivity.this.isFinishing())
                    return;
                closeProgressDialog();
                if (!mIsError) {
                    if (data.getSuccess()) {
                        if (tag == 0) {
                            String username = "";
                            CustomerInfo customer = CustomerAccountManager.getInstance().getCustomer();
                            if (customer != null) {
                                username = customer.getUserName();
                            }

                            SharedPreferences sh = getSharedPreferences(CommonConfig.KEY_DATA, MODE_PRIVATE);
                            SharedPreferences.Editor editor = sh.edit();
                            editor.putBoolean(CommonConfig.KEY_DATA_STUPID_LOGIN_PASSWORD_FLAG, false);
                            editor.putString(username, et2.getText().toString());
                            editor.commit();
                        }
                        else if (tag == 1) {
                            SharedPreferences sh = getSharedPreferences(CommonConfig.KEY_DATA, MODE_PRIVATE);
                            SharedPreferences.Editor editor = sh.edit();
                            editor.putBoolean(CommonConfig.KEY_DATA_STUPID_MONEY_PASSWORD_FLAG, false);
                            editor.commit();
                        }

                        if (imgBack != null) {
                            imgBack.setVisibility(View.VISIBLE);
                        }
                        if (llLogout != null) {
                            llLogout.setVisibility(View.GONE);
                        }

                        Intent next = new Intent();
                        next.setClass(FormActivity.this, SuccessActivity.class);
                        next.putExtra("tag", 0);
                        next.putExtra("pw", et2.getText().toString());
                        startActivity(next);
                        finish();
                    }
                    else if(!data.getSuccess()){
                        MyToast.show(FormActivity.this,data.getMessage());
                    }
                    closeProgressDialog();
                }
                else {
                    BaseApp.changeUrl(FormActivity.this, new BaseApp.OnChangeUrlListener() {
                        @Override
                        public void changeSuccess() {
                            changePw(tag);
                        }

                        @Override
                        public void changeFail() {}
                    });
                }
                pressed = false;
            }
        };
        task.setOnError(new MyAsyncTask.OnError() {

            @Override
            public void handleError(Exception e) {
                closeProgressDialog();
                mIsError = true;
            }
        });
        task.executeTask();

    }
    private void getId(){
        et1 = (EditText) findViewById(R.id.cur_pw);
        et2 = (EditText) findViewById(R.id.new_pw);
        et3 = (EditText) findViewById(R.id.re_new_pw);
        err1 = (LinearLayout) findViewById((R.id.error1));
        err2 = (LinearLayout) findViewById((R.id.error2));
        err3 = (LinearLayout) findViewById((R.id.error3));
        errText1 = (TextView) findViewById((R.id.error_pw1));
        errText2 = (TextView) findViewById((R.id.error_pw2));
        errText3 = (TextView) findViewById((R.id.error_pw3));
        errCross1 = (ImageView) findViewById((R.id.error_cross1));
        errCross2 = (ImageView) findViewById((R.id.error_cross2));
        errCross3 = (ImageView) findViewById((R.id.error_cross3));
        rl2 = (RelativeLayout) findViewById(R.id.rl2);
        rl4 = (RelativeLayout) findViewById(R.id.rl4);
        rl6 = (RelativeLayout) findViewById(R.id.rl6);
        imgBack = (ImageView) findViewById(R.id.imgBack);
        title = (TextView) findViewById(R.id.title);
        pwr = (TextView) findViewById(R.id.pw_remark);
        ll = (LinearLayout) findViewById(R.id.confirm_change);
        llLogout = (LinearLayout) findViewById(R.id.logout_change);
        tvLogout = (TextView) findViewById(R.id.logout_text);

        if (et1 != null)
            et1.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    err1.setVisibility(View.GONE);
                    errCross1.setVisibility(View.GONE);
                    rl2.setBackgroundResource(0);
                    rl2.setBackgroundColor(Color.parseColor("#f2f2f2"));
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

        if (et2 != null) {
            et2.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    err2.setVisibility(View.GONE);
                    errCross2.setVisibility(View.GONE);
                    rl4.setBackgroundResource(0);
                    rl4.setBackgroundColor(Color.parseColor("#f2f2f2"));
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }

        if (et3 != null) {
            et3.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    err3.setVisibility(View.GONE);
                    errCross3.setVisibility(View.GONE);
                    rl6.setBackgroundResource(0);
                    rl6.setBackgroundColor(Color.parseColor("#f2f2f2"));
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }
    }
    public void addCardToShared(Map<String, String> card){

        SharedPreferences mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        List<Map<String,String>> cards = new ArrayList<>();

        Gson gson = new Gson();
        String json = mySharedPreferences.getString("cards", null);
        Type type = new TypeToken<ArrayList<Map<String, String>>>() {}.getType();
        cards = gson.fromJson(json, type);

        cards.add(cards.size() - 1, card);

        String json2 = gson.toJson(cards);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putString("cards", json2);
        editor.commit();

    }

    private void showProgressDialog(String loadingMessage){
        try {
            progressDialog = DialogUtil.getProgressDialog(FormActivity.this, loadingMessage);
            progressDialog.show();
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

    private boolean checkChinese(String inputStr){
        inputStr = inputStr.replace(" ","");
        char[] ch = inputStr.toCharArray();
        for (int i = 0; i < ch.length; i++) {
            char c = ch[i];
            if (isChinese(c)) {
                if(isChinesePunctuation(c)){
                    return false;
                }else{
                    continue;
                }
            }else{
                return false;
            }
        }
        return true;
    }

    private boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION) {
            return true;
        }
        return false;
    }

    private boolean isChinesePunctuation(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_FORMS) {
            return true;
        } else {
            return false;
        }
    }

    private void getProvinceInfo(){
        mIsError = false;
        MyAsyncTask<List<ProvinceInfo>> task = new MyAsyncTask<List<ProvinceInfo>>(FormActivity.this) {
            @Override
            public List<ProvinceInfo> callService() throws IOException, JsonParseException, BizException, ServiceException {
                return new WithdrawService().getProvince();
            }

            @Override
            public void onLoaded(final List<ProvinceInfo> result) throws Exception {
                if(!mIsError){
                    provinceInfos = result;
                    provincename = new ArrayList<>();
                    for (ProvinceInfo provinceInfo : result) {
                        provincename.add(provinceInfo.getProvinceName());
                    }
                    provincename.add(0,"请选择");
                    ArrayAdapter<String> ProvinceAdapter
                            = new ArrayAdapter<String>(FormActivity.this, android.R.layout.simple_spinner_dropdown_item, provincename);
                    provinceSpinner.setAdapter(ProvinceAdapter);
                    provinceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            int pro = 0;
                            for(ProvinceInfo provinceInfo : result){
                                if(provinceInfo.getProvinceName().equals(provincename.get(position))){
                                    pro = provinceInfo.getProvinceId();
                                    Log.i("wxj","bank province");
                                    break;
                                }
                            }
                            getCity(pro);
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                }
            }
        };
        task.executeTask();
        task.setOnError(new MyAsyncTask.OnError() {
            @Override
            public void handleError(Exception paramException) {
                mIsError = true;
            }
        });
    }

    private void getCity(final int provinceid){
        mIsError = false;
        MyAsyncTask<List<CityInfo>> task = new MyAsyncTask<List<CityInfo>>(FormActivity.this) {
            @Override
            public List<CityInfo> callService() throws IOException, JsonParseException, BizException, ServiceException {
                return new WithdrawService().getCity(provinceid);
            }

            @Override
            public void onLoaded(List<CityInfo> result) throws Exception {
                if(!mIsError){
                    cityInfos = result;
                    cityname = new ArrayList<>();
                    for (CityInfo cityInfo : result) {
                        cityname.add(cityInfo.getCityName());
                    }
                    ArrayAdapter<String> cityAdapter
                            = new ArrayAdapter<String>(FormActivity.this, android.R.layout.simple_spinner_dropdown_item, cityname);
                    citySpinner.setAdapter(cityAdapter);
                    citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                }
            }
        };
        task.executeTask();
        task.setOnError(new MyAsyncTask.OnError() {
            @Override
            public void handleError(Exception paramException) {
                mIsError = true;
            }
        });
    }
    private void addquick(final Spinner list_bank, final int btnType){
        int procinceid = 0;
        for(ProvinceInfo provinceInfo : provinceInfos){
            if(provinceInfo.getProvinceName().equals((String) provinceSpinner.getSelectedItem())){
                procinceid = provinceInfo.getProvinceId();
                Log.i("wxj","bank province");
                break;
            }
        }
        int cityid = 0;
        for(CityInfo cityInfo : cityInfos){
            if(cityInfo.getCityName().equals((String) citySpinner.getSelectedItem())){
                cityid = cityInfo.getCityId();
                Log.i("wxj","bank province");
                break;
            }
        }
        showProgressDialog("正在提交申请!");
        mIsError = false;
        MyAsyncTask<Response> task = new MyAsyncTask<Response>(FormActivity.this) {
            @Override
            public Response callService() throws IOException, JsonParseException, BizException, ServiceException {
                return new WithdrawService().addCardNew((bankTypeId.get(list_bank.getSelectedItemPosition())).intValue(), list_bank.getSelectedItem().toString(),et2.getText().toString(), et1.getText().toString()
                ,branchbank.getText().toString(),citySpinner.getSelectedItem().toString(),telephoneNo.getText().toString(),provinceSpinner.getSelectedItem().toString());
            }

            @Override
            public void onLoaded(Response data) throws Exception {
                if(FormActivity.this == null || FormActivity.this.isFinishing())
                    return;
                closeProgressDialog();
                if(!mIsError){
                    if (data.getSuccess()) {
                        Intent next = new Intent(FormActivity.this, SuccessActivity.class);
                        next.putExtra("tag", 2);
                        next.putExtra("name", et1.getText().toString());
                        next.putExtra("bank", list_bank.getSelectedItem().toString());
                        next.putExtra("card", et2.getText().toString());
                        next.putExtra("btn_type", btnType);
                        startActivity(next);
                        finish();
                    }else{
                        MyToast.show(FormActivity.this,data.getMessage());
                    }
                }else{
                    BaseApp.getAppBean().resetApiUrl(FormActivity.this);
                    Toast.makeText(FormActivity.this, "抱歉，网络异常，请查看记录是否已经添卡", Toast.LENGTH_LONG).show();
                    BaseApp.getAppBean().setRetryCount(0);
                }
            }
        };
        task.executeTask();
        task.setOnError(new MyAsyncTask.OnError() {
            @Override
            public void handleError(Exception paramException) {
                mIsError = true;
            }
        });
    }

    private void withdrawquick(final int bankId, final String bank){
        mIsError = false;
        MyAsyncTask<Response<BankInfo>> task = new MyAsyncTask<Response<BankInfo>>(FormActivity.this) {
            @Override
            public Response<BankInfo> callService() throws IOException, JsonParseException, BizException, ServiceException {
                return new WithdrawService().withdrawQuick(bankId, bank, Double.valueOf(Double.parseDouble(et1.getText().toString())), et2.getText().toString());
            }

            @Override
            public void onLoaded(Response<BankInfo> data) throws Exception {
                if(FormActivity.this == null || FormActivity.this.isFinishing())
                    return;
                closeProgressDialog();
                if (!mIsError) {
                    if (data.getSuccess()) {
                        Intent next = new Intent(FormActivity.this, SuccessActivity.class);
                        next.putExtra("tag", 3);
                        next.putExtra("amount", Double.parseDouble(et1.getText().toString()));
                        startActivity(next);
                        finish();
                    } else {
                        MyToast.show(FormActivity.this, data.getMessage());
                    }
                } else {
                    BaseApp.getAppBean().resetApiUrl(FormActivity.this);
                    Toast.makeText(FormActivity.this, "抱歉，网络异常，请查看记录是否已经提款", Toast.LENGTH_LONG).show();
                    BaseApp.getAppBean().setRetryCount(0);
                }
                pressed = false;
            }
        };
        task.executeTask();
        task.setOnError(new MyAsyncTask.OnError() {
            @Override
            public void handleError(Exception paramException) {
                mIsError = true;
            }
        });

    }

    private void quickupdate(final Spinner list_bank, final int banktypeid, final String bankname){
        showProgressDialog("正在提交申请!");
        mIsError = false;
        MyAsyncTask<Response> task = new MyAsyncTask<Response>(FormActivity.this) {
            @Override
            public Response callService() throws IOException, JsonParseException, BizException, ServiceException {
                return new WithdrawService().quickUpdate(banktypeid, bankname,et2.getText().toString(), et1.getText().toString()
                        ,branchbank.getText().toString(),citySpinner.getSelectedItem().toString(),telephoneNo.getText().toString(),provinceSpinner.getSelectedItem().toString());
            }

            @Override
            public void onLoaded(Response data) throws Exception {
                if(FormActivity.this == null || FormActivity.this.isFinishing())
                    return;
                closeProgressDialog();
                if(!mIsError){
                    if (data.getSuccess()) {
                        Intent next = new Intent(FormActivity.this, SuccessActivity.class);
                        next.putExtra("tag", 2);
                        next.putExtra("name", et1.getText().toString());
                        next.putExtra("bank", list_bank.getSelectedItem().toString());
                        next.putExtra("card", et2.getText().toString());
                        startActivity(next);
                        finish();
                    }else{
                        MyToast.show(FormActivity.this,data.getMessage());
                    }
                }else{
                    BaseApp.getAppBean().resetApiUrl(FormActivity.this);
                    Toast.makeText(FormActivity.this, "抱歉，网络异常，请查看记录是否已经添卡", Toast.LENGTH_LONG).show();
                    BaseApp.getAppBean().setRetryCount(0);
                }
            }
        };
        task.executeTask();
        task.setOnError(new MyAsyncTask.OnError() {
            @Override
            public void handleError(Exception paramException) {
                mIsError = true;
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        Intent intent = getIntent();
        int tag = intent.getIntExtra("tag", -1);
        SharedPreferences sharedPreferences = getSharedPreferences(CommonConfig.KEY_DATA, MODE_PRIVATE);
        if (keyCode == KeyEvent.KEYCODE_BACK && tag == 0 && sharedPreferences.getBoolean(CommonConfig.KEY_DATA_STUPID_LOGIN_PASSWORD_FLAG, false)) {
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}