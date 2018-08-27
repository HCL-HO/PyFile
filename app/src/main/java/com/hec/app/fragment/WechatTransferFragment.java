package com.hec.app.fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hec.app.R;
import com.hec.app.activity.RechargeWechatActivity;
import com.hec.app.util.DialogUtil;
import com.hec.app.util.WechatRechargeListener;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WechatTransferFragment extends Fragment implements View.OnClickListener {
    private EditText amountEdit;
    private RelativeLayout amount10;
    private RelativeLayout amount100;
    private RelativeLayout amount200;
    private RelativeLayout amount500;
    private RelativeLayout amount1000;
    private RelativeLayout amount2000;
    private RelativeLayout amount3000;
    private RelativeLayout amount4000;
    private RelativeLayout amount5000;
    private LinearLayout wechatRecharge;
    private ImageView errorImgBtn;
    private TextView amountHint;
    private WechatRechargeListener listener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.content_recharge_wechat, null);
        amountEdit = (EditText) view.findViewById(R.id.recharge_moneysum_edittext);
        errorImgBtn = (ImageView) view.findViewById(R.id.error_btn);
        amount10 = (RelativeLayout) view.findViewById(R.id.recharge_10_btn);
        amount100 = (RelativeLayout) view.findViewById(R.id.recharge_100_btn);
        amount200 = (RelativeLayout) view.findViewById(R.id.recharge_200_btn);
        amount500 = (RelativeLayout) view.findViewById(R.id.recharge_500_btn);
        amount1000 = (RelativeLayout) view.findViewById(R.id.recharge_1000_btn);
        amount2000 = (RelativeLayout) view.findViewById(R.id.recharge_2000_btn);
        amount3000 = (RelativeLayout) view.findViewById(R.id.recharge_3000_btn);
        amount4000 = (RelativeLayout) view.findViewById(R.id.recharge_4000_btn);
        amount5000 = (RelativeLayout) view.findViewById(R.id.recharge_5000_btn);
        wechatRecharge = (LinearLayout) view.findViewById(R.id.wechat_recharge_btn);
        amountHint = (TextView) view.findViewById(R.id.amount_hint);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((ImageView) getActivity().findViewById(R.id.wechat_title_icon)).setVisibility(View.VISIBLE);
        ((TextView) getActivity().findViewById(R.id.title_tv)).setText(getResources().getText(R.string.recharge_wechat_title));
        amount10.setOnClickListener(this);
        amount100.setOnClickListener(this);
        amount200.setOnClickListener(this);
        amount500.setOnClickListener(this);
        amount1000.setOnClickListener(this);
        amount2000.setOnClickListener(this);
        amount3000.setOnClickListener(this);
        amount4000.setOnClickListener(this);
        amount5000.setOnClickListener(this);
        wechatRecharge.setOnClickListener(this);
        errorImgBtn.setOnClickListener(this);
        
        String text = "<font color=\"#ff0000\">*</font> <font color=\"#000000\">小提示：使用非整数金额充值能更快到帐哦～<br/>(例如:</font>" +
                      "<font color=\"#ff0000\">101、1002、2011</font> <font color=\"#000000\">)</font>";
        amountHint.setText(Html.fromHtml(text));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.recharge_10_btn:
                setAmountEditText("10");
                break;
            case R.id.recharge_100_btn:
                setAmountEditText("100");
                break;
            case R.id.recharge_200_btn:
                setAmountEditText("200");
                break;
            case R.id.recharge_500_btn:
                setAmountEditText("500");
                break;
            case R.id.recharge_1000_btn:
                setAmountEditText("1000");
                break;
            case R.id.recharge_2000_btn:
                setAmountEditText("2000");
                break;
            case R.id.recharge_3000_btn:
                setAmountEditText("3000");
                break;
            case R.id.recharge_4000_btn:
                setAmountEditText("4000");
                break;
            case R.id.recharge_5000_btn:
                setAmountEditText("5000");
                break;
            case R.id.error_btn:
                amountEdit.setFocusableInTouchMode(true);
                amountEdit.requestFocus();
                amountEdit.setBackgroundColor(Color.parseColor("#f2f2f2"));
                amountEdit.setHint(getResources().getText(R.string.recharge_quick_please_enter_moneysum));
                amountEdit.setHintTextColor(getResources().getColor(R.color.hint_word_gray));
                errorImgBtn.setVisibility(View.GONE);
                break;
            case R.id.wechat_recharge_btn:
                if (amountEdit.getText().toString().isEmpty()) {
                    amountEdit.setFocusable(false);
                    amountEdit.setBackgroundColor(Color.parseColor("#ffcccc"));
                    amountEdit.setHint(getResources().getText(R.string.recharge_quick_moneysum_textview_cant_null));
                    amountEdit.setHintTextColor(Color.RED);
                    errorImgBtn.setVisibility(View.VISIBLE);
                } else if (Integer.valueOf(amountEdit.getText().toString()) < 10 || Integer.valueOf(amountEdit.getText().toString()) > 5000) {
                    DialogUtil.getAlertDialog(getActivity(), getResources().getString(R.string.friendly_reminder),
                            getResources().getString(R.string.recharge_wechat_amount_limit), getResources().getString(R.string.dialog_determine),
                            null, "", null).show();
                } else {
                    listener.onWechatRecharged("", String.format("%.02f", Float.parseFloat(amountEdit.getText().toString())));
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

    private void setAmountEditText(String amount) {
        amountEdit.setText(amount);
        amountEdit.setSelection(amount.length());
    }
}
