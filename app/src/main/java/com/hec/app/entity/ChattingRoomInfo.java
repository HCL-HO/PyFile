package com.hec.app.entity;

/**
 * Created by wangxingjian on 2017/3/20.
 */

public class ChattingRoomInfo {
    private String username;
    private String ticket;
    private String room_label;

    public ChattingRoomInfo(String room_label, String username, String ticket) {
        this.room_label = room_label;
        this.username = username;
        this.ticket = ticket;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public String getRoom() {
        return room_label;
    }

    public void setRoom(String room) {
        this.room_label = room_label;
    }
}
