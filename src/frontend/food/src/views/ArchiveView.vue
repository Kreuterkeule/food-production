<template>
    <div class="site-container">
        <h1 v-if="this.$route.name !== 'all'">
          {{ capitalize(this.$route.name) + ' ' + capitalize(this.$route.params.name) }}</h1>
        <h1 v-else>All Recipes</h1>
        <div class="controls">
          <div>
            <button @touchend="this.page=1; this.getRecipes()"
            @click.prevent="this.page=1; this.getRecipes()">&lt;&lt;&lt;</button>
            <button @touchend="if (this.page > 1) this.page--; this.getRecipes()"
            @click.prevent="if (this.page > 1) this.page--; this.getRecipes()">&lt;</button>
            <label for="page">
              <input @input="this.resizeInput('pageInput')"
              ref="pageInput" @change="this.getRecipes()" id="page" v-model="page" type="text">
            </label>
            <button @touchend="this.page++; this.getRecipes()"
            @click.prevent="this.page++; this.getRecipes()">&gt;</button>
            <label for="page">
              <input @input="this.resizeInput('pageSizeInput')"
              ref="pageSizeInput" @change="this.getRecipes()"
              id="page" v-model="pageSize" type="text">
            </label>
          </div>
        </div>
        <div v-if="this.recipes.length > 0" class="recipe-bar daily-recipes">
          <router-link
            v-for="recipe in this.recipes" :key="recipe.id"
            :class="(recipe.user.username === this.$store.state.userData.username) ? 'own-recipe' :
            checkSaved(recipe) ? 'saved-recipe' : ''"
          :to="'/recipe/' + recipe.id + '/'" class="recipe-card">
            <!-- <img :src="recipe.image" alt=""> -->
            <img :src="recipe.imageUrl" alt="">
            <h2>{{ recipe.name }}</h2>
            <router-link :to="`/user/${recipe.user.username}`">
              By {{ recipe.user.username }} ({{ recipe.user.own_recipes }})</router-link>
          </router-link>
        </div>
    </div>
</template>

<script>
import backendService from '@/services/backendService';
import { defineComponent } from 'vue';

export default defineComponent({
  name: 'ArchiveView',
  data() {
    return {
      recipes: [],
      pageSize: 25,
      page: 1,
    };
  },
  mounted() {
    this.getRecipes();
    this.resizeInput('pageInput');
    this.resizeInput('pageSizeInput');
    this.pageSize = this.$store.state.pageSize;
  },
  methods: {
    checkSaved(recipe) {
      if (this.$store.state.userData.loggedIn) {
        return recipe.usersSaved.map((e) => e.username)
          .includes(this.$store.state.userData.username);
      }
      return false;
    },
    capitalize(string) {
      if (string === undefined) return '';
      return string.charAt(0).toUpperCase() + string.slice(1);
    },
    resizeInput(input) {
      this.$refs[input].style.width = `${this.$refs[input].value.length}ch`;
    },
    getRecipes() {
      if (this.$route.name === 'tag') {
        this.getWithTags();
      } else if (this.$route.name === 'ingredient') {
        this.getWithIngredients();
      } else if (this.$route.name === 'user') {
        this.getWithUser();
      } else if (this.$route.name === 'saved') {
        this.getSaved();
      } else {
        this.getAll();
      }
    },
    getSaved() {
      backendService.getSaved(this.$store.state.userData.jwt, this.page, this.pageSize)
        .then((response) => {
          const data = response.clone().json().catch(() => response.text());
          data.then((d) => {
            this.recipes = d;
          });
        });
    },
    getWithUser() {
      backendService.getRecipesByUser(this.$route.params.name, this.page, this.pageSize)
        .then((response) => {
          if (response.ok) {
            const data = response.clone().json().catch(() => response.text());
            data.then((d) => {
              this.recipes = d;
            });
          }
        });
    },
    getWithIngredients() {
      backendService
        .getRecipesByIngredients([this.$route.params.name], this.page, this.pageSize)
        .then((response) => {
          if (response.ok) {
            const data = response.clone().json().catch(() => response.text());
            data.then((d) => {
              this.recipes = d;
            });
          }
        });
    },
    getWithTags() {
      backendService.getRecipesByTags([this.$route.params.name], this.page, this.pageSize)
        .then((response) => {
          if (response.ok) {
            const data = response.clone().json().catch(() => response.text());
            data.then((d) => {
              this.recipes = d;
            });
          }
        });
    },
    getAll() {
      backendService.getAllRecipes(this.page, this.pageSize)
        .then((response) => {
          if (response.ok) {
            const data = response.clone().json().catch(() => response.text());
            data.then((d) => {
              this.recipes = d;
            });
          }
        });
    },
  },
  watch: {
    $route() {
      this.$store.commit('persistPageSize', this.pageSize);
      this.getRecipes();
    },
  },
  beforeUnmount() {
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
