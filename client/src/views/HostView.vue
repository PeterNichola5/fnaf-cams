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
      let answer;
      const msg = JSON.parse(tick.body);
      const msgType = msg.type;
      const messagerId = msg.id;
      const content = msg.content;
      let msgIndex = this.connectionIndexs[messagerId];
      console.log(`BODY: ${JSON.parse(tick.body).type}`);

      if(msgIndex === null) {
        this.connectionIndexs[messagerId] = this.connections.length;
        msgIndex = this.connections.length;
        this.connections.push({id: messagerId, pc: new RTCPeerConnection(), stream: new MediaStream()})
      }
      switch(msgType) {
        case 'ICE_CANDIDATE':
          //HANDLE ICE CANDIDATE
          this.connections[msg].pc.add
        break;
        case 'OFFER':
          //HANDLE OFFER
        break;
        default:
          console.error("UNKNOWN MESSAGE TYPE: " + msgType);
        break;
      }
      const connectionId = JSON.parse(tick.body).id;
      const sdpOffer = JSON.parse(tick.body).offer;
      this.numOfConnections = this.numOfConnections + 1;
      this.connections[`src${this.numOfConnections}`] = {
        id: messagerId,
        pc: new RTCPeerConnection(),
        stream: new MediaStream()
      };
      this.connections[`stream${this.numOfConnections}`].pc.setRemoteDescription(sdpOffer).then(
        () => {
          this.connections[`stream${this.numOfConnections}`].pc.createAnswer()
        }
      ).then(result => {
        answer = result;
        this.connections[`stream${this.numOfConnections}`].pc.setLocalDescription(result)
        }
        
      ).then(() => {
        console.log(answer);

        this.stompClient.send('/app/offerResponse/' + connectionId, `${JSON.stringify(answer)}`);
        this.connections[`stream${this.numOfConnections}`].pc.ontrack = e => {
          console.log(e.streams);
        }

        console.log(this.connections[`stream${this.numOfConnections}`].pc);
      });


      
    }
  },

  components: {
  }
};
</script>