package com.extrime.electrician.service;

import com.extrime.electrician.model.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private static final String SESSION_AUTH_KEY = "isAuthenticated";
    private static final String SESSION_USER_KEY = "user";

    // Проверка аутентификации
    public boolean isAuthenticated(HttpSession session) {
        return session.getAttribute(SESSION_AUTH_KEY) != null;
    }

    // Получить текущего пользователя
    public User getCurrentUser(HttpSession session) {
        return (User) session.getAttribute(SESSION_USER_KEY);
    }

    // Установить пользователя в сессию
    public void setAuthenticated(HttpSession session, User user) {
        session.setAttribute(SESSION_AUTH_KEY, true);
        session.setAttribute(SESSION_USER_KEY, user);
    }

    // Получить роль текущего пользователя
    public String getCurrentUserRole(HttpSession session) {
        User user = getCurrentUser(session);
        return user != null ? user.getRole() : null;
    }

    // Проверить, является ли пользователь админом
    public boolean isAdmin(HttpSession session) {
        User user = (User) session.getAttribute("user");
        return user != null && "ADMIN".equals(user.getRole());
    }
}