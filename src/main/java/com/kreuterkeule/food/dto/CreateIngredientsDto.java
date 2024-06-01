package com.kreuterkeule.food.dto;

import lombok.Data;

import java.util.List;

@Data
public class CreateIngredientsDto {

    public List<CreateIngredientDto> ingredients;

}
