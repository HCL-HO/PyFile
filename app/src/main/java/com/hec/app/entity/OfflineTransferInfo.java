package com.hec.app.entity;

import com.networkbench.com.google.gson.annotations.SerializedName;

public class OfflineTransferInfo {
    @SerializedName("Step")
    private String Step;
    @SerializedName("Phone")
    private String Phone;

    public String getStep() {
        return Step;
    }

    public void setStep(String step) {
        Step = step;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }
}
