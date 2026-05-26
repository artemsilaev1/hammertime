package com.example.hammertime.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class LotForm {

    @NotBlank(message = "Название лота обязательно")
    private String title;

    @NotBlank(message = "Описание лота обязательно")
    private String description;

    @NotNull(message = "Начальная цена обязательна")
    @Positive(message = "Начальная цена должна быть больше 0")
    private BigDecimal startPrice;

    @NotNull(message = "Минимальная ставка обязательна")
    @Positive(message = "Минимальная ставка должна быть больше 0")
    private BigDecimal minBid;

    @NotNull(message = "Время начала обязательно")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime startTime;

    @NotNull(message = "Время окончания обязательно")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime endTime;

    private List<MultipartFile> images = new ArrayList<>();

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getStartPrice() {
        return startPrice;
    }

    public BigDecimal getMinBid() {
        return minBid;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public List<MultipartFile> getImages() {
        return images;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStartPrice(BigDecimal startPrice) {
        this.startPrice = startPrice;
    }

    public void setMinBid(BigDecimal minBid) {
        this.minBid = minBid;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public void setImages(List<MultipartFile> images) {
        this.images = images;
    }
}