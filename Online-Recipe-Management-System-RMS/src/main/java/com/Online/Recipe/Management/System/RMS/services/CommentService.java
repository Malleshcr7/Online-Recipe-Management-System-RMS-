package com.Online.Recipe.Management.System.RMS.services;

import com.Online.Recipe.Management.System.RMS.dtos.CommentCreationRequest;
import com.Online.Recipe.Management.System.RMS.dtos.CommentDTO;
import java.util.List;

public interface CommentService {
    CommentDTO addComment(String recipeId, CommentCreationRequest request, String authenticatedUserEmail);
    List<CommentDTO> getCommentsByRecipeId(String recipeId);
    void deleteComment(String commentId, String authenticatedUserEmail);
}
