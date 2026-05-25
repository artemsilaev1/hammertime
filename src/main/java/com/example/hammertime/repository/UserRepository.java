package com.example.hammertime.repository;

import com.example.hammertime.model.Role;
import com.example.hammertime.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(User user) {
        String sql = """
                INSERT INTO users
                (first_name, last_name, email, password, role, enabled, balance, activation_code, reset_password_code)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        jdbcTemplate.update(
                sql,
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPassword(),
                user.getRole().name(),
                user.isEnabled(),
                user.getBalance(),
                user.getActivationCode(),
                user.getResetPasswordCode()
        );
    }

    public void update(User user) {
        String sql = """
                UPDATE users
                SET first_name = ?,
                    last_name = ?,
                    email = ?,
                    password = ?,
                    role = ?,
                    enabled = ?,
                    balance = ?,
                    activation_code = ?,
                    reset_password_code = ?
                WHERE id = ?
                """;

        jdbcTemplate.update(
                sql,
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPassword(),
                user.getRole().name(),
                user.isEnabled(),
                user.getBalance(),
                user.getActivationCode(),
                user.getResetPasswordCode(),
                user.getId()
        );
    }

    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";

        return jdbcTemplate.query(sql, userRowMapper(), email)
                .stream()
                .findFirst();
    }

    public Optional<User> findByActivationCode(String code) {
        String sql = "SELECT * FROM users WHERE activation_code = ?";

        return jdbcTemplate.query(sql, userRowMapper(), code)
                .stream()
                .findFirst();
    }

    public boolean existsByEmail(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email);
        return count != null && count > 0;
    }

    private RowMapper<User> userRowMapper() {
        return (rs, rowNum) -> {
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
        };
    }
}