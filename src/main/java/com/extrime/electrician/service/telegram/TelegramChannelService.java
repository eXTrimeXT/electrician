package com.extrime.electrician.service.telegram;

import com.extrime.electrician.config.TelegramBotConfig;
import com.extrime.electrician.model.telegram.TelegramChannel;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.ChatFullInfo;
import com.pengrad.telegrambot.request.GetChat;
import com.pengrad.telegrambot.request.GetChatMemberCount;
import com.pengrad.telegrambot.response.GetChatMemberCountResponse;
import com.pengrad.telegrambot.response.GetChatResponse;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramChannelService {
    @Autowired
    private TelegramBotInit telegramBotInit;

    @Autowired
    private TelegramBotConfig telegramBotConfig;

    private TelegramBot bot;

    @PostConstruct
    public void init() {
        this.bot = telegramBotInit.getBot();
    }

    /**
     * Получение информации о канале
     */
    @Scheduled(fixedDelay = 604800) // Проверка информации канал каждую неделю
    public TelegramChannel getChannelInfo() {
        TelegramChannel channel = new TelegramChannel();

        try {
            String channelId = telegramBotConfig.getTELEGRAM_CHANNEL_ID();
            GetChat getChat = new GetChat(channelId);
            GetChatResponse response = bot.execute(getChat);

            if (response.isOk()) {
                ChatFullInfo chat = response.chat();
                channel.setTitle(chat.title());
                channel.setDescription(chat.description());
                channel.setInviteLink(chat.inviteLink());

                // Получение количества подписчиков
                GetChatMemberCount requestMemberCount = new GetChatMemberCount(chat.id());
                GetChatMemberCountResponse memberCount = bot.execute(requestMemberCount);
                channel.setSubscribersCount((long) memberCount.count());
                log.info("Информация о канале успешно получена: {}", channel.getTitle());
            } else {
                log.warn("Не удалось получить информацию о канале: {}", response.description());
            }
        } catch (Exception e) {
            log.error("Ошибка при получении информации о канале", e);
        }
        return channel;
    }
}