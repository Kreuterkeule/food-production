package com.kreuterkeule.food.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.kreuterkeule.food.repository.TagRepository;
import com.kreuterkeule.food.serializer.TagSerializer;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@JsonSerialize(using = TagSerializer.class)
public class Tag {

    public Tag() {
        this.recipes = new ArrayList<>();
    }
    public Tag(String name) {
        this.name = name;
        this.recipes = new ArrayList<>();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    @ManyToMany(mappedBy = "tags")
    @JsonIgnore // prevent circularity whiles generating json -> we also just need the recipes.length
    private List<Recipe> recipes;
}
