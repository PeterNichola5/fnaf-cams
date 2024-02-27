package com.fnafgame.WebsocketServer.controllers;

import com.fnafgame.WebsocketServer.models.RoleAssignment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class ConnectionController {
    private String numOfConnections;

    public ConnectionController() {
        this.numOfConnections = null;
    }

    @GetMapping("/app/id")
    public RoleAssignment assignRole(Principal user) {
        RoleAssignment role;
        if(this.numOfConnections != null) {
            role = new RoleAssignment(user.getName());
        } else {
            role = new RoleAssignment(user.getName(), true);
            this.numOfConnections = user.getName();
        };

        return role;
    }


}
