<template>
  <nav>
    <router-link to="/">Home</router-link>
    <router-link to="/createRecipe">Add Recipe</router-link>
    <router-link to="/about">About</router-link>
    <button v-if="this.$store.state.userData.loggedIn" class="split" @click.prevent="logout()">
      Logout
    </button>
    <div class="no-hover split" v-if="this.$store.state.userData.loggedIn">
      <div class="icon-spacer"></div>
      <img class="profile-icon" src="@/assets/profile-user.png" alt="">
      {{ this.$store.state.userData.username}}</div>
    <router-link v-if="!this.$store.state.userData.loggedIn" class="split" to="/register">
      Sign Up
    </router-link>
    <router-link v-if="!this.$store.state.userData.loggedIn" class="split" to="/login">
      Login
    </router-link>
  </nav>
  <div style="height: 64px"></div>
  <div class="page-wrapper">
  <router-view/>
  </div>
  <p>{{ userData }}</p>
  <footer>
    <p>&copy; Moritz Siefke 2024</p>
  </footer>
</template>

<script>
import { defineComponent } from 'vue';

export default defineComponent({
  name: 'App',
  components: {
  },
  methods: {
    logout() {
      console.log('logging out');
      this.$store.commit('logout');
    },
  },
});

</script>

<style lang="scss">

// TODO: for legal use replace this with a font that you have downloaded
@import url('https://fonts.googleapis.com/css2?family=Roboto:ital,wght@0,100;0,300;0,400;0,500;0,700;0,900;1,100;1,300;1,400;1,500;1,700;1,900&display=swap');

* {
  font-family: 'Roboto', sans-serif;
  margin: 0;
  padding: 0;
}

nav {
  background-color: #333;
  overflow: hidden;
  height: fit-content;
  padding: 0 20px;
  position: fixed;
  width: calc(100% - 40px);
  .icon-spacer {
    margin: 0px !important;
    padding: 0px !important;
    width: 30px !important;
    height: 10px !important;
  }
  .profile-icon {
    position: absolute;
    height: 17px;
    width: 17px;
    transform: translate(-170%, 10%);
    filter: invert(1);
  }
  a, button, div {
    color: white;
    text-decoration: none;
    float: left;
    text-align: center;
    padding: 20px 16px;
    font-size: 17px;
    border: none
  }
  div.split, a.split, button.split {
    float: right;
    background-color: green;
    text-align: center;
    text-decoration: none;
  }
  a:hover, button:hover {
    background-color: #ddd;
    color: black;
  }
  div.no-hover {
    background-color: #555;
  }

}
.page-wrapper {
  padding: 20px 20vw 64px 20vw;
}
.site-container {
  background-color: #f4f4f4;
  padding: 20px;
  padding-bottom: 100px;
}
footer {
  background-color: #333;
  color: white;
  text-align: center;
  padding: 20px;
  position: absolute;
  width: calc(100% - 40px);
  bottom: 0;
}
.required {
  color: red;
}
</style>
