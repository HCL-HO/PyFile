package com.hec.app.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jhezenhu on 2017/4/25.
 */

public class ExpertPlayTypeRadioInfo {
    private String playTypeModel;
    private List<PlayTypeRadioInfo> playTypeRadioInfo;

    public void addPlayTypeRadioInfo(PlayTypeRadioInfo playTypeRadioInfo) {
        if (this.playTypeRadioInfo == null) {
            this.playTypeRadioInfo = new ArrayList<>();
        }

        this.playTypeRadioInfo.add(playTypeRadioInfo);
    }

    public String getPlayTypeModel() {
        return playTypeModel;
    }

    public void setPlayTypeModel(String playTypeModel) {
        this.playTypeModel = playTypeModel;
    }

    public List<PlayTypeRadioInfo> getPlayTypeRadioInfo() {
        return playTypeRadioInfo;
    }

    public void setPlayTypeRadioInfo(List<PlayTypeRadioInfo> playTypeRadioInfo) {
        this.playTypeRadioInfo = playTypeRadioInfo;
    }
}
