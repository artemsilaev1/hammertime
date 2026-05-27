package com.example.hammertime.controller;

import com.example.hammertime.dto.RegisterForm;
import com.example.hammertime.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("form", new RegisterForm());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("form") RegisterForm form,
                           BindingResult bindingResult,
                           Model model) {

        if (bindingResult.hasErrors()) {
            return "auth/register";
        }

        try {
            userService.register(form);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "auth/register";
        }

        model.addAttribute("success", "Регистрация прошла успешно. Проверьте почту для активации аккаунта.");
        return "auth/login";
    }

    @GetMapping("/activate/{code}")
    public String activate(@PathVariable String code, Model model) {
        boolean activated = userService.activate(code);

        if (activated) {
            model.addAttribute("success", "Аккаунт успешно активирован. Теперь можно войти.");
        } else {
            model.addAttribute("error", "Неверная или устаревшая ссылка активации.");
        }

        return "auth/login";
    }

    @GetMapping("/forgot-password")
    public String forgotPasswordPage() {
        return "auth/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String forgotPassword(@RequestParam String email, Model model) {
        userService.startPasswordReset(email);

        model.addAttribute(
                "success",
                "Если пользователь с таким email существует, на почту отправлена ссылка восстановления пароля."
        );

        return "auth/forgot-password";
    }

    @GetMapping("/reset-password/{code}")
    public String resetPasswordPage(@PathVariable String code, Model model) {
        if (!userService.isResetCodeValid(code)) {
            model.addAttribute("error", "Неверная или устаревшая ссылка восстановления пароля.");
            return "auth/login";
        }

        model.addAttribute("code", code);
        return "auth/reset-password";
    }

    @PostMapping("/reset-password/{code}")
    public String resetPassword(@PathVariable String code,
                                @RequestParam String password,
                                @RequestParam String repeatPassword,
                                Model model) {

        if (!password.equals(repeatPassword)) {
            model.addAttribute("code", code);
            model.addAttribute("error", "Пароли не совпадают");
            return "auth/reset-password";
        }

        try {
            userService.resetPassword(code, password);
        } catch (IllegalArgumentException e) {
            model.addAttribute("code", code);
            model.addAttribute("error", e.getMessage());
            return "auth/reset-password";
        }

        model.addAttribute("success", "Пароль успешно изменён. Теперь можно войти.");
        return "auth/login";
    }
}