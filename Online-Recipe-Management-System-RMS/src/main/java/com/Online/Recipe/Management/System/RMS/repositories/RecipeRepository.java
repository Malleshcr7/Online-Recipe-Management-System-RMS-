package com.Online.Recipe.Management.System.RMS.repositories;

import com.Online.Recipe.Management.System.RMS.models.Recipe;
import org.springframework.data.mongodb.repository.MongoRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Collection;

public interface RecipeRepository extends MongoRepository<Recipe, String> {
    // save() method is inherited from MongoRepository
    Page<Recipe> findAllByIdIn(Collection<String> ids, Pageable pageable);
}
