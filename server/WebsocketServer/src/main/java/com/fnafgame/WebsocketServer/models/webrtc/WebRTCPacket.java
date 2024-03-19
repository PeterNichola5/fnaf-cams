package com.fnafgame.WebsocketServer.models.webrtc;

public class WebRTCPacket<T> {
    private final String id;
    private final WebRTCPacketType type;
    private final T content;



    public WebRTCPacket(String id, WebRTCPacketType type, T content) {
        this.id = id;
        this.type = type;
        this.content = content;
    }

    public T getContent() {
        return content;
    }

    public WebRTCPacketType getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "ID: " + this.id + ", TYPE: " + this.type + ", CONTENT: \n{" + this.content.toString() + "}";
    }
}
