package com.example.hammertime.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.url}")
    private String appUrl;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendActivationEmail(String to, String activationCode) {
        String link = appUrl + "/activate/" + activationCode;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Подтверждение регистрации Hammer-Time");
        message.setText("Здравствуйте!\n\nДля подтверждения регистрации перейдите по ссылке:\n" + link);

        try {
            mailSender.send(message);
        } catch (Exception e) {
            System.out.println("Почтовый сервер пока не настроен.");
            System.out.println("Ссылка для активации аккаунта:");
            System.out.println(link);
        }
    }
}