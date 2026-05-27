package com.example.hammertime.controller;

import com.example.hammertime.service.SubscriptionService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/lots/{lotId}/subscriptions")
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @PostMapping("/subscribe")
    public String subscribe(@PathVariable Long lotId, Principal principal) {
        subscriptionService.subscribe(lotId, principal.getName());
        return "redirect:/lots/" + lotId;
    }

    @PostMapping("/unsubscribe")
    public String unsubscribe(@PathVariable Long lotId, Principal principal) {
        subscriptionService.unsubscribe(lotId, principal.getName());
        return "redirect:/lots/" + lotId;
    }
}