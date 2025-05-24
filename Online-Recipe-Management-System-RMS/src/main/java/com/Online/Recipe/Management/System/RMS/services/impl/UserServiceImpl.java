package com.Online.Recipe.Management.System.RMS.services.impl;

import com.Online.Recipe.Management.System.RMS.dtos.JwtAuthenticationResponse;
import com.Online.Recipe.Management.System.RMS.dtos.LoginRequest;
import com.Online.Recipe.Management.System.RMS.dtos.RegistrationRequest;
import com.Online.Recipe.Management.System.RMS.dtos.UserDTO;
import com.Online.Recipe.Management.System.RMS.models.User;
import com.Online.Recipe.Management.System.RMS.repositories.UserRepository;
import com.Online.Recipe.Management.System.RMS.security.JwtTokenProvider;
import com.Online.Recipe.Management.System.RMS.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.Online.Recipe.Management.System.RMS.dtos.IngredientDTO;
import com.Online.Recipe.Management.System.RMS.dtos.RecipeDTO;
import com.Online.Recipe.Management.System.RMS.exceptions.ResourceNotFoundException;
import com.Online.Recipe.Management.System.RMS.models.Recipe;
import com.Online.Recipe.Management.System.RMS.repositories.RecipeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final RecipeRepository recipeRepository; // Injected RecipeRepository

    @Autowired
    public UserServiceImpl(UserRepository userRepository, 
                           PasswordEncoder passwordEncoder,
                           AuthenticationManager authenticationManager,
                           JwtTokenProvider jwtTokenProvider,
                           RecipeRepository recipeRepository) { // Added recipeRepository to constructor
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.recipeRepository = recipeRepository; // Initialize recipeRepository
    }

    @Override
    public UserDTO registerUser(RegistrationRequest registrationRequest) {
        if (userRepository.existsByUsername(registrationRequest.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        if (userRepository.existsByEmail(registrationRequest.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = new User();
        user.setUsername(registrationRequest.getUsername());
        user.setEmail(registrationRequest.getEmail());
        user.setPasswordHash(passwordEncoder.encode(registrationRequest.getPassword()));

        User savedUser = userRepository.save(user);

        return new UserDTO(savedUser.getId(), savedUser.getUsername(), savedUser.getEmail(), savedUser.getBio());
    }

    @Override
    public JwtAuthenticationResponse loginUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtTokenProvider.generateToken(authentication);

        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new BadCredentialsException("User not found after authentication"));
        
        UserDTO userDTO = new UserDTO(user.getId(), user.getUsername(), user.getEmail(), user.getBio());
        return new JwtAuthenticationResponse(jwt, userDTO);
    }

    @Override
    public void requestPasswordReset(String email) {
        userRepository.findByEmail(email).ifPresentOrElse(user -> {
            String token = UUID.randomUUID().toString();
            user.setPasswordResetToken(token);
            user.setPasswordResetTokenExpiry(LocalDateTime.now().plusHours(1)); // Token valid for 1 hour
            userRepository.save(user);
            // Simulate email sending
            logger.info("Password reset token for user {} (email: {}): {}. Send this token via email.", user.getUsername(), email, token);
        }, () -> {
            // User not found, log a generic message to prevent email enumeration
            logger.info("Password reset requested for non-existent email: {}", email);
        });
    }

    @Override
    public void resetPassword(String token, String newPassword) {
        User user = userRepository.findByPasswordResetToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid password reset token."));

        if (user.getPasswordResetTokenExpiry() == null || user.getPasswordResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Password reset token has expired.");
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setPasswordResetToken(null);
        user.setPasswordResetTokenExpiry(null);
        userRepository.save(user);
        logger.info("Password for user {} has been successfully reset.", user.getUsername());
    }

    @Override
    public UserDTO getUserProfile(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        return new UserDTO(user.getId(), user.getUsername(), user.getEmail(), user.getBio());
    }

    @Override
    public UserDTO getUserDTOByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return new UserDTO(user.getId(), user.getUsername(), user.getEmail(), user.getBio());
    }

    @Override
    public UserDTO updateUserProfile(String userId, com.Online.Recipe.Management.System.RMS.dtos.UpdateUserProfileRequest updateRequest, String authenticatedUserEmail) {
        User userToUpdate = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        User authenticatedUser = userRepository.findByEmail(authenticatedUserEmail)
                .orElseThrow(() -> new org.springframework.security.core.userdetails.UsernameNotFoundException("Authenticated user not found with email: " + authenticatedUserEmail));

        if (!userToUpdate.getId().equals(authenticatedUser.getId())) {
            throw new org.springframework.security.access.AccessDeniedException("Users can only update their own profiles.");
        }

        boolean updated = false;
        if (updateRequest.getUsername() != null && !updateRequest.getUsername().isEmpty() && !updateRequest.getUsername().equals(userToUpdate.getUsername())) {
            if (userRepository.existsByUsername(updateRequest.getUsername())) {
                throw new IllegalArgumentException("Username '" + updateRequest.getUsername() + "' is already taken by another user.");
            }
            userToUpdate.setUsername(updateRequest.getUsername());
            updated = true;
        }

        if (updateRequest.getBio() != null) {
            userToUpdate.setBio(updateRequest.getBio());
            updated = true;
        }

        if (updated) {
            User savedUser = userRepository.save(userToUpdate);
            return new UserDTO(savedUser.getId(), savedUser.getUsername(), savedUser.getEmail(), savedUser.getBio());
        }
        
        // If no fields were actually updated, just return the current DTO
        return new UserDTO(userToUpdate.getId(), userToUpdate.getUsername(), userToUpdate.getEmail(), userToUpdate.getBio());
    }

    @Override
    public void addRecipeToFavorites(String recipeId, String authenticatedUserEmail) {
        User user = userRepository.findByEmail(authenticatedUserEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + authenticatedUserEmail));
        
        if (!recipeRepository.existsById(recipeId)) {
            throw new ResourceNotFoundException("Recipe not found with id: " + recipeId);
        }

        user.getFavoriteRecipeIds().add(recipeId);
        userRepository.save(user);
    }

    @Override
    public void removeRecipeFromFavorites(String recipeId, String authenticatedUserEmail) {
        User user = userRepository.findByEmail(authenticatedUserEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + authenticatedUserEmail));
        
        user.getFavoriteRecipeIds().remove(recipeId);
        userRepository.save(user);
    }

    @Override
    public Page<RecipeDTO> getFavoriteRecipes(String authenticatedUserEmail, Pageable pageable) {
        User user = userRepository.findByEmail(authenticatedUserEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + authenticatedUserEmail));

        if (user.getFavoriteRecipeIds() == null || user.getFavoriteRecipeIds().isEmpty()) {
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }
        
        // Assuming RecipeRepository has findAllByIdIn method (will be added next)
        Page<Recipe> favoriteRecipesPage = recipeRepository.findAllByIdIn(user.getFavoriteRecipeIds(), pageable);
        
        // Need to convert Page<Recipe> to Page<RecipeDTO>
        // Re-using the conversion logic from RecipeServiceImpl (or making it commonly accessible)
        List<RecipeDTO> dtos = favoriteRecipesPage.getContent().stream()
                .map(this::convertToRecipeDTO) // Using a local or imported conversion method
                .collect(Collectors.toList());
        
        return new PageImpl<>(dtos, pageable, favoriteRecipesPage.getTotalElements());
    }

    // Helper method to convert Recipe to RecipeDTO (similar to one in RecipeServiceImpl)
    // This can be moved to a common mapper/converter class if preferred
    private RecipeDTO convertToRecipeDTO(Recipe recipe) {
        if (recipe == null) return null;
        return new RecipeDTO(
                recipe.getId(),
                recipe.getName(),
                recipe.getDescription(),
                recipe.getIngredients() != null ? recipe.getIngredients().stream()
                        .map(ing -> new IngredientDTO(ing.getName(), ing.getQuantity(), ing.getUnit()))
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
}
