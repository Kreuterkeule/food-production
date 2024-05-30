import createPersistedState from 'vuex-persistedstate';
import { createStore } from 'vuex';
import backendService from '../services/backendService';

export default createStore({
  state: {
    userData: {
      loggedIn: false,
      username: '',
      jwt: '',
    },
  },
  getters: {
  },
  mutations: {
    async signUp(state, payload) {
      await backendService.signUp(
        payload.username,
        payload.email,
        payload.password,
      ).then((response) => {
        if (response.ok) {
          console.log(response);
          console.log(response.data);
          this.commit('login', { username: payload.username, password: payload.password });
        }
        if (response.status === 409) {
          // TODO: implement notification system
          alert('Username already exists');
        }
      });
    },
    async login(state, payload) {
      await backendService.getJwt(
        payload.username,
        payload.password,
      ).then((response) => {
        if (response.ok) {
          console.log(response);
          console.log(response.data);
          response.clone().json().catch(() => response.text()).then((data) => {
            state.userData.jwt = data;
            console.log(data);
          });
          state.userData.loggedIn = true;
          state.userData.username = payload.username;
        }
        if (response.status === 401) {
          // TODO: implement notification system
          alert('Invalid username or password');
        }
      });
    },
    logout(state) {
      state.userData.loggedIn = false;
      state.userData.username = '';
      state.userData.jwt = '';
    },
  },
  actions: {
  },
  modules: {
  },
  plugins: [createPersistedState()],
});
