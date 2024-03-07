package com.fnafgame.WebsocketServer.models;

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
}
