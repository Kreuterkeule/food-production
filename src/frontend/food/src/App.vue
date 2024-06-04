<template>
  <nav ref="nav">
    <router-link to="/">Home</router-link>
    <router-link to="/createRecipe">Add Recipe</router-link>
    <router-link to="/all">All Recipes</router-link>
    <router-link to="/users">Users</router-link>
    <router-link to="/search">Search</router-link>
    <button v-if="this.$store.state.userData.loggedIn" class="split" @click.prevent="logout()">
      Logout
    </button>
    <router-link :to="`/user/${this.$store.state.userData.username}`"
    class="no-hover split profile-button" v-if="this.$store.state.userData.loggedIn">
      <div class="icon-spacer"></div>
      <img class="profile-icon" src="@/assets/profile-user.png" alt="">
      {{ this.$store.state.userData.username}}
    </router-link>
    <router-link v-if="this.$store.state.userData.loggedIn" to="/saved">Saved Recipes</router-link>
    <router-link v-if="!this.$store.state.userData.loggedIn" class="split" to="/register">
      Sign Up
    </router-link>
    <router-link v-if="!this.$store.state.userData.loggedIn" class="split" to="/login">
      Login
    </router-link>
  </nav>
  <div :style="{height: PageWrapperHeight}"></div>
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
  data() {
    return {
      PageWrapperHeight: '64px',
    };
  },
  components: {
    NotificationSystem,
  },
  methods: {
    computePageWrapperPadding() {
      this.PageWrapperHeight = this.$refs.nav ? `${this.$refs.nav.offsetHeight}px !important` : '64px';
    },
    logout() {
      this.$store.commit('logout');
      this.$store.commit('addNotification', {
        message: 'Logged out',
        type: 'success',
      });
    },
  },
  mounted() {
    this.computePageWrapperPadding();
    window.addEventListener('resize', this.computePageWrapperPadding);
  },
  beforeUnmount() {
    window.removeEventListener('resize', this.computePageWrapperPadding);
  },
});

</script>

<style lang="scss">

// TODO: for legal use replace this with a font that you have downloaded
/* roboto-regular - latin */
@font-face {
  font-display: swap;
  font-family: 'Roboto';
  font-style: normal;
  font-weight: 400;
  src: url('@/fonts/roboto-v30-latin-regular.woff2') format('woff2');
}

/* roboto-italic - latin */
@font-face {
  font-display: swap;
  font-family: 'Roboto';
  font-style: italic;
  font-weight: 400;
  src: url('@/fonts/roboto-v30-latin-italic.woff2') format('woff2');
}

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
  a.no-hover {
    background-color: #555 !important;
    &:hover {
      background-color: #ddd !important;
      img {
        filter: invert(0);
      }
    }
  }

}
.page-wrapper {
  min-height: 100vh;
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

.recipe-bar {
  padding: 10px;
  display: flex;
  gap: 1rem;
  flex-wrap: nowrap;
  overflow-x: scroll;
  justify-content: left;
  font-size: 12px;
  height: 300px;;
  a.recipe-card {
    width: 200px;
    &.own-recipe {
      background-color: #fdefae;
    }
    &.saved-recipe {
      background-color: #aefdef;
    }
    padding: 10px;
    &:hover {
      background-color: #aaaaaa;
    }
    display: flex;
    flex-direction: column;
    gap: 0.5rem;
    justify-content: center;
    color: black;
    text-decoration: none;
    img {
      width: 200px;
      height: 200px;
      object-fit: cover;
      object-fit: cover;
    }
    h2 {
      text-align: center;
      margin: 0;
      overflow: hidden;
      height: 50px;
    }
  }
  a {
    color: black;
    &:hover {
      text-decoration: none;
    }
  }
}

h2 {
  margin-top: 24px;
}
.controls {
  display: flex;
  justify-content: center;
  margin: 40px 0;
  flex-wrap: wrap;
  gap: 10px;
  div {
    display: flex;
    flex-wrap: nowrap;
  }
  & div, & {
    button {
      margin: 0 2px;
      color: white;
      background-color: green;
      border: none;
      padding: 5px;
      cursor: pointer;
      border-radius: 5px;
    }
    label {
      display: flex;
      align-items: center;
      width: fit-content;
      min-width: auto;
    }
    input {
      margin: 0 10px;
      min-width: auto;
      padding: 5px 10px;
      border: 1px solid green;
      border-radius: 5px;
    }
  }
}

</style>
