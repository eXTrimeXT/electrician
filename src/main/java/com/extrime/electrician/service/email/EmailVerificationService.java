package com.extrime.electrician.service.email;

import com.extrime.electrician.config.Config;
import com.extrime.electrician.dao.EmailVerificationDAO;
import com.extrime.electrician.model.EmailVerification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Random;

@Service
public class EmailVerificationService {

    @Autowired
    private EmailVerificationDAO emailVerificationDAO;

    @Autowired
    private EmailService emailService;

    @Autowired
    private Config config;

    /**
     * Генерация кода верификации
     */
    public String generateVerificationCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();

        for (int i = 0; i < config.getCodeLength(); i++) {
            code.append(random.nextInt(10)); // только цифры
        }

        return code.toString();
    }

    /**
     * Создание и отправка кода верификации
     */
    @Transactional
    public boolean createAndSendVerificationCode(String email, Long userId) {
        try {
            // Генерируем код
            String verificationCode = generateVerificationCode();

            // Устанавливаем время истечения
            LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(config.getExpirationMinutes());

            // Создаем запись в БД
            EmailVerification verification = new EmailVerification();
            verification.setEmail(email);
            verification.setVerificationCode(verificationCode);
            verification.setExpiresAt(expiresAt);
            verification.setUserId(userId);
            verification.setUsed(false);

            // Сохраняем в БД
            Long verificationId = emailVerificationDAO.save(verification);

            if (verificationId == null || verificationId <= 0) {
                return false;
            }

            // Отправляем email
            return emailService.sendVerificationCode(email, verificationCode);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Проверка кода верификации
     */
    @Transactional
    public boolean verifyCode(String email, String code) {
        EmailVerification verification = emailVerificationDAO
                .findByEmailAndCode(email, code);

        if (verification == null) {
            return false;
        }

        // Проверяем, не истек ли срок действия
        if (verification.getExpiresAt().isBefore(LocalDateTime.now())) {
            return false;
        }

        // Проверяем, не использован ли уже код
        if (verification.isUsed()) {
            return false;
        }

        // Помечаем код как использованный
        verification.setUsed(true);
        emailVerificationDAO.update(verification);

        return true;
    }

    /**
     * Проверка наличия активной верификации
     */
    public boolean hasActiveVerification(Long userId) {
        EmailVerification verification = emailVerificationDAO
                .findActiveByUserId(userId);
        return verification != null &&
                verification.getExpiresAt().isAfter(LocalDateTime.now()) &&
                !verification.isUsed();
    }
}