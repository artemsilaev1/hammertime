package com.example.hammertime.service;

import com.example.hammertime.dto.RegisterForm;
import com.example.hammertime.model.Role;
import com.example.hammertime.model.User;
import com.example.hammertime.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    public void register(RegisterForm form) {
        if (userRepository.existsByEmail(form.getEmail())) {
            throw new IllegalArgumentException("Пользователь с таким email уже зарегистрирован");
        }

        User user = new User();
        user.setFirstName(form.getFirstName());
        user.setLastName(form.getLastName());
        user.setEmail(form.getEmail());
        user.setPassword(passwordEncoder.encode(form.getPassword()));
        user.setRole(Role.USER);
        user.setEnabled(false);
        user.setBalance(BigDecimal.ZERO);
        user.setActivationCode(UUID.randomUUID().toString());

        userRepository.save(user);

        emailService.sendActivationEmail(user.getEmail(), user.getActivationCode());
    }

    public boolean activate(String code) {
        User user = userRepository.findByActivationCode(code).orElse(null);

        if (user == null) {
            return false;
        }

        user.setEnabled(true);
        user.setActivationCode(null);

        userRepository.update(user);

        return true;
    }

    public User getByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
    }

    public void startPasswordReset(String email) {
        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            return;
        }

        String resetCode = UUID.randomUUID().toString();
        user.setResetPasswordCode(resetCode);

        userRepository.update(user);

        emailService.sendPasswordResetEmail(user.getEmail(), resetCode);
    }

    public boolean isResetCodeValid(String code) {
        return userRepository.findByResetPasswordCode(code).isPresent();
    }

    public void resetPassword(String code, String newPassword) {
        User user = userRepository.findByResetPasswordCode(code)
                .orElseThrow(() -> new IllegalArgumentException("Неверная или устаревшая ссылка восстановления пароля"));

        if (newPassword == null || newPassword.length() < 6) {
            throw new IllegalArgumentException("Пароль должен быть минимум 6 символов");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetPasswordCode(null);

        userRepository.update(user);
    }
}