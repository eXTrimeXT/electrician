package com.extrime.electrician.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class EmailVerification {
    private Long id;
    private String email;
    private String verificationCode;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private boolean used;
    private Long userId;
}
