<template>
    <main></main>
</template>

<script>

import SockJS from "sockjs-client/dist/sockjs";
import Stomp from "webstomp-client";

export default {
  data() {
    return {
      numOfConnections: 0,
      connections: {}
    };
  },
  mounted() {
    this.stompClient = this.$store.state.stompClient;
    this.stompClient.subscribe("/topic/offer", this.handleOffer);
  },
  beforeUnmount() {
    this.stompClient.disconnect({});
  },
  methods: {
    async handleOffer(tick) {
      const connectionId = JSON.parse(tick.body).id;
      const sdpOffer = JSON.parse(tick.body).offer;
      this.connections[`stream${++this.numOfConnections}`] = {
        id: connectionId,
        pc: new RTCPeerConnection(),
        stream: new MediaStream()
      };
      await this.connections[`stream${this.numOfConnections}`].pc.setRemoteDescription(sdpOffer);

      const answer = await this.connections[`stream${this.numOfConnections}`].pc.createAnswer();

      await this.connections[`stream${this.numOfConnections}`].pc.setLocalDescription(answer);

      console.log(answer);

      this.stompClient.send('/app/offerResponse/' + connectionId, `${JSON.stringify(answer)}`);
      this.connections[`stream${this.numOfConnections}`].pc.ontrack = e => {
        console.log(e.streams);
      }
    }
  },

  components: {
  }
};
</script>