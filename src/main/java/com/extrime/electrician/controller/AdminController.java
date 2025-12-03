package com.extrime.electrician.controller;

import com.extrime.electrician.model.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

    private static final String SESSION_AUTH_KEY = "isAuthenticated";

    // Показ админ панели
    @GetMapping("/admin")
    public String adminPanel(HttpSession session, Model model) {
        // Проверяем авторизацию
        Object auth = session.getAttribute("isAuthenticated");
        if (auth == null || !(Boolean) auth) {
            return "redirect:/login";
        }

        // Проверяем роль пользователя
        User user = (User) session.getAttribute("user");
        if (user == null || !"ADMIN".equals(user.getRole())) {
            // Если пользователь не админ, перенаправляем на профиль
            return "redirect:/profile";
        }

        model.addAttribute("pageTitle", "Админ панель - Управление услугами и работами");
        return "admin";
    }

     // Защищенный маршрут для получения данных (например, через REST)
    @GetMapping("/admin/data")
    public String getAdminData(HttpSession session, Model model) {
        // Проверяем авторизацию
        if (session.getAttribute(SESSION_AUTH_KEY) == null) {
            return "redirect:/login";
        }
        // Логика получения данных для админ панели
        return "admin_data";
    }

    // Добавьте эту проверку в каждый метод админ-контроллера
    private boolean isAdmin(HttpSession session) {
        User user = (User) session.getAttribute("user");
        return user != null && "ADMIN".equals(user.getRole());
    }
}