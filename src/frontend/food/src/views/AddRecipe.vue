<template>
    <div class="site-container">

    </div>
</template>

<script>
import { defineComponent } from 'vue';
import backendService from '@/services/backendService';

export default defineComponent({
  name: 'AddRecipe',
  data() {
    return {
      ingredients: [
        {
          id: 1,
          name: 'ingredient 1',
          info: 'info about ingredient 1',
          calories_per_gram: 1,
          measure: 'g',

        },
        {
          id: 2,
          name: 'ingredient 2',
          info: 'info about ingredient 2',
          calories_per_gram: 2,
          measure: 'g',
        },
        {
          id: 3,
          name: 'ingredient 3',
          info: 'info about ingredient 3',
          calories_per_gram: 3,
          measure: 'g',
        },
      ],
      name: '',
      text: '',
      ingredient_amount: [],
      image: '',
    };
  },
  components: {
  },
  methods: {
    validate() {
      return this.name.length > 0
                && this.text.length > 0;
    },
    addRecipe() {
      if (!this.validate()) {
        // TODO implement notification system
        alert('input validation failed');
        return;
      }
      backendService.addRecipe(
        this.$store.state.userData.jwt,
        {
          name: this.name,
          text: this.text,
          ingredient_amount: this.ingredient_amount,
          instructions: this.instructions,
          image: this.image,
        },
      ).then((response) => {
        if (response.ok) {
          console.log(response);
          console.log(response.data);
          this.$router.push('/');
        }
        if (response.status === 409) {
          alert('Recipe already exists');
        }
      });
    },
  },
});

</script>
