package com.Online.Recipe.Management.System.RMS.services.impl;

import com.Online.Recipe.Management.System.RMS.dtos.CommentCreationRequest;
import com.Online.Recipe.Management.System.RMS.dtos.CommentDTO;
import com.Online.Recipe.Management.System.RMS.exceptions.ResourceNotFoundException;
import com.Online.Recipe.Management.System.RMS.models.Comment;
import com.Online.Recipe.Management.System.RMS.models.User;
import com.Online.Recipe.Management.System.RMS.repositories.CommentRepository;
import com.Online.Recipe.Management.System.RMS.repositories.RecipeRepository;
import com.Online.Recipe.Management.System.RMS.repositories.UserRepository;
import com.Online.Recipe.Management.System.RMS.services.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository,
                              RecipeRepository recipeRepository,
                              UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.recipeRepository = recipeRepository;
        this.userRepository = userRepository;
    }

    @Override
    public CommentDTO addComment(String recipeId, CommentCreationRequest request, String authenticatedUserEmail) {
        User user = userRepository.findByEmail(authenticatedUserEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + authenticatedUserEmail));

        if (!recipeRepository.existsById(recipeId)) {
            throw new ResourceNotFoundException("Recipe not found with id: " + recipeId);
        }

        Comment comment = new Comment();
        comment.setRecipeId(recipeId);
        comment.setUserId(user.getId());
        comment.setUsername(user.getUsername()); // Denormalized username
        comment.setText(request.getText());
        comment.setRating(request.getRating());
        // createdAt will be set by Mongo Auditing

        Comment savedComment = commentRepository.save(comment);
        return convertToDTO(savedComment);
    }

    @Override
    public List<CommentDTO> getCommentsByRecipeId(String recipeId) {
        if (!recipeRepository.existsById(recipeId)) {
            // Or return empty list, depending on desired behavior for non-existent recipes
            throw new ResourceNotFoundException("Recipe not found with id: " + recipeId); 
        }
        List<Comment> comments = commentRepository.findByRecipeIdOrderByCreatedAtDesc(recipeId);
        return comments.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public void deleteComment(String commentId, String authenticatedUserEmail) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + commentId));

        User authenticatedUser = userRepository.findByEmail(authenticatedUserEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + authenticatedUserEmail));

        if (!comment.getUserId().equals(authenticatedUser.getId())) {
            throw new AccessDeniedException("Users can only delete their own comments.");
        }

        commentRepository.deleteById(commentId);
    }

    private CommentDTO convertToDTO(Comment comment) {
        if (comment == null) return null;
        return new CommentDTO(
                comment.getId(),
                comment.getRecipeId(),
                comment.getUserId(),
                comment.getUsername(),
                comment.getText(),
                comment.getRating(),
                comment.getCreatedAt()
        );
    }
}
