package com.hec.app.entity;

/**
 * Created by wangxingjian on 2017/1/19.
 */

public class CityInfo {
    private String ProvinceId;
    private int CityId;
    private String CityName;

    public String getProvinceId() {
        return ProvinceId;
    }

    public void setProvinceId(String provinceId) {
        ProvinceId = provinceId;
    }

    public int getCityId() {
        return CityId;
    }

    public void setCityId(int cityId) {
        CityId = cityId;
    }

    public String getCityName() {
        return CityName;
    }

    public void setCityName(String cityName) {
        CityName = cityName;
    }
}
