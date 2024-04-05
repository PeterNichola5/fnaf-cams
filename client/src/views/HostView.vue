<template>
    <main>
      <div>
        <div v-if="!connections[0]">PLEASE WAIT FOR SOURCES TO CONNECT</div>
        <div v-else>
          <ul>
            <li v-for="connection in connections" :key="connection.id">
              <video  ref="video" :srcObject="connection.stream" autoplay></video>
            </li>
          </ul>
        </div>
      </div>
      <div>
        <button @click="unsub">Start</button>
      </div>
    </main>
</template>

<script>

// import SockJS from "sockjs-client/dist/sockjs";
// import Stomp from "webstomp-client";

export default {
  data() {
    return {
      connectionIndexs: {},
      connections: [],
      rtcSub: null
    };
  },

  mounted() {
    this.stompClient = this.$store.state.stompClient;
    this.rtcSub = this.stompClient.subscribe("/topic/webrtc_msg", this.handleMsg);
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

      //Checks to see if a connection with the client has been initialized
      if(msgIndex === undefined) {
        this.connectionIndexs[messagerId] = this.connections.length;
        msgIndex = this.connections.length;
        this.connections.push({
          id: messagerId, 
          pc: new RTCPeerConnection(), 
          stream: new MediaStream(), 
          messages: null
        });

        this.connections[msgIndex].pc.ondatachannel = event => {
          this.connections[msgIndex].messages = event.channel;

          this.connections[msgIndex].messages.onopen = event => {
            console.log(event);
            this.connections[msgIndex].messages.send("INITIAL MESSAGE");
          }

          this.connections[msgIndex].messages.onmessage = event => {
            console.log(`MESSAGE: ${event.data}`);
          }
        }

        

        this.connections[msgIndex].pc.ontrack = e => {
          this.connections[msgIndex].stream = e.streams[0];
        }
        console.log(this.connections);
      }

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
 
    },

    unsub() {
      this.rtcSub.unsubscribe();
    }
  },

  components: {
  }
};
</script>

<style>

  button {
  
    font-family: "Press Start 2P";
    color: white;
    position: fixed;
    font-size: 2vmin;
    border: 0.625vmin solid white;
    padding: 0.625vmin;
    background: #333;
    display: inline-block;
    z-index: 100004;
    cursor: pointer;
  }
</style>