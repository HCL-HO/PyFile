package com.hec.app.util;

public interface WechatRechargeListener {
    void onWechatRecharged(String userName, String amount);
    void onWechatRechargeFinished();
    void onWechatRechargeTimesUp();
}
