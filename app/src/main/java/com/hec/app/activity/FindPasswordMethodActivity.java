package com.hec.app.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hec.app.R;
import com.hec.app.config.CommonConfig;
import com.hec.app.fragment.ValidEmailFragment;
import com.hec.app.fragment.ValidFundPasswordFragment;
import com.hec.app.fragment.ValidIssueFragment;
import com.hec.app.fragment.ValidSmsFragment;

public class FindPasswordMethodActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_password_method);

        ImageView imgBack = (ImageView)findViewById(R.id.imgBack);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView txFindMethodPasswordTitle = (TextView)findViewById(R.id.findMethodPasswrodTitle);
        Bundle bundle = getIntent().getExtras();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        switch (bundle.getInt(CommonConfig.BUNDLE_FIND_PASSWORD_POSITION)) {
            case CommonConfig.FIND_PASSWORD_EMAIL:
                txFindMethodPasswordTitle.setText(getResources().getText(R.string.valid_email_title));
                ValidEmailFragment validEmailFragment = new ValidEmailFragment();
                validEmailFragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.find_password_method_layout, validEmailFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;

            case CommonConfig.FIND_PASSWORD_ISSUE:
                txFindMethodPasswordTitle.setText(getResources().getText(R.string.valid_issue_title));
                ValidIssueFragment validIssueFragment = new ValidIssueFragment();
                validIssueFragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.find_password_method_layout, validIssueFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;

            case CommonConfig.FIND_PASSWORD_MONEY_PWD:
                txFindMethodPasswordTitle.setText(getResources().getText(R.string.valid_fund_password_title));
                ValidFundPasswordFragment validFundPasswordFragment = new ValidFundPasswordFragment();
                validFundPasswordFragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.find_password_method_layout, validFundPasswordFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;

            case CommonConfig.FIND_PASSWORD_SMS:
                txFindMethodPasswordTitle.setText(getResources().getText(R.string.valid_sms_title));
                ValidSmsFragment validSmsFragment = new ValidSmsFragment();
                validSmsFragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.find_password_method_layout, validSmsFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;
        }
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
