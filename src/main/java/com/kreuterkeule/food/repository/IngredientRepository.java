package com.kreuterkeule.food.repository;

import com.kreuterkeule.food.entity.Ingredient;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
    
    Optional<Ingredient> findByName(String name);

    List<Ingredient> findByNameLikeIgnoreCase(String name);

}
