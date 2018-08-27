package com.hec.app.entity;

public class ObjectItem {

    private String head;
    private String tail;
    private boolean hasArrow;
    private boolean hasSwitch;
    private boolean switchStatus;


    // constructor
    public ObjectItem(String head,String tail,boolean hasArrow,boolean hasSwitch, boolean switchStatus) {
        this.head = head;
        this.tail = tail;
        this.hasArrow = hasArrow;
        this.hasSwitch = hasSwitch;
        this.switchStatus = switchStatus;
    }

    public String getHead() {
        return head;
    }
    public String getTail() {
        return tail;
    }

    public boolean isHasArrow() {
        return hasArrow;
    }

    public boolean isHasSwitch() {
        return hasSwitch;
    }

    public boolean isSwitchStatus() {
        return switchStatus;
    }
}
