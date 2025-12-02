package com.extrime.electrician.service;

import org.springframework.stereotype.Service;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Service
public class PasswordService {

    // Простое хеширование пароля (в продакшене используйте BCrypt)
    public String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Ошибка при хешировании пароля", e);
        }
    }

    // Проверка пароля
    public boolean checkPassword(String inputPassword, String storedHash) {
        String inputHash = hashPassword(inputPassword);
        return inputHash.equals(storedHash);
    }

//    // Генерация случайного пароля
//    public String generateRandomPassword() {
//        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
//        StringBuilder password = new StringBuilder();
//        for (int i = 0; i < 10; i++) {
//            int index = (int) (Math.random() * chars.length());
//            password.append(chars.charAt(index));
//        }
//        return password.toString();
//    }
}
