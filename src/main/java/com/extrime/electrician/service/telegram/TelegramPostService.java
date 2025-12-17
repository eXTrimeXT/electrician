package com.extrime.electrician.service.telegram;

import com.extrime.electrician.config.TelegramBotConfig;
import com.extrime.electrician.dao.TelegramPostDAO;
import com.extrime.electrician.model.telegram.TelegramPost;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.GetUpdates;
import com.pengrad.telegrambot.response.GetUpdatesResponse;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
public class TelegramPostService {
    @Autowired
    private TelegramBotInit telegramBotInit;

    @Autowired
    private TelegramBotConfig telegramBotConfig;

    @Autowired
    private TelegramPostDAO telegramPostDAO;

    private TelegramBot bot;

    @PostConstruct
    public void init() {
        this.bot = telegramBotInit.getBot();
    }

    /**
     * Получение постов из канала
     */
    @Scheduled(fixedDelay = 60000)  // Проверка каждую минуту
    public TelegramPost getPostInfo() {
        try {
            GetUpdates getUpdates = new GetUpdates().limit(1).offset(-1).timeout(0);
            GetUpdatesResponse getUpdatesResponse = bot.execute(getUpdates);
            List<Update> updates = getUpdatesResponse.updates();

            if (updates == null || updates.isEmpty()) {
                return null;
            }

            Update update = updates.getFirst();
            Message updatePost = update.channelPost() != null ?
                    update.channelPost() : update.editedChannelPost();

            if (updatePost == null) {
                return null;
            }

            Long postId = Long.valueOf(updatePost.messageId());
            String postText = updatePost.text() != null ? updatePost.text() : updatePost.caption();
            Integer dateTimestamp = updatePost.date();

            // Проверяем существующую запись
            TelegramPost existingPost = telegramPostDAO.getPostByPostId(postId);

            // Если записи нет - создаем новую
            if (existingPost == null) {
                TelegramPost newPost = new TelegramPost();
                newPost.setPostId(postId);
                newPost.setPostText(postText);

                if (dateTimestamp != null && dateTimestamp > 0) {
                    Instant instant = Instant.ofEpochSecond(dateTimestamp);
                    LocalDateTime date = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
                    newPost.setCreatedAt(date);
                } else {
                    newPost.setCreatedAt(LocalDateTime.now());
                }

                Long newId = telegramPostDAO.addPost(newPost);
                newPost.setId(newId);
                log.info("Добавлена новая запись с postId: {}", postId);
                return newPost;
            }

            // Если запись есть, но текст изменился - обновляем
            if (!existingPost.getPostText().equals(postText)) {
                telegramPostDAO.updatePostText(postId, postText);
                log.info("Обновлен текст для postId: {}", postId);

                // Получаем обновленную запись
                return telegramPostDAO.getPostByPostId(postId);
            }

            // Если ничего не изменилось - возвращаем существующую запись
            log.debug("Изменений для postId: {} не обнаружено", postId);
            return existingPost;

        } catch (Exception e) {
            log.error("Ошибка в getPostInfo: {}", e.getMessage(), e);
            return null;
        }
    }
}
