package com.Online.Recipe.Management.System.RMS.services;

import com.Online.Recipe.Management.System.RMS.dtos.JwtAuthenticationResponse;
import com.Online.Recipe.Management.System.RMS.dtos.LoginRequest;
import com.Online.Recipe.Management.System.RMS.dtos.RegistrationRequest;
import com.Online.Recipe.Management.System.RMS.dtos.UserDTO;

public interface UserService {
    UserDTO registerUser(RegistrationRequest registrationRequest);
    JwtAuthenticationResponse loginUser(LoginRequest loginRequest);
    void requestPasswordReset(String email);
    void resetPassword(String token, String newPassword);
import com.Online.Recipe.Management.System.RMS.dtos.RecipeDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    UserDTO registerUser(RegistrationRequest registrationRequest);
    JwtAuthenticationResponse loginUser(LoginRequest loginRequest);
    void requestPasswordReset(String email);
    void resetPassword(String token, String newPassword);
    UserDTO getUserProfile(String userId);
    UserDTO getUserDTOByEmail(String email); // For fetching authenticated user's details
    UserDTO updateUserProfile(String userId, com.Online.Recipe.Management.System.RMS.dtos.UpdateUserProfileRequest updateRequest, String authenticatedUserEmail);
    void addRecipeToFavorites(String recipeId, String authenticatedUserEmail);
    void removeRecipeFromFavorites(String recipeId, String authenticatedUserEmail);
    Page<RecipeDTO> getFavoriteRecipes(String authenticatedUserEmail, Pageable pageable);
}
