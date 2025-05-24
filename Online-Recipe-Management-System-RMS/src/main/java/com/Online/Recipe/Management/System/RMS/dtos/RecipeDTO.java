package com.Online.Recipe.Management.System.RMS.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecipeDTO {
    private String id;
    private String name;
    private String description;
    private List<IngredientDTO> ingredients;
    private List<String> instructions;
    private Integer preparationTimeInMinutes;
    private Integer cookingTimeInMinutes;
    private Integer servings;
    private String category;
    private String difficulty;
    private List<String> tags;
    private String imageUrl;
    private String userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
