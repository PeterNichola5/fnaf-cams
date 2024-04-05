import { createStore as _createStore } from 'vuex';
import SockJS from "sockjs-client/dist/sockjs";
import Stomp from "webstomp-client";

export function createStore() {
  let store = _createStore({
    state: {
      userId: '',
      role: '',
      socket: null,
      stompClient: null,
      hostProperties: {
        connectionIndexes: {},
        connections: [],
      },
    },

    mutations: {
      SET_ID(state, id) {
        this.state.userId = id;
      },
      SET_ROLE(state, userRole) {
        this.state.role = userRole;
      },
      CREATE_WS_CONNECTION(state, url) {
        this.state.socket = new SockJS(url);
        this.state.stompClient = Stomp.over(this.state.socket);
      },

      ADD_CONNECTION_INDEX(state, messagerId) {
        this.state.hostProperties.connectionIndexes[messagerId] = this.state.hostProperties.connections.length;
      },
      ADD_WEBRTC_CONNECTION(state, connection) {
        const msgIndex = this.state.hostProperties.connections.length;

        this.state.hostProperties.connections.push(connection);

        this.state.hostProperties.connections[msgIndex].pc.ondatachannel = event => {
          this.state.hostProperties.connections[msgIndex].messages = event.channel;

          this.state.hostProperties.connections[msgIndex].messages.onopen = event => {
            console.log(event);
            this.state.hostProperties.connections[msgIndex].messages.send("INITIAL MESSAGE");
          }

          this.state.hostProperties.connections[msgIndex].messages.onmessage = event => {
            console.log(`MESSAGE: ${event.data}`);
          }
        }

        this.state.hostProperties.connections[msgIndex].pc.ontrack = e => {
          this.state.hostProperties.connections[msgIndex].stream = e.streams[0];
        }
      },
      ADD_ICE_CANDIDATE(state, update) {
        this.state.hostProperties.connections[update.index].pc.addIceCandidate(update.candidate);
      }

    },

    getters: {
      getConnectionIndexById: (state) => id => {
        return state.hostProperties.connectionIndexes[id];
      }
    }

  });
  return store;
}