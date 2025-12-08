package com.extrime.electrician.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class Review {
    // Getters and Setters
    private Long id;
    private Long userId;
    private String username;
    private Integer rating;
    private String comment;
    private String adminResponse;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean active;

    // Constructors
    public Review() {}

    public Review(Long userId, String username, Integer rating, String comment) {
        this.userId = userId;
        this.username = username;
        this.rating = rating;
        this.comment = comment;
        this.active = true;
    }
}