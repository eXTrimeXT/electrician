package com.extrime.electrician.dao;

import com.extrime.electrician.config.Config;
import com.extrime.electrician.model.Work;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Repository
public class WorkDAO {
    @Autowired
    public Config config;

    private final JdbcTemplate jdbcTemplate;
    private String sql;

    @Autowired
    public WorkDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Получить все работы
    public List<Work> getAllWorks() {
        if (config.isPostgres()) sql = "SELECT * FROM works ORDER BY work_date DESC";
        return jdbcTemplate.query(sql, new WorkRowMapper());
    }

    // Получить работу по ID
    public Work getWorkById(Long id) {
        if (config.isPostgres()) sql = "SELECT * FROM works WHERE id = ?";
        List<Work> works = jdbcTemplate.query(sql, new WorkRowMapper(), id);
        return works.isEmpty() ? null : works.get(0);
    }

    // Добавить новую работу
    public Long addWork(Work work) {
        if (config.isPostgres()) sql = "INSERT INTO works (title, description, work_date, price, image_url) " + "VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, work.getTitle());
            ps.setString(2, work.getDescription());
            ps.setDate(3, java.sql.Date.valueOf(work.getWorkDate()));
            ps.setDouble(4, work.getPrice());
            ps.setString(5, work.getImageUrl());
            return ps;
        }, keyHolder);
        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    // Обновить работу
    public boolean updateWork(Work work) {
        if (config.isPostgres()) sql = "UPDATE works SET title = ?, description = ?, work_date = ?, " +
                "price = ?, image_url = ? WHERE id = ?";

        int rowsAffected = jdbcTemplate.update(sql,
                work.getTitle(),
                work.getDescription(),
                java.sql.Date.valueOf(work.getWorkDate()),
                work.getPrice(),
                work.getImageUrl(),
                work.getId()
        );

        return rowsAffected > 0;
    }

    // Удалить работу
    public boolean deleteWork(Long id) {
        if (config.isPostgres()) sql = "DELETE FROM works WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(sql, id);
        return rowsAffected > 0;
    }

    // Маппер для работ
    private static class WorkRowMapper implements RowMapper<Work> {
        @Override
        public Work mapRow(ResultSet rs, int rowNum) throws SQLException {
            Work work = new Work();
            work.setId(rs.getLong("id"));
            work.setTitle(rs.getString("title"));
            work.setDescription(rs.getString("description"));
            work.setWorkDate(rs.getDate("work_date").toLocalDate());
            work.setPrice(rs.getDouble("price"));
            work.setImageUrl(rs.getString("image_url"));
            return work;
        }
    }
}