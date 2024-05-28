package com.kreuterkeule.food.repository;

import com.kreuterkeule.food.entity.Recipe;
import com.kreuterkeule.food.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    List<Recipe> getRecipesByUser(UserEntity user);
}
