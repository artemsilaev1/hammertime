package com.example.hammertime.service;

import com.example.hammertime.model.User;
import com.example.hammertime.repository.LotRepository;
import com.example.hammertime.repository.SubscriptionRepository;
import com.example.hammertime.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final LotRepository lotRepository;

    public SubscriptionService(SubscriptionRepository subscriptionRepository,
                               UserRepository userRepository,
                               LotRepository lotRepository) {
        this.subscriptionRepository = subscriptionRepository;
        this.userRepository = userRepository;
        this.lotRepository = lotRepository;
    }

    public void subscribe(Long lotId, String userEmail) {
        lotRepository.findById(lotId)
                .orElseThrow(() -> new IllegalArgumentException("Лот не найден"));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

        subscriptionRepository.subscribe(lotId, user.getId());
    }

    public void unsubscribe(Long lotId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

        subscriptionRepository.unsubscribe(lotId, user.getId());
    }

    public boolean isSubscribed(Long lotId, String userEmail) {
        if (userEmail == null) {
            return false;
        }

        User user = userRepository.findByEmail(userEmail).orElse(null);

        if (user == null) {
            return false;
        }

        return subscriptionRepository.exists(lotId, user.getId());
    }
}