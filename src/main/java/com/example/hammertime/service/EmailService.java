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

    @Value("${spring.mail.username:}")
    private String mailFrom;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendActivationEmail(String to, String activationCode) {
        String link = appUrl + "/activate/" + activationCode;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setFrom(mailFrom);
        message.setSubject("Подтверждение регистрации Hammer-Time");
        message.setText("""
                Здравствуйте!

                Вы зарегистрировались на сайте Hammer-Time.

                Для подтверждения регистрации перейдите по ссылке:
                %s

                Если вы не регистрировались на сайте, просто проигнорируйте это письмо.
                """.formatted(link));

        try {
            mailSender.send(message);
            System.out.println("Письмо подтверждения отправлено на почту: " + to);
        } catch (Exception e) {
            System.out.println("Почтовый сервер пока не настроен или письмо не отправилось.");
            System.out.println("Ссылка для активации аккаунта:");
            System.out.println(link);
            System.out.println("Ошибка отправки email:");
            System.out.println(e.getMessage());
        }
    }

    public void sendPasswordResetEmail(String to, String resetCode) {
        String link = appUrl + "/reset-password/" + resetCode;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setFrom(mailFrom);
        message.setSubject("Восстановление пароля Hammer-Time");
        message.setText("""
                Здравствуйте!

                Для восстановления пароля на сайте Hammer-Time перейдите по ссылке:
                %s

                Если вы не запрашивали восстановление пароля, просто проигнорируйте это письмо.
                """.formatted(link));

        try {
            mailSender.send(message);
            System.out.println("Письмо восстановления пароля отправлено на почту: " + to);
        } catch (Exception e) {
            System.out.println("Письмо восстановления пароля не отправилось.");
            System.out.println("Ссылка для восстановления пароля:");
            System.out.println(link);
            System.out.println("Ошибка отправки email:");
            System.out.println(e.getMessage());
        }
    }

    public void sendSimpleEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setFrom(mailFrom);
        message.setSubject(subject);
        message.setText(text);

        try {
            mailSender.send(message);
            System.out.println("Письмо отправлено на почту: " + to);
        } catch (Exception e) {
            System.out.println("Письмо не отправилось.");
            System.out.println("Получатель: " + to);
            System.out.println("Тема: " + subject);
            System.out.println("Текст письма:");
            System.out.println(text);
            System.out.println("Ошибка отправки email:");
            System.out.println(e.getMessage());
        }
    }
}