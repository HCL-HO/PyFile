package com.hec.app.fragment;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hec.app.R;
import com.hec.app.activity.MoneyActivity;
import com.hec.app.util.DialogUtil;
import com.hec.app.util.ImageDialog;
import com.hec.app.util.IntentUtil;
import com.hec.app.util.WechatRechargeListener;

import java.util.concurrent.TimeUnit;

public class WechatTransferSuccessFragment extends Fragment implements View.OnClickListener {
    private TextView tvBankUser;
    private TextView copyBankUserBtn;
    private TextView tvBankCard;
    private TextView copyBankCardBtn;
    private TextView tvBankType;
    private TextView tvAmount;
    private TextView tvTime;
    private LinearLayout confirmBtn;
    private LinearLayout wechatGuideBtn;
    private TextView copyAmountBtn;

    private String bankUser;
    private String bankCard;
    private String bankType;
    private String amount;

    private CountDownTimer timer;
    private WechatRechargeListener listener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            bankUser = bundle.getString("bankUser", "");
            bankCard = bundle.getString("bankCard", "");
            bankType = bundle.getString("bankType", "");
            amount = bundle.getString("amount", "0.00");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.content_recharge_wechat_success, null);
        tvBankUser = (TextView) view.findViewById(R.id.tv_bank_user);
        copyBankUserBtn = (TextView) view.findViewById(R.id.copy_bank_user_btn);
        tvBankCard = (TextView) view.findViewById(R.id.tv_bank_card);
        copyBankCardBtn = (TextView) view.findViewById(R.id.copy_bank_card_btn);
        tvBankType = (TextView) view.findViewById(R.id.tv_bank_type);
        tvAmount = (TextView) view.findViewById(R.id.tv_amount);
        tvTime = (TextView) view.findViewById(R.id.tv_time);
        confirmBtn = (LinearLayout) view.findViewById(R.id.confirm_btn);
        wechatGuideBtn = (LinearLayout) view.findViewById(R.id.wechat_guide_btn);
        copyAmountBtn = (TextView) view.findViewById(R.id.copy_amount_btn);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        (getActivity().findViewById(R.id.wechat_title_icon)).setVisibility(View.GONE);
        ((TextView) getActivity().findViewById(R.id.title_tv)).setText(getResources().getText(R.string.recharge_done_title));

        copyBankUserBtn.setOnClickListener(this);
        copyBankCardBtn.setOnClickListener(this);
        copyAmountBtn.setOnClickListener(this);
        confirmBtn.setOnClickListener(this);
        wechatGuideBtn.setOnClickListener(this);

        tvBankUser.setText(bankUser);
        tvBankCard.setText(bankCard);
        tvBankType.setText(bankType);
        tvAmount.setText(amount);

        DialogUtil.getAlertDialog(getContext(), getString(R.string.friendly_reminder), getString(R.string.recharge_hint), getString(R.string.confirm_send), null, null, null).show();

        timer = new CountDownTimer(300000, 1000) {
            public void onTick(long millisUntilFinished) {
                long minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished);
                millisUntilFinished -= TimeUnit.MINUTES.toMillis(minutes);

                long seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished);
                tvTime.setText(String.format(getActivity().getResources().getString(R.string.recharge_success_countdown_time), String.format("%02d", minutes), String.format("%02d", seconds)));
            }

            public void onFinish() {
                tvTime.setText(String.format(getString(R.string.recharge_success_countdown_time), "00", "00"));
                DialogUtil.getAlertDialog(getContext(), "溫馨提示", getResources().getString(R.string.recharge_success_time_up), "確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                            listener.onWechatRechargeTimesUp();
                    }
                }, "", null).show();
            }
        }.start();
    }

    @Override
    public void onClick(View v) {
        ClipboardManager clipboardManager = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        switch (v.getId()) {
            case R.id.copy_bank_user_btn:
                if (clipboardManager != null) {
                    clipboardManager.setPrimaryClip(ClipData.newPlainText(null, bankUser));  // 将内容set到剪贴板
                    if (clipboardManager.hasPrimaryClip()) {
                        clipboardManager.getPrimaryClip().getItemAt(0).getText();
                    }
                    Toast.makeText(getContext(), getActivity().getResources().getText(R.string.recharge_success_copy_bank_user), Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.copy_bank_card_btn:
                if (clipboardManager != null) {
                    clipboardManager.setPrimaryClip(ClipData.newPlainText(null, bankCard));  // 将内容set到剪贴板
                    if (clipboardManager.hasPrimaryClip()) {
                        clipboardManager.getPrimaryClip().getItemAt(0).getText();
                    }
                    Toast.makeText(getContext(), getActivity().getResources().getText(R.string.recharge_success_copy_bank_card), Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.confirm_btn:
                listener.onWechatRechargeFinished();
                break;

            case R.id.wechat_guide_btn:
                new ImageDialog(getContext(), R.mipmap.wechat_page).show();
                break;
            case R.id.copy_amount_btn:
                if (clipboardManager != null) {
                    clipboardManager.setPrimaryClip(ClipData.newPlainText(null, amount));  // 将内容set到剪贴板
                    if (clipboardManager.hasPrimaryClip()) {
                        clipboardManager.getPrimaryClip().getItemAt(0).getText();
                    }
                    Toast.makeText(getContext(), getActivity().getResources().getText(R.string.recharge_success_copy_amount), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (WechatRechargeListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement WechatRechargeListener");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }
}
