package com.kreuterkeule.food.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;


@Data
@Entity
@Getter
@Setter
public class Recipe {

    public Recipe() {

    }
    public Recipe(Map<Ingredient, Integer> ingredients, String name, Integer time, String text, UserEntity user) {
        this.ingredients = ingredients;
        this.time = time;
        this.text = text;
        this.user = user;
        this.name = name;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @JsonIgnore
    @ElementCollection
    @CollectionTable(name="ingredient_amount",
            joinColumns=@JoinColumn(name="recipe_id"))
    @MapKeyJoinColumn(name="ingredient_id")
    @Column(name="qty")
    private Map<Ingredient, Integer> ingredients;
    private Integer time;
    private String name;
    @Column(name = "created_date", updatable = false)
    @CreatedDate
    private Date created_date;
    @UpdateTimestamp
    private Timestamp updated_date;
    @LastModifiedDate
    private Date modified_date;
    private String text;
    @ManyToOne
    @JoinColumn(name="user_id", nullable=false)
    @JsonIgnore
    private UserEntity user;

    @Override
    public String toString() {
        return "Recipe{" +
                "id=" + id +
                ", ingredients=" + ingredients +
                ", time=" + time +
                ", name='" + name + '\'' +
                ", created_date=" + created_date +
                ", updated_date=" + updated_date +
                ", modified_date=" + modified_date +
                ", text='" + text + '\'' +
                '}';
    }
}
