package com.fnafgame.WebsocketServer.models.webrtc;

public class ICECandidate {
    private final String candidate;
    private final String sdpMLineIndex;
    private final String sdpMid;
    private final String usernameFragment;


    public ICECandidate(String candidate, String sdpMid, String sdpMLineIndex, String usernameFragment) {
        this.candidate = candidate;
        this.sdpMid = sdpMid;
        this.sdpMLineIndex = sdpMLineIndex;
        this.usernameFragment = usernameFragment;
    }

    public String getUsernameFragment() {
        return usernameFragment;
    }

    public String getSdpMid() {
        return sdpMid;
    }

    public String getSdpMLineIndex() {
        return sdpMLineIndex;
    }

    public String getCandidate() {
        return candidate;
    }

    @Override
    public String toString() {
        return String.format("candidate: %s, sdpMid: %s, sdpMLineIndex: %s, usernameFragment: %s", this.candidate, this.sdpMid, this.sdpMLineIndex, this.usernameFragment);
    }
}
