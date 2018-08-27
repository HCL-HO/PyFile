package com.hec.app.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hec.app.R;
import com.hec.app.activity.PhoneActivity;
import com.hec.app.util.DialogUtil;
import com.hec.app.util.OfflineTransferListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OfflineTransferFragment extends Fragment implements View.OnClickListener {
    private EditText transferUser;
    private EditText transferAmount;
    private EditText transferPassword;
    private LinearLayout warningAmount;
    private LinearLayout warningPassword;
    private LinearLayout warningAccount;
    private ImageView amountCross;
    private ImageView passwordCross;
    private ImageView accountCross;
    private TextView amountFilter;
    private TextView passwordFilter;
    private TextView accountFilter;
    private LinearLayout confirmBtn;
    private OfflineTransferListener listener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_offline_transfer, null);
        transferUser = (EditText) view.findViewById(R.id.transfer_user);
        transferAmount = (EditText) view.findViewById(R.id.transfer_amount);
        transferPassword = (EditText) view.findViewById(R.id.transfer_password);

        warningAmount = (LinearLayout) view.findViewById(R.id.amount_warning);
        amountCross = (ImageView) view.findViewById(R.id.amount_cross);
        warningPassword = (LinearLayout) view.findViewById(R.id.password_warning);
        passwordCross = (ImageView) view.findViewById(R.id.password_cross);
        amountFilter = (TextView) view.findViewById(R.id.amount_red_filter);
        passwordFilter = (TextView) view.findViewById(R.id.password_red_filter);
        warningAccount = (LinearLayout) view.findViewById(R.id.account_warning);
        accountCross = (ImageView) view.findViewById(R.id.account_cross);
        accountFilter = (TextView) view.findViewById(R.id.account_red_filter);
        confirmBtn = (LinearLayout) view.findViewById(R.id.confirm_transfer);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        accountCross.setOnClickListener(this);
        passwordCross.setOnClickListener(this);
        accountCross.setOnClickListener(this);
        confirmBtn.setOnClickListener(this);

        transferUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty())
                    hideWarnings(2);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        transferAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty())
                    hideWarnings(0);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        transferPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty())
                    hideWarnings(1);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (OfflineTransferListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement TransferListener");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.account_cross:
                hideWarnings(2);
                break;
            case R.id.password_cross:
                hideWarnings(1);
                break;
            case R.id.amount_cross:
                hideWarnings(0);
                break;
            case R.id.confirm_transfer:
                if (validInput()) {
                    listener.onConfirmTransfer(transferUser.getText().toString(), transferPassword.getText().toString(), transferAmount.getText().toString());
                }
                break;
        }
    }

    private boolean validInput() {
        boolean flag = true;
        if (transferUser.getText().toString().isEmpty()) {
            flag = false;
            showWarnings(2);
        }
        if (transferPassword.getText().toString().isEmpty()) {
            flag = false;
            showWarnings(1);
        }
        if (transferAmount.getText().toString().isEmpty()) {
            flag = false;
            showWarnings(0);
            return flag;
        }
        if (Double.parseDouble(transferAmount.getText().toString()) < 10 || Double.parseDouble(transferAmount.getText().toString()) > 99999) {
            flag = false;
            showWarnings(4);
        }
        return flag;
    }

    private void showWarnings(int mode) {
        if (mode == 0 || mode == 3) {
            warningAmount.setVisibility(View.VISIBLE);
            amountCross.setVisibility(View.VISIBLE);
            amountFilter.setVisibility(View.VISIBLE);
        }
        if (mode == 1 || mode == 3) {
            warningPassword.setVisibility(View.VISIBLE);
            passwordCross.setVisibility(View.VISIBLE);
            passwordFilter.setVisibility(View.VISIBLE);
        }
        if (mode == 2 || mode == 3) {
            warningAccount.setVisibility(View.VISIBLE);
            accountCross.setVisibility(View.VISIBLE);
            accountFilter.setVisibility(View.VISIBLE);
        }
        if (mode == 4) {
            DialogUtil.getAlertDialog(getActivity(), getString(R.string.friendly_reminder), getString(R.string.offline_transfer_amount_limit), getString(R.string.confirm_send), null, "", null).show();
        }
    }

    private void hideWarnings(int mode) {
        if (mode == 0 || mode == 3) {
            warningAmount.setVisibility(View.GONE);
            amountCross.setVisibility(View.GONE);
            amountFilter.setVisibility(View.INVISIBLE);
        }
        if (mode == 1 || mode == 3) {
            warningPassword.setVisibility(View.GONE);
            passwordCross.setVisibility(View.GONE);
            passwordFilter.setVisibility(View.INVISIBLE);
        }
        if (mode == 2 || mode == 3) {
            warningAccount.setVisibility(View.GONE);
            accountCross.setVisibility(View.GONE);
            accountFilter.setVisibility(View.INVISIBLE);
        }
    }
}
