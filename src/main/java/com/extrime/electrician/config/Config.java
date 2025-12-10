package com.extrime.electrician.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
public class Config {
    // Базовая конфигурация
    @Value("${spring.application.url}")
    private String URL;
    @Value("${server.port}")
    private String PORT;

    // DATABASE
    private final String NAME_TYPE_DATABASE = "postgres"; // postgres or mysql

    @Value("${spring.datasource.url}")
    private String databaseUrl;
    @Value("${spring.datasource.username}")
    private String databaseUsername;
    @Value("${spring.datasource.password}")
    private String databasePassword;
    @Value("${spring.datasource.driver-class-name}")
    private String databaseDriverClassName;

    public boolean isPostgres(){
        return NAME_TYPE_DATABASE.equals("postgres");
    }

    public boolean isMysql(){
        return NAME_TYPE_DATABASE.equals("mysql");
    }

    // MAIL
    @Value("${spring.mail.username}")
    private String fromEmail;
    @Value("${email.verification.sender-name}")
    private String senderName;
    @Value("${email.verification.subject}")
    private String subject;
    @Value("${email.verification.code-length}")
    private int codeLength;
    @Value("${email.verification.expiration-minutes}")
    private int expirationMinutes;

    // FileStorageService - path to uploads dir
    @Value("${file.upload-dir:./uploads}")
    private String uploadDir;
}
