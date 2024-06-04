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
      srcConnection: null,
      hostProperties: {
        connectionIndexes: {},
        connections: [],
        cameras: [
          {camera: '01', src: null},
          {camera: '02', src: null},
          {camera: '03', src: null},
          {camera: '04', src: null}
        ],
        currentCam: null,
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
      SET_CLIENT_CONNECTION(state, connection) {
        state.srcConnection = connection;
      },
      ADD_CONNECTION_INDEX(state, messagerId) {
        this.state.hostProperties.connectionIndexes[messagerId] = this.state.hostProperties.connections.length;
      },
      ADD_WEBRTC_CONNECTION(state, connection) {
        
        const msgIndex = this.state.hostProperties.connections.length;

        console.log(`Adding new WebRTC connection! Total number of connections before addition: ${msgIndex}`);
        console.log(connection);

        this.state.hostProperties.connections.push(connection);

        this.state.hostProperties.connections[msgIndex].pc.ondatachannel = event => {
          this.state.hostProperties.connections[msgIndex].messages = event.channel;

          this.state.hostProperties.connections[msgIndex].messages.onopen = event => {
            console.log(event);
          }

          this.state.hostProperties.connections[msgIndex].messages.onmessage = event => {
            console.log(`MESSAGE: ${event.data}`);
          }

          //Links new connection source to camera role and sends msg to src
          const source = this.state.hostProperties.connections[msgIndex];
          console.log(source);
          for(let obj of state.hostProperties.cameras) {
            console.log(obj);
            if(obj.src === null) {
              obj.src = source;
              source.messages.send(`CAM ${obj.camera}`);
              break;
            }
          }
        }

        this.state.hostProperties.connections[msgIndex].pc.ontrack = e => {
          this.state.hostProperties.connections[msgIndex].stream = e.streams[0];
        }
      },
      ADD_ICE_CANDIDATE(state, update) {
        this.state.hostProperties.connections[update.index].pc.addIceCandidate(update.candidate);
      },
      SET_CURRENT_STREAM(state, cam) {
        console.log(cam);
        this.state.hostProperties.currentCam.src.messages.send('OFF');
        this.state.hostProperties.currentCam = cam;
        cam.src.messages.send('ON');
      },
      BEGIN_GAME(state) {
        state.hostProperties.currentCam = state.hostProperties.cameras[0];
        for(let camera of state.hostProperties.cameras) {
          camera.src.messages.send('START');
        }
        state.hostProperties.cameras[0].src.messages.send('ON');
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