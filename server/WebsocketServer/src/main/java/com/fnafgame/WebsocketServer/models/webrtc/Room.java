package com.fnafgame.WebsocketServer.models.webrtc;

import com.fnafgame.WebsocketServer.models.Client;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;

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

    public Client getHost() {
        return host;
    }

    public List<Client> getSourceList() {
        return sourceList;
    }

    public Client getClientById(String id) {
        for(Client source : sourceList) {
            if(source.getId().equals(id)) return source;
        }
        throw new NoSuchElementException();
    }

    public void addSource(Client source) {
        this.sourceList.add(source);
    }

    public static String generateCode() {
        final String characters = "AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz0123456789";
        String code = "";
        Random r = new Random();

        for(int i = 0; i < 4; i++) {
            code += characters.charAt(r.nextInt(characters.length()));
        }

        code += "-";

        for(int i = 0; i < 4; i++) {
            code += characters.charAt(r.nextInt(characters.length()));
        }

        return code;
    }
}
