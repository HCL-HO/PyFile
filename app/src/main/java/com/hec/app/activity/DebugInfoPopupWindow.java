package com.hec.app.activity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Build;
import android.system.Os;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.gson.Gson;
import com.hec.app.BuildConfig;
import com.hec.app.R;
import com.hec.app.config.CommonConfig;
import com.hec.app.util.CustomerAccountManager;
import com.hec.app.util.DisplayUtil;
import com.hec.app.util.StringUtil;
import com.hec.app.webservice.BaseService;

import java.util.ArrayList;

/**
 * Created by wangxingjian on 2016/12/8.
 */

public class DebugInfoPopupWindow extends PopupWindow {
    Context context;
    TextView info1,info2,info3,info4,info5,info6;
    SharedPreferences user;
    public DebugInfoPopupWindow(Context mContext) {
        this.context = mContext;
        init();
    }

    private void init() {
        View contentView = LayoutInflater.from(context).inflate(
                R.layout.pop_debuginfo, null);
        info1 = (TextView) contentView.findViewById(R.id.url_info);
        info2 = (TextView) contentView.findViewById(R.id.user_info1);
        info3 = (TextView) contentView.findViewById(R.id.user_info2);
        info4 = (TextView) contentView.findViewById(R.id.user_info3);
        info5 = (TextView) contentView.findViewById(R.id.machine_info);
        //info6 = (TextView) contentView.findViewById(R.id.url_info);

        info1.setText("当前线路：" + BaseService.getRestfulServiceHost());
        info2.setText("用户token：" + CustomerAccountManager.getInstance().getCustomer().getAuthenticationKey());
        user = context.getSharedPreferences(CommonConfig.KEY_DATA, Context.MODE_PRIVATE);
        ArrayList<String> userList = new Gson().fromJson(user.getString(CommonConfig.KEY_DATA_USER_NAMES, "[]"), ArrayList.class);
        info3.setText("聊天室URL" + BaseService.CHAT_URL);
        info4.setText("老虎机后台：" + BaseService.SLOT_URL);
        this.setContentView(contentView);
        this.setWidth(DisplayUtil.getPxByDp(context, 320));
        this.setHeight(GridLayout.LayoutParams.WRAP_CONTENT);

        this.setFocusable(true);
        this.setBackgroundDrawable(context.getResources().getDrawable(android.R.color.white));
        this.setTouchable(true);
        this.setOutsideTouchable(true);
        this.update();
    }


    @Override
    public void dismiss() {
        super.dismiss();
    }
}
