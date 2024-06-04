package com.fnafgame.WebsocketServer.models.webrtc;

public class WebRTCPacket<T> {
    private final String senderId;
    private final String receiverId;
    private final WebRTCPacketType type;
    private final T content;



    public WebRTCPacket(String senderId, String receiverId, WebRTCPacketType type, T content) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.type = type;
        this.content = content;
    }

    public T getContent() {
        return content;
    }

    public WebRTCPacketType getType() {
        return type;
    }

    public String getSenderId() {
        return senderId;
    }
    public String getReceiverId() {
        return receiverId;
    }

    @Override
    public String toString() {
        return "SENDER_ID: " + this.senderId + "RECEIVER_ID: " + this.receiverId + ", TYPE: " + this.type + ", CONTENT: \n{" + this.content.toString() + "}";
    }
}
