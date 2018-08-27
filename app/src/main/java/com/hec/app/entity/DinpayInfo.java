package com.hec.app.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Isaac on 30/3/2016.
 */
public class DinpayInfo {
    @SerializedName("extra_return_param")
    String extra_return_param;
    @SerializedName("interface_version")
    String interface_version;
    @SerializedName("merchant_code")
    String merchant_code;
    @SerializedName("notify_url")
    String notify_url;
    @SerializedName("order_amount")
    String order_amount;
    @SerializedName("order_no")
    String order_no;
    @SerializedName("order_time")
    String order_time;
    @SerializedName("product_name")
    String product_name;
    @SerializedName("sign")
    String sign;
    @SerializedName("sign_type")
    String sign_type;

    public String getURL() {
        return notify_url;
    }

    public String getExtra_return_param() {
        return extra_return_param;
    }

    public String getInterface_version() {
        return interface_version;
    }

    public String getMerchant_code() {
        return merchant_code;
    }

    public String getNotify_url() {
        return notify_url;
    }

    public String getOrder_amount() {
        return order_amount;
    }

    public String getOrder_no() {
        return order_no;
    }

    public String getOrder_time() {
        return order_time;
    }

    public String getProduct_name() {
        return product_name;
    }

    public String getSign() {
        return sign;
    }

    public String getSign_type() {
        return sign_type;
    }
}
