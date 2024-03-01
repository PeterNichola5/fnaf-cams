

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
      connected: false,
    }
  },
  mounted() {

    let availableDevices = navigator.mediaDevices;
    availableDevices.enumerateDevices().then(results => this.devices = results);
    console.log(availableDevices);
    availableDevices.getUserMedia({
      audio: true,
      video: { width: 500, height: 500, facingMode: 'environment'},
    }).then(stream => {
      console.log(`STREAM: ${stream.toString()}`)

      this.media = stream;
      this.connect();
    });
  },
  methods: {
    connect() {
      this.socket = new SockJs("https://localhost:8080/ws");
      this.stompClient = Stomp.over(this.socket);
      this.stompClient.connect({},
        frame => {
          this.connected = true;
          console.log(`Connection Status: ${this.connected}`)
          console.log(`frame: ${frame.headers["user-name"]}`);
          this.stompClient.subscribe("/role", tick => {
            console.log(`x: ${tick}`);
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
