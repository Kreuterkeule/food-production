<template>
    <div v-if="this.$store.state.userData.loggedIn" class="site-container">
      <h1>Create Recipe</h1>
      <form>
        <img v-if="this.image !== null" :src="this.image" alt="">
        <div class="ingredient-input">
          <label for="ingredient">
            Ingredient
            <input autocomplete="off"
            @keydown.tab.prevent="tabPressed()"
            ref="ingredientInput" v-model="ingredient" type="text" id="tags" name="tags"
            @keydown.enter.prevent="addIngredient()" @input="ingredientSelected = true"
            @focus="ingredientSelected = true"
            @blur="ingredientSelected = false">
          </label>
          <div ref="ingredientAutocomplete" v-show="ingredientSelected" class="autocomplete">
            <ul class="autocomplete-results">
              <li @mousedown="selectIngredient(ingredient)" :key="ingredient.id"
              v-for="ingredient in filterIngredientsForAutocompletion()"
              class="autocomplete-result">
              {{ ingredient.name }}
            </li>
          </ul>
        </div>
          <label for="amount">
            Amount
            <input
            @focus="amountSelected = true"
            @blur="amountSelected = false"
            @input="amountSelected = true"
            ref="amountInput"
            v-model="amount" type="text" id="amount" name="amount">
          </label>
          <button @click.prevent="addIngredient(true)">Add Ingredient</button>
        </div>
        <table>
          <tr v-for="ingredient in ingredient_amount" :key="ingredient.ingredient">
            <td>{{ ingredient.ingredient }}</td>
            <td>{{ ingredient.amount }}</td>
            <td><button @click.prevent="removeIngredient(ingredient)">x</button></td>
          </tr>
        </table>
        <label for="name">
          Name
          <input v-model="name" type="text" id="name" name="name" required>
          </label>
        <label for="text">
          Instructions
          <textarea v-model="text" type="text" id="text" name="text"></textarea>
        </label>
        <label for="time">
          Cooking duration (min.)
          <input v-model="time" type="number" id="time" name="time">
        </label>
        <label for="image" class="file-input-label">
          Image
          <input ref="file-input" v-on:change="previewFileName()"
          class="file-input" type="file" id="image" name="image"
          accept="image/*">
        </label>
        <!-- eslint-disable-next-line-->
        <label for="tags">Type a new tag and press enter
          <input autocomplete="off"
          ref="tagInput" v-model="tag" type="text" id="tags" name="tags"
          @keydown.enter.prevent="addTag()" @input="tagSelected = true"
          @focus="tagSelected = true;"
          @blur="tagAutocompleteOff()">
        </label>
          <div ref="tagAutocomplete" v-show="tagSelected" class="autocomplete">
            <ul class="autocomplete-results">
              <li tabindex="-1" @mousedown="selectTag(tag)" :key="tag.id"
              v-for="tag in filterTagsForAutocompletion()" class="autocomplete-result">
                {{ tag.name }} <span class="tag-usage">({{ tag.recipes }})</span>
            </li>
          </ul>
        </div>
          <div class="tags">
            <button
            @click.prevent="removeTag(tag)" class="tag" v-for="tag in tags" :key="tag">
              {{ tag }} ({{ getTagUsage(tag) }})
            </button>
          </div>
          <button @click.prevent="addRecipe()">
            {{ (this.$route.params.id) ? 'Update Recipe' : 'Add Recipe' }}
          </button>
      </form>
    </div>
    <div v-else>
      <h1>Log in to create recpies</h1>
    </div>
</template>

<script>
import { defineComponent } from 'vue';
import backendService from '@/services/backendService';
import constantService from '@/services/constantService';

