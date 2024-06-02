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
import com.kreuterkeule.food.service.ListService;
import com.kreuterkeule.food.service.UserService;
import org.springframework.core.io.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
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
    private ListService listService;

    @Autowired
    public AppController(UserRepository userRepository, IngredientRepository ingredientRepository, RecipeRepository recipeRepository, UserDetailsManager userDetailsManager, UserService userService, ImageService imageService, TagRepository tagRepository, ListService listService) {
        this.userRepository = userRepository;
        this.ingredientRepository = ingredientRepository;
        this.recipeRepository = recipeRepository;
        this.users = userDetailsManager;
        this.userService = userService;
        this.imageService = imageService;
        this.tagRepository = tagRepository;
        this.listService = listService;
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
    public ResponseEntity<List<Recipe>> get_saved(
            @RequestParam(value = "page", defaultValue = "1", required = false) int page,
            @RequestParam(value = "page_size", defaultValue = "10", required = false) int page_size
    ) {
        UserEntity user = userService.getAuthenticatedUserEntity();
        if (user == null) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
        List<Recipe> all = user.getSaved_recipes();
        System.out.println(user);
        System.out.println(all);
        return new ResponseEntity<>(listService.getPage(all, page, page_size), HttpStatus.OK);
    }

    @GetMapping("save/{id}")
    public ResponseEntity<Recipe> save(@PathVariable("id") Long id) {
        UserEntity user = userService.getAuthenticatedUserEntity();
        if (user == null) return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        if (user.getSaved_recipes().stream().map(e -> e.getId()).toList().contains(id)) return new ResponseEntity<>(null, HttpStatus.PRECONDITION_FAILED);
        Recipe recipe = recipeRepository.findById(id).orElse(null);
        if (recipe == null) return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        user.addSavedRecipe(recipe);
        userRepository.saveAndFlush(user);
        return new ResponseEntity<>(recipe, HttpStatus.ACCEPTED);
    }

    @GetMapping("unsave/{id}")
    public ResponseEntity<Recipe> unsave(@PathVariable("id") Long id) {
        UserEntity user = userService.getAuthenticatedUserEntity();
        if (user == null) return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        Recipe recipe = recipeRepository.findById(id).orElse(null);
        if (recipe == null) return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        user.removeRecipe(recipe);
        userRepository.saveAndFlush(user);
        return new ResponseEntity<>(recipe, HttpStatus.ACCEPTED);
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
        if (recipeDto.id != null) { // update existing
            if (userService.getAuthenticatedUserEntity().getUsername() != recipeRepository.findById(recipeDto.id).get().getUser().getUsername()) {
                return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
            }
            recipe.setId(recipeDto.id);
        }
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
    public ResponseEntity<Resource> get_image(@PathVariable String imageName) throws Exception {
        System.out.println("GETTING IMAGE");
        String imageDirectory = "src/main/resources/static/";

        Resource image = imageService.getImage(imageDirectory, imageName);
        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG)
                .body(image);
    }

    @DeleteMapping("ingredient")
    public ResponseEntity<Ingredient> delete_ingredient(@RequestParam("id") Long id) {
        Ingredient ingredient = ingredientRepository.findById(id).orElse(null);
        if (ingredient == null) return new ResponseEntity<>(null, HttpStatus.FAILED_DEPENDENCY);
        try {
            ingredientRepository.delete(ingredient);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(ingredient, HttpStatus.OK);
    }

    @DeleteMapping("recipe/{id}")
    public ResponseEntity<String> delete_recipe(@PathVariable("id") Long id) {
        String authenticateUsername = userService.getAuthenticatedUser().getUsername();
        Recipe recipe = recipeRepository.findById(id).orElse(null);
        if (recipe == null) return new ResponseEntity<>(null, HttpStatus.FAILED_DEPENDENCY);
        if (!recipe.getUser().getUsername().equals(authenticateUsername)) return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
        try {
            System.out.println(recipe.getUsersSaved());
            recipe.getUsersSaved().forEach(e -> e.removeRecipe(recipe));
            recipeRepository.delete(recipe);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>("Success", HttpStatus.OK);

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
    public ResponseEntity<List<Recipe>> getRecipesAll(
            @RequestParam(value = "page", defaultValue = "1", required = false) int page,
            @RequestParam(value = "page_size", defaultValue = "25", required = false) int page_size) {
        Page<Recipe> recipes = recipeRepository.findAll(
                PageRequest.of(page - 1, page_size, Sort.by(Sort.Direction.DESC, "updatedDate")));
        List<Recipe> recipeList = recipes.stream().toList();
        return new ResponseEntity<>(recipeList, HttpStatus.OK);
    }

    @GetMapping("recipe/{id}")
    public ResponseEntity<Recipe> getRecipeById(@PathVariable("id") Long id) {
        Recipe recipe = recipeRepository.findById(id).orElse(null);
        if (recipe == null) return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(recipe, HttpStatus.OK);
    }

    @PostMapping("recipe/ingredients")
    public ResponseEntity<List<Recipe>> recipesForIngredients(@RequestBody RecipesForIngredientsDto recipesForIngredientsDto) {
        List<Recipe> all = recipeRepository.findAll();
        List<Recipe> filtered = all.stream().filter(e -> e.getIngredients().keySet().stream().map(i -> i.getName()).collect(Collectors.toList()).containsAll(recipesForIngredientsDto.names))
                .toList();
        System.out.println(filtered);
        List<Recipe> page = ListService.getPage(filtered, recipesForIngredientsDto.page, recipesForIngredientsDto.page_size);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    @PostMapping("recipe/tags")
    public ResponseEntity<List<Recipe>> recipesForTags(@RequestBody RecipesForTagsDto recipesForTagsDto) {
        List<Recipe> all = recipeRepository.findAll();
        List<Recipe> filtered = all.stream().filter(e -> e.getTags().stream().map(i -> i.getName()).collect(Collectors.toList()).containsAll(recipesForTagsDto.names))
                .toList();
        List<Recipe> page = ListService.getPage(filtered, recipesForTagsDto.page, recipesForTagsDto.page_size);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    @GetMapping("recipe/user")
    public ResponseEntity<List<Recipe>> recipesForUser(@RequestParam("username") String username,
            @RequestParam(value = "page", defaultValue = "1", required = false) int page,
            @RequestParam(value = "page_size", defaultValue = "25", required = false) int page_size
        ) {
        UserEntity user = userRepository.findByUsername(username).orElse(null);
        if (user == null) return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        List<Recipe> recipes = user.getOwn_recipes();
        List<Recipe> recipe_page = listService.getPage(recipes, page, page_size);
        return new ResponseEntity<>(recipe_page, HttpStatus.OK);

    }

    @GetMapping("user")
    public ResponseEntity<List<UserEntity>> getUser(
            @RequestParam(value = "page", defaultValue = "1", required = false) int page,
            @RequestParam(value = "page_size", defaultValue = "25", required = false) int page_size
            ) {
        Page<UserEntity> users = userRepository.findAll(
                PageRequest.of(page - 1, page_size)
        );
        List<UserEntity> usersList = users.stream().toList();
        return new ResponseEntity<>(usersList, HttpStatus.OK);
    }

    @GetMapping("/user/string")
    public ResponseEntity<List<UserEntity>> getUserByString(@RequestParam("string") String string,
            @RequestParam(value = "page", defaultValue = "1", required = false) int page,
            @RequestParam(value = "page_size", defaultValue = "25", required = false) int page_size
    ) {
        Page<UserEntity> users = userRepository.findAllByUsernameLikeIgnoreCase(
                "%" + string + "%",
                PageRequest.of(page - 1, page_size)
        );
        List<UserEntity> usersList = users.stream().toList();
        return new ResponseEntity<>(usersList, HttpStatus.OK);
    }

    @GetMapping("/recipe/search")
    public ResponseEntity<List<Recipe>> searchRecipes(
            @RequestParam("string") String string,
            @RequestParam(value = "page", defaultValue = "1", required = false) int page,
            @RequestParam(value = "page_size", defaultValue = "25", required = false) int page_size
    ) {
        String searchString = "%" + string + "%";
        List<Recipe> recipes = recipeRepository.findAllByTextLikeIgnoreCaseOrNameLikeIgnoreCase(
                searchString, searchString
        );

        /*
        * A Lot of auto IDE problem-solving done here (something with finalLists in lambda
        */

        List<Recipe> t = recipes.stream().toList(); // Attention this list is immutable
        List<Recipe> recipeList = new ArrayList<>();
        List<Recipe> finalRecipeList2 = recipeList;
        t.forEach(e -> finalRecipeList2.add(e));
        List<Tag> tags = tagRepository.findByNameLikeIgnoreCase(searchString);
        List<Recipe> r = recipeRepository.findDistinctByTagsIn(tags);
        List<Recipe> finalRecipeList1 = recipeList;
        r.forEach(e -> {
            if (finalRecipeList1.stream().map(i -> i.getId()).toList().contains(e.getId())) return;
            finalRecipeList1.add(e);
        });
        // TODO optimize ingredients for performance
        // Maybe bidirectional relationship with recipes but json ignore...
        List<Ingredient> ingredients = ingredientRepository.findByNameLikeIgnoreCase(searchString);
        r = recipeRepository.findAll();
        List<Recipe> filteredRecipes = r.stream().filter(e -> ingredients.stream().anyMatch(i -> e.getIngredients().containsKey(i))).toList();
        List<Recipe> finalRecipeList = recipeList;
        filteredRecipes.forEach(e -> {
            if (finalRecipeList.stream().map(i -> i.getId()).toList().contains(e.getId())) return;
            finalRecipeList.add(e);
        });
        List<UserEntity> users = userRepository.findAllByUsernameLikeIgnoreCase(searchString, Pageable.unpaged()).stream().toList();
        users.forEach(e -> e.getOwn_recipes().forEach(o -> {
            if (finalRecipeList.stream().map(i -> i.getId()).toList().contains(o.getId())) return;
            finalRecipeList.add(o);
        }));
        recipeList = listService.getPage(recipeList, page, page_size);
        return new ResponseEntity<>(recipeList, HttpStatus.OK);
    }
}
