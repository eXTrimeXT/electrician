package com.extrime.electrician.model;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

// Класс для хранения информации о токене
@Setter
@Getter
public class PasswordResetToken {
    private final String email;
    private final String token;
    private final LocalDateTime expiresAt;
    private boolean used;

    public PasswordResetToken(String email, String token, LocalDateTime expiresAt) {
        this.email = email;
        this.token = token;
        this.expiresAt = expiresAt;
        this.used = false;
    }

    public boolean isValid() {
        return !used && expiresAt.isAfter(LocalDateTime.now());
    }
}
