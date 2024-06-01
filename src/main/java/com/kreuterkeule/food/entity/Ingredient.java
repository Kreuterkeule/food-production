package com.kreuterkeule.food.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Entity
@NoArgsConstructor
public class Ingredient {

    public Ingredient(String name, String info, Integer calories_per_gram) {
        this.name = name;
        this.info = info;
        this.calories_per_gram = calories_per_gram;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String info;
    private Integer calories_per_gram;

    @Override
    public String toString() {
        return id + "|" + name + "|" + calories_per_gram;
    }
}
