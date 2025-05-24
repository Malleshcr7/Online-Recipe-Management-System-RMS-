package com.Online.Recipe.Management.System.RMS.services;

import com.Online.Recipe.Management.System.RMS.dtos.CategoryDTO;
import com.Online.Recipe.Management.System.RMS.dtos.DifficultyLevelDTO;
import java.util.List;

public interface ReferenceDataService {
    List<CategoryDTO> getAllCategories();
    List<DifficultyLevelDTO> getAllDifficultyLevels();
    List<String> getAllTags();
}
