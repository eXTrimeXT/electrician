package com.extrime.electrician.model.telegram;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TelegramChannel {
    private String title;
    private String description;
    private String inviteLink;
    private Long subscribersCount;
}