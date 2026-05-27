package com.example.hammertime.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public class BalanceForm {
    @NotNull(message = "Баланс обязателен")
    @PositiveOrZero(message = "Баланс не может быть отрицательным")
    private BigDecimal balance;

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}