package com.kreuterkeule.food.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.List;

@Data
@Entity
@Getter
@Setter
@Table(name = "user_data")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    @NotNull
    private String username; // same as users username
    private String info;
    @OneToMany(mappedBy="user")
    private List<Recipe> own_recipes;
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
}
