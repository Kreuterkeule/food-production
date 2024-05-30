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
            <button type="submit" @click.prevent="signUp()">Sign Up</button>
        </form>
    </div>
</template>

<script>
import { defineComponent } from 'vue';

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
        // TODO implement notification system
        /*
        password must contain at least one letter, at least one number,
        and be longer than 8 characters and shorter than 128
        */
        alert('input validation failed');
        return;
      }
      this.$store.commit('signUp', { username: this.username, email: this.email, password: this.password });
      this.$router.push('/');
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
