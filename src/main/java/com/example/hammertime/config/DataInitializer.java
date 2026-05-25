package com.example.hammertime.config;

import com.example.hammertime.model.Role;
import com.example.hammertime.model.User;
import com.example.hammertime.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (!userRepository.existsByEmail("admin@hammer-time.local")) {
            User admin = new User();
            admin.setFirstName("Admin");
            admin.setLastName("HammerTime");
            admin.setEmail("admin@hammer-time.local");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(Role.ADMIN);
            admin.setEnabled(true);
            admin.setBalance(new BigDecimal("1000.00"));

            userRepository.save(admin);

            System.out.println("Создан администратор:");
            System.out.println("email: admin@hammer-time.local");
            System.out.println("password: admin123");
        }
    }
}