package com.example.hammertime.service;

import com.example.hammertime.model.Lot;
import com.example.hammertime.model.LotStatus;
import com.example.hammertime.model.User;
import com.example.hammertime.repository.LotRepository;
import com.example.hammertime.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AdminService {
    private final UserRepository userRepository;
    private final LotRepository lotRepository;

    public AdminService(UserRepository userRepository, LotRepository lotRepository) {
        this.userRepository = userRepository;
        this.lotRepository = lotRepository;
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public List<Lot> findAllLots() {
        return lotRepository.findAll(null, null);
    }

    public void updateBalance(Long userId, BigDecimal balance) {
        if (balance == null || balance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Баланс не может быть отрицательным");
        }

        userRepository.updateBalance(userId, balance);
    }

    public void cancelLot(Long lotId) {
        lotRepository.updateStatus(lotId, LotStatus.CANCELLED);
    }

    public void deleteLot(Long lotId) {
        lotRepository.deleteById(lotId);
    }
}