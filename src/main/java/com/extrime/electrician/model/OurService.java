package com.extrime.electrician.model;

import lombok.Getter;
import lombok.Setter;

// Наши услуги(service) на сайте
@Setter
@Getter
public class OurService {
    // Геттеры и сеттеры
    private Long id;
    private String title;
    private String description;
    private Double price;
    private String priceUnit;
    private Boolean isPopular;

    // Конструкторы
    public OurService() {}

    public OurService(Long id, String title, String description, Double price, String priceUnit, Boolean isPopular) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.price = price;
        this.priceUnit = priceUnit;
        this.isPopular = isPopular;
    }
}