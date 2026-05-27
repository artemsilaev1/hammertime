package com.example.hammertime.service;

import com.example.hammertime.dto.BidResponse;
import com.example.hammertime.model.Bid;
import com.example.hammertime.model.Lot;
import com.example.hammertime.model.LotStatus;
import com.example.hammertime.model.User;
import com.example.hammertime.repository.BidRepository;
import com.example.hammertime.repository.LotRepository;
import com.example.hammertime.repository.SubscriptionRepository;
import com.example.hammertime.repository.UserRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class BidService {
    private final BidRepository bidRepository;
    private final LotRepository lotRepository;
    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final EmailService emailService;
    private final SimpMessagingTemplate messagingTemplate;

    public BidService(BidRepository bidRepository,
                      LotRepository lotRepository,
                      UserRepository userRepository,
                      SubscriptionRepository subscriptionRepository,
                      EmailService emailService,
                      SimpMessagingTemplate messagingTemplate) {
        this.bidRepository = bidRepository;
        this.lotRepository = lotRepository;
        this.userRepository = userRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.emailService = emailService;
        this.messagingTemplate = messagingTemplate;
    }

    @Transactional
    public BidResponse placeBid(Long lotId, BigDecimal amount, String userEmail) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Ставка должна быть больше 0");
        }

        Lot lot = lotRepository.findById(lotId)
                .orElseThrow(() -> new IllegalArgumentException("Лот не найден"));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

        LocalDateTime now = LocalDateTime.now();

        if (lot.getOwnerId().equals(user.getId())) {
            throw new IllegalArgumentException("Нельзя делать ставку на свой лот");
        }

        if (lot.getStatus() == LotStatus.CANCELLED) {
            throw new IllegalArgumentException("Аукцион отменён");
        }

        if (lot.getStatus() == LotStatus.FINISHED || now.isAfter(lot.getEndTime())) {
            throw new IllegalArgumentException("Аукцион уже завершён");
        }

        if (now.isBefore(lot.getStartTime())) {
            throw new IllegalArgumentException("Аукцион ещё не начался");
        }

        BigDecimal minAllowedBid = lot.getCurrentPrice().add(lot.getMinBid());

        if (amount.compareTo(minAllowedBid) < 0) {
            throw new IllegalArgumentException("Минимальная допустимая ставка: " + minAllowedBid);
        }

        if (user.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Недостаточно кредитов на балансе");
        }

        Bid bid = new Bid();
        bid.setLotId(lotId);
        bid.setUserId(user.getId());
        bid.setUserName(user.getFirstName() + " " + user.getLastName());
        bid.setAmount(amount);

        bidRepository.save(bid);

        lotRepository.updateCurrentPrice(lotId, amount);
        lotRepository.updateStatus(lotId, LotStatus.ACTIVE);

        String createdAt = bid.getCreatedAt()
                .format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));

        BidResponse response = new BidResponse(
                lotId,
                amount,
                amount,
                bid.getUserName(),
                createdAt,
                "Ставка принята"
        );

        messagingTemplate.convertAndSend("/topic/lots/" + lotId, response);

        notifySubscribers(lot, user, amount);

        return response;
    }

    public List<Bid> findByLotId(Long lotId) {
        return bidRepository.findByLotId(lotId);
    }

    private void notifySubscribers(Lot lot, User bidder, BigDecimal amount) {
        List<User> subscribers = subscriptionRepository.findUsersByLotId(lot.getId());

        for (User subscriber : subscribers) {
            if (subscriber.getId().equals(bidder.getId())) {
                continue;
            }

            emailService.sendSimpleEmail(
                    subscriber.getEmail(),
                    "Новая ставка на лот " + lot.getTitle(),
                    "На лот '" + lot.getTitle() + "' сделана новая ставка: " + amount
            );
        }
    }
}