package com.hec.app.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.google.gson.JsonParseException;
import com.hec.app.R;
import com.hec.app.activity.base.BaseActivity;
import com.hec.app.activity.base.BaseApp;
import com.hec.app.entity.BankInfo;
import com.hec.app.entity.BizException;
import com.hec.app.entity.CityInfo;
import com.hec.app.entity.NewBankInfo;
import com.hec.app.entity.ProvinceInfo;
import com.hec.app.entity.Response;
import com.hec.app.util.DialogUtil;
import com.hec.app.util.MyAsyncTask;
import com.hec.app.webservice.ServiceException;
import com.hec.app.webservice.WithdrawService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ModifyBankCardInformationActivity extends BaseActivity {
    private EditText nameEditText;
    private EditText bankCardNoEditText;
    private EditText confirmBankCardNoEditText;
    private EditText phoneEditText;
    private EditText brachBankEditText;
    private Spinner bankSpinner;
    private Spinner provinceSpinner;
    private Spinner citySpinner;
    private LinearLayout deleateLl;
    private LinearLayout confirmChangeLl;
    private ArrayAdapter bankArrayAdapter;
    private List<String> bankNameList;
    private List<BankInfo.BankList> bankInfoList;
    private List<String> provinceNameList;
    private List<ProvinceInfo> provinceInfoList;
    private List<String> cityNameList;
    private List<CityInfo> cityInfoList;
    private int bankTypeId;
    private String cardUser;
    private String bankCardNo;
    private String phone;
    private int cityId;
    private int provinceId;
    private String siteName;
    private boolean mIsError = false;
    private boolean isCityFirst;
    private ProgressDialog progressDialog;
    private Bundle bankInfo;
    private String moneyPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_bank_card_information);
        initData();
        initView();

        getBankList();
        getProvinceInfo();
    }

    private void initData() {
        isCityFirst = true;

        bankNameList = new ArrayList<>();
        bankInfoList = new ArrayList<>();
        provinceNameList = new ArrayList<>();
        provinceInfoList = new ArrayList<>();
        cityNameList = new ArrayList<>();
        cityInfoList = new ArrayList<>();

        bankInfo = getIntent().getBundleExtra("BankInfo");
        bankTypeId = bankInfo.getInt("BankTypeID", 0);
        cardUser = bankInfo.getString("CardUser", "");
        bankCardNo = bankInfo.getString("BankCard", "");
        phone = bankInfo.getString("MobileNo", "");
        cityId = bankInfo.getInt("CityID", 0);
        provinceId = bankInfo.getInt("ProvinceID", 0);
        siteName = bankInfo.getString("SiteName", "");
        moneyPwd = bankInfo.getString("moneyPwd", "");

    }

    private void initView() {
        nameEditText = (EditText) findViewById(R.id.name_edittext);
        bankCardNoEditText = (EditText) findViewById(R.id.bank_card_no_edittext);
        confirmBankCardNoEditText = (EditText) findViewById(R.id.confirm_bank_card_no_edittext);
        phoneEditText = (EditText) findViewById(R.id.phone_edittext);
        brachBankEditText = (EditText) findViewById(R.id.brach_bank_edittext);
        bankSpinner = (Spinner) findViewById(R.id.bank_spinner);
        provinceSpinner = (Spinner) findViewById(R.id.province_spinner);
        citySpinner = (Spinner) findViewById(R.id.city_spinner);
        deleateLl = (LinearLayout) findViewById(R.id.deleate_ll);
        confirmChangeLl = (LinearLayout) findViewById(R.id.confirm_change_ll);

        ImageView imgBack = (ImageView) findViewById(R.id.imgBack);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        nameEditText.setText(cardUser);
        bankCardNoEditText.setText(bankCardNo);
        confirmBankCardNoEditText.setText(bankCardNo);
        phoneEditText.setText(phone);
        brachBankEditText.setText(siteName);

        bankArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, bankNameList);
        bankArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        bankSpinner.setAdapter(bankArrayAdapter);

        deleateLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtil.getConfirmAlertDialog(ModifyBankCardInformationActivity.this, "提示", "您确定要解除此银行卡的绑定吗?", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DelBankInfo(moneyPwd, bankInfo.getInt("BankID", 0), bankInfo.getString("Bankname", ""));
                    }
                }).show();
            }
        });

        confirmChangeLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameStr = nameEditText.getText().toString().trim();
                String bankCardNoStr = bankCardNoEditText.getText().toString().trim();
                String confirmBankCardNoStr = confirmBankCardNoEditText.getText().toString().trim();
                String phoneStr = phoneEditText.getText().toString().trim();
                String brachBankStr = brachBankEditText.getText().toString().trim();

                if (TextUtils.isEmpty(nameStr) || TextUtils.isEmpty(bankCardNoStr) || TextUtils.isEmpty(confirmBankCardNoStr)) {
                    DialogUtil.getErrorAlertDialog(ModifyBankCardInformationActivity.this, "请填写所有空格").show();
                } else if (!bankCardNoStr.equals(confirmBankCardNoStr)) {
                    DialogUtil.getErrorAlertDialog(ModifyBankCardInformationActivity.this, "卡号不匹配").show();
                } else if (!checkChinese(nameStr)) {
                    DialogUtil.getErrorAlertDialog(ModifyBankCardInformationActivity.this, "开户人姓名应为中文").show();
                } else {
                    if (!TextUtils.isEmpty(phoneStr)) {
                        String patternStr = "^((13[0-9])|(15[0-9])|(18[0-9])|(17[0-9])|(14)[0-9])\\d{8}$";
                        if (!Pattern.compile(patternStr).matcher(phoneStr).matches()) {
                            DialogUtil.getErrorAlertDialog(ModifyBankCardInformationActivity.this, "手机号码格式有误").show();
                            return;
                        }
                    }

                    NewBankInfo newBankInfo = new NewBankInfo();
                    newBankInfo.setBankCard(bankCardNoStr);
                    newBankInfo.setBankID(bankInfo.getInt("BankID", 0));
                    newBankInfo.setBankname(bankInfoList.get(bankSpinner.getSelectedItemPosition()).getBankTypeName());
                    newBankInfo.setBankTypeID(bankInfoList.get(bankSpinner.getSelectedItemPosition()).getBankTypeID());
                    newBankInfo.setCardUser(nameStr);
                    if (citySpinner.getSelectedItem() != null) {
                        newBankInfo.setCity(cityInfoList.get(citySpinner.getSelectedItemPosition()).getCityName());
                        newBankInfo.setCityID(cityInfoList.get(citySpinner.getSelectedItemPosition()).getCityId());
                    }
                    newBankInfo.setMobileNo(phoneStr == null ? "" : phoneStr);
                    if (provinceSpinner.getSelectedItem() != null && !provinceSpinner.getSelectedItem().equals("请选择")) {
                        newBankInfo.setProvince(provinceInfoList.get(provinceSpinner.getSelectedItemPosition()).getProvinceName());
                        newBankInfo.setProvinceID(provinceInfoList.get(provinceSpinner.getSelectedItemPosition()).getProvinceId());
                    }
                    newBankInfo.setSiteName(brachBankStr == null ? "" : brachBankStr);
                    newBankInfo.setUserID(bankInfo.getString("UserID", ""));
                    newBankInfo.setApplyTime(bankInfo.getString("ApplyTime", ""));

                    UpdBankInfo(moneyPwd, newBankInfo);
                }
            }
        });
    }

    private void getBankList() {
        MyAsyncTask<List<BankInfo.BankList>> task = new MyAsyncTask<List<BankInfo.BankList>>(ModifyBankCardInformationActivity.this) {
            @Override
            public  List<BankInfo.BankList> callService() throws IOException, JsonParseException, BizException, ServiceException {
                return new WithdrawService().getBankList();
            }

            @Override
            public void onLoaded(List<BankInfo.BankList> data) throws Exception {
                if(ModifyBankCardInformationActivity.this == null || ModifyBankCardInformationActivity.this.isFinishing()) {
                    return;
                }

                if(!mIsError) {
                    int selectionIndex = -1;
                    bankInfoList = data;
                    for (int i = 0; i < bankInfoList.size(); ++i) {
                        bankNameList.add(data.get(i).getBankTypeName());

                        if (data.get(i).getBankTypeID() == bankTypeId) {
                            selectionIndex = i;
                        }
                    }
                    bankArrayAdapter.notifyDataSetChanged();

                    if (data.size() > 0 && selectionIndex != -1) {
                        bankSpinner.setSelection(selectionIndex);
                    }
                }
                else {
                    BaseApp.changeUrl(ModifyBankCardInformationActivity.this, new BaseApp.OnChangeUrlListener() {
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

    private void getProvinceInfo(){
        mIsError = false;
        MyAsyncTask<List<ProvinceInfo>> task = new MyAsyncTask<List<ProvinceInfo>>(ModifyBankCardInformationActivity.this) {
            @Override
            public List<ProvinceInfo> callService() throws IOException, JsonParseException, BizException, ServiceException {
                return new WithdrawService().getProvince();
            }

            @Override
            public void onLoaded(final List<ProvinceInfo> result) throws Exception {
                if(ModifyBankCardInformationActivity.this == null || ModifyBankCardInformationActivity.this.isFinishing()) {
                    return;
                }

                if(!mIsError){
                    provinceInfoList = result;

                    int selectionIndex = -1;
                    provinceNameList = new ArrayList<>();
                    for (int i = 0; i < result.size(); ++i) {
                        provinceNameList.add(result.get(i).getProvinceName());
                        if (provinceId == result.get(i).getProvinceId()) {
                            selectionIndex = i;
                        }
                    }
                    provinceNameList.add(0,"请选择");

                    ArrayAdapter<String> ProvinceAdapter = new ArrayAdapter<String>(ModifyBankCardInformationActivity.this, android.R.layout.simple_spinner_dropdown_item, provinceNameList);
                    provinceSpinner.setAdapter(ProvinceAdapter);
                    provinceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            int provinceId = 0;
                            for(ProvinceInfo provinceInfo : result){
                                if(provinceInfo.getProvinceName().equals(provinceNameList.get(position))){
                                    provinceId = provinceInfo.getProvinceId();
                                    break;
                                }
                            }
                            getCity(provinceId);
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });

                    if (result.size() > 0 && selectionIndex != -1) {
                        provinceSpinner.setSelection(selectionIndex+1);
                    } else {
                        isCityFirst = false;
                    }
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

    private void getCity(final int provinceId){
        mIsError = false;
        MyAsyncTask<List<CityInfo>> task = new MyAsyncTask<List<CityInfo>>(ModifyBankCardInformationActivity.this) {
            @Override
            public List<CityInfo> callService() throws IOException, JsonParseException, BizException, ServiceException {
                return new WithdrawService().getCity(provinceId);
            }

            @Override
            public void onLoaded(List<CityInfo> result) throws Exception {
                if(ModifyBankCardInformationActivity.this == null || ModifyBankCardInformationActivity.this.isFinishing()) {
                    return;
                }

                if(!mIsError){
                    cityInfoList = result;

                    int selectionIndex = -1;
                    cityNameList = new ArrayList<>();
                    for (int i = 0; i < result.size(); ++i) {
                        cityNameList.add(result.get(i).getCityName());
                        if (cityId == result.get(i).getCityId()) {
                            selectionIndex = i;
                        }
                    }

                    ArrayAdapter<String> cityAdapter = new ArrayAdapter<String>(ModifyBankCardInformationActivity.this, android.R.layout.simple_spinner_dropdown_item, cityNameList);
                    citySpinner.setAdapter(cityAdapter);

                    if (isCityFirst && result.size() > 0 && selectionIndex != -1) {
                        citySpinner.setSelection(selectionIndex);
                    }
                    isCityFirst = false;
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
            } else{
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

    private void showProgressDialog(String loadingMessage){
        try {
            progressDialog = DialogUtil.getProgressDialog(ModifyBankCardInformationActivity.this, loadingMessage);
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

    private void UpdBankInfo(final String moneyPwd, final NewBankInfo newBankInfo){
        mIsError = false;

        showProgressDialog("正在加载");
        MyAsyncTask<Response> task = new MyAsyncTask<Response>(ModifyBankCardInformationActivity.this) {
            @Override
            public Response callService() throws IOException, JsonParseException, BizException, ServiceException {
                return new WithdrawService().updBankInfo(moneyPwd, newBankInfo);
            }

            @Override
            public void onLoaded(Response data) throws Exception {
                if(ModifyBankCardInformationActivity.this == null || ModifyBankCardInformationActivity.this.isFinishing()) {
                    return;
                }

                closeProgressDialog();
                if (!mIsError) {
                    if (data.getSuccess()) {
                        Intent next = new Intent(ModifyBankCardInformationActivity.this, SuccessModifyBankCardInformationActivity.class);
                        next.putExtra("CardUser", newBankInfo.getCardUser());
                        next.putExtra("BankName", newBankInfo.getBankname());
                        next.putExtra("BankCard", newBankInfo.getBankCard());
                        if (!(provinceSpinner.getSelectedItem()).equals("请选择")) {
                            next.putExtra("BankProvince", newBankInfo.getProvince());
                            next.putExtra("BankCity", newBankInfo.getCity());
                        }
                        startActivity(next);
                        finish();
                    }
                    else {
                        DialogUtil.getErrorAlertDialog(ModifyBankCardInformationActivity.this, data.getMessage()).show();
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

    private void DelBankInfo(final String moneyPwd, final int bankId, final String bankName){
        mIsError = false;

        showProgressDialog("正在加载");
        MyAsyncTask<Response> task = new MyAsyncTask<Response>(ModifyBankCardInformationActivity.this) {
            @Override
            public Response callService() throws IOException, JsonParseException, BizException, ServiceException {
                return new WithdrawService().delBankInfo(moneyPwd, bankId, bankName);
            }

            @Override
            public void onLoaded(Response data) throws Exception {
                if(ModifyBankCardInformationActivity.this == null || ModifyBankCardInformationActivity.this.isFinishing()) {
                    return;
                }

                closeProgressDialog();
                if (!mIsError) {
                    if (data.getSuccess()) {
                        String localBankName = bankName;
                        if (TextUtils.isEmpty(localBankName)) {
                            for (BankInfo.BankList bankList : bankInfoList) {
                                if (bankList.getBankTypeID() == bankTypeId) {
                                    localBankName = bankList.getBankTypeName();
                                    break;
                                }
                            }
                        }

                        DialogUtil.getPromptAlertDialog(ModifyBankCardInformationActivity.this, "提示", "您已成功解绑"+localBankName+"银行卡", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }).show();
                    }
                    else {
                        DialogUtil.getErrorAlertDialog(ModifyBankCardInformationActivity.this, data.getMessage()).show();
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
}
