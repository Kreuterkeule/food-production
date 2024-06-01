package com.kreuterkeule.food.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateIngredientDto {

    public String name;
    public String info;
    public Integer calories_per_gram;
}
