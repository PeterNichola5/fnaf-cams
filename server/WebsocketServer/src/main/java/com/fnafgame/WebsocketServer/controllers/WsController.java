package com.fnafgame.WebsocketServer.controllers;

import com.fnafgame.WebsocketServer.models.FocusPacket;
import com.fnafgame.WebsocketServer.models.Offer;
import com.fnafgame.WebsocketServer.models.RoleAssignment;
import com.fnafgame.WebsocketServer.models.SDP;
import jakarta.websocket.OnClose;
import jakarta.websocket.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.security.Principal;

@CrossOrigin
@Controller
public class WsController {

    private String hostId;
    private String focusedSrcId;
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    public WsController() {

        this.hostId = null;
        this.focusedSrcId = null;
    }

    @OnClose
    public void closedConnectionHandler(Session session) {
        System.out.println("CONNECTION CLOSED:");
        System.out.println(session.getId());
        System.out.println(session.getUserPrincipal().getName());
    }


    @MessageMapping("/role")
    @SendToUser("/queue/role")
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

    @MessageMapping("/offerResponse/{srcId}")
    public void sendAnswerToUser(@RequestBody SDP responseSdp, @DestinationVariable String srcId) {
        simpMessagingTemplate.convertAndSendToUser(srcId, "/queue/answer", responseSdp);
    }

    @MessageMapping("/offer")
    @SendTo("/topic/offer")
    public Offer sendOfferToHost(@RequestBody SDP sdp, Principal principal) {
        Offer offer = null;
        String userId = principal.getName();
        offer = new Offer(userId, sdp);
        return offer;
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
    public String hostToSrc(@PathVariable String id) {
        return "id: " + id;
    }

}
