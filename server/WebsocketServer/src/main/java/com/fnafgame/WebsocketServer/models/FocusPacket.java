package com.fnafgame.WebsocketServer.models;

public class FocusPacket {
    private final String focusId;
    private final String unfocusId;

    public FocusPacket(String focusId, String unfocusId) {
        this.focusId = focusId;
        this.unfocusId = unfocusId;
    }

    public FocusPacket(String focusId) {
        this.focusId = focusId;
        this.unfocusId = null;
    }


    public String getUnfocusId() {
        return unfocusId;
    }

    public String getFocusId() {
        return focusId;
    }
}

