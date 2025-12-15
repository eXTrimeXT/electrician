package com.extrime.electrician.dao;

import com.extrime.electrician.config.Config;
import com.extrime.electrician.model.EmailVerification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Objects;

@Repository
public class EmailVerificationDAO {
    @Autowired
    public Config config;

    private final JdbcTemplate jdbcTemplate;
    private String sql;

    @Autowired
    public EmailVerificationDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void createTableIfNotExists() {
        if (config.isPostgres()) {
            sql = """
                    CREATE TABLE IF NOT EXISTS email_verifications (
                        id SERIAL PRIMARY KEY,
                        email VARCHAR(100) NOT NULL,
                        verification_code VARCHAR(10) NOT NULL,
                        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        expires_at TIMESTAMP NOT NULL,
                        used BOOLEAN DEFAULT FALSE,
                        user_id BIGINT NOT NULL,
                        CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
                    )
                    """;
            jdbcTemplate.execute(sql);

            // Создание индексов для таблицы email_verifications
            jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_email_verifications_email ON email_verifications(email)");
            jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_email_verifications_email_code ON email_verifications(email, verification_code)");
            jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_email_verifications_expires ON email_verifications(expires_at)");
            jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_email_verifications_used ON email_verifications(used) WHERE used = false");
            jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_email_verifications_user_id ON email_verifications(user_id)");
        }
    }

    public Long save(EmailVerification verification) {
        if (config.isPostgres()) sql = """
            INSERT INTO email_verifications 
            (email, verification_code, expires_at, user_id, used) 
            VALUES (?, ?, ?, ?, ?)
            """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, verification.getEmail());
            ps.setString(2, verification.getVerificationCode());
            ps.setTimestamp(3, Timestamp.valueOf(verification.getExpiresAt()));
            ps.setLong(4, verification.getUserId());
            ps.setBoolean(5, verification.isUsed());
            return ps;
        }, keyHolder);
         return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    public EmailVerification findByEmailAndCode(String email, String code) {
        if (config.isPostgres()) sql = """
            SELECT * FROM email_verifications 
            WHERE email = ? AND verification_code = ? 
            ORDER BY created_at DESC LIMIT 1
            """;

        try {
            return jdbcTemplate.queryForObject(sql,
                    new BeanPropertyRowMapper<>(EmailVerification.class),
                    email, code);
        } catch (Exception e) {
            return null;
        }
    }

    public EmailVerification findActiveByUserId(Long userId) {
        if (config.isPostgres()) sql = """
            SELECT * FROM email_verifications 
            WHERE user_id = ? AND used = false 
            AND expires_at > ? 
            ORDER BY created_at DESC LIMIT 1
            """;

        try {
            return jdbcTemplate.queryForObject(sql,
                    new BeanPropertyRowMapper<>(EmailVerification.class),
                    userId, Timestamp.valueOf(LocalDateTime.now()));
        } catch (Exception e) {
            return null;
        }
    }

    public void update(EmailVerification verification) {
        if (config.isPostgres()) sql = """
            UPDATE email_verifications 
            SET used = ? 
            WHERE id = ?
            """;

        jdbcTemplate.update(sql,
                verification.isUsed(),
                verification.getId());
    }

    /**
     * Удаление старых верификаций
     */
    public void cleanupOldVerifications() {
        if (config.isPostgres()) sql = """
            DELETE FROM email_verifications 
            WHERE expires_at < ? OR used = true
            """;

        jdbcTemplate.update(sql,
                Timestamp.valueOf(LocalDateTime.now().minusDays(1)));
    }
}
