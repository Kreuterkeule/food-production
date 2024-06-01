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
            this.$store.commit('addNotification', {
              message: 'Logged in',
              type: 'success',
            });
            this.$router.push(this.$route.query.from || '/');
          });
        }
        if (response.status === 401) {
          // TODO: implement notification system
          this.$store.commit('addNotification', {
            message: 'Invalid username or password',
            type: 'error',
          });
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
</style>
