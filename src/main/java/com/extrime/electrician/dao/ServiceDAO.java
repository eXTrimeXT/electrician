package com.extrime.electrician.dao;

import com.extrime.electrician.config.Config;
import com.extrime.electrician.model.OurService;
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
import java.util.Objects;

@Repository
public class ServiceDAO {
    @Autowired
    public Config config;

    private final JdbcTemplate jdbcTemplate;
    private String sql;

    @Autowired
    public ServiceDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void createTableIfNotExists() {
        if (config.isPostgres()) {
            sql = """
                    CREATE TABLE IF NOT EXISTS services (
                        id SERIAL PRIMARY KEY,
                        title VARCHAR(255) NOT NULL,
                        description TEXT,
                        price DECIMAL(10, 2) NOT NULL,
                        price_unit VARCHAR(50) DEFAULT 'за штуку',
                        is_popular BOOLEAN DEFAULT FALSE,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                    )
                    """;
        }
        jdbcTemplate.execute(sql);
    }

    // Получить все услуги
    public List<OurService> getAllServices() {
        if (config.isPostgres()) sql = "SELECT * FROM services ORDER BY title";
        return jdbcTemplate.query(sql, new ServiceRowMapper());
    }

    // Получить популярные услуги
    public List<OurService> getPopularServices() {
        if (config.isPostgres()) sql = "SELECT * FROM services WHERE is_popular = true ORDER BY title";
        return jdbcTemplate.query(sql, new ServiceRowMapper());
    }

    // Получить услугу по ID
    public OurService getServiceById(Long id) {
        if (config.isPostgres()) sql = "SELECT * FROM services WHERE id = ?";
        List<OurService> services = jdbcTemplate.query(sql, new ServiceRowMapper(), id);
        return services.isEmpty() ? null : services.get(0);
    }

    // Добавить новую услугу
    public Long addService(OurService service) {
        if (config.isPostgres()) sql = "INSERT INTO services (title, description, price, price_unit, is_popular) " + "VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, service.getTitle());
            ps.setString(2, service.getDescription());
            ps.setDouble(3, service.getPrice());
            ps.setString(4, service.getPriceUnit());
            ps.setBoolean(5, service.getIsPopular());
            return ps;
        }, keyHolder);
        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    // Обновить услугу
    public boolean updateService(OurService service) {
        if (config.isPostgres()) sql = "UPDATE services SET title = ?, description = ?, price = ?, " +
                "price_unit = ?, is_popular = ? WHERE id = ?";

        int rowsAffected = jdbcTemplate.update(sql,
                service.getTitle(),
                service.getDescription(),
                service.getPrice(),
                service.getPriceUnit(),
                service.getIsPopular(),
                service.getId()
        );

        return rowsAffected > 0;
    }

    // Удалить услугу
    public boolean deleteService(Long id) {
        if (config.isPostgres()) sql = "DELETE FROM services WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(sql, id);
        return rowsAffected > 0;
    }

    // Маппер для услуг
    private static class ServiceRowMapper implements RowMapper<OurService> {
        @Override
        public OurService mapRow(ResultSet rs, int rowNum) throws SQLException {
            OurService service = new OurService();
            service.setId(rs.getLong("id"));
            service.setTitle(rs.getString("title"));
            service.setDescription(rs.getString("description"));
            service.setPrice(rs.getDouble("price"));
            service.setPriceUnit(rs.getString("price_unit"));
            service.setIsPopular(rs.getBoolean("is_popular"));
            return service;
        }
    }
}