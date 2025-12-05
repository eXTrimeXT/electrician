package com.extrime.electrician.config;

import com.extrime.electrician.dao.ReviewDAO;
import com.extrime.electrician.dao.UserDAO;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class DatabaseInitializer {
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private ReviewDAO reviewDAO;

    @PostConstruct
    @Profile("!test") // Не выполняем в тестах
    public void init() {
        try {
            userDAO.createTableIfNotExists();
            System.out.println("✅ Пользователи созданы/проверены");
        } catch (Exception e) {
            System.err.println("❌ Ошибка при инициализации базы данных: " + e.getMessage());
        }
        try {
            reviewDAO.createTableIfNotExists();
            System.out.println("✅ ОТЗЫВЫ созданы/проверены");
        } catch (Exception e){
            System.err.println("❌ Ошибка при инициализации базы данных: " + e.getMessage());
        }
    }
}
