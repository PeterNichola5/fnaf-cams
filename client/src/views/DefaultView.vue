<template>
    <div v-if="!connected">WAITING FOR CONNECTION TO FAZBEAR SERVERS . . .</div>
    <main v-else>
      <button @click="joinRoom">Join A Room</button>
      <button @click="hostRoom">Create A Room</button>
      <input v-show="joiningGame" v-model="code" placeholder="####-####"/>
    </main>
  </template>
  
  <script>
  
    export default {
      data() {
        return {
          connected: false,
          joiningGame: false,
          code: ""
        };
      },

      mounted() {
        this.connect();
      },
  
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
                const roomCode = JSON.parse(tick.body).roomCode;
                console.log(`assigned role: ${myRole}`);
  
                this.$store.commit('SET_ROLE', myRole);

  
                //Once role is determined, we don't need to be subscribed to the "/user/queue/role" endpoint any longer
                roleSub.unsubscribe();
  
                //Switches to hostview if the role assigned is HOST
                if(this.$store.state.role === 'HOST') {
                    this.$store.commit('SET_ROOMCODE', roomCode);
                    this.$router.push({ name: 'host' });
                } else {
                    console.error(`Unexpected Role: ${this.$store.state.role}`);
                }
              });
            },
            error => {
              console.log(error);
              this.connected = false;
            }
          );
        },
 
        hostRoom() {
            this.stompClient.send('/app/role', 'HOST');
        },

        joinRoom() {
            //go to new view
            this.joiningGame = !this.joiningGame;
        }
        
      },
  
      components: {
      }
    };
  </script>
  