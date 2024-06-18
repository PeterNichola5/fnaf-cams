

<template>
  <div v-if="!connected">ESTABLISHING CONNECTION WITH HOST . . .</div>
  <main v-else>
    <h2>YOU ARE <span>{{ camNum }}</span></h2>
    <p>THIS DEVICE HAS BEEN ASSINGED A SOURCE ROLE</p>
    <p>PLEASE SET THE DEVICE AT ITS ASSINGED SPOT AND STANDBY UNTIL THE HOST BEGINS THE GAME</p>
  </main>
</template>

<script>
  // import SockJS from "sockjs-client/dist/sockjs";
  // import Stomp from "webstomp-client";

  export default {
    data() {
      return {
        devices: null,
        media: null,
        connected: false,
        pc: null,
        messages: null,
        camNum: null,
        pendingIceCandidates:[]
      };
    },
    mounted() {
      let availableDevices = navigator.mediaDevices;
      availableDevices.enumerateDevices().then(results => this.devices = results);

      availableDevices.getUserMedia({
        audio: true,
        video: { width: 1920, height: 1080, facingMode: 'environment'},
      }).then(stream => {
        this.media = stream;
        this.connect();
      });
    },

    methods: {
      connect() {
        //Initializes websocket connection
        this.$store.commit('CREATE_WS_CONNECTION', "https://localhost:8080/ws");
        this.stompClient = this.$store.state.stompClient;
        this.connected = true;


        this.establishOffer();

        //TODO: split into its own method for readability
        this.stompClient.subscribe("/user/queue/host_msg", ansTick => {
          this.stompClient.send('/app/processing-status');

          const msg = JSON.parse(ansTick.body);
          console.log(msg)
          const msgType = msg.type;
          const content = msg.content;
          console.log(this.pc);

          switch(msgType) {
            case 'ICE_CANDIDATE':
              //HANDLE ICE CANDIDATE
              this.pc.addIceCandidate(content);
              this.stompClient.send('/app/open-status');
            break;
            case 'ANSWER':
              //HANDLE OFFER
              this.pc.setRemoteDescription(content).then(() => {
                this.stompClient.send('/app/open-status');
              });
            break;
            default:
              console.error("UNKNOWN MESSAGE TYPE: " + msgType);
            break;
          }  
        });
      },
      establishOffer() {
        const offerOptions = {
          iceRestart: false,
          offerToReceiveAudio: false,
          offerToReceiveVideo: false
        };

        this.pc = new RTCPeerConnection();
        console.log(this.media);
        this.media.getTracks().forEach(track => {
          this.pc.addTrack(track, this.media);
        });

        this.messages = this.pc.createDataChannel("Comms");

        this.messages.onopen = event => {
          console.log(event);
        }

        this.messages.onmessage = event => {
          console.log(`NEW MESSAGE: ${event.data}`);
          if(event.data.includes('CAM')) {
            this.camNum = event.data;
            this.connected = true;
          } else if(event.data.includes('START')) {
            this.$store.commit('SET_TIME', '12 AM');
            this.$store.commit('SET_GAME_STATE', 'IN_PROGRESS');
            this.$router.push({ name: 'cam' });
          }
        }

        let hostOffer = null;

        this.pc.createOffer(offerOptions)
          .then(result => {
            hostOffer = result;
            this.pc.setLocalDescription(result);
          }).then(() => {
            this.pc.addEventListener('icecandidate', e => {
              if(e.candidate) {
                this.pendingIceCandidates.push(e.candidate);
              }
              else {
                this.pendingIceCandidates.forEach(candidate => {
                  this.stompClient.send('/app/ice-candidate', JSON.stringify(candidate));
                });
                this.stompClient.send("/app/end-of-candidates", null);
              }
            });
            this.stompClient.send("/app/offer", JSON.stringify(hostOffer));

            const connection = {
              pc: this.pc,
              messages: this.messages
            };

            this.$store.commit('SET_CLIENT_CONNECTION', connection);
            
          });
        
      }
    },

    components: {
    }
  };
</script>
