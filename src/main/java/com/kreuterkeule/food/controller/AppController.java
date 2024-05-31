package com.kreuterkeule.food.controller;


import com.kreuterkeule.food.dto.CreateIngredientDto;
import com.kreuterkeule.food.dto.CreateRecipeDto;
import com.kreuterkeule.food.entity.Ingredient;
import com.kreuterkeule.food.entity.Recipe;
import com.kreuterkeule.food.entity.UserEntity;
import com.kreuterkeule.food.repository.IngredientRepository;
import com.kreuterkeule.food.repository.RecipeRepository;
import com.kreuterkeule.food.repository.UserRepository;
import com.kreuterkeule.food.service.ImageService;
import com.kreuterkeule.food.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/app")
public class AppController {

    private UserRepository userRepository;
    private IngredientRepository ingredientRepository;
    private RecipeRepository recipeRepository;
    private UserDetailsManager users;
    private UserService userService;
    private ImageService imageService;

    @Autowired
    public AppController(UserRepository userRepository, IngredientRepository ingredientRepository, RecipeRepository recipeRepository, UserDetailsManager userDetailsManager, UserService userService, ImageService imageService) {
        this.userRepository = userRepository;
        this.ingredientRepository = ingredientRepository;
        this.recipeRepository = recipeRepository;
        this.users = userDetailsManager;
        this.userService = userService;
        this.imageService = imageService;
    }

    @GetMapping("own")
    public ResponseEntity<List<Recipe>> get_own() {
        UserEntity user = userService.getAuthenticatedUserEntity();
        if (user == null) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(user.getOwn_recipes(), HttpStatus.OK);
    }
    @GetMapping("saved")
    public ResponseEntity<List<Recipe>> get_saved() {
        UserEntity user = userService.getAuthenticatedUserEntity();
        if (user == null) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(user.getSaved_recipes(), HttpStatus.OK);
    }
    @GetMapping("daily")
    public ResponseEntity<List<Recipe>> get_daily() {
        Page<Recipe> page = recipeRepository.findAll(
                PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "created_date"))
        );
        List<Recipe> recipes = page.stream().toList();
        return new ResponseEntity<>(recipes, HttpStatus.OK);
    }
    @PutMapping("recipe")
    public ResponseEntity<Recipe> create_recipe(@RequestBody CreateRecipeDto recipeDto) {
        UserEntity user = userService.getAuthenticatedUserEntity();
        if (user == null) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
        Map<Ingredient, Integer> ingredients = new HashMap<>();
        for (Map.Entry<String, Integer> e : recipeDto.ingredient_amount.entrySet()) {
            ingredients.put(ingredientRepository.findById(Long.valueOf(e.getKey())).orElse(null), e.getValue());
        }
        Recipe recipe = new Recipe(ingredients, recipeDto.name, recipeDto.time, recipeDto.text, user);
        return new ResponseEntity<>(recipeRepository.save(recipe), HttpStatus.CREATED);
    }
    @PutMapping("ingredient")
    public ResponseEntity<Ingredient> create_ingredient(@RequestBody CreateIngredientDto ingredientDto) {
        Ingredient ingredient = new Ingredient(ingredientDto.name, ingredientDto.info, ingredientDto.calories_per_gram);
        return new ResponseEntity<>(ingredientRepository.save(ingredient), HttpStatus.CREATED);
    }

    @PutMapping("image")
    public ResponseEntity<String> upload_image(@RequestParam("image")MultipartFile file) throws Exception {
        String uploadDirectory = "src/main/resources/static/images/recipe";
        String imageString = imageService.saveImageToStorage(uploadDirectory, file);
        return new ResponseEntity<>(imageString, HttpStatus.OK);
    }

    @GetMapping("image/{imageName}")
    public ResponseEntity<byte[]> get_image(@PathVariable String imageName) throws Exception {
        String imageDirectory = "src/main/resources/static/images/recipe";

        byte[] image = imageService.getImage(imageDirectory, imageName);
        return new ResponseEntity<>(image, HttpStatus.OK);
    }

    @DeleteMapping("ingredient")
    public ResponseEntity<Ingredient> delete_ingredient(@RequestParam("id") Long id) {
        Ingredient ingredient = ingredientRepository.findById(id).orElse(null);
        if (ingredient == null) return new ResponseEntity<>(null, HttpStatus.FAILED_DEPENDENCY);
        try {
            ingredientRepository.delete(ingredient);
        } catch (Exception e) {
            // Ingredient probably still in use by some recipe
            return new ResponseEntity<>(null, HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(ingredient, HttpStatus.OK);
    }

    @DeleteMapping("recipe")
    public ResponseEntity<Recipe> delete_recipe(@RequestParam("id") Long id) {
        Recipe recipe = recipeRepository.findById(id).orElse(null);
        if (recipe == null) return new ResponseEntity<>(null, HttpStatus.FAILED_DEPENDENCY);
        try {
            recipeRepository.delete(recipe);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(recipe, HttpStatus.OK);

    }
    @GetMapping("ingredients/all")
    public ResponseEntity<List<Ingredient>> getIngredientsAll() {
        List<Ingredient> ingredients = ingredientRepository.findAll();
        return new ResponseEntity<>(ingredients, HttpStatus.OK);
    }

    @GetMapping("recipe/all")
    public ResponseEntity<List<Recipe>> getRecipesAll() {
        List<Recipe> recipes = recipeRepository.findAll();
        return new ResponseEntity<>(recipes, HttpStatus.OK);
    }
}
