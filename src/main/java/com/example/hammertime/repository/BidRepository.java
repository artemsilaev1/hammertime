package com.example.hammertime.repository;

import com.example.hammertime.model.Bid;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class BidRepository {
    private final JdbcTemplate jdbcTemplate;

    public BidRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(Bid bid) {
        String sql = """
                INSERT INTO bids (lot_id, user_id, amount, created_at)
                VALUES (?, ?, ?, ?)
                """;

        LocalDateTime now = LocalDateTime.now();
        bid.setCreatedAt(now);

        jdbcTemplate.update(
                sql,
                bid.getLotId(),
                bid.getUserId(),
                bid.getAmount(),
                Timestamp.valueOf(now)
        );
    }

    public List<Bid> findByLotId(Long lotId) {
        String sql = """
                SELECT b.*,
                       CONCAT(u.first_name, ' ', u.last_name) AS user_name
                FROM bids b
                JOIN users u ON b.user_id = u.id
                WHERE b.lot_id = ?
                ORDER BY b.created_at DESC
                """;

        return jdbcTemplate.query(sql, bidRowMapper(), lotId);
    }

    public Optional<Bid> findTopByLotId(Long lotId) {
        String sql = """
                SELECT b.*,
                       CONCAT(u.first_name, ' ', u.last_name) AS user_name
                FROM bids b
                JOIN users u ON b.user_id = u.id
                WHERE b.lot_id = ?
                ORDER BY b.amount DESC, b.created_at ASC
                LIMIT 1
                """;

        return jdbcTemplate.query(sql, bidRowMapper(), lotId)
                .stream()
                .findFirst();
    }

    private RowMapper<Bid> bidRowMapper() {
        return (rs, rowNum) -> {
            Bid bid = new Bid();
            bid.setId(rs.getLong("id"));
            bid.setLotId(rs.getLong("lot_id"));
            bid.setUserId(rs.getLong("user_id"));
            bid.setUserName(rs.getString("user_name"));
            bid.setAmount(rs.getBigDecimal("amount"));
            bid.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            return bid;
        };
    }
}