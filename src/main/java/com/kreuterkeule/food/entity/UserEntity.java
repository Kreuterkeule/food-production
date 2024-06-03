package com.kreuterkeule.food.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.kreuterkeule.food.serializer.UserSerializer;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Getter
@Setter
@Table(name = "user_data")
@JsonSerialize(using = UserSerializer.class)
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    @NotNull
    private String username; // same as users username
    private String info;
    @OneToMany(mappedBy="user")
    private List<Recipe> own_recipes = new ArrayList<>();
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_saved_recipes",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "recipe_id", referencedColumnName = "id"))
    private List<Recipe> saved_recipes;

    public UserEntity(String username) {
        this.username = username;
    }

    public UserEntity() {

    }

    public void addSavedRecipe(Recipe recipe) {
        this.saved_recipes.add(recipe);
    }

    public void removeRecipe(Recipe el) {
        this.saved_recipes.remove(el);
    }
}
