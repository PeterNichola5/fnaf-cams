

<template>
  <div v-if="connected">Waiting for connection</div>
  <main v-else>
    HOME
    <p>{{ devices }}</p>
    <div>
      <video ref="video"></video>
      <button v-on:click="toggleLight">Flashlight</button>
    </div>
  </main>
</template>

<script>

import SockJs from "sockjs-client/dist/sockjs";
import Stomp from "webstomp-client";

export default {
  data() {
    return {
      devices: null,
      media: null,
      isLightOn: false,
      recieved_messages: [],
      send_message: null,
      connected: false
    }
  },
  mounted() {
    this.connect();

    let availableDevices = navigator.mediaDevices;
    availableDevices.enumerateDevices().then(results => this.devices = results);
    console.log(availableDevices);
    availableDevices.getUserMedia({
      audio: true,
      video: { width: 500, height: 500, facingMode: 'environment'},
    }).then(stream => {
      this.$refs.video.srcObject = stream;
      this.$refs.video.play();
      this.media = stream;
    });
  },
  methods: {
    toggleLight() {
      if(this.media === null) return;
      this.isLightOn = !this.isLightOn;
      console.log(this.media.getVideoTracks()[0].getSettings());
      this.media.getVideoTracks()[0].applyConstraints({
        torch: true
      });

      this.devices = this.media.getVideoTracks()[0].getConstraints();

    },
    connect() {
      this.socket = new SockJs("https://localhost:8080/ws");
      this.stompClient = Stomp.over(this.socket);
      this.stompClient.connect({},
        frame => {
          this.connected = true;
          console.log(`frame: ${frame}`);
          this.stompClient.subscribe("/role", tick => {
            console.log(tick);
            this.recieved_messages.push(JSON.parse(tick.body).content);
          });
        },
        error => {
          console.log(error);
          this.connected = false;
        }
      );
    }
  },
  components: {
  }
}
</script>
