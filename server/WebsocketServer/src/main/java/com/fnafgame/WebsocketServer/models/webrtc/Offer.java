package com.fnafgame.WebsocketServer.models.webrtc;

import com.fnafgame.WebsocketServer.models.webrtc.SDP;

public class Offer {
    private final String id;
    private final SDP offer;

    public Offer(String id, SDP offer) {
        this.id = id;
        this.offer = offer;
    }

    public SDP getOffer() {
        return offer;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "ID: " + this.id + ", \nOFFER: {" + this.offer.toString() + "}";
    }
}
