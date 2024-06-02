package com.kreuterkeule.food.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.tags.Tags;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;


@Data
@Entity
@Getter
@Setter
public class Recipe {

    public Recipe() {

    }
    public Recipe(List<Tag> tags, Map<Ingredient, String> ingredients, String name, Integer time, String text, UserEntity user, String imageUrl) {
        this.ingredients = ingredients;
        this.time = time;
        this.text = text;
        this.user = user;
        this.name = name;
        this.tags = tags;
        this.imageUrl = imageUrl;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ElementCollection
    @CollectionTable(name="ingredient_amount",
            joinColumns=@JoinColumn(name="recipe_id"))
    @MapKeyJoinColumn(name="ingredient_id")
    @Column(name="qty")
    private Map<Ingredient, String> ingredients;
    private Integer time;
    private String name;
    @Column(name = "created_date", updatable = false)
    @CreatedDate
    private Date createdDate;
    @UpdateTimestamp
    private Timestamp updatedDate;
    @LastModifiedDate
    private Date modified_date;
    @Column(columnDefinition = "text")
    private String text;
    @ManyToOne
    @JoinColumn(name="user_id", nullable=false)
    private UserEntity user;
    @ManyToMany
    @JoinTable(
            name = "recipe_tag",
            joinColumns = @JoinColumn(name = "recipe_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private List<Tag> tags;
    private String imageUrl;
    @ManyToMany(mappedBy = "saved_recipes")
    private List<UserEntity> usersSaved;

    @Override
    public String toString() {
        return "Recipe{" +
                "id=" + id +
                ", ingredients=" + ingredients +
                ", time=" + time +
                ", name='" + name + '\'' +
                ", created_date=" + createdDate +
                ", updated_date=" + updatedDate +
                ", modified_date=" + modified_date +
                ", text='" + text + '\'' +
                '}';
    }
}
