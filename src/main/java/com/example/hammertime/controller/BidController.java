package com.example.hammertime.controller;

import com.example.hammertime.dto.BidRequest;
import com.example.hammertime.dto.BidResponse;
import com.example.hammertime.service.BidService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/lots/{lotId}/bids")
public class BidController {
    private final BidService bidService;

    public BidController(BidService bidService) {
        this.bidService = bidService;
    }

    @PostMapping
    public ResponseEntity<Object> placeBid(@PathVariable Long lotId,
                                           @RequestBody BidRequest request,
                                           Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Нужно войти в аккаунт"));
        }

        try {
            BidResponse response = bidService.placeBid(
                    lotId,
                    request.getAmount(),
                    principal.getName()
            );

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }
}