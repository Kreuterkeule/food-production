<template>
  <div class="site-container">
    <h1>Home</h1>
    <p>Welcome to the Home page</p>
    <br>
    <h2>Your own Recipes</h2>
    <div class="recipe-bar own-recipes">
      <router-link v-for="recipe in this.own_recipes" :key="recipe.id"
      :to="'/recipe/' + recipe.id" class="recipe-card">
        <!-- <img :src="recipe.image" alt=""> -->
        <img src="https://cataas.com/cat" alt="">
        <h2>{{ recipe.name }}</h2>
        <p>By Author Name</p>
        <!-- TODO: maybe link to author profile -->
      </router-link>
    </div>
</div>
</template>

<script>
import backendService from '@/services/backendService';

// @ is an alias to /src

export default {
  data() {
    return {
      own_recipes: [],
    };
  },
  mounted() {
    this.OwnRecipes();
  },
  name: 'HomeView',
  components: {
  },
  methods: {
    async OwnRecipes() {
      console.log(this.$store.state.userData.jwt);
      await backendService.getOwn(this.$store.state.userData.jwt).then((response) => {
        const data = response.clone().json().catch(() => response.text());
        data.then((d) => {
          this.own_recipes = d;
          console.log(d);
        });
      });
    },
  },
};
</script>

<style lang="scss" scoped>
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
      text-align: center
    }
  }
}
</style>
