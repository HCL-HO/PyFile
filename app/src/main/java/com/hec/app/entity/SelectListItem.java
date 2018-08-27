package com.hec.app.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hec on 2015/12/8.
 */
public class SelectListItem {
    @SerializedName("Text")
    private String text;
    @SerializedName("Value")
    private String value;
    @SerializedName("Selected")
    private Boolean selected;
    @SerializedName("Key")
    private String key;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String value) {
        this.key = key;
    }
}
