package com.example.hammertime.service;

import com.example.hammertime.model.Bid;
import com.example.hammertime.model.Lot;
import com.example.hammertime.model.LotStatus;
import com.example.hammertime.model.User;
import com.example.hammertime.repository.BidRepository;
import com.example.hammertime.repository.LotRepository;
import com.example.hammertime.repository.SubscriptionRepository;
import com.example.hammertime.repository.UserRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AuctionService {
    private final LotRepository lotRepository;
    private final BidRepository bidRepository;
    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final EmailService emailService;

    public AuctionService(LotRepository lotRepository,
                          BidRepository bidRepository,
                          UserRepository userRepository,
                          SubscriptionRepository subscriptionRepository,
                          EmailService emailService) {
        this.lotRepository = lotRepository;
        this.bidRepository = bidRepository;
        this.userRepository = userRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.emailService = emailService;
    }

    @Scheduled(fixedRate = 10000)
    @Transactional
    public void finishExpiredLots() {
        List<Lot> lots = lotRepository.findLotsForFinish(LocalDateTime.now());

        for (Lot lot : lots) {
            finishLot(lot);
        }
    }

    private void finishLot(Lot lot) {
        lotRepository.updateStatus(lot.getId(), LotStatus.FINISHED);

        Optional<Bid> topBidOptional = bidRepository.findTopByLotId(lot.getId());

        if (topBidOptional.isEmpty()) {
            notifySubscribers(lot, "Аукцион завершён без ставок");
            return;
        }

        Bid topBid = topBidOptional.get();

        User winner = userRepository.findById(topBid.getUserId()).orElse(null);

        if (winner != null) {
            emailService.sendSimpleEmail(
                    winner.getEmail(),
                    "Вы выиграли аукцион",
                    "Вы выиграли лот '" + lot.getTitle() + "' со ставкой " + topBid.getAmount()
            );
        }

        notifySubscribers(
                lot,
                "Аукцион завершён. Победитель: " + topBid.getUserName()
                        + ", ставка: " + topBid.getAmount()
        );
    }

    private void notifySubscribers(Lot lot, String text) {
        List<User> subscribers = subscriptionRepository.findUsersByLotId(lot.getId());

        for (User subscriber : subscribers) {
            emailService.sendSimpleEmail(
                    subscriber.getEmail(),
                    "Изменение по лоту " + lot.getTitle(),
                    text
            );
        }
    }
}