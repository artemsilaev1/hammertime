package com.example.hammertime.repository;

import com.example.hammertime.model.Comment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CommentRepository {

    private final JdbcTemplate jdbcTemplate;

    public CommentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(Comment comment) {
        String sql = """
                INSERT INTO comments (lot_id, user_id, text)
                VALUES (?, ?, ?)
                """;

        jdbcTemplate.update(
                sql,
                comment.getLotId(),
                comment.getUserId(),
                comment.getText()
        );
    }

    public List<Comment> findByLotId(Long lotId) {
        String sql = """
                SELECT c.*,
                       CONCAT(u.first_name, ' ', u.last_name) AS user_name
                FROM comments c
                JOIN users u ON c.user_id = u.id
                WHERE c.lot_id = ?
                ORDER BY c.created_at DESC
                """;

        return jdbcTemplate.query(sql, commentRowMapper(), lotId);
    }

    private RowMapper<Comment> commentRowMapper() {
        return (rs, rowNum) -> {
            Comment comment = new Comment();

            comment.setId(rs.getLong("id"));
            comment.setLotId(rs.getLong("lot_id"));
            comment.setUserId(rs.getLong("user_id"));
            comment.setUserName(rs.getString("user_name"));
            comment.setText(rs.getString("text"));
            comment.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());

            return comment;
        };
    }
}