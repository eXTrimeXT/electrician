package com.extrime.electrician.controller;

import com.extrime.electrician.service.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
public class RegisterController {

    @Autowired
    private RegisterService registerService;

    // Отображение страницы регистрации
    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("pageTitle", "Регистрация - Электрик");
        return "register";
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

        // Очистка входных данных от лишних пробелов
        username = username != null ? username.trim() : "";
        email = email != null ? email.trim() : "";
        password = password != null ? password : "";
        confirmPassword = confirmPassword != null ? confirmPassword : "";

        // Вызываем сервис для обработки регистрации
        Map<String, Object> result = registerService.processRegistration(username, password, confirmPassword, email);

        if (result.containsKey("errors")) {
            // Если есть ошибки валидации
            @SuppressWarnings("unchecked")
            Map<String, String> errors = (Map<String, String>) result.get("errors");
            model.addAttribute("errors", errors);
            model.addAttribute("pageTitle", "Регистрация - Электрик");
            model.addAttribute("username", username);
            model.addAttribute("email", email);

            return "register";

        } else if (result.containsKey("requiresVerification")) {
            // Если требуется верификация email
            Long userId = (Long) result.get("userId");
            String userEmail = (String) result.get("email");
            String userName = (String) result.get("username");

            // Добавляем данные для модального окна
            model.addAttribute("requiresVerification", true);
            model.addAttribute("userId", userId);
            model.addAttribute("email", userEmail);
            model.addAttribute("emailMasked", maskEmail(userEmail));
            model.addAttribute("username", userName); // Добавляем username
            model.addAttribute("pageTitle", "Подтверждение email - Электрик");

            return "verification";

        } else if (result.containsKey("error")) {
            // Если произошла ошибка
            String errorMessage = (String) result.get("error");
            model.addAttribute("error", errorMessage);

            if (result.containsKey("errors")) {
                @SuppressWarnings("unchecked")
                Map<String, String> errors = (Map<String, String>) result.get("errors");
                model.addAttribute("errors", errors);
            }

            model.addAttribute("pageTitle", "Регистрация - Электрик");
            model.addAttribute("username", username);
            model.addAttribute("email", email);
            return "register";
        }

        // Если что-то пошло не так
        model.addAttribute("error", "Произошла непредвиденная ошибка. Пожалуйста, попробуйте позже.");
        model.addAttribute("pageTitle", "Регистрация - Электрик");
        model.addAttribute("username", username);
        model.addAttribute("email", email);
        return "register";
    }

    // Подтверждение email (обработка AJAX запроса)
    @PostMapping("/register/verify")
    @ResponseBody
    public Map<String, Object> verifyEmail(
            @RequestParam("userId") Long userId,
            @RequestParam("email") String email,
            @RequestParam("code") String code) {
        return registerService.confirmEmail(userId, email, code);
    }

    // Повторная отправка кода (обработка AJAX запроса)
    @PostMapping("/register/resend")
    @ResponseBody
    public Map<String, Object> resendCode(@RequestParam("userId") Long userId, @RequestParam("email") String email) {
        return registerService.resendVerificationCode(userId, email);
    }

    /**
     * Маскирование email для отображения
     */
    private String maskEmail(String email) {
        if (email == null || email.length() < 5) {
            return email;
        }

        String[] parts = email.split("@");
        if (parts.length != 2) {
            return email;
        }

        String username = parts[0];
        String domain = parts[1];

        if (username.length() <= 2) {
            return "*".repeat(username.length()) + "@" + domain;
        }

        String maskedUsername = username.charAt(0) +
                "*".repeat(username.length() - 2) +
                username.charAt(username.length() - 1);

        return maskedUsername + "@" + domain;
    }
}