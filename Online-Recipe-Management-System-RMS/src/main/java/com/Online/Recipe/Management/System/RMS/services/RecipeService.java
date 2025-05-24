package com.Online.Recipe.Management.System.RMS.services;

import com.Online.Recipe.Management.System.RMS.dtos.RecipeCreationRequest;
import com.Online.Recipe.Management.System.RMS.dtos.RecipeDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RecipeService {
    RecipeDTO createRecipe(RecipeCreationRequest request, String authenticatedUserEmail);
    Page<RecipeDTO> getAllRecipes(Pageable pageable);
    RecipeDTO getRecipeById(String recipeId);
    RecipeDTO updateRecipe(String recipeId, com.Online.Recipe.Management.System.RMS.dtos.RecipeUpdateDTO request, String authenticatedUserEmail);
    void deleteRecipe(String recipeId, String authenticatedUserEmail);
}
