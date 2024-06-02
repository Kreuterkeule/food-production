<template>
    <div class="site-container">
        <div class="recipe-container">
          <h1>{{ recipe.name }}</h1>
          <img :src="recipe.imageUrl" alt="">
          <h2>Ingredients</h2>
          <table v-if="this.recipe !== 0">
            <tr @mousedown="this.$router.push('/ingredient/' + ia.ingredient.name)"
             v-for="ia in prepareIngredients(recipe.ingredients)" :key="ia.ingredient">
              <td>{{ ia.ingredient.name }}</td>
              <td>{{ ia.amount }}</td>
            </tr>
          </table>
          <h2>Insturctions</h2>
          <div class="instructions">
            <p><span v-html="prepareText(recipe.text)"></span></p>
          </div>
        </div>
        <div class="tags">
          <router-link v-for="tag in recipe.tags" :key="tag.id" :to="'/tag/' + tag.name + '/'">
            {{ tag.name }} ({{ tag.recipes }})
          </router-link>
        </div>
        <div class="recipe-menu">
          <button class="not-authenticated" @click.prevent="copyLink()">
            Share
          </button>
          <button v-if="!checkSaved && !checkOwn" class="not-authenticated" @click.prevent="save()">
            Save
          </button>
          <button v-if="checkSaved" class="not-authenticated" @click.prevent="unsave()">
            Unsave
          </button>
          <router-link class="edit-button"
          v-if="checkOwn"
          :to="`/editRecipe/${recipe.id}`">Edit</router-link>
          <button v-if="checkOwn" class="delete-btn" @click.prevent="deleteRecipe()">Delete</button>
        </div>
      </div>
</template>

<script>
import { defineComponent } from 'vue';
import backendService from '@/services/backendService';

export default defineComponent({
  name: 'RecipeView',
  data() {
    return {
      recipe: {
        ingredients: {},
      },
      recipeComplete: false,
    };
  },
  mounted() {
    this.getRecipe();
  },
  methods: {
    unsave() {
      backendService.unsaveFromUser(this.$store.state.userData.jwt, this.recipe.id)
        .then((response) => {
          if (response.ok) {
            this.$store.commit('addNotification', {
              message: 'Recipe unsaved',
              type: 'success',
            });
            this.$router.go();
          } else if (response.status === 403) {
            this.$store.commit('addNotification', {
              message: 'You are not authorized to unsave this recipe',
              type: 'error',
            });
          } else if (response.status === 404) {
            this.$store.commit('addNotification', {
              message: 'Recipe not found',
              type: 'error',
            });
          } else {
            this.$store.commit('addNotification', {
              message: `Failed to unsave recipe (${response.status})`,
              type: 'error',
            });
          }
        });
    },
    save() {
      backendService.saveToUser(this.$store.state.userData.jwt, this.recipe.id)
        .then((response) => {
          if (response.ok) {
            this.$store.commit('addNotification', {
              message: 'Recipe saved',
              type: 'success',
            });
            this.$router.go();
          } else if (response.status === 403) {
            this.$store.commit('addNotification', {
              message: 'You are not authorized to save this recipe',
              type: 'error',
            });
          } else if (response.status === 404) {
            this.$store.commit('addNotification', {
              message: 'Recipe not found',
              type: 'error',
            });
          } else {
            this.$store.commit('addNotification', {
              message: `Failed to save recipe (${response.status})`,
              type: 'error',
            });
          }
        });
    },
    deleteRecipe() {
      backendService.deleteRecipe(
        this.$store.state.userData.jwt,
        this.recipe.id,
      ).then((response) => {
        if (response.ok) {
          this.$store.commit('addNotification', {
            message: 'Recipe deleted',
            type: 'success',
          });
          this.$router.push('/');
        } else if (response.status === 403) {
          this.$store.commit('addNotification', {
            message: 'You are not authorized to delete this recipe, try deleteing one of your own recipes',
            type: 'error',
          });
        } else if (response.status === 404) {
          this.$store.commit('addNotification', {
            message: 'Recipe not found',
            type: 'error',
          });
        } else {
          this.$store.commit('addNotification', {
            message: `Failed to delete recipe (${response.status})`,
            type: 'error',
          });
        }
      });
    },
    copyLink() {
      const link = (
        window.location.href + (window.location.href.endsWith('/') ? this.recipe.name : ''
        )).replaceAll(' ', '-');
      navigator.clipboard.writeText(link);
      this.$store.commit('addNotification', {
        message: `Link copied to clipboard (${link})`,
        type: 'success',
      });
    },
    prepareIngredients(ingredients) {
      const ingredientsFinal = [];
      Object.keys(ingredients).forEach((key) => {
        const keyArray = key.split('|');
        ingredientsFinal.push(
          {
            ingredient: {
              id: keyArray[0],
              name: keyArray[1],
              calories_per_gram: keyArray[2],
            },
            amount: ingredients[key],
          },
        );
      });
      return ingredientsFinal;
    },
    prepareText(text) {
      if (text === undefined) {
        return '';
      }
      return text.split('\n').join('<br>');
    },
    getRecipe() {
      const { id } = this.$route.params;
      backendService.getRecipe(id).then((response) => {
        if (!response.ok) {
          this.$store.commit('addNotification', {
            message: 'This recipe does not exist',
            type: 'error',
          });
          this.$router.push('/');
          return;
        }
        const data = response.clone().json().catch(() => response.text());
        data.then((d) => {
          this.recipe = d;
          this.recipeComplete = true;
        });
      });
    },
  },
  computed: {
    checkOwn() {
      if (this.$store.state.userData.loggedIn
        && this.recipeComplete) {
        return this.$store.state.userData.username === this.recipe.user.username;
      }
      return false;
    },
    checkSaved() {
      if (this.$store.state.userData.loggedIn
        && this.recipeComplete) {
        return this.recipe.usersSaved.map((e) => e.username)
          .includes(this.$store.state.userData.username);
      }
      return false;
    },
  },
});

