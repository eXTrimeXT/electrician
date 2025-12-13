package com.extrime.electrician.service;

import com.extrime.electrician.config.Config;
import com.extrime.electrician.config.TelegramBotConfig;
import com.extrime.electrician.model.Review;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramBotService {
    @Autowired
    private Config config;

    private final TelegramBotConfig telegramBotConfig;
    private final ReviewService reviewService;

    private TelegramBot bot;
    private final AtomicReference<LocalDateTime> lastReviewTime = new AtomicReference<>(LocalDateTime.now());
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    @PostConstruct
    public void init() {
        try {
            this.bot = new TelegramBot(telegramBotConfig.getTELEGRAM_TOKEN());
            log.info("Telegram бот инициализирован для владельца: {}", telegramBotConfig.getTELEGRAM_OWNER_ID());

            // При старте получаем время последнего отзыва
            LocalDateTime latestReviewTime = reviewService.getLatestReviewTime();
            if (latestReviewTime != null) {
                lastReviewTime.set(latestReviewTime);
                log.info("Время последнего отзова установлено: {}", latestReviewTime);
            }
        } catch (Exception e) {
            log.error("Ошибка инициализации Telegram бота", e);
        }
    }

    /**
     * Проверка новых отзывов каждые 30 секунд
     */
    @Scheduled(fixedDelay = 30000) // 30 секунд
    public void checkNewReviews() {
        try {
            LocalDateTime currentLastTime = lastReviewTime.get();
            LocalDateTime newLastTime = reviewService.getLatestReviewTime();

            if (newLastTime != null && newLastTime.isAfter(currentLastTime)) {
                // Найден новый отзыв
                var newReview = reviewService.getLatestReview();
                if (newReview != null) {
                    sendNewReviewNotification(newReview);
                    lastReviewTime.set(newLastTime);
                    log.info("Обнаружен новый отзыв от {}", newReview.getCreatedAt());
                }
            }
        } catch (Exception e) {
            log.error("Ошибка при проверке новых отзывов", e);
        }
    }

    /**
     * Отправка уведомления о новом отзыве
     */
    private void sendNewReviewNotification(Review review) {
        String message = buildNotificationMessage(review);

        try {
            // Отправка владельцу
            SendMessage sendMessage = new SendMessage(telegramBotConfig.getTELEGRAM_OWNER_ID(), message);
            SendResponse response = bot.execute(sendMessage);

            if (response.isOk()) {
                log.info("Уведомление о новом отзыве отправлено владельцу");
            } else {
                log.error("Ошибка отправки уведомления: {}", response.description());
            }
        } catch (Exception e) {
            log.error("Ошибка отправки сообщения в Telegram", e);
        }
    }

    /**
     * Формирование сообщения об отзыве
     */
    private String buildNotificationMessage(Review review) {
        String stars = "⭐".repeat(review.getRating()) + "☆".repeat(5 - review.getRating());

        return String.format("""
                🔔 Новый отзыв на сайте!
                
                👤 Пользователь: %s
                ⭐ Рейтинг: %d/5 %s
                
                💬 Комментарий:
                %s
                
                📅 Дата: %s
                
                %sreviews
                """,
                review.getUsername(),
                review.getRating(),
                stars,
                review.getComment(),
                review.getCreatedAt().format(formatter),
                config.getDOMAIN()
        );
    }

    /**
     * Метод для ручной отправки сообщений
     */
    public void sendMessage(String text) {
        try {
            SendMessage message = new SendMessage(telegramBotConfig.getTELEGRAM_OWNER_ID(), text);
            SendResponse response = bot.execute(message);

            if (!response.isOk()) {
                log.error("Ошибка отправки сообщения: {}", response.description());
            }
        } catch (Exception e) {
            log.error("Ошибка отправки сообщения", e);
        }
    }
}