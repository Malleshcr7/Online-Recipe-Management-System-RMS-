package com.Online.Recipe.Management.System.RMS.dtos;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecipeCreationRequest {

    @NotBlank(message = "Recipe name is required.")
    @Size(min = 3, max = 100, message = "Recipe name must be between 3 and 100 characters.")
    private String name;

    @NotBlank(message = "Description is required.")
    @Size(max = 1000, message = "Description cannot exceed 1000 characters.")
    private String description;

    @NotEmpty(message = "At least one ingredient is required.")
    private List<IngredientDTO> ingredients;

    @NotEmpty(message = "At least one instruction is required.")
    private List<String> instructions;

    @NotNull(message = "Preparation time is required.")
    @Min(value = 1, message = "Preparation time must be at least 1 minute.")
    private Integer preparationTimeInMinutes;

    @NotNull(message = "Cooking time is required.")
    @Min(value = 1, message = "Cooking time must be at least 1 minute.")
    private Integer cookingTimeInMinutes;

    @NotNull(message = "Number of servings is required.")
    @Min(value = 1, message = "Servings must be at least 1.")
    private Integer servings;

    @NotBlank(message = "Category is required.")
    private String category;

    @NotBlank(message = "Difficulty is required.")
    private String difficulty;

    private List<String> tags; // Optional

    private String imageUrl; // Optional
}
