package com.extrime.electrician.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class User {
    // Геттеры и сеттеры
    private Long id;
    private String username;
    private String password;
    private String email;
    private String role; // ADMIN, USER
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean active;

    // Конструкторы
    public User() {}

    public User(Long id, String username, String password, String email, String role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.active = true;
    }
}