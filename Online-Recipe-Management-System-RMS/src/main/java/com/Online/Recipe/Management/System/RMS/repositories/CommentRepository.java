package com.Online.Recipe.Management.System.RMS.repositories;

import com.Online.Recipe.Management.System.RMS.models.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface CommentRepository extends MongoRepository<Comment, String> {
    List<Comment> findByRecipeIdOrderByCreatedAtDesc(String recipeId);
}
