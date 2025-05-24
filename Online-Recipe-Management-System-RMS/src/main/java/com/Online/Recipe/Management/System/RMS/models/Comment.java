package com.Online.Recipe.Management.System.RMS.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "comments")
public class Comment {

    @Id
    private String id;

    @Indexed
    private String recipeId;

    @Indexed
    private String userId;

    private String username; // Denormalized for easier display

    private String text;

    private Integer rating; // e.g., 1-5 stars

    @CreatedDate
    private LocalDateTime createdAt;
}
