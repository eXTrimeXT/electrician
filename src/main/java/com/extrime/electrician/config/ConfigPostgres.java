package com.extrime.electrician.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import javax.sql.DataSource;

@Configuration
public class ConfigPostgres {
    @Autowired
    private Config config;

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(config.getPostgresDriverClassName());
        dataSource.setUrl(config.getPostgresUrl());
        dataSource.setUsername(config.getPostgresUsername());
        dataSource.setPassword(config.getPostgresPassword());
        return dataSource;
    }
}