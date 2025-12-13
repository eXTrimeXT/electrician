package com.extrime.electrician.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
public class TelegramBotConfig {
    @Value("${telegram.TOKEN}")
    private String TELEGRAM_TOKEN;

    @Value("${telegram.OWNER_ID}")
    private String TELEGRAM_OWNER_ID;

    @Value("${telegram.CHANNEL_ID}")
    private String TELEGRAM_CHANNEL_ID;
}


