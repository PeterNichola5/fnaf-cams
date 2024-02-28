package com.fnafgame.WebsocketServer.controllers;

import com.fnafgame.WebsocketServer.models.FocusPacket;
import com.fnafgame.WebsocketServer.models.RoleAssignment;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;

import java.security.Principal;

@CrossOrigin
@Controller
public class WsController {

    private String hostId;
    private String focusedSrcId;

    public WsController() {

        this.hostId = null;
        this.focusedSrcId = null;
    }

    @SubscribeMapping("/role")
    public RoleAssignment assignRole(Principal user) {
        RoleAssignment role;
        if(this.hostId == null) {
            role = new RoleAssignment(user.getName(), true);
            this.hostId = user.getName();
        } else {
            role = new RoleAssignment(user.getName());
        }

        return role;
    }

    @MessageMapping("/hostcmd/{id}")
    @SendToUser("/sources")
    public FocusPacket changeFocus(@DestinationVariable String id) {
        FocusPacket cmd;
        if(focusedSrcId == null) {
            cmd = new FocusPacket(id);
        } else {
            cmd = new FocusPacket(id, focusedSrcId);
        }

        this.focusedSrcId = id;
        return cmd;
    }

    @MessageMapping("/hostRTC/{id}")
    @SendTo("/sources")
    public String hostToSrc(@PathVariable int id) {
        return "id: " + id;
    }


    @MessageMapping("/ws")
    @SendTo("/topic/test")
    public String offerToHost() {
        System.out.println("recieved");
        return "tst";
    }
}
