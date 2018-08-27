package com.hec.app.entity;

import android.util.Log;

import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Isaac on 12/2/2016.
 */
public class RebateUtil {
    @SerializedName("quotas")
    String quotas;
    boolean success = true;
 //   List<String> rebates;

    public void setQuotas (JSONArray val) {
        quotas = val.toString();
    }

    public JSONArray getQuotas () {
        JSONArray jsonArray = new JSONArray();

        try {
            jsonArray = new JSONArray(quotas);
            if (jsonArray.isNull(0))
                success = false;
        } catch (Exception e) {
            success = false;
        }
        return jsonArray;
    }

    public void setSuccess(boolean val) {
        success = val;
    }

    public boolean getSuccess(){
        return success;
    }

    public String toPercentage(String val) {
        if (val == null || val.isEmpty())
            return "";
        float f = Float.parseFloat(val) * 100;
        String str;
        if (Math.round(f) == f)
            str = String.format("%d%%", (int)f);
        else
            str = String.format("%.1f%%", f);
        return str;
    }

    public String fromPercentage(String val) throws NumberFormatException, NullPointerException{
        if (val == null || val.isEmpty()) {
            return "";
        }
        val = val.substring(0, val.length() - 1);
        float f = Float.parseFloat(val) / 100;
        return String.format("%.3f", f);
    }

    public String getQuota (String rebate) {
        JSONArray jsonArray = getQuotas();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                if (jsonArray.getJSONObject(i).getString("rebate").equals(rebate)) {
                    return jsonArray.getJSONObject(i).getString("quota");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "-1";
        }
        return "无限制";
    }

    public void updateQuota (String oldVal, String newVal) {
        if (oldVal.equals(newVal))
            return;
        JSONArray jsonArray = getQuotas();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String quotaStr = jsonObject.getString("quota");
                if (jsonObject.getString("rebate").equals(oldVal) && !quotaStr.equals("无限制")) {
                    quotaStr = Integer.toString(Integer.parseInt(quotaStr) + 1);
                //    Log.i("Util", quotaStr);
                }
                else if (jsonObject.getString("rebate").equals(newVal) && !quotaStr.equals("无限制")) {
                    quotaStr = Integer.toString(Integer.parseInt(quotaStr) - 1);
                //    Log.i("Util", quotaStr);
                }
                jsonObject.put("quota", quotaStr);
                jsonArray.put(i, jsonObject);
            }
            quotas = jsonArray.toString();
        } catch (Exception e) {

        }
    }
}
