package com.extrime.electrician.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import javax.sql.DataSource;

@Configuration
public class ConfigDatabase {
    @Autowired
    private Config config;

    @Bean
    public DataSource dataSource() {
        System.out.println("### NAME_TYPE_DATABASE = " + config.getNAME_TYPE_DATABASE() + " ###");
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(config.getDatabaseDriverClassName());
        dataSource.setUrl(config.getDatabaseUrl());
        dataSource.setUsername(config.getDatabaseUsername());
        dataSource.setPassword(config.getDatabasePassword());
        return dataSource;
    }
}