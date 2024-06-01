package com.kreuterkeule.food.dto;

import com.kreuterkeule.food.entity.Tag;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class CreateRecipeDto {

    public String name;
    public String text;
    public Integer time;
    // TODO: fix this jackson conversion
    public Map<String, String> ingredient_amount; // ids, amount

    public List<Integer> tag_ids;
    private String imageUrl;
}
