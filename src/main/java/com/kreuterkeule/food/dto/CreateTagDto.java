package com.kreuterkeule.food.dto;

import lombok.Data;

@Data
public class CreateTagDto {
    
    public CreateTagDto() {}
    public CreateTagDto(String name) {
        this.name = name;
    }
    
    public String name;

}
