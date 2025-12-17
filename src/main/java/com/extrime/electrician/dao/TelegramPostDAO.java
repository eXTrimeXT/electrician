package com.extrime.electrician.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import com.extrime.electrician.model.telegram.TelegramPost;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

@Repository
public class TelegramPostDAO {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public TelegramPostDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void createTableIfNotExists() {
        String sql = """
                    CREATE TABLE IF NOT EXISTS telegram_posts (
                        id SERIAL PRIMARY KEY,
                        post_id BIGINT NOT NULL UNIQUE,
                        post_text TEXT,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        is_edited BOOLEAN DEFAULT FALSE
                    )
                    """;
        jdbcTemplate.execute(sql);
    }

    // Добавить новую запись
    public Long addPost(TelegramPost post) {
        String sql = """
                    INSERT INTO telegram_posts (post_id, post_text, created_at, is_edited) 
                    VALUES (?, ?, ?, ?)
                    """;
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setLong(1, post.getPostId());
            ps.setString(2, post.getPostText());
            ps.setTimestamp(3, post.getCreatedAt() != null ? Timestamp.valueOf(post.getCreatedAt()) : null);
            ps.setBoolean(4, post.getIsEdited());
            return ps;
        }, keyHolder);
        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    // Обновить запись
    public boolean updatePost(TelegramPost post) {
        String sql = """
                    UPDATE telegram_posts 
                    SET post_id = ?, post_text = ?, 
                        created_at = ?, is_edited = true 
                    WHERE id = ?
                    """;

        int rowsAffected = jdbcTemplate.update(sql,
                post.getPostId(),
                post.getPostText(),
                post.getCreatedAt() != null ? Timestamp.valueOf(post.getCreatedAt()) : null,
                post.getId()
        );

        return rowsAffected > 0;
    }

    // Обновить только текст и дату редактирования
    public boolean updatePostText(Long postId, String postText) {
        String sql = """
                    UPDATE telegram_posts 
                    SET post_text = ?, created_at = CURRENT_TIMESTAMP, is_edited = true 
                    WHERE post_id = ?
                    """;

        int rowsAffected = jdbcTemplate.update(sql, postText, postId);
        return rowsAffected > 0;
    }

    // Получить все записи
    public List<TelegramPost> getAllPosts() {
        String sql = "SELECT * FROM telegram_posts ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, new TelegramPostRowMapper());
    }

    // Получить запись по ID
    public TelegramPost getPostById(Long id) {
        String sql = "SELECT * FROM telegram_posts WHERE id = ?";
        List<TelegramPost> posts = jdbcTemplate.query(sql, new TelegramPostRowMapper(), id);
        return posts.isEmpty() ? null : posts.get(0);
    }

    // Получить самую последнюю запись по дате создания
    public TelegramPost getLatestPost() {
        String sql = "SELECT * FROM telegram_posts ORDER BY created_at DESC LIMIT 1";
        List<TelegramPost> posts = jdbcTemplate.query(sql, new TelegramPostRowMapper());
        return posts.isEmpty() ? null : posts.getFirst();
    }

    // Получить самую последнюю запись по дате
    public TelegramPost getLatestPostByEditDate() {
        String sql = """
                SELECT * FROM telegram_posts 
                WHERE created_at IS NOT NULL 
                ORDER BY created_at DESC 
                LIMIT 1
                """;
        List<TelegramPost> posts = jdbcTemplate.query(sql, new TelegramPostRowMapper());
        return posts.isEmpty() ? null : posts.getFirst();
    }

    // Получить запись по postId
    public TelegramPost getPostByPostId(Long postId) {
        String sql = "SELECT * FROM telegram_posts WHERE post_id = ?";
        List<TelegramPost> posts = jdbcTemplate.query(sql, new TelegramPostRowMapper(), postId);
        return posts.isEmpty() ? null : posts.get(0);
    }

    // Получить количество записей
    public int getCount() {
        String sql = "SELECT COUNT(*) FROM telegram_posts";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
        return count != null ? count : 0;
    }

    // Удалить запись
    public boolean deletePost(Long id) {
        String sql = "DELETE FROM telegram_posts WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(sql, id);
        return rowsAffected > 0;
    }

    // Удалить запись по postId
    public boolean deletePostByPostId(Long postId) {
        String sql = "DELETE FROM telegram_posts WHERE post_id = ?";
        int rowsAffected = jdbcTemplate.update(sql, postId);
        return rowsAffected > 0;
    }

    // Получить записи с пагинацией
    public List<TelegramPost> getPostsWithPagination(int offset, int limit) {
        String sql = "SELECT * FROM telegram_posts ORDER BY created_at DESC LIMIT ? OFFSET ?";
        return jdbcTemplate.query(sql, new TelegramPostRowMapper(), limit, offset);
    }

    // Маппер для записей Telegram
    private static class TelegramPostRowMapper implements RowMapper<TelegramPost> {
        @Override
        public TelegramPost mapRow(ResultSet rs, int rowNum) throws SQLException {
            TelegramPost post = new TelegramPost();
            post.setId(rs.getLong("id"));
            post.setPostId(rs.getLong("post_id"));
            post.setPostText(rs.getString("post_text"));

            Timestamp createdAt = rs.getTimestamp("created_at");
            post.setCreatedAt(createdAt != null ? createdAt.toLocalDateTime() : null);

            post.setIsEdited(rs.getBoolean("is_edited"));

            return post;
        }
    }
}
