package com.extrime.electrician.dao;

import com.extrime.electrician.config.Config;
import com.extrime.electrician.model.Review;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class ReviewDAO {
    @Autowired
    public Config config;

    private final JdbcTemplate jdbcTemplate;
    private String sql;
    private static final String FIND_LATEST_TIME_SQL = """
            SELECT MAX(created_at) as latest_time 
            FROM reviews 
            WHERE active = true
            """;

    private static final String FIND_LATEST_REVIEW_SQL = """
            SELECT id, user_id, username, rating, comment, 
                   admin_response, created_at, updated_at, active
            FROM reviews 
            WHERE active = true 
            ORDER BY created_at DESC 
            LIMIT 1
            """;

    @Autowired
    public ReviewDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void createTableIfNotExists() {
        if (config.isPostgres()) {
            sql = """
                    CREATE TABLE IF NOT EXISTS reviews (
                        id SERIAL PRIMARY KEY,
                        user_id INTEGER REFERENCES users(id) ON DELETE SET NULL,
                        username VARCHAR(100) NOT NULL,
                        rating INTEGER CHECK (rating >= 1 AND rating <= 5),
                        comment TEXT NOT NULL,
                        admin_response TEXT,
                        active BOOLEAN DEFAULT true,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                    )
                    """;
            jdbcTemplate.execute(sql);

            jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_reviews_user_id ON reviews(user_id)");
            jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_reviews_active ON reviews(active)");
        }
    }

    public Long save(Review review) {
        if (config.isPostgres()) sql = """
        INSERT INTO reviews (user_id, username, rating, comment, created_at, updated_at)
        VALUES (?, ?, ?, ?, ?, ?)
        """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});

            // Устанавливаем user_id как Integer или NULL
            if (review.getUserId() != null) {
                ps.setInt(1, review.getUserId().intValue());
            } else {
                ps.setNull(1, java.sql.Types.INTEGER);
            }

            ps.setString(2, review.getUsername());
            ps.setInt(3, review.getRating());
            ps.setString(4, review.getComment());
            ps.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            ps.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
            return ps;
        }, keyHolder);

        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    public List<Review> findAllActive() {
        if (config.isPostgres()) sql = """
            SELECT r.*, u.username as user_username 
            FROM reviews r 
            LEFT JOIN users u ON r.user_id = u.id 
            WHERE r.active = true 
            ORDER BY r.created_at DESC
            """;
        return jdbcTemplate.query(sql, new ReviewRowMapper());
    }

    public List<Review> findAll() {
        if (config.isPostgres()) sql = """
            SELECT r.*, u.username as user_username 
            FROM reviews r 
            LEFT JOIN users u ON r.user_id = u.id 
            ORDER BY r.created_at DESC
            """;
        return jdbcTemplate.query(sql, new ReviewRowMapper());
    }

    public Review findById(Long id) {
        if (config.isPostgres()) sql = """
            SELECT r.*, u.username as user_username 
            FROM reviews r 
            LEFT JOIN users u ON r.user_id = u.id 
            WHERE r.id = ?
            """;
        return jdbcTemplate.queryForObject(sql, new ReviewRowMapper(), id);
    }

    public boolean update(Review review) {
        if (config.isPostgres()) sql = """
            UPDATE reviews 
            SET comment = ?, rating = ?, admin_response = ?, updated_at = ? 
            WHERE id = ?
            """;

        int rowsAffected = jdbcTemplate.update(sql,
                review.getComment(),
                review.getRating(),
                review.getAdminResponse(),
                Timestamp.valueOf(LocalDateTime.now()),
                review.getId()
        );

        return rowsAffected > 0;
    }

    public boolean delete(Long id) {
        if (config.isPostgres()) sql = "UPDATE reviews SET active = false, updated_at = ? WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(sql,
                Timestamp.valueOf(LocalDateTime.now()),
                id
        );
        return rowsAffected > 0;
    }

    public boolean addAdminResponse(Long reviewId, String response) {
        if (config.isPostgres()) sql = "UPDATE reviews SET admin_response = ?, updated_at = ? WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(sql,
                response,
                Timestamp.valueOf(LocalDateTime.now()),
                reviewId
        );
        return rowsAffected > 0;
    }

    /**
     * Находит время последнего отзыва
     */
    public Optional<LocalDateTime> findLatestReviewTime() {
        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject(
                            FIND_LATEST_TIME_SQL,
                            LocalDateTime.class
                    )
            );
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    /**
     * Находит последний отзыв
     */
    public Optional<Review> findLatestReview() {
        try {
            Review review = jdbcTemplate.queryForObject(
                    FIND_LATEST_REVIEW_SQL,
                    new ReviewRowMapper()
            );
            return Optional.ofNullable(review);
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    private static class ReviewRowMapper implements RowMapper<Review> {
        @Override
        public Review mapRow(ResultSet rs, int rowNum) throws SQLException {
            Review review = new Review();
            review.setId(rs.getLong("id"));
            review.setUserId(rs.getLong("user_id"));
            review.setUsername(rs.getString("username"));
            review.setRating(rs.getInt("rating"));
            review.setComment(rs.getString("comment"));
            review.setAdminResponse(rs.getString("admin_response"));
            review.setActive(rs.getBoolean("active"));

            Timestamp createdAt = rs.getTimestamp("created_at");
            review.setCreatedAt(createdAt != null ? createdAt.toLocalDateTime() : null);

            Timestamp updatedAt = rs.getTimestamp("updated_at");
            review.setUpdatedAt(updatedAt != null ? updatedAt.toLocalDateTime() : null);

            return review;
        }
    }
}