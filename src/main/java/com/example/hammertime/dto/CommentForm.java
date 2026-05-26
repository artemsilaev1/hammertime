package com.example.hammertime.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CommentForm {

    @NotBlank(message = "Комментарий не может быть пустым")
    @Size(max = 1000, message = "Комментарий не должен быть длиннее 1000 символов")
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}