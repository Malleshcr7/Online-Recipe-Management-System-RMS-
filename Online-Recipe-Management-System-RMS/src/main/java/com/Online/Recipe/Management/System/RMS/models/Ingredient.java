package com.Online.Recipe.Management.System.RMS.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ingredient {
    private String name;
    private String quantity;
    private String unit; // e.g., "cups", "tbsp", "grams", can be optional
}
