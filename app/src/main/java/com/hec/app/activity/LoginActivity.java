package com.hec.app.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.hec.app.BuildConfig;
import com.hec.app.R;
import com.hec.app.activity.base.BaseApp;
import com.hec.app.adapter.RmbAdapter;
import com.hec.app.config.CommonConfig;
import com.hec.app.config.UrlConfig;
import com.hec.app.entity.AllPlayConfig;
import com.hec.app.entity.BasicDataInfo;
import com.hec.app.entity.BizException;
import com.hec.app.entity.CustomerInfo;
import com.hec.app.entity.LogonInfoNew;
import com.hec.app.entity.Response;
import com.hec.app.framework.cache.MyFileCache;
import com.hec.app.framework.widget.MyToast;
import com.hec.app.lottery.LotteryConfig;
import com.hec.app.util.BitmapUtil;
import com.hec.app.util.CustomerAccountManager;
import com.hec.app.util.DialogUtil;
import com.hec.app.util.DisplayUtil;
import com.hec.app.util.IntentUtil;
import com.hec.app.util.MyAsyncTask;
import com.hec.app.util.StringUtil;
import com.hec.app.util.TestUtil;
import com.hec.app.webservice.AccountService;
import com.hec.app.webservice.BaseService;
import com.hec.app.webservice.DownLoadService;
import com.hec.app.webservice.LotteryService;
import com.hec.app.webservice.ServiceException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import wxj.fliplog.View.LogFloatingActionButton;

public class LoginActivity extends AppCompatActivity {
    public static final String ACCOUNT_CUSTOMER_INFO = "CustomerInfo";
    public static final String ACCOUNT_CUSTOMER_INFO_DETAIL = "ACCOUNT_CUSTOMER_INFO_DETAIL";
    private ProgressDialog mProgressDialog;
    private AutoCompleteTextView txtUserName;
    private EditText txtPassword;
    private TextView text_findpassword, text_chat;
    private ImageView btn_password_delete, btn_dropdown_arrow;
    private RelativeLayout btn_Login;
    private Boolean mIsError;
    private ArrayList<String> userList = new ArrayList<String>();
    private String currentUser = new String();
    private ProgressDialog mLoadingDialog;
    private AlertDialog mRetryDialog;
    private CheckBox rmb;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private DownLoadService.MyBinder myBinder;
    private static final int REQUEST_CODE = 0x11;
    long requestTime, responseTime;
    public int count = 0;
    private LinearLayout captcha_layout;
    private EditText captcha_text;
    private ImageView captcha_img;
    private int RETRY_COUNT = 0;
    private SharedPreferences retry;
    private SharedPreferences token;
    private SharedPreferences servertime;
    private SharedPreferences userBestWin;
    private SharedPreferences keyData;
    private int LOADING_COUNT = 0;
    private Spinner URL_spinner;
    private RelativeLayout login_back;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        login_back = (RelativeLayout) findViewById(R.id.login_back);

        cutBitmap();
        servertime = getSharedPreferences("servertime", MODE_PRIVATE);
        BaseApp.SERVER_TIME_DIFF = servertime.getLong("servertimes", 0);
        userBestWin = getSharedPreferences("userbestwin", MODE_PRIVATE);
        token = getSharedPreferences("token", MODE_PRIVATE);
        keyData = getSharedPreferences(CommonConfig.KEY_DATA, MODE_PRIVATE);

        SharedPreferences.Editor editor = keyData.edit();
        editor.putBoolean(CommonConfig.KEY_IS_LOGIN, true);
        editor.apply();

        if (new LotteryService().getCachedBasicDataInfo(LotteryConfig.PLAY_MODE.CLASSIC) == null) {
            getBasicDataWhileNoCache();
        } else {
            if ("".equals(token.getString("tokens", ""))) {
                servertime = getSharedPreferences(CommonConfig.KEY_SERVERTIME, MODE_PRIVATE);
                BaseApp.SERVER_TIME_DIFF = servertime.getLong(CommonConfig.KEY_SERVERTIME_SERVER_TIMES, 0);
                retry = getSharedPreferences(CommonConfig.KEY_RETRY, MODE_PRIVATE);
                RETRY_COUNT = retry.getInt(CommonConfig.KEY_RETRY_COUNT, 0);
                userBestWin = getSharedPreferences(CommonConfig.KEY_USERBESTWIN, MODE_PRIVATE);
                token = getSharedPreferences(CommonConfig.KEY_TOKEN, MODE_PRIVATE);
            }
            if (new LotteryService().getCachedBasicDataInfo(LotteryConfig.PLAY_MODE.CLASSIC) == null) {
                getBasicDataWhileNoCache();
            } else if (new LotteryService().getCachedAllPlayConfigInfo() == null) {
                getAllPlayConfigWhileNoCache();
            } else {
                quickLogin();
            }
        }

