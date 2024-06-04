<template>
    <div class="site-container">
      <h1>Search</h1>
      <div class="controls">
        <label for="search">
          <input type="text" id="search" v-model="searchString"
          @input="getRecipes(); this.getRecipes()" placeholder="search">
        </label>
        <div>
          <button
          @click.prevent="this.page=1; this.getRecipes()">&lt;&lt;&lt;</button>
          <button
          @click.prevent="if (this.page > 1) this.page--; this.getRecipes()">&lt;</button>
          <label for="page">
            <input @input="this.resizeInput('pageInput')"
            ref="pageInput" @change="this.getRecipes()" id="page" v-model="page" type="text">
          </label>
          <button
          @click.prevent="this.page++; this.getRecipes()">&gt;</button>
          <label for="page">
            <input @input="this.resizeInput('pageSizeInput'); this.getRecipes()"
            ref="pageSizeInput" @change="this.getRecipes()"
            id="page" v-model="pageSize" type="text">
          </label>
        </div>
      </div>
      <h2>Results for: {{ searchString }}</h2>
      <div v-if="this.recipes.length > 0" class="recipe-bar daily-recipes">
        <router-link
          v-for="recipe in this.recipes" :key="recipe.id"
          :class="(recipe.user.username === this.$store.state.userData.username) ? 'own-recipe' :
          checkSaved(recipe) ? 'saved-recipe' : ''"
        :to="'/recipe/' + recipe.id + '/'" class="recipe-card">
          <!-- <img :src="recipe.image" alt=""> -->
          <img :src="makeUrl(recipe.imageUrl)" alt="">
          <h2>{{ recipe.name }}</h2>
          <router-link :to="`/user/${recipe.user.username}`">
            By {{ recipe.user.username }} ({{ recipe.user.own_recipes }})</router-link>
        </router-link>
      </div>
    </div>
</template>

<script>
import constantService from '@/services/constantService';
import backendService from '@/services/backendService';
import { defineComponent } from 'vue';

export default defineComponent({
  name: 'SearchView',
  data() {
    return {
      searchString: '',
      page: 1,
      pageSize: 25,
      recipes: [],
    };
  },
  mounted() {
    this.resizeInput('pageInput');
    this.resizeInput('pageSizeInput');
    this.searchString = this.$store.state.searchString;
    this.pageSize = this.$store.state.pageSize;
    this.getRecipes();
  },
  methods: {
    makeUrl(url) {
      return `${constantService.baseUrl}/app/image/${url}`;
    },
    checkSaved(recipe) {
      if (this.$store.state.userData.loggedIn) {
        return recipe.usersSaved.map((e) => e.username)
          .includes(this.$store.state.userData.username);
      }
      return false;
    },
    getRecipes() {
      backendService.searchRecipes(this.searchString, this.page, this.pageSize).then((response) => {
        const data = response.clone().json().catch(() => response.text());
        data.then((d) => {
          this.recipes = d;
        });
      });
    },
    resizeInput(input) {
      this.$refs[input].style.width = `${this.$refs[input].value.length}ch`;
    },
    search() {
    },
  },
  beforeUnmount() {
    this.$store.commit('persistSearchString', this.searchString);
    this.$store.commit('persistPageSize', this.pageSize);
  },
});

</script>

<style lang="scss" scoped>

.recipe-bar {
  height: auto;
  overflow-x: hidden;
  flex-wrap: wrap;
}

</style>
