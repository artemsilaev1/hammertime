package com.example.hammertime.service;

import com.example.hammertime.dto.CommentForm;
import com.example.hammertime.model.Comment;
import com.example.hammertime.model.User;
import com.example.hammertime.repository.CommentRepository;
import com.example.hammertime.repository.LotRepository;
import com.example.hammertime.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final LotRepository lotRepository;

    public CommentService(CommentRepository commentRepository,
                          UserRepository userRepository,
                          LotRepository lotRepository) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.lotRepository = lotRepository;
    }

    public void addComment(Long lotId, CommentForm form, String userEmail) {
        lotRepository.findById(lotId)
                .orElseThrow(() -> new IllegalArgumentException("Лот не найден"));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

        Comment comment = new Comment();
        comment.setLotId(lotId);
        comment.setUserId(user.getId());
        comment.setText(form.getText());

        commentRepository.save(comment);
    }

    public List<Comment> findByLotId(Long lotId) {
        return commentRepository.findByLotId(lotId);
    }
}