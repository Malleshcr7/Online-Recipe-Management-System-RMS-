package com.Online.Recipe.Management.System.RMS.controllers;

import com.Online.Recipe.Management.System.RMS.dtos.RecipeCreationRequest;
import com.Online.Recipe.Management.System.RMS.dtos.RecipeDTO;
import com.Online.Recipe.Management.System.RMS.services.RecipeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import com.Online.Recipe.Management.System.RMS.dtos.RecipeUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recipes")
public class RecipeController {

    private final RecipeService recipeService;

    @Autowired
    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @PostMapping
    public ResponseEntity<RecipeDTO> createRecipe(@Valid @RequestBody RecipeCreationRequest request,
                                                  Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            // This case should ideally be caught by Spring Security filters
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String authenticatedUserEmail;
        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails) {
            authenticatedUserEmail = ((UserDetails) principal).getUsername();
        } else if (principal instanceof String) {
            authenticatedUserEmail = (String) principal; // Sometimes it's directly the username string
        } 
        else {
            // Fallback or error if principal is not recognized
            // This might indicate a misconfiguration or an unexpected authentication type
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        
        RecipeDTO recipeDTO = recipeService.createRecipe(request, authenticatedUserEmail);
        return ResponseEntity.status(HttpStatus.CREATED).body(recipeDTO);
    }

    @GetMapping
    public ResponseEntity<Page<RecipeDTO>> getAllRecipes(Pageable pageable) {
        Page<RecipeDTO> recipeDTOPage = recipeService.getAllRecipes(pageable);
        return ResponseEntity.ok(recipeDTOPage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecipeDTO> getRecipeById(@PathVariable String id) {
        RecipeDTO recipeDTO = recipeService.getRecipeById(id);
        return ResponseEntity.ok(recipeDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RecipeDTO> updateRecipe(@PathVariable String id,
                                                  @Valid @RequestBody RecipeUpdateDTO request,
                                                  Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            // This case should ideally be caught by Spring Security filters
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String authenticatedUserEmail;
        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails) {
            authenticatedUserEmail = ((UserDetails) principal).getUsername();
        } else if (principal instanceof String) {
            authenticatedUserEmail = (String) principal;
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        RecipeDTO updatedRecipeDTO = recipeService.updateRecipe(id, request, authenticatedUserEmail);
        return ResponseEntity.ok(updatedRecipeDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecipe(@PathVariable String id,
                                           Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            // This case should ideally be caught by Spring Security filters
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String authenticatedUserEmail;
        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails) {
            authenticatedUserEmail = ((UserDetails) principal).getUsername();
        } else if (principal instanceof String) {
            authenticatedUserEmail = (String) principal;
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        recipeService.deleteRecipe(id, authenticatedUserEmail);
        return ResponseEntity.noContent().build(); // HTTP 204
    }
}
