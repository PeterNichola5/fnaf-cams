import { createStore as _createStore } from 'vuex';

export function createStore() {
  let store = _createStore({
    state: {
      userId: '',
      role: ''
    },

    mutations: {
      SET_ID(state, id) {
        this.state.userId = id;
      },
      SET_ROLE(state, userRole) {
        this.state.role = userRole;
      }
    },

  });
  return store;
}