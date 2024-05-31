import VuexPersistence from 'vuex-persist';
import Vuex from 'vuex';

const vuexLocal = new VuexPersistence({
  key: 'vuex',
  storage: window.localStorage,
  reducer: (state) => state,
});

const store = new Vuex.Store({
  state: {
    asfd: '',
    userData: {
      loggedIn: false,
      username: '',
      jwt: '',
    },
  },
  getters: {
  },
  mutations: {
    login(state, payload) {
      state.userData.loggedIn = true;
      state.userData.username = payload.username;
      state.userData.jwt = payload.jwt;
    },
    logout() {
      this.state.userData.loggedIn = false;
      this.state.userData.username = '';
      this.state.userData.jwt = '';
    },
  },
  actions: {
  },
  modules: {
  },
  plugins: [vuexLocal.plugin],
});

export default store;
