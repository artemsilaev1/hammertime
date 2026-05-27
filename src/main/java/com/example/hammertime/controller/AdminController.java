package com.example.hammertime.controller;

import com.example.hammertime.dto.BalanceForm;
import com.example.hammertime.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping
    public String adminHome() {
        return "redirect:/admin/users";
    }

    @GetMapping("/users")
    public String users(Model model) {
        model.addAttribute("users", adminService.findAllUsers());
        model.addAttribute("balanceForm", new BalanceForm());
        return "admin/users";
    }

    @PostMapping("/users/{id}/balance")
    public String updateBalance(@PathVariable Long id,
                                @Valid @ModelAttribute("balanceForm") BalanceForm balanceForm,
                                BindingResult bindingResult,
                                Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("users", adminService.findAllUsers());
            return "admin/users";
        }

        adminService.updateBalance(id, balanceForm.getBalance());
        return "redirect:/admin/users";
    }

    @GetMapping("/lots")
    public String lots(Model model) {
        model.addAttribute("lots", adminService.findAllLots());
        return "admin/lots";
    }

    @PostMapping("/lots/{id}/cancel")
    public String cancelLot(@PathVariable Long id) {
        adminService.cancelLot(id);
        return "redirect:/admin/lots";
    }

    @PostMapping("/lots/{id}/delete")
    public String deleteLot(@PathVariable Long id) {
        adminService.deleteLot(id);
        return "redirect:/admin/lots";
    }
}