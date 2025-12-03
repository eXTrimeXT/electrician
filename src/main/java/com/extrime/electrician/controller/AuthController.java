package com.extrime.electrician.controller;

import com.extrime.electrician.dao.UserDAO;
import com.extrime.electrician.model.ContactInfo;
import com.extrime.electrician.model.User;
import com.extrime.electrician.service.PasswordService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Controller
public class AuthController {
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin";
    private static final String SESSION_AUTH_KEY = "isAuthenticated";
    private static final String SESSION_USER_KEY = "user";

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private PasswordService passwordService;

    // Регулярные выражения для валидации
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,30}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    // Отображение страницы авторизации
    @GetMapping("/login")
    public String showLoginPage(Model model) {
        model.addAttribute("pageTitle", "Авторизация - Электрик");
        return "login";
    }

    // Отображение страницы регистрации
    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("pageTitle", "Регистрация - Электрик");
        return "register";
    }


    private User createAdminUser() {
        User adminUser = new User();
        adminUser.setId(0L);
        adminUser.setUsername("admin");
        adminUser.setRole("ADMIN");
        adminUser.setActive(true);
        return adminUser;
    }

    // Обработка формы авторизации
    @PostMapping("/login")
    public String login(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            HttpSession session,
            Model model) {

        // Проверяем стандартные учетные данные админа
        if (ADMIN_USERNAME.equals(username) && ADMIN_PASSWORD.equals(password)) {
            session.setAttribute(SESSION_AUTH_KEY, true);
            session.setAttribute(SESSION_USER_KEY, createAdminUser());
            return "redirect:/admin";
        }

        // Ищем пользователя в базе данных
        var userOptional = userDAO.findByUsername(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // Проверяем пароль
            if (passwordService.checkPassword(password, user.getPassword())){
                session.setAttribute(SESSION_AUTH_KEY, true);
                session.setAttribute(SESSION_USER_KEY, user);
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
        if (!isAuthenticated(session)) {
            return "redirect:/login";
        }

        User user = getCurrentUser(session);
        model.addAttribute("user", user);
        return "profile";
    }

    // Обработка формы регистрации
    @PostMapping("/register")
    public String register(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            @RequestParam("confirmPassword") String confirmPassword,
            @RequestParam("email") String email,
            Model model,
            RedirectAttributes redirectAttributes) {

        Map<String, String> errors = new HashMap<>();

        // Валидация данных
        validateRegistration(username, password, confirmPassword, email, errors);

        if (!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            model.addAttribute("pageTitle", "Регистрация - Электрик");
            model.addAttribute("username", username);
            model.addAttribute("email", email);
            return "register";
        }

        try {
            // Создаем нового пользователя
            User newUser = new User();
            newUser.setUsername(username);

            // Хешируем пароль
            String hashedPassword = passwordService.hashPassword(password);

            newUser.setPassword(hashedPassword);
            newUser.setEmail(email);
            newUser.setRole("USER"); // По умолчанию обычный пользователь
            newUser.setActive(true);

            Long userId = userDAO.createUser(newUser);

            // Добавляем сообщение об успешной регистрации
            redirectAttributes.addFlashAttribute("successMessage",
                    "Регистрация прошла успешно! Теперь вы можете войти в систему.");

            return "redirect:/login";

        } catch (Exception e) {
            e.printStackTrace(); // Для отладки
            model.addAttribute("error", "Ошибка при регистрации: " + e.getMessage());
            model.addAttribute("pageTitle", "Регистрация - Электрик");
            return "register";
        }
    }

    // Проверка аутентификации
    public boolean isAuthenticated(HttpSession session) {
        return session.getAttribute(SESSION_AUTH_KEY) != null;
    }

    // Получить текущего пользователя
    public User getCurrentUser(HttpSession session) {
        return (User) session.getAttribute(SESSION_USER_KEY);
    }

    // Выход из системы
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    // Вспомогательные методы

    private void validateRegistration(String username, String password,
                                      String confirmPassword, String email,
                                      Map<String, String> errors) {

        // Проверка имени пользователя
        if (!USERNAME_PATTERN.matcher(username).matches()) {
            errors.put("username", "Логин должен содержать 3-30 символов (буквы, цифры, подчеркивания)");
        }

        if (userDAO.existsByUsername(username)) {
            errors.put("username", "Пользователь с таким логином уже существует");
        }

        // Проверка пароля
        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            errors.put("password",
                    "Пароль должен содержать минимум 8 символов, включая цифры, строчные и заглавные буквы");
        }

        if (!password.equals(confirmPassword)) {
            errors.put("confirmPassword", "Пароли не совпадают");
        }

        // Проверка email
        if (email != null && !email.isEmpty()) {
            if (!EMAIL_PATTERN.matcher(email).matches()) {
                errors.put("email", "Введите корректный email адрес");
            }

            if (userDAO.existsByEmail(email)) {
                errors.put("email", "Пользователь с таким email уже существует");
            }
        }
    }
}