package com.Online.Recipe.Management.System.RMS.controllers;

import com.Online.Recipe.Management.System.RMS.dtos.CategoryDTO;
import com.Online.Recipe.Management.System.RMS.dtos.DifficultyLevelDTO;
import com.Online.Recipe.Management.System.RMS.services.ReferenceDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ReferenceDataController {

    private final ReferenceDataService referenceDataService;

    @Autowired
    public ReferenceDataController(ReferenceDataService referenceDataService) {
        this.referenceDataService = referenceDataService;
    }

    @GetMapping("/categories")
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {
        List<CategoryDTO> categories = referenceDataService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/difficulty-levels")
    public ResponseEntity<List<DifficultyLevelDTO>> getAllDifficultyLevels() {
        List<DifficultyLevelDTO> difficultyLevels = referenceDataService.getAllDifficultyLevels();
        return ResponseEntity.ok(difficultyLevels);
    }

    @GetMapping("/tags")
    public ResponseEntity<List<String>> getAllTags() {
        List<String> tags = referenceDataService.getAllTags();
        return ResponseEntity.ok(tags);
    }
}
