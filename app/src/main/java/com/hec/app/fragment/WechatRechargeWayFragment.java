package com.hec.app.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hec.app.R;
import com.hec.app.activity.RechargeAlipayActivity;

import cn.sharesdk.line.Line;

public class WechatRechargeWayFragment extends Fragment implements View.OnClickListener {
    private LinearLayout wechatTransferImg;
    private LinearLayout wechatDirectImg;
    private boolean[] itemShowArray;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.content_wechat_recharge_way, null);
        wechatTransferImg = (LinearLayout) view.findViewById(R.id.wechat_transfer);
        wechatDirectImg = (LinearLayout) view.findViewById(R.id.wechat_direct);
        LinearLayout block = (LinearLayout) view.findViewById(R.id.ll_block);
        if (!itemShowArray[0]) {
            wechatTransferImg.setVisibility(View.GONE);
            block.setVisibility(View.INVISIBLE);
        } else if (!itemShowArray[1]) {
            wechatDirectImg.setVisibility(View.GONE);
            block.setVisibility(View.INVISIBLE);
        }
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().findViewById(R.id.wechat_title_icon).setVisibility(View.GONE);
        ((TextView) getActivity().findViewById(R.id.title_tv)).setText(getResources().getText(R.string.recharge_select_way));

        wechatTransferImg.setOnClickListener(this);
        wechatDirectImg.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.wechat_transfer) {
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_container, new WechatTransferFragment()).commit();
        } else {
            Intent intent = new Intent();
            intent.setClass(getActivity(), RechargeAlipayActivity.class);
            intent.putExtra("wechat", true);
            startActivity(intent);
        }
    }

    public void setItemShowArray(boolean[] itemShowArray) {
        if (itemShowArray == null) {
            itemShowArray = new boolean[] {true, true};
        }
        this.itemShowArray = itemShowArray;
    }
}
