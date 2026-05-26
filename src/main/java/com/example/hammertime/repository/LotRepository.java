package com.example.hammertime.repository;

import com.example.hammertime.model.Lot;
import com.example.hammertime.model.LotStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class LotRepository {

    private final JdbcTemplate jdbcTemplate;

    public LotRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Long save(Lot lot) {
        String sql = """
                INSERT INTO lots
                (title, description, start_price, current_price, min_bid,
                 start_time, end_time, status, owner_id)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, lot.getTitle());
            ps.setString(2, lot.getDescription());
            ps.setBigDecimal(3, lot.getStartPrice());
            ps.setBigDecimal(4, lot.getCurrentPrice());
            ps.setBigDecimal(5, lot.getMinBid());
            ps.setTimestamp(6, Timestamp.valueOf(lot.getStartTime()));
            ps.setTimestamp(7, Timestamp.valueOf(lot.getEndTime()));
            ps.setString(8, lot.getStatus().name());
            ps.setLong(9, lot.getOwnerId());
            return ps;
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }

    public void saveImage(Long lotId, String fileName) {
        String sql = "INSERT INTO lot_images (lot_id, file_name) VALUES (?, ?)";
        jdbcTemplate.update(sql, lotId, fileName);
    }

    public List<Lot> findAll(String query, LocalDate date) {
        StringBuilder sql = new StringBuilder("""
                SELECT l.*,
                       CONCAT(u.first_name, ' ', u.last_name) AS owner_name
                FROM lots l
                JOIN users u ON l.owner_id = u.id
                WHERE 1 = 1
                """);

        List<Object> params = new ArrayList<>();

        if (query != null && !query.isBlank()) {
            sql.append("""
                    AND (
                        LOWER(l.title) LIKE ?
                        OR LOWER(l.description) LIKE ?
                    )
                    """);

            String search = "%" + query.toLowerCase() + "%";
            params.add(search);
            params.add(search);
        }

        if (date != null) {
            sql.append(" AND DATE(l.start_time) = ? ");
            params.add(Date.valueOf(date));
        }

        sql.append(" ORDER BY l.created_at DESC ");

        List<Lot> lots = jdbcTemplate.query(sql.toString(), lotRowMapper(), params.toArray());

        for (Lot lot : lots) {
            lot.setImages(findImagesByLotId(lot.getId()));
        }

        return lots;
    }

    public Optional<Lot> findById(Long id) {
        String sql = """
                SELECT l.*,
                       CONCAT(u.first_name, ' ', u.last_name) AS owner_name
                FROM lots l
                JOIN users u ON l.owner_id = u.id
                WHERE l.id = ?
                """;

        return jdbcTemplate.query(sql, lotRowMapper(), id)
                .stream()
                .findFirst()
                .map(lot -> {
                    lot.setImages(findImagesByLotId(lot.getId()));
                    return lot;
                });
    }

    public List<String> findImagesByLotId(Long lotId) {
        String sql = "SELECT file_name FROM lot_images WHERE lot_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getString("file_name"), lotId);
    }

    private RowMapper<Lot> lotRowMapper() {
        return (rs, rowNum) -> {
            Lot lot = new Lot();

            lot.setId(rs.getLong("id"));
            lot.setTitle(rs.getString("title"));
            lot.setDescription(rs.getString("description"));
            lot.setStartPrice(rs.getBigDecimal("start_price"));
            lot.setCurrentPrice(rs.getBigDecimal("current_price"));
            lot.setMinBid(rs.getBigDecimal("min_bid"));
            lot.setStartTime(rs.getTimestamp("start_time").toLocalDateTime());
            lot.setEndTime(rs.getTimestamp("end_time").toLocalDateTime());
            lot.setStatus(LotStatus.valueOf(rs.getString("status")));
            lot.setOwnerId(rs.getLong("owner_id"));
            lot.setOwnerName(rs.getString("owner_name"));
            lot.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());

            return lot;
        };
    }
}