package com.Online.Recipe.Management.System.RMS.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO {
    private String id;
    private String recipeId;
    private String userId;
    private String username;
    private String text;
    private Integer rating;
    private LocalDateTime createdAt;
}