        TestUtil.print("LoginActivity");
        System.gc();
        readUserName();
        setupTxtUsername();

        txtPassword = (EditText) findViewById(R.id.txtPwd);

        rmb = (CheckBox) findViewById(R.id.rmb);

        captcha_layout = (LinearLayout) findViewById(R.id.captcha_layout);
        captcha_text = (EditText) findViewById(R.id.captcha_text);
        captcha_img = (ImageView) findViewById(R.id.captcha_img);
        if (RETRY_COUNT >= 5) {
            captcha_layout.setVisibility(View.VISIBLE);
            gotoGetCaptcha();
        }
        captcha_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoGetCaptcha();
            }
        });

        btn_password_delete = (ImageView) findViewById(R.id.btn_password_delete);
        btn_dropdown_arrow = (ImageView) findViewById(R.id.btn_history);
        text_findpassword = (TextView) findViewById(R.id.text_findpassword);
        text_chat = (TextView) findViewById(R.id.text_chat);
        try {
            txtUserName.setText(userList.size() > 0 ? userList.get(0) : "");
            if (userList.size() > 0) {
                rmb.setChecked(keyData.getBoolean(CommonConfig.KEY_DATA_RMB, false));
                if (rmb.isChecked()) {
                    txtPassword.setText(keyData.getString(userList.get(0), ""));
                } else {
                    txtPassword.setText("");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        btn_dropdown_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtUserName.showDropDown();
            }
        });
        txtUserName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentUser = txtUserName.getText().toString();
            }

        });

        SpannableString content = new SpannableString(getString(R.string.activity_login_findpassword));
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        text_findpassword.setText(content);
        text_findpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(LoginActivity.this, FindPasswordActivity.class);
                startActivity(it);
                overridePendingTransition(R.anim.push_left_in_no_alpha, R.anim.push_left_out_no_alpha);
            }
        });
        text_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(LoginActivity.this, WebchatActivity.class);
                it.putExtra(CommonConfig.WEBCHAT_TYPE, CommonConfig.WEBCHAT_TYPE_NORMAL);
                startActivity(it);
                overridePendingTransition(R.anim.push_left_in_no_alpha, R.anim.push_left_out_no_alpha);
            }
        });
        btn_password_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userList.contains(txtUserName)) {
                    SharedPreferences.Editor editor = keyData.edit();
                    editor.putString(txtUserName.getText().toString(), "");
                    editor.apply();
                }
                txtPassword.setText("");
            }
        });

        setupLoginBtn();

        //checkUpdate();

        if (BuildConfig.DEBUG || BuildConfig.SIT || BuildConfig.UAT) {
            URL_spinner = (Spinner) findViewById(R.id.debug_url_choose);
            URL_spinner.setVisibility(View.VISIBLE);
            ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(LoginActivity.this, android.R.layout.simple_spinner_dropdown_item, UrlConfig.URL_HOST_LIST);
            URL_spinner.setAdapter(adapter2);
            URL_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                    BaseService.RESTFUL_SERVICE_HOST = UrlConfig.URL_HOST_LIST.get(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

        }

        if (BuildConfig.DEBUG || BuildConfig.SIT || BuildConfig.UAT) {
            new LogFloatingActionButton.Builder(this)
                    .setPopupWindowHec()
                    .putHecInfo(
                            "app api:" + BaseService.RESTFUL_SERVICE_HOST,
                            "聊天室api:" + BaseService.CHAT_URL,
                            "slot cdn:" + BaseService.SLOT_CDN_URL,
                            "slot back" + BaseService.SLOT_URL)
                    .build();
        }

        registerKeyboardListener();
        //TODO registerUserAccountTxtListener
        //TODO addRemoveUsername input action
        //TODO checkbox ClickListener
    }

    private void setupLoginBtn() {
        btn_Login = (RelativeLayout) findViewById(R.id.btnUserLogin);
        btn_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isUserNameNotNull(txtUserName.getText().toString().trim())) {
                    Toast.makeText(v.getContext(), R.string.error_message_username_null, Toast.LENGTH_SHORT).show();
                }
                if (!isUserPasswordNotNull(txtPassword.getText().toString().trim())) {
                    Toast.makeText(v.getContext(), R.string.error_message_password_null, Toast.LENGTH_SHORT).show();
                }
                loginUser();
            }
        });
    }

    private void setupTxtUsername() {
        txtUserName = (AutoCompleteTextView) findViewById(R.id.txtUserName);
        txtUserName.setThreshold(1);

        final RmbAdapter adapter = new RmbAdapter(this, R.layout.simple_dropdown_item, userList);
        adapter.setListener(new RmbAdapter.BtnClickListener() {
            @Override
            public void onBtnClick(int position) {
                removeUserNameAndPassword(userList.get(position));
                adapter.notifyDataSetChanged();
                txtUserName.setText("");
                txtUserName.dismissDropDown();
            }
        });
        if (BuildConfig.DEBUG || BuildConfig.SIT || BuildConfig.UAT) {
            ImageView imageView = (ImageView) findViewById(R.id.backdoor_img);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
        txtUserName.setAdapter(adapter);
        txtUserName.setDropDownHeight(DisplayUtil.getPxByDp(this, 160));
        txtUserName.setDropDownWidth(DisplayUtil.getPxByDp(this, 912 / 3));
        txtUserName.setDropDownVerticalOffset(DisplayUtil.getPxByDp(this, 6));
        txtUserName.setDropDownAnchor(R.id.relativeLayout);
        txtUserName.setDropDownBackgroundResource(R.drawable.dropdown_menu_shape);
    }

    private void registerKeyboardListener() {
        final View placeHolder = findViewById(R.id.placeHolder);
        login_back.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int heightDiff = login_back.getRootView().getHeight() - login_back.getHeight();
                try {
                    if (heightDiff > DisplayUtil.getPxByDp(LoginActivity.this, 200)) { // if more than 200 dp, it's probably a keyboard...
                        placeHolder.setVisibility(View.GONE);
                    } else {
                        placeHolder.setVisibility(View.VISIBLE);
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    @Override
    public void onPause() {
//        saveUserName(spUserName);
        super.onPause();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // save file
                    MyFileCache.install(getApplicationContext());
                } else {
                    Toast.makeText(this, R.string.error_message_permissions, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, R.string.error_message_permissions, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void saveUserName(String username) {
        userList.add(0, username);
        for (int i = 1; i < userList.size(); i++) {
            if (userList.get(i).compareTo(username) == 0) {
                userList.remove(i);
                break;
            }

        }
        SharedPreferences.Editor editor = keyData.edit();
        editor.putString(CommonConfig.KEY_DATA_USER_NAMES, new Gson().toJson(userList));
        editor.commit();
    }

    public void savePassword(String username, String password) {
        SharedPreferences.Editor editor = keyData.edit();
        editor.putString(username, password);
        editor.commit();
    }

    public void saveRmbStatus(boolean rmb) {
        SharedPreferences.Editor editor = keyData.edit();
        editor.putBoolean(CommonConfig.KEY_DATA_RMB, rmb);
        editor.commit();
    }

    public void removeUserNameAndPassword(String username) {

        for (int i = 0; i < userList.size(); i++) {
            if (userList.get(i).compareTo(username) == 0) {
                userList.remove(i);
                break;
            }
        }

        SharedPreferences.Editor editor = keyData.edit();
        editor.putString(CommonConfig.KEY_DATA_USER_NAMES, new Gson().toJson(userList));
        editor.remove(username);
        editor.commit();
    }

    public void readUserName() {
        userList = new Gson().fromJson(keyData.getString(CommonConfig.KEY_DATA_USER_NAMES, "[]"), ArrayList.class);
        TestUtil.print(userList.toString());
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            if (this.getCurrentFocus().getId() == R.id.txtUserName) {
                txtPassword.requestFocus();
            } else if (this.getCurrentFocus().getId() == R.id.txtPwd) {
                loginUser();
            }
        }
        return super.onKeyUp(keyCode, event);
    }

//    private void showProgressDialog() {
//        try {
//            mProgressDialog = DialogUtil.getProgressDialog(this, "正在登录");
//            mProgressDialog.show();
//        } catch (Exception e) {
//
//        }
//    }
//
//    private void closeProgressDialog() {
//        try {
//            if (mProgressDialog != null && mProgressDialog.isShowing()) {
//                mProgressDialog.dismiss();
//            }
//        } catch (Exception e) {
//
//        }
//    }

    private boolean isUserNameNotNull(String username) {
        return username != null && !StringUtil.isEmpty(username);
    }

    private boolean isUserPasswordNotNull(String pwd) {
        return pwd != null && !StringUtil.isEmpty(pwd);
    }

    private void loginUser() {
        LOADING_COUNT = 0;
        String useName = txtUserName.getText().toString().trim();
        String password = txtPassword.getText().toString().trim();
        if (!isUserNameNotNull(useName)) {
            txtUserName.setText(null);
            txtUserName.requestFocus();
            return;
        }

        if (!isUserPasswordNotNull(password)) {
            txtPassword.setText(null);
            txtPassword.requestFocus();
            return;
        }

        // 点击登录时关闭输入法。
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(txtPassword.getWindowToken(), 0);
        Log.i("speed", "loginuser");
        if (RETRY_COUNT < 5) {
            Log.i("speed", "normal");
            login(useName, password);
        } else {
            String captcha = captcha_text.getText().toString();
            if (captcha.equals("")) {
                MyToast.show(LoginActivity.this, getString(R.string.error_message_captcha_input));
            } else {
                loginWithCaptcha(useName, password, BaseApp.CAPTCHA_KEY, captcha);
            }
        }
    }

    private void login(final String userName, final String password) {
        mIsError = false;
        LOADING_COUNT++;
        if ("HKAA4TestBoom".equals(userName)) {
            MyToast.show(LoginActivity.this, "进入开发模式! ");
            txtUserName.setText("");
            BaseService.RESTFUL_SERVICE_HOST = "http://bigbrothers.info:19088/";
            BaseApp.CHEN_MODE = true;
            return;
        }

        showLoading(String.format(getString(R.string.loading_message_line), LOADING_COUNT), false);
        //btn_Login.setClickable(false);
        MyAsyncTask<Response<LogonInfoNew>> task = new MyAsyncTask<Response<LogonInfoNew>>(LoginActivity.this) {

            @Override
            public Response<LogonInfoNew> callService() throws IOException,
                    JsonParseException, BizException, ServiceException {
                requestTime = System.currentTimeMillis();
                Log.i("speed", "in login");
                return new AccountService().logOnWithCaptcha(userName, password, "", "");
            }

            @Override
            public void onLoaded(Response<LogonInfoNew> result) throws Exception {
                closeLoading();
                TestUtil.print("done");
                btn_Login.setClickable(true);
                closeLoading();
                if (!mIsError) {
                    responseTime = System.currentTimeMillis();
                    Log.i("speed", "login url " + BaseService.RESTFUL_SERVICE_HOST);
                    if (result.getSuccess()) {
                        RETRY_COUNT = 0;
                        Log.i("store", "in get success");
                        CustomerInfo customerInfo = new CustomerInfo();
                        customerInfo.setAuthenticationKey(result.getData().getKey());
                        customerInfo.setUserID(String.valueOf(result.getData().getUserID()));
                        customerInfo.setUserName(result.getData().getUserName());
                        customerInfo.setInfoComplete(result.getData().getInfoComplete());
                        if (result.getData().getBankShow() != null) {
                            customerInfo.setBankShow(new Gson().fromJson(result.getData().getBankShow(), CustomerInfo.BankShow.class));
                        }
                        saveLoginResultInfoToLocalStore(customerInfo);
                        SharedPreferences.Editor editor = token.edit();
                        editor.putString(CommonConfig.KEY_TOKEN_TOKENS, result.getData().getKey());
                        editor.putInt(CommonConfig.KEY_TOKEN_USER_ID, result.getData().getUserID());
                        editor.putString(CommonConfig.KEY_TOKEN_USER_NAME, result.getData().getUserName());
                        editor.putBoolean(CommonConfig.KEY_TOKEN_INFOCOMPLETE, result.getData().getInfoComplete());
                        editor.putString(CommonConfig.KEY_TOKEN_BANK_SHOW, result.getData().getBankShow());
                        editor.commit();
                        SharedPreferences.Editor editor3 = servertime.edit();
                        BaseApp.SERVER_TIME_DIFF = System.currentTimeMillis() -
                                Long.parseLong(result.getData().getCurrentTime().substring(6, 19));
                        Log.i("wxj", "3 times: " + System.currentTimeMillis() + " ss "
                                + Long.parseLong(result.getData().getCurrentTime().substring(6, 19)));
                        Log.i("wxj", "timesss: " + BaseApp.SERVER_TIME_DIFF);
                        editor3.putLong(CommonConfig.KEY_SERVERTIME_SERVER_TIMES, BaseApp.SERVER_TIME_DIFF);
                        editor3.commit();
                        Log.i("store", "normal login");
                        Class<?> loginBeforeCls = BaseApp.instance().getLoginBeforeCls();
                        SharedPreferences.Editor editor4 = userBestWin.edit();
                        if (result.getData().getUserBestPrize() != null) {
                            editor4.putString(CommonConfig.KEY_USERBESTWIN_LOTTERY_TYPE, result.getData().getUserBestPrize().getLotteryType());
                            editor4.putString(CommonConfig.KEY_USERBESTWIN_AMOUNT, result.getData().getUserBestPrize().getWinMoney());
                        } else {
                            editor4.putString(CommonConfig.KEY_USERBESTWIN_LOTTERY_TYPE, "无");
                            editor4.putString(CommonConfig.KEY_USERBESTWIN_AMOUNT, "0");
                        }
                        editor4.commit();
//                        Bundle bundle = new Bundle();
//                        bundle.putSerializable("balanceInfo",result.getData().getBalanceInfo());
//                        bundle.putSerializable("userBestPrize",result.getData().getUserBestPrize());
                        if (loginBeforeCls != null) {
                            BaseApp.instance().setLoginBeforeCls(null);
                            IntentUtil.redirectToNextActivity(LoginActivity.this, loginBeforeCls);
                            LoginActivity.this.finish();
                        } else {
                            IntentUtil.redirectToNextActivity(LoginActivity.this, HomeActivity.class);
                            LoginActivity.this.finish();
                            saveUserName(userName);
                            savePassword(userName, password);
                            saveRmbStatus(rmb.isChecked());
                        }
                    } else {
                        Log.i("speed", "login failed " + RETRY_COUNT);
                        if (!"".equals(getRetryNum(result.getMessage()))) {
                            RETRY_COUNT = Integer.parseInt(getRetryNum(result.getMessage()));
                        }
                        if (RETRY_COUNT >= 5) {
                            captcha_layout.setVisibility(View.VISIBLE);
                            MyToast.show(LoginActivity.this, String.format(getString(R.string.error_message_captcha_conut), CustomMsg(result.getMessage())));
                            gotoGetCaptcha();
                        } else {
                            MyToast.show(LoginActivity.this, CustomMsg(result.getMessage()));
                        }
                    }
                    SharedPreferences.Editor editor = retry.edit();
                    editor.putInt(CommonConfig.KEY_RETRY_COUNT, RETRY_COUNT);
                    editor.commit();
                } else {
                    BaseApp.changeUrl(LoginActivity.this, new BaseApp.OnChangeUrlListener() {
                        @Override
                        public void changeSuccess() {
                            login(userName, password);
                        }

                        @Override
                        public void changeFail() {
                        }
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

    private void loginWithCaptcha(final String username, final String pwd, final String verifyID, final String captcha) {
        mIsError = false;
        LOADING_COUNT++;

        showLoading(String.format(getString(R.string.loading_message_line), LOADING_COUNT), false);
        //btn_Login.setClickable(false);
        MyAsyncTask<Response<LogonInfoNew>> task = new MyAsyncTask<Response<LogonInfoNew>>(LoginActivity.this) {
            @Override
            public Response<LogonInfoNew> callService() throws IOException, JsonParseException, BizException, ServiceException {
                return new AccountService().logOnWithCaptcha(username, pwd, verifyID, captcha);
            }

            @Override
            public void onLoaded(Response<LogonInfoNew> result) throws Exception {
                closeLoading();
                btn_Login.setClickable(true);
                if (!mIsError) {
                    if (result.getSuccess()) {
                        RETRY_COUNT = 0;
                        SharedPreferences.Editor editor = retry.edit();
                        editor.putInt(CommonConfig.KEY_RETRY_COUNT, RETRY_COUNT);
                        editor.commit();
                        CustomerInfo customerInfo = new CustomerInfo();
                        customerInfo.setAuthenticationKey(result.getData().getKey());
                        customerInfo.setUserID(String.valueOf(result.getData().getUserID()));
                        customerInfo.setUserName(result.getData().getUserName());
                        customerInfo.setInfoComplete(result.getData().getInfoComplete());
                        if (result.getData().getBankShow() != null) {
                            customerInfo.setBankShow(new Gson().fromJson(result.getData().getBankShow(), CustomerInfo.BankShow.class));
                        }
                        saveLoginResultInfoToLocalStore(customerInfo);
                        SharedPreferences.Editor editor2 = token.edit();
                        editor2.putString(CommonConfig.KEY_TOKEN_TOKENS, result.getData().getKey());
                        editor2.putInt(CommonConfig.KEY_TOKEN_USER_ID, result.getData().getUserID());
                        editor2.putString(CommonConfig.KEY_TOKEN_USER_NAME, result.getData().getUserName());
                        editor2.putBoolean(CommonConfig.KEY_TOKEN_INFOCOMPLETE, result.getData().getInfoComplete());
                        editor2.putString(CommonConfig.KEY_TOKEN_BANK_SHOW, result.getData().getBankShow());
                        editor2.commit();
                        BaseApp.SERVER_TIME_DIFF = System.currentTimeMillis() -
                                Long.parseLong(result.getData().getCurrentTime().substring(6, 19));
                        SharedPreferences.Editor editor3 = servertime.edit();
                        editor3.putLong(CommonConfig.KEY_SERVERTIME_SERVER_TIMES, BaseApp.SERVER_TIME_DIFF);
                        editor3.commit();
                        Class<?> loginBeforeCls = BaseApp.instance().getLoginBeforeCls();
                        SharedPreferences.Editor editor4 = userBestWin.edit();
                        editor4.putString(CommonConfig.KEY_USERBESTWIN_LOTTERY_TYPE, result.getData().getUserBestPrize().getLotteryType());
                        editor4.putString(CommonConfig.KEY_USERBESTWIN_AMOUNT, result.getData().getUserBestPrize().getWinMoney());
                        editor4.commit();
                        //Bundle bundle = new Bundle();
                        //bundle.putSerializable("balanceInfo",result.getData().getBalanceInfo());
                        //bundle.putSerializable("userBestPrize",result.getData().getUserBestPrize());
                        if (loginBeforeCls != null) {
                            BaseApp.instance().setLoginBeforeCls(null);
                            IntentUtil.redirectToNextActivity(LoginActivity.this, loginBeforeCls);
                            LoginActivity.this.finish();
                        } else {
                            IntentUtil.redirectToNextActivity(LoginActivity.this, HomeActivity.class);
//                            Intent intent = new Intent();
//                            intent.putExtras(bundle);
//                            intent.setClass(LoginActivity.this,HomeActivity.class);
//                            startActivity(intent);
                            LoginActivity.this.finish();
                            saveUserName(username);
                            savePassword(username, pwd);
                            saveRmbStatus(rmb.isChecked());
                        }
                    } else {
                        if (!"".equals(getRetryNum(result.getMessage()))) {
                            RETRY_COUNT = Integer.parseInt(getRetryNum(result.getMessage()));
                        }
                        MyToast.show(LoginActivity.this, CustomMsg(result.getMessage()));
                        gotoGetCaptcha();
                    }
                } else {
                    BaseApp.changeUrl(LoginActivity.this, new BaseApp.OnChangeUrlListener() {
                        @Override
                        public void changeSuccess() {
                            loginWithCaptcha(username, pwd, verifyID, captcha);
                        }

                        @Override
                        public void changeFail() {
                        }
                    });
                }
            }
        };
        task.setOnError(new MyAsyncTask.OnError() {
            @Override
            public void handleError(Exception paramException) {
                mIsError = true;
            }
        });
        task.executeTask();
    }

    private void saveLoginResultInfoToLocalStore(CustomerInfo customerInfo) {
        SharedPreferences mMyAccountPreferences = this.getSharedPreferences(
                LoginActivity.ACCOUNT_CUSTOMER_INFO,
                MODE_PRIVATE);
        CustomerInfo customer = new CustomerInfo();
        customer.setUserName(customerInfo.getUserName());
        customerInfo.setIsRemember(false);
        mMyAccountPreferences
                .edit()
                .putString(LoginActivity.ACCOUNT_CUSTOMER_INFO_DETAIL,
                        new Gson().toJson(customer)).commit();
        CustomerAccountManager.getInstance().setCustomer(customerInfo);
    }

    private void killProcessAndExit() {
        moveTaskToBack(true);
        if (BaseApp.activityList.size() != 0) {
            for (Activity activity : BaseApp.activityList) {
                if (activity != null) {
                    activity.finish();
                }
            }
        }
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(10);
    }

    private Dialog buildExitConfirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_leave_title);
        builder.setMessage(R.string.dialog_leave_message);
        builder.setPositiveButton(R.string.dialog_determine, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (retry != null) {
                    SharedPreferences.Editor editor = retry.edit();
                    editor.putInt(CommonConfig.KEY_RETRY_COUNT, RETRY_COUNT);
                    editor.commit();
                }
                killProcessAndExit();
            }
        });
        builder.setNegativeButton(R.string.dialog_cancel, null);

        return builder.create();
    }

    private boolean needConfirmWhenExit() {
        SharedPreferences settings = getBaseContext().getSharedPreferences(CommonConfig.KEY_SETTING_PREFERENCE, MODE_PRIVATE);
        return settings.getBoolean(CommonConfig.KEY_CONFIRM_WHEN_EXIT, true);
    }

    @Override
    public void onBackPressed() {
        if (needConfirmWhenExit()) {
            buildExitConfirmDialog().show();
        } else {
            killProcessAndExit();
        }
    }

    //    public void checkUpdate(){
//        if(BaseApp.getAppBean() != null){
//            try {
//                final int v = Integer.parseInt(BaseApp.getAppBean().getVersion());
//                final int cV = Integer.parseInt(BaseApp.instance().getVersionCode());
//                TestUtil.print("before invalid");
//                TestUtil.print("v:" + v + ",cV:" + cV);
//
//                if(cV < v){
//                    //final String downloadUrl = "http://ceshi.lisun1.com/app-29.apk";//
//                    final String downloadUrl = BaseApp.getAppBean().getAppUrl();
//                            AlertDialog.Builder dialog = new AlertDialog.Builder(LoginActivity.this)
//                            .setTitle("有新的版本")
//                            .setMessage("请更新后使用")
//                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    SharedPreferences sharedPreferences = getSharedPreferences("entrance",MODE_PRIVATE);
//                                    SharedPreferences.Editor editor = sharedPreferences.edit();
//                                    editor.putString("updatetime","");
//                                    editor.commit();
//                                    if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
//                                        MyToast.show(LoginActivity.this, "找不到存储卡，无法下载！");
//                                    }else if(new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/hec"+"/hec-"+v+".apk").exists()) {
//                                        Uri uri = Uri.fromFile(new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/hec"+"/hec-"+v+".apk"));
//                                        Intent intent = new Intent(Intent.ACTION_VIEW);
//                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                        intent.setDataAndType(uri, "application/vnd.android.package-archive");
//                                        startActivity(intent);
//                                    }else{
//                                        String filepath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/hec";
//                                        String filename = "hec-"+v+".apk";
//                                        startDownload(downloadUrl, filename, filepath);
//                                    }
//                                }
//                            }).setCancelable(false);
//
//                    if(BuildConfig.DEBUG){
//                        dialog.setNegativeButton("取消",null);
//                    }
//                    dialog.show();
//                }
//            }catch (Exception e){
//                TestUtil.print(e.getLocalizedMessage());
//            }
//        }
//    }
//
//
//    ServiceConnection connection;
//    public void startDownload(final String url, final String filename, final String path){
//        //do in backgroud
//
//        Intent startIntent = new Intent(this, DownLoadService.class);
//        connection = new ServiceConnection() {
//            @Override
//            public void onServiceConnected(ComponentName name, IBinder service) {
//                myBinder = (DownLoadService.MyBinder) service;
//                myBinder.startDownload(LoginActivity.this,filename,path,url);
//            }
//
//            @Override
//            public void onServiceDisconnected(ComponentName name) {
//
//            }
//        };
//        bindService(startIntent,connection,BIND_AUTO_CREATE);
//    }
    public void showLoading(String tips, Boolean cancelable) {
        closeLoading();
        try {
            if (mLoadingDialog == null) {
                mLoadingDialog = DialogUtil.getProgressDialog(this, tips);
            }
            mLoadingDialog.setMessage(tips);
            mLoadingDialog.setCancelable(cancelable);
            mLoadingDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void closeLoading() {

        try {
            if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                mLoadingDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void gotoGetCaptcha() {
        mIsError = false;
        captcha_img.setClickable(false);
        captcha_img.setVisibility(View.VISIBLE);
        MyAsyncTask<Bitmap> task = new MyAsyncTask<Bitmap>(LoginActivity.this) {
            @Override
            public Bitmap callService() throws IOException, JsonParseException, BizException, ServiceException {
                return new AccountService().getCaptcha();
            }

            @Override
            public void onLoaded(Bitmap result) throws Exception {
                if (!mIsError) {
                    captcha_img.setVisibility(View.VISIBLE);
                    captcha_img.setImageBitmap(result);
                    captcha_img.setClickable(true);
                    Log.i("speed", "get the captcha!!");
                } else {
                    captcha_img.setImageResource(R.mipmap.getcaptcha_button);
                    MyToast.show(LoginActivity.this, getString(R.string.error_message_captcha));
                }
            }
        };
        task.setOnError(new MyAsyncTask.OnError() {
            @Override
            public void handleError(Exception paramException) {
                mIsError = true;
            }
        });
        task.executeTask();
    }

    private String getRetryNum(String s) {
        Pattern pattern = Pattern.compile("\\d+");
        if (!s.equals("")) {
            Matcher matcher = pattern.matcher(s);
            while (matcher.find()) {
                return matcher.group(0);
            }
        }
        return "";
    }

    private void getBasicDataWhileNoCache() {
        showLoading(getString(R.string.loading_message_login), false);

        mIsError = false;
        Log.i("store", "getbasicnocache");
        MyAsyncTask<List<BasicDataInfo>> task = new MyAsyncTask<List<BasicDataInfo>>(LoginActivity.this) {
            @Override
            public List<BasicDataInfo> callService() throws IOException, JsonParseException, BizException, ServiceException {
                return new LotteryService().getBasicData("");
            }

            @Override
            public void onLoaded(List<BasicDataInfo> result) throws Exception {
                closeLoading();
                if (mIsError) {
                    BaseApp.changeUrl(LoginActivity.this, new BaseApp.OnChangeUrlListener() {
                        @Override
                        public void changeSuccess() {
                            getBasicDataWhileNoCache();
                        }

                        @Override
                        public void changeFail() {
                        }
                    });
                } else {
                    BasicDataInfo basicDataInfo = new LotteryService().getCachedBasicDataInfo(LotteryConfig.PLAY_MODE.CLASSIC);
                    if (basicDataInfo != null) {
                        SharedPreferences.Editor editor = getSharedPreferences(CommonConfig.KEY_HASHCODE, MODE_PRIVATE).edit();
                        editor.putString(CommonConfig.KEY_HASHCODE_BASICDATA_CACHE, basicDataInfo.getHashCode());
                        editor.commit();
                    }

                    if (new LotteryService().getCachedAllPlayConfigInfo() == null) {
                        getAllPlayConfigWhileNoCache();
                    } else {
                        quickLogin();
                    }
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

    private void getAllPlayConfigWhileNoCache() {
        showLoading(getString(R.string.loading_message_login), false);

        mIsError = false;
        MyAsyncTask<AllPlayConfig> task = new MyAsyncTask<AllPlayConfig>(LoginActivity.this) {
            @Override
            public AllPlayConfig callService() throws IOException, JsonParseException, BizException, ServiceException {
                return new LotteryService().getAllPlayConfig("");
            }

            @Override
            public void onLoaded(AllPlayConfig result) throws Exception {
                closeLoading();
                if (mIsError) {
                    BaseApp.changeUrl(LoginActivity.this, new BaseApp.OnChangeUrlListener() {
                        @Override
                        public void changeSuccess() {
                            getAllPlayConfigWhileNoCache();
                        }

                        @Override
                        public void changeFail() {
                        }
                    });
                } else {
                    AllPlayConfig allPlayConfig = new LotteryService().getCachedAllPlayConfigInfo();
                    if (allPlayConfig != null) {
                        SharedPreferences.Editor editor = getSharedPreferences(CommonConfig.KEY_HASHCODE, MODE_PRIVATE).edit();
                        editor.putString(CommonConfig.KEY_HASHCODE_ALLPLAYCONFIG_CACHE, allPlayConfig.getHashCode());
                        editor.commit();
                    }

                    quickLogin();
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

    public String CustomMsg(String str) {
        if (str != null) {
            str = str.replace("，", ",");
            String array[] = str.split(",");
            if (array.length == 4) {
                String output = "";
                for (int i = 0; i < 3; i++) {
                    output = output + array[i] + ",";
                }
                return output.substring(0, output.length() - 1);
            }
            return str;
        }
        return null;
    }


    private void quickLogin() {
        if (!"".equals(token.getString(CommonConfig.KEY_TOKEN_TOKENS, ""))) {
            CustomerInfo customerInfo = new CustomerInfo();
            customerInfo.setAuthenticationKey(token.getString(CommonConfig.KEY_TOKEN_TOKENS, ""));
            customerInfo.setUserID(String.valueOf(token.getInt(CommonConfig.KEY_TOKEN_USER_ID, 0)));
            customerInfo.setUserName(token.getString(CommonConfig.KEY_TOKEN_USER_NAME, ""));
            customerInfo.setInfoComplete(token.getBoolean(CommonConfig.KEY_TOKEN_INFOCOMPLETE, true));
            String bankShowString = token.getString(CommonConfig.KEY_TOKEN_BANK_SHOW, "");
            if (!bankShowString.equals("")) {
                customerInfo.setBankShow(new Gson().fromJson(bankShowString, CustomerInfo.BankShow.class));
            }
            CustomerAccountManager.getInstance().setCustomer(customerInfo);
            IntentUtil.redirectToNextActivity(LoginActivity.this, HomeActivity.class);
            LoginActivity.this.finish();
        }
    }

    private void cutBitmap() {

        try {
            int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
            int screenHeight = getWindowManager().getDefaultDisplay().getHeight();
            bitmap = BitmapUtil.decodeSampledBitmapFromResource(
                    getResources(),
                    R.mipmap.login_bg,
                    screenWidth,
                    screenHeight
            );
            login_back.setBackground(new BitmapDrawable(getResources(), bitmap));
        } catch (OutOfMemoryError oom) {
            try {
                Bitmap b = BitmapUtil.readBitMap(this, R.mipmap.bg, Bitmap.Config.ALPHA_8);
                login_back.setBackground(new BitmapDrawable(getResources(), b));
            } catch (OutOfMemoryError oom2) {
                MyToast.show(this, "请您清理内存!");
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
            bitmap = null;
        }
    }
}
