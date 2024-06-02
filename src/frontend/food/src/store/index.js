/* eslint-disable */
import VuexPersistence from 'vuex-persist';
import Vuex from 'vuex';

const vuexLocal = new VuexPersistence({
  key: 'vuex',
  storage: window.localStorage,
  reducer: (state) => state,
});

const store = new Vuex.Store({
  state: {
    notifications: [

    ],
    userData: {
      loggedIn: false,
      username: '',
      jwt: '',
    },
    addRecipeState: {},
    pageSize: 10,
    searchString: '',
    userSearchString: '',
    userPageSize: 10,
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
    addNotification(state, payload) { // payload is the notification object ({ message, type })
      const notification = payload;
      const genId = () => {
        let id = Math.random().toString(36).substr(2, 9);
        if (state.notifications.find((e) => e.id === id)) {
          id = genId();
        }
        return id;
      };
      notification.id = genId();
      notification.time = 100;
      state.notifications.push(notification);
    },
    removeNotification(state, payload) {
      state.notifications = state.notifications
        .filter((notification) => notification.id !== payload);
    },
    removeAllNotifications(state) {
      state.notifications = [];
    },
    updateNotification(state, payload) {
      const notification = state.notifications
        .find((e) => e.id === payload.id);
      notification.time = payload.time;
      notification.message = payload.message;
      notification.type = payload.type;
      state.notifications = state.notifications
        .map((e) => (e.id === payload.id ? notification : e));
    },
    persistAddRecipeState(state, payload) {
      state.addRecipeState = payload;
    },
    clearAddRecipeState(state) {
      state.addRecipeState = {}
    },
    persistPageSize(state, payload) {
      this.state.pageSize = payload;
    },
    persistSearchString(state, payload) {
      this.state.searchString = payload;
    },
    persistUserPageSize(state, payload) {
      this.state.userPageSize = payload;
    },
    persistUserSearchString(state, payload) {
      this.state.userSearchString = payload;
    },
  },
  actions: {
  },
  modules: {
  },
  plugins: [vuexLocal.plugin],
});

export default store;
