package com.fnafgame.WebsocketServer.controllers;

import com.fnafgame.WebsocketServer.models.Client;
import com.fnafgame.WebsocketServer.models.webrtc.*;
import com.fnafgame.WebsocketServer.models.RoleAssignment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.sql.SQLOutput;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@CrossOrigin
@Controller
public class WsController {
    private Map<String, Room> rooms;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    public WsController() {
        this.rooms = new HashMap<>();
    }



    //updates the processing status of Client to 'OPEN'
    @MessageMapping("/open-status/{roomCode}")
    public void updateStatusToOpen(Principal user, @DestinationVariable String roomCode) {
        String id = user.getName();
        Room room = this.rooms.get(roomCode);
        String roomHostId = room.getHost().getId();

        try {
            //If there are messages in the client's queue, the next message is sent and status is kept as 'PROCESSING'
            WebRTCPacket packet = room.getClientById(id).getNextMsg();



            if(id.equals(roomHostId)) simpMessagingTemplate.convertAndSend("/topic/webrtc_msg/" + roomCode, packet);
            else simpMessagingTemplate.convertAndSendToUser(id, "/queue/host_msg", packet);
            
        } catch(NoSuchElementException e) {
            if(id.equals(roomHostId)) room.getHost().setStatus("OPEN");
            else room.getClientById(id).setStatus("OPEN");
        }
    }

    //updates the processing status of Client to 'PROCESSING'
    @MessageMapping("processing-status/{roomCode}")
    public void updateStatusToProcessing(Principal user, @DestinationVariable String roomCode) {
        String id = user.getName();
        if(id.equals(this.rooms.get(roomCode).getHost().getId())) this.rooms.get(roomCode).getHost().setStatus("PROCESSING");
        else this.rooms.get(roomCode).getClientById(id).setStatus("PROCESSING");
    }


    //Assigns a role to the Client
    @MessageMapping("/role")
    @SendToUser("/queue/role")
    public RoleAssignment assignRole(@RequestBody String body, Principal user) {
        RoleAssignment role = null;
        if(body.equals("HOST")) {
            //generate unique room code
            String newCode = Room.generateCode();
            boolean isCodeUnique = false;

            while(!isCodeUnique) {
                if(rooms.containsKey(newCode)) {
                    System.out.println("key: " + newCode + " already used");
                    newCode = Room.generateCode();
                } else {
                    isCodeUnique = true;
                }
            }

            role = new RoleAssignment(user.getName(), newCode, true);
            Client newHost = new Client(user.getName(), newCode);
            rooms.put(newCode, new Room(newHost, newCode));
        } else {
            if(rooms.get(body) == null) {
                return new RoleAssignment();
            }

            role = new RoleAssignment(user.getName(), body, false);
            rooms.get(body).addSource(new Client(user.getName(), body));
        }

//        if(this.hostId == null) {
//            role = new RoleAssignment(user.getName(), true);
//            this.hostId = user.getName();
//        } else {
//            role = new RoleAssignment(user.getName(), "code");
//        }
//
//        this.clients.put(user.getName(), new Client(user.getName()));

        System.out.println(role.getRoomCode());
        return role;
    }

    //Path for Host to send answer to client
    @MessageMapping("/answer/{srcId}")
    public void sendAnswerToUser(Principal user, @RequestBody SDP responseSdp, @DestinationVariable String srcId) {
        System.out.println("answer received . . . \n============\n");

        WebRTCPacket<SDP> answer = new WebRTCPacket<>(user.getName(), srcId, WebRTCPacketType.ANSWER, responseSdp);

        System.out.println("Packet Constructed ------------[ANSWER]------------: \n");

        String roomCode = null;

        for(Room room : rooms.values()) {
            if(room.getHost().getId().equals(user.getName())) {
                roomCode = room.getCode();
                break;
            }
        }


        if(roomCode == null) {
            //TODO: Add handler for null roomCode
        }

        if(this.rooms.get(roomCode).getClientById(srcId).getStatus().equals("PROCESSING")) {
            this.rooms.get(roomCode).getClientById(srcId).addToBacklog(answer);
        } else {
            simpMessagingTemplate.convertAndSendToUser(srcId, "/queue/host_msg", answer);
        }
    }


