package com.hec.app.entity;

import android.content.Intent;

import com.google.gson.annotations.SerializedName;
import com.hec.app.config.CommonConfig;

/**
 * Created by hec on 2015/11/3.
 */
public class CustomerInfo {
    @SerializedName("IsRemember")
    private boolean IsRemember;
    @SerializedName("AuthenticationKey")
    private String AuthenticationKey;
    @SerializedName("UserID")
    private String userID;
    @SerializedName("UserName")
    private String UserName;
    @SerializedName("UniqueID")
    private String uniqueID;
    @SerializedName("isInfoComplete")
    private boolean isInfoComplete;
    @SerializedName("isVIP")
    private boolean isVIP = false;
    @SerializedName("bankShow")
    private BankShow bankShow;

    private boolean isVIPLonIn = false;


    public String getUserName() {
        return UserName;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public boolean getIsRemember() {
        return IsRemember;
    }

    public void setIsRemember(boolean isRemember) {
        IsRemember = isRemember;
    }

    public String getAuthenticationKey() {
        return AuthenticationKey;
    }

    public void setAuthenticationKey(String authenticationKey) {
        AuthenticationKey = authenticationKey;
    }

    public String getUniqueID() {
        return uniqueID;
    }

    public void setUniqueID(String uniqueID) {
        this.uniqueID = uniqueID;
    }

    public boolean getIsInfoComplete() {
        return isInfoComplete;
    }

    public void setInfoComplete(boolean val) {
        isInfoComplete = val;
    }

    public boolean isVIP() {
        return isVIP;
    }

    public void setVIP(boolean VIP) {
        isVIP = VIP;
    }

    public boolean isVIPLonIn() {
        return isVIPLonIn;
    }

    public void setVIPLonIn(boolean VIPLonIn) {
        isVIPLonIn = VIPLonIn;
    }

    public BankShow getBankShow() {
        return bankShow;
    }

    public void setBankShow(BankShow bankShow) {
        this.bankShow = bankShow;
    }

    /*
        * quick: 快捷支付, jingdong: 京東錢包, alipay:1,2: 支付寶1或2, wechat:1,2: 微信支付1或2, onetouch: 一鍵支付, qqpay: QQ支付,
            bank:1,2: 網銀轉帳1或2, yl:銀聯, tenpay:財富
        */
    public class BankShow {
        private boolean quick;
        private boolean jingdong;
        private boolean[] alipay;
        private boolean[] wechat;
        private boolean[] bank;
        private boolean onetouch;
        private boolean qqpay;

        public boolean getQuick() {
            return quick;
        }

        public void setQuick(boolean quick) {
            this.quick = quick;
        }

        public boolean getJingdong() {
            return jingdong;
        }

        public void setJingdong(boolean jingdong) {
            this.jingdong = jingdong;
        }

        public boolean[] getAlipay() {
            return alipay;
        }

        public void setAlipay(boolean[] alipay) {
            this.alipay = alipay;
        }

        public boolean[] getWechat() {
            return wechat;
        }

        public void setWechat(boolean[] wechat) {
            this.wechat = wechat;
        }

        public boolean[] getBank() {
            return bank;
        }

        public void setBank(boolean[] bank) {
            this.bank = bank;
        }

        public boolean getOnetouch() {
            return onetouch;
        }

        public void setOnetouch(boolean onetouch) {
            this.onetouch = onetouch;
        }

        public boolean getQqpay() {
            return qqpay;
        }

        public void setQqpay(boolean qqpay) {
            this.qqpay = qqpay;
        }

        public boolean isShow(int type) {
            switch (type) {
                case CommonConfig.RECHARGE_QUICK:
                    return quick;
                case CommonConfig.RECHARGE_JD_PAY:
                    return jingdong;
                case CommonConfig.RECHARGE_ALIPAY:
                    if (alipay != null && alipay.length == 2) {
                        return alipay[0] || alipay[1];
                    } else {
                        return true;
                    }
                case CommonConfig.RECHARGE_WECHAT:
                    if (wechat != null && wechat.length == 2) {
                        return wechat[0] || wechat[1];
                    } else {
                        return true;
                    }
                case CommonConfig.RECHARGE_ONLINE_BANK:
                    if (bank != null && bank.length == 4) {
                        return bank[0] || bank[1] || bank[2] || bank[3];
                    } else {
                        return true;
                    }
                case CommonConfig.RECHARGE_QQ:
                    return qqpay;
                case CommonConfig.RECHARGE_ONE_TOUCH:
                    return onetouch;
            }
            return true;
        }
    }
}
