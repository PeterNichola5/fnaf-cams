package com.fnafgame.WebsocketServer.models;

import com.fnafgame.WebsocketServer.models.webrtc.WebRTCPacket;

import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;

public class Client {
    private final String id;

    private final String roomCode;
    private Queue<WebRTCPacket> backlog;
    private String status;

    public Client(String id, String roomCode) {
        this.id = id;
        this.roomCode = roomCode;
        status = "OPEN";
        backlog = new LinkedList<>();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public WebRTCPacket getNextMsg() throws NoSuchElementException {
        return this.backlog.remove();
    }

    public void addToBacklog(WebRTCPacket msg) {
        this.backlog.add(msg);
    }
}
