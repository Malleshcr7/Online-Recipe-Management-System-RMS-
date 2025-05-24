package com.Online.Recipe.Management.System.RMS.services.impl;

import com.Online.Recipe.Management.System.RMS.dtos.CategoryDTO;
import com.Online.Recipe.Management.System.RMS.dtos.DifficultyLevelDTO;
import com.Online.Recipe.Management.System.RMS.services.ReferenceDataService;
import com.Online.Recipe.Management.System.RMS.models.Recipe;
import com.Online.Recipe.Management.System.RMS.repositories.RecipeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ReferenceDataServiceImpl implements ReferenceDataService {

    private final RecipeRepository recipeRepository;

    @Autowired
    public ReferenceDataServiceImpl(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    @Override
    public List<CategoryDTO> getAllCategories() {
        return Arrays.asList(
                new CategoryDTO("Breakfast"),
                new CategoryDTO("Lunch"),
                new CategoryDTO("Dinner"),
                new CategoryDTO("Dessert"),
                new CategoryDTO("Appetizer"),
                new CategoryDTO("Snack"),
                new CategoryDTO("Drink"),
                new CategoryDTO("Soup"),
                new CategoryDTO("Salad"),
                new CategoryDTO("Main Course")
        );
    }

    @Override
    public List<DifficultyLevelDTO> getAllDifficultyLevels() {
        return Arrays.asList(
                new DifficultyLevelDTO("Easy"),
                new DifficultyLevelDTO("Medium"),
                new DifficultyLevelDTO("Hard")
        );
    }

    @Override
    public List<String> getAllTags() {
        List<Recipe> recipes = recipeRepository.findAll();
        return recipes.stream()
                      .flatMap(recipe -> recipe.getTags() == null ? Stream.empty() : recipe.getTags().stream())
                      .filter(tag -> tag != null && !tag.trim().isEmpty())
                      .map(String::toLowerCase)
                      .distinct()
                      .sorted()
                      .collect(Collectors.toList());
    }
}
