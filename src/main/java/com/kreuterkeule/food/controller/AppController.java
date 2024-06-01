package com.kreuterkeule.food.controller;


import com.kreuterkeule.food.dto.*;
import com.kreuterkeule.food.entity.Ingredient;
import com.kreuterkeule.food.entity.Recipe;
import com.kreuterkeule.food.entity.Tag;
import com.kreuterkeule.food.entity.UserEntity;
import com.kreuterkeule.food.repository.IngredientRepository;
import com.kreuterkeule.food.repository.RecipeRepository;
import com.kreuterkeule.food.repository.TagRepository;
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
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/app")
public class AppController {

    private UserRepository userRepository;
    private IngredientRepository ingredientRepository;
    private RecipeRepository recipeRepository;
    private UserDetailsManager users;
    private UserService userService;
    private ImageService imageService;
    private TagRepository tagRepository;

    @Autowired
    public AppController(UserRepository userRepository, IngredientRepository ingredientRepository, RecipeRepository recipeRepository, UserDetailsManager userDetailsManager, UserService userService, ImageService imageService, TagRepository tagRepository) {
        this.userRepository = userRepository;
        this.ingredientRepository = ingredientRepository;
        this.recipeRepository = recipeRepository;
        this.users = userDetailsManager;
        this.userService = userService;
        this.imageService = imageService;
        this.tagRepository = tagRepository;
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
                PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "updatedDate"))
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
        Map<Ingredient, String> ingredients = new HashMap<>();
        for (Map.Entry<String, String> e : recipeDto.ingredient_amount.entrySet()) {
            ingredients.put(ingredientRepository.findById(Long.valueOf(e.getKey())).orElse(null), e.getValue());
        }
        List<Tag> tags = new ArrayList<>();
        List<Tag> finalTags = tags;
        recipeDto.tag_ids.forEach(e -> {
            finalTags.add(tagRepository.findById(e.longValue()).orElse(null));
        });
        tags = finalTags;
        tags = tags.stream().filter(e -> e != null).collect(Collectors.toList());
        Recipe recipe = new Recipe(tags, ingredients, recipeDto.name, recipeDto.time, recipeDto.text, user, recipeDto.getImageUrl());
        return new ResponseEntity<>(recipeRepository.save(recipe), HttpStatus.CREATED);
    }
    @PutMapping("ingredient")
    public ResponseEntity<Ingredient> create_ingredient(@RequestBody CreateIngredientDto ingredientDto) {
        if (!ingredientRepository.findByName(ingredientDto.name).isEmpty()) return new ResponseEntity<>(null, HttpStatus.CONFLICT);
        Ingredient ingredient = new Ingredient(ingredientDto.name, ingredientDto.info, ingredientDto.calories_per_gram);
        return new ResponseEntity<>(ingredientRepository.save(ingredient), HttpStatus.CREATED);
    }

    @PutMapping("tag")
    public ResponseEntity<Tag> create_tag(@RequestBody CreateTagDto createTagDto) {
        if (!tagRepository.findByName(createTagDto.name).isEmpty()) return new ResponseEntity<>(null, HttpStatus.CONFLICT);
        Tag tag = new Tag(createTagDto.name);
        return new ResponseEntity<>(tagRepository.save(tag), HttpStatus.CREATED);
    }

    @PutMapping("tags")
    public ResponseEntity<List<Tag>> create_tags(@RequestBody CreateTagsDto createTagsDto) {
        createTagsDto.names.forEach(e-> create_tag(new CreateTagDto(e)));
        return new ResponseEntity<>(null, HttpStatus.CREATED);
    }

    @PutMapping("ingredients")
    public ResponseEntity<List<Ingredient>> create_ingredients(@RequestBody CreateIngredientsDto createIngredientsDto) {
        createIngredientsDto.ingredients.forEach(e -> create_ingredient(new CreateIngredientDto(e.name, e.info, e.calories_per_gram)));
        return new ResponseEntity<>(null, HttpStatus.CREATED); // TODO; maybe collect returns from the queries and return list here, but not necessary for frontend
    }

    @PutMapping("image")
    public ResponseEntity<String> upload_image(@RequestParam("image") MultipartFile[] files) throws Exception {
        String uploadDirectory = "src/main/resources/static/";
        MultipartFile file = files[0];
        String imageString = imageService.saveImageToStorage(uploadDirectory, file);
        return new ResponseEntity<>(imageString, HttpStatus.CREATED);
    }

    @GetMapping("image/{imageName}")
    public ResponseEntity<byte[]> get_image(@PathVariable String imageName) throws Exception {
        String imageDirectory = "src/main/resources/static/";

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

    @DeleteMapping("tag")
    public ResponseEntity<Tag> delete_tag(@RequestParam("id") Long id) {
        Tag tag = tagRepository.findById(id).orElse(null);
        if (tag == null) return new ResponseEntity<>(null, HttpStatus.FAILED_DEPENDENCY);
        try {
            tagRepository.delete(tag);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(tag, HttpStatus.OK);
    }

    @GetMapping("tag")
    public ResponseEntity<List<Tag>> getTagsAll() {
        List<Tag> tags = tagRepository.findAll();
        return new ResponseEntity<>(tags, HttpStatus.OK);
    }
    @PostMapping("tag/names")
    public ResponseEntity<List<Tag>> getTagsByNames(@RequestBody List<String> names) {
        List<Tag> tags = new ArrayList<>();
        names.forEach(e -> tags.add(tagRepository.findByName(e).orElse(null)));
        List<Tag> finalTags = tags.stream().filter(e -> e != null).collect(Collectors.toList());
        return new ResponseEntity<>(finalTags, HttpStatus.OK);
    }
    @GetMapping("ingredient")
    public ResponseEntity<List<Ingredient>> getIngredientsAll() {
        List<Ingredient> ingredients = ingredientRepository.findAll();
        return new ResponseEntity<>(ingredients, HttpStatus.OK);
    }

    @GetMapping("recipe")
    public ResponseEntity<List<Recipe>> getRecipesAll() {
        List<Recipe> recipes = recipeRepository.findAll();
        return new ResponseEntity<>(recipes, HttpStatus.OK);
    }
}
