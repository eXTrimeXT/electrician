package com.extrime.electrician.controller;

import com.extrime.electrician.dao.UserDAO;
import com.extrime.electrician.model.PasswordResetToken;
import com.extrime.electrician.model.User;
import com.extrime.electrician.service.PasswordService;
import com.extrime.electrician.service.email.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Controller
@RequestMapping("/password")
public class PasswordResetController {

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private PasswordService passwordService;

    @Autowired
    private EmailService emailService;

    // Хранилище токенов сброса пароля (в production используйте БД или Redis)
    private final Map<String, PasswordResetToken> resetTokens = new ConcurrentHashMap<>();

    /**
     * Страница для ввода email для сброса пароля
     */
    @GetMapping("/forgot")
    public String showForgotPasswordPage(Model model) {
        model.addAttribute("pageTitle", "Восстановление пароля - Электрик");
        return "password-forgot";
    }

    /**
     * Обработка запроса на сброс пароля
     */
    @PostMapping("/forgot")
    public String processForgotPassword(
            @RequestParam("email") String email,
            Model model) {

        try {
            // Ищем пользователя по email
            var userOptional = userDAO.findByEmail(email);
            if (userOptional.isEmpty()) {
                // Для безопасности не сообщаем, что email не найден
                model.addAttribute("successMessage",
                        "Если указанный email существует в системе, на него будет отправлена инструкция по восстановлению пароля.");
                return "password-forgot";
            }

            User user = userOptional.get();

            // Генерируем токен
            String token = UUID.randomUUID().toString();
            LocalDateTime expiresAt = LocalDateTime.now().plusHours(24); // Токен действителен 24 часа

            // Сохраняем токен
            resetTokens.put(token, new PasswordResetToken(email, token, expiresAt));

            // Отправляем email с ссылкой для сброса
            sendResetPasswordEmail(email, token, user.getUsername());

            model.addAttribute("successMessage",
                    "На ваш email отправлена инструкция по восстановлению пароля. " +
                            "Проверьте вашу почту (включая папку Спам).");

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error",
                    "Произошла ошибка при обработке запроса. Пожалуйста, попробуйте позже.");
        }

        return "password-forgot";
    }

    /**
     * Страница для ввода нового пароля
     */
    @GetMapping("/reset")
    public String showResetPasswordPage(
            @RequestParam("token") String token,
            Model model) {

        PasswordResetToken resetToken = resetTokens.get(token);

        if (resetToken == null || !resetToken.isValid()) {
            model.addAttribute("error",
                    "Ссылка для восстановления пароля недействительна или истек срок её действия.");
            return "password-error";
        }

        model.addAttribute("token", token);
        model.addAttribute("pageTitle", "Ввод нового пароля - Электрик");
        return "password-reset";
    }

    /**
     * Обработка установки нового пароля
     */
    @PostMapping("/reset")
    public String processResetPassword(
            @RequestParam("token") String token,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("confirmPassword") String confirmPassword,
            Model model) {

        try {
            // Проверяем токен
            PasswordResetToken resetToken = resetTokens.get(token);
            if (resetToken == null || !resetToken.isValid()) {
                model.addAttribute("error",
                        "Ссылка для восстановления пароля недействительна или истек срок её действия.");
                return "password-error";
            }

            // Проверяем совпадение паролей
            if (!newPassword.equals(confirmPassword)) {
                model.addAttribute("error", "Пароли не совпадают.");
                model.addAttribute("token", token);
                return "password-reset";
            }

            // Проверяем сложность пароля
            if (newPassword.length() < 8) {
                model.addAttribute("error", "Пароль должен содержать минимум 8 символов.");
                model.addAttribute("token", token);
                return "password-reset";
            }

            // Находим пользователя
            var userOptional = userDAO.findByEmail(resetToken.getEmail());
            if (userOptional.isEmpty()) {
                model.addAttribute("error", "Пользователь не найден.");
                return "password-error";
            }

            User user = userOptional.get();

            // Хешируем новый пароль
            String hashedPassword = passwordService.hashPassword(newPassword);

            // Обновляем пароль в базе
            user.setPassword(hashedPassword);
            userDAO.update(user);

            // Помечаем токен как использованный
            resetToken.setUsed(true);

            // Очищаем старые токены (можно сделать по расписанию)
            cleanupExpiredTokens();

            model.addAttribute("successMessage",
                    "Пароль успешно изменён! Теперь вы можете войти в систему с новым паролем.");
            return "password-success";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error",
                    "Произошла ошибка при изменении пароля. Пожалуйста, попробуйте позже.");
            return "password-error";
        }
    }

    /**
     * Отправка email для сброса пароля
     */
    private void sendResetPasswordEmail(String toEmail, String token, String username) {
        try {
            String resetLink = "http://localhost:8081/password/reset?token=" + token;

            String htmlContent = """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <style>
                        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                        .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                        .header { background: #2c3e50; color: white; padding: 20px; text-align: center; }
                        .button { 
                            display: inline-block; 
                            padding: 12px 24px; 
                            background: #3498db; 
                            color: white; 
                            text-decoration: none; 
                            border-radius: 5px; 
                            margin: 20px 0;
                            font-weight: bold;
                        }
                        .footer { 
                            margin-top: 30px; 
                            padding-top: 20px; 
                            border-top: 1px solid #eee; 
                            color: #7f8c8d;
                            font-size: 12px;
                        }
                        .token { 
                            background: #f8f9fa; 
                            padding: 15px; 
                            border-radius: 5px; 
                            font-family: monospace; 
                            word-break: break-all;
                            margin: 15px 0;
                        }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h2>Восстановление пароля</h2>
                        </div>
                        
                        <p>Здравствуйте, %s!</p>
                        <p>Мы получили запрос на восстановление пароля для вашей учётной записи на сайте "Электрик Сервис".</p>
                        
                        <p><strong>Для установки нового пароля нажмите на кнопку ниже:</strong></p>
                        
                        <p style="text-align: center;">
                            <a href="%s" class="button">Восстановить пароль</a>
                        </p>
                        
                        <p>Или скопируйте эту ссылку в адресную строку браузера:</p>
                        <div class="token">%s</div>
                        
                        <p><strong>Эта ссылка действительна в течение 24 часов.</strong></p>
                        
                        <p>Если вы не запрашивали восстановление пароля, просто проигнорируйте это письмо.</p>
                        
                        <div class="footer">
                            <p>С уважением,<br>Команда Электрик Сервис</p>
                            <p><small>Это письмо отправлено автоматически, пожалуйста, не отвечайте на него.</small></p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(username, resetLink, resetLink);

            boolean emailSent = emailService.sendResetPasswordEmail(toEmail, resetLink, username);

            if (!emailSent) {
                // Обработка ошибки
                System.err.println("Failed to send reset password email to: " + toEmail);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Очистка просроченных токенов
     */
    private void cleanupExpiredTokens() {
        resetTokens.entrySet().removeIf(entry ->
                !entry.getValue().isValid() ||
                        entry.getValue().getExpiresAt().isBefore(LocalDateTime.now().minusDays(1))
        );
    }

    /**
     * Генерация временного пароля
     */
    private String generateTemporaryPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();

        for (int i = 0; i < 12; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }

        return password.toString();
    }

    @Scheduled(fixedRate = 3600000) // Каждый час
    public void cleanupExpiredTokensScheduled() {
        cleanupExpiredTokens();
    }
}