package com.hec.app.activity;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.JsonParseException;
import com.hec.app.BuildConfig;
import com.hec.app.R;
import com.hec.app.activity.base.BaseActivity;
import com.hec.app.activity.base.BaseApp;
import com.hec.app.adapter.SettingAdapter;
import com.hec.app.config.CommonConfig;
import com.hec.app.entity.BizException;
import com.hec.app.entity.ObjectItem;
import com.hec.app.framework.widget.MyToast;
import com.hec.app.util.CustomerAccountManager;
import com.hec.app.util.IntentUtil;
import com.hec.app.util.MyAsyncTask;
import com.hec.app.util.TestUtil;
import com.hec.app.webservice.DownLoadService;
import com.hec.app.webservice.HomeService;
import com.hec.app.webservice.RequestAnno;
import com.hec.app.webservice.ServiceException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class SettingActivity extends BaseActivity implements SettingAdapter.SettingAdapterListener {

    private final int LOGIN_PWD = 0;
    private final int MONEY_PWD = 1;
    private final int PWD_EMAIL = 2;
    private final int MY_NICKNAME = 3;
    private final int BANK_CARD_MANAGEMENT = 4;
    private final int SYSTEM_UPDATE = 1;
    private Boolean mIsError = false;
    private Boolean mIsDownLoading = false;
    private ServiceConnection mConnection;
    private LinearLayout mLlInitSetting;
    private ListView mLvAccountSecurity;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        ImageView imgBack = (ImageView) findViewById(R.id.imgBack);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        updateAccountSecurity();
        SharedPreferences preferences = getSharedPreferences(CommonConfig.KEY_SYSTEM_NOTIFICATION, MODE_PRIVATE);
        boolean systemNotify = preferences.getBoolean(CommonConfig.KEY_SYSTEM_NOTIFICATION, true);
        if (systemNotify) {
            getAppInfo();
        }

        ArrayList<ObjectItem> systemItems = new ArrayList<>();
        systemItems.add(new ObjectItem(getString(R.string.title_sys_notice), "", false, true, systemNotify));
        systemItems.add(new ObjectItem(getString(R.string.title_sys_update), getString(R.string.title_current_ver) + getVersion(), false, false, false));

        SettingAdapter systemAdapter = new SettingAdapter(this, systemItems, this);
        ListView lvSystem = (ListView) findViewById(R.id.system_item);
        lvSystem.setAdapter(systemAdapter);
        lvSystem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                if (position == SYSTEM_UPDATE) {
                    if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                        return;
                    } else if (mIsDownLoading) {
                        MyToast.show(SettingActivity.this, getString(R.string.loading_message_download_app));
                        return;
                    }

                    getAppInfo();
                }
            }
        });

        String logoutStr = getString(R.string.title_logout);
        if (CustomerAccountManager.getInstance() != null && CustomerAccountManager.getInstance().getCustomer() != null) {
            logoutStr += " " + CustomerAccountManager.getInstance().getCustomer().getUserName();
        }
        TextView tvUsername = (TextView) findViewById(R.id.userName);
        tvUsername.setText(logoutStr);

        LinearLayout llLogout = (LinearLayout) findViewById(R.id.logout);
        llLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog resultBox = new Dialog(SettingActivity.this);
                resultBox.requestWindowFeature(Window.FEATURE_NO_TITLE);
                resultBox.setCancelable(false);
                resultBox.setContentView(R.layout.setting_dialog);

                LinearLayout yes = (LinearLayout) resultBox.findViewById(R.id.logout_yes);
                LinearLayout no = (LinearLayout) resultBox.findViewById(R.id.logout_no);
                yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (CustomerAccountManager.getInstance() != null) {
                            CustomerAccountManager.getInstance().logOut();
                        }

                        finish();
                        if (BaseApp.rootActivity != null) {
                            BaseApp.rootActivity.finish();
                        }

                        SharedPreferences token = getSharedPreferences(CommonConfig.KEY_TOKEN, Context.MODE_PRIVATE);
                        token.edit().putString(CommonConfig.KEY_TOKEN_TOKENS, "").commit();
                        Intent next = new Intent(SettingActivity.this, LoginActivity.class);
                        startActivity(next);

                        resultBox.dismiss();
                    }
                });
                no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        resultBox.dismiss();
                    }
                });

                resultBox.show();
            }
        });

        mLlInitSetting = (LinearLayout) findViewById(R.id.init_setting);
        mLlInitSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog initInfoDialog = new Dialog(SettingActivity.this);
                initInfoDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                initInfoDialog.setContentView(R.layout.setting_initinfo);

                LinearLayout yes = (LinearLayout) initInfoDialog.findViewById(R.id.logout_yes);
                LinearLayout no = (LinearLayout) initInfoDialog.findViewById(R.id.logout_no);
                yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent next = new Intent(SettingActivity.this, NicknameActivity.class);
                        next.putExtra(CommonConfig.INTENT_NICKNAME_TAG, CommonConfig.NICKNAME_KEY_ADD);
                        startActivity(next);
                        initInfoDialog.dismiss();
                    }
                });
                no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        initInfoDialog.dismiss();
                    }
                });

                initInfoDialog.show();
            }
        });
        ifUserInfoCompleted();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateAccountSecurity();
        ifUserInfoCompleted();

        SharedPreferences sharedPreferences = getSharedPreferences(CommonConfig.KEY_DATA, MODE_PRIVATE);
        if (sharedPreferences.getBoolean(CommonConfig.KEY_DATA_STUPID_LOGIN_PASSWORD_FLAG, false)) {
            Intent next = new Intent(SettingActivity.this, FormActivity.class);
            next.putExtra(CommonConfig.INTENT_FORM_TAG, LOGIN_PWD);
            startActivity(next);
        } else if (sharedPreferences.getBoolean(CommonConfig.KEY_DATA_STUPID_MONEY_PASSWORD_FLAG, false)) {
            if (sharedPreferences.getBoolean(CommonConfig.KEY_DATA_STUPID_PASSWORD_SHOW_DIALOG, false)) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(CommonConfig.KEY_DATA_STUPID_PASSWORD_SHOW_DIALOG, false);
                editor.commit();

                AlertDialog.Builder dialog = new AlertDialog.Builder(SettingActivity.this)
                        .setMessage(getString(R.string.confirm_money_pw_stupid))
                        .setPositiveButton(R.string.dialog_determine, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent next = new Intent(SettingActivity.this, FormActivity.class);
                                next.putExtra(CommonConfig.INTENT_FORM_TAG, MONEY_PWD);
                                startActivity(next);
                            }
                        }).setCancelable(false);
                dialog.show();
            } else {
                Intent next = new Intent(SettingActivity.this, FormActivity.class);
                next.putExtra(CommonConfig.INTENT_FORM_TAG, MONEY_PWD);
                startActivity(next);
            }
        }
    }

    @Override
    public void onSwitchStatusChanged(String head, boolean isTurnOn) {
        if (getString(R.string.title_sys_notice).equals(head) && isTurnOn) {
            SharedPreferences preferences = getSharedPreferences(CommonConfig.KEY_SYSTEM_NOTIFICATION, MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(CommonConfig.KEY_SYSTEM_NOTIFICATION, true);
            editor.commit();
            getAppInfo();
        } else {
            SharedPreferences preferences = getSharedPreferences(CommonConfig.KEY_SYSTEM_NOTIFICATION, MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(CommonConfig.KEY_SYSTEM_NOTIFICATION, false);
            editor.commit();
        }

    }

    public String getVersion() {
        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            String version = info.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return getString(R.string.activity_setting_version_not_found);
        }
    }

    public void startDownload(final String url, final String filename, final String path) {
        mConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                DownLoadService.MyBinder binder = (DownLoadService.MyBinder) service;
                binder.startDownload(SettingActivity.this, filename, path, url);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
            }
        };

        Intent startIntent = new Intent(this, DownLoadService.class);
        bindService(startIntent, mConnection, BIND_AUTO_CREATE);
    }

    @RequestAnno
    private void getAppInfo() {
        mIsError = false;
        MyAsyncTask<com.hec.app.entity.AppBean> task = new MyAsyncTask<com.hec.app.entity.AppBean>(SettingActivity.this) {
            @Override
            public com.hec.app.entity.AppBean callService() throws IOException, JsonParseException, BizException, ServiceException {
                return new HomeService().getAppInfo();
            }

            @Override
            public void onLoaded(final com.hec.app.entity.AppBean result) throws Exception {
                if (SettingActivity.this == null || SettingActivity.this.isFinishing()) {
                    return;
                }

                if (!mIsError) {
                    BaseApp.setAppBean(result);

                    try {
                        // 20180724 經討論結果設定頁版本更新根據entrance info 的version
//                        final String version = result.getAaMinAvailableVersion();
                        final String version = result.getVersion();

                        int currentVersion = Integer.parseInt(BaseApp.instance().getVersionCode());
                        if ("".equals(version)) {
                            return;
                        }
                        if (currentVersion < Integer.parseInt(version)) {
                            AlertDialog.Builder dialog = new AlertDialog.Builder(SettingActivity.this)
                                    .setTitle(R.string.dialog_update_version_title)
                                    .setMessage(R.string.dialog_update_version_message)
                                    .setPositiveButton(R.string.dialog_determine, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/hec";
                                            String fileName = "hec-" + version + ".apk";
                                            File file = new File(filePath + "/" + fileName);
                                            if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                                                MyToast.show(SettingActivity.this, getString(R.string.error_message_sd_card));
                                            } else if (file.exists()) {
                                                Uri uri = Uri.fromFile(file);
                                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                intent.setDataAndType(uri, "application/vnd.android.package-archive");
                                                startActivity(intent);
                                            } else {
                                                setIsDownLoading(true);
                                                MyToast.show(SettingActivity.this, getString(R.string.loading_message_download_app));
                                                startDownload(BaseApp.getAppBean().getAppUrl(), fileName, filePath);
                                            }
                                        }
                                    }).setCancelable(false);

                            if (BuildConfig.DEBUG || BuildConfig.SIT || BuildConfig.UAT) {
                                dialog.setNegativeButton(R.string.dialog_cancel, null);
                            }
                            dialog.show();
                        } else {
                            final Dialog resultbox = new Dialog(SettingActivity.this);
                            resultbox.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            resultbox.setContentView(R.layout.version_dialog);

                            LinearLayout llVersion = (LinearLayout) resultbox.findViewById(R.id.version_ll);
                            TextView tvVersion = (TextView) resultbox.findViewById(R.id.version);
                            llVersion.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    resultbox.dismiss();
                                }
                            });
                            tvVersion.setText(getString(R.string.updated_version));

                            resultbox.show();
                        }
                    } catch (Exception e) {
                        TestUtil.print(e.getLocalizedMessage());
                    }
                } else {
                    if (getErrorMessage() != null) {
                        MyToast.show(getBaseContext(), getErrorMessage());
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

    private void ifUserInfoCompleted() {
        if (mLlInitSetting == null) {
            return;
        }

        if (isInfoComplete()) {
            mLlInitSetting.setVisibility(View.VISIBLE);
        } else {
            mLlInitSetting.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mConnection != null) {
            unbindService(mConnection);
            mConnection = null;
        }
    }

    public void setIsDownLoading(boolean isDownLoading) {
        mIsDownLoading = isDownLoading;
    }

    public boolean isInfoComplete() {
        return (CustomerAccountManager.getInstance().getCustomer() != null && !CustomerAccountManager.getInstance().getCustomer().getIsInfoComplete());
    }

    public void updateAccountSecurity() {
        ArrayList<ObjectItem> accountSecurityItems = new ArrayList<>();
        accountSecurityItems.add(new ObjectItem(getString(R.string.title_login_pw), getString(R.string.title_modify), true, false, false));
        accountSecurityItems.add(new ObjectItem(getString(R.string.title_fund_pw), getString(R.string.title_modify), true, false, false));
        if (!isInfoComplete()) {
            accountSecurityItems.add(new ObjectItem(getString(R.string.title_pw_email), "", true, false, false));
            accountSecurityItems.add(new ObjectItem(getString(R.string.title_my_nuckname), getString(R.string.title_modify), true, false, false));
            accountSecurityItems.add(new ObjectItem(getString(R.string.title_bankcard_management), "", true, false, false));
        }

        SettingAdapter accountSecurityAdapter = new SettingAdapter(this, accountSecurityItems, this);
        if (mLvAccountSecurity == null) {
            mLvAccountSecurity = (ListView) findViewById(R.id.account_security_item);
            mLvAccountSecurity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                    switch (position) {
                        case LOGIN_PWD:
                        case MONEY_PWD:
                            Intent next = new Intent(SettingActivity.this, FormActivity.class);
                            next.putExtra(CommonConfig.INTENT_FORM_TAG, position);
                            startActivity(next);
                            break;
                        case PWD_EMAIL:
                            IntentUtil.redirectToNextActivity(SettingActivity.this, ConfidentialityActivity.class);
                            break;
                        case MY_NICKNAME:
                            IntentUtil.redirectToNextActivity(SettingActivity.this, NicknameActivity.class, CommonConfig.INTENT_NICKNAME_TAG, CommonConfig.NICKNAME_KEY_FIX);
                            break;
                        case BANK_CARD_MANAGEMENT:
                            IntentUtil.redirectToNextActivity(SettingActivity.this, BankCardManagementActivity.class);
                            break;
                    }
                }
            });
        }
        mLvAccountSecurity.setAdapter(accountSecurityAdapter);
    }
}
