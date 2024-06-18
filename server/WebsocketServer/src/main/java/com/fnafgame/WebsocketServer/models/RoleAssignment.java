package com.fnafgame.WebsocketServer.models;

import com.fnafgame.WebsocketServer.RoleCategory;

import java.util.Random;

public class RoleAssignment {
    private final RoleCategory role;
    private final String id;

    private final String roomCode;



    public RoleAssignment(String id, String roomCode, boolean isHost) {
        this.role = isHost? RoleCategory.HOST : RoleCategory.SOURCE;
        this.id = id;
        this.roomCode = roomCode;
    }

    public RoleAssignment() {
        this.role = null;
        this.id = null;
        this.roomCode = null;
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
