package com.Online.Recipe.Management.System.RMS.services.impl;

import com.Online.Recipe.Management.System.RMS.dtos.IngredientDTO;
import com.Online.Recipe.Management.System.RMS.dtos.RecipeCreationRequest;
import com.Online.Recipe.Management.System.RMS.dtos.RecipeDTO;
import com.Online.Recipe.Management.System.RMS.models.Ingredient;
import com.Online.Recipe.Management.System.RMS.models.Recipe;
import com.Online.Recipe.Management.System.RMS.models.User;
import com.Online.Recipe.Management.System.RMS.repositories.RecipeRepository;
import com.Online.Recipe.Management.System.RMS.repositories.UserRepository;
import com.Online.Recipe.Management.System.RMS.services.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import com.Online.Recipe.Management.System.RMS.exceptions.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import com.Online.Recipe.Management.System.RMS.dtos.RecipeUpdateDTO;
import com.Online.Recipe.Management.System.RMS.exceptions.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class RecipeServiceImpl implements RecipeService {

    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;

    @Autowired
    public RecipeServiceImpl(RecipeRepository recipeRepository, UserRepository userRepository) {
        this.recipeRepository = recipeRepository;
        this.userRepository = userRepository;
    }

    @Override
    public RecipeDTO createRecipe(RecipeCreationRequest request, String authenticatedUserEmail) {
        User user = userRepository.findByEmail(authenticatedUserEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + authenticatedUserEmail));

        Recipe recipe = new Recipe();
        recipe.setName(request.getName());
        recipe.setDescription(request.getDescription());
        
        if (request.getIngredients() != null) {
            recipe.setIngredients(request.getIngredients().stream()
                    .map(dto -> new Ingredient(dto.getName(), dto.getQuantity(), dto.getUnit()))
                    .collect(Collectors.toList()));
        }
        
        recipe.setInstructions(request.getInstructions());
        recipe.setPreparationTimeInMinutes(request.getPreparationTimeInMinutes());
        recipe.setCookingTimeInMinutes(request.getCookingTimeInMinutes());
        recipe.setServings(request.getServings());
        recipe.setCategory(request.getCategory());
        recipe.setDifficulty(request.getDifficulty());
        recipe.setTags(request.getTags());
        recipe.setImageUrl(request.getImageUrl());
        recipe.setUserId(user.getId());
        // createdAt and updatedAt will be set by Mongo Auditing

        Recipe savedRecipe = recipeRepository.save(recipe);
        return convertToDTO(savedRecipe);
    }

    @Override
    public Page<RecipeDTO> getAllRecipes(Pageable pageable) {
        Page<Recipe> recipePage = recipeRepository.findAll(pageable);
        return recipePage.map(this::convertToDTO);
    }

    @Override
    public RecipeDTO getRecipeById(String recipeId) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe not found with id: " + recipeId));
        return convertToDTO(recipe);
    }

    @Override
    public RecipeDTO updateRecipe(String recipeId, RecipeUpdateDTO request, String authenticatedUserEmail) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe not found with id: " + recipeId));

        User authenticatedUser = userRepository.findByEmail(authenticatedUserEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Authenticated user not found with email: " + authenticatedUserEmail));

        if (!recipe.getUserId().equals(authenticatedUser.getId())) {
            throw new AccessDeniedException("Users can only update their own recipes.");
        }

        // Update fields from request
        recipe.setName(request.getName());
        recipe.setDescription(request.getDescription());
        if (request.getIngredients() != null) {
            recipe.setIngredients(request.getIngredients().stream()
                    .map(dto -> new Ingredient(dto.getName(), dto.getQuantity(), dto.getUnit()))
                    .collect(Collectors.toList()));
        }
        recipe.setInstructions(request.getInstructions());
        recipe.setPreparationTimeInMinutes(request.getPreparationTimeInMinutes());
        recipe.setCookingTimeInMinutes(request.getCookingTimeInMinutes());
        recipe.setServings(request.getServings());
        recipe.setCategory(request.getCategory());
        recipe.setDifficulty(request.getDifficulty());
        recipe.setTags(request.getTags());
        recipe.setImageUrl(request.getImageUrl());
        // userId remains the same
        // createdAt is not changed
        // updatedAt will be handled by Mongo Auditing

        Recipe updatedRecipe = recipeRepository.save(recipe);
        return convertToDTO(updatedRecipe);
    }

    @Override
    public void deleteRecipe(String recipeId, String authenticatedUserEmail) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe not found with id: " + recipeId));

        User authenticatedUser = userRepository.findByEmail(authenticatedUserEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Authenticated user not found with email: " + authenticatedUserEmail));

        if (!recipe.getUserId().equals(authenticatedUser.getId())) {
            throw new AccessDeniedException("Users can only delete their own recipes.");
        }

        recipeRepository.deleteById(recipeId);
    }

    // Renamed from convertToRecipeDTO for consistency with previous implementation
    private RecipeDTO convertToDTO(Recipe recipe) { 
        if (recipe == null) return null;
        return new RecipeDTO(
                recipe.getId(),
                recipe.getName(),
                recipe.getDescription(),
                recipe.getIngredients() != null ? recipe.getIngredients().stream()
                        .map(this::convertIngredientToDTO) // Using helper for ingredient mapping
                        .collect(Collectors.toList()) : null,
                recipe.getInstructions(),
                recipe.getPreparationTimeInMinutes(),
                recipe.getCookingTimeInMinutes(),
                recipe.getServings(),
                recipe.getCategory(),
                recipe.getDifficulty(),
                recipe.getTags(),
                recipe.getImageUrl(),
                recipe.getUserId(),
                recipe.getCreatedAt(),
                recipe.getUpdatedAt()
        );
    }

    private IngredientDTO convertIngredientToDTO(Ingredient ingredient) {
        if (ingredient == null) return null;
        return new IngredientDTO(
                ingredient.getName(),
                ingredient.getQuantity(),
                ingredient.getUnit()
        );
    }
}
