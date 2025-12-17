package com.extrime.electrician.service.telegram;

import com.extrime.electrician.config.Config;
import com.extrime.electrician.model.Review;
import com.extrime.electrician.service.ReviewService;
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
public class TelegramNotifyReviews {
    @Autowired
    private Config config;

    @Autowired
    private TelegramBotInit telegramBotInit;

    private final ReviewService reviewService;
    private final AtomicReference<LocalDateTime> lastReviewTime = new AtomicReference<>(LocalDateTime.now());
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    @PostConstruct
    public void init() {
        try {
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
    @Scheduled(fixedDelay = 60000) // 60 секунд
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
        String stars = "⭐".repeat(review.getRating()) + "☆".repeat(5 - review.getRating());

        // Формирование сообщения о новом отзыве
        String message = String.format("""
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

        // Отправка владельцу
        try {
            SendResponse response = telegramBotInit.SendOwnerMessage(message);

            if (response.isOk()) {
                log.info("Уведомление о новом отзыве отправлено владельцу");
            } else {
                log.error("Ошибка отправки уведомления: {}", response.description());
            }
        } catch (Exception e) {
            log.error("Ошибка отправки сообщения в Telegram", e);
        }
    }
}