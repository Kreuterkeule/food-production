package com.kreuterkeule.food.dto;

import lombok.Data;

@Data
public class CreateIngredientDto {

    public String name;
    public String info;
    public Integer calories_per_gram;
}
