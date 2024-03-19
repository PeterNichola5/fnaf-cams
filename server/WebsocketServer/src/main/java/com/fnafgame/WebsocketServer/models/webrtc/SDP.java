package com.fnafgame.WebsocketServer.models.webrtc;

public class SDP {
    private final String type;
    private final String sdp;

    public SDP(String type, String sdp) {
        this.type = type;
        this.sdp = sdp;
    }

    public String getSdp() {
        return sdp;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "TYPE: " + this.type + ", SDP: " + this.sdp;
    }
}
