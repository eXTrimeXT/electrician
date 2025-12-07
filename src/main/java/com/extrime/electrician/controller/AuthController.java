package com.extrime.electrician.controller;

import com.extrime.electrician.dao.UserDAO;
import com.extrime.electrician.model.User;
import com.extrime.electrician.service.PasswordService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.extrime.electrician.service.AuthService;

@Controller
public class AuthController {
//    private static final String ADMIN_USERNAME = "admin";
//    private static final String ADMIN_PASSWORD = "adminn";
    private static final String SESSION_AUTH_KEY = "isAuthenticated";
    private static final String SESSION_USER_KEY = "user";

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private PasswordService passwordService;

    @Autowired
    private AuthService authService;

    // Отображение страницы авторизации
    @GetMapping("/login")
    public String showLoginPage(Model model) {
        model.addAttribute("pageTitle", "Авторизация - Электрик");
        return "login";
    }

//    private User createAdminUser() {
//        User adminUser = new User();
//        adminUser.setId(0L);
//        adminUser.setUsername("admin");
//        adminUser.setRole("ADMIN");
//        adminUser.setActive(true);
//        return adminUser;
//    }

    // Обработка формы авторизации
    @PostMapping("/login")
    public String login(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            HttpSession session,
            Model model) {

        // Проверяем стандартные учетные данные админа
//        if (ADMIN_USERNAME.equals(username) && ADMIN_PASSWORD.equals(password)) {
//            session.setAttribute(SESSION_AUTH_KEY, true);
//            session.setAttribute(SESSION_USER_KEY, createAdminUser());
//            return "redirect:/admin";
//        }

        // Ищем пользователя в базе данных
        var userOptional = userDAO.findByUsername(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // Проверяем пароль
            if (passwordService.checkPassword(password, user.getPassword())) {
                session.setAttribute(SESSION_AUTH_KEY, true);
                session.setAttribute(SESSION_USER_KEY, user);
//                if (user.getRole().equals("ADMIN")) return "redirect:/admin";
                return "redirect:/profile";
            }
        }
        // Если неверные данные, показываем ошибку
        model.addAttribute("error", "Неверный логин или пароль");
        model.addAttribute("pageTitle", "Авторизация - Электрик");
        return "login";
    }

    @GetMapping("/profile")
    public String showProfile(HttpSession session, Model model) {
        if (!authService.isAuthenticated(session)) {
            return "redirect:/login";
        }
        User user = authService.getCurrentUser(session);
        model.addAttribute("user", user);
        return "profile";
    }

    // Выход из системы
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}