package com.extrime.electrician.model.telegram;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class TelegramPost {
    private Long id;
    private Long postId;
    private String postText;
    private LocalDateTime createdAt;
    private Boolean isEdited = false;
}