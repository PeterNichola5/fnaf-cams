<template>
    <main></main>
</template>

<script>

// import SockJS from "sockjs-client/dist/sockjs";
// import Stomp from "webstomp-client";

export default {
  data() {
    return {
      connectionIndexs: {},
      connections: []
    };
  },

  mounted() {
    this.stompClient = this.$store.state.stompClient;
    this.stompClient.subscribe("/topic/webrtc_msg", this.handleMsg);
  },

  beforeUnmount() {
    // this.stompClient.disconnect({});
  },

  methods: {
    handleMsg(tick) {
      this.stompClient.send('/app/processing-status');
      const msg = JSON.parse(tick.body);
      const msgType = msg.type;
      const messagerId = msg.id;
      const content = msg.content;
      let msgIndex = this.connectionIndexs[messagerId];
      console.log(`BODY: ${JSON.parse(tick.body)}`);

      //Checks to see if a connection with the client has been initialized
      if(msgIndex === undefined) {
        this.connectionIndexs[messagerId] = this.connections.length;
        msgIndex = this.connections.length;
        this.connections.push({id: messagerId, pc: new RTCPeerConnection(), stream: new MediaStream()})
        console.log(this.connections);
      }

      console.log(`CURRENT CONNECTION OBJECT: ${msgIndex}`);
      const peerConnection = this.connections[msgIndex].pc;
      switch(msgType) {
        case 'ICE_CANDIDATE':
          //HANDLE ICE CANDIDATE
          peerConnection.addIceCandidate(content);
          this.stompClient.send("/app/open-status");
        break;
        case 'OFFER':
          //HANDLE OFFER
          peerConnection.setRemoteDescription(content)
            .then(() => peerConnection.createAnswer())
            .then(answer => peerConnection.setLocalDescription(answer))
            .then(() => {
              this.stompClient.send(`/app/answer/${messagerId}`, JSON.stringify(peerConnection.localDescription));
              peerConnection.addEventListener('icecandidate', e => {
                console.log('new ICE candidate found');

                //TODO: send ice candidates to pending ice candidates unless remote description has been set
                if(e.candidate) this.stompClient.send("/app/ice-candidate/" + messagerId, JSON.stringify(e.candidate));
                else this.stompClient.send("app/end-of-candidates/" + messagerId, null);
              });

              this.stompClient.send("/app/open-status");
            });
        break;
        default:
          console.error("UNKNOWN MESSAGE TYPE: " + msgType);
        break;
      }
 
    }
  },

  components: {
  }
};
</script>