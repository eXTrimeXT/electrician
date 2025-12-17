package com.extrime.electrician.service.telegram;

import com.extrime.electrician.config.TelegramBotConfig;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Getter
@Service
@RequiredArgsConstructor
public class TelegramBotInit {
    @Autowired
    private TelegramBotConfig telegramBotConfig;
    private TelegramBot bot;

    @PostConstruct
    public void init(){
        this.bot = new TelegramBot(telegramBotConfig.getTELEGRAM_TOKEN());
        log.info("TG_BOT: инициализирован");
        log.info("TG_BOT: ID Владельца: {}", telegramBotConfig.getTELEGRAM_OWNER_ID());
        log.info("TG_BOT: ID Канала: {}", telegramBotConfig.getTELEGRAM_CHANNEL_ID());
    }

    /**
     * Отправка сообщения владельцу бота
     */
    public SendResponse SendOwnerMessage(String msg){
        SendMessage sendMessage = new SendMessage(telegramBotConfig.getTELEGRAM_OWNER_ID(), msg);
        return this.bot.execute(sendMessage);
    }
}
