package com.Online.Recipe.Management.System.RMS.controllers;

import com.Online.Recipe.Management.System.RMS.dtos.CommentCreationRequest;
import com.Online.Recipe.Management.System.RMS.dtos.CommentDTO;
import com.Online.Recipe.Management.System.RMS.services.CommentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CommentController {

    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/recipes/{recipeId}/comments")
    public ResponseEntity<CommentDTO> addComment(@PathVariable String recipeId,
                                                 @Valid @RequestBody CommentCreationRequest request,
                                                 Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String authenticatedUserEmail = getEmailFromAuthentication(authentication);
        CommentDTO commentDTO = commentService.addComment(recipeId, request, authenticatedUserEmail);
        return ResponseEntity.status(HttpStatus.CREATED).body(commentDTO);
    }

    @GetMapping("/recipes/{recipeId}/comments")
    public ResponseEntity<List<CommentDTO>> getCommentsByRecipeId(@PathVariable String recipeId) {
        List<CommentDTO> comments = commentService.getCommentsByRecipeId(recipeId);
        return ResponseEntity.ok(comments);
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable String commentId,
                                              Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String authenticatedUserEmail = getEmailFromAuthentication(authentication);
        commentService.deleteComment(commentId, authenticatedUserEmail);
        return ResponseEntity.noContent().build();
    }

    private String getEmailFromAuthentication(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else if (principal instanceof String) {
            return (String) principal;
        }
        // Should not happen with standard Spring Security setup if user is authenticated
        throw new IllegalStateException("Authenticated principal is not of expected type (UserDetails or String).");
    }
}
