<template>
    <div class="site-container">
        <h1>Users</h1>
        <div class="controls">
          <label for="searchString">
            <input @input="getUsers()" v-model="searchString" type="text" id="searchString"
            name="searchString" required placeholder="search">
          </label>
          <div>
            <button
            @click.prevent="this.page=1; this.getUsers()">&lt;&lt;&lt;</button>
            <button
            @click.prevent="if (this.page > 1) this.page--; this.getUsers()">&lt;</button>
            <label for="page">
              <input @input="this.resizeInput('pageInput'); this.getUsers()"
              ref="pageInput" @change="this.getUsers()" id="page" v-model="page" type="text">
            </label>
            <button
            @click.prevent="this.page++; this.getUsers()">&gt;</button>
            <label for="page">
              <input @input="this.resizeInput('pageSizeInput'); this.getUsers()"
              ref="pageSizeInput" @change="this.getUsers()"
              id="page" v-model="pageSize" type="text">
            </label>
          </div>
        </div>
        <div class="users">
            <table v-if="this.users.length > 0">
                <tr
                @click.prevent="this.$router.push(`/user/${user.username}`)"
                v-for="user in this.users" :key="user.id" class="user">
                  {{ user.username }}
                </tr>
            </table>
        </div>
    </div>
</template>

<script>
import backendService from '@/services/backendService';
import { defineComponent } from 'vue';

export default defineComponent({
  name: 'UsersView',
  data() {
    return {
      users: [],
      page: 1,
      pageSize: 20,
      searchString: '',
    };
  },
  mounted() {
    this.resizeInput('pageInput');
    this.resizeInput('pageSizeInput');
    this.pageSize = this.$store.state.userPageSize;
    this.searchString = this.$store.state.userSearchString;
    this.getUsers();
  },
  methods: {
    resizeInput(input) {
      this.$refs[input].style.width = `${this.$refs[input].value.length}ch`;
    },
    getUsers() {
      backendService.getUsers(this.searchString, this.page, this.pageSize).then((response) => {
        const data = response.clone().json().catch(() => response.text());
        data.then((d) => {
          this.users = d;
        });
      });
    },
  },
  beforeUnmount() {
    this.$store.commit('persistUserSearchString', this.searchString);
    this.$store.commit('persistUserPageSize', this.pageSize);
  },
});

</script>

<style lang="scss" scoped>

table {
  width: 100%;
  margin-bottom: 32px;
  tr {
    display: flex;
    justify-content: space-between;
    border-collapse: collapse;
    background-color: #fff;
    padding: 16px;
    font-size: large;
    text-decoration: none;
    * {
      color: black;
      text-decoration: none;
    }
    &:nth-child(odd) {
      background-color: #bbb;
    }
    &:hover {
      background-color: green;
    }
  }
}

</style>
