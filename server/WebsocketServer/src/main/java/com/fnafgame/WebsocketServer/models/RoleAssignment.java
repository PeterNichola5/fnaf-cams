package com.fnafgame.WebsocketServer.models;

import com.fnafgame.WebsocketServer.RoleCategory;

public class RoleAssignment {
    private final RoleCategory role;
    private final String id;

    public RoleAssignment(String id) {
        this.role = RoleCategory.SOURCE;
        this.id = id;
    }

    public RoleAssignment(String id, boolean isHost) {
        this.role = RoleCategory.HOST;
        this.id = id;
    }


    public RoleCategory getRole() {
        return role;
    }

    public String getId() {
        return id;
    }
}
