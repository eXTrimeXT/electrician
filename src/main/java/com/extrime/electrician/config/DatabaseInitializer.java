package com.extrime.electrician.config;

import com.extrime.electrician.dao.*;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Slf4j
@Configuration
public class DatabaseInitializer {

    @Autowired
    private ServiceDAO serviceDAO;

    @Autowired
    private WorkDAO workDAO;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private EmailVerificationDAO emailVerificationDAO;

    @Autowired
    private ReviewDAO reviewDAO;

    @Autowired
    private TelegramPostDAO telegramPostDAO;

    @PostConstruct
    @Profile("!test") // Не выполняем в тестах
    public void init() {
        try {
            serviceDAO.createTableIfNotExists();
            log.info("✅ База данных SERVICES инициализирована успешно!");
        }catch (Exception e) { log.error("❌ Ошибка при инициализации базы данных SERVICES: {}", e.getMessage());}

        try{
            workDAO.createTableIfNotExists();
            log.info("✅ База данных WORKS инициализирована успешно!");
        }catch (Exception e) { log.error("❌ Ошибка при инициализации базы данных WORKS: {}", e.getMessage());}

        try {
            userDAO.createTableIfNotExists();
            log.info("✅ База данных USERS инициализирована успешно!");
        }catch (Exception e) { log.error("❌ Ошибка при инициализации базы данных USERS: {}", e.getMessage());}

        try{
            emailVerificationDAO.createTableIfNotExists();
            log.info("✅ База данных EMAIL инициализирована успешно!");
        }catch (Exception e) { log.error("❌ Ошибка при инициализации базы данных EMAIL: {}", e.getMessage());}

        try{
            reviewDAO.createTableIfNotExists();
            log.info("✅ База данных REVIEWS инициализирована успешно!");
        }catch (Exception e) { log.error("❌ Ошибка при инициализации базы данных REVIEWS: {}", e.getMessage());}

        try{
            telegramPostDAO.createTableIfNotExists();
            log.info("✅ База данных TELEGRAM_POSTS инициализирована успешно!");
        }catch (Exception e) { log.error("❌ Ошибка при инициализации базы данных TELEGRAM_POSTS: {}", e.getMessage());}
    }
}