<template>
    <main>
      <div>
        <h1>Room Code: <span>{{ this.$store.state.roomCode }}</span></h1>
        <div v-if="!this.$store.state.hostProperties.connections[0]">PLEASE WAIT FOR SOURCES TO CONNECT</div>
        <div id="container" v-else>
          <StreamList />
          <footer>
            <button @click="unsub">Start</button>
            <HudComponent />
          </footer>
          
        </div>
      </div>
    </main>
</template>

<script>

import HudComponent from '../components/HudComponent.vue';
import StreamList from '../components/StreamList.vue';

export default {
  data() {
    return {
      rtcSub: null
    };
  },
  components: {
    HudComponent,
    StreamList
  },

  mounted() {
    this.stompClient = this.$store.state.stompClient;
    this.rtcSub = this.stompClient.subscribe("/topic/webrtc_msg/" + this.$store.state.roomCode, this.handleMsg);
  },

  methods: {
    handleMsg(tick) {
      this.stompClient.send('/app/processing-status/' + this.$store.state.roomCode);
      const msg = JSON.parse(tick.body);
      const msgType = msg.type;
      const messagerId = msg.senderId;
      const content = msg.content;
      let msgIndex = this.$store.getters.getConnectionIndexById(messagerId);

      //Checks to see if a connection with the client has been initialized
      if(msgIndex === undefined) {
        this.$store.commit('ADD_CONNECTION_INDEX', messagerId);
        msgIndex = this.$store.state.hostProperties.connections.length;

        const connection = {
          id: messagerId, 
          pc: new RTCPeerConnection(), 
          stream: new MediaStream(), 
          messages: null
        }

        this.$store.commit('ADD_WEBRTC_CONNECTION', connection);
      }

      const peerConnection = this.$store.state.hostProperties.connections[msgIndex].pc;
      let connectionUpdate = {
        index: msgIndex
      };

      switch(msgType) {
        case 'ICE_CANDIDATE':
          //HANDLE ICE CANDIDATE
          connectionUpdate.candidate = content;
          this.$store.commit('ADD_ICE_CANDIDATE', connectionUpdate);
          this.stompClient.send("/app/open-status/" + this.$store.state.roomCode);
        break;
        case 'OFFER':
          //HANDLE OFFER
          peerConnection.setRemoteDescription(content)
            .then(() => peerConnection.createAnswer())
            .then(answer => peerConnection.setLocalDescription(answer))
            .then(() => {
              this.stompClient.send(`/app/answer/${messagerId}`, JSON.stringify(peerConnection.localDescription));
              peerConnection.addEventListener('icecandidate', e => {

                if(e.candidate) this.stompClient.send("/app/host-ice-candidate/" + messagerId, JSON.stringify(e.candidate));
                else this.stompClient.send("app/host-end-of-candidates/" + messagerId, null);
              });

              this.stompClient.send("/app/open-status/" + this.$store.state.roomCode);
            });
        break;
        default:
          console.error("UNKNOWN MESSAGE TYPE: " + msgType);
        break;
      }
 
    },

    unsub() {
      this.rtcSub.unsubscribe();
      this.$store.commit('BEGIN_GAME');
      this.$store.commit('SET_TIME', '12 AM');
      this.$router.push({name: 'guard'});
    }
  },
};
</script>

<style scoped>
  main {
    width: 100%;
  }
  button {
  
    font-family: "Press Start 2P";
    height: 2rem;
    color: white;
    font-size: 2vmin;
    border: 0.625vmin solid white;
    padding: 0.625vmin;
    background: #333;
    display: inline-block;
    z-index: 100004;
    cursor: pointer;
  }

  #container {
    display: flex;
    flex-direction: column;
    width: 100%;
  }

  footer {
    width: 100vw;
    display: flex;
    justify-content: space-between;
    align-items: end;
  }
</style>