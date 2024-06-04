<template>
  <div class="site-container">
    <h1>Home</h1>
    <p>Welcome to the Home page</p>
    <br>
    <h2>New Recipes</h2>
    <!-- v-if for so that the v-for loop runs after the data arrived :> -->
    <div v-if="this.daily_recipes.length > 0" class="recipe-bar daily-recipes">
      <router-link
        v-for="recipe in this.daily_recipes" :key="recipe.id"
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
    <h2 v-if="this.$store.state.userData.loggedIn">Your Saved Recipes</h2>
    <!-- v-if for so that the v-for loop runs after the data arrived :> -->
    <div v-if="this.$store.state.userData.loggedIn && this.saved_recipes.length > 0"
      class="recipe-bar own-recipes">
      <router-link v-for="recipe in this.saved_recipes" :key="recipe.id"
      :class="(recipe.user.username === this.$store.state.userData.username) ? 'own-recipe' :
          checkSaved(recipe) ? 'saved-recipe' : ''"
      :to="'/recipe/' + recipe.id + '/'" class="recipe-card">
        <img :src="makeUrl(recipe.imageUrl)" alt="">
        <h2>{{ recipe.name }}</h2>
        <router-link :to="`/user/${recipe.user.username}`">
          By {{ recipe.user.username }} ({{ recipe.user.own_recipes }})</router-link>
      </router-link>
    </div>
    <h2 v-if="this.$store.state.userData.loggedIn">Your own Recipes</h2>
    <!-- v-if for so that the v-for loop runs after the data arrived :> -->
    <div v-if="this.$store.state.userData.loggedIn && this.own_recipes.length > 0"
      class="recipe-bar own-recipes">
      <router-link v-for="recipe in this.own_recipes" :key="recipe.id"
      :class="(recipe.user.username === this.$store.state.userData.username) ? 'own-recipe' :
          checkSaved(recipe) ? 'saved-recipe' : ''"
      :to="'/recipe/' + recipe.id + '/'" class="recipe-card">
        <!-- <img :src="recipe.image" alt=""> -->
        <img :src="makeUrl(recipe.imageUrl)" alt="">
        <h2>{{ recipe.name }}</h2>
      </router-link>
    </div>
</div>
</template>

<script>
import constantService from '@/services/constantService';
import backendService from '@/services/backendService';

// @ is an alias to /src

export default {
  data() {
    return {
      own_recipes: [],
      daily_recipes: [],
      saved_recipes: [],
    };
  },
  mounted() {
    if (this.$store.state.userData.loggedIn) {
      this.OwnRecipes();
      this.SavedRecipes();
    }
    this.DailyRecipes();
  },
  name: 'HomeView',
  components: {
  },
  methods: {
    makeUrl(url) {
      return `${constantService.baseUrl}/app/image/${url}`;
    },
    SavedRecipes() {
      backendService.getSaved(this.$store.state.userData.jwt).then((response) => {
        const data = response.clone().json().catch(() => response.text());
        data.then((d) => {
          this.saved_recipes = d;
        });
      });
    },
    OwnRecipes() {
      backendService.getOwn(this.$store.state.userData.jwt).then((response) => {
        const data = response.clone().json().catch(() => response.text());
        data.then((d) => {
          this.own_recipes = d;
        });
      });
    },
    DailyRecipes() {
      backendService.getDaily().then((response) => {
        const data = response.clone().json().catch(() => response.text());
        data.then((d) => {
          this.daily_recipes = d;
        });
      });
    },
    checkSaved(recipe) {
      if (this.$store.state.userData.loggedIn) {
        return recipe.usersSaved.map((e) => e.username)
          .includes(this.$store.state.userData.username);
      }
      return false;
    },
  },
};
</script>
