package com.Online.Recipe.Management.System.RMS.controllers;

import com.Online.Recipe.Management.System.RMS.dtos.UserDTO;
import com.Online.Recipe.Management.System.RMS.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.Online.Recipe.Management.System.RMS.dtos.UpdateUserProfileRequest;
import com.Online.Recipe.Management.System.RMS.exceptions.ResourceNotFoundException;
import jakarta.validation.Valid;
import com.Online.Recipe.Management.System.RMS.dtos.RecipeDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;


@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable String id, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String authenticatedUserEmail = authentication.getName(); // This is the email
        UserDTO authenticatedUserDTO = userService.getUserDTOByEmail(authenticatedUserEmail);

        if (!authenticatedUserDTO.getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        UserDTO userProfile = userService.getUserProfile(id);
        return ResponseEntity.ok(userProfile);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUserProfile(@PathVariable String id, 
                                                 @Valid @RequestBody UpdateUserProfileRequest updateRequest, 
                                                 Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not authenticated.");
        }
        String authenticatedUserEmail = authentication.getName();

        try {
            UserDTO updatedUserDTO = userService.updateUserProfile(id, updateRequest, authenticatedUserEmail);
            return ResponseEntity.ok(updatedUserDTO);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (IllegalArgumentException e) { // For username taken or other bad inputs from service
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            // Generic error handler for unexpected errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }

    // Helper to get email, can be refactored or made part of a base controller if used frequently
    private String getAuthenticatedUserEmail(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("User is not authenticated.");
        }
        return authentication.getName();
    }

    @PostMapping("/me/favorites/{recipeId}")
    public ResponseEntity<String> addRecipeToFavorites(@PathVariable String recipeId, Authentication authentication) {
        String authenticatedUserEmail = getAuthenticatedUserEmail(authentication);
        userService.addRecipeToFavorites(recipeId, authenticatedUserEmail);
        return ResponseEntity.ok("Recipe added to favorites.");
    }

    @DeleteMapping("/me/favorites/{recipeId}")
    public ResponseEntity<String> removeRecipeFromFavorites(@PathVariable String recipeId, Authentication authentication) {
        String authenticatedUserEmail = getAuthenticatedUserEmail(authentication);
        userService.removeRecipeFromFavorites(recipeId, authenticatedUserEmail);
        return ResponseEntity.ok("Recipe removed from favorites.");
    }

    @GetMapping("/me/favorites")
    public ResponseEntity<Page<RecipeDTO>> getFavoriteRecipes(Pageable pageable, Authentication authentication) {
        String authenticatedUserEmail = getAuthenticatedUserEmail(authentication);
        Page<RecipeDTO> favoriteRecipes = userService.getFavoriteRecipes(authenticatedUserEmail, pageable);
        return ResponseEntity.ok(favoriteRecipes);
    }
}
