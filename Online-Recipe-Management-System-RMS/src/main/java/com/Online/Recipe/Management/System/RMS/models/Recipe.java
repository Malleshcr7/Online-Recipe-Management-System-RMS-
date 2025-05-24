package com.Online.Recipe.Management.System.RMS.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "recipes")
public class Recipe {

    @Id
    private String id;

    @TextIndexed
    private String name;

    @TextIndexed
    private String description;

    private List<Ingredient> ingredients;
    private List<String> instructions;

    private Integer preparationTimeInMinutes;
    private Integer cookingTimeInMinutes;
    private Integer servings;

    private String category; // e.g., "Dessert", "Main Course"
    private String difficulty; // e.g., "Easy", "Medium", "Hard"

    @TextIndexed
    private List<String> tags;

    private String imageUrl; // Optional

    @Indexed
    private String userId; // ID of the user who created the recipe

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
