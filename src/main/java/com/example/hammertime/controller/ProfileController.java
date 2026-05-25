package com.example.hammertime.controller;

import com.example.hammertime.model.User;
import com.example.hammertime.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class ProfileController {

    private final UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public String profile(Principal principal, Model model) {
        User user = userService.getByEmail(principal.getName());
        model.addAttribute("user", user);
        return "profile";
    }
}