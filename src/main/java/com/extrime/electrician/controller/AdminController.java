package com.extrime.electrician.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

    private static final String SESSION_AUTH_KEY = "isAuthenticated";

    // Показ админ панели
    @GetMapping("/admin")
    public String showAdminPanel(HttpSession session, Model model) {
        // Проверяем авторизацию
        if (session.getAttribute(SESSION_AUTH_KEY) == null) {
            return "redirect:/login";
        }

        model.addAttribute("pageTitle", "Админ панель - Управление услугами");
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
}