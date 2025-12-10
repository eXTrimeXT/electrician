package com.extrime.electrician.config;

import com.extrime.electrician.dao.*;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.nio.channels.WritePendingException;

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

    @PostConstruct
    @Profile("!test") // Не выполняем в тестах
    public void init() {
        try {
            serviceDAO.createTableIfNotExists();
            System.out.println("✅ База данных SERVICES инициализирована успешно!");
            workDAO.createTableIfNotExists();
            System.out.println("✅ База данных WORKS инициализирована успешно!");
            userDAO.createTableIfNotExists();
            System.out.println("✅ База данных USERS инициализирована успешно!");
            emailVerificationDAO.createTableIfNotExists();
            System.out.println("✅ База данных EMAIL инициализирована успешно!");
            reviewDAO.createTableIfNotExists();
            System.out.println("✅ База данных REVIEWS инициализирована успешно!");
        } catch (Exception e) {
            System.err.println("❌ Ошибка при инициализации базы данных: " + e.getMessage());
        }
    }
}