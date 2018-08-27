package com.hec.app.activity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.JsonParseException;
import com.hec.app.R;
import com.hec.app.activity.base.BaseActivity;
import com.hec.app.activity.base.BaseApp;
import com.hec.app.config.CommonConfig;
import com.hec.app.entity.BankInfo;
import com.hec.app.entity.BizException;
import com.hec.app.entity.NicknameInfo;
import com.hec.app.entity.Response;
import com.hec.app.entity.SecurityInfoFinishInfo;
import com.hec.app.framework.adapter.CommonAdapter;
import com.hec.app.framework.adapter.ViewHolder;
import com.hec.app.framework.widget.MyToast;
import com.hec.app.util.IntentUtil;
import com.hec.app.util.MyAsyncTask;
import com.hec.app.webservice.AccountService;
import com.hec.app.webservice.ServiceException;
import com.hec.app.webservice.WithdrawService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BankCardManagementActivity extends BaseActivity {
    private final static int[] CARD_BGS = {R.mipmap.icon_bankcard_blue, R.mipmap.icon_bankcard_green, R.mipmap.icon_bankcard_orange, R.mipmap.icon_bankcard_red, R.mipmap.icon_bankcard_grey};
    private GridView gridView;
    private CommonAdapter commonAdapter;
    private List<BankInfo> bankInfoList;
    private int colorIndex;
    private boolean mIsError = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_card_management);
        initData();
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getBankInfo();
    }

    private void initData() {
        colorIndex = 0;
        bankInfoList = new ArrayList<>();
    }

    private void initView() {
        ImageView imgBack = (ImageView) findViewById(R.id.imgBack);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        gridView = (GridView) findViewById(R.id.gridviewCards);
    }

    private void getBankInfo(){
        mIsError = false;
        MyAsyncTask<List<BankInfo>> task = new MyAsyncTask<List<BankInfo>>(BankCardManagementActivity.this) {
            @Override
            public List<BankInfo> callService() throws IOException, JsonParseException, BizException, ServiceException {
                return new WithdrawService().getBankCard();
            }
            @Override
            public void onLoaded(List<BankInfo> data) throws Exception {
                if(BankCardManagementActivity.this == null || BankCardManagementActivity.this.isFinishing()) {
                    return;
                }

                if(!mIsError) {
                    colorIndex = 0;
                    bankInfoList.clear();
                    for (BankInfo info : data) {
                        info.setColorIndex(colorIndex++);
                        bankInfoList.add(info);
                    }

                    BankInfo dummy = new BankInfo();
                    dummy.setBankCard("");
                    bankInfoList.add(dummy);

                    bindData();
                }
                else {
                    BaseApp.changeUrl(BankCardManagementActivity.this, new BaseApp.OnChangeUrlListener() {
                        @Override
                        public void changeSuccess() {
                            getBankInfo();
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

    public void bindData(){
        commonAdapter = new CommonAdapter<BankInfo>(BankCardManagementActivity.this, bankInfoList, R.layout.gridview_item_card) {
            @Override
            public void convert(ViewHolder helper, BankInfo item, final int position) {

                final BankInfo item_copy = item;
                RelativeLayout cardView = helper.getView(R.id.cardView);
                ImageView cardBg = helper.getView(R.id.imgBankcard);
                ImageView cardDel = helper.getView(R.id.imgBankcardDel);
                TextView cardBanker = helper.getView(R.id.cardBanker);
                TextView cardHolder = helper.getView(R.id.cardHolder);
                TextView cardNumber = helper.getView(R.id.cardNumber);

                if(item.getBankCard().compareTo("") == 0){
                    cardBg.setImageResource(R.mipmap.icon_bankcard_add);
                    cardHolder.setText(R.string.withdraw_add);
                    cardHolder.setTextColor(getResources().getColor(R.color.gray));
                    cardNumber.setVisibility(View.INVISIBLE);
                    cardBanker.setVisibility(View.INVISIBLE);
                    cardView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent next = new Intent(BankCardManagementActivity.this, FormActivity.class);
                            next.putExtra("tag", 3);
                            next.putExtra("btn_type", 0);
                            startActivity(next);
                        }
                    });
                    cardDel.setVisibility(View.GONE);
                } else {
                    cardBg.setImageResource(CARD_BGS[Integer.valueOf(item.getColorIndex())%5]);
                    cardHolder.setText(item.getCardUser());
                    cardNumber.setText(item.getBankCard());
                    cardBanker.setText(item.getBankName());

                    cardView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            showMoneyPasswordDialog();
                            Bundle bundle = new Bundle();
                            bundle.putInt("BankId", item_copy.getBankId());
                            bundle.putString("CardUser", item_copy.getCardUser());

                            Intent next = new Intent(BankCardManagementActivity.this, VerifyMoneyPasswordActivity.class);
                            next.putExtra("BankInfo", bundle);
                            startActivity(next);
                        }
                    });
                }

                cardDel.setVisibility(View.GONE);
            }

        };
        gridView.setAdapter(commonAdapter);
    }

    private void showMoneyPasswordDialog() {
        final Dialog dialog = new Dialog(BankCardManagementActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.money_password_confidentiality_dialog);

        View determine = dialog.findViewById(R.id.determine);
        determine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        View cancel = dialog.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
