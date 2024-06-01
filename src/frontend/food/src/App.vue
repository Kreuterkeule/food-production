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
  <NotificationSystem></NotificationSystem>
  <footer>
    <p>&copy; Moritz Siefke 2024</p>
  </footer>
</template>

<script>
import { defineComponent } from 'vue';
import NotificationSystem from './components/NotificationSystem.vue';

export default defineComponent({
  name: 'App',
  components: {
    NotificationSystem,
  },
  methods: {
    logout() {
      console.log('logging out');
      this.$store.commit('logout');
      this.$store.commit('addNotification', {
        message: 'Logged out',
        type: 'success',
      });
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
  width: calc(100% - 40px);
  padding: 20px;
  display: flex;
  justify-content: center;
}
.site-container {
  max-width: 1200px;
  width: calc(100% - 40px);
  background-color: #f4f4f4;
  padding: 20px;
  padding-bottom: 100px;
}
footer {
  background-color: #333;
  color: white;
  text-align: center;
  padding: 20px;
  position: fixed;
  width: calc(100% - 40px);
  bottom: 0;
}
.required {
  color: red;
}

form {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  max-width: 500px;
  margin: 0 auto;
  label {
    display: flex;
    flex-direction: column;
    gap: 0.5rem;
    input ,textarea {
      padding: 0.5rem;
      font-size: 1rem;
      border-radius: 0.25rem;
      border: none;
      &:focus {
        outline: none;
        box-shadow: 0 0 0 2px green;
      }
    }
  }
  button {
    margin: 8px 0;
    padding: 0.5rem;
    font-size: 1rem;
    border-radius: 0.25rem;
    border: none;
    background-color: green;
    color: white;
    width: 100%;
    cursor: pointer;
    &:hover {
      background-color: green;
    }
  }
  .file-input {
    display: none;
    &-label {
      display: block;
      padding: 0.5rem;
      font-size: 1rem;
      border-radius: 0.25rem;
      border: none;
      background-color: green;
      color: white;
      cursor: pointer;
      &:hover {
        opacity: .8;
      }
    }
  }
  table {
    width: 100%;
    border-collapse: collapse;
    th, td {
      border-right: 1px solid black;
      padding: 0.5rem;
      overflow-x: scroll;
      &:last-child {
        border-right: none;
      }
    }
  }
}
</style>
