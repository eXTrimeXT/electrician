package com.extrime.electrician.config;

import com.extrime.electrician.dao.UserDAO;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class DatabaseInitializer {
    @Autowired
    private UserDAO userDAO;

    @PostConstruct
    @Profile("!test") // Не выполняем в тестах
    public void init() {
        try {
            userDAO.createTableIfNotExists();
            System.out.println("✅ Таблица пользователей создана/проверена");
        } catch (Exception e) {
            System.err.println("❌ Ошибка при инициализации базы данных: " + e.getMessage());
        }
    }
}
