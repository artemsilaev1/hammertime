package com.example.hammertime.model;

import java.time.LocalDateTime;

public class Comment {

    private Long id;
    private Long lotId;
    private Long userId;
    private String userName;
    private String text;
    private LocalDateTime createdAt;

    public Long getId() {
        return id;
    }

    public Long getLotId() {
        return lotId;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getText() {
        return text;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setLotId(Long lotId) {
        this.lotId = lotId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}