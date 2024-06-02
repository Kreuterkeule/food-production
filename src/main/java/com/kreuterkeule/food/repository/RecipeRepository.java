package com.kreuterkeule.food.repository;

import com.kreuterkeule.food.entity.Ingredient;
import com.kreuterkeule.food.entity.Recipe;
import com.kreuterkeule.food.entity.Tag;
import com.kreuterkeule.food.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    List<Recipe> getRecipesByUser(UserEntity user);

    List<Recipe> findAllByTextLikeIgnoreCaseOrNameLikeIgnoreCase(String text, String name);

    List<Recipe> findDistinctByTagsIn(List<Tag> tags);

    List<Recipe> findDistinctByIngredientsIn(List<Ingredient> ingredients);

}