export default defineComponent({
  name: 'AddRecipe',
  data() {
    return {
      time: 0,
      amount: '',
      name: '',
      text: '',
      ingredient_amount: [],
      image_raw: null,
      image: null,
      allTags: [],
      tags: [],
      tag: '',
      tagSelected: false,
      allIngredients: [],
      ingredients: [],
      ingredient: '',
      ingredientSelected: false,
      amountSelected: false,
      recipe: {},
    };
  },
  components: {
  },
  methods: {
    getRecipe() {
      backendService.getRecipe(this.$route.params.id).then((response) => {
        const data = response.clone().json().catch(() => response.text());
        data.then((d) => {
          console.log(d);
          this.name = d.name;
          this.text = d.text;
          this.ingredient_amount = this.deserializeIngredientAmount(d.ingredients);
          this.image = d.imageUrl;
          this.time = d.time;
          this.tags = this.deserializeTags(d.tags);
        });
      });
    },
    deserializeTags(tags) {
      return tags.map((t) => t.name);
    },
    removeIngredient(ingredient) {
      this.ingredient_amount = this.ingredient_amount.filter((i) => i !== ingredient);
    },
    selectIngredient(ingredient) {
      this.ingredient = ingredient.name;
    },
    tagAutocompleteOff() {
      setTimeout(() => {
        this.tagSelected = false;
      }, 100);
    },
    tabPressed() {
      if (this.ingredientSelected) {
        this.$refs.amountInput.focus();
      }
    },
    addIngredient(clicked = false) {
      console.log('adding ingredient');
      if (!(this.ingredientSelected || this.amountSelected || clicked)) { // for enter keyup
        return;
      }
      console.log('amount', this.amount === '');
      console.log('ingredient', this.ingredient === '');
      if (this.ingredient === '' || this.amount === '') {
        this.$store.commit('addNotification', {
          message: 'Ingredient and amount can\'t be empty',
          type: 'error',
        });
        return;
      }
      if (this.ingredient_amount === undefined) this.ingredient_amount = [];
      if (this.ingredient_amount.find((i) => i.ingredient === this.ingredient)) {
        this.$store.commit('addNotification', {
          message: 'Ingredient already added',
          type: 'error',
        });
        return;
      }
      this.ingredient_amount.push({
        ingredient: this.ingredient,
        amount: this.amount,
      });
      this.ingredient = '';
      this.amount = '';
      this.$refs.ingredientInput.focus();
    },
    selectTag(tag) {
      console.log('selecting tag');
      console.log(tag);
      this.tags.push(tag.name);
      this.tag = '';
      this.tags = [...new Set(this.tags)]; // remove duplicates
    },
    filterIngredientsForAutocompletion() {
      return this.allIngredients.filter(
        (ingredient) => ingredient.name.includes(this.ingredient),
      ).sort((a, b) => {
        if (a.recipes > b.recipes) return -1;
        if (a.recipes < b.recipes) return 1;
        return 0;
      });
    },
    filterTagsForAutocompletion() {
      return this.allTags.filter((tag) => tag.name.includes(this.tag)).sort((a, b) => {
        if (a.recipes > b.recipes) return -1;
        if (a.recipes < b.recipes) return 1;
        return 0;
      });
    },
    getTagUsage(tag) {
      const e = this.allTags.find((t) => t.name === tag);
      if (e) return e.recipes ?? 0;
      return 0;
    },
    removeTag(tag) {
      this.tags = this.tags.filter((t) => t !== tag);
    },
    addTag(clicked = false) {
      if (!(this.tagSelected || clicked)) {
        return;
      }
      if (this.tags === undefined) this.tags = [];
      this.tags.push(this.tag);
      this.tag = '';
      this.tags = [...new Set(this.tags)]; // remove duplicates
    },
    clearImageInput() {
      const fileInput = document.querySelector('.file-input');
      fileInput.value = '';
    },
    previewFileName() {
      const fileInput = document.querySelector('.file-input');
      const fileLabel = document.querySelector('.file-input-label');
      const textNode = fileLabel.childNodes[0];
      const file = fileInput.files[0];
      if (!file) {
        textNode.nodeValue = 'Image';
        this.image = null;
        return;
      }
      textNode.nodeValue = file.name;
      this.image_raw = file;
      console.log(this.image_raw instanceof Blob);
      const reader = new FileReader();
      reader.onload = (e) => {
        this.image = e.target.result;
      };
      reader.readAsDataURL(file);
    },
    validate() {
      return this.name.length > 0
                && this.text.length > 0;
    },
    stageClearState() {
      this.name = '';
      this.text = '';
      this.ingredient_amount = [];
      this.image = null;
      this.image_raw = null;
      this.tags = [];
    },
    stagePutRecipe(recipe, recall = false) {
      if (this.$route.params.id && !recall) {
        console.log(recipe);
        const updateRecipe = recipe;
        updateRecipe.id = this.$route.params.id;
        this.stagePutRecipe(updateRecipe, true);
        return;
      }
      backendService.putRecipe(this.$store.state.userData.jwt, recipe)
        .then((recipeResponse) => {
          if (recipeResponse.status !== 201) {
            if (recipeResponse.status === 403) {
              this.$store.commit('addNotification', {
                message: 'This is not your recipe :), sorry, try editing one of your own',
                type: 'error',
              });
              this.$router.push('/');
              return;
            }
            this.$store.commit('addNotification', {
              message: `Failed to put recipe (Error: ${recipeResponse.status}), please contact an Administrator, this should not happen`,
              type: 'error',
            });
            return;
          }
          this.$store.commit('addNotification', {
            message: 'Recipe added',
            type: 'success',
          });
          this.stageClearState();
          this.$router.push('/');
        });
    },
    stagePutImage(tags) {
      if (this.$route.params.id && this.image_raw === null) {
        const recipe = {
          name: this.name,
          text: this.text,
          time: this.time,
          tag_ids: tags.map((t) => t.id),
          ingredient_amount: {},
          imageUrl: this.image,
        };
        this.ingredient_amount.forEach((i) => {
          recipe.ingredient_amount[this.allIngredients
            .find((e) => e.name === i.ingredient).id] = i.amount;
        });
        console.log(recipe);
        this.stagePutRecipe(recipe);
        return;
      }
      backendService.putImage(this.$store.state.userData.jwt, this.image_raw, this.image)
        .then((imageResponse) => {
          if (imageResponse.status !== 201) {
            this.$store.commit('addNotification', {
              message: 'Please choose an image',
              type: 'error',
            });
            return;
          }
          imageResponse.text().then((imageUrl) => {
            const recipe = {
              name: this.name,
              text: this.text,
              time: this.time,
              tag_ids: tags.map((t) => t.id),
              ingredient_amount: {},
              imageUrl: `${constantService.baseUrl}/app/image/${imageUrl}`,
            };
            console.log(this.ingredient_amount);
            this.ingredient_amount.forEach((i) => {
              recipe.ingredient_amount[this.allIngredients
                .find((e) => e.name === i.ingredient).id] = i.amount;
            });
            this.stagePutRecipe(recipe);
          });
        });
    },
    stageGetTags() {
      backendService.getTagsByNames(this.$store.state.userData.jwt, this.tags)
        .then((getTagsResponse) => {
          if (getTagsResponse.status !== 200) {
            this.$store.commit('addNotification', {
              message: `Failed to get tags (Error: ${getTagsResponse.status}), please contact an Administrator, this should not happen`,
              type: 'error',
            });
            return;
          }
          getTagsResponse.clone().json().catch(() => getTagsResponse.text()).then((tags) => {
            this.stagePutImage(tags);
          });
        });
    },
    stagePutIngredients() {
      backendService.putIngredients(this.$store.state.userData.jwt, this.ingredient_amount
        .map((i) => {
          const ingredient = {
            name: i.ingredient,
            info: '',
            calories_per_gram: 0,
          };
          return ingredient;
        })).then((ingredientResponse) => {
        if (ingredientResponse.status !== 201) {
          this.$store.commit('addNotification', {
            message: `Failed to put ingredients (Error: ${ingredientResponse.status}), please contact an Administrator, this should not happen`,
            type: 'error',
          });
          return;
        }
        this.getIngredients();
        this.stageGetTags();
      });
    },
    stagePutTags() {
      backendService.putTags(this.$store.state.userData.jwt, this.tags).then((tagResponse) => {
        if (tagResponse.status !== 201) {
          this.$store.commit('addNotification', {
            message: `Failed to put tags (Error: ${tagResponse.status}), please contact an Administrator, this should not happen`,
            type: 'error',
          });
          return;
        }
        this.stagePutIngredients();
      });
    },
    addRecipe() {
      if (!this.validate()) {
        // TODO implement notification system
        this.$store.commit('addNotification', {
          message: 'Name and Instructions can\'t be empty',
          type: 'error',
        });
        return;
      }
      this.stagePutTags();
    },
    getIngredients() {
      backendService.getIngredients().then((response) => {
        const data = response.clone().json().catch(() => response.text());
        data.then((d) => {
          this.allIngredients = d;
        });
      });
    },
    getTags() {
      backendService.getTags().then((response) => {
        if (response.status !== 200) {
          this.$store.commit('addNotification', {
            message: `Failed to get tags (Error: ${response.status})`,
            type: 'error',
          });
        }
        const data = response.clone().json().catch(() => response.text());
        data.then((d) => {
          this.allTags = d;
        });
      });
    },
    deserializeIngredientAmount(ia) {
      const ingredientAmount = [];
      Object.keys(ia).forEach((key) => {
        ingredientAmount.push({
          ingredient: key.split('|')[1],
          amount: ia[key],
        });
      });
      return ingredientAmount;
    },
  },
  mounted() {
    if (!this.$store.state.userData.loggedIn) {
      this.$store.commit('addNotification', {
        message: 'You need to be logged in to add a recipe',
        type: 'error',
      });
      this.$router.push('/login?from=createRecipe');
      return;
    }
    this.getTags();
    this.getIngredients();
    if (this.$route.params.id) {
      this.getRecipe();
      return;
    }
    console.log('loading state');
    this.name = this.$store.state.addRecipeState.name;
    this.text = this.$store.state.addRecipeState.text;
    this.ingredient_amount = this.$store.state.addRecipeState.ingredient_amount;
    this.image = this.$store.state.addRecipeState.image;
    this.image_raw = new Blob([this.$store.state.addRecipeState.image_raw], { type: 'image/png' });
    this.tags = this.$store.state.addRecipeState.tags;
    console.log(this.image_raw instanceof Blob);
  },
  beforeUnmount() {
    if (this.$route.params.id) {
      return; // editing will not overwrite the state
    }
    if (this.name === '' && this.text === '' && this.ingredient_amount.length === 0
      && this.image === null && this.tags.length === 0) {
      return;
    }
    console.log('persisting state');
    if (this.image_raw === null) {
      this.$store.commit('persistAddRecipeState', {
        name: this.name,
        text: this.text,
        ingredient_amount: this.ingredient_amount,
        image: this.image,
        image_raw: null,
        tags: this.tags,
      });
      return;
    }
    this.image_raw.text().then((imageRaw) => {
      this.$store.commit('persistAddRecipeState', {
        name: this.name,
        text: this.text,
        ingredient_amount: this.ingredient_amount,
        image: this.image,
        image_raw: imageRaw,
        tags: this.tags,
      });
    });
  },
});

</script>

<style lang="scss" scoped>
textarea {
  resize: vertical;
}
.create-ingredient {
  min-height: 40vh;
  min-width: 60vw;
  background-color: #f1f1f1;
  padding: 20px;
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
}

.tags {
  display: flex;
  gap: 0.5rem;
  flex-wrap: wrap;
  button {
    background-color: green;
    color: white;
    padding: 0.5rem;
    border-radius: 0.5rem;
    &:hover {
      background-color: red;
    }
  }
}

.autocomplete {
  position: relative;
  &-results {
    padding: 0;
    margin: 0;
    height: 120px;
    min-height: 1em;
    max-height: 6em;
    overflow-y: scroll;
    overflow-x: hidden;
    background-color: white
  }
  &-result {
    list-style: none;
    text-align: left;
    cursor: pointer;
    position: relative;
    padding: 4px 2px;
    margin: 0;
    background-color: white;
    color: black;
    border: none;
    width: 100%;
    text-align: left;
    border-radius: 0;
    cursor: pointer;
    &:hover {
      background-color: #f0f0f0;
    }
  }
}

.tag-usage {
  color: gray;
}
.tag {
  width: auto;
}

h1 {
  margin-bottom: 100px;
}
</style>
