<template>
    <div class="site-container">
        <h1>Sign Up</h1>
        <form>
            <label for="username">Username
                <input v-model="username" type="username" id="username" name="username" required>
            </label>
            <label for="email">
                Email
                <input v-model="email" type="email" id="email" name="email" required>
            </label>
            <label for="password">Password
                <input v-model="password" type="password" id="password" name="password" required>
            </label>
            <button type="submit"
            @click.prevent="signUp()">Sign Up</button>
        </form>
    </div>
</template>

<script>
import { defineComponent } from 'vue';
import backendService from '@/services/backendService';

export default defineComponent({
  name: 'RegisterView',
  data() {
    return {
      username: '',
      email: '',
      password: '',
    };
  },
  components: {
  },
  methods: {
    signUp() {
      if (!this.validate()) {
        console.log(this.username, this.email, this.password);
        console.log(/^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{8,}$/.test(this.password));
        this.$store.commit('addNotification', {
          message: 'password must contain at least one letter, at least one number, at least one special character f.e [!,@,#,$] and be longer than 8 characters and shorter than 128',
          type: 'error',
        });
        return;
      }
      backendService.signUp(
        this.username,
        this.email,
        this.password,
      ).then((response) => {
        if (response.ok) {
          this.$store.commit('addNotification', {
            message: 'Signed up sucess',
            type: 'success',
          });
          this.login();
        }
        if (response.status === 409) {
          this.$store.commit('addNotification', {
            message: 'Username already exists',
            type: 'error',
          });
        }
      });
    },
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
            this.$store.commit('addNotification', {
              message: 'Singed up and logged in',
              type: 'success',
            });
          });
        }
        if (response.status === 401) {
          this.$store.commit('addNotification', {
            message: 'error 401 Unauthorized after sign up\n this should not happen\n please contact the administrator',
            type: 'error',
          });
        }
      });
    },
    validate() {
      return (this.username.length > 0 && this.email.length > 0 && this.password.length > 0 && /^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\W)(?!.* ).{8,128}$/.test(this.password));
    },
  },
});
</script>

<style lang="scss" scoped>
h1 {
  margin-bottom: 100px;
}
</style>
