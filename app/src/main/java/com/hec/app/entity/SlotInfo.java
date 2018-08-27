package com.hec.app.entity;

/**
 * Created by wangxingjian on 2016/11/17.
 */

public class SlotInfo {
    private long ID;
    private String Memo;
    private double NoteMoney;
    private String NoteTime;
    private int OrderState;
    private String PlayTypeName;
    private double WinMoney;
    private double ValidMoney;

    public long getID() {
        return ID;
    }

    public void setID(long ID) {
        this.ID = ID;
    }

    public String getMemo() {
        return Memo;
    }

    public void setMemo(String memo) {
        Memo = memo;
    }

    public double getNoteMoney() {
        return NoteMoney;
    }

    public void setNoteMoney(double noteMoney) {
        NoteMoney = noteMoney;
    }

    public String getNoteTime() {
        return NoteTime;
    }

    public void setNoteTime(String noteTime) {
        NoteTime = noteTime;
    }

    public int getOrderState() {
        return OrderState;
    }

    public void setOrderState(int orderState) {
        OrderState = orderState;
    }

    public String getPlayTypeName() {
        return PlayTypeName;
    }

    public void setPlayTypeName(String playTypeName) {
        PlayTypeName = playTypeName;
    }

    public double getWinMoney() {
        return WinMoney;
    }

    public void setWinMoney(double winMoney) {
        WinMoney = winMoney;
    }

    public double getValidMoney() {
        return ValidMoney;
    }

    public void setValidMoney(double validMoney) {
        ValidMoney = validMoney;
    }
}
