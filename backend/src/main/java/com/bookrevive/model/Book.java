package com.bookrevive.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "books")
public class Book {

    @Id
    private String id;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Author is required")
    private String author;

    @NotNull(message = "Price is required")
    private Double price;  // 0 for donations

    private String category;    // fiction, non-fiction, academic, science, history, etc.
    private String condition;   // new, good, fair, poor
    private String description;
    private String imageUrl;

    @NotBlank(message = "Type is required")
    private String type;        // SELL or DONATE

    private String userId;      // owner's user ID
    private String ownerName;
    private String ownerEmail;

    private Boolean available = true;
    private LocalDateTime listedAt;
    private LocalDateTime updatedAt;
    
    private Double averageRating = 0.0;
    private Integer reviewCount = 0;
    private String coverColor;
    private String emoji;
}
