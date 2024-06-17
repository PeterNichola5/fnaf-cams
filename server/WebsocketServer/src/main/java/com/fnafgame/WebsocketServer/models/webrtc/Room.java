package com.fnafgame.WebsocketServer.models.webrtc;

import com.fnafgame.WebsocketServer.models.Client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Room {
    private final Client host;

    private final String code;

    private final List<Client> sourceList;

    public Room(Client host, String code) {
        this.host = host;
        this.code = code;
        this.sourceList = new ArrayList<>();
    }


    public String getCode() {
        return code;
    }
}
