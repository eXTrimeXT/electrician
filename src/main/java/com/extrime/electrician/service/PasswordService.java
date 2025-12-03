//package com.extrime.electrician.service;
//
//import org.springframework.stereotype.Service;
//import java.security.MessageDigest;
//import java.security.NoSuchAlgorithmException;
//import java.util.Base64;
//
//@Service
//public class PasswordService {
//    // Простое хеширование пароля (в продакшене используйте BCrypt)
//    public String hashPassword(String password) {
//        try {
//            MessageDigest md = MessageDigest.getInstance("SHA-256");
//            byte[] hash = md.digest(password.getBytes());
//            return Base64.getEncoder().encodeToString(hash);
//        } catch (NoSuchAlgorithmException e) {
//            throw new RuntimeException("Ошибка при хешировании пароля", e);
//        }
//    }
//    // Проверка пароля
//    public boolean checkPassword(String inputPassword, String storedHash) {
//        String inputHash = hashPassword(inputPassword);
//        return inputHash.equals(storedHash);
//    }
//}

package com.extrime.electrician.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PasswordService {
    private final PasswordEncoder passwordEncoder;

    public PasswordService() {
        this.passwordEncoder = new BCryptPasswordEncoder(12);
    }

    // Хеширование пароля с использованием BCrypt
    public String hashPassword(String password) {
        String hashPassword = passwordEncoder.encode(password);
        System.out.println("hashPassword = " + hashPassword);
        return hashPassword;
    }

    // Проверка пароля
    public boolean checkPassword(String inputPassword, String storedHash) {
        System.out.println("inputPassword = " + inputPassword + " storedHash = " + storedHash);
        return passwordEncoder.matches(inputPassword, storedHash);
    }

    // Дополнительный метод для проверки валидности хеша
    public boolean isValidBCryptHash(String hash) {
        // BCrypt хеш начинается с $2a$, $2b$, $2x$ или $2y$
        return hash != null &&
                (hash.startsWith("$2a$") ||
                        hash.startsWith("$2b$") ||
                        hash.startsWith("$2x$") ||
                        hash.startsWith("$2y$"));
    }
}