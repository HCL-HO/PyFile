package com.hec.app.entity;

import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Isaac on 26/2/2016.
 */
public class AlipayInfo {
    @SerializedName("url")
    String url;

    public AlipayInfo() {

    }

    public String getURL() {
        return url;
    }

    public void setURL(String val) {
        url = val;
    }
/*    @SerializedName("AttachWord")
    String AttachWord;
    @SerializedName("BankName")
    String BankName;
    @SerializedName("BankUser")
    String BankUser;
    @SerializedName("BankCard")
    String BankCard;
    @SerializedName("Amount")
    String Amount;

    boolean success = true;
    public AlipayInfo() {

    }
    public AlipayInfo(ArrayList<String> val) {
        if (val.size() == 5) {
            AttachWord = val.get(0);
            BankName = val.get(1);
            BankUser = val.get(2);
            BankCard = val.get(3);
            Amount = val.get(4);
        }
    }

    public void setAttachWord (String val) {
        AttachWord = val;
    }

    public String getAttachWord () {
        return AttachWord;
    }

    public void setBankName (String val) {
        BankName = val;
    }

    public String getBankName () {
        return BankName;
    }

    public void setBankUser (String val) {
        BankUser = val;
    }

    public String getBankUser () {
        return BankUser;
    }

    public void setBankCard (String val) {
        BankCard = val;
    }

    public String getBankCard () {
        return BankCard;
    }

    public void setAmount (String val) {
        Amount = val;
    }

    public String getAmount () {
        return Amount;
    }

    public void setSuccess (boolean val) {
        success = val;
    }

    public boolean isSuccess () {
        return success;
    }

    public ArrayList<String> toStringArrayList() {
        ArrayList<String> r = new ArrayList<>();
        if (AttachWord != null && BankName != null && BankCard != null && BankUser != null && Amount != null) {
            r.add(AttachWord);
            r.add(BankName);
            r.add(BankUser);
            r.add(BankCard);
            r.add(Amount);
        }
        return r;
    }
*/

}
