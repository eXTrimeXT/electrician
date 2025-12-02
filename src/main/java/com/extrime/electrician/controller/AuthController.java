package com.extrime.electrician.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpSession;

@Controller
public class AuthController {

    // Константы для учетных данных
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin";
    private static final String SESSION_AUTH_KEY = "isAuthenticated";

    // Отображение страницы авторизации
    @GetMapping("/login")
    public String showLoginPage(Model model) {
        model.addAttribute("pageTitle", "Авторизация - Электрик");
        return "login";
    }

     // Обработка формы авторизации
    @PostMapping("/login")
    public String login(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            HttpSession session,
            Model model) {

        // Проверяем учетные данные
        if (ADMIN_USERNAME.equals(username) && ADMIN_PASSWORD.equals(password)) {
            // Устанавливаем флаг аутентификации в сессии
            session.setAttribute(SESSION_AUTH_KEY, true);
            session.setAttribute("username", username);

            // Перенаправляем на админ панель
            return "redirect:/admin";
        } else {
            // Если неверные данные, показываем ошибку
            model.addAttribute("error", "Неверный логин или пароль");
            model.addAttribute("pageTitle", "Авторизация - Электрик");
            return "login";
        }
    }

     // Проверка аутентификации (можно использовать как вспомогательный метод)
    private boolean isAuthenticated(HttpSession session) {
        return session.getAttribute(SESSION_AUTH_KEY) != null;
    }

     // Выход из системы
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}