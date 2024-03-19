

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
  import SockJS from "sockjs-client/dist/sockjs";
  import Stomp from "webstomp-client";

  export default {
    data() {
      return {
        devices: null,
        media: null,
        isLightOn: false,
        send_message: null,
        connected: false,
        background: 'black',
        pc: null,
        pendingIceCandidates: []

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
          this.$store.commit('CREATE_WS_CONNECTION', "https://localhost:8080/ws");
        this.stompClient = this.$store.state.stompClient;

        this.stompClient.connect({},
          frame => {
            this.connected = true;
            console.log(this.$store);
            this.$store.commit('SET_ID', frame.headers["user-name"]);

            let roleSub = this.stompClient.subscribe("/user/queue/role", tick => {
              const myRole = JSON.parse(tick.body).role;
              console.log(`role response: ${tick}`);
              console.log(`assigned role: ${myRole}`);

              this.$store.commit('SET_ROLE', myRole);

              roleSub.unsubscribe();
              console.log(`role in State: ${this.$store.state.role}`);
              if(this.$store.state.role === 'HOST') {
                this.$router.push({ name: 'host' });
              } else {
                // this.stompClient.subscribe("/user/topic/sources", srcTick => {
                //   this.background = JSON.parse.apply(srcTick.body).isFocused ? 'white' : 'black';
                // });
                this.establishOffer();
                this.stompClient.subscribe("/user/queue/host_msg", ansTick => {

                  const packet = {
                    connection: this.pc,
                    desc: JSON.parse(ansTick.body)
                  };
                  this.pc.setRemoteDescription(packet.desc);
                  console.log(this.pc);
                  // this.$store.commit('SET_REMOTE_LOCATION', packet)
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

        

        let hostOffer = null;

        this.pc.createOffer(offerOptions)
          .then(result => {
            hostOffer = result;
            this.pc.setLocalDescription(result);
          })
          .then(() => {
            this.stompClient.send("/app/offer", JSON.stringify(hostOffer));
            this.pc.addEventListener('icecandidate', e => {
              console.log('new ICE candidate found: ' + e.candidate);
              if(e.candidate) this.stompClient.send("/app/ice-candidate", JSON.stringify(e.candidate));
              //TODO send candidate to Host
            });
          });
        
      }
    },

    components: {
    }
  };
</script>
