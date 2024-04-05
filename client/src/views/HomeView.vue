

<template>
  <div v-if="!connected">Waiting for connection</div>
  <main v-else>
    HOME
    <p>{{ devices }}</p>
    <div>
      <video ref="video" :srcObject="media" autoplay></video>
    </div>
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
        background: 'black',
        pc: null,
        messages: null,
        pendingIceCandidates:[]

      };
    },
    mounted() {
      let availableDevices = navigator.mediaDevices;
      availableDevices.enumerateDevices().then(results => this.devices = results);

      availableDevices.getUserMedia({
        audio: true,
        video: { width: 500, height: 500, facingMode: 'environment'},
      }).then(stream => {
        this.media = stream;
        this.connect();
      });
    },
  //   beforeUnmount() {
  //     this.stompClient.disconnect({});
  //   },
    methods: {
      connect() {
        //Initializes websocket connection
        this.$store.commit('CREATE_WS_CONNECTION', "https://localhost:8080/ws");
        this.stompClient = this.$store.state.stompClient;

        this.stompClient.connect({},
          frame => {
            this.connected = true;
            this.$store.commit('SET_ID', frame.headers["user-name"]);

            //Grabs role from server and reacts accordingly
            let roleSub = this.stompClient.subscribe("/user/queue/role", tick => {
              const myRole = JSON.parse(tick.body).role;
              console.log(`assigned role: ${myRole}`);

              this.$store.commit('SET_ROLE', myRole);

              //Once role is determined, we don't need to be subscribed to the "/user/queue/role" endpoint any longer
              roleSub.unsubscribe();

              //Switches to hostview if the role assigned is HOST
              if(this.$store.state.role === 'HOST') {
                this.$router.push({ name: 'host' });
              } else {
                // this.stompClient.subscribe("/user/topic/sources", srcTick => {
                //   this.background = JSON.parse.apply(srcTick.body).isFocused ? 'white' : 'black';
                // });
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
              }
            });

            this.stompClient.send('/app/role', 'ROLE REQUEST');
          },
          error => {
            console.log(error);
            this.connected = false;
          }
        );
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
        //TODO: ADD DATACHANNEL 
        let hostOffer = null;

        this.pc.createOffer(offerOptions)
          .then(result => {
            hostOffer = result;
            this.pc.setLocalDescription(result);
          }).then(() => {
            console.log('pls:' + this.pc.localDescription);
            this.pc.addEventListener('icecandidate', e => {
              console.log('new ICE candidate found');

              console.log(`LC: ${this.pc.localDescription}`);
              if(e.candidate) {
                this.pendingIceCandidates.push(e.candidate);
                console.log(this.pendingIceCandidates);
              }
              else {
                this.pendingIceCandidates.forEach(candidate => {
                  this.stompClient.send('/app/ice-candidate', JSON.stringify(candidate));
                });
                this.stompClient.send("/app/end-of-candidates", null);
              }
            });
            this.stompClient.send("/app/offer", JSON.stringify(hostOffer));
            
          });
        
      }
    },

    components: {
    }
  };
</script>
