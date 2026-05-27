package com.example.hammertime.dto;

import java.math.BigDecimal;

public class BidResponse {
    private Long lotId;
    private BigDecimal amount;
    private BigDecimal currentPrice;
    private String userName;
    private String createdAt;
    private String message;

    public BidResponse() {
    }

    public BidResponse(Long lotId, BigDecimal amount, BigDecimal currentPrice,
                       String userName, String createdAt, String message) {
        this.lotId = lotId;
        this.amount = amount;
        this.currentPrice = currentPrice;
        this.userName = userName;
        this.createdAt = createdAt;
        this.message = message;
    }

    public Long getLotId() {
        return lotId;
    }

    public void setLotId(Long lotId) {
        this.lotId = lotId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(BigDecimal currentPrice) {
        this.currentPrice = currentPrice;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}