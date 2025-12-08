package com.extrime.electrician.service.email;

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

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${email.verification.sender-name:Электрик Сервис}")
    private String senderName;

    @Value("${email.verification.subject:Код подтверждения регистрации}")
    private String subject;

    public boolean sendVerificationCode(String toEmail, String verificationCode) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, senderName);
            helper.setTo(toEmail);
            helper.setSubject(subject);

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
}