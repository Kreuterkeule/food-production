package com.kreuterkeule.food.dto;

import lombok.Data;

import java.util.Map;

@Data
public class CreateRecipeDto {

    public String name;
    public String text;
    public Integer time;
    // TODO: fix this jackson conversion
    public Map<String, Integer> ingredient_amount; // ids, amount

}