    //Path for Clients to send their offers to the host
    @MessageMapping("/offer/{roomCode}")
    public void sendOfferToHost(@RequestBody SDP sdp, Principal principal, @DestinationVariable String roomCode) {
        System.out.println("offer received . . . \n============\n");

        Room room = this.rooms.get(roomCode);

        WebRTCPacket<SDP> offer = null;
        String userId = principal.getName();
        offer = new WebRTCPacket<>(userId, room.getHost().getId(), WebRTCPacketType.OFFER, sdp);

        System.out.println("Packet Constructed ------------[OFFER]------------: \n");



        //Checks whether to store offer in queue or to send it straight away
        if(room.getHost().getStatus().equals("PROCESSING")) {
            room.getHost().addToBacklog(offer);
        } else {
            simpMessagingTemplate.convertAndSend("/topic/webrtc_msg/" + roomCode, offer);
        }
    }

    //Path for Clients to send ice candidates to host
    @MessageMapping("/src-ice-candidate/{roomCode}")
    public void sendCandidateToHost(@RequestBody ICECandidate iceCandidate, Principal principal, @DestinationVariable String roomCode) {
        System.out.println("candidate received . . . \n============\n");

        Room room = this.rooms.get(roomCode);

        WebRTCPacket<ICECandidate> newCandidate = null;
        String userId = principal.getName();
        newCandidate = new WebRTCPacket<>(userId, room.getHost().getId(), WebRTCPacketType.ICE_CANDIDATE, iceCandidate);

        System.out.println("Packet Constructed: \n");

        //Checks whether to store candidate in queue or to send it straight away
        if(room.getHost().getStatus().equals("PROCESSING")) {
            room.getHost().addToBacklog(newCandidate);
        } else {
            simpMessagingTemplate.convertAndSend("/topic/webrtc_msg" + roomCode , newCandidate);
        }
    }

    @MessageMapping("/host-ice-candidate/{srcId}")
    public void sendCandidateToHost(@RequestBody ICECandidate iceCandidate, @DestinationVariable String srcId, Principal user) {

        System.out.println("candidate received . . . \n============\n");

        System.out.println("FROM SOURCE TO USER: " + srcId);

        WebRTCPacket<ICECandidate> newCandidate = new WebRTCPacket<>(user.getName(), srcId, WebRTCPacketType.ICE_CANDIDATE, iceCandidate);

        System.out.println("Packet Constructed: \n");

        System.out.println("ICE: " + newCandidate.toString());

        String roomCode = null;

        for(Room room : rooms.values()) {
            if(room.getHost().getId().equals(user.getName())) {
                roomCode = room.getCode();
                break;
            }
        }


        if(roomCode == null) {
            //TODO: Add handler for null roomCode
        }

        //Checks whether to store candidate in queue or to send it straight away
        if(this.rooms.get(roomCode).getClientById(srcId).getStatus().equals("PROCESSING")) {
            this.rooms.get(roomCode).getClientById(srcId).addToBacklog(newCandidate);
        } else {
            simpMessagingTemplate.convertAndSendToUser(srcId, "/queue/host_msg", newCandidate);
        }
    }

    @MessageMapping("/src-end-of-candidates/{roomCode}")
    public void endClientCandidates(Principal user, @DestinationVariable String roomCode) {
        Room room = this.rooms.get(roomCode);

        WebRTCPacket<ICECandidate> end = new WebRTCPacket<>(user.getName(), room.getHost().getId(), WebRTCPacketType.ICE_CANDIDATE, null);
        if(room.getHost().getStatus().equals("PROCESSING")) {
            room.getHost().addToBacklog(end);
        } else {
            simpMessagingTemplate.convertAndSend("/topic/webrtc_msg/" + roomCode, end);
        }
    }

    @MessageMapping("/host-end-of-candidates/{srcId}")
    public void endClientCandidates(@DestinationVariable String srcId, Principal user) {
        WebRTCPacket<ICECandidate> end = new WebRTCPacket<>(user.getName(), srcId, WebRTCPacketType.ICE_CANDIDATE, null);

        String roomCode = null;

        for(Room room : rooms.values()) {
            if(room.getHost().getId().equals(user.getName())) {
                roomCode = room.getCode();
                break;
            }
        }


        if(roomCode == null) {
            //TODO: Add handler for null roomCode
        }

        Room room = this.rooms.get(roomCode);

        if(room.getClientById(srcId).getStatus().equals("PROCESSING")) {
            room.getClientById(srcId).addToBacklog(end);
        } else {
            simpMessagingTemplate.convertAndSendToUser(srcId, "/queue/host_msg", end);
        }
    }
}
