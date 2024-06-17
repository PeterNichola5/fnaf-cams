package com.fnafgame.WebsocketServer.models;

import com.fnafgame.WebsocketServer.RoleCategory;

import java.util.Random;

public class RoleAssignment {
    private final RoleCategory role;
    private final String id;

    private final String roomCode;

    public RoleAssignment(String id, String roomCode) {
        this.role = RoleCategory.SOURCE;
        this.id = id;
        this.roomCode = roomCode;
    }

    public RoleAssignment(String id) {
        this.role = RoleCategory.HOST;
        this.id = id;
        this.roomCode = this.generateCode();
    }

    private String generateCode() {
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


    public RoleCategory getRole() {
        return role;
    }

    public String getId() {
        return id;
    }

    public String getRoomCode() {
        return roomCode;
    }
}
