package com.hec.app.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Isaac on 12/2/2016.
 */
public class MemberInfo extends AgentListInfo{
    @SerializedName("IslowMoneyIn")
    boolean IslowMoneyIn;
    @SerializedName("RebatePro")
    String RebatePro;

    boolean formatted = false;

    public void setRebatePro (float val) {
        RebatePro = String.format("%.3f", val);
        formatted = true;
    }

    public String getRebatePro () {
        if (!formatted) {
            if (RebatePro != null) {
                String s = String.format("%.3f", Float.parseFloat(RebatePro));
                formatted = true;
                return s;
            }
        }
        return RebatePro;
    }

    public void setIslowMoneyIn (boolean val) {
        IslowMoneyIn = val;
    }

    public boolean getIslowMoneyIn () {
        return IslowMoneyIn;
    }
}
