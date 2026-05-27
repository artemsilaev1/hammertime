package com.example.hammertime.repository;

import com.example.hammertime.model.Role;
import com.example.hammertime.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SubscriptionRepository {
    private final JdbcTemplate jdbcTemplate;

    public SubscriptionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void subscribe(Long lotId, Long userId) {
        String sql = """
                INSERT IGNORE INTO subscriptions (lot_id, user_id)
                VALUES (?, ?)
                """;

        jdbcTemplate.update(sql, lotId, userId);
    }

    public void unsubscribe(Long lotId, Long userId) {
        String sql = """
                DELETE FROM subscriptions
                WHERE lot_id = ? AND user_id = ?
                """;

        jdbcTemplate.update(sql, lotId, userId);
    }

    public boolean exists(Long lotId, Long userId) {
        String sql = """
                SELECT COUNT(*)
                FROM subscriptions
                WHERE lot_id = ? AND user_id = ?
                """;

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, lotId, userId);
        return count != null && count > 0;
    }

    public List<User> findUsersByLotId(Long lotId) {
        String sql = """
                SELECT u.*
                FROM subscriptions s
                JOIN users u ON s.user_id = u.id
                WHERE s.lot_id = ?
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            User user = new User();
            user.setId(rs.getLong("id"));
            user.setFirstName(rs.getString("first_name"));
            user.setLastName(rs.getString("last_name"));
            user.setEmail(rs.getString("email"));
            user.setPassword(rs.getString("password"));
            user.setRole(Role.valueOf(rs.getString("role")));
            user.setEnabled(rs.getBoolean("enabled"));
            user.setBalance(rs.getBigDecimal("balance"));
            user.setActivationCode(rs.getString("activation_code"));
            user.setResetPasswordCode(rs.getString("reset_password_code"));
            return user;
        }, lotId);
    }
}