</script>

<style lang="scss" scoped>
.site-container {
  display: flex;
  flex-direction: column;
  align-items: center;
}
.recipe-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  margin-top: 20px;
  width: 100%;
  height: 100%;
  max-width: 600px;
  h1 {
    margin-bottom:  64px;
  }
  h2 {
    margin: 16px;
  }
  img {
    width: 100%;
    height: auto;
    margin-bottom: 32px;
  }
  table {
    width: 100%;
    margin-bottom: 32px;
    tr {
      display: flex;
      justify-content: space-between;
      border-collapse: collapse;
      &:nth-child(odd) {
        td {
          background-color: #bbb;
        }
      }
      td {
        background-color: #fff;
        width: 50%;
        padding: 8px;
        border-right: 1px solid black;
        &:last-child {
          border-right: none;
        }
      }
      &:hover {
        td {
          background-color: green;
        }
      }
    }
  }
}
.instructions {
  width: 100%;
  p {
    padding: 16px;
    background-color: #fff;
  }
}

.recipe-menu
{
  position: fixed;
  bottom: 8vh;
  left: 8vw;
  display: flex;
  flex-direction: column;
  z-index: 100;
  a, button {
    margin: 4px 0;
    padding: 8px 16px;
    font-size: 1rem;
    border-radius: 100px;
    border: none;
    background-color: green;
    color: white;
    width: fit-content;
    text-decoration: none;
    cursor: pointer;
    &.delete-btn {
      background-color: #ff0000;
      &:hover {
        background-color: #f56565;
      }
    }
    &.not-authenticated {
      background-color: #00c3ff;
      &:hover {
        background-color: #8be4ff;
      }
    }
    &:hover {
      background-color: lightgreen;
    }
  }
}

.tags {
  display: flex;
  gap: 0.5rem;
  flex-wrap: wrap;
  margin-top: 20px;
  a {
    text-decoration: none;
    background-color: green;
    color: white;
    padding: 0.5rem;
    border-radius: 0.5rem;
    &:hover {
      opacity: .8;
      text-decoration: underline;
    }
  }
}
</style>
