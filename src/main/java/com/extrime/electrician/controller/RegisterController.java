package com.extrime.electrician.controller;

import com.extrime.electrician.service.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
        Map<String, Object> result = registerService.processRegistration(
                username, password, confirmPassword, email);

        if (result.containsKey("errors")) {
            // Если есть ошибки валидации
            @SuppressWarnings("unchecked")
            Map<String, String> errors = (Map<String, String>) result.get("errors");
            model.addAttribute("errors", errors);
            model.addAttribute("pageTitle", "Регистрация - Электрик");
            model.addAttribute("username", username);
            model.addAttribute("email", email);
            return "register";
        } else if (result.containsKey("success")) {
            // Если регистрация успешна
            boolean success = (boolean) result.get("success");
            if (success) {
                redirectAttributes.addFlashAttribute("successMessage",
                        "Регистрация прошла успешно! Теперь вы можете войти в систему.");
                return "redirect:/login";
            }
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
}