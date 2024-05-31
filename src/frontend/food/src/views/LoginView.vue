<template>
    <div class="site-container">
        <h1>Login</h1>
        <form>
            <label for="username">Username
                <input v-model="username" type="username" id="username" name="username" required>
            </label>
            <label for="password">Password
                <input v-model="password" type="password" id="password" name="password" required>
            </label>
            <button type="submit" @click.prevent="this.login()">Login</button>
        </form>
    </div>
</template>

<script>
import { defineComponent } from 'vue';
import backendService from '@/services/backendService';

export default defineComponent({
  name: 'LoginView',
  components: {
  },
  methods: {
    login() {
      backendService.getJwt(
        this.username,
        this.password,
      ).then((response) => {
        if (response.ok) {
          response.clone().json().catch(() => response.text()).then((data) => {
            this.$store.commit('login', { username: this.username, jwt: data });
            console.log(data);
            this.$router.push('/');
          });
        }
        if (response.status === 401) {
          // TODO: implement notification system
          alert('Invalid username or password');
        }
      });
    },

  },
});
</script>

<style lang="scss" scoped>
h1 {
  margin-bottom: 100px;
}
form {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  max-width: 300px;
  margin: 0 auto;
  label {
    display: flex;
    flex-direction: column;
    gap: 0.5rem;
    input {
      padding: 0.5rem;
      font-size: 1rem;
      border-radius: 0.25rem;
      border: none;
      &:focus {
        box-shadow: 0 1px 1px 2px green;
      }
    }
  }
  button {
    padding: 0.5rem;
    font-size: 1rem;
    border-radius: 0.25rem;
    border: none;
    background-color: green;
    color: white;
    cursor: pointer;
    &:hover {
      background-color: green;
    }
  }
}
</style>
