package com.extrime.electrician.service.email;

import com.extrime.electrician.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private Config config;

    public boolean sendVerificationCode(String toEmail, String verificationCode) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(config.getFromEmail(), config.getSenderName());
            helper.setTo(toEmail);
            helper.setSubject(config.getSubject());

            String htmlContent = """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <style>
                        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                        .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                        .header { background: #2c3e50; color: white; padding: 20px; text-align: center; }
                        .code { 
                            font-size: 32px; 
                            font-weight: bold; 
                            color: #27ae60; 
                            text-align: center;
                            padding: 20px;
                            margin: 20px 0;
                            border: 2px dashed #27ae60;
                            border-radius: 10px;
                            background: #f8f9fa;
                        }
                        .footer { 
                            margin-top: 30px; 
                            padding-top: 20px; 
                            border-top: 1px solid #eee; 
                            color: #7f8c8d;
                            font-size: 12px;
                        }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h2>Подтверждение регистрации</h2>
                        </div>
                        
                        <p>Здравствуйте!</p>
                        <p>Благодарим вас за регистрацию на сайте "Электрик Сервис".</p>
                        <p>Для завершения регистрации, пожалуйста, введите следующий код подтверждения:</p>
                        
                        <div class="code">%s</div>
                        
                        <p><strong>Код действителен в течение 5 минут.</strong></p>
                        <p>Если вы не регистрировались на нашем сайте, просто проигнорируйте это письмо.</p>
                        
                        <div class="footer">
                            <p>С уважением,<br>Команда Электрик Сервис</p>
                            <p><small>Это письмо отправлено автоматически, пожалуйста, не отвечайте на него.</small></p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(verificationCode);

            helper.setText(htmlContent, true);

            mailSender.send(message);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean sendResetPasswordEmail(String toEmail, String resetLink, String username) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(config.getFromEmail(), config.getSenderName());
            helper.setTo(toEmail);
            helper.setSubject("Восстановление пароля - Электрик Сервис");

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

            helper.setText(htmlContent, true);
            mailSender.send(message);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}