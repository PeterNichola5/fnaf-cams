<template>
    <main></main>
</template>

<script>

// import SockJS from "sockjs-client/dist/sockjs";
// import Stomp from "webstomp-client";

export default {
  data() {
    return {
      numOfConnections: 0,
      connections: {}
    };
  },
  mounted() {
    this.stompClient = this.$store.state.stompClient;
    this.stompClient.subscribe("/topic/webrtc_msg", this.handleOffer);
  },
  beforeUnmount() {
    // this.stompClient.disconnect({});
  },
  methods: {
    handleOffer(tick) {
      let answer;
      console.log(`BODY: ${JSON.parse(tick.body)}`);
      const connectionId = JSON.parse(tick.body).id;
      const sdpOffer = JSON.parse(tick.body).offer;
      this.connections[`stream${++this.numOfConnections}`] = {
        id: connectionId,
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