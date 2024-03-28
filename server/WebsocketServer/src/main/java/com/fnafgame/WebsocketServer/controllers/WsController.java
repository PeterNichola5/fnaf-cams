package com.fnafgame.WebsocketServer.controllers;

import com.fnafgame.WebsocketServer.models.Client;
import com.fnafgame.WebsocketServer.models.FocusPacket;
import com.fnafgame.WebsocketServer.models.webrtc.*;
import com.fnafgame.WebsocketServer.models.RoleAssignment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@CrossOrigin
@Controller
public class WsController {

    private String hostId;
    private String focusedSrcId;

    private Map<String, Client> clients;
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    public WsController() {

        this.hostId = null;
        this.focusedSrcId = null;
        this.clients = new HashMap<>();
    }



    //updates the processing status of Client to 'OPEN'
    @MessageMapping("/open-status")
    public void updateStatusToOpen(Principal user) {
        String id = user.getName();
        try {
            //If there are messages in the client's queue, the next message is sent and status is kept as 'PROCESSING'
            WebRTCPacket packet = this.clients.get(id).getNextMsg();
            simpMessagingTemplate.convertAndSend("/topic/webrtc_msg", packet);
        } catch(NoSuchElementException e) {
            this.clients.get(id).setStatus("OPEN");
        }
    }

    //updates the processing status of Client to 'PROCESSING'
    @MessageMapping("processing-status")
    public void updateStatusToProcessing(Principal user) {
        String id = user.getName();

        this.clients.get(id).setStatus("PROCESSING");
    }


    //Assigns a role to the Client
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

        this.clients.put(user.getName(), new Client(user.getName()));

        return role;
    }

    //Path for Host to send answer to client -- TODO: Rework channel to act as generic communication between host and client
    @MessageMapping("/answer/{srcId}")
    public void sendAnswerToUser(@RequestBody SDP responseSdp, @DestinationVariable String srcId) {
        System.out.println("answer received . . . \n============\n" + responseSdp.toString());

        WebRTCPacket<SDP> answer = new WebRTCPacket<>(this.hostId, WebRTCPacketType.ANSWER, responseSdp);

        System.out.println("Packet Constructed ------------[ANSWER]------------: \n" + answer.toString());

        if(this.clients.get(srcId).getStatus().equals("PROCESSING")) {
            this.clients.get(srcId).addToBacklog(answer);
        } else {
            simpMessagingTemplate.convertAndSendToUser(srcId, "/queue/host_msg", answer);
        }
    }


    //Path for Clients to send their offers to the host
    @MessageMapping("/offer")
    public void sendOfferToHost(@RequestBody SDP sdp, Principal principal) {
        System.out.println("offer received . . . \n============\n" + sdp.toString());

        WebRTCPacket<SDP> offer = null;
        String userId = principal.getName();
        offer = new WebRTCPacket<>(userId, WebRTCPacketType.OFFER, sdp);

        System.out.println("Packet Constructed ------------[OFFER]------------: \n" + offer.toString());

        //Checks whether to store offer in queue or to send it straight away
        if(this.clients.get(this.hostId).getStatus().equals("PROCESSING")) {
            this.clients.get(this.hostId).addToBacklog(offer);
        } else {
            simpMessagingTemplate.convertAndSend("/topic/webrtc_msg", offer);
        }
    }

    //Path for Clients to send ice candidates to host
    @MessageMapping("/ice-candidate")
    public void sendCandidateToHost(@RequestBody ICECandidate iceCandidate, Principal principal) {
        System.out.println("candidate received . . . \n============\n" + iceCandidate.toString());

        WebRTCPacket<ICECandidate> newCandidate = null;
        String userId = principal.getName();
        newCandidate = new WebRTCPacket<>(userId, WebRTCPacketType.ICE_CANDIDATE, iceCandidate);

        System.out.println("Packet Constructed: \n" + newCandidate.toString());

        //Checks whether to store candidate in queue or to send it straight away
        if(this.clients.get(this.hostId).getStatus().equals("PROCESSING")) {
            this.clients.get(this.hostId).addToBacklog(newCandidate);
        } else {
            simpMessagingTemplate.convertAndSend("/topic/webrtc_msg", newCandidate);
        }
    }

    @MessageMapping("/ice-candidate/{srcId}")
    public void sendCandidateToHost(@RequestBody ICECandidate iceCandidate, @DestinationVariable String srcId) {
        System.out.println("candidate received . . . \n============\n" + iceCandidate.toString());

        WebRTCPacket<ICECandidate> newCandidate = new WebRTCPacket<>(this.hostId, WebRTCPacketType.ICE_CANDIDATE, iceCandidate);

        System.out.println("Packet Constructed: \n" + newCandidate.toString());

        //Checks whether to store candidate in queue or to send it straight away
        if(this.clients.get(srcId).getStatus().equals("PROCESSING")) {
            this.clients.get(srcId).addToBacklog(newCandidate);
        } else {
            simpMessagingTemplate.convertAndSendToUser(srcId, "/topic/webrtc_msg", newCandidate);
        }
    }

    @MessageMapping("/end-of-candidates")
    public void endClientCandidates(Principal user) {
        WebRTCPacket<ICECandidate> end = new WebRTCPacket<>(user.getName(), WebRTCPacketType.ICE_CANDIDATE, null);
        if(this.clients.get(this.hostId).getStatus().equals("PROCESSING")) {
            this.clients.get(this.hostId).addToBacklog(end);
        } else {
            simpMessagingTemplate.convertAndSend("/queue/host_msg", end);
        }
    }

    @MessageMapping("/end-of-candidates/{srcId}")
    public void endClientCandidates(@DestinationVariable String srcId) {
        WebRTCPacket<ICECandidate> end = new WebRTCPacket<>(this.hostId, WebRTCPacketType.ICE_CANDIDATE, null);
        if(this.clients.get(srcId).getStatus().equals("PROCESSING")) {
            this.clients.get(srcId).addToBacklog(end);
        } else {
            simpMessagingTemplate.convertAndSendToUser(srcId, "/queue/host_msg", end);
        }
    }

    @MessageMapping("/echo")
    @SendTo("/topic/webrtc_msg")
    public WebRTCPacket<String> clientEcho(Principal user) {
        return new WebRTCPacket<String>(user.getName(), WebRTCPacketType.ECHO, "ANSWER PROCESSED AND ACCEPTED");
    }

    @MessageMapping("/echo/{srcId}")
    public void hostEcho(Principal user, @DestinationVariable String srcId) {
        WebRTCPacket<String> echo = new WebRTCPacket<>(user.getName(), WebRTCPacketType.ECHO, "OFFER PROCESSED AND ACCEPTED");
        this.simpMessagingTemplate.convertAndSendToUser(srcId, "/queue/host_msg", echo);
    }

    //TODO: Path for host to communicate to Clients about camera focus
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
