package com.kreuterkeule.food.dto;

import lombok.Data;

import java.util.List;

@Data
public class RecipesForIngredientsDto {

    public List<String> names;
    public int page;
    public int page_size;

}
