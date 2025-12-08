package com.extrime.electrician.dao;

import com.extrime.electrician.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class UserDAO {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Создать таблицу пользователей (если не существует)
    public void createTableIfNotExists() {
        String sql = """
            CREATE TABLE IF NOT EXISTS users (
                id SERIAL PRIMARY KEY,
                username VARCHAR(50) UNIQUE NOT NULL,
                password VARCHAR(255) NOT NULL,
                email VARCHAR(100) UNIQUE,
                role VARCHAR(20) DEFAULT 'USER',
                active BOOLEAN DEFAULT true,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
            """;
        jdbcTemplate.execute(sql);

        // Создаем индекс для быстрого поиска по username
        jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_users_username ON users(username)");
    }

    // Получить пользователя по username
    public Optional<User> findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ? AND active = true";
        try {
            User user = jdbcTemplate.queryForObject(sql, new UserRowMapper(), username);
            return Optional.ofNullable(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    // Получить пользователя по email
    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ? AND active = true";
        try {
            User user = jdbcTemplate.queryForObject(sql, new UserRowMapper(), email);
            return Optional.ofNullable(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    // Получить пользователя по ID
    public Optional<User> findById(Long id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try {
            User user = jdbcTemplate.queryForObject(sql, new UserRowMapper(), id);
            return Optional.ofNullable(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    // Проверить существует ли пользователь с таким username
    public boolean existsByUsername(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, username);
        return count != null && count > 0;
    }

    // Проверить существует ли пользователь с таким email
    public boolean existsByEmail(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email);
        return count != null && count > 0;
    }

    // Создать нового пользователя
    public Long createUser(User user) {
        String sql = """
            INSERT INTO users (username, password, email, role, active, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
//            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword()); // Пароль должен быть уже хеширован
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getRole());
            ps.setBoolean(5, user.isActive());
            ps.setTimestamp(6, java.sql.Timestamp.valueOf(LocalDateTime.now()));
            ps.setTimestamp(7, java.sql.Timestamp.valueOf(LocalDateTime.now()));
            return ps;
        }, keyHolder);

//        return keyHolder.getKey().longValue();
        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    // Обновить пользователя
    public boolean updateUser(User user) {
        String sql = """
            UPDATE users 
            SET username = ?, password = ?, email = ?, role = ?, 
                active = ?, updated_at = ? 
            WHERE id = ?
            """;

        int rowsAffected = jdbcTemplate.update(sql,
                user.getUsername(),
                user.getPassword(),
                user.getEmail(),
                user.getRole(),
                user.isActive(),
                java.sql.Timestamp.valueOf(LocalDateTime.now()),
                user.getId()
        );

        return rowsAffected > 0;
    }

    /**
     * Обновление пароля пользователя
     */
    public void update(User user) {
        String sql = """
        UPDATE users 
        SET username = ?, email = ?, password = ?, 
            role = ?, active = ?, created_at = ?, 
            email_verified = ?
        WHERE id = ?
        """;

        jdbcTemplate.update(sql,
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                user.getRole(),
                user.isActive(),
                user.getCreatedAt(),
                user.isEmailVerified(),
                user.getId());
    }

    // Получить всех пользователей
    public List<User> findAll() {
        String sql = "SELECT * FROM users ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, new UserRowMapper());
    }

    // Удалить пользователя (мягкое удаление)
    public boolean deleteUser(Long id) {
        String sql = "UPDATE users SET active = false, updated_at = ? WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(sql,
                java.sql.Timestamp.valueOf(LocalDateTime.now()), id);
        return rowsAffected > 0;
    }

    /**
     * Активация пользователя (установка флага active = true)
     */
    public boolean activateUser(Long userId) {
        try {
            String sql = "UPDATE users SET active = true WHERE id = ?";
            int rowsAffected = jdbcTemplate.update(sql, userId);
            return rowsAffected > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Проверка, активен ли пользователь
     */
    public boolean isUserActive(Long userId) {
        try {
            String sql = "SELECT active FROM users WHERE id = ?";
            Boolean active = jdbcTemplate.queryForObject(sql, Boolean.class, userId);
            return active != null && active;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // RowMapper для пользователей
    private static class UserRowMapper implements RowMapper<User> {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setId(rs.getLong("id"));
            user.setUsername(rs.getString("username"));
            user.setPassword(rs.getString("password"));
            user.setEmail(rs.getString("email"));
            user.setRole(rs.getString("role"));
            user.setActive(rs.getBoolean("active"));
            user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            user.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
            return user;
        }
    }
}
