package com.extrime.electrician.service;

import com.extrime.electrician.dao.UserDAO;
import com.extrime.electrician.model.User;
import com.extrime.electrician.service.email.EmailVerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Service
public class RegisterService {

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private PasswordService passwordService;

    @Autowired
    private EmailVerificationService emailVerificationService;

    // Регулярные выражения для валидации
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,30}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    /**
     * Создание объекта пользователя с хешированием пароля
     */
    private User createUser(String username, String password, String email) {
        User newUser = new User();
        newUser.setUsername(username);

        // Хешируем пароль
        String hashedPassword = passwordService.hashPassword(password);
        newUser.setPassword(hashedPassword);
        newUser.setEmail(email);
        newUser.setRole("USER"); // По умолчанию обычный пользователь
        newUser.setActive(true);

        return newUser;
    }

    /**
     * Валидация данных регистрации
     */
    private Map<String, String> validateRegistration(String username, String password,
                                                     String confirmPassword, String email) {

        Map<String, String> errors = new HashMap<>();

        // ===== ВАЛИДАЦИЯ USERNAME =====
        if (username == null || username.trim().isEmpty()) {
            errors.put("username", "Логин не может быть пустым");
        } else if (username.length() < 3) {
            errors.put("username", "Логин должен содержать минимум 3 символа");
        } else if (username.length() > 30) {
            errors.put("username", "Логин не должен превышать 30 символов");
        } else if (!USERNAME_PATTERN.matcher(username).matches()) {
            errors.put("username", "Логин может содержать только латинские буквы, цифры и подчеркивания");
        }

        // ===== ВАЛИДАЦИЯ EMAIL =====
        if (email == null || email.trim().isEmpty()) {
            errors.put("email", "Email обязателен для заполнения");
        } else if (email.length() > 100) {
            errors.put("email", "Email не должен превышать 100 символов");
        } else if (!EMAIL_PATTERN.matcher(email).matches()) {
            errors.put("email", "Введите корректный email адрес (например: user@example.com)");
        } else if (email.contains(" ")) {
            errors.put("email", "Email не должен содержать пробелы");
        }

        // ===== ВАЛИДАЦИЯ PASSWORD =====
        if (password == null || password.isEmpty()) {
            errors.put("password", "Пароль не может быть пустым");
        } else if (password.length() < 8) {
            errors.put("password", "Пароль должен содержать минимум 8 символов");
        } else if (password.length() > 50) {
            errors.put("password", "Пароль не должен превышать 50 символов");
        } else {
            validatePasswordStrength(password, errors);
        }

        // ===== ВАЛИДАЦИЯ ПОДТВЕРЖДЕНИЯ ПАРОЛЯ =====
        if (confirmPassword == null || confirmPassword.isEmpty()) {
            errors.put("confirmPassword", "Подтверждение пароля не может быть пустым");
        } else if (!password.equals(confirmPassword)) {
            errors.put("confirmPassword", "Пароли не совпадают");
        }

        // ===== ДОПОЛНИТЕЛЬНЫЕ ПРОВЕРКИ =====
        // Проверка на одинаковый username и password
        if (!username.isEmpty() && !password.isEmpty() && username.equals(password)) {
            errors.put("password", "Пароль не должен совпадать с логином");
        }

        return errors;
    }

    /**
     * Проверка силы пароля
     */
    private void validatePasswordStrength(String password, Map<String, String> errors) {
        boolean hasDigit = false;
        boolean hasLower = false;
        boolean hasUpper = false;

        for (char c : password.toCharArray()) {
            if (Character.isDigit(c)) hasDigit = true;
            if (Character.isLowerCase(c)) hasLower = true;
            if (Character.isUpperCase(c)) hasUpper = true;
        }

        if (!hasDigit) {
            errors.put("password", "Пароль должен содержать хотя бы одну цифру");
        }
        if (!hasLower) {
            errors.put("password", "Пароль должен содержать хотя бы одну строчную букву");
        }
        if (!hasUpper) {
            errors.put("password", "Пароль должен содержать хотя бы одну заглавную букву");
        }

        // Проверка на только цифры
        if (password.matches("^\\d+$")) {
            errors.put("password", "Пароль не может состоять только из цифр");
        }

        // Проверка на только буквы
        if (password.matches("^[a-zA-Z]+$")) {
            errors.put("password", "Пароль не может состоять только из букв");
        }

        // Проверка на слишком простые пароли
        checkWeakPasswords(password, errors);
    }

    /**
     * Проверка на слабые пароли
     */
    private void checkWeakPasswords(String password, Map<String, String> errors) {
        String[] weakPasswords = {
                "12345678", "password", "qwerty123", "admin123", "letmein",
                "123456789", "password123", "123456", "1234567890", "1234567"
        };

        for (String weak : weakPasswords) {
            if (password.equalsIgnoreCase(weak)) {
                errors.put("password", "Пароль слишком простой. Выберите более сложный пароль");
                break;
            }
        }
    }

    /**
     * Обработка исключений базы данных
     */
    private Map<String, String> handleDatabaseException(DataAccessException e) {
        Map<String, String> dbErrors = new HashMap<>();

        if (e.getMessage().contains("duplicate key") || e.getMessage().contains("violates unique constraint")) {
            if (e.getMessage().toLowerCase().contains("username")) {
                dbErrors.put("username", "Пользователь с таким логином уже существует");
            } else if (e.getMessage().toLowerCase().contains("email")) {
                dbErrors.put("email", "Пользователь с таким email уже существует");
            }
        }

        return dbErrors;
    }

    /**
     * Проверка доступности логина (можно использовать для AJAX валидации)
     */
    public boolean isUsernameAvailable(String username) {
        return !userDAO.existsByUsername(username);
    }

    /**
     * Проверка доступности email (можно использовать для AJAX валидации)
     */
    public boolean isEmailAvailable(String email) {
        return !userDAO.existsByEmail(email);
    }

    @Transactional
    public Map<String, Object> processRegistration(String username, String password,
                                                   String confirmPassword, String email) {
        Map<String, Object> result = new HashMap<>();

        // Валидация данных
        Map<String, String> validationErrors = validateRegistration(username, password, confirmPassword, email);
        if (!validationErrors.isEmpty()) {
            result.put("errors", validationErrors);
            return result;
        }

        try {
            // Проверяем доступность логина и email
            if (userDAO.existsByUsername(username)) {
                validationErrors.put("username", "Пользователь с таким логином уже существует");
                result.put("errors", validationErrors);
                return result;
            }

            if (userDAO.existsByEmail(email)) {
                validationErrors.put("email", "Пользователь с таким email уже существует");
                result.put("errors", validationErrors);
                return result;
            }

            // Создаем и сохраняем нового пользователя (НО ЕЩЕ НЕ АКТИВИРУЕМ)
            User newUser = createUser(username, password, email);
            newUser.setActive(false); // Пользователь неактивен до подтверждения email
            newUser.setEmailVerified(false); // email еще не подтвержден
            Long userId = userDAO.createUser(newUser);

            if (userId != null && userId > 0) {
                // Отправляем код подтверждения
                boolean emailSent = emailVerificationService.createAndSendVerificationCode(email, userId);

                if (emailSent) {
                    // Возвращаем ID пользователя для верификации
                    result.put("success", true);
                    result.put("userId", userId);
                    result.put("email", email);
                    result.put("requiresVerification", true);
                } else {
                    result.put("error", "Не удалось отправить код подтверждения. Попробуйте позже.");
                    // Удаляем созданного пользователя
                    userDAO.deleteUser(userId);
                }
            } else {
                result.put("error", "Не удалось создать пользователя");
            }

        } catch (DataAccessException e) {
            // Обработка ошибок базы данных
            Map<String, String> dbErrors = handleDatabaseException(e);
            if (!dbErrors.isEmpty()) {
                result.put("errors", dbErrors);
            } else {
                result.put("error", "Ошибка базы данных при регистрации. Попробуйте позже.");
            }
        } catch (Exception e) {
            // Общая обработка исключений
            e.printStackTrace();
            result.put("error", "Произошла непредвиденная ошибка. Пожалуйста, попробуйте позже.");
        }

        return result;
    }

    /**
     * Подтверждение email и активация пользователя
     */
    @Transactional
    public Map<String, Object> confirmEmail(Long userId, String email, String code) {
        Map<String, Object> result = new HashMap<>();

        try {
            // Проверяем код
            boolean isValid = emailVerificationService.verifyCode(email, code);

            if (isValid) {
                // Активируем пользователя
                boolean activated = userDAO.activateUser(userId);

                if (activated) {
                    result.put("success", true);
                    result.put("message", "Email успешно подтвержден! Теперь вы можете войти.");
                } else {
                    result.put("error", "Не удалось активировать пользователя.");
                }
            } else {
                result.put("error", "Неверный или устаревший код подтверждения.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            result.put("error", "Произошла ошибка при подтверждении email.");
        }

        return result;
    }

    /**
     * Повторная отправка кода подтверждения
     */
    @Transactional
    public Map<String, Object> resendVerificationCode(Long userId, String email) {
        Map<String, Object> result = new HashMap<>();

        try {
            // Проверяем, не активен ли уже пользователь
            if (userDAO.isUserActive(userId)) {
                result.put("error", "Пользователь уже активирован.");
                return result;
            }

            // Отправляем новый код
            boolean emailSent = emailVerificationService.createAndSendVerificationCode(email, userId);

            if (emailSent) {
                result.put("success", true);
                result.put("message", "Новый код подтверждения отправлен на вашу почту.");
            } else {
                result.put("error", "Не удалось отправить код подтверждения. Попробуйте позже.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            result.put("error", "Произошла ошибка при отправке кода.");
        }

        return result;
    }
}