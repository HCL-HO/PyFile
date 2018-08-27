package com.hec.app.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.hec.app.R;
import com.hec.app.activity.base.BaseActivity;
import com.hec.app.activity.base.BaseApp;

/**
 * ����Fragment
 *
 * @author jiangqq
 */
public class EntertainFragment extends Fragment {
    private View mView;
    private Button loginBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.entertain, container, false);
        loginBtn = (Button) mView.findViewById(R.id.btnLogin);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseActivity activity = (BaseActivity) getActivity();
                BaseApp.instance().setLoginBeforeCls(activity.getClass());
                BaseActivity.forceLogin(activity, activity.getClass());
            }
        });

        Button btnLottery = (Button) mView.findViewById(R.id.btnLottery);
        btnLottery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();

                //intent.setClass(getActivity(), LotteryActivity.class);

                startActivity(intent);
            }
        });
        return mView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
