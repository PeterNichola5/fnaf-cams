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
      SET_REMOTE_LOCATION(state, obj) {
        console.log(obj.connection);
        console.log(obj.desc);
        obj.connection.setRemoteDescription(obj.desc);
      }
    },

  });
  return store;
}