package com.extrime.electrician.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class FileUploadConfig implements WebMvcConfigurer {
    @Autowired
    private Config config;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path uploadPath = Paths.get(config.getUploadDir()).toAbsolutePath().normalize();
        String uploadLocation = uploadPath.toUri().toString();

        // Для доступа к загруженным файлам через URL /uploads/**
        registry.addResourceHandler("/static/uploads/**")
                .addResourceLocations(uploadLocation)
                .setCachePeriod(3600)
                .resourceChain(true);

        // Также для статических ресурсов
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/")
                .setCachePeriod(3600);
    }
